package mo.com.phonesafe;

import android.app.Application;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.log.ACRALog;

import mo.com.phonesafe.tools.Logger;

/**
 * 作者：MoMxMo on 2015/9/16 19:07
 * 邮箱：xxxx@qq.com
 * <p/>
 * 设置日志的上传到服务器上（使用到开源框架）
 */

@ReportsCrashes(formUri = "http://192.168.23.1/CrashWeb/crash",
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text, // optional,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info, // optional.
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When
        resDialogEmailPrompt = R.string.crash_user_email_label, // optional. When
        resDialogOkToast = R.string.crash_dialog_ok_toast)
public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        // 应用打开时的回调
     /*   ACRA.init(this);
        ACRA.setLog(new ACRALOG());*/


   /*      //捕获没有捕获的异常
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Logger.i(TAG,"捕获没有捕获的异常");
                ex.printStackTrace();
               //将错误的日志上传到服务器上
                //获取日志
                File file = new File("xxx.error");
                PrintWriter printWriter = null;
                try {
                     printWriter = new PrintWriter(file);
                    ex.printStackTrace(printWriter);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // 上传到服务器中
                //展示友好的提示页面
            }
        });*/
    }

    private class ACRALOG implements ACRALog {

        @Override
        public int d(String arg0, String arg1) {
            Logger.d(arg0, arg1);
            return 0;
        }

        @Override
        public int d(String arg0, String arg1, Throwable arg2) {
            Logger.d(arg0, arg1);
            return 0;
        }

        @Override
        public int e(String arg0, String arg1) {
            Logger.e(arg0, arg1);
            return 0;
        }

        @Override
        public int e(String arg0, String arg1, Throwable arg2) {
            Logger.e(arg0, arg1);
            return 0;
        }

        @Override
        public String getStackTraceString(Throwable arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int i(String arg0, String arg1) {
            Logger.i(arg0, arg1);
            return 0;
        }

        @Override
        public int i(String arg0, String arg1, Throwable arg2) {
            Logger.i(arg0, arg1);
            return 0;
        }

        @Override
        public int v(String arg0, String arg1) {
            Logger.v(arg0, arg1);
            return 0;
        }

        @Override
        public int v(String arg0, String arg1, Throwable arg2) {
            Logger.v(arg0, arg1);
            return 0;
        }

        @Override
        public int w(String arg0, String arg1) {
            Logger.w(arg0, arg1);
            return 0;
        }

        @Override
        public int w(String arg0, Throwable arg1) {
//			Logger.w(arg0, arg1);
            return 0;
        }

        @Override
        public int w(String arg0, String arg1, Throwable arg2) {
            Logger.w(arg0, arg1);
            return 0;
        }

        ;
    }
}
