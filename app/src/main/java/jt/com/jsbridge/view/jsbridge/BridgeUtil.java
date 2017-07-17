package jt.com.jsbridge.view.jsbridge;

import android.annotation.SuppressLint;
import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import jt.com.jsbridge.utils.AppHelper;


public class BridgeUtil {

	final static String NYB_OVERRIDE_SCHEMA = "nybjs://";
	final static String NYB_RETURN_DATA = NYB_OVERRIDE_SCHEMA + "return/";  // yy://return/{function}/{data}
	final static String NYB_FETCHQUEUE = NYB_RETURN_DATA + "fetchMessageQueue/";
	final static String EMPTY_STR = "";
	final static String UNDERLINE_STR = "_";
	final static String SPLIT_MARK = "/";

	final static String CALLBACK_ID_FORMAT = "JAVA_CB_%s";
	final static String JS_HANDLE_MESSAGE_FROM_JAVA = "javascript:window.WebViewJavascriptBridge.handleMessageFromNative('%s');";
	final static String JS_FETCH_QUEUE_FROM_JAVA = "javascript:window.WebViewJavascriptBridge.fetchMessageQueue();";
	public final static String JAVASCRIPT_STR = "javascript:";

	public static String parseFunctionName(String jsUrl) {
		return jsUrl.replace("javascript:window.WebViewJavascriptBridge.", "")
				.replaceAll("\\(.*\\);", "");
	}

	public static void bridgeWebViewLoadLoaclJs(WebView webView,
			String localJsFilePath) {
		String jsContent = AppHelper.assetFile2Str(webView.getContext(),
				localJsFilePath);
		loadUrl(webView, JAVASCRIPT_STR + jsContent);
	}

	/**
	 * ͨ获取调用方法  注册的调用方法
	 * @param url
	 * @return
	 */
	public static String getFunctionFromReturnUrl(String url) {
		String temp = url.replace(NYB_RETURN_DATA, EMPTY_STR);
		String[] functionAndData = temp.split(SPLIT_MARK);
		if (functionAndData.length > 1) {
			return functionAndData[0];
		}
		return null;
	}

	/**
	 * 解析  数据
	 * 
	 * @param url
	 * @return
	 */
	public static String getDataFromReturnUrl(String url) {
		if (url.startsWith(NYB_FETCHQUEUE)) {
			//此次是安卓调用fetchMessageQueue 读取的数据
			return url.replace(NYB_FETCHQUEUE, EMPTY_STR);
		}

		String temp = url.replace(NYB_RETURN_DATA, EMPTY_STR);
		String[] functionAndData = temp.split(SPLIT_MARK);
		if (functionAndData.length > 2) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < functionAndData.length; i++) {
				sb.append(functionAndData[i]);
			}
			return sb.toString();
		}
		return null;
	}

	@SuppressLint("NewApi")
	public static void loadUrl(WebView webView, String url) {
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				webView.evaluateJavascript(url, new ValueCallback() {

					@Override
					public void onReceiveValue(Object value) {

					}

				});
			} else {
				webView.loadUrl(url);
			}
		} catch (Exception e) {
		}
	}

}
