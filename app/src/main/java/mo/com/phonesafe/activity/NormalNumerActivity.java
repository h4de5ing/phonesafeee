package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import mo.com.phonesafe.R;
import mo.com.phonesafe.dao.CommonNumberDao;

/**
 * Created by Gh0st on 2015/9/7 20:04
 */


public class NormalNumerActivity extends Activity {

    private ExpandableListView nor_listview;
    private int mCurrentOpenPostion = -1;  //标记当前展开的group

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_number_query);
        initView();
        initEvent();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NormalNumerActivity.this,CommonToolActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
        super.onBackPressed();
    }

    private void initView() {
        nor_listview = (ExpandableListView) findViewById(R.id.el_normal_number);
    }

    private void initEvent() {
        nor_listview.setAdapter(new NormalNumAdapter());


        //监听Group
        nor_listview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                //是否是第一次点击
                if (mCurrentOpenPostion == -1) {
                    //是
                    //展开当前被点击的对象,并标记当前展开的位置currentPosition
                    nor_listview.expandGroup(groupPosition);
                    mCurrentOpenPostion = groupPosition;
                } else {
                    //不是
                    if (groupPosition == mCurrentOpenPostion) {
                        /*判断被点击的是不是和上一次点击的位置相同*/
                        /*相同，关闭当前位置，currentPosition标志味为-1*/
                        nor_listview.collapseGroup(groupPosition);
                        mCurrentOpenPostion = -1;
                    } else {

                     /*不相同，关闭当前位置，展开position,并重新标记*/
                        nor_listview.collapseGroup(mCurrentOpenPostion);
                        nor_listview.expandGroup(groupPosition);
                        mCurrentOpenPostion = groupPosition;
                    }

                }
                return true;
            }
        });

        //监听Child  点击之后拨打电话
        nor_listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                String content = CommonNumberDao.getChildContent(NormalNumerActivity.this, groupPosition, childPosition);
                String[] split = content.split("\n");
                String number = split[1];

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+number));
                startActivity(intent);
                return false;
            }
        });
    }

    /**
     * 扩展listView的适配器
     */
    private class NormalNumAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return CommonNumberDao.getGroupCount(NormalNumerActivity.this);
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return CommonNumberDao.getChildrenConut(NormalNumerActivity.this, groupPosition);

        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder holder = null;
            if (convertView == null) {
                convertView = new TextView(NormalNumerActivity.this);
                holder = new GroupViewHolder();
                holder.tv_tile = (TextView) convertView;
                holder.tv_tile.setTextSize(20);
                holder.tv_tile.setTextColor(Color.parseColor("#44A1F7"));
                holder.tv_tile.setPadding(45, 45, 45, 45);

                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }
            String content = CommonNumberDao.getGroupContent(NormalNumerActivity.this, groupPosition);
            holder.tv_tile.setText(content);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder = null;
            if (convertView == null) {
                convertView = new TextView(NormalNumerActivity.this);
                holder = new ChildViewHolder();
                holder.tv_tile = (TextView) convertView;
                holder.tv_tile.setTextSize(18);
                holder.tv_tile.setTextColor(Color.parseColor("#B9C2C5"));
                holder.tv_tile.setPadding(40, 40, 40, 40);
                convertView.setTag(holder);
            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }
            String content = CommonNumberDao.getChildContent(NormalNumerActivity.this,groupPosition, childPosition);
            holder.tv_tile.setText(content);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    static class GroupViewHolder {
        TextView tv_tile;
    }
    static class ChildViewHolder {
        TextView tv_tile;
    }
}
