package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.BlackBean;
import mo.com.phonesafe.dao.BlackDao;

/**
 * 作者：MoMxMo on 2015/8/31 19:59
 * 邮箱：momxmo@qq.com
 * <p/>
 * 黑名单管理的Activity
 */


public class BlackManagerActivity extends Activity {

    private static final String TAG = "BlackManagerActivity";
    private ImageView lm_ivon;
    private ListView bm_list;
    private List<BlackBean> listdata;
    private BlackListAdater listAdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackmanager);

        //初始化view
        initView();

//        初始化event
        initEvent();

        //初始化数据
        initData();
    }

    private void initEvent() {
        lm_ivon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*进入黑名单添加界面*/
                Intent intent = new Intent(BlackManagerActivity.this, BlackAddActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void initData() {
        BlackDao blackDao = new BlackDao(BlackManagerActivity.this);

        //获取所有的黑名单
        listdata = blackDao.queryAll();

        //避免删除的时候  重新常见Adapter
        if (listAdater == null) {
            listAdater = new BlackListAdater();
            bm_list.setAdapter(listAdater);
        } else {
            listAdater.notifyDataSetChanged();
        }
    }

    private void initView() {
        lm_ivon = (ImageView) findViewById(R.id.iv_lm_icon);
        bm_list = (ListView) findViewById(R.id.lv_bm_list);

    }

    private class BlackListAdater extends BaseAdapter {

        @Override
        public int getCount() {
            if (listdata.size()>0) {
                return listdata.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (listdata.size() > 0) {
                return listdata.get(position);
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
                convertView = View.inflate(BlackManagerActivity.this, R.layout.item_black, null);

                holder = new ViewHolder();
                holder.item_number = (TextView) convertView.findViewById(R.id.item_black_number);
                holder.item_type = (TextView) convertView.findViewById(R.id.item_black_type);
                holder.item_delete_icon = (ImageView) convertView.findViewById(R.id.item_iv_delete);
                convertView.setTag(holder);
            }

            holder = (ViewHolder) convertView.getTag();

            final BlackBean blackBean = listdata.get(position);


            holder.item_number.setText(blackBean.number);
            switch (blackBean.type) {
                case BlackBean.TYPE_CALL:
                    holder.item_type.setText("电话");
                    break;
                case BlackBean.TYPE_SMS:
                    holder.item_type.setText("短信");
                    break;
                case BlackBean.TYPE_ALL:
                    holder.item_type.setText("电话 + 短信");
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
                    BlackDao dao = new BlackDao(BlackManagerActivity.this);
                    boolean delete = dao.delete(blackBean.number);
                    Log.i(TAG, "onClick delete itme: number-->"+blackBean.number);
                    if (delete) {
                        Toast.makeText(BlackManagerActivity.this, "删除成功", Toast.LENGTH_SHORT).show();

                        //通知list数据已经发生改变
                        initData();

                    } else {
                        Toast.makeText(BlackManagerActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return convertView;
        }
    }

    /**
     * 优化ListView中的findById，减少创建的
     */
    static class ViewHolder {
        TextView item_number;
        TextView item_type;
        ImageView item_delete_icon;
    }



}
