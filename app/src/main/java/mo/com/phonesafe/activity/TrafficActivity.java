package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.TrafficBean;
import mo.com.phonesafe.tools.GZIPUtils;

/**
 * 作者：MoMxMo on 2015/9/15 14:34
 * 邮箱：xxxx@qq.com
 * <p/>
 * <p/>
 * 手机流量统计
 */

public class TrafficActivity extends Activity {

    private static final String TAG = "TrafficActivity";
    private ListView lv_traffic;
    private List<TrafficBean> mListData;
    private PackageManager mPm;
    private LinearLayout ll_normal_loading;
    private TrafficAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        mPm = getPackageManager();
        initView();
        initData();
    }

    private void initView() {
        lv_traffic = (ListView) findViewById(R.id.lv_traffic);
        ll_normal_loading = (LinearLayout) findViewById(R.id.ll_normal_loading);
    }

    private void initData() {
        mAdapter = new TrafficAdapter();
        lv_traffic.setAdapter(mAdapter);
        final File file = new File("/proc/uid_stat");
        if (!file.exists()) {
            Toast.makeText(TrafficActivity.this, "手机不支持流量统计", Toast.LENGTH_SHORT).show();
            return;
        }

        /*使用线程获取数据*/
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                if (mListData == null) {
                    mListData = new ArrayList<TrafficBean>();
                } else {
                    mListData.clear();
                }

                /*显示加载，隐藏数据*/
                ll_normal_loading.setVisibility(View.VISIBLE);
                lv_traffic.setVisibility(View.GONE);
                super.onPreExecute();
            }

            /*执行耗时操作*/
            @Override
            protected Void doInBackground(Void... params) {
                List<PackageInfo> packageInfoList = mPm.getInstalledPackages(0);

                for (PackageInfo info : packageInfoList) {
                    TrafficBean bean = new TrafficBean();

                    ApplicationInfo applicationInfo = info.applicationInfo;

                    Drawable icon = applicationInfo.loadIcon(mPm);
                    String appName = applicationInfo.loadLabel(mPm).toString();

                    /*获取程序运行时的id,
                    读取系统内容文件中的流统计信息
                    /proc/uid_start/用户id/tcp_rcv:手机接收到的数据包
                    /proc/uid_start/用户id/tcp_snd:手机发送的数据包
                    */
                    int uid = applicationInfo.uid;
                    long rcv = getTcp_rcv(uid);
                    long snd = getTcp_snd(uid);
                    bean.icon = icon;
                    bean.app_name = appName;
                    bean.inTraffic = rcv;
                    bean.outTraffic = snd;
                    if (rcv > 0 || snd > 0) {
                        mListData.add(bean);
                    }
                }
                return null;
            }

            /*接收耗时操作*/
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.i(TAG, "共有： " + mListData.size());
                for (TrafficBean bean : mListData) {
                    Log.i(TAG, bean.toString());
                }

                /*显示数据,隐藏进度*/
                ll_normal_loading.setVisibility(View.GONE);
                lv_traffic.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();

            }
        }.execute();
    }

    /*获取发送的数据流量，通过程序运行是的UId*/
    private long getTcp_snd(int uid) {
        File file = new File("/proc/uid_stat/" + uid + "/tcp_snd");
        if (!file.exists()) {
            return 0;
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line = in.readLine();
            return Long.valueOf(line);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            GZIPUtils.closeIO(in);
        }
    }

    /*获取接收到的数据流量，通过程序运行是的UId*/
    private long getTcp_rcv(int uid) {
        File file = new File("/proc/uid_stat/" + uid + "/tcp_rcv");
        if (!file.exists()) {
            return 0;
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line = in.readLine();
            return Long.valueOf(line);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            GZIPUtils.closeIO(in);
        }
        return 0;
    }

    private class TrafficAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mListData != null ? mListData.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mListData != null ? mListData.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public android.view.View getView(int position, android.view.View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(TrafficActivity.this, R.layout.item_traffic_list, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.mIcon = (ImageView) convertView.findViewById(R.id.iv_traffic_item_icon);
                holder.mTitle = (TextView) convertView.findViewById(R.id.tv_item_title);
                holder.mInTraffic = (TextView) convertView.findViewById(R.id.tv_item_rcv);
                holder.mOutTraffic = (TextView) convertView.findViewById(R.id.tv_item_snd);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            TrafficBean bean = (TrafficBean) getItem(position);

            holder.mIcon.setImageDrawable(bean.icon);
            holder.mTitle.setText(bean.app_name);
            holder.mInTraffic.setText(getString(R.string.rcv, Formatter.formatFileSize(TrafficActivity.this, bean.inTraffic)));
            holder.mOutTraffic.setText(getString(R.string.snd, Formatter.formatFileSize(TrafficActivity.this, bean.outTraffic)));
            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView mIcon;
        TextView mTitle;
        TextView mInTraffic;
        TextView mOutTraffic;

    }


}
