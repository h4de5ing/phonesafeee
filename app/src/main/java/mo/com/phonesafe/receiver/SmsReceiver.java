package mo.com.phonesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

import mo.com.phonesafe.R;
import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.service.GPSService;
import mo.com.phonesafe.tools.Constants;

/**
 * Created by Gh0st on 2015/9/1 22:26
 * <p/>
 * 短信广播接收者
 * <p/>
 * 专门处理安全号码发送过来的指令
 */


public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        // 1.判断是否已经开启防盗保护
        boolean isProtect = PreferenceUtils.getBoolean(context, Constants.SJFD_PROTECT);
        if (isProtect) {
            //已经开启，进行下面的操作
            //手机防盗功能
            sjfd_function(context, intent);
        }
    }

    /**
     * 手机防盗业务
     * @param context
     * @param intent
     */
    private void sjfd_function(Context context, Intent intent) {
        //安全号码
        String sjfd_number = PreferenceUtils.getString(context, Constants.SJFD_PHONE_NUMBER);

        //获取安全号码发送过来的指令
        //先判断接收到的短信号码是否是安全号码
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");

        //解析短信
        for (Object obj : pdus) {

            SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);

            //短信内容
            String body = sms.getMessageBody();

            //短信号码
            String address = sms.getOriginatingAddress();

            Log.i(TAG, "body:----> " + body + "----------address:" + address);

            //判断收到的短信号码是否和安全号码一致

            if (!sjfd_number.equals(address)) {
                //安全号码和短信号码不一致
                return;
            }
            if (body.equals("#*location*#")) {
                //开启GPS服务
                Intent service = new Intent(context, GPSService.class);
                context.startService(service);

            } else if (body.equals("#*alarm*#")) {
                Log.i(TAG, "执行报警声音。。。。。。");
                // 报警音乐
                MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
                player.setLooping(true);
                player.setVolume(1.0f, 1.0f);
                player.start();
                //中断短信（中断广播）
                abortBroadcast();

            } else if (body.equals("#*wipeddata*#")) {

               /*判断用户是否已经开启了设备管理员*/
                DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName whoN = new ComponentName(context, SjfdAdminReceicer.class);
                if (dpm.isAdminActive(whoN)) {
                    //用户已经开启激活功能，可以进行锁屏幕操作
                    //WIPE_EXTERNAL_STORAGE     擦除SD卡的数据（格式化SD）
                    // WIPE_RESET_PROTECTION_DATA   恢复出厂设置
                    //慎用
                   /* dpm.wipeData(DevicePolicyManager.WIPE_RESET_PROTECTION_DATA);*/
                    Log.d(TAG, "onReceive ,,,,,,,,,,,,执行恢复出厂设置2");
                    abortBroadcast();
                }
            } else if (body.equals("#*lockscreen*#")) {
                     /*判断用户是否已经开启了设备管理员*/
                DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

                ComponentName whoN = new ComponentName(context, SjfdAdminReceicer.class);

                Log.i(TAG, "sjfd_function 是否已经激活设备管理员："+dpm.isAdminActive(whoN));
                if (dpm.isAdminActive(whoN)) {
                    //用户已经开启激活功能，可以进行锁屏幕操作
                    dpm.lockNow();
                    dpm.resetPassword("123456789", 0);    //设置PIN的密码为123
                }
                abortBroadcast();
            }

        }
    }

}
