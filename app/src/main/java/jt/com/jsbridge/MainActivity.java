package jt.com.jsbridge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import jt.com.jsbridge.view.jsbridge.BridgeHandler;
import jt.com.jsbridge.view.jsbridge.NMWebView;
import jt.com.jsbridge.view.jsbridge.ResponseCallBackFunction;

public class MainActivity extends AppCompatActivity {

    Boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NMWebView nmWebView = new NMWebView(MainActivity.this);

        nmWebView.registerHandler("picker", new BridgeHandler() {
            @Override
            public void handler(String data, ResponseCallBackFunction function) {
                HashMap<String, String> map = new HashMap<>();
                try {
                    map.put("title", new JSONObject(data).getString("title"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                map.put("status", flag.toString());
                if (flag)
                    function.onSuccessCallBack(new JSONObject(map).toString());
                else
                    function.onErrorCallBack(new JSONObject(map).toString());
                flag = !flag;
            }
        });
        nmWebView.getWebView().loadUrl("file:///android_asset/testJs.html");
//        nmWebView.getWebView().loadUrl("http://192.168.1.117:8000/testJs.html");
        setContentView(nmWebView);
    }
}
