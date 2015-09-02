package mo.com.phonesafe.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * 作者：MoMxMo on 2015/9/2 00:55
 * 邮箱：xxxx@qq.com
 *
 * 获取GPS定位的服务
 */


public class GPSService extends Service {
    private static final String TAG = "GPSService";

    LocationManager lm;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate  GPS服务 ");

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        /*public void requestLocationUpdates (
        String provider,    开启GPS位置请求
        long minTime,       多久更新位置请求
        float minDistance,  多远距离更新请求
         LocationListener listener
         )*/
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationlistener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy GPS服务关闭");
    }

    private LocationListener locationlistener = new LocationListener(){

        @Override
        public void onLocationChanged(Location location) {
            // 位置发送改变时的回调
//            location.
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}


