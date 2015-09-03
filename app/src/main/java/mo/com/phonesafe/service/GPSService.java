package mo.com.phonesafe.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import mo.com.phonesafe.R;
import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.tools.Constants;
import mo.com.phonesafe.tools.ModifyOffset;

/**
 * 作者：MoMxMo on 2015/9/2 00:55
 * 邮箱：xxxx@qq.com
 * <p/>
 * 获取GPS定位的服务
 */


public class GPSService extends Service {
    private static final String TAG = "GPSService";

    LocationManager mLm;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate  GPS服务 ");

        mLm = (LocationManager) getSystemService(LOCATION_SERVICE);

        /*public void requestLocationUpdates (
        String provider,    开启GPS位置请求
        long minTime,       多久更新位置请求
        float minDistance,  多远距离更新请求
         LocationListener listener
         )*/
        mLm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy GPS服务关闭");
        mLm.removeUpdates(mListener);
    }

    /**
     * 使用火星坐标获取真正的坐标
     *
     * @param x
     * @param y
     * @return
     */
    private double[] getLocaltion(double x, double y) {

        InputStream in = null;
        try {
            in = getResources().openRawResource(R.raw.axisoffset);
            ModifyOffset instance = ModifyOffset.getInstance(in);

            ModifyOffset.PointDouble pointDouble = new ModifyOffset.PointDouble(x, y);

            ModifyOffset.PointDouble s2c = instance.s2c(pointDouble);

            return new double[]{s2c.x, s2c.y};
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private LocationListener mListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //精度
            float accuracy = location.getAccuracy();

            //海拔
            double altitude = location.getAltitude();

            //纬度
            final double latitude = location.getLatitude();

            //经度
            final double longitude = location.getLongitude();

            Log.i(TAG, "经度：" + longitude);
            Log.i(TAG, "纬度：" + latitude);
            Log.i(TAG, "海拔：" + altitude);
            Log.i(TAG, "精度：" + accuracy);
            // 获取安全号码
            final String phone_number = PreferenceUtils.getString(GPSService.this, Constants.SJFD_PHONE_NUMBER);


            final double[] mdoubles = getLocaltion(longitude, latitude);

            //使用聚合数据查询位置
            // 去网络获取具体位置信息
            // 接口地址：http://lbs.juhe.cn/api/getaddressbylngb
            // 支持格式：JSON/XML
            // 请求方式：GET
            // 请求示例：http://lbs.juhe.cn/api/getaddressbylngb?lngx=116.407431&lngy=39.914492
            // 请求参数：
            // 名称 类型 必填 说明
            // lngx String Y google地图经度 (如：119.9772857)
            // lngy String Y google地图纬度 (如：27.327578)
            // dtype String N 返回数据格式：json或xml,默认json

            HttpUtils mHttp = new HttpUtils();
            String url = "http://lbs.juhe.cn/api/getaddressbylngb?lngx=" + mdoubles[0] +
                    "&lngy=" + mdoubles[1];

            mHttp.send(HttpRequest.HttpMethod.CONNECT.GET, url, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {

                    /*成功获取数据*/
                    String json = responseInfo.result;

                    //解析json格式数据
                    String realAddress = null;
                    try {
                        realAddress = getRealAddress(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // 发送短信
                        final SmsManager sms = SmsManager.getDefault();
                        // 获取安全号码


                        sms.sendTextMessage(phone_number, null, "longitude:" + mdoubles[0] +
                                "   latitude:" + mdoubles[1] +
                                "phone_number:"
                                , null, null);
                        //关闭服务
                        stopSelf();

                    }


                    Log.i(TAG, "真实地址是：" + realAddress);

                    // 发送短信
                    final SmsManager sms = SmsManager.getDefault();
                    // 获取安全号码


                    sms.sendTextMessage(phone_number, null, "longitude:" + mdoubles[0] +
                            "   latitude:" + mdoubles[1] +
                            "phone_number:" + realAddress
                            , null, null);
                    //关闭服务
                    stopSelf();
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    /*访问失败*/

                    // 发送短信
                    final SmsManager sms = SmsManager.getDefault();


                    sms.sendTextMessage(phone_number, null, "longitude:" + mdoubles[0] + "   latitude:" + mdoubles[1], null, null);

                    Log.i(TAG, "服务器访问失败：");
                    //关闭服务
                    stopSelf();
                }
            });


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

    /**
     * 解析json  获取地址
     *
     * @param json
     * @return {
     * "resultcode": "1",
     * "resultinfo": "Successful",
     * "row": {
     * "status": "OK",
     * "result": {
     * "location": {
     * "lng": 116.407431,
     * "lat": 39.914492
     * },
     * "formatted_address": "北京市东城区东长安街",
     * "business": "天安门,前门,大栅栏",
     * "addressComponent": {
     * "city": "北京市",
     * "direction": "",
     * "distance": "",
     * "district": "东城区",
     * "province": "北京市",
     * "street": "东长安街",
     * "street_number": ""
     * },
     * "cityCode": 131
     * }
     * }
     * }
     */
    private String getRealAddress(String json) throws JSONException {


        JSONObject data_1 = new JSONObject(json);

        JSONObject data_2 = data_1.getJSONObject("row");

        JSONObject data_3 = data_2.getJSONObject("result");

        String formatted_address = null;
        formatted_address = data_3.getString("formatted_address");

        return formatted_address;
    }
}


