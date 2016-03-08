package mo.com.phonesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import mo.com.phonesafe.R;
import mo.com.phonesafe.business.ProcessProvider;
import mo.com.phonesafe.receiver.ProcessWidgetProvider;

/**
 * Created by Gh0st on 2015/9/11 18:46
 */


public class ProcessService extends Service {
    private static final String TAG = "ProcessService";

    private boolean isrunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate 开启一键清理服务");

        //服务开启
        isrunning = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isrunning) {
                    AppWidgetManager instance = AppWidgetManager.getInstance(ProcessService.this);
                    RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);

                    //使用广播的方式监听一键器清理
                    Intent intent = new Intent();
                    intent.setAction("mo.com.phonesafe.onekeyclean");
                    //sendBroadcast(intent);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    //设置点击一键清理的广播事件
                    views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

                     /*设置进程数*/
                    int processRunningCount = ProcessProvider.getProcessRunningCount(ProcessService.this);
                    views.setTextViewText(R.id.process_count, processRunningCount + "个进程正在运行");

                    //设置内存的数量
                    long vaildMemomy = ProcessProvider.getVaildMemomy(ProcessService.this);
                    views.setTextViewText(R.id.process_memory, "可用内存:" + Formatter.formatFileSize(ProcessService.this, vaildMemomy));

                    ComponentName component = new ComponentName(ProcessService.this, ProcessWidgetProvider.class);
                    instance.updateAppWidget(component, views);

                    /*睡眠*/
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();


    }

    @Override
    public void onDestroy() {

        Log.i(TAG, "onDestroy 关闭一键清理服务");


        /*如果服务关闭，那么我们停止上面的线程*/
        isrunning = false;
        super.onDestroy();
    }
}
