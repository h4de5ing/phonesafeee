package mo.com.phonesafe;

import android.app.Application;
import android.util.Log;

import com.baidu.apistore.sdk.ApiStoreSDK;


/**
 * 作者：MoMxMo on 2015/9/16 19:07
 * 邮箱：xxxx@qq.com
 * <p/>
 */


public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        // 应用打开时的回调
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.i(TAG, "有未捕获的异常" + thread.getName());
                ex.getStackTrace();
            }
        });
    }
}
