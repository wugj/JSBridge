package jt.com.jsbridge.utils;


import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2016-06-07.
 */
public class AppHelper {

    private static final String TAG = "AppHelper";

    /**
     * 读取asset中的文件
     *
     * @author wujian
     * @date 2016-10-12 15:13
     * @company 上海若美科技有限公司
     */
    public static String assetFile2Str(Context context, String path) {
        InputStream in = null;
        try {
            in = context.getAssets().open(path);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = null;
            do {
                line = reader.readLine();
                if (line != null && !line.matches("^\\s*\\/\\/.*")) {
                    sb.append(line);
                }
            } while (line != null);
            reader.close();
            in.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
