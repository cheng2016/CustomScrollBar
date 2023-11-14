package com.sample;



import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;


public class CustomScrollBarNew extends View {
    private final static String TAG = "CustomScrollBar";
    private Paint paint;
    private float scrollPosition;
    private WebView webView;
    private float trackWidth = 2f; // 轨迹的宽度
    private float barWidth = 6f; //指示器宽度
    private float barHeight;
    private int trackColor = Color.parseColor("#2B2E31");
    private int barColor = Color.parseColor("#8B8B8B");


    public CustomScrollBarNew(Context context) {
        super(context);
        init(context);
    }

    public CustomScrollBarNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,context);
    }

    public CustomScrollBarNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
    }

    private void init(AttributeSet attrs,Context context){
        // 获取自定义属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScrollBar);
        trackColor = typedArray.getColor(R.styleable.CustomScrollBar_trackColor, Color.parseColor("#2B2E31"));
        barColor = typedArray.getColor(R.styleable.CustomScrollBar_barColor, Color.parseColor("#8B8B8B"));
        trackWidth = typedArray.getDimension(R.styleable.CustomScrollBar_trackWidth, 2f);
        barWidth = typedArray.getDimension(R.styleable.CustomScrollBar_barWidth, 8f);
        typedArray.recycle(); // 记得回收
        init(context);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        barHeight = getHeight() / 4;
        float left = (width - barWidth) / 2;
        float top = scrollPosition * (height - barHeight);
        float right = width / 2;
        float bottom = top + barHeight;

        // 绘制轨迹
        paint.setColor(trackColor );
        paint.setStrokeWidth(trackWidth);
        float trackStartX = left + barWidth / 4;
        float trackEndX = left + barWidth / 4;
        float trackStartY = 0;
        float trackEndY = height;
        // 绘制轨迹
        canvas.drawLine(trackStartX, trackStartY, trackEndX, trackEndY, paint);

        Log.e(TAG, "left = " + left + "top =" + top + "  right" + right + "  bottom" + bottom);
        // 绘制bar
        paint.setColor(barColor);
        paint.setStrokeWidth(barWidth);
        top = Math.max(Math.min(top, height - barHeight), 0);
        bottom = Math.max(Math.min(bottom, height), barHeight);
        canvas.drawRect(left, top, right, bottom, paint);
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
                Log.e(TAG, "scrollToPosition scrollTo : " + scrollTo);
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
                Log.e(TAG, "updateScrollBarPosition scrollPosition : " + scrollPosition);
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
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    Log.i(TAG," scrollX = " + scrollX + " scrollY = " + scrollY +" oldScrollX = " + oldScrollX + " oldScrollY = " + oldScrollY);
                    Log.i(TAG," canScrollVertically = " +webView.canScrollVertically(1));
                    // 更新滚动条位置
                    // 更新滚动条位置
                    updateScrollBarPosition();
                    if (onWebViewScrollChangedCallback != null) {
                        if (!webView.canScrollVertically(-1)) {
                            Log.e(TAG, "webView.  头部");
                            onWebViewScrollChangedCallback.onScrollStatus(true, false);
                        } else if (!webView.canScrollVertically(1)) {
                            onWebViewScrollChangedCallback.onScrollStatus(false, true);
                            Log.e(TAG, "webView.  底部");
                        } else {
                            Log.e(TAG, "webView.  中间");
                            onWebViewScrollChangedCallback.onScrollStatus(false, false);
                        }
                    }
                }
            });
        }
    }

    private OnWebViewScrollChangedCallback onWebViewScrollChangedCallback;

    public void setOnWebViewScrollChangedCallback(OnWebViewScrollChangedCallback callback) {
        this.onWebViewScrollChangedCallback = callback;
    }

    // Interface for WebView scroll callback
    public interface OnWebViewScrollChangedCallback {
        void onScrollStatus(boolean  frist, boolean last);
    }
}




