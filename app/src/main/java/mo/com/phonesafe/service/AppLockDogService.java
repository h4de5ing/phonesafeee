package mo.com.phonesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mo.com.phonesafe.activity.LockScreenActivity;
import mo.com.phonesafe.dao.AppLockDao;

/**
 * Created by Gh0st on 2015/9/13 22:43
 *
 * 电子狗服务
 */


public class AppLockDogService extends Service {
    private static final String TAG = "AppLockDogService";
    public static final String PCK_NAME = "package_name";
    private List<String> mData;
    private ActivityManager mAm;
    private boolean isRunning;

    // 需要放行的包名集合
    private List<String> mFreeDatas = new ArrayList<String>();
    private AppLockDao dao;
    private DBContentObserver observer;
    private ContentResolver resolver;
    private FreedBroad mFreedBroad;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate 电子狗开启");

        //开启线程实时监听用户打开的程序
        
        //从数据库中加载数据，放到内存集合中,数据库发生改变的时候通知我们重新加载数据库中的数据
        dao = new AppLockDao(this);
        mData = dao.getAllLockPackName();

        //注册放行广播
        mFreedBroad = new FreedBroad();
        IntentFilter filter = new IntentFilter();
        filter.addAction("mo.com.phonesafe.free");
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mFreedBroad, filter);


        //注册contentobserver,接收数据库数据的改变通知
        resolver = getContentResolver();
        //两种情况：true content://applock/abc/xxx
        //false content://applock
        observer = new DBContentObserver(new Handler());
        Uri uri = Uri.parse("content://applock");
        resolver.registerContentObserver(uri, true, observer);
        //电子狗线程
        watckDog();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        Log.i(TAG, "onDestroy 电子狗关闭");

        /*取消数据库观察者*/
        resolver.unregisterContentObserver(observer);
        unregisterReceiver(mFreedBroad);
    }
    private void watckDog() {
        if (isRunning == true) {
            return;
        }
        isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    //获取任务管理器
                    mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

                   /*获取排在第一的Tast任务栈*/

                    ActivityManager.RunningTaskInfo info = mAm.getRunningTasks(1).get(0);

                    //目前正在显示的界面
                    ComponentName topActivity = info.topActivity;

                    //获取当前界面应对对应的包名
                    String packageName = topActivity.getPackageName();
                    String className = topActivity.getClassName();

                     /*判断当前界面是不是程序锁界面*/
                    if (className.equals(LockScreenActivity.class.getName())) {
                        //当前是自己程序锁界面，不进行上锁拦截
                        continue;
                    }

                    /*判断应用程序是否已经被临时放行*/
                    if (mFreeDatas.contains(packageName)) {
                        continue;
                    }

                    /*判断当前程序的包名是否在用户设置的上锁数据中*/
                    if (mData.contains(packageName)) {
                        //进行拦截，启动程序锁界面

                        Intent intent = new Intent(AppLockDogService.this, LockScreenActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(PCK_NAME, packageName);
                        startActivity(intent);
                    }
                }

            }
        }).start();
    }




    //内容观察者，观察数据库发生了改变，重新添加数据
    private class DBContentObserver extends ContentObserver {

        public DBContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            //重新加载数据
            mData = dao.getAllLockPackName();
        }
    }

    //注册放行广播
    private class FreedBroad extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                //屏幕关闭的时候，我们停止循环，节省资源  清理临时放行的应用
                isRunning = false;
                mFreeDatas.clear();
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                //屏幕点亮的时候，开启循环
                watckDog();
            } else {
                //获取广播中的数据
                String packageName = intent.getStringExtra(LockScreenActivity.FREED_PACKNAME);
                //将数据添加到放行数据中
                mFreeDatas.add(packageName);
            }
        }
    }
}
