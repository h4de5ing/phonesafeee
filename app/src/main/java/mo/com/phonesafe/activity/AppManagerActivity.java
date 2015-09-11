package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.AppBean;
import mo.com.phonesafe.business.AppProvider;

/**
 * 作者：MoMxMo on 2015/9/8 10:19
 * 邮箱：xxxx@qq.com
 */


public class AppManagerActivity extends Activity {

    private static final String TAG = "AppManagerActivity";
    private TextView tv_inter_remainder;
    private TextView tv_inter_total;
    private TextView tv_out_remainder;
    private TextView tv_out_total;
    private ListView lv_applist;
    private List<AppBean> listdata;
    private List<AppBean> listdataUser;
    private List<AppBean> listdataSystem;
    private AppManagerAdapter mAdapter;
    private RelativeLayout rl_loading;
    private TextView tv_titile;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);

        //初始化View
        initView();

        //初始化数据
        initData();

        //事件的监听
        initEvent();
    }

    private void initEvent() {
        mAdapter = new AppManagerAdapter();
        lv_applist.setAdapter(mAdapter);

        //listView的item监听事件
        lv_applist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //显示PopupWindow*/

                final AppBean bean = (AppBean) mAdapter.getItem(position);
                Log.i(TAG, "onItemSelected 您好。。。。。。" + position + ".....");
                if (bean!=null) {

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
                if (listdataUser != null && firstVisibleItem == 0) {
                    tv_titile.setText("SD卡(" + listdataUser.size() + ")应用");
                }
                if (listdataSystem != null && firstVisibleItem == listdataUser.size() + 1) {
                    tv_titile.setText("手机存储(" + listdataSystem.size() + ")应用");
                }
            }
        });

    }

    private void showPopup(View view, final AppBean bean) {
        LinearLayout ll_popup = (LinearLayout) View.inflate(AppManagerActivity.this, R.layout.popup_app_option, null);
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

        //展示位置
        window.showAsDropDown(view,view.getWidth(),-view.getHeight());

        //卸载
        ll_popup.findViewById(R.id.pop_uninstall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (bean.isInstallSD) {  TODO  手机因为是不是装载手机内部就是system应用
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_UNINSTALL_PACKAGE);
                    intent.setData(Uri.parse("package:" + bean.packageName));
                    startActivity(intent);
//                } else {
//                    Toast.makeText(AppManagerActivity.this, "系统程序不能卸载", Toast.LENGTH_SHORT).show();
//                }
                window.dismiss();
            }
        });

        //打开
        ll_popup.findViewById(R.id.pop_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onItemSelected 打开。。....."+bean.toString());

                //启动程序
                PackageManager pm = getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(bean.packageName);

                if (intent == null) {
                    //系统内存的程序是没有界面的，所以intent为空
                    Toast.makeText(AppManagerActivity.this, "后台系统没有界面", Toast.LENGTH_SHORT).show();
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
                //应用程序的分享
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
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

    private void initData() {
        //获取内部空间
        getRomMemory();

        //获取SD空间
        getSdMemory();

        tv_titile.setVisibility(View.GONE);
        rl_loading.setVisibility(View.VISIBLE);
        /*使用线程加载数据*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取索引应用的相关信息
                listdata = AppProvider.getAppManagerInfo(AppManagerActivity.this);
                listdataUser = new ArrayList<AppBean>();
                listdataSystem = new ArrayList<AppBean>();

                //使用迭代器，保证了线程的安全
                Iterator<AppBean> iterator = listdata.iterator();
                while (iterator.hasNext()) {
                    AppBean bean = iterator.next();
                    if (bean.isInstallSD) {
                        listdataUser.add(bean);
                    }
                    if (bean.isSystem) {
                        listdataSystem.add(bean);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rl_loading.setVisibility(View.GONE);
                        tv_titile.setText("SD卡应用");
                        tv_titile.setVisibility(View.VISIBLE);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void getSdMemory() {
        //获取SD内存大小
        File sdFile = Environment.getExternalStorageDirectory();
        String[] sdMemory = getMemory(sdFile);
        tv_out_remainder.setText("剩余：" + sdMemory[0]);
        tv_out_total.setText("总内存：" + sdMemory[1]);
    }

    private void getRomMemory() {
        //获取Rom内存大小
        File romFile = Environment.getDataDirectory();
        String[] romMemory = getMemory(romFile);
        tv_inter_remainder.setText("剩余：" + romMemory[0]);
        tv_inter_total.setText("总内存：" + romMemory[1]);
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

    private void initView() {
        tv_inter_remainder = (TextView) findViewById(R.id.tv_inter_remainder);
        tv_inter_total = (TextView) findViewById(R.id.tv_inter_total);
        tv_out_remainder = (TextView) findViewById(R.id.tv_out_remainder);
        tv_titile = (TextView) findViewById(R.id.tv_titile);
        tv_out_total = (TextView) findViewById(R.id.tv_out_total);
        rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
        lv_applist = (ListView) findViewById(R.id.lv_app_list);
    }

    private class AppManagerAdapter extends BaseAdapter {
        @Override
        public int getCount() {

            int count = 0;
            if (listdataUser != null && listdataUser.size() > 0) {
                count += listdataUser.size();
                count++;
            }

            if (listdataSystem != null && listdataSystem.size() > 0) {
                count += listdataSystem.size();
                count++;
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            Log.i(TAG, "测试位置（1）： " + position);

            if (position == 0) {
                return null;    //显示用户程序
            }

            if (position > 0 && position <= listdataUser.size()) {
                return listdataUser.get(position - 1);
            } else {
                if (position == listdataUser.size() + 1) {
                    return null;
                }
                Log.i(TAG, "测试位置（2）： " + (position - listdataUser.size() - 1));
                return listdataSystem.get(position - listdataUser.size() - 1);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {

            //显示用户
            if (position == 0 ) {
                TextView tvSD = new TextView(AppManagerActivity.this);
                tvSD.setBackgroundColor(getResources().getColor(R.color.switch_thumb_normal_material_dark));
                tvSD.setPadding(8, 8, 8, 8);
                tvSD.setText("SD卡共" + listdataUser.size() + "个应用");
                return tvSD;
            }
            if (position == listdataUser.size()+1) {
                TextView tvSD = new TextView(AppManagerActivity.this);
                tvSD.setBackgroundColor(getResources().getColor(R.color.switch_thumb_normal_material_dark));
                tvSD.setPadding(8,8, 8, 8);
                tvSD.setText("手机存储(" + listdataSystem.size() + ")应用");
                return tvSD;
            }


            ViewHolder holder = null;
            if (convertView == null || convertView instanceof TextView) {
                convertView = View.inflate(AppManagerActivity.this, R.layout.activity_item_app_manager, null);
                holder = new ViewHolder();

                holder.mIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.mName = (TextView) convertView.findViewById(R.id.tv_name);
                holder.mPostion = (TextView) convertView.findViewById(R.id.tv_install_position);
                holder.mSize = (TextView) convertView.findViewById(R.id.tv_size);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //获取数据
            AppBean appBean = listdata.get(position);
            holder.mIcon.setImageDrawable(appBean.icon);
            holder.mName.setText(appBean.name);
            String size = Formatter.formatFileSize(AppManagerActivity.this, appBean.size);
            holder.mSize.setText(size);

            //应用的安装位置
            if (appBean.isInstallSD) {
                holder.mPostion.setText("SD卡");
            }
            if (appBean.isSystem) {
                holder.mPostion.setText("手机内存");
            }

            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView mIcon;
        TextView mName;
        TextView mPostion;
        TextView mSize;
    }
}
