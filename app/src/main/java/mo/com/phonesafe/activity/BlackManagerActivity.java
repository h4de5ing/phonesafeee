package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.BlackBean;
import mo.com.phonesafe.dao.BlackDao;

/**
 * Created by Gh0st on 2015/8/31 19:59
 * 黑名单管理的Activity
 */


public class BlackManagerActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final String BLACK_NUMBER = "black_number";
    public static final String BLACK_TYPE = "black_type";
    private static final int REQUEST_CODE_UPDATE = 100;
    private ImageView lm_icon;
    private ListView bm_list;
    private List<BlackBean> mListData;
    private BlackListAdapter listAdapter;
    private LinearLayout pb_load;
    private BlackDao blackDao;
    private int pageSize = 20;
    private boolean isLoading = false;
    private boolean hasMore = true;    //是否有更多数据加载

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackmanager);
        initView();
        initData();
        initEvent();

    }

    private void initView() {
        lm_icon = (ImageView) findViewById(R.id.iv_lm_icon);
        bm_list = (ListView) findViewById(R.id.lv_bm_list);
        pb_load = (LinearLayout) findViewById(R.id.pb_bm_load);

        /*设置加载进度的样式图片*/

    }

    private void initData() {

        //避免删除的时候  重新常见Adapter
        if (listAdapter == null) {
            listAdapter = new BlackListAdapter();
            bm_list.setAdapter(listAdapter);
        } else {
            listAdapter.notifyDataSetChanged();
        }
        blackDao = new BlackDao(BlackManagerActivity.this);

        //显示加载的
        pb_load.setVisibility(View.VISIBLE);

        /*使用线程去加载数据*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //获取所有的黑名单
                // mListData = blackDao.queryAll();

                //分页参训
                mListData = blackDao.querySize(pageSize, 0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //主线程中操作UI
                        pb_load.setVisibility(View.GONE);
                        listAdapter.notifyDataSetChanged();
                        //当listview为空的时候，可以显示指定的View
                        bm_list.setEmptyView(findViewById(R.id.iv_bm_icon_empty));
                        //刷新完数据之后
                        isLoading = false;
                    }
                });
            }
        }).start();


    }

    private void initEvent() {
        lm_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*进入黑名单添加界面*/
                Intent intent = new Intent(BlackManagerActivity.this, BlackAddActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //listView 点击事件
        bm_list.setOnItemClickListener(this);

        //listView的滑动监听事件
        bm_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             * 一值在滑动
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 当 滚动时的回调
                // firstVisibleItem: 第一个可见的item的position
                // visibleItemCount:可见的item的数量
                // totalItemCount:item的总数量
                //加载更多
                loadMore();
            }
        });


    }

    private void loadMore() {
        if (mListData == null) {//数据为空的时候，禁止滑动刷新
            return;
        }

        if (!hasMore) {
            return;
        }

        if (isLoading) { //判断是否正在加载，如果正在加载，则，不进入
            return;
        }
        int currentlast = bm_list.getLastVisiblePosition();    //当前listview最后的位置是

        //当滑动到底部的时候（底部位置可见的时候）
        if (currentlast == listAdapter.getCount() - 1) {

            //显示加载进度
            pb_load.setVisibility(View.VISIBLE);

            isLoading = true;   //正在加载
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //分页查询加载黑名单
                    final List<BlackBean> list = blackDao.querySize(pageSize, mListData.size());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            /*判断还有没有更多数据*/
                            if (list.size() < pageSize) {
                                //Toast.makeText(BlackManagerActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                                hasMore = false;
                            }

                            mListData.addAll(list);
                            //主线程中操作UI
                            pb_load.setVisibility(View.GONE);
                            listAdapter.notifyDataSetChanged();
                            //当listview为空的时候，可以显示指定的View
                            bm_list.setEmptyView(findViewById(R.id.iv_bm_icon_empty));
                        }
                    });

                    //加载完毕
                    isLoading = false;
                }
            }).start();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Log.i(TAG, "onItemSelected 当前点击的是：" + position);
        //点击进入更新界面
        Intent intent = new Intent(BlackManagerActivity.this, BlackUpdateActivity.class);
        BlackBean blackBean = mListData.get(position);
        intent.putExtra(BLACK_NUMBER, blackBean.number);
        intent.putExtra(BLACK_TYPE, blackBean.type);

        //使用result放回的方式，将更改的数据从在内存中添加，不用再去搜索数据库
        startActivityForResult(intent, REQUEST_CODE_UPDATE);
    }

    /**
     * 获取更新的值，添加到listdata中，不用再去查询数据库
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mListData = blackDao.queryAll();
        if (mListData != null) {
            listAdapter.notifyDataSetChanged();
        }
        //listAdapter.notifyDataSetChanged();
    }

    private class BlackListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mListData != null) {
                return mListData.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mListData != null) {
                return mListData.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(BlackManagerActivity.this, R.layout.item_black, null);

                holder = new ViewHolder();
                holder.item_number = (TextView) convertView.findViewById(R.id.item_black_number);
                holder.item_type = (TextView) convertView.findViewById(R.id.item_black_type);
                holder.item_delete_icon = (ImageView) convertView.findViewById(R.id.item_iv_delete);
                convertView.setTag(holder);
            }

            holder = (ViewHolder) convertView.getTag();

            final BlackBean blackBean = mListData.get(position);


            holder.item_number.setText(blackBean.number);
            switch (blackBean.type) {
                case BlackBean.TYPE_CALL:
                    holder.item_type.setText(getString(R.string.blacktypephone)); //电话
                    break;
                case BlackBean.TYPE_SMS:
                    holder.item_type.setText(getString(R.string.blacktypesms)); //短信
                    break;
                case BlackBean.TYPE_ALL:
                    holder.item_type.setText(getString(R.string.blacktypeall)); //电话加短信
                    break;
                default:
                    break;
            }

            /**
             * 点击删除事件
             */
            holder.item_delete_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean delete = blackDao.delete(blackBean.number);
                    if (delete) {
                        Toast.makeText(BlackManagerActivity.this, getString(R.string.deletesuccess), Toast.LENGTH_SHORT).show();
                        mListData.remove(position);
                        List<BlackBean> list = blackDao.querySize(1, listAdapter.getCount());
                        mListData.addAll(list);
                        listAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(BlackManagerActivity.this, getString(R.string.deletefaild), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return convertView;
        }
    }


    static class ViewHolder {
        TextView item_number;
        TextView item_type;
        ImageView item_delete_icon;
    }

}
