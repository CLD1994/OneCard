package com.zhuoli.onecard.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateFragment;
import com.zhuoli.onecard.RxBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import codetail.graphics.drawables.DrawableHotspotTouch;
import codetail.graphics.drawables.LollipopDrawable;
import codetail.graphics.drawables.LollipopDrawablesCompat;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseFragment<V extends MvpView, P extends MvpPresenter<V>> extends MvpViewStateFragment<V,P> {

    private BaseActivity mActivity;
    private Unbinder unbinder;
    private CompositeSubscription mAllSubscription;

    protected abstract Integer getLayoutId();

    protected abstract void initView(View root, Bundle savedInstanceState);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            mActivity = (BaseActivity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAllSubscription = new CompositeSubscription();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        initView(view, savedInstanceState);
    }

    public <T> void busSubscribe(Class<T> eventType, Action1<T> onNext){
        mAllSubscription.add(RxBus.getDefault().subscribe(eventType,onNext));
    }

    public void setRippleEffect(View view,@DrawableRes int resId){
        view.setBackground(LollipopDrawablesCompat.getDrawable(getResources(), resId, mActivity.getTheme()));
        view.setOnTouchListener(new DrawableHotspotTouch((LollipopDrawable) view.getBackground()));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //解除视图绑定
        unbinder.unbind();
        //取消所有动作订阅
        mAllSubscription.unsubscribe();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //解绑宿主
        mActivity = null;
    }

    public BaseActivity getHostActivity() {
        return mActivity;
    }
}
