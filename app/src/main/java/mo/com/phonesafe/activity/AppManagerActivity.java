package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.AppBean;
import mo.com.phonesafe.business.AppProvider;

/**
 * 作者：MoMxMo on 2015/9/8 10:19
 * 邮箱：xxxx@qq.com
 */


public class AppManagerActivity extends Activity {
    private TextView tv_inter_remainder;
    private TextView tv_inter_total;
    private TextView tv_out_remainder;
    private TextView tv_out_total;
    private ListView lv_applist;
    private List<AppBean> listdata;
    private List<AppBean> mUserDatas;
    private List<AppBean> mSystemDatas;
    private AppManagerAdapter mAdapter;
    private TextView tv_title;
    private ProgressBar mProgressBar;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        tv_inter_remainder = (TextView) findViewById(R.id.tv_inter_remainder);
        tv_inter_total = (TextView) findViewById(R.id.tv_inter_total);
        tv_out_remainder = (TextView) findViewById(R.id.tv_out_remainder);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setHeight(25);
        tv_out_total = (TextView) findViewById(R.id.tv_out_total);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        lv_applist = (ListView) findViewById(R.id.lv_app_list);
    }

    private void initData() {
        //获取内部空间
        getRomMemory();

        //获取SD空间
        getSdMemory();

        mProgressBar.setVisibility(View.VISIBLE);
        /*使用线程加载数据*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取索引应用的相关信息
                listdata = AppProvider.getAppManagerInfo(AppManagerActivity.this);
                mUserDatas = new ArrayList<AppBean>();
                mSystemDatas = new ArrayList<AppBean>();

                for (AppBean bean : listdata) {
                    if (bean.isSystem) {
                        mSystemDatas.add(bean);
                    } else {
                        mUserDatas.add(bean);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mProgressBar.setVisibility(View.GONE);
                        tv_title.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }

    private void initEvent() {
        mAdapter = new AppManagerAdapter();
        lv_applist.setAdapter(mAdapter);
        lv_applist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AppBean bean = (AppBean) mAdapter.getItem(position);
                if (bean != null) {
                    showPopup(view, bean);
                }
            }
        });


        //listView滑动的监听
        lv_applist.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mUserDatas == null && mSystemDatas == null) {
                    return;
                }
                if (firstVisibleItem >= 0 && firstVisibleItem <= mUserDatas.size()) {
                    tv_title.setText(getString(R.string.userappcount, mUserDatas.size()));
                } else {
                    tv_title.setText(getString(R.string.systemappcount, mSystemDatas.size()));
                }
            }
        });

    }

    private void showPopup(View view, final AppBean bean) {
        LinearLayout ll_popup = (LinearLayout) View.inflate(AppManagerActivity.this, R.layout.popup_app_option, null);

        //设置宽度和高度
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        final PopupWindow window = new PopupWindow(ll_popup, width, height);

        //获取焦点
        window.setFocusable(true);
        //外侧可以点击
        window.setOutsideTouchable(true);
        //设置背景
        window.setBackgroundDrawable(new ColorDrawable());

        //设置进出动画
        window.setAnimationStyle(R.style.PopupAnimation);

        //展示位置，在当前Activity位置
        window.showAsDropDown(view, view.getWidth(), -view.getHeight());

        //卸载
        ll_popup.findViewById(R.id.pop_uninstall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.isSystem) {
                    Toast.makeText(AppManagerActivity.this, getString(R.string.uninstallerr), Toast.LENGTH_SHORT).show();
                    window.dismiss();
                    return;
                }
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_UNINSTALL_PACKAGE);
                intent.setData(Uri.parse("package:" + bean.packageName));
                startActivity(intent);
                window.dismiss();
            }
        });

        //打开
        ll_popup.findViewById(R.id.pop_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PackageManager pm = getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(bean.packageName);
                if (intent == null) { //系统内存的程序是没有界面的，所以intent为空
                    Toast.makeText(AppManagerActivity.this, getString(R.string.noui), Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(intent);
                }
                window.dismiss();
            }
        });

        //分享
        ll_popup.findViewById(R.id.pop_shape).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:"));
                intent.putExtra("sms_body", "MoMxMo Team Welcome to");
                startActivity(intent);
                window.dismiss();
            }
        });

        //信息
        ll_popup.findViewById(R.id.pop_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // <intent-filter>
                // <action
                // android:name="android.settings.APPLICATION_DETAILS_SETTINGS"
                // />
                // <category
                // android:name="android.intent.category.DEFAULT" />
                // <data android:scheme="package" />
                // </intent-filter>
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + bean.packageName));
                startActivity(intent);
                window.dismiss();
            }
        });
    }

    //获取SD内存大小
    private void getSdMemory() {
        File sdFile = Environment.getExternalStorageDirectory();
        String[] sdMemory = getMemory(sdFile);
        tv_out_remainder.setText(getString(R.string.remainder, sdMemory[0]));
        tv_out_total.setText(getString(R.string.total, sdMemory[1]));
    }

    //获取Rom内存大小
    private void getRomMemory() {
        File romFile = Environment.getDataDirectory();
        String[] romMemory = getMemory(romFile);
        tv_inter_remainder.setText(getString(R.string.remainder, romMemory[0]));
        tv_inter_total.setText(getString(R.string.total, romMemory[1]));
    }

    /**
     * 获取内存大小  和 剩余量
     *
     * @param file
     * @return String[0]表示内存剩余量 String[1]表示内存总量
     */
    private String[] getMemory(File file) {

        //获取剩余内存
        long freeSpace = file.getFreeSpace();

        //获取中空间
        long totalSpace = file.getTotalSpace();

        //格式数据
        String free = Formatter.formatFileSize(AppManagerActivity.this, freeSpace);
        String total = Formatter.formatFileSize(AppManagerActivity.this, totalSpace);

        String[] data = {free, total};
        return data;
    }

    private class AppManagerAdapter extends BaseAdapter {
        @Override
        public int getCount() {

            int count = 0;
            if (mUserDatas != null && mUserDatas.size() > 0) {
                count += mUserDatas.size();
                count++;
            }

            if (mSystemDatas != null && mSystemDatas.size() > 0) {
                count += mSystemDatas.size();
                count++;
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;    //显示用户程序
            }

            if (position > 0 && position <= mUserDatas.size()) {
                return mUserDatas.get(position - 1);
            } else {
                if (position == mUserDatas.size()) {
                    return null;
                }
                return mSystemDatas.get(position - mUserDatas.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {

            //显示用户
            if (position == 0) {
                TextView tvSD = new TextView(AppManagerActivity.this);
                tvSD.setBackgroundColor(getResources().getColor(R.color.switch_thumb_normal_material_dark));
                tvSD.setPadding(8, 8, 8, 8);
                tvSD.setText(getString(R.string.userappcount, mUserDatas.size()));
                return tvSD;
            }
            if (position == mUserDatas.size() + 1) {
                TextView tvSD = new TextView(AppManagerActivity.this);
                tvSD.setBackgroundColor(getResources().getColor(R.color.switch_thumb_normal_material_dark));
                tvSD.setPadding(8, 8, 8, 8);
                tvSD.setText(getString(R.string.systemappcount, mSystemDatas.size()));
                return tvSD;
            }

            ViewHolder holder = null;
            if (convertView == null || convertView instanceof TextView) {
                convertView = View.inflate(AppManagerActivity.this, R.layout.activity_item_app_manager, null);
                holder = new ViewHolder();

                holder.mIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.mName = (TextView) convertView.findViewById(R.id.tv_name);
                holder.mPosition = (TextView) convertView.findViewById(R.id.tv_install_position);
                holder.mSize = (TextView) convertView.findViewById(R.id.tv_size);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //获取数据
            AppBean appBean = (AppBean) getItem(position);
            holder.mIcon.setImageDrawable(appBean.icon);
            holder.mName.setText(appBean.name);
            String size = Formatter.formatFileSize(AppManagerActivity.this, appBean.size);
            holder.mSize.setText(size);

            //应用的安装位置
            if (appBean.isInstallSD) {
                holder.mPosition.setText(getString(R.string.sdcard));
            }
            if (appBean.isSystem) {
                holder.mPosition.setText(getString(R.string.phonemeoney));
            }
            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView mIcon;
        TextView mName;
        TextView mPosition;
        TextView mSize;
    }
}
