package mo.com.phonesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

import mo.com.phonesafe.R;
import mo.com.phonesafe.preference.PreferenceUtils;
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

//        1.判断是否已经开启防盗保护
        boolean isProtect = PreferenceUtils.getBoolean(context, Constants.SJFD_PROTECT);

        if (!isProtect) {
            //没有开启，不进行下面的操作
            return;
        }


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
                //获取GPS定位 TODO
            } else if (body.equals("#*alarm*#")) {
                //播放报警音乐 TODO:

                Log.i(TAG, "执行报警声音。。。。。。");


                // 报警音乐
                Log.d(TAG, "报警音乐 ");

                MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);

                player.setLooping(true);
                player.setVolume(1.0f, 1.0f);
                player.start();

                //中断短信（中断广播）
                abortBroadcast();

               /* SoundPool soundPool;
                int id;
                //定义声音池
                //第一个参数是：池子里面可以放多少个音频文件
                //第二个参数是：音频的流格式，一般是STREAM_MUSIC
                //第三个参数是：音频的速率，默认是0
                soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
                //加载一个音频文件到池子里面
                id = soundPool.load(context, R.raw.alarm, 1);
                //指定播放的声音的id.左声道右声道，优先级，是否循环，播放速率
                soundPool.play(id, 1, 1, 0, 0, 1.0f);*/

            } else if (body.equals("#*wipeddata*#")) {
                //远程销毁数据 TODO:

            } else if (body.equals("#*lockscreen*#")) {
                //远程锁频 TODO:

            }


        }

    }
}
