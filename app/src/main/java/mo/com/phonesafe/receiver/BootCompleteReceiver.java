package mo.com.phonesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.tools.Constants;

/**
 * 作者：MoMxMo on 2015/9/1 22:07
 * 邮箱：xxxx@qq.com
 * <p/>
 * 开启启动的广播接收者，主要监听启动手机时
 * 手机的SIM卡是否发生了变更
 * <p/>
 * 如果发生了改变，我们将进行一系列的操作
 * 1.发送报警短信给安全号码
 * 2.GPS点位
 * 3.播放报警乐音
 * 4.远程锁屏
 */


public class BootCompleteReceiver extends BroadcastReceiver {

    private static final String TAG = "BootCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        //1.先判断用户是否开启防盗保护

        boolean isProtect = PreferenceUtils.getBoolean(context, Constants.SJFD_PROTECT);

        if (!isProtect) {
    //  没有开启防盗保护，不执行下面的操作

            return;
        }


        //检测手机的SIM卡是否已经变更

        //获取目前的手机SIM卡信息
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String now_SIM = tm.getSimSerialNumber();

        //获取绑定的SIM卡
        String sim = PreferenceUtils.getString(context, Constants.SJFD_SIM);


        //认为修改SIM 记得修改回来 TODO:
        if (sim.equals(now_SIM+"xxxx")) {
            //如果目前的SIM卡信息和绑定的SIM卡匹配，者不进行下面的操做
            return;
        }

        //手机已经被盗（原因是SIM不匹配）

        /*
        1.向安全号码发送短信
         */
        String sjfd_phone = PreferenceUtils.getString(context, Constants.SJFD_PHONE_NUMBER);

        SmsManager smsManager = SmsManager.getDefault();

        Log.i(TAG, "----------sjfd_phone:"+sjfd_phone);
        smsManager.sendTextMessage(
                sjfd_phone,    // 收件人
                null,    // 短信中心号码
                "yor phone was lose.....", // 内容
                null,
                null);


    }
}
