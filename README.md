# CustomScrollBar

Android 自定义纵向滚动调，自定义垂直滚动条，自定义Webview滚动条

## [功能介绍](https://blog.csdn.net/chengzhenjia/article/details/134402282)

1. 传统滚动条有的功能他都有，可进行手势滑动，可与webView进行滑动联动

2. 可以控制滚动条在任意位置，和任意高度，不用与webview高度保持一致

3. 可以控制滚动条宽度提高可触摸性，甩传统滚动条不好触摸滚动缺点一条街

4. 暴露了接口可以进行到顶部到底部的相关逻辑处理

5. 增加控制上下翻页按钮，可以实现翻页功能 

## 效果图

   ![](效果图%202023-11-14%2017-10-52.png)



## 如果对你有帮助，欢迎赞助，你的赞助是我分享的最大动力！！

  ![](56130.png)       ![](772ac.png)

## Contact Me

- Email: mitnick.cheng@outlook.com
- QQ: 1102743539
- [CSDN: souls0808](https://blog.csdn.net/chengzhenjia?type=blog)


## View 代码

         
         import android.content.Context;
         import android.content.res.TypedArray;
         import android.graphics.Canvas;
         import android.graphics.Color;
         import android.graphics.Paint;
         import android.os.Handler;
         import android.util.AttributeSet;
         import android.util.Log;
         import android.view.MotionEvent;
         import android.view.View;
         import android.webkit.WebView;
         
         import com.baidu.naviauto.R;
         
         import java.lang.ref.WeakReference;
         
         public class CustomScrollBar extends View {
             public static final String TAG = "CustomScrollBar";
             private WeakReference<WebView> webViewRef;
             private Paint paint;
             private float scrollPosition;
             private float trackWidth = 2f;
             private float barWidth = 8f;
             private float barHeight;
             private int trackColor = Color.parseColor("#2B2E31");
             private int barColor = Color.parseColor("#8B8B8B");
         
             public CustomScrollBar(Context context) {
                 this(context, null, 0);
             }
         
             public CustomScrollBar(Context context, AttributeSet attrs) {
                 this(context, attrs, 0);
             }
         
             public CustomScrollBar(Context context, AttributeSet attrs, int defStyleAttr) {
                 super(context, attrs, defStyleAttr);
                 init(context, attrs);
             }
         
             private void init(Context context, AttributeSet attrs) {
                 paint = new Paint();
                 paint.setColor(Color.GRAY);
                 paint.setStyle(Paint.Style.FILL);
                 if (attrs != null) {
                     // 获取自定义属性值
                     TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScrollBar);
                     trackColor = typedArray.getColor(R.styleable.CustomScrollBar_trackColor, Color.parseColor("#2B2E31"));
                     barColor = typedArray.getColor(R.styleable.CustomScrollBar_barColor, Color.parseColor("#8B8B8B"));
                     trackWidth = typedArray.getDimension(R.styleable.CustomScrollBar_trackWidth, 2f);
                     barWidth = typedArray.getDimension(R.styleable.CustomScrollBar_barWidth, 8f);
                     typedArray.recycle(); // 记得回收
                 }
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
                 paint.setColor(trackColor);
                 paint.setStrokeWidth(trackWidth);
                 float trackStartX = left + barWidth / 4;
                 float trackEndX = left + barWidth / 4;
                 float trackStartY = 0;
                 float trackEndY = height;
                 // 绘制轨迹
                 canvas.drawLine(trackStartX, trackStartY, trackEndX, trackEndY, paint);
         
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
                 WebView webView = webViewRef.get();
                 if (webView != null) {
                     int contentHeight = (int) (webView.getContentHeight() * webView.getScale());
                     int webViewHeight = webView.getHeight();
                     int scrollRange = contentHeight - webViewHeight;
         
                     if (scrollRange > 0) {
                         int scrollTo = (int) (position * scrollRange);
                         webView.scrollTo(0, Math.min(Math.max(scrollTo, 0), scrollRange));
                     }
                 }
             }
         
             public void setWebView(WebView webView) {
                 this.webViewRef = new WeakReference<>(webView);
                 // 设置 WebView 的滚动监听
                 if (webView != null) {
                     webView.setOnScrollChangeListener(new OnScrollChangeListener() {
                         @Override
                         public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                             Log.i(TAG, "onScrollChange scrollY = " + scrollY + " webView.canScrollVertically(1) = " + webView.canScrollVertically(1));
                             // 更新滚动条位置
                             updateScrollBarPosition();
                             if (onWebViewScrollChangedCallback != null) {
                                 if (!webView.canScrollVertically(-1)) {
                                     onWebViewScrollChangedCallback.onScrollStatus(true, false);
                                 } else {
                                     onWebViewScrollChangedCallback.onScrollStatus(false,
                                             !webView.canScrollVertically(1));
                                 }
                             }
                         }
                     });
                 }
             }
         
             private void updateScrollBarPosition() {
                 WebView webView = webViewRef.get();
                 if (webView != null) {
                     // 获取 WebView 的滚动信息
                     int contentHeight = (int) (webView.getContentHeight() * webView.getScale());
                     int webViewHeight = webView.getHeight();
                     int scrollRange = contentHeight - webViewHeight;
         
                     // 计算滚动条位置
                     if (scrollRange > 0) {
                         scrollPosition = (float) webView.getScrollY() / scrollRange;
                         invalidate();
                     }
                 }
             }
         
             private OnWebViewScrollChangedCallback onWebViewScrollChangedCallback;
         
             public void setOnWebViewScrollChangedCallback(OnWebViewScrollChangedCallback callback) {
                 this.onWebViewScrollChangedCallback = callback;
             }
         
             public interface OnWebViewScrollChangedCallback {
                 void onScrollStatus(boolean frist, boolean last);
             }
         
             public void setViewListener(View view, boolean isPre) {
                 view.setOnClickListener(new OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         webViewScrollTo(webViewRef.get(), isPre);
                     }
                 });
                 view.setOnTouchListener(new OnTouchListener() {
                     private Handler handler;
                     private volatile boolean isLongPressing = false;
                     @Override
                     public boolean onTouch(View v, MotionEvent event) {
                         switch (event.getAction()) {
                             case MotionEvent.ACTION_DOWN:
                                 if (!isLongPressing) {
                                     isLongPressing = true;
                                     handler = new Handler();
                                     handler.postDelayed(new Runnable() {
                                         @Override
                                         public void run() {
                                             if (isLongPressing) {
                                                 WebView webView = webViewRef.get();
                                                 webViewScrollTo(webView, isPre);
                                                 if (webView != null) {
                                                     boolean frist = !webView.canScrollVertically(-1);
                                                     boolean last = !webView.canScrollVertically(1);
                                                     Log.i(TAG, "onTouch isPre " + isPre + " frist = " + frist + " last = " + last);
                                                     if ((isPre && !frist) || (!isPre && !last)) {
                                                         handler.postDelayed(this, 100);
                                                     } else {
                                                         handler.removeCallbacksAndMessages(null); // 移除定时器
                                                         isLongPressing = false;
                                                         Log.i(TAG, "onTouch ---移除定时器---");
                                                     }
                                                 }
                                             }
                                         }
                                     }, 500); // 延迟500毫秒启动，根据需求调整
                                 }
                                 break;
                             case MotionEvent.ACTION_UP:
                             case MotionEvent.ACTION_CANCEL:
                                 if (handler != null) {
                                     handler.removeCallbacksAndMessages(null); // 移除定时器
                                     isLongPressing = false;
                                     Log.i(TAG, "onTouch up cancel 移除定时器");
                                 }
                                 break;
                         }
                         return false; // 返回false以保证点击事件能够继续传递给下一层
                     }
                 });
             }
         
             private void webViewScrollTo(WebView webView, boolean isPre) {
                 if (webView != null) {
                     int preIndex = Math.max(Math.min(webView.getScrollY(),
                             (int) (webView.getContentHeight() * webView.getScale())) - webView.getHeight(), 0);
                     int nextIndex = Math.min(webView.getScrollY() + webView.getHeight(),
                             (int) (webView.getContentHeight() * webView.getScale() - webView.getHeight()));
                     webView.scrollTo(webView.getScrollX(), isPre ? preIndex : nextIndex);
                     Log.i(TAG, "webViewScrollTo isPre = " + isPre + " ScrollTo : " + (isPre ? preIndex : nextIndex));
                 }
             }
         }



# License

    Copyright 2023 cheng2016,Inc.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
