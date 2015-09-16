package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.CacheBean;

/**
 * 作者：MoMxMo on 2015/9/14 12:30
 * 邮箱：xxxx@qq.com
 */


public class CacheCleanActivity extends Activity {

    private static final String TAG = "CacheCleanActivity";
    private ImageView civ_line;
    private ImageView iv_icon;
    private ProgressBar pb_progress;
    private TextView tv_title;
    private TextView tv_cachesize;
    private ScanAsyncTask mScanAsyncTask;
    private PackageManager mPm;
    private ListView lv_clean;
    private List<CacheBean> mListData;
    private CleanCacheAdapter mAdapter;
    private ImageView iv_line;
    private RelativeLayout rl_clean_pre;
    private RelativeLayout rl_scan_result;
    private RelativeLayout rl_clean_pre1;
    private Button preed_clean;
    private TextView tv_show_scan_result;
    private Button btn_onekey_clean;
    private long cacheCount;
    private ClearCacheObserver cacheObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clean);
           /*获取包管理者*/
        mPm = getPackageManager();
        cacheObserver = new ClearCacheObserver();

        //初始化View
        initView();

        //初始化事件
        initEvent();

        //初始化数据
        initData();

    }


    private void initView() {
        tv_cachesize = (TextView) findViewById(R.id.cc_tv_cachesize);
        tv_title = (TextView) findViewById(R.id.cc_tv_title);
        pb_progress = (ProgressBar) findViewById(R.id.cc_pb_progress);
        iv_icon = (ImageView) findViewById(R.id.cc_iv_icon);
        civ_line = (ImageView) findViewById(R.id.cc_iv_line);
        lv_clean = (ListView) findViewById(R.id.lv_clean);
        iv_line = (ImageView) findViewById(R.id.cc_iv_line);

        rl_clean_pre1 = (RelativeLayout) findViewById(R.id.rl_clean_pre);
        rl_scan_result = (RelativeLayout) findViewById(R.id.rl_scan_result);
        preed_clean = (Button) findViewById(R.id.btn_preed_clean);
        tv_show_scan_result = (TextView) findViewById(R.id.tv_show_scan_result);
        btn_onekey_clean = (Button) findViewById(R.id.btn_onekey_clean);

    }

    private void initData() {
        mAdapter = new CleanCacheAdapter();
        lv_clean.setAdapter(mAdapter);
        //启动线程执行扫面操作
        scanStart();
    }

    private void scanStart() {
        mScanAsyncTask = new ScanAsyncTask();
        /*启动线程*/
        mScanAsyncTask.execute();
    }

    private void initEvent() {

        //一键清理
        btn_onekey_clean.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*   // @SystemApi
    public abstract void freeStorageAndNotify(long freeStorageSize, IPackageDataObserver observer);*/

                if (cacheCount < 0) {
                    Toast.makeText(CacheCleanActivity.this, "没有可以清理的缓存", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cacheObserver == null) {
                    cacheObserver = new ClearCacheObserver();
                }
                Method method = null;
                try {
                    method = mPm.getClass().getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
                    method.invoke(mPm, Long.MAX_VALUE, cacheObserver);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                //重新开始扫描
                scanStart();
            }


        });

        //快速扫描
        preed_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击快速扫描
                //启动线程执行扫面操作
                scanStart();
            }
        });
    }

    private class ScanAsyncTask extends AsyncTask<Void, CacheBean, Void> {
        private int maxCount;
        private int process;


        @Override
        protected void onPreExecute() {
            maxCount = 0;
            process = 0;
            cacheCount = 0;
            if (mListData == null) {
                mListData = new ArrayList<CacheBean>();
            } else {
                mListData.clear();
            }

            /*显示和隐藏相关的界面*/
            rl_clean_pre1.setVisibility(View.VISIBLE);
            rl_scan_result.setVisibility(View.GONE);

            //开启扫面动画
            TranslateAnimation ta = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0,
                    Animation.RELATIVE_TO_PARENT, 0,
                    Animation.RELATIVE_TO_PARENT, 0,
                    Animation.RELATIVE_TO_PARENT, 1
            );
            ta.setDuration(800);
            ta.setRepeatCount(Animation.INFINITE);
            ta.setRepeatMode(Animation.REVERSE);
            iv_line.startAnimation(ta);
        }

        @Override
        protected Void doInBackground(Void... params) {
            final List<PackageInfo> listInfo = mPm.getInstalledPackages(0);
            Log.i(TAG, "initData :" + listInfo.size());
            maxCount = listInfo.size();
            for (PackageInfo info : listInfo) {
                String packageName = info.packageName;
                process++;
                // 获得具体的包的缓存信息
                // mPm.getPackageSizeInfo(packageName, mStatsObserver);

                //使用反射技术获取包管理者中隐藏的方法
                try {
                    Method method = mPm.getClass().getMethod(
                            "getPackageSizeInfo", String.class,
                            IPackageStatsObserver.class);
                    method.invoke(mPm, packageName, mStatsObserver);

                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        //暴露这个方法让用户可见，并通过外部方式去传值
        public void updateProgress(CacheBean bean) {
            publishProgress(bean);
        }

        //更新显示进入和大小
        @Override
        protected void onProgressUpdate(CacheBean... values) {
            CacheBean bean = values[0];
            pb_progress.setMax(maxCount);
            pb_progress.setProgress(process);
            tv_title.setText(bean.name);
            iv_icon.setImageDrawable(bean.icon);
            tv_cachesize.setText("占用有" + Formatter.formatFileSize(CacheCleanActivity.this, bean.cachesize) + "缓存");
            cacheCount = cacheCount + bean.cachesize;
            mListData.add(bean);

            lv_clean.smoothScrollToPosition(mAdapter.getCount());
            mAdapter.notifyDataSetChanged();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            tv_show_scan_result.setText("总有" + Formatter.formatFileSize(CacheCleanActivity.this, cacheCount) + "缓存");
            /*显示和隐藏相关的界面*/
            rl_clean_pre1.setVisibility(View.GONE);
            rl_scan_result.setVisibility(View.VISIBLE);
            lv_clean.smoothScrollToPosition(0);
            mAdapter.notifyDataSetChanged();
        }
    }

    //*远程的缓存服务*//*
    private final IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
        public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {

            long cacheSize = stats.cacheSize;
            String packageName = stats.packageName;
            ApplicationInfo applicationInfo = null;
            try {
                CacheBean bean = new CacheBean();
                applicationInfo = mPm.getApplicationInfo(packageName, 0);
                Drawable icon = applicationInfo.loadIcon(mPm);
                String appName = applicationInfo.loadLabel(mPm).toString();
                bean.icon = icon;
                bean.name = appName;
                bean.cachesize = cacheSize;
                bean.packageName = packageName;

                Log.i(TAG, "onGetStatsCompleted 远程的缓存服务" + bean.toString());
                /*将获封装好的bean对象传送给异步线程更新显示*/
                mScanAsyncTask.updateProgress(bean);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
    };

    class CleanCacheAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mListData != null) {
                return mListData.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mListData != null) {
                return mListData.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(CacheCleanActivity.this, R.layout.item_cache_clean, null);
                holder = new ViewHolder();
                convertView.setTag(holder);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.iv_clean_item_icon);
                holder.mTitle = (TextView) convertView.findViewById(R.id.tv_item_title);
                holder.mCacheSize = (TextView) convertView.findViewById(R.id.tv_item_cachesize);
                holder.mClean = (ImageView) convertView.findViewById(R.id.iv_item_clean);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final CacheBean bean = (CacheBean) getItem(position);
            holder.mIcon.setImageDrawable(bean.icon);
            holder.mTitle.setText(bean.name);
            holder.mCacheSize.setText("缓存" + Formatter.formatFileSize(CacheCleanActivity.this, bean.cachesize));

            if (bean.cachesize > 0) {
                holder.mCacheSize.setTextColor(getResources().getColor(R.color.normalColor));
                holder.mClean.setVisibility(View.VISIBLE);

                holder.mClean.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //跳转到清理页面
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + bean.packageName));
                        startActivity(intent);
                    }
                });
            } else {
                holder.mCacheSize.setTextColor(getResources().getColor(R.color.normalblue_green));
                holder.mClean.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView mIcon;
        TextView mTitle;
        TextView mCacheSize;
        ImageView mClean;

    }
    private class ClearCacheObserver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            Log.i(TAG, "清除了："+packageName+"  缓存成功");
        }
    }

}
