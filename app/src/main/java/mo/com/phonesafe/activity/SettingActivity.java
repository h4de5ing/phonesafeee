package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import mo.com.phonesafe.R;
import mo.com.phonesafe.dialog.AddressStyleDialog;
import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.service.CallSmsInterceptService;
import mo.com.phonesafe.service.NumberAddressService;
import mo.com.phonesafe.tools.Constants;
import mo.com.phonesafe.tools.ServiceStateUtils;
import mo.com.phonesafe.view.SettingItemView;

/**
 * 作者：MoMxMo on 2015/8/29 21:18
 * 邮箱：xxxx@qq.com
 */


public class SettingActivity extends Activity {

    private static final String TAG = "SettingActivity";
    SettingItemView mSivAutoUpdate;
    SettingItemView mSivAutoIntercept;
    private SettingItemView mSivNumnberAddress;
    private String[] add_style_title = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
    private int[] add_style_color = {R.drawable.address_shape_color_translucence,
            R.drawable.address_shape_color_yellow,
            R.drawable.address_shape_color_blue,
            R.drawable.address_shape_color_gray,
            R.drawable.address_shape_color_green};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityi_setting);
        //初始化View
        initView();

        //初始化数据
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mSivAutoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "settingAutoUpdate");

                //UI图标开的时候就关，关的时候就开
                mSivAutoUpdate.toggle();

                //业务逻辑：下次进入时，不检测网络更新--》持久化存储
                PreferenceUtils.putBoolean(SettingActivity.this, Constants.AUTO_UPDATE, mSivAutoUpdate.getToggleState());

            }
        });


        //拦截服务点击事件的监听
        mSivAutoIntercept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "拦截设置被点击了额");

                //UI图标开的时候就关，关的时候就开
                mSivAutoIntercept.toggle();
                //业务逻辑：下次进入时，不检测网络更新--》持久化存储

                if (ServiceStateUtils.getServiceState(SettingActivity.this, CallSmsInterceptService.class)) {
                    //服务是开启的，点击停止服务
                    Intent intent = new Intent(SettingActivity.this, CallSmsInterceptService.class);
                    stopService(intent);
                } else {
                    //服务没有开启，点击开启服务
                    Intent intent = new Intent(SettingActivity.this, CallSmsInterceptService.class);
                    startService(intent);
                }

            }
        });

        //来电和去电显示号码归属地
        mSivNumnberAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "电话归属点被点击了");

                //UI图标开的时候就关，关的时候就开
                mSivNumnberAddress.toggle();
                //业务逻辑：下次进入时，不检测网络更新--》持久化存储

                if (ServiceStateUtils.getServiceState(SettingActivity.this, NumberAddressService.class)) {
                    //服务是开启的，点击停止服务
                    Intent intent = new Intent(SettingActivity.this, NumberAddressService.class);
                    stopService(intent);
                } else {
                    //服务没有开启，点击开启服务
                    Intent intent = new Intent(SettingActivity.this, NumberAddressService.class);
                    startService(intent);
                }
            }
        });

        /**
         * 自定义显示号码归属地样式
         */
        findViewById(R.id.setting_siv_numberaddress_style).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.i(TAG, "onClick 号码归属地样式开启。。。。。。。");
                final AddressStyleDialog mDialog = new AddressStyleDialog(SettingActivity.this);
                mDialog.show();
                mDialog.setAdapter(new AddressStyleAdapter());
                mDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mDialog.dismiss();

                        //保存选择的选项
                        PreferenceUtils.putInt(SettingActivity.this,Constants.ADDRESS_STYLE,add_style_color[position]);

                        ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                        iv_icon.setImageResource(add_style_color[position]);
                    }
                });
            }
        });
    }

    /*号码归属地适配器*/
    private class AddressStyleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return add_style_color.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(SettingActivity.this, R.layout.item_address_style, null);
                holder = new ViewHolder();
                holder.ivColor = (ImageView) convertView.findViewById(R.id.iv_color);
                holder.tvTile = (TextView) convertView.findViewById(R.id.tv_tile);
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.ivColor.setImageResource(add_style_color[position]);
            holder.tvTile.setText(add_style_title[position]);
            int currentStyle = PreferenceUtils.getInt(SettingActivity.this, Constants.ADDRESS_STYLE);

            if (currentStyle == add_style_color[position]) {
                holder.ivIcon.setImageResource(R.drawable.rh);
            }

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView ivColor;
        TextView tvTile;
        ImageView ivIcon;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //显示服务开启的状态(在onstart状态获取实时服务启动的状态)
        //设置拦截
        mSivAutoIntercept.setToggleState(ServiceStateUtils.getServiceState(this, CallSmsInterceptService.class));

        //显示号码归属地
        mSivNumnberAddress.setToggleState(ServiceStateUtils.getServiceState(this, NumberAddressService.class));

    }

    //初始化View
    private void initView() {

        mSivAutoUpdate = (SettingItemView) findViewById(R.id.setting_siv_autoupdate);
        mSivAutoIntercept = (SettingItemView) findViewById(R.id.setting_siv_autouIntercept);
        mSivNumnberAddress = (SettingItemView) findViewById(R.id.setting_siv_numberaddress);

        //设置初始化用户设置更新的状态
        mSivAutoUpdate.setToggleState(PreferenceUtils.getBoolean(this, Constants.AUTO_UPDATE));

        Log.i(TAG, "initView 更新：" + PreferenceUtils.getBoolean(this, Constants.AUTO_UPDATE));
        Log.i(TAG, "initView 拦截：" + PreferenceUtils.getBoolean(this, Constants.AUTO_INTERCEPT));
    }

    //点击返回主界面
    public void exitSetting(View view) {
        /*关闭自己的界面*/
        finish();
    }
}
