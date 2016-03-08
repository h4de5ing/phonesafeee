package mo.com.phonesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import mo.com.phonesafe.bean.BlackBean;
import mo.com.phonesafe.dao.BlackDao;

/**
 * Created by Gh0st onn 2015/9/4 15:01
 * <p/>
 * <p/>
 * 电话和短信拦截的服务
 */


public class CallSmsInterceptService extends Service {

    private static final String TAG = "CallSmsInterceptService";
    private SmsIntercept smsReceiver;
    BlackDao dao;
    TelephonyManager tm;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate 服务创建");
        dao = new BlackDao(CallSmsInterceptService.this);

        //1.电话拦截
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(new CallInterceptPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);

        //2.短信拦截
         /*  <action android:name="android.provider.Telephony.SMS_RECEIVED" />*/

        //动态注册广播接收者
        smsReceiver = new SmsIntercept();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);      //设置优先级1000
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);

    }

    /*
     * 短信广播接收者
     */
    private class SmsIntercept extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //解析短信
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");

            for (Object obj : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                //发件人
                String address = sms.getOriginatingAddress();

                //短信内容
                String body = sms.getMessageBody();
                Log.i(TAG, "onReceive 来短信啦。。。。。。。address：" + address + " body:" + body);
                int type = dao.getType(address);
                if (type != -1) {
                    if (type == BlackBean.TYPE_ALL || type == BlackBean.TYPE_SMS) {
                        Log.i(TAG, "拦截短信啦。。。。。。。address：" + address + " body:" + body);
                        //在黑名单中，拦截短信，终止广播
                        abortBroadcast();
                    }
                }
            }
        }
    }

    /**
     * 电话状态监听类
     */
    private class CallInterceptPhoneStateListener extends PhoneStateListener {
        /**
         * Callback invoked when device call state changes.
         *
         * @see TelephonyManager#CALL_STATE_IDLE       闲置
         * @see TelephonyManager#CALL_STATE_RINGING     响铃
         * @see TelephonyManager#CALL_STATE_OFFHOOK     接听
         */
        public void onCallStateChanged(int state, final String incomingNumber) {
            //state  状态
            //incomingNumber    拨打进来的号码

            if (state == TelephonyManager.CALL_STATE_RINGING) {
                //电话响铃的时候

                int type = dao.getType(incomingNumber);
                if (type == BlackBean.TYPE_ALL || type == BlackBean.TYPE_CALL) {
                    //这个号码是被拦截的号码
                    //挂机
                    // ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));

                    try {
                        Class<?> clazz = Class.forName("android.os.ServiceManager");
                        Method method = clazz.getDeclaredMethod("getService", String.class);
                        IBinder binder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);

                        ITelephony iTelephony = ITelephony.Stub.asInterface(binder);

                        iTelephony.endCall();       //拦截电话

                        //电话Log的删除
                        final ContentResolver resolver = getContentResolver();
                        final Uri uri = CallLog.Calls.CONTENT_URI;

                        //内容观察者，当有电话内容的时候触发，然后删除最近的电话记录
                        resolver.registerContentObserver(uri, true, new ContentObserver(new Handler()) {
                            @Override
                            public void onChange(boolean selfChange) {
                                String where = CallLog.Calls.NUMBER + "=?";
                                String[] args = {incomingNumber};
                                resolver.delete(uri, where, args);
                            }
                        });


                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }

            }
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onStartCommand 服务关闭");
        super.onDestroy();
        /*注销广播*/
        unregisterReceiver(smsReceiver);
    }
}
