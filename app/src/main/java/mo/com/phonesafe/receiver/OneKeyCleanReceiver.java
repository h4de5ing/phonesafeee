package mo.com.phonesafe.receiver;

import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import mo.com.phonesafe.bean.ProcessBean;
import mo.com.phonesafe.business.ProcessProvider;

/**
 * 自定义一键清理进程广播
 * <p/>
 * Created by Gh0st on 2015/9/11 18:25
 */


public class OneKeyCleanReceiver extends BootCompleteReceiver {
    private static final String TAG = "OneKeyCleanReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i(TAG, "onReceive 一键清理");

        int count = 0;
        long startMemory = 0;
        long endMemory = 0;

        /*执行一键清理的操作*/
        List<ProcessBean> list = ProcessProvider.getProcessInfo(context);
        startMemory = ProcessProvider.getVaildMemomy(context);
        for (ProcessBean bean : list) {
            //不杀死自己的进程
            if (bean.packageName.equals(context.getPackageName())) {
                continue;
            }
            //清理进程
            ProcessProvider.cleanProcess(context, bean.packageName);
            count++;
        }

        endMemory = ProcessProvider.getVaildMemomy(context);

        if (count > 0) {
            Toast.makeText(context, "共清理了" + count + "个进程,"
                            + "节省了" + Formatter.formatFileSize(context, endMemory - startMemory) + "内存"
                    , Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "没有可以清理的进程", Toast.LENGTH_SHORT).show();
        }

    }
}