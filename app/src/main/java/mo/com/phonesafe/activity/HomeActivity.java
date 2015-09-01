package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.HomeBean;
import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.tools.Constants;
import mo.com.phonesafe.view.MyDialog;

/**
 * 作者：momxmo on 2015/8/28 19:06
 * 邮箱：xxxx@qq.com
 */


public class HomeActivity extends Activity implements AdapterView.OnItemClickListener {
    private static final String TAG = "HomeActivity";
    ImageView home_icon;
    GridView home_gridview;
    List<HomeBean> mDatas;


    private final static String[] TITLES = new String[]{"手机防盗", "骚扰拦截",
            "软件管家", "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具"};
    private final static String[] DESCS = new String[]{"远程定位手机", "全面拦截骚扰",
            "管理您的软件", "管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全"};
    private final static int[] ICONS = new int[]{
            R.mipmap.lj, R.mipmap.ac,
            R.mipmap.oh, R.mipmap.mm,
            R.mipmap.nm, R.mipmap.ph,
            R.mipmap.qh, R.mipmap.bn,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityi_home);
        //初始化View
        initView();


        //加载事件
        initEvent();

        //加载数据
        initDate();

    }

    /**
     * 加载事件
     */
    private void initEvent() {
        home_gridview.setOnItemClickListener(this);

    }

    /**
     * 加载数据
     */
    private void initDate() {

        mDatas = new ArrayList<HomeBean>();
        for (int i = 0; i < ICONS.length; i++) {
            HomeBean homebean = new HomeBean();
            homebean.title = TITLES[i];
            homebean.desc = DESCS[i];
            homebean.icon = ICONS[i];
            mDatas.add(homebean);
        }
        //添加适配器对象
        home_gridview.setAdapter(new HomeAdapter());


    }


    /**
     * 点击设置
     *
     * @param view
     */
    public void clickSetting(View view) {
        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
        //打开设置界面
        startActivity(intent);


    }

    /**
     * 初始化View
     */
    private void initView() {
        home_icon = (ImageView) findViewById(R.id.iv_home_icon);
        home_gridview = (GridView) findViewById(R.id.gv_home_gridview);

    }


    /**
     * 监听主界面的点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                //进入手机防盗界面  先设置跳过进入密码设置的界面 TODO

                /*判断用户是否已经开启防盗功能
                */
                performSjfd();
                break;
            default:
                break;
        }
    }

    /**
     * 进入手机防盗界面
     */
    private void performSjfd() {
        //第一次使用此功能---没有密码设置
        String pwd = PreferenceUtils.getString(this, Constants.SJFD_PWD);
        if (TextUtils.isEmpty(pwd)) {
            //如果为空，表示是用户第一次进入使用,
            Log.i(TAG, "提示用户初始化代码");
            // 显示Dialog,提示用户初始化密码
            showInitPwdDialog();
        } else {
            // 显示Dialog,提示用户输入密码

            Log.i(TAG, "提示用户输入密码");
            showEnterPwdDialog();
        }
    }

    /*进入手机防盗设置向导界面*/
    private void loadSjfd1Activity() {
        //进入设置向导 TODO:
        Intent intent = new Intent(this, SjfdSetup1Activity.class);
        startActivity(intent);
    }

    /*进入手机防盗界面*/
    private void loadSjfdActivity() {
        //进入手机防盗 TODO:
        Intent intent = new Intent(this, SjfdSetupActivity.class);
        startActivity(intent);
    }


    /**
     * 弹出dialog.提示用户输入密码,
     */
    private void showEnterPwdDialog() {
        final MyDialog mDialog = new MyDialog(this, R.style.MyDialog);
        mDialog.setContentView(R.layout.dialog_enter_pwd);
        //确认点击事件
        mDialog.findViewById(R.id.dialog_btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户输入的密码
                EditText EtPwd = (EditText) mDialog.findViewById(R.id.dialog_et_pwd);
                String password = EtPwd.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    //用户输入的密码为空
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();

                    return;
                }

                String Sjfd_pwd = PreferenceUtils.getString(HomeActivity.this, Constants.SJFD_PWD);

                //判断用户输入的密码和初始密码是否一致
                if (!password.equals(Sjfd_pwd)) {
                    //用户输入密码不一致
                    Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    return;
                }

                //关闭Dialog
                mDialog.dismiss();

                //如果用户是
                // 第一次进入设置页面，显示设置向导
                // 进入手机防盗页面 TODO:

//                判断用户是否已经开启防盗功能
                boolean sjfd_protect = PreferenceUtils.getBoolean(HomeActivity.this, Constants.SJFD_PROTECT);
                if (sjfd_protect) {
                    // 已经开启防盗功能，直接进入到SjfdSetupActivity的设置界面中
                    loadSjfdActivity();
                } else {

                    //进入设置向导界面
                    loadSjfd1Activity();
                }

            }
        });

        //用户取消输入密码
        mDialog.findViewById(R.id.dialog_btn_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //关闭dialog
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    /**
     * 弹出的dialog，显示让用户输入,用户第一次使用，验证完毕之后，进入向导页面
     */
    private void showInitPwdDialog() {

       /* //向老师咨询Dialog的显示风格为什么不样   TODO:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view= View.inflate(this,R.layout.dialog_init_pwd,null);
        builder.setView(view);
        builder.show();*/


        final MyDialog builder = new MyDialog(this, R.style.MyDialog);
        builder.setContentView(R.layout.dialog_init_pwd);
        builder.show();

      //用户确认输入的是监听事件
        builder.findViewById(R.id.dialog_btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText mPwd = (EditText) builder.findViewById(R.id.dialog_et_pwd);
                EditText mPwdConfirm = (EditText) builder.findViewById(R.id.dialog_et_confirm);

                //获取用户输入的密码和确认密码

                String password = mPwd.getText().toString().trim();
                String confirm = mPwdConfirm.getText().toString().trim();

                /*判断用户输入的密码是否为空*/
                if (TextUtils.isEmpty(password)) {
                    //用户输入的密码为空
                    //让用户的输入框获取焦点
                    mPwd.requestFocus();
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                /*判断用户输入的确认密码是否为空*/
                if (TextUtils.isEmpty(confirm)) {
                    //用户输入的确认密码为空
                    //让用户的输入框获取焦点
                    mPwdConfirm.requestFocus();
                    Toast.makeText(HomeActivity.this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                //判断用户输入的密码和确认密码是否一致
                if (!password.equals(confirm)) {
                    mPwd.requestFocus();
                    Toast.makeText(HomeActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }

                //用户输入密码和确认密码是一致的
                //将用户输入的密码保存到配置文件中
                PreferenceUtils.putString(HomeActivity.this, Constants.SJFD_PWD, password);

                //将Dialog页面关闭
                builder.dismiss();

                //进入手机防盗向导页面
                loadSjfd1Activity();

                Log.i(TAG, "mpwd:" + password + "............mPwdConfirm:" + confirm);

            }
        });

        //用户取消输入
        builder.findViewById(R.id.dialog_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //将Dialog页面关闭
                builder.dismiss();

                Log.i(TAG, "用户取消输入Cancel");
            }
        });
    }


    /**
     * 主界面的适配器
     */
    private class HomeAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(HomeActivity.this, R.layout.item_home, null);
            }
            ImageView home_itme_icon = (ImageView) convertView.findViewById(R.id.iv_home_item_icon);
            TextView home_item_title = (TextView) convertView.findViewById(R.id.tv_home_item_title);
            TextView home_item_desc = (TextView) convertView.findViewById(R.id.tv_home_item_desc);


            HomeBean homebean = mDatas.get(position);

            home_itme_icon.setImageResource(homebean.icon);
            home_item_desc.setText(homebean.desc);
            home_item_title.setText(homebean.title);


            return convertView;
        }
    }
}
