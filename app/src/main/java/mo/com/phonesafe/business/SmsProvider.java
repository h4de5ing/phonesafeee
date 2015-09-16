package mo.com.phonesafe.business;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mo.com.phonesafe.bean.SmsBean;

/**
 * 作者：MoMxMo on 2015/9/11 22:27
 * 邮箱：xxxx@qq.com
 *
 *
 * 短信的备份和还原业务
 */


public class SmsProvider {


    private static final String TAG = "SmsProvider";

    /**
     * 短信的还原
     * @param context   上下文
     */
    public static void restore(final Context context, final onRestoreListener listener) {

        new AsyncTask<Void, Integer, Boolean>() {

            //准备进行耗时操作（主线程中）
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //结束耗时操作后调用（主线程中）
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                listener.onResult(aBoolean);
            }

            //当进度改变的回调，在主线程中执行，必须通过publishProgress()方法触发
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);

                /*
                values[0]表示当前进度
                values[1]表示总的短信数量
                 */
                listener.onProgress(values[0],values[1]);

            }

            //做耗时操作（子线程中 ）
            @Override
            protected Boolean doInBackground(Void... params) {

                //获取备份文件中的json数据

                BufferedReader in = null;
                try {
                    //获取流，使用Gson解析json文本
                    File file = new File(context.getFilesDir(),"sms_backup.json");
                    in = new BufferedReader(new FileReader(file));
                    String json = in.readLine();
                    Gson gson = new Gson();
                    List<SmsBean> list = gson.fromJson(json, new TypeToken<List<SmsBean>>() {
                    }.getType());

                    //还原到数据库中
                    ContentResolver resolver = context.getContentResolver();
                    Uri uri = Uri.parse("content://sms");
                    if (list != null && list.size() > 0) {
                        int count = list.size();  //还原短信的总数量
                        int progress = 0;   //还原短信的进度
                        for (SmsBean bean : list) {
                            ContentValues values = new ContentValues();
                            Log.i(TAG, " bean:....." + bean.toString());
                            /*values.put("_id", bean._id);
                            *
                            * String[] projection = {"_id","address","date","body","type"};
                            * */
                            values.put("address",bean.address);
                            values.put("date",bean.date);
                            values.put("body", bean.body);
                            values.put("type",bean.type+"");

                            Uri insert = resolver.insert(uri, values);
                            Log.i(TAG, "doInBackground "+insert.toString());


                            progress++;
                            Thread.sleep(200);

                            //实时更新显示还原进度
                            publishProgress(progress,count);
                        }
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }.execute();

    }


    /**
     * 备份短信
     */
    public static  void backup(final Context context, final OnBackUpListener listener) {

        //这里使用安卓中提供的的线程方式处理（Google建议使用这种方式）

        new AsyncTask<Void, Integer, Boolean > (){
            //准备进行耗时的操作(主线程中执行)
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //执行耗时操做（子线程中执行）
            @Override
            protected Boolean doInBackground(Void... params) {
                //数据的加载到json格式文件中(使用内容提供者获取短信数据)
                ContentResolver resolver = context.getContentResolver();
                Uri uri = Uri.parse("content://sms");
                String[] projection = {"_id","address","date","body","type"};

                Cursor cursor = resolver.query(uri, projection, null, null, null);

                List<SmsBean> list = null;
                int count = 0;  //短信的总数量
                int current = 0;    //当前备份进度

                if (cursor != null) {
                    count = cursor.getCount();
                    list = new ArrayList<SmsBean>();
                    while (cursor.moveToNext()) {
                        SmsBean bean = new SmsBean();
                        bean._id = cursor.getString(0);
                        bean.address = cursor.getString(1);
                        bean.date = cursor.getString(2);
                        bean.body = cursor.getString(3);
                        bean.type = cursor.getInt(4);
                        list.add(bean);

                        //休眠显示进度
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        current++;
                        publishProgress(current,count);
                    }
                    cursor.close();
                }

                /*判断list是否为空*/
                if (list != null && list.size() > 0) {
                    //不为空，将数据写到json格式的文件中
                    Gson gson = new Gson();
                    String json = gson.toJson(list);

                    //保存备份文件的位置在安装包下的files文件夹
                    File file = new File(context.getFilesDir(),"sms_backup.json");

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        fos.write(json.getBytes());
                        fos.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }finally {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return true;
            }

            //当前进度改变的回调，在主线程中执行，必须通过publishProgress();方法触发
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);

                //获取当前进度values[0]表示进度，values[1]表示短信的总数量
                listener.onProgress(values[0], values[1]);
            }

            //结束耗时操作之后调用（主线程中执行）
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);

                //当运行结束后的对调结果
                listener.onResult(aBoolean);
            }
        }.execute();
    }
    //定义接口监听备份中的进度
    public interface OnBackUpListener{

        //当进度运行时的回调
        void onProgress(int progress, int max);

        //当运行结束的回调
        void onResult(boolean result);

    }

    //定义接口，监听短信还原的进度和结果
    public interface onRestoreListener{
        //当前运行时会回调
        void onProgress(int progress, int max);

        //当运行接收的回调
        void onResult(boolean result);


    }
}
