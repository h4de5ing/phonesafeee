package mo.com.phonesafe.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.VersionMessage;
import mo.com.phonesafe.tools.PackageInfoUtil;
import mo.com.phonesafe.tools.ShimmerFrameLayout;

public class SplashActivity extends Activity {

    private static final int LOADMAIN = 1;
    private static final int NEWVERSION = 2;
    private static final int ERROR = 3;
    private static final int SHOW_UPDATE_DIALOG = 4;
    private static final int SHOW_ERROR = 5;
    private static final int REQUEST_INSTALL_CODE = 6;
    private static final String TAG = "SplashActivity";
    private int mCurrentPreset = 1;    //默认的闪动方式
    private ShimmerFrameLayout mShimmerViewContainer;   //布局的闪动对象
    TextView tv_app_version;    //显示版本名称
    int mCurrentversionCode;    //版本号
    private long mDuration;    //动画执行的时间
    VersionMessage versionMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //界面View
        initView();
        //数据model,检测版本更新
        initData();
        //启动动画
        startAnimation();
        //事件
    }

    /**
     * 初始化界面
     */
    private void initView() {

        //布局文件
        setContentView(R.layout.activity_splash);
        //动态对象布局
        mShimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);

        tv_app_version = (TextView) findViewById(R.id.tv_app_version);

    }

    /**
     * 进入主界面
     */
    private void loadHome() {
        //等待一段时间进入
        //延时执行任务
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //主线程中执行

                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);

                //关闭自己
                finish();

            }
        }, 1500);


    }

    /**
     * 启动动画
     */
    private void startAnimation() {

        //设置闪动的方式的默认的2（以后可以切换模式）
        selectPreset(mCurrentPreset);
        //开启闪动效果
        mShimmerViewContainer.startShimmerAnimation();

    }

    /**
     * 初始化数据，检测版本更新
     */
    private void initData() {


        //获取当前app包的信息
        try {
            //版本名称
            String versionName = PackageInfoUtil.getVersionName(SplashActivity.this);

            //显示版本名称
            tv_app_version.setText(versionName);

            //当前版本号
            mCurrentversionCode = PackageInfoUtil.getVersionCode(SplashActivity.this);

            //开启线程去访问网络，获取版本信息
            new CheckVersionThread().start();


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 定义一个线程用于网络检测版本更新的
     */
    private class CheckVersionThread extends Thread {

        @Override
        public void run() {
            checkVersion();
        }
    }

    private Handler mHandler = new Handler() {

        //检测网络更新的:TODO
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_UPDATE_DIALOG:    //显示更新页面
                    showUpdateDialog();
                    break;
                case SHOW_ERROR:    //显示错误的代码Toast
                    Toast.makeText(SplashActivity.this, "" + msg.obj, Toast.LENGTH_LONG).show();
                    loadHome();
                    break;
            }
        }
    };

    /**
     * 显示提示用户进行更新   TODO
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);       //设置Dialog外部失去点击
        builder.setTitle("版本更新提醒");// 设置标题
        builder.setMessage(versionMessage.getDesc());// 服务器获取的

        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载最新版信息，下载进度条
                downloadNewVersion();

            }
        });
        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //进入主页面
                loadHome();
            }
        });

        builder.show();
    }

    /**
     * 下载新的版本
     */
    private void downloadNewVersion() {
        //创建现在的进度条
        final ProgressDialog progressDialog = new ProgressDialog(this);
        //设置进度条水平显示
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progressDialog.setCancelable(false);
        //显示
        progressDialog.show();

        //去下载APK
        HttpUtils httpUtils = new HttpUtils();

        //网络下载的路径
        String downloadUrl = versionMessage.getDownloadurl();

        //设置文件存储的位置

        String fileName = System.currentTimeMillis() + ".apk";
        final File file = new File(Environment.getExternalStorageDirectory(), fileName);

        httpUtils.download(downloadUrl, file.getAbsolutePath(), new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                //下载成功

                progressDialog.dismiss();
                //召唤系统，使用意图进行安装，并设置安装结果的回调
                /**
                 * 系统中的隐式意图（参看源码）
                 *
                 * <activity android:name=".PackageInstallerActivity"
                 android:configChanges="orientation|keyboardHidden"
                 android:theme="@style/TallTitleBarTheme">
                 <intent-filter>
                 <action android:name="android.intent.action.VIEW" />
                 <category android:name="android.intent.category.DEFAULT" />
                 <data android:scheme="content" />
                 <data android:scheme="file" />
                 <data android:mimeType="application/vnd.android.package-archive" />
                 </intent-filter>
                 </activity>
                */
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");

                //启动的系统安装的界面
                startActivityForResult(intent,REQUEST_INSTALL_CODE);

            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                //下载进入
                progressDialog.setMax((int) total); //文件的总大小
                progressDialog.setProgress((int) current);  //已经下载的大小

            }

            @Override
            public void onFailure(HttpException e, String s) {
                //下载失败
                progressDialog.dismiss();

                //进入主页面
                loadHome();

            }
        });
    }

    /**
     * 安装软件的结果回调函数
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        requestCode调用startActivityForResule参数
        resultleCode：对方的activity提供的
        data:对方放回的数据
         */
        if(requestCode == REQUEST_INSTALL_CODE){
            switch (resultCode){
                case Activity.RESULT_CANCELED:
                    //用户取消动作
                    Log.i(TAG, "用户取消动作");
                    //进入主界面
                    loadHome();
                    break;
                case Activity.RESULT_OK:
                    //用户确定操作
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 检测版本号更新
     */
    private void checkVersion() {
        Message msg = null;

        //从服务器获取版本信息
        String uri = getResources().getString(R.string.versionurl);

        //获取连接客户端
        AndroidHttpClient client = AndroidHttpClient.newInstance("mo", SplashActivity.this);

        //设置连接超时时间
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 5 * 1000);

        //设置响应的超时时间
        HttpConnectionParams.setSoTimeout(client.getParams(), 5 * 1000);

        //创建请求
        HttpGet get = new HttpGet(uri);

        //执行连接服务器，获取响应对象
        HttpResponse response = null;
        try {
            response = client.execute(get);

            if (response.getStatusLine().getStatusCode() == 200) {
                //访问成功
                HttpEntity entity = response.getEntity();

                //获取服务器返回的数据
                String json = EntityUtils.toString(entity,"UTF-8");


                //解析json格式数据
                versionMessage = parseJson(json);

                Log.i(TAG,"versionMessage:"+versionMessage.toString());


                //本地版本号
                int localCode = PackageInfoUtil.getVersionCode(this);

                if (localCode < versionMessage.getVersionCode()) {
                    //需要进行更新，提示用户是否需要更新

                    msg = mHandler.obtainMessage();
                    msg.what = SHOW_UPDATE_DIALOG;
                    msg.sendToTarget();
                } else {
                    //不需要更新，跳到主页面
                    msg = mHandler.obtainMessage();
                    msg.what = SHOW_ERROR;
                    msg.sendToTarget();
                }
            } else {
                //访问网络失败,进入主界面
                loadHome();
            }
        } catch (IOException e) {
            msg = mHandler.obtainMessage();
            msg.what = SHOW_ERROR;
            msg.obj = "error:1000110";
            msg.sendToTarget();
            e.printStackTrace();
        } catch (JSONException e) {
            msg = mHandler.obtainMessage();
            msg.what = SHOW_ERROR;
            msg.obj = "error:1000120";
            msg.sendToTarget();
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            msg = mHandler.obtainMessage();
            msg.what = SHOW_ERROR;
            msg.obj = "error:1000130";
            msg.sendToTarget();
            e.printStackTrace();
        } finally {

            //记得一定要关闭网络，否则会出现异常
            client.close();
        }
    }


    /**
     * 解析json数据
     *
     * @param jsondata
     * @throws JSONException
     */
    private VersionMessage parseJson(String jsondata) throws JSONException {
        JSONObject json = new JSONObject(jsondata);
        VersionMessage versionMessage = new VersionMessage();
        versionMessage.setVersionCode(json.getInt("versioncode"));
        versionMessage.setDesc(json.getString("desc"));
        versionMessage.setDownloadurl(json.getString("downloadurl"));
        return versionMessage;
    }


    @Override
    public void finish() {
        super.finish();
        //停止闪动效果
        mShimmerViewContainer.stopShimmerAnimation();
    }

    /**
     * 旋转闪动的方式
     *
     * @param preset
     */
    private void selectPreset(int preset) {
        // Save the state of the animation
        boolean isPlaying = mShimmerViewContainer.isAnimationStarted();

        // Reset all parameters of the shimmer animation
        mShimmerViewContainer.useDefaults();
        switch (preset) {
            default:
            case 0:
                // Default
                break;
            case 1:
                // Slow and reverse
                mShimmerViewContainer.setDuration(5000);
                mShimmerViewContainer.setRepeatMode(ObjectAnimator.REVERSE);
                break;
            case 2:
                // Thin, straight and transparent
                mShimmerViewContainer.setBaseAlpha(0.1f);
                mShimmerViewContainer.setDropoff(0.1f);
                mShimmerViewContainer.setTilt(0);
                break;
            case 3:
                // Sweep angle 90
                mShimmerViewContainer.setAngle(ShimmerFrameLayout.MaskAngle.CW_90);
                break;
            case 4:
                // Spotlight
                mShimmerViewContainer.setBaseAlpha(0);
                mShimmerViewContainer.setDuration(2000);
                mShimmerViewContainer.setDropoff(0.1f);
                mShimmerViewContainer.setIntensity(0.35f);
                mShimmerViewContainer.setMaskShape(ShimmerFrameLayout.MaskShape.RADIAL);
                break;
        }

        // Setting a value on the shimmer layout stops the animation. Restart it, if necessary.
        if (isPlaying) {
            mShimmerViewContainer.startShimmerAnimation();
        }
    }

}
