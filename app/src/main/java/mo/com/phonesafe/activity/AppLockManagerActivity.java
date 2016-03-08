package mo.com.phonesafe.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.AppBean;
import mo.com.phonesafe.business.AppProvider;
import mo.com.phonesafe.dao.AppLockDao;
import mo.com.phonesafe.view.SegementView;

/**
 * Created by Gh0st on 2015/9/12 15:42
 */


public class AppLockManagerActivity extends Activity {

    private static final String TAG = "AppLockManagerActivity";
    private ListView lv_unlock;
    private ListView lv_lock;
    private SegementView sv_lock;

    private List<AppBean> listDataLock;
    private List<AppBean> listDataUnLock;
    private RelativeLayout rl_loading;
    private AppLockAdatper mLockAdatper;
    private AppLockAdatper mUnlockAdatper;
    private AppLockDao mDao;
    private TextView lock_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock_manager);
        initView();
        initData();
        initEvent();
    }

    private void initData() {

        mLockAdatper = new AppLockAdatper(true);
        mUnlockAdatper = new AppLockAdatper(false);
        lv_lock.setAdapter(mLockAdatper);
        lv_unlock.setAdapter(mUnlockAdatper);

        mDao = new AppLockDao(AppLockManagerActivity.this);

        /*使用线程获取数据*/
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //显示加载进入，隐藏listView
                rl_loading.setVisibility(View.VISIBLE);
                lock_title.setVisibility(View.GONE);

                /*设置不能点击*/
                sv_lock.setClickable(false);
            }

            @Override
            protected Void doInBackground(Void... params) {
                //获取所有有运行图标的App程序信息列表
                List<AppBean> list = AppProvider.getAllLauncherApps(AppLockManagerActivity.this);

                listDataLock = new ArrayList<AppBean>();
                listDataUnLock = new ArrayList<AppBean>();
                for (AppBean bean : list) {
                    if (mDao.findLock(bean.packageName)) {
                        //程序被添加了锁
                        listDataLock.add(bean);
                    } else {
                        //没有添加锁的App
                        listDataUnLock.add(bean);
                    }
                }
                Log.i(TAG, "listDataLock Size : "+listDataLock.size());
                Log.i(TAG, "listDataUnLock Size : "+listDataUnLock.size());
                for (AppBean bena : listDataUnLock) {
                    Log.i(TAG, "------------ : "+bena.toString());

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //加载数据完成,隐藏加载进出，显示ListView
                rl_loading.setVisibility(View.GONE);
                lock_title.setText("已上锁(" + listDataLock.size() + ")");
                lock_title.setVisibility(View.VISIBLE);

                mLockAdatper.notifyDataSetChanged();
                mUnlockAdatper.notifyDataSetChanged();
                /*设置能点击*/
                sv_lock.setClickable(true);
            }

        }.execute();


    }

    /**
     * 初始化View
     */
    private void initView() {

        sv_lock = (SegementView) findViewById(R.id.sv_lock);
        lv_lock = (ListView) findViewById(R.id.lv_lock);
        lv_unlock = (ListView) findViewById(R.id.lv_unlock);
        rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
        lock_title = (TextView) findViewById(R.id.tv_app_lock_title);
    }

    private void initEvent() {
        //设置点击上锁和未上锁的监听事件
        sv_lock.setOnSegementSelectListener(new SegementView.OnSegementSelectListener() {
            @Override
            public void onSelected(boolean locked) {
                if (locked) {
                    /*通知数据的改变*/
                    mLockAdatper.notifyDataSetChanged();
                    //上锁
                    lv_lock.setVisibility(View.VISIBLE);
                    lv_unlock.setVisibility(View.GONE);
                    lock_title.setText("已上锁(" + listDataLock.size() + ")");

                    /*加载数据的时候不能点击事件*/
                } else {
                    mUnlockAdatper.notifyDataSetChanged();
                    //未上锁
                    lv_lock.setVisibility(View.GONE);
                    lv_unlock.setVisibility(View.VISIBLE);
                    lock_title.setText("未上锁(" + listDataUnLock.size() + ")");

                }
                Log.i(TAG, "onSelected 是否上锁："+locked);
            }
        });


    }

    /**
     * 显示App的上锁和未上锁的适配器
     */
    private class AppLockAdatper extends BaseAdapter {
        private boolean mIsLock;

        public AppLockAdatper(boolean isLock) {
            this.mIsLock = isLock;
        }

        @Override
        public int getCount() {
            if (mIsLock) {
                if (listDataLock != null) {
                   return listDataLock.size();
                }
            } else {
                if (listDataUnLock != null) {
                    return listDataUnLock.size();
                }
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mIsLock) {
                if (listDataLock != null) {
                   return listDataLock.get(position);
                }
            } else {
                if (listDataUnLock != null) {
                   return listDataUnLock.get(position);
                }
            }
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
                convertView = View.inflate(AppLockManagerActivity.this, R.layout.item_app_lock, null);
                holder = new ViewHolder();
                holder.mIcon = (ImageView) convertView.findViewById(R.id.iv_item_applock_icon);
                holder.mAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.mLock = (ImageView) convertView.findViewById(R.id.lv_item_lock);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final AppBean bean = (AppBean) getItem(position);
            holder.mIcon.setImageDrawable(bean.icon);
            holder.mAppName.setText(bean.name);
            if (mIsLock) {
                holder.mLock.setImageResource(R.drawable.app_unlock_selector);
            } else {
                holder.mLock.setImageResource(R.drawable.app_lock_selector);
            }

            final View view = convertView;
            //监听点击枷锁图标的事件
            holder.mLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    //设置标记，防止点击过导致的Bug
                    Object tag = v.getTag();

                    if (tag!=null && (Boolean)tag==true) {
                        return;
                    }
                    if (mIsLock) {
                        //设置解锁的动画
                        //平移动画
                        TranslateAnimation ta = new TranslateAnimation(
                                Animation.RELATIVE_TO_PARENT,0,
                                Animation.RELATIVE_TO_PARENT,-1,
                                Animation.RELATIVE_TO_PARENT,0,
                                Animation.RELATIVE_TO_PARENT,0
                        );
                        /*设置动画执行时间*/
                        ta.setDuration(300);
                        //动画执行的时的监听
                        ta.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                v.setTag(true);

                            }
                            //执行删除操作
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                //在已上锁的界面
                                //点击了解锁按钮
                                //将数据从数据库中删除
                                boolean delete = mDao.delete(bean.packageName);
                                if (delete) {
                                    listDataLock.remove(bean);
                                    listDataUnLock.add(bean);
                                    lock_title.setText("已上锁(" + listDataLock.size() + ")");

                                    mUnlockAdatper.notifyDataSetChanged();
                                    mLockAdatper.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(AppLockManagerActivity.this, "解锁失败", Toast.LENGTH_SHORT).show();
                                }
                                v.setTag(false);
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        /*对指定控件执行动画*/
                        view.setAnimation(ta);

                    } else {
                        //设置上锁的动画
                        //平移动画
                        TranslateAnimation ta = new TranslateAnimation(
                                Animation.RELATIVE_TO_PARENT,0,
                                Animation.RELATIVE_TO_PARENT,1,
                                Animation.RELATIVE_TO_PARENT,0,
                                Animation.RELATIVE_TO_PARENT,0
                        );
                        /*动画执行事件*/
                        ta.setDuration(300);
                        //监听动画执行
                        ta.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                v.setTag(true);
                            }
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                //在未上锁的界面
                                //点击进行枷锁
                                //将数据添加到数据库中，并通知Adapter
                                boolean insert = mDao.insert(bean.packageName);
                                if (insert) {
                                    listDataLock.add(bean);
                                    listDataUnLock.remove(bean);
                                    lock_title.setText("未上锁(" + listDataUnLock.size() + ")");
                                    mUnlockAdatper.notifyDataSetChanged();
                                    mLockAdatper.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(AppLockManagerActivity.this, "上锁失败", Toast.LENGTH_SHORT).show();
                                }
                                v.setTag(false);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        /*动画执行*/
                        view.startAnimation(ta);
                    }
                }
            });
            return convertView;
        }
    }
    private static class ViewHolder {
        ImageView mIcon;
        ImageView mLock;
        TextView mAppName;
    }
}
