package com.zhuoli.onecard;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.zhuoli.onecard.utils.NetworkUtil;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public final class AppModule {

	private static final String baseUrl = "https://m.api.gululu-a.com:9443/api/v1/m/";
	private static final String authenticationUrl = "";

	private final Context mContext;

	public AppModule(Context context) {
		mContext = context;
	}

	@Provides
	Context provideAppContext() {
		return mContext;
	}

	@Provides
	@Singleton
	SharedPreferences providePreferences() {
		return PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	@Provides
	@Singleton
	RealmMigration provideRealmMigration() {
		return new RealmMigration() {
			@Override
			public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
			}
		};
	}

	@Provides
	@Singleton
	RealmConfiguration provideRealmConfiguration() {
		return new RealmConfiguration.Builder()
				.name("oneCard.realm")
				.schemaVersion(0)
				.deleteRealmIfMigrationNeeded()
				.build();
	}

	@Provides
	@Singleton
	Realm provideRealm(RealmConfiguration configuration) {
		return Realm.getInstance(configuration);
	}


	/**
	 * 提供一般拦截器,为请求添加请求头信息
	 */
	@Provides
	@Singleton
	Interceptor provideInterceptor() {
		return new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				Request request = chain.request()
						.newBuilder()
						.addHeader("Content-type", "application/json")
						.build();
				if (!NetworkUtil.isConnected(mContext)) {
					request = request
							.newBuilder()
							.cacheControl(CacheControl.FORCE_CACHE)//无网络时只从缓存中读取
							.build();
				}
				Response response = chain.proceed(request);
				if (NetworkUtil.isConnected(mContext)) {
					int maxAge = 60 * 2; // 有网络时 设置缓存超时时间2分钟
					response.newBuilder()
							.removeHeader("Pragma")
							//清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
							.header("Cache-Control", "public, max-age=" + maxAge)//设置缓存超时时间
							.build();
				} else {
					int maxStale = 60 * 60; // 无网络时，设置超时为1小时
					response.newBuilder()
							.removeHeader("Pragma")
							.header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
							//设置缓存策略，及超时策略
							.build();
				}
				return response;
			}
		};
	}

	/**
	 * 提供认证器,为OkHttp提供认证功能
	 */
	@Provides
	@Singleton
	Authenticator provideAuthenticator() {
		return new Authenticator() {
			@Override
			public Request authenticate(Route route, Response response) throws IOException {
				return null;
			}
		};
	}

	/**
	 * 提供OkHttp客户端,为Retrofit提供支持
	 */
	@Provides
	@Singleton
	OkHttpClient provideOkHttpClient(Interceptor interceptor, Authenticator authenticator) {

		File httpCacheDirectory = new File(mContext.getExternalCacheDir(), "responses");

		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.connectTimeout(5, TimeUnit.SECONDS)
				.readTimeout(5,TimeUnit.SECONDS)
				.retryOnConnectionFailure(true)
				.addInterceptor(interceptor)
				.authenticator(authenticator)
				.cache(new Cache(httpCacheDirectory, 10 * 1024 * 1024))
				.build();

		X509TrustManager xtm = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		};

		HostnameVerifier verifier = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession sslSession) {
				return true;
			}
		};

		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[]{xtm}, null);
			okHttpClient.newBuilder()
					.sslSocketFactory(sslContext.getSocketFactory(),xtm)
					.hostnameVerifier(verifier)
					.build();

		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
		}

		return okHttpClient;
	}

	/**
	 *提供Retrofit框架实例
	 */
	@Provides
	@Singleton
	Retrofit provideRetrofit(OkHttpClient okHttpClient) {
		return new Retrofit.Builder()
				.baseUrl(baseUrl)
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create())
				.client(okHttpClient)
				.build();
	}
}
