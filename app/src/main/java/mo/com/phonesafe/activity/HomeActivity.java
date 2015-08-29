package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.HomeBean;

/**
 * 作者：momxmo on 2015/8/28 19:06
 * 邮箱：xxxx@qq.com
 */


public class HomeActivity extends Activity{
    ImageView home_icon;
    GridView home_gridview;
    List<HomeBean>  mDatas ;


    private final static String[] TITLES = new String[] { "手机防盗", "骚扰拦截",
            "软件管家", "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具" };
    private final static String[] DESCS = new String[] { "远程定位手机", "全面拦截骚扰",
            "管理您的软件", "管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全" };
    private final static int[] ICONS = new int[] {
            R.mipmap.lj,R.mipmap.ac,
            R.mipmap.oh,R.mipmap.mm,
            R.mipmap.nm,R.mipmap.ph,
            R.mipmap.qh,R.mipmap.bn,
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityi_home);
        //初始化View
        initView();

        //加载数据
        initDate();
    }

    /**
     * 加载数据
     */
    private void initDate() {

        mDatas = new ArrayList<HomeBean>();
        for(int i = 0 ;i<ICONS.length;i++){
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
     * @param view
     */
    public void  clickSetting(View view){
        Intent intent = new Intent(HomeActivity.this,SettingActivity.class);

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
     *主界面的适配器
     */
    private class HomeAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if(mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if(mDatas != null) {
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
            if(convertView == null){
                convertView = View.inflate(HomeActivity.this,R.layout.item_home,null);
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
