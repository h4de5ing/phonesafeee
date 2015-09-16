package mo.com.phonesafe.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.ProcessBean;
import mo.com.phonesafe.business.ProcessProvider;
import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.service.AutoCleanService;
import mo.com.phonesafe.tools.Constants;
import mo.com.phonesafe.view.SettingItemView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * 作者：MoMxMo on 2015/9/9 21:40
 * 邮箱：xxxx@qq.com
 */


public class ProcessManagerActivity extends Activity {

    private static final String TAG = "ProcessManagerActivity";
    private TextView tv_runing;
    private TextView tv_total_pro;
    private TextView tv_used_memo;
    private TextView tv_vaild_memo;
    private StickyListHeadersListView lv_process;
    private List<ProcessBean> listData;
    private ProcessAdpter mAdpter;
    private RelativeLayout rl_loading;
    private ImageView iv_clean;
    private List<ProcessBean> mlistUser;
    private List<ProcessBean> mlistSystem;
    private long memoryCount;
    private int processRunning;
    private int processtotal;
    private ImageView iv_arrow_2;
    private ImageView iv_arrow_1;
    private SlidingDrawer sd_pull;
    private ObjectAnimator animator_2;
    private ObjectAnimator animator_1;
    private SettingItemView siv_auto_clean;
    private SettingItemView siv_show_sysprp;
    private boolean show_sys_process;
    private boolean auto_clean_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processmanager);

        //初始化VIew
        initView();

        //初始化动画
        initAnim();

        //初始化数据
        initData();

        //初始化事件的监听
        initEvent();
    }


    private void initAnim() {

        /*iv_arrow_1的动画*/
        animator_1 = ObjectAnimator.ofFloat(iv_arrow_1, "alpha", 0, 1f);
        animator_1.setDuration(600);
        animator_1.setRepeatCount(ObjectAnimator.INFINITE);
        animator_1.setRepeatMode(ObjectAnimator.REVERSE);
        animator_1.start();
        /*iv_arrow_2的动画*/
        animator_2 = ObjectAnimator.ofFloat(iv_arrow_2, "alpha", 1f, 0);
        animator_2.setDuration(600);
        animator_2.setRepeatCount(ObjectAnimator.INFINITE);
        animator_2.setRepeatMode(ObjectAnimator.REVERSE);
        animator_2.start();
    }

    private void initEvent() {
        mAdpter = new ProcessAdpter();
        lv_process.setAdapter(mAdpter);

        //显示系统进程事件的监听，关闭的时候，不显示系统监听
        siv_show_sysprp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_sys_process = !show_sys_process;
                siv_show_sysprp.setToggleState(show_sys_process);
                PreferenceUtils.putBoolean(ProcessManagerActivity.this, Constants.SHOW_SYS_PROCESS, show_sys_process);
                Log.i(TAG, "显示系统进程事件的监听：" + show_sys_process);

                mAdpter.notifyDataSetChanged();
            }
        });
        //锁屏自动清理
        siv_auto_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto_clean_lock = !auto_clean_lock;
                siv_auto_clean.setToggleState(auto_clean_lock);
                PreferenceUtils.putBoolean(ProcessManagerActivity.this, Constants.AUTO_CLEAN_LOCK, auto_clean_lock);
                Log.i(TAG, "锁屏自动清理状态：" + auto_clean_lock);

                /*判断当前的用户是否设置了锁屏自动清理功能*/
                if (auto_clean_lock) {
                    /*开启服务，时刻监听用户锁屏的广播*/
                    Intent intent = new Intent(ProcessManagerActivity.this, AutoCleanService.class);
                    startService(intent);
                }else {
                    Intent intent = new Intent(ProcessManagerActivity.this, AutoCleanService.class);
                    stopService(intent);

                }

            }
        });

        //监听抽屉的打开
        sd_pull.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                iv_arrow_1.setImageResource(R.drawable.drawer_arrow_down);
                iv_arrow_2.setImageResource(R.drawable.drawer_arrow_down);

                //在低版本中是不兼容的
                iv_arrow_1.setAlpha(1f);
                iv_arrow_2.setAlpha(1f);

                animator_1.cancel();
                animator_2.cancel();
            }
        });
        //监听抽屉的关闭
        sd_pull.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                iv_arrow_1.setImageResource(R.drawable.drawer_arrow_up);
                iv_arrow_2.setImageResource(R.drawable.drawer_arrow_up);
                animator_1.start();
                animator_2.start();
            }
        });

        //监听条目被点击事件
        lv_process.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击选择条目
                ProcessBean bean = (ProcessBean) mAdpter.getItem(position);

                //判断如果是自己的程序，则不会被选择
                if (bean.packageName.equals(getPackageName())) {
                    return;
                }
                bean.isSelected = !bean.isSelected;
                mAdpter.notifyDataSetChanged();

            }
        });

        //点击清理
        iv_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                long totalMemory = 0;

                ListIterator<ProcessBean> iterator = null;
                //判断是否展示系统程序
                if (show_sys_process) {
                    iterator = listData.listIterator();
                } else {
                    iterator = mlistUser.listIterator();
                }

                while (iterator.hasNext()) {
                    ProcessBean bean = iterator.next();
                    if (bean.isSelected) {
                        ProcessProvider.cleanProcess(ProcessManagerActivity.this, bean.packageName);
                        //移除集合
                        iterator.remove();

                        //判断当前是否是系统的进程，并将此进程从当前集合中移除
                       if (show_sys_process) {
                           mlistUser.remove(bean);
                       } else {
                           listData.remove(bean);
                       }


                        totalMemory += bean.memory;
                        count++;
                    }
                }
                if (count > 0) {
                    Toast.makeText(ProcessManagerActivity.this, "清理了" + count + "个进程,释放了" +
                            Formatter.formatFileSize(ProcessManagerActivity.this, totalMemory)
                            + "内存", Toast.LENGTH_SHORT).show();
                    processRunning -= count;
                    //重新加载显示内存和进程数量
                    loadProcessAndMemory();
                } else {
                    Toast.makeText(ProcessManagerActivity.this, "没有可以清除的进程", Toast.LENGTH_SHORT).show();
                }
                mAdpter.notifyDataSetChanged();

            }
        });
    }

    private void loadProcessAndMemory() {
        //加载显示内存和进程数


        //获取内存的使用情况
        memoryCount = ProcessProvider.getMemoryCount(ProcessManagerActivity.this);

        //获取手机可用的内存
        long vaildMemomy = ProcessProvider.getVaildMemomy(this);
        //将数据设置到相应的控件中
        tv_runing.setText("正在运行" + processRunning + "个");
        tv_total_pro.setText("总进程" + processtotal + "个");

        //已经使用的内出
        long usedmemo = memoryCount - vaildMemomy;
        tv_used_memo.setText("已使用:" + Formatter.formatFileSize(this, usedmemo));
        tv_vaild_memo.setText("可用:" + Formatter.formatFileSize(this, vaildMemomy));

    }

    //反选的点击按钮
    public void unSelect(View view) {
        Log.i(TAG, "unSelect ");
        //反选
        for (ProcessBean bean : listData) {
            if (bean.packageName.equals(getPackageName())) {
                continue;
            }
            bean.isSelected = bean.isSelected?false:true;
        }
        mAdpter.notifyDataSetChanged();
    }

    //全选的点击按钮
    public void allSelect(View view) {
        Log.i(TAG, "allSelect ");
        //全选所有程序
        for (ProcessBean bean : listData) {
            if (bean.packageName.equals(getPackageName())) {
                continue;
            }
            bean.isSelected = !bean.isSelected;
        }
        mAdpter.notifyDataSetChanged();
    }

    private void initData() {

        //回显用户的配置文件信息
        show_sys_process = PreferenceUtils.getBoolean(ProcessManagerActivity.this, Constants.SHOW_SYS_PROCESS);
        siv_show_sysprp.setToggleState(show_sys_process);

        auto_clean_lock = PreferenceUtils.getBoolean(ProcessManagerActivity.this, Constants.AUTO_CLEAN_LOCK);
        siv_auto_clean.setToggleState(auto_clean_lock);

        //正在运行的进程
        processRunning = ProcessProvider.getProcessRunningCount(ProcessManagerActivity.this);
        //获取进程的总数量
        processtotal = ProcessProvider.getProcessTotal(this);

        //加载内存和进程信息
        loadProcessAndMemory();

        //显示加载进度
        rl_loading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取进程数据信息
                listData = ProcessProvider.getProcessInfo(ProcessManagerActivity.this);

                mlistUser = new ArrayList<ProcessBean>();
                mlistSystem = new ArrayList<ProcessBean>();

                for (ProcessBean bean : listData) {
                    if (bean.isSystem) {
                        mlistSystem.add(bean);
                    } else {
                        mlistUser.add(bean);
                    }
                }
                listData.clear();
                listData.addAll(mlistUser);
                listData.addAll(mlistSystem);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdpter.notifyDataSetChanged();
                        rl_loading.setVisibility(View.GONE);
                    }
                });
            }
        }).start();

    }

    private void initView() {

        tv_runing = (TextView) findViewById(R.id.tv_run_process);
        tv_total_pro = (TextView) findViewById(R.id.tv_process_total);
        tv_used_memo = (TextView) findViewById(R.id.tv_used_memory);
        tv_vaild_memo = (TextView) findViewById(R.id.tv_vaild_memory);
        lv_process = (StickyListHeadersListView) findViewById(R.id.lv_process_list);
        rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
        iv_clean = (ImageView) findViewById(R.id.iv_clean);
        //上拉图标
        iv_arrow_1 = (ImageView) findViewById(R.id.iv_arrow_1);
        iv_arrow_2 = (ImageView) findViewById(R.id.iv_arrow_2);

        sd_pull = (SlidingDrawer) findViewById(R.id.sd_pull);

        //显示系统进程
        siv_show_sysprp = (SettingItemView) findViewById(R.id.siv_show_sysprp);
        //锁屏自动清理
        siv_auto_clean = (SettingItemView) findViewById(R.id.siv_auto_clean);

    }

    private class ProcessAdpter extends BaseAdapter implements StickyListHeadersAdapter {

        @Override
        public int getCount() {
            if (show_sys_process) {
                if (listData != null) {
                    return listData.size();
                }
            } else {
                if (mlistUser != null) {
                    return mlistUser.size();
                }
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (show_sys_process) {
                if (listData != null) {
                    return listData.get(position);
                }
            } else {
                if (mlistUser != null) {
                    return mlistUser.get(position);
                }
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            ViewHodler hodler = null;
            if (convertView == null) {
                convertView = View.inflate(ProcessManagerActivity.this, R.layout.item_pro_manager, null);
                hodler = new ViewHodler();

                hodler.micon = (ImageView) convertView.findViewById(R.id.iv_pro_icon);
                hodler.mTile = (TextView) convertView.findViewById(R.id.tv_proc_name);
                hodler.mMemory = (TextView) convertView.findViewById(R.id.tv_pro_memory);
                hodler.mSelect = (CheckBox) convertView.findViewById(R.id.ck_select);
                convertView.setTag(hodler);
            } else {
                hodler = (ViewHodler) convertView.getTag();
            }
            ProcessBean bean = listData.get(position);
            hodler.micon.setImageDrawable(bean.icon);
            hodler.mMemory.setText("占用内存:" + Formatter.formatFileSize(ProcessManagerActivity.this, bean.memory));
            hodler.mTile.setText(bean.name);

            if (bean.packageName.equals(getPackageName())) {
                hodler.mSelect.setVisibility(View.GONE);
            } else {
                hodler.mSelect.setVisibility(View.VISIBLE);
            }
            hodler.mSelect.setChecked(bean.isSelected ? true : false);

            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {

            //这里也要复用

            if (convertView == null) {
                convertView = new TextView(ProcessManagerActivity.this);
                convertView.setBackgroundColor(Color.parseColor("#FFD0D1D0"));
                convertView.setPadding(8, 16, 16, 8);
            }

            ProcessBean bean = (ProcessBean) getItem(position);
            TextView tv_title = (TextView) convertView;
            tv_title.setText(bean.isSystem ? "系统进程" : "用户进程");
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            ProcessBean bean = (ProcessBean) getItem(position);
            /*判断是否系统进程，使用1和0来标记*/
            return bean.isSystem ? 0 : 1;
        }
    }

    private class ViewHodler {
        ImageView micon;
        TextView mTile;
        TextView mMemory;
        CheckBox mSelect;
    }
}
