package mo.com.phonesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import mo.com.phonesafe.dao.AddressDao;
import mo.com.phonesafe.view.AddressToast;

/**
 * Created by Gh0st on 2015/9/6 13:53
 *
 * 电话号码归属地的服务
 */


public class NumberAddressService extends Service{
    private static final String TAG = "NumberAddressService";
    private CallPhoneStateListener scl;
    private TelephonyManager tm;
    private CallOutReceiver callOutReceiver;
    private AddressToast at;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate 号码归属地服务----->开启");
        super.onCreate();

        //3.创建自定义Toast
        at = new AddressToast(this);

        //1.来电显示号码归属地
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        scl = new CallPhoneStateListener();
        tm.listen(scl, PhoneStateListener.LISTEN_CALL_STATE);

        /*2.去电显示号码归属地*/
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        callOutReceiver = new CallOutReceiver();
        registerReceiver(callOutReceiver, filter);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onCreate 号码归属地服务-----关闭");

        //注销服务
        tm.listen(scl, PhoneStateListener.LISTEN_NONE);
        //注销广播
        unregisterReceiver(callOutReceiver);
        at.hide();
        super.onDestroy();
    }

    private class CallPhoneStateListener extends  PhoneStateListener{
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:      //闲置
                    at.hide();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:   //响铃
                    String address = AddressDao.getAddress(NumberAddressService.this, incomingNumber);

                    if (at == null) {
                        at = new AddressToast(NumberAddressService.this);
                    }
                    at.show(address);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:   //接机
                    break;
            }
        }
    }

    /**
     * 去电广播
     */
    private class CallOutReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //获取拨打出去的号码
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            //通过号码归属地查询号码的归属地，也可以通过网络查询的方式
            String address = AddressDao.getAddress(NumberAddressService.this, number);
            if (at == null) {
                at = new AddressToast(NumberAddressService.this);
            }
            at.show(address);
            Log.i(TAG, "onCallStateChanged 来电响铃。。。。。。。。。" + address);
        }
    }

}
