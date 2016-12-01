package com.zhuoli.onecard.imageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by CLD on 2016/7/17 0017.
 */
public interface ImageLoader {

    void loadImage(ImageView target, String url, @Nullable ImageParams params);

    void loadImage(ImageView target, int resId, @Nullable ImageParams params);

    void loadImage(ImageView target, File file, @Nullable ImageParams params);

    void loadImage(String url, @NonNull ImageParams params);

    void preLoad(String url);

    void pause();

    void resume();

    class ImageParams {
        private Drawable placeholderImage;
        private Drawable failureImage;
        private Drawable retryImage;
        private Drawable progressBarImage;
        private boolean isCircle;
        private float cornersRadius;
        private LoaderListener listener;
        private DownloadListener downloadListener;

        private ImageParams() {
            isCircle = false;
        }

        public static ImageParamsBuilder Builder(Context context) {
            return new ImageParamsBuilder(context);
        }


        public interface DownloadListener {
            void onLoadData(@NonNull Bitmap bitmap);

            void onLoadFail();
        }

        public interface LoaderListener {
            void onImageSet();

            void onFailure();
        }

        public DownloadListener getDownloadListener() {
            return downloadListener;
        }

        public void setDownloadListener(DownloadListener downloadListener) {
            this.downloadListener = downloadListener;
        }

        public Drawable getPlaceholderImage() {
            return placeholderImage;
        }

        public Drawable getFailureImage() {
            return failureImage;
        }

        public Drawable getRetryImage() {
            return retryImage;
        }

        public Drawable getProgressBarImage() {
            return progressBarImage;
        }

        public boolean isRoundAsCircle(){return isCircle;}

        public float getCornersRadius() {
            return cornersRadius;
        }

        public LoaderListener getListener() {
            return listener;
        }

        public static class ImageParamsBuilder {
            private final Context mContext;
            private final ImageParams mImageParams;

            public ImageParamsBuilder(Context context) {
                mContext = context;
                mImageParams = new ImageParams();
            }

            public ImageParamsBuilder placeholderImage(int resId) {
                mImageParams.placeholderImage = mContext.getResources().getDrawable(resId);
                return this;
            }

            public ImageParamsBuilder placeholderImage(Drawable drawable) {
                mImageParams.placeholderImage = drawable;
                return this;
            }

            public ImageParamsBuilder failureImage(int resId) {
                mImageParams.failureImage = mContext.getResources().getDrawable(resId);
                return this;
            }

            public ImageParamsBuilder failureImage(Drawable drawable) {
                mImageParams.failureImage = drawable;
                return this;
            }

            public ImageParamsBuilder retryImage(int resId) {
                mImageParams.retryImage = mContext.getResources().getDrawable(resId);
                return this;
            }

            public ImageParamsBuilder retryImage(Drawable drawable) {
                mImageParams.retryImage = drawable;
                return this;
            }

            public ImageParamsBuilder progressBarImage(int resId) {
                mImageParams.progressBarImage = mContext.getResources().getDrawable(resId);
                return this;
            }

            public ImageParamsBuilder progressBarImage(Drawable drawable) {
                mImageParams.progressBarImage = drawable;
                return this;
            }

            public ImageParamsBuilder cornersRadius(float radius) {
                mImageParams.cornersRadius = radius;
                return this;
            }

            public ImageParamsBuilder roundAsCircle(boolean isCircle){
                mImageParams.isCircle = isCircle;
                return this;
            }

            public ImageParamsBuilder loaderListener(LoaderListener listener) {
                mImageParams.listener = listener;
                return this;
            }

            public ImageParamsBuilder downloadListener(DownloadListener listener) {
                mImageParams.downloadListener = listener;
                return this;
            }

            public ImageParams build() {
                return mImageParams;
            }
        }
    }
}
