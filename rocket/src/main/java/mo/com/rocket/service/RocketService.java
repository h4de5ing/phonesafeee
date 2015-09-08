package mo.com.rocket.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import mo.com.rocket.RocketToast;

/**
 * 小火箭服务
 *
 * 作者：MoMxMo on 2015/9/6 18:48
 * 邮箱：xxxx@qq.com
 */


public class RocketService extends Service{
    private static final String TAG = "RocketService";
    private RocketToast rt;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate 小火箭服务开启");
        rt = new RocketToast(RocketService.this);
        rt.show();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy 小火箭服务关闭");
        rt.hide();
        super.onDestroy();
    }


}
