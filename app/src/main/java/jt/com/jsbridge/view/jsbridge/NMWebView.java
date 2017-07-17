package jt.com.jsbridge.view.jsbridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jt.com.jsbridge.R;


/**
 * @name: com.nanyibang.nomi.view.jsbridge
 * @description:
 * @author：Administrator
 * @date: 2016-11-02 11:13
 * @company: 上海若美科技有限公司
 */

@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
public class NMWebView extends LinearLayout {

    private Context mContext;
    private String mLocalJsPath = "js/WebViewJavascriptBridge.js";
    private Map<String, CallBackFunction> mCallBackFunctionMap = new HashMap<String, CallBackFunction>();
    private Map<String, BridgeHandler> mHandlerMap = new HashMap<String, BridgeHandler>();
    private WebView mWebView;

    public NMWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public NMWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NMWebView(Context context) {
        this(context, null);
    }

    public WebView getWebView() {
        return mWebView;
    }


    @SuppressLint("NewApi")
    protected void init() {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.layout_nomei_webview, this);
        mWebView = (WebView) view.findViewById(R.id.wv_web);
        // 聚焦 否则文本框点击不调用软盘
        mWebView.requestFocus();
        // 取消滚动条
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        final WebSettings settings = mWebView.getSettings();

        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        // 禁止file域
        // settings.setAllowFileAccess(false);
        // js可用
        settings.setJavaScriptEnabled(true);
        // 使用缓存
         settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        /*if (BangApplication.getInstance().isNetable()) {
        } else {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setWebContentsDebuggingEnabled(true);
        }

        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        /**
         * 用WebView显示图片，可使用这个参数 设置网页布局类型： 1、LayoutAlgorithm.NARROW_COLUMNS ：
         * 适应内容大小 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        }

        mWebView.setInitialScale(70);

        String ua = settings.getUserAgentString();
        // 双击放大
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        // 图片加载房子最后 在4.4之后设置为true时不能显示图片 并且设置false不影响速度 开启两个线程加载
        settings.setBlockNetworkImage(false);
        // settings.setDisplayZoomControls(false);
        settings.setDefaultTextEncodingName("utf-8");
        // 作为 HTML5 标准的一部分，绝大多数的浏览器都是支持 localStorage 的，
        // 但是鉴于它的安全特性（任何人都能读取到它，
        // 尽管有相应的限制，将敏感数据存储在这里依然不是明智之举），Android 默认是关闭该功能的。
        settings.setDomStorageEnabled(true);
        mWebView.addJavascriptInterface(this, "Android");
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new BridgeWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                // 注意:
                // 必须要这一句代码:result.confirm()表示:
                // 处理结果为确定状态同时唤醒WebCore线程
                // 否则不能继续点击按钮
                result.confirm();
                return true;
            }
        });
    }

    /**
     * 解析url 获取调用方法以及参数 回调方法等
     * @author wujian
     * @date 2017-01-19 16:58
     * @company 上海若美科技有限公司
     */
    protected void handlerData(String url) {
        String functioName = BridgeUtil.getFunctionFromReturnUrl(url);//fetchMessageQueue
        String data = BridgeUtil.getDataFromReturnUrl(url);
        if (!TextUtils.isEmpty(functioName)) {
            CallBackFunction callBackFunction = mCallBackFunctionMap
                    .get(functioName);
            if (callBackFunction != null) {
                callBackFunction.onCallBack(data);
                mCallBackFunctionMap.remove(callBackFunction);
            }
        }

    }

    /**
     * 读取请求跟数据 并设置读取成功后的回调函数  key为fetchMessageQueue
     * @author wujian
     * @date 2017-07-07 17:45
     * @company 上海若美科技有限公司
     */
    protected void flushMessageQueue(String url) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA,
                    new CallBackFunction() {

                        @Override
                        public void onCallBack(final String data) {
                            //第八步  处理数据 并调用数据中handleName的对应的方法并注册成功或者失败的回调方法
                            List<Message> messageList = Message
                                    .toArrayList(data);
                            if (messageList == null && messageList.isEmpty()) {
                                return;
                            }
                            for (Message message : messageList) {
                                final Message tempMessage = message;
                                String responseId = message.getResponseId();
                                ResponseCallBackFunction responseCallBackFunction = null;
                                if (TextUtils.isEmpty(responseId)) {
                                    responseCallBackFunction = new ResponseCallBackFunction() {

                                        @Override
                                        public void onSuccessCallBack(
                                                String responseData) {
                                            tempMessage
                                                    .setResponseData(responseData);
                                            tempMessage.setResponseId(tempMessage
                                                    .getSuccessCallbackId());
                                            //第九步   调用JSbridg中的handleMessageFromNative方法 传输处理结果的数据
                                            dispatchMessage(tempMessage);
                                        }

                                        @Override
                                        public void onErrorCallBack(
                                                String responseData) {
                                            tempMessage
                                                    .setResponseData(responseData);
                                            tempMessage.setResponseId(tempMessage
                                                    .getErrorCallbackId());
                                            //第九步   调用JSbridg中的handleMessageFromNative方法 传输处理结果的数据
                                            dispatchMessage(tempMessage);
                                        }

                                    };
                                    //读取注册的函数调用
                                    BridgeHandler handler = mHandlerMap
                                            .get(tempMessage.getHandlerName());
                                    if (handler != null) {
                                        handler.handler(message.getData(),
                                                responseCallBackFunction);
                                    }
                                }
                            }

                        }
                    });
        }

    }

    //调用JsBridge中的dispatchMessageFromNative返回传递处理结果
    private void dispatchMessage(Message message) {
        String messageJson = message.toJson();
        // 去除特殊字符串
        messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
        messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            BridgeUtil.loadUrl(mWebView, String.format(
                    BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson));
        }
    }


    private void loadUrl(String jsUrl, CallBackFunction callBackFunction) {
        BridgeUtil.loadUrl(mWebView, jsUrl);
        mCallBackFunctionMap.put(BridgeUtil.parseFunctionName(jsUrl),
                callBackFunction);
    }

    /**
     * 注册handler
     * @param handlerName
     * @param handler
     */
    public void registerHandler(String handlerName, BridgeHandler handler) {
        mHandlerMap.put(handlerName, handler);
    }

    class BridgeWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                url = url.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                url = URLDecoder.decode(url, "UTF-8");
            } catch (Exception e) {
            }
            //
            if (url.startsWith(BridgeUtil.NYB_RETURN_DATA)) {
                //第七步   读取到了数据 并通过key为fetchMessageQueue的方法处理数据
                handlerData(url);
                return true;
            } else if (url.startsWith(BridgeUtil.NYB_OVERRIDE_SCHEMA)) {
                //第五步  调用js的fetchMessageQueue方法读取请求的方法跟数据
                flushMessageQueue(url);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //第一步 注册 jsBridge
            if (!TextUtils.isEmpty(mLocalJsPath)) {
                BridgeUtil.bridgeWebViewLoadLoaclJs(view, mLocalJsPath);
            }
        }

    }
}
