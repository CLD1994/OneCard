package com.zhuoli.onecard.imageLoader.impl;

import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.GenericDraweeView;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.orhanobut.logger.Logger;
import com.zhuoli.onecard.imageLoader.ImageLoader;

import java.io.File;

/**
 * Created by CLD on 2016/7/17 0017.
 */
public class FrescoLoader implements ImageLoader {

	@Override
	public void loadImage(final ImageView target, final String url, @Nullable final ImageParams params) {
		if (target instanceof GenericDraweeView) {
			Uri uri;
			if (TextUtils.isEmpty(url)){
				uri = Uri.EMPTY;
			}else {
				uri = Uri.parse(url);
			}
			GenericDraweeView draweeView = (GenericDraweeView) target;
			ImageParams.LoaderListener loaderListener = null;
			if (params != null) {
				setParams(draweeView, params);
				loaderListener = params.getListener();
			}
			setUri(draweeView, uri, loaderListener);
		}
	}

	@Override
	public void loadImage(ImageView target, @IdRes int resId, @Nullable ImageParams params) {
		if (target instanceof DraweeView) {
			Uri uri = Uri.parse("res://com.vvelink.yiqilai/" + resId);
			GenericDraweeView draweeView = (GenericDraweeView) target;
			ImageParams.LoaderListener loaderListener = null;
			if (params != null) {
				setParams(draweeView, params);
				loaderListener = params.getListener();
			}
			setUri(draweeView, uri, loaderListener);
		}
	}


	@Override
	public void loadImage(ImageView target, File file, @Nullable ImageParams params) {
		if (target instanceof DraweeView) {
			Uri uri = Uri.parse("file://com.vvelink.yiqilai/" + file.getAbsolutePath());
			GenericDraweeView draweeView = (GenericDraweeView) target;
			ImageParams.LoaderListener loaderListener = null;
			if (params != null) {
				setParams(draweeView, params);
				loaderListener = params.getListener();
			}
			setUri(draweeView, uri, loaderListener);
		}
	}

	@Override
	public void loadImage(String url, @NonNull final ImageParams params) {
		ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).setProgressiveRenderingEnabled(true).build();
		DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline().fetchDecodedImage(ImageRequest.fromUri(url), this);
		dataSource.subscribe(new BaseBitmapDataSubscriber() {
			@Override
			protected void onNewResultImpl(Bitmap bitmap) {
				params.getDownloadListener().onLoadData(bitmap);
			}

			@Override
			protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
				params.getDownloadListener().onLoadFail();
			}
		}, CallerThreadExecutor.getInstance());
	}

	@Override
	public void preLoad(String url) {
		Fresco.getImagePipeline().prefetchToBitmapCache(ImageRequest.fromUri(url), null);
		Fresco.getImagePipeline().prefetchToDiskCache(ImageRequest.fromUri(url), null);
	}

	@Override
	public void pause() {
		Fresco.getImagePipeline().pause();
	}

	@Override
	public void resume() {
		Fresco.getImagePipeline().resume();
	}

	private void setParams(GenericDraweeView draweeView, ImageParams params) {

		GenericDraweeHierarchy hierarchy = draweeView.getHierarchy();

		if (params.getPlaceholderImage() != null) {
			hierarchy.setPlaceholderImage(params.getPlaceholderImage());
		}

		if (params.getFailureImage() != null) {
			hierarchy.setFailureImage(params.getPlaceholderImage());
		}

		if (params.getRetryImage() != null) {
			hierarchy.setRetryImage(params.getPlaceholderImage());
		}

		if (params.getProgressBarImage() != null) {
			hierarchy.setProgressBarImage(params.getProgressBarImage());
		}

		if (params.getCornersRadius() > 0) {
			hierarchy.setRoundingParams(RoundingParams.fromCornersRadius(params.getCornersRadius()));
		}

		if (params.isRoundAsCircle()){
			hierarchy.setRoundingParams(RoundingParams.asCircle());
		}
	}

	private void setUri(GenericDraweeView draweeView, Uri uri, @Nullable final ImageParams.LoaderListener listener) {
		ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
			@Override
			public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
				if (listener != null) {
					listener.onImageSet();
				}
			}

			@Override
			public void onFailure(String id, Throwable throwable) {
				Logger.e(throwable, "Error loading %s", id);
				if (listener != null) {
					listener.onFailure();
				}
			}
		};

		DraweeController controller = Fresco.newDraweeControllerBuilder()
				.setUri(uri)
				.setControllerListener(controllerListener)
				.setOldController(draweeView.getController())
				.build();
		draweeView.setController(controller);
	}
}
