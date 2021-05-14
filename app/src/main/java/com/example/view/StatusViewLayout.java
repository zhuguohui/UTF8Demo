package com.example.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.utf8demo.BuildConfig;
import com.example.utf8demo.R;


/**
 * Created by Vincent Woo
 * Date: 2016/12/26
 * Time: 16:06
 */

public class StatusViewLayout extends RelativeLayout implements View.OnClickListener {
    private View mEmptyView, mErrorView, mLoadingView, mNoNetworkView;
    private int mEmptyViewResID, mErrorViewResID, mLoadingViewResID, mNoNetworkViewResID;
    private OnRetryListener mListener;

    public StatusViewLayout(Context context) {
        this(context, null);
    }

    public StatusViewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttrs(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StatusViewLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        parseAttrs(context, attrs);
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatusViewLayout, 0, 0);
            mEmptyViewResID = typedArray.getResourceId(R.styleable.StatusViewLayout_empty_view,
                    R.layout.layout_status_view_layout_empty_view);
            mErrorViewResID = typedArray.getResourceId(R.styleable.StatusViewLayout_error_view,
                    R.layout.layout_status_view_layout_error_view);
            mLoadingViewResID = typedArray.getResourceId(R.styleable.StatusViewLayout_loading_view,
                    R.layout.layout_status_view_layout_loading_view);
            mNoNetworkViewResID = typedArray.getResourceId(R.styleable.StatusViewLayout_no_network_view,
                    R.layout.layout_status_view_layout_no_network_view);

            typedArray.recycle();
        } else {
            mEmptyViewResID = R.layout.layout_status_view_layout_empty_view;
            mErrorViewResID = R.layout.layout_status_view_layout_error_view;
            mLoadingViewResID = sLoadingLayoutId;
            mNoNetworkViewResID = R.layout.layout_status_view_layout_no_network_view;
        }
    }

    static int sLoadingLayoutId = R.layout.layout_status_view_layout_loading_view;
    ;

    public static void setLoadingLayout(int LodingLayoutID) {
        sLoadingLayoutId = LodingLayoutID;
    }

    public void showEmpty() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(View.INVISIBLE);
        }
        if (mEmptyView == null) {
            mEmptyView = LayoutInflater.from(getContext()).inflate(mEmptyViewResID, null);
            mEmptyView.setOnClickListener(this);
            LayoutParams mLayoutParams =
                    new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            addView(mEmptyView, mLayoutParams);
        }
        mEmptyView.setVisibility(View.VISIBLE);
    }

    public void showError() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(View.INVISIBLE);
        }
        if (mErrorView == null) {
            mErrorView = LayoutInflater.from(getContext()).inflate(mErrorViewResID, null);
            LayoutParams mLayoutParams =
                    new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            addView(mErrorView, mLayoutParams);
        }
        mErrorView.setVisibility(View.VISIBLE);
    }

    public void showError(String errorInfo, OnClickListener errorOnClickListener) {
        showError();
        if (!BuildConfig.DEBUG) {
            errorInfo = "加载出错";
        }
        if (mErrorView != null) {
            View view = mErrorView.findViewById(R.id.tv_error_info);
            View reloadView = mErrorView.findViewById(R.id.btn_reload);
            if (reloadView != null) {
                reloadView.setOnClickListener(this);
            }
            if (!TextUtils.isEmpty(errorInfo) || view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setText(errorInfo);
            }
            if (errorOnClickListener != null) {
                reloadView.setOnClickListener(errorOnClickListener);
            }
        }
    }

    public void showLoading() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(View.INVISIBLE);
        }
        if (mLoadingView == null) {
            mLoadingView = LayoutInflater.from(getContext()).inflate(mLoadingViewResID, null);
            mLoadingView.setOnClickListener(this);
            LayoutParams mLayoutParams =
                    new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            addView(mLoadingView, mLayoutParams);
        }
        mLoadingView.setVisibility(View.VISIBLE);
    }

    public void showNoNetwork() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(View.INVISIBLE);
        }
        if (mNoNetworkView == null) {
            mNoNetworkView = LayoutInflater.from(getContext()).inflate(mNoNetworkViewResID, null);
            mNoNetworkView.setOnClickListener(this);
            LayoutParams mLayoutParams =
                    new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            addView(mNoNetworkView, mLayoutParams);
        }
        mNoNetworkView.setVisibility(View.VISIBLE);
    }

    public void showContent() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view == mEmptyView) {
                mEmptyView.setVisibility(INVISIBLE);
            } else if (view == mErrorView) {
                mErrorView.setVisibility(INVISIBLE);
            } else if (view == mLoadingView) {
                mLoadingView.setVisibility(INVISIBLE);
            } else if (view == mNoNetworkView) {
                mNoNetworkView.setVisibility(INVISIBLE);
            } else {
                view.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if ((view == mEmptyView || view == mErrorView || view == mNoNetworkView) && mListener != null) {
            mListener.OnRetry();
        }
    }

    public void setOnRetryListener(OnRetryListener listener) {
        mListener = listener;
    }

    public interface OnRetryListener {
        void OnRetry();
    }
}
