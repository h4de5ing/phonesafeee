package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.ContactInfo;
import mo.com.phonesafe.tools.ContactUtils;

/**
 * Created by Gh0st on 2015/8/31 19:59
 * <p/>
 * 显示用户联系人的Activity
 * <p/>
 * 优化的版本  专门应用于大数据量的访问的时候，使用这个获取数据是比较能减少消耗内存的
 */


public class ContactActivity2 extends Activity implements AdapterView.OnItemClickListener {

    public static final String KEY_NUMBER = "key_number";
    private static final String TAG = "ContactActivity2";
    ListView lv_contact;
    Cursor cursor;
    ProgressBar pb_contact;
    ContactCursorAdapter mContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_contact);
        initView();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mContactAdapter = new ContactCursorAdapter(this, cursor);
        lv_contact.setAdapter(mContactAdapter);
        pb_contact.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                cursor = ContactUtils.getAllCursor(ContactActivity2.this);
                if (cursor == null) {
                    return;
                }
                //使用这个方法在主线程中显示UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb_contact.setVisibility(ProgressBar.GONE);
                        mContactAdapter.changeCursor(cursor);
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
        cursor.moveToPosition(position);
        ContactInfo info = ContactUtils.getContactInfo(cursor);

        Intent data = new Intent();
        data.putExtra(KEY_NUMBER, info.number);
        setResult(Activity.RESULT_OK, data);
        finish();
    }


    /**
     * 手机联系人的适配器
     */
    private class ContactCursorAdapter extends CursorAdapter {

        public ContactCursorAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            return View.inflate(ContactActivity2.this, R.layout.item_contact, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_contact_icon);
            TextView tvName = (TextView) view.findViewById(R.id.tv_contact_name);
            TextView tvNumber = (TextView) view.findViewById(R.id.tv_contact_number);

            ContactInfo info = ContactUtils.getContactInfo(cursor);
            tvName.setText(info.name);
            tvNumber.setText(info.number);

            Bitmap bitmap = ContactUtils.getContactBitmap(ContactActivity2.this, info.contactId);
            if (bitmap != null) {
                ivIcon.setImageBitmap(bitmap);
            } else {
                ivIcon.setImageResource(R.mipmap.vc);
            }
        }
    }


}
