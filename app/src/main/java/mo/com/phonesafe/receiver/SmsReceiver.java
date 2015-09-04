package mo.com.phonesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.telephony.SmsMessage;
import android.util.Log;

import mo.com.phonesafe.R;
import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.service.GPSService;
import mo.com.phonesafe.tools.Constants;

/**
 * 作者：MoMxMo on 2015/9/1 22:26
 * 邮箱：xxxx@qq.com
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

     /*   //2.判断用户是否开启短信拦截功能
        boolean intercept = PreferenceUtils.getBoolean(context, Constants.AUTO_INTERCEPT);

        Log.i(TAG, " 拦截功能是否开启。。。。。。。。。。。。"+intercept);
        if (intercept) {

            //获取当前短信的号码
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");

            //判断当前号码是否是黑名单中的（数据库中获取判断，并判断拦截的类型是什么）
            BlackDao dao = new BlackDao(context);

            //解析短信
            for (Object obj : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                String address = sms.getOriginatingAddress();
                String body = sms.getMessageBody();
                Log.i(TAG, address+" 发来短信。。。。。。。。。。。。");
                BlackBean query = dao.query(address);
                if (query != null) {
                    //短信号码在黑名单中
                    //判断黑名单中拦截的类型是否包含短信拦截
                    if (query.type == BlackBean.TYPE_SMS || query.type == BlackBean.TYPE_ALL) {
                        Log.i(TAG, query.number+" 发来"+query.type);
                        //拦截短信

                        //删除短信
                        deleteSMS(context, body,address);

                        abortBroadcast();
                    }
                }
            }

        }*/
    }

    /**
     * 删除短信
     * @param context
     * @param smscontent
     */
    private void deleteSMS(Context context, String smscontent,String smsAddress) {
        Uri uri = Uri.parse("content://sms/inbox");
        ContentResolver resolver = context.getContentResolver();
        Log.d(TAG, "deleteSMS .................");
        //查询短信
        Cursor cursor = resolver.query(uri, null, "address=?", new String[]{smsAddress},null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String address = cursor.getString(cursor.getColumnIndex("address"));//确认发件人
                String body = cursor.getString(cursor.getColumnIndex("body"));//获取短信内容

                Log.d(TAG, "deleteSMS address:" + address + "   body:" + body);

                //如果查询到的短信与拦截的短信内容一致   执行删除操作
                if (body.equals(smscontent)) {
                    Uri uri_delete = Uri.parse("content://sms");
                    int delete = resolver.delete(uri_delete, "body=?", new String[]{body});
                    if (delete > 0) {
                        Log.i(TAG, "deleteSMS 删除短信成功");
                    }
                }
            }
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

            //先屏蔽 TODO:
          /*  if (!sjfd_number.equals(address)) {
                //安全号码和短信号码不一致
                return;
            }*/


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
                Log.d(TAG, "onReceive ,,,,,,,,,,,,执行恢复出厂设置1");
                if (dpm.isAdminActive(whoN)) {
                    //用户已经开启激活功能，可以进行锁屏幕操作
                    //WIPE_EXTERNAL_STORAGE     擦除SD卡的数据（格式化SD）
                    // WIPE_RESET_PROTECTION_DATA   恢复出厂设置

                    //慎用
                    // dpm.wipeData(DevicePolicyManager.WIPE_RESET_PROTECTION_DATA);

                    Log.d(TAG, "onReceive ,,,,,,,,,,,,执行恢复出厂设置2");
                }

            } else if (body.equals("#*lockscreen*#")) {
                     /*判断用户是否已经开启了设备管理员*/
                DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

                ComponentName whoN = new ComponentName(context, SjfdAdminReceicer.class);
                if (dpm.isAdminActive(whoN)) {
                    //用户已经开启激活功能，可以进行锁屏幕操作
                    dpm.lockNow();
                }

            }

        }
    }

}
