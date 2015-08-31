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


public class ContactActivity extends Activity implements AdapterView.OnItemClickListener{

    public static final String KEY_NUMBER = "key_number";
    private static final String TAG = "ContactActivity";
    ListView lv_contact;
    List<ContactInfo> list_contact;

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
        lv_contact.setAdapter(new ContactAdater());
    }

    /**
     * 初始化view
     */

    private void initView() {

        //获取手机联系人
        list_contact = ContactUtils.getAllPhone(this);
        lv_contact = (ListView) findViewById(R.id.lv_contact);

        // 设置点击事件
        lv_contact.setOnItemClickListener(this);
    }


    /**
     * 监听联系人条目点击事件，将数据返回给启动者
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
            View view = null;
            ViewHolder vHolder = null;
            if (convertView == null) {
                //加载联系人条目
                view = View.inflate(ContactActivity.this, R.layout.item_contact, null);

                vHolder = new ViewHolder();
                //将获得到的对象放到Holder中，并将其标记到converView中
                vHolder.ivIcon = (ImageView) view.findViewById(R.id.iv_contact_icon);
                vHolder.tvName = (TextView) view.findViewById(R.id.tv_contact_name);
                vHolder.tvNumber = (TextView) view.findViewById(R.id.tv_contact_number);

                view.setTag(vHolder);

            } else {
                view = convertView;
                vHolder = (ViewHolder) view.getTag();
            }

            ContactInfo conInfo = list_contact.get(position);

            vHolder.tvName.setText(conInfo.name);
            vHolder.tvNumber.setText(conInfo.number);
            Bitmap bitmap = ContactUtils.getContactBitmap(ContactActivity.this, conInfo.contactId);

            if (bitmap != null) {

//                获取到联系人的头像
//                显示
                vHolder.ivIcon.setImageBitmap(bitmap);
            }else{
                vHolder.ivIcon.setImageResource(R.mipmap.vc);
            }
            return view;
        }

        @Override
        public int getCount() {
            if (list_contact.size() > 0) {
                return list_contact.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (list_contact.size() > 0) {
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
