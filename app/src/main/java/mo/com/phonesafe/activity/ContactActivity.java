package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.ContactInfo;
import mo.com.phonesafe.tools.ContactUtils;

/**
 * 作者：MoMxMo on 2015/8/31 19:59
 * 邮箱：momxmo@qq.com
 * <p/>
 * 显示用户联系人的Activity
 */


public class ContactActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final String KEY_NUMBER = "key_number";
    private static final String TAG = "ContactActivity";
    ListView lv_contact;
    List<ContactInfo> list_contact;
    ProgressBar pb_contact;
    ContactAdater mContactAdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_contact);

        //初始化view
        initView();

        //初始化数据
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mContactAdater = new ContactAdater();


        //设置适配器
        lv_contact.setAdapter(mContactAdater);
        //显示监督条
        pb_contact.setVisibility(View.VISIBLE);

//        获取数据的优化,在主线程中获取资源太消耗时间，我们使用子线程进行操作
        new Thread(new Runnable() {
            @Override
            public void run() {

                //获取手机联系人
                list_contact = ContactUtils.getAllPhone(ContactActivity.this);

                //当用户进入的时候显示进度

                //使用这个方法在主线程中显示UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //关闭进度条
                        pb_contact.setVisibility(ProgressBar.GONE);

                        // 更新adapter--->UI
                        mContactAdater.notifyDataSetChanged();
                    }
                });
            }
        }
        ).start();


    }

    /**
     * 初始化view
     */

    private void initView() {
        lv_contact = (ListView) findViewById(R.id.lv_contact);
        pb_contact = (ProgressBar) findViewById(R.id.pb_contact);
        // 设置点击事件
        lv_contact.setOnItemClickListener(this);
    }


    /**
     * 监听联系人条目点击事件，将数据返回给启动者
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.i(TAG, "onItemClick" + position);
        ContactInfo info = list_contact.get(position);

        Intent data = new Intent();
        data.putExtra(KEY_NUMBER, info.number);

        setResult(Activity.RESULT_OK, data);

        //关闭当前页面
        finish();
    }


    /**
     * 优化ListView中的findById，减少创建的
     */
    static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvNumber;
    }

    /**
     * 手机联系人的适配器
     */
    private class ContactAdater extends BaseAdapter {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vHolder = null;
            if (convertView == null) {
                //加载布局条目，希望能看得懂
                convertView = View.inflate(ContactActivity.this, R.layout.item_contact, null);
                vHolder = new ViewHolder();
                //将获得到的对象放到Holder中，并将其标记到converView中
                vHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_contact_icon);
                vHolder.tvName = (TextView) convertView.findViewById(R.id.tv_contact_name);
                vHolder.tvNumber = (TextView) convertView.findViewById(R.id.tv_contact_number);
                //添加标记
                convertView.setTag(vHolder);
            } else {
                //获取标记
                vHolder = (ViewHolder) convertView.getTag();
            }
            //获取list集合中的数据
            ContactInfo conInfo = list_contact.get(position);

            vHolder.tvName.setText(conInfo.name);
            vHolder.tvNumber.setText(conInfo.number);
            Bitmap bitmap = ContactUtils.getContactBitmap(ContactActivity.this, conInfo.contactId);

            if (bitmap != null) {
                vHolder.ivIcon.setImageBitmap(bitmap);
            } else {
                vHolder.ivIcon.setImageResource(R.mipmap.vc);
            }
            return convertView;
        }
        @Override
        public int getCount() {
            if (list_contact != null) {
                return list_contact.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (list_contact != null) {
                return list_contact.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }


}
