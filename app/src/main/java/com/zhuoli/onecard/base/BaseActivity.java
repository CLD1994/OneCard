package com.zhuoli.onecard.base;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.zhuoli.onecard.APP;
import com.zhuoli.onecard.R;
import com.zhuoli.onecard.RxBus;
import com.zhuoli.onecard.utils.Preconditions;

import java.util.ArrayList;
import java.util.Stack;

import butterknife.ButterKnife;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseActivity<C> extends AppCompatActivity{

    private C mComponent;
    protected APP mApp;
    private Stack<String> previousFragments;
    protected Fragment currentFragment;
    private CompositeSubscription mAllSubscription;
    private TextView toolbarTitle;

    protected abstract Integer getLayoutId();

    protected abstract Integer getToolbarId();

    protected abstract Integer getFragmentContainerId();

    protected abstract C injectDependencies();

    protected abstract void initView(Bundle savedInstanceState);


    static {AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initToolbar();
        mApp = (APP) getApplication();
        mAllSubscription = new CompositeSubscription();
        previousFragments = new Stack<>();
        mComponent = injectDependencies();
        initView(savedInstanceState);
    }

    private void initToolbar() {
        if (getToolbarId() != null) {
            Toolbar toolbar = (Toolbar) findViewById(getToolbarId());
            if (toolbar != null) {
                toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
                setSupportActionBar(toolbar);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setDisplayShowTitleEnabled(false);
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    final Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_ab_back_material,null);
                    actionBar.setHomeAsUpIndicator(drawable);
                }
            }
        }
    }

    public <T> void busSubscribe(Class<T> eventType, Action1<T> onNext){
        mAllSubscription.add(RxBus.getDefault().subscribe(eventType,onNext));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消所有动作订阅
        mAllSubscription.unsubscribe();
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void addFragment(MvpView view, String tag, Boolean needAddToBackStack, Pair<View, String>... pairs){
        addFragment((BaseFragment)view,tag,needAddToBackStack,pairs);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void addFragment(BaseFragment fragment, String tag, Boolean needAddToBackStack, Pair<View, String>... pairs) {

        Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(Preconditions.checkNotEmpty(tag));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        initSharedElement(transaction, pairs);
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        if (needAddToBackStack) {
            transaction.addToBackStack(tag);
            previousFragments.push(currentFragment.getTag());
        }

        if (fragmentByTag == null) {
            transaction.add(getFragmentContainerId(), fragment, tag);
            currentFragment = fragment;
        } else {
            transaction.show(fragmentByTag);
            currentFragment = fragmentByTag;
        }
        transaction.commitAllowingStateLoss();
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void replaceFragment(MvpView view, String tag, Boolean needAddToBackStack, Pair<View, String>... pairs){
        replaceFragment((BaseFragment)view,tag,needAddToBackStack,pairs);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void replaceFragment(BaseFragment fragment, String tag, Boolean needAddToBackStack, Pair<View, String>... pairs) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            initSharedElement(transaction, pairs);
            if (needAddToBackStack) {
                transaction.addToBackStack(Preconditions.checkNotEmpty(tag));
                previousFragments.push(currentFragment.getTag());
            }
            transaction.replace(getFragmentContainerId(), fragment, tag);
            currentFragment = fragment;
            transaction.commitAllowingStateLoss();
    }


    @SuppressWarnings({"unchecked", "varargs"})
    private void initSharedElement(FragmentTransaction transaction, Pair<View, String>... pairs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (pairs != null && pairs.length != 0) {
                for (Pair<View, String> pair : pairs) {
                    transaction.addSharedElement(pair.first, pair.second);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() != 0){
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commitNow();
        }

        if (!previousFragments.empty()) {
            currentFragment = getSupportFragmentManager().findFragmentByTag(previousFragments.pop());
        }
        super.onBackPressed();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentFragmentTag",currentFragment.getTag());
        ArrayList<String> previousFragmentTags = new ArrayList<>();
        for(String tag : previousFragments){
            previousFragmentTags.add(tag);
        }
        outState.putStringArrayList("previousFragmentTags",previousFragmentTags);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String currentFragmentTag = savedInstanceState.getString("currentFragmentTag");
        if (!TextUtils.isEmpty(currentFragmentTag)){
            currentFragment = getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
        }

        ArrayList<String> previousFragmentTags = savedInstanceState.getStringArrayList("previousFragmentTags");
        if (previousFragmentTags != null){
            for (String tag : previousFragmentTags){
                previousFragments.push(tag);
            }
        }

    }

    public APP getApp() {
        return mApp;
    }

    public C getComponent() {
        return mComponent;
    }

    public void setToolbarTitle(int resId) {
        toolbarTitle.setText(resId);
    }

    public void setToolbarTitle(CharSequence title) {
        toolbarTitle.setText(title);
    }
}
