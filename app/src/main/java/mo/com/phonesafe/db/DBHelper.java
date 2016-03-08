package mo.com.phonesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gh0st on 2015/9/2 22:27
 *
 * 数据库
 */


public class DBHelper extends SQLiteOpenHelper{

    public DBHelper(Context context) {
        super(context, BlackListUtils.DB_NAME, null, BlackListUtils.VERSION);
    }
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BlackListUtils.BlackTable.CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
