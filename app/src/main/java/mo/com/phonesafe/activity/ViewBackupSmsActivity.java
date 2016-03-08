package mo.com.phonesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import mo.com.phonesafe.R;
import mo.com.phonesafe.tools.GZIPUtils;

/**
 * Created by Gh0st on 2016/3/7 007.
 */
public class ViewBackupSmsActivity extends Activity {
    private List<String> mList;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewsms);
        mListView = (ListView) findViewById(R.id.view_sms);
        initData();
        mListView.setAdapter(new ViewSmsAdapter());
        initEvent();
    }

    private void initData() {
        mList = new ArrayList<String>();
        File file = new File(getFilesDir().getAbsolutePath());
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.getName().endsWith(".json")) {
                mList.add(f.getName());
            }
        }
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewBackupSmsActivity.this);
                TextView tv = new TextView(ViewBackupSmsActivity.this);
                File file = new File(getFilesDir().getAbsolutePath() + "/" + mList.get(position));
                BufferedReader bufferedReader = null;
                StringBuffer sb = new StringBuffer();
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                    String linetext = null;
                    while ((linetext = bufferedReader.readLine()) != null) {
                        sb.append(linetext);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    GZIPUtils.closeIO(bufferedReader);
                }
                tv.setText(sb.toString());
                builder.setView(tv);
                builder.show();
            }
        });
    }

    private class ViewSmsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList != null ? mList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mList != null ? mList.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(ViewBackupSmsActivity.this);
            tv.setGravity(Gravity.CENTER);
            tv.setHeight(200);
            tv.setText(mList.get(position));
            return tv;
        }

    }

}
