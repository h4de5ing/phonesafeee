package mo.com.gpstest;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView tv_longitude;
    private TextView tv_latitude;
    private TextView tv_accuracy;
    private TextView tv_altitude;
    private LocationManager lm;
    private GPSLocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_longitude = (TextView) findViewById(R.id.tv_longitude);
        tv_latitude = (TextView) findViewById(R.id.tv_latitude);
        tv_altitude = (TextView) findViewById(R.id.tv_altitude);
        tv_accuracy = (TextView) findViewById(R.id.tv_accuracy);

        /**
         * GPS manager need to permission
         */
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new GPSLocationListener();

    }
    public class GPSLocationListener implements android.location.LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            //精度
            float accuracy = location.getAccuracy();

            //海拔
            double altitude = location.getAltitude();

            //纬度
            double latitude = location.getLatitude();

            //经度
            double longitude = location.getLongitude();

            tv_longitude.setText("经度："+longitude);
            tv_latitude.setText("纬度："+latitude);
            tv_altitude.setText("海拔："+altitude);
            tv_accuracy.setText("精度："+accuracy);
            Log.i(TAG, "经度：" + longitude);
            Log.i(TAG, "纬度："+latitude);
            Log.i(TAG, "海拔："+altitude);
            Log.i(TAG, "精度："+accuracy);
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
    @Override
    protected void onResume() {
        super.onResume();

        String provider = LocationManager.GPS_PROVIDER;
        long minTime = 0;// 多久更新一次gps位置
        float minDistance = 0;// 移动多少距离

        /**
         * @param provider the name of the provider with which to register
         * @param minTime minimum time interval between location updates, in milliseconds
         * @param minDistance minimum distance between location updates, in meters
         * @param listener a {@link LocationListener} whose
         */
        lm.requestLocationUpdates(provider, minTime, minDistance, listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        lm.removeUpdates(listener);
    }




}
