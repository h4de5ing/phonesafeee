package mo.com.phonesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
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
        lv_applist.setAdapter(new AppManagerAdapter());

    }

    private void initData() {
        //获取Rom内存大小
        File romFile = Environment.getDataDirectory();
        String[] romMemory = getMemory(romFile);
        tv_inter_remainder.setText("剩余：" + romMemory[0]);
        tv_inter_total.setText("总内存：" + romMemory[1]);


        //获取SD内存大小
        File sdFile = Environment.getExternalStorageDirectory();
        String[] sdMemory = getMemory(sdFile);
        tv_out_remainder.setText("剩余：" + sdMemory[0]);
        tv_out_total.setText("总内存：" + sdMemory[1]);

        //获取索引应用的相关信息
        listdata = AppProvider.getAppManagerInfo(AppManagerActivity.this);

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
        tv_out_total = (TextView) findViewById(R.id.tv_out_total);
        lv_applist = (ListView) findViewById(R.id.lv_app_list);
    }

    private class AppManagerAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (listdata != null) {
                return listdata.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (listdata != null) {

                return listdata.get(position);
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
