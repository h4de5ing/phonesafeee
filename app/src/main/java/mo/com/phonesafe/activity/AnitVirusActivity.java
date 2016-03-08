package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.VirusBean;
import mo.com.phonesafe.dao.AnitVirusDao;
import mo.com.phonesafe.tools.MD5Utils;

/**
 * Created by Gh0st  on 2015/9/15 14:34
 * 手机杀毒
 */

public class AnitVirusActivity extends Activity {

    private ListView lv_virus;
    private List<VirusBean> mlistData;
    private PackageManager mPm;
    private VirusAdapter mAdapter;
    private ArcProgress mProgress;
    private TextView mPackeNamge;
    private LinearLayout ll_scan_result;
    private LinearLayout ll_scan;
    private Button btn_rescan;
    private TextView tv_scan_result;
    private PackageRemovedReceiver packageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anit_virus);
        mPm = getPackageManager();
        initView();
        initData();
        initEvent();

        /*注册包删除的广播*/
        packageReceiver = new PackageRemovedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(packageReceiver, filter);


    }

    private class PackageRemovedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            /*重新开始扫描*/
            startScan();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(packageReceiver);
    }

    private void initEvent() {
        //监听listView的活动状态，如果在滑动，重新扫描按钮不可以点击

        /*重新扫描的点击事件*/
        btn_rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*开始扫描*/
                startScan();
            }
        });
    }

    private void startScan() {
          /*使用线程加载数据*/
        new AsyncTask<Void, VirusBean, Boolean>() {
            int count;
            int progress;
            int anitvirusCount;

            /*执行耗时操作准备（主线程中）*/

            @Override
            protected void onPreExecute() {
                count = 0;
                progress = 0;
                //确保数据不为空
                if (mPm == null) {
                    mPm = getPackageManager();
                }
                if (mlistData == null) {
                    mlistData = new ArrayList<VirusBean>();
                } else {
                /*清空前一次的记录*/
                    mlistData.clear();
                    mAdapter.notifyDataSetChanged();
                }
                /*对应UI的显示和隐藏*/
                ll_scan.setVisibility(View.VISIBLE);
                ll_scan_result.setVisibility(View.GONE);

            }

            /*执行耗时操作（子线程中）*/
            @Override
            protected Boolean doInBackground(Void... params) {
                 /*获取已经安装的程序的相关信息*/
                List<PackageInfo> installedPackages = mPm.getInstalledPackages(0);
                count = installedPackages.size();

                for (PackageInfo info : installedPackages) {
                    VirusBean bean = new VirusBean();

                    /*判断程序的md5*/
                    String sourceDir = info.applicationInfo.sourceDir;
                    File file = new File(sourceDir);
                    FileInputStream in = null;
                    String md5 = null;
                    try {
                        in = new FileInputStream(file);
                        /*使用流数据进行MD5加密*/
                        md5 = MD5Utils.encode(in);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    bean.isSafe = AnitVirusDao.isVirus(AnitVirusActivity.this, md5);

                    String packageName = info.packageName;
                    ApplicationInfo applicationInfo = info.applicationInfo;
                    Drawable drawable = applicationInfo.loadIcon(mPm);
                    String appName = applicationInfo.loadLabel(mPm).toString();
                    bean.icon = drawable;
                    bean.name = appName;

                    bean.packageName = packageName;
                    progress++;
                    /*主线程UI数据的更新*/
                    publishProgress(bean);
                }

                return null;
            }

            //实时更新UI数据
            @Override
            protected void onProgressUpdate(VirusBean... values) {
                super.onProgressUpdate(values);
                VirusBean bean = values[0];

                /*更新进度和包名*/
                mPackeNamge.setText(bean.packageName);
                int percent = (int) (progress * 100f / count + 0.5f);
                mProgress.setMax(100);
                mProgress.setProgress(percent);
                lv_virus.smoothScrollToPosition(mAdapter.getCount());

                if (bean.isSafe) {
                    mlistData.add(0, bean);
                    anitvirusCount++;
                } else {
                    mlistData.add(bean);
                }

               /*通知Apater数据发生改变*/
                mAdapter.notifyDataSetChanged();
            }

            //结束耗时操作之后（主线程中）
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (anitvirusCount > 0) {
                    tv_scan_result.setTextColor(getResources().getColor(R.color.normalColor));
                    tv_scan_result.setText(getString(R.string.viruscount, anitvirusCount));
                }

                /*listView回到第一个位置*/
                lv_virus.smoothScrollToPosition(0);

                /*对应UI的显示和隐藏*/
                ll_scan.setVisibility(View.GONE);
                ll_scan_result.setVisibility(View.VISIBLE);

            }
        }.execute();
    }

    private void initView() {
        lv_virus = (ListView) findViewById(R.id.lv_anit_virus);
        mProgress = (ArcProgress) findViewById(R.id.aa_arc_progress);
        mPackeNamge = (TextView) findViewById(R.id.tv_virus_packname);
        ll_scan = (LinearLayout) findViewById(R.id.ll_scan);
        ll_scan_result = (LinearLayout) findViewById(R.id.ll_scan_result);
        btn_rescan = (Button) findViewById(R.id.btn_rescan);
        tv_scan_result = (TextView) findViewById(R.id.tv_scan_result);
    }

    private void initData() {
        mAdapter = new VirusAdapter();
        /*添加适配器*/
        lv_virus.setAdapter(mAdapter);

        //开始扫描
        startScan();
    }

    private class VirusAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mlistData != null) {
                return mlistData.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mlistData != null) {
                return mlistData.get(position);
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
                convertView = View.inflate(AnitVirusActivity.this, R.layout.item_anit_virus, null);
                holder = new ViewHolder();
                holder.mIcon = (ImageView) convertView.findViewById(R.id.iv_virus_icon);
                holder.mClean = (ImageView) convertView.findViewById(R.id.iv_virus_clean);
                holder.mTitle = (TextView) convertView.findViewById(R.id.tv_virus_title);
                holder.mIsSafe = (TextView) convertView.findViewById(R.id.tv_virus_safe);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final VirusBean bean = (VirusBean) getItem(position);
            holder.mIcon.setImageDrawable(bean.icon);
            holder.mTitle.setText(bean.name);
            if (bean.isSafe) {
                holder.mIsSafe.setTextColor(getResources().getColor(R.color.normalColor));
                holder.mIsSafe.setText(getString(R.string.unsafe));
                holder.mClean.setImageResource(R.drawable.list_clean_cache_seletor);
                holder.mClean.setVisibility(View.VISIBLE);

                holder.mClean.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击进入程序卸载界面
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.setAction("android.intent.action.DELETE");
                        intent.setData(Uri.parse("package:" + bean.packageName));

                        startActivity(intent);
                    }
                });
            } else {
                holder.mIsSafe.setText(getString(R.string.safe));
                holder.mIsSafe.setTextColor(getResources().getColor(R.color.normalblue_green));
                holder.mClean.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

    private class ViewHolder {
        ImageView mIcon;
        TextView mTitle;
        TextView mIsSafe;
        ImageView mClean;
    }
}
