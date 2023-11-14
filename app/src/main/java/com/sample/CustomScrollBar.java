package com.sample;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

public class CustomScrollBar extends View {
    private final static String TAG = "CustomScrollBar";
    private Paint paint;
    private float scrollPosition;
    private WebView webView;

    private float trackWidth = 5; // 轨迹的宽度

    private int barWidth = 16;

    private int barHeight;

    private Context mContext;

    public CustomScrollBar(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public CustomScrollBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public CustomScrollBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        barHeight = height / 3;

        float left = (width - barWidth)/2;
        float top = scrollPosition * (height - barHeight);
        float right = width / 2  ;
        float bottom = top + barHeight;

        Log.e(TAG, "left = " + left + "top = " + top + "  right = " + right + "  bottom = " + bottom);

        // 绘制轨迹
        paint.setColor( Color.parseColor("#2B2E31"));
        paint.setStrokeWidth(trackWidth);
        float trackStartX = left + barWidth/4;
        float trackEndX = left + barWidth /4;
        float trackStartY = 0;
        float trackEndY = height;
        // 绘制轨迹
        canvas.drawLine(trackStartX, trackStartY, trackEndX, trackEndY, paint);


        // 绘制bar
        paint.setColor(Color.parseColor("#8B8B8B"));
        paint.setStrokeWidth(barWidth);
        top = Math.max(Math.min(top, height - barHeight), 0);
        bottom = Math.max(Math.min(bottom, height), barHeight);
        canvas.drawRect(left, top < 0 ? 0 : top, right, bottom, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float y = event.getY() / getHeight();
                scrollToPosition(y);
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private void scrollToPosition(float position) {
        if (webView != null) {
            int contentHeight = (int) (webView.getContentHeight() * webView.getScale());
            int webViewHeight = webView.getHeight();
            int scrollRange = contentHeight - webViewHeight;
            if (scrollRange > 0) {
                int scrollTo = (int) (position * scrollRange);
                Log.i(TAG,"scrollToPosition  position = " + position + "  scrollTo = " + scrollTo);
                webView.scrollTo(0, Math.min(Math.max(scrollTo, 0), scrollRange));
            }
        }
    }

    private void updateScrollBarPosition() {
        if (webView != null) {
            // 获取 WebView 的滚动信息
            int contentHeight = (int) (webView.getContentHeight() * webView.getScale());
            int webViewHeight = webView.getHeight();
            int scrollRange = contentHeight - webViewHeight;
            // 计算滚动条位置
            if (scrollRange > 0) {
                scrollPosition = (float) webView.getScrollY() / scrollRange;
                Log.i(TAG,"updateScrollBarPosition  scrollPosition = " + scrollPosition );
                invalidate();
            }
        }
    }

    public void setWebView(WebView webView) {
        this.webView = webView;

        // 设置 WebView 的滚动监听
        if (webView != null) {
            webView.setOnScrollChangeListener(new OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    // 更新滚动条位置
                    updateScrollBarPosition();
                    if (onWebViewScrollChangedCallback != null) {
                        if (webView.getScrollY() == 0) {
                            onWebViewScrollChangedCallback.onScrollStatus(true, false);
                        } else if ( (int) (webView.getContentHeight() * webView.getScale() - webView.getHeight()) - webView.getScrollY() <=1) {
                            onWebViewScrollChangedCallback.onScrollStatus(false, true);
                        } else {
                            onWebViewScrollChangedCallback.onScrollStatus(false, false);
                        }
                    }
                }
            });
        }
    }

    OnWebViewScrollChangedCallback onWebViewScrollChangedCallback;

    public void setOnWebViewScrollChangedCallback(OnWebViewScrollChangedCallback callback) {
        this.onWebViewScrollChangedCallback = callback;
    }

    // Interface for WebView scroll callback
    public interface OnWebViewScrollChangedCallback {
        void onScrollStatus(boolean  frist, boolean last);
    }
}


