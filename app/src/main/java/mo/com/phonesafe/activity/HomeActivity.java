package mo.com.phonesafe.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.HomeBean;
import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.tools.Constants;

/**
 * Created by Gh0st on 2015/8/28 19:06
 */


public class HomeActivity extends Activity implements AdapterView.OnItemClickListener {
    ImageView home_icon;
    GridView home_gridview;
    List<HomeBean> mDatas;
    private ImageView iv_ui;
    private ImageView iv_ui2;
    private ImageView iv_ui3;
    private ObjectAnimator oa2;
    private ObjectAnimator oa3;
    private String[] TITLES;
    private String[] DESCRIPTION;
    private final static int[] ICONS = new int[]{
            R.mipmap.lj, R.mipmap.cm,
            R.mipmap.oh, R.mipmap.mm,
            R.mipmap.nm, R.mipmap.ph,
            R.mipmap.qh, R.mipmap.lh,
    };
    private TextView tv_precent;
    private InputMethodManager mIm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityi_home2);
        TITLES = getResources().getStringArray(R.array.home_title);
        DESCRIPTION = getResources().getStringArray(R.array.home_title_description);
        initView();
        initCreatAnim();
        initEvent();
        initDate();
    }

    @Override
    protected void onStart() {
        //开启动画
        oa2.start();
        oa3.start();

        /*累加显示百分比*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; i++) {
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_precent.setText(finalI + "");
                        }
                    });
                }
            }
        }).start();
        super.onStart();
    }

    private void initCreatAnim() {
        //透明圆圈动画
        oa2 = ObjectAnimator.ofFloat(iv_ui2, "rotation", new float[]{0, 45, 270, 360});
        oa2.setDuration(1500);
        oa2.setRepeatCount(1);
        oa2.setRepeatMode(ObjectAnimator.REVERSE);
        //外边圆动画
        oa3 = ObjectAnimator.ofFloat(iv_ui3, "rotation", new float[]{360, 20});
        oa3.setDuration(1500);
        oa3.setRepeatCount(1);
        oa3.setRepeatMode(ObjectAnimator.REVERSE);
    }

    private void initEvent() {
        home_gridview.setOnItemClickListener(this);

        iv_ui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStart();
            }
        });
    }

    private void initDate() {

        mDatas = new ArrayList<HomeBean>();
        for (int i = 0; i < ICONS.length; i++) {
            HomeBean homebean = new HomeBean();
            homebean.title = TITLES[i];
            homebean.desc = DESCRIPTION[i];
            homebean.icon = ICONS[i];
            mDatas.add(homebean);
        }
        home_gridview.setAdapter(new HomeAdapter());
    }

    public void clickSetting(View view) {
        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    private void initView() {
        home_icon = (ImageView) findViewById(R.id.iv_home_icon);
        home_gridview = (GridView) findViewById(R.id.gv_home_gridview);

        iv_ui = (ImageView) findViewById(R.id.iv_ui_360);
        iv_ui2 = (ImageView) findViewById(R.id.iv_ui2_360);
        iv_ui3 = (ImageView) findViewById(R.id.iv_ui3_360);
        tv_precent = (TextView) findViewById(R.id.tv_home_precent);

    }


    /**
     * 主界面Item的点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                performSjfd();
                break;
            case 1:
                /*黑名单管理*/
                preformNextActivity(BlackManagerActivity.class);
                break;
            case 2:
                /*软件管理*/
                preformNextActivity(AppManagerActivity.class);
                break;
            case 3:
                /*进程管理*/
                preformNextActivity(ProcessManagerActivity.class);
                break;
            case 4:
                /*流量统计*/
                preformNextActivity(TrafficActivity.class);
                break;
            case 5:
                /*手机杀毒*/
                preformNextActivity(AnitVirusActivity.class);
                break;
            case 6:
                /*缓存清理*/
                preformNextActivity(CacheCleanActivity.class);
                break;
            case 7:
                /*常用工具*/
                preformNextActivity(CommonToolActivity.class);
                break;
            default:
                break;
        }
    }

    /**
     * 到下一个界面
     *
     * @param clazz 类名
     */
    private void preformNextActivity(Class clazz) {
        Intent intent = new Intent(HomeActivity.this, clazz);
        startActivity(intent);
    }

    /**
     * 进入手机防盗界面
     */
    private void performSjfd() {
        String pwd = PreferenceUtils.getString(this, Constants.SJFD_PWD);
        if (TextUtils.isEmpty(pwd)) {
            showInitPwdDialog();
        } else {
            showEnterPwdDialog();
        }
    }


    /**
     * 弹出dialog.提示用户输入密码,
     */
    private void showEnterPwdDialog() {
        mIm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = View.inflate(this, R.layout.dialog_enter_pwd, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        dialog.show();

        view.findViewById(R.id.dialog_btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText EtPwd = (EditText) view.findViewById(R.id.dialog_et_pwd);
                mIm.showSoftInput(EtPwd, 0);
                String password = EtPwd.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    EtPwd.setError(getString(R.string.passwordempyt)); //密码不能为空
                    return;
                }
                String Sjfd_pwd = PreferenceUtils.getString(HomeActivity.this, Constants.SJFD_PWD);
                if (!password.equals(Sjfd_pwd)) {
                    EtPwd.setError(getString(R.string.passworderrer)); //密码错误
                    return;
                }

                dialog.dismiss();

                //如果用户是 第一次进入设置页面，显示设置向导进入手机防盗页面

                //判断用户是否已经开启防盗功能
                boolean sjfd_protect = PreferenceUtils.getBoolean(HomeActivity.this, Constants.SJFD_PROTECT);
                if (sjfd_protect) {// 已经开启防盗功能，直接进入到SjfdSetupActivity的设置界面中
                    preformNextActivity(SjfdSetup1Activity.class);
                } else {//进入设置向导界面
                    preformNextActivity(SjfdSetupActivity.class);
                }
            }
        });
        view.findViewById(R.id.dialog_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    /**
     * 弹出的dialog，显示让用户输入,用户第一次使用，验证完毕之后，进入向导页面
     */
    private void showInitPwdDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = View.inflate(this, R.layout.dialog_init_pwd, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        dialog.show();
        /**
         * 监听OK
         */
        view.findViewById(R.id.dialog_btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mPwd = (EditText) view.findViewById(R.id.dialog_et_pwd);
                EditText mPwdConfirm = (EditText) view.findViewById(R.id.dialog_et_confirm);
                String password = mPwd.getText().toString().trim();
                String confirm = mPwdConfirm.getText().toString().trim();

                //判断用户输入的密码是否为空
                if (TextUtils.isEmpty(password)) {
                    mPwd.requestFocus();
                    mPwd.setError(getString(R.string.passwordempyt));
                    return;
                }
                // 判断用户输入的确认密码是否为空
                if (TextUtils.isEmpty(confirm)) {
                    mPwdConfirm.requestFocus();
                    mPwdConfirm.setError(getString(R.string.passwordempyt));
                    return;
                }

                //判断用户输入的密码和确认密码是否一致
                if (!password.equals(confirm)) {
                    mPwdConfirm.requestFocus();
                    mPwdConfirm.setError(getString(R.string.confpassworderrer));
                    return;
                }

                //将用户输入的密码保存到配置文件中
                PreferenceUtils.putString(HomeActivity.this, Constants.SJFD_PWD, password);

                //进入手机防盗向导页面
                //loadSjfd1Activity();
                preformNextActivity(SjfdSetupActivity.class);
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.dialog_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    /**
     * 主界面的适配器
     */
    private class HomeAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mDatas != null ? mDatas.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mDatas != null ? mDatas.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(HomeActivity.this, R.layout.item_home, null);
            }
            ImageView home_item_icon = (ImageView) convertView.findViewById(R.id.iv_home_item_icon);
            TextView home_item_title = (TextView) convertView.findViewById(R.id.tv_home_item_title);
            TextView home_item_desc = (TextView) convertView.findViewById(R.id.tv_home_item_desc);

            HomeBean homebean = mDatas.get(position);

            home_item_icon.setImageResource(homebean.icon);
            home_item_desc.setText(homebean.desc);
            home_item_title.setText(homebean.title);
            return convertView;
        }
    }

}
