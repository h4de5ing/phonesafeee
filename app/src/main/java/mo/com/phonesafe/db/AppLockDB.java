package mo.com.phonesafe.db;

/**
 * 作者：MoMxMo on 2015/9/13 09:24
 * 邮箱：xxxx@qq.com
 */


public interface AppLockDB {

    /*数据库名*/
    String APPLOCK_DATABASE_NAME = "applock.db";

    /*版本号*/
    int VERSION = 1;

    /*表*/
    interface ApplockTable{
        /*表名*/
        String TABL_NAME = "applock";

        String COLUMN_ID = "_id";

        String PACKE_NAME = "packa_name";

        String TABLE_SQL = "create table " + TABL_NAME + "("
                 +COLUMN_ID+ " integer primary key autoincrement,"
                +PACKE_NAME+" text unique)";
    }

}
