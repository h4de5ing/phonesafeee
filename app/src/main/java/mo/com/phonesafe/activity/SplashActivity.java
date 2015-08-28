package mo.com.phonesafe.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import mo.com.phonesafe.R;
import mo.com.phonesafe.activity.bean.VersionMessage;
import mo.com.phonesafe.activity.tools.ShimmerFrameLayout;

public class SplashActivity extends Activity {

    private static final int LOADMAIN = 1;
    private static final int NEWVERSION = 2;
    private static final int ERROR = 3;
    private int mCurrentPreset = 1;    //默认的闪动方式
    private ShimmerFrameLayout mShimmerViewContainer;   //布局的闪动对象
    TextView tv_app_version;    //显示版本名称
    int mCurrentversionCode;    //版本号
    private long mDuration ;    //动画执行的时间
    ImageView iv_mo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //界面View
        initView();
        //数据model
        initData();
        //启动动画
        startAnimation(1500);
        //事件

        mShimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
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
        iv_mo = (ImageView) findViewById(R.id.iv_mo);

    }

    /**
     * 进入主界面
     */
    private void startMain() {
        Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
        startActivity(intent);

        //关闭自己
        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * 启动动画
     */
    private void startAnimation(int animationTime) {
        mDuration = animationTime;

        //设置闪动的方式的默认的2（以后可以切换模式）
        selectPreset(mCurrentPreset);
        //开启闪动效果
        mShimmerViewContainer.startShimmerAnimation();

   /*     ObjectAnimator oa = ObjectAnimator.ofFloat(iv_mo, "translationY",new float[]{-1250,-450,-90,0});
        oa.setDuration(animationTime);
//        oa.setRepeatCount(1);
        oa.setRepeatMode(ObjectAnimator.REVERSE);
        oa.start();

        oa.AnimatorListener();*/



    }

    /**
     * 初始化数据
     */
    private void initData() {

        //版本名显示 版本号 检测判断是否有新的版本
         PackageManager pm = getPackageManager();

        //获取当前app包的信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);

            //版本名称
             String versionName = packageInfo.versionName;

            //显示版本名称
            tv_app_version.setText(versionName);

            //当前版本号
            mCurrentversionCode = packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 定义一个线程用于网络检测版本更新的
     */
    private class CheckVersionThread extends Thread{
        @Override
        public void run() {
            checkVersion();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    /**
     * 检测版本号更新
     */
    private void checkVersion() {
        Message msg = null;
        long startTime = System.currentTimeMillis();//起时间

        Message message = null;
        //从服务器获取版本信息
        URL url = null;
        try {
            url = new URL(getResources().getString(R.string.versionurl));

            //打来连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);//设置超时时间
            int codestate = conn.getResponseCode();

            if(codestate == 200){
                //成功把io流获取，并转化成数据
                String jsondata  =readStream(conn);

                //解析json数据
                VersionMessage versionMessage = parseJson(jsondata);

                //判断版本是否一致
                if(mCurrentversionCode == versionMessage.getVersionCode()){
                    //版本一致
                    message = mHandler.obtainMessage(LOADMAIN);
                }else{
                    //版本不一致
                    message =  mHandler.obtainMessage(NEWVERSION);
                }


            }
        } catch (MalformedURLException e) {
            //Error
            message = mHandler.obtainMessage(ERROR);
            message.arg1 = 404; //url连接错误
            e.printStackTrace();
        } catch (IOException e) {
            //Error
            message = mHandler.obtainMessage(ERROR);
            message.arg1 = 303; //文件找不到
            e.printStackTrace();
        } catch (JSONException e) {
            //Error
            message = mHandler.obtainMessage(ERROR);
            message.arg1 = 404; //json格式错误
            e.printStackTrace();
        }finally {
            //统一发送消息
            long endTime = System.currentTimeMillis();


            if(endTime - startTime < mDuration){

                //代码执行的时间小于动画播放的时间
                SystemClock.sleep(mDuration - (endTime - startTime));
            }

            //发送消息
            mHandler.sendMessage(message);

        }


    }

    /**
     * 解析json数据
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




    /**
     * 获取流数据
     * @param conn
     * @return
     * @throws IOException
     */
    private String readStream(HttpURLConnection conn) throws IOException {
        conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while((line = br.readLine()) != null){
            sb.append(line);
        }
      return  sb.toString();
    }


    @Override
    public void finish() {
        super.finish();
        //停止闪动效果
        mShimmerViewContainer.stopShimmerAnimation();
    }

    /**
     * 旋转闪动的方式
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
