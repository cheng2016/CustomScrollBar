package com.sample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private final static String TAG = "CustomScrollBar";

    WebView mWebView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWebview();
        initControl();
    }

    // 获取WebView当前页面的底部Y轴坐标
    private int getBottomY(WebView webView) {
        int contentHeight = (int) Math.floor(webView.getContentHeight() * webView.getScale());
        int currentHeight = webView.getHeight() + webView.getScrollY();
        return Math.max(contentHeight, currentHeight);
    }

    void initWebview(){
        mWebView = findViewById(R.id.privacy_web_view);
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        // 设置允许访问文件数据
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowContentAccess(true);
        // 如果页面中使用了JavaScript，不加改代码页面不显示。
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                // 页面加载进度发生变化时调用
                if (newProgress == 100) {
                    // 页面加载完成后，获取底部Y轴坐标
                    int bottomY = getBottomY(mWebView);
                    Log.d(TAG, "Bottom Y Coordinate: " + bottomY);
                }
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                // 返回值为true时在WebView中打开，为false时调用浏览器打开
                return true;
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e(TAG, "error: " + error.getDescription());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e(TAG, "onPageFinished: ");

                int contentHeight = (int) (mWebView.getContentHeight() * mWebView.getScale());
                int webViewHeight = mWebView.getHeight();
                int scrollRange = contentHeight - webViewHeight;

                Log.e(TAG, "contentHeight : " + contentHeight
                        + " webViewHeight : " + webViewHeight
                        + " scrollRange : " + scrollRange);
            }
        });


        String url = "https://blog.csdn.net/qq_15128547/article/details/50588988";
        Log.e(TAG, "loadUrl: " + url);

        mWebView.loadUrl(url);
        CustomScrollBar scrollBar = findViewById(R.id.scrollBar);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!defualt){
                    scrollBar.setWebView(mWebView);
                } else {
                    customScrollBar.setWebView(mWebView);
                }
                defualt = !defualt;
            }
        });
    }

    boolean defualt = false;
    CustomScrollBarNew customScrollBar;
    void initControl(){
        customScrollBar = findViewById(R.id.customScrollBar);
        customScrollBar.setWebView(mWebView);
        customScrollBar.setOnWebViewScrollChangedCallback(new CustomScrollBarNew.OnWebViewScrollChangedCallback() {
            @Override
            public void onScrollStatus(boolean frist, boolean last) {
                if (frist) {
                    findViewById(R.id.pre).setEnabled(false);
                } else if (last) {
                    findViewById(R.id.next).setEnabled(false);
                } else {
                    findViewById(R.id.pre).setEnabled(true);
                    findViewById(R.id.next).setEnabled(true);
                }
            }
        });
        findViewById(R.id.pre).setEnabled(false);
        findViewById(R.id.pre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Math.max(Math.min(mWebView.getScrollY(),
                        (int) (mWebView.getContentHeight() * mWebView.getScale())) - mWebView.getHeight(), 0);
                Log.i(TAG, "spread_img current = " + current + " " +"  Math.abs(current) = "+ Math.abs(current)+
                        "  max = "+  (mWebView.getContentHeight() * mWebView.getScale() -  mWebView.getHeight())+
                        "  mWebView.getScrollY() = " + mWebView.getScrollY() + "  mWebView.getHeight() = " + mWebView.getHeight());
                mWebView.scrollTo(mWebView.getScrollX(), current);
            }
        });
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Math.min(mWebView.getScrollY() +  mWebView.getHeight(),
                        (int) (mWebView.getContentHeight() * mWebView.getScale() -  mWebView.getHeight()));
                Log.i(TAG, "packup_img current = " + current + " " +"  Math.abs(current) = "+ Math.abs(current)+
                        "  max = "+  (mWebView.getContentHeight() * mWebView.getScale() -  mWebView.getHeight())+
                        "  mWebView.getScrollY() = " + mWebView.getScrollY() + "  mWebView.getHeight() = " + mWebView.getHeight());
                mWebView.scrollTo(mWebView.getScrollX(), current);
            }
        });
    }

}