package mo.com.phonesafe.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import mo.com.phonesafe.R;

/**
 * 自定义号码归属地Dialog
 *
 * 作者：MoMxMo on 2015/9/7 09:28
 * 邮箱：xxxx@qq.com
 *
 */


public class AddressStyleDialog extends Dialog{
    private ListAdapter mAdapter;
    private ListView lv_address_style;
    private OnItemClickListener mListener;
    public AddressStyleDialog(Context context) {
        super(context, R.style.AddressStyleDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window mWindow = getWindow();
        WindowManager.LayoutParams mParams = mWindow.getAttributes();
        mParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        mWindow.setAttributes(mParams);

        setContentView(R.layout.dialog_address_sytle);
        lv_address_style = (ListView) findViewById(R.id.lv_dialog_address);

        if (mAdapter != null) {
            lv_address_style.setAdapter(mAdapter);
        }
        if (lv_address_style != null) {
            lv_address_style.setOnItemClickListener(mListener);
        }
    }
    public void setAdapter(ListAdapter adapter) {
        mAdapter = adapter;
        if (lv_address_style!=null) {
            lv_address_style.setAdapter(adapter);
        }
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
        if (lv_address_style != null) {
            lv_address_style.setOnItemClickListener(listener);
        }
    }
}
