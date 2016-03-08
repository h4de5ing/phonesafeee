package mo.com.phonesafe.db;

/**
 * Created by Gh0st on 2015/9/2 22:28
 */


public interface BlackListUtils {

    /**
     * 数据库的名称
     */
    String DB_NAME = "black.db";

    /**
     * 版本号
     */
    int VERSION = 1;

    interface BlackTable {

        /**
         * 表名
         */
        String TABLE_NAME = "black";

        String COLUMN_ID = "_id";
        String COLUMN_NUMBER = "number";        //号码
        String COLUMN_TYPE = "type";    //拦截的类型 1电话  2短信  3全部

        /**
         * SQL创建表的语句
         */
        String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "(" +
                COLUMN_ID + " integer primary key autoincrement," +
                COLUMN_NUMBER + " text unique," +
                COLUMN_TYPE + " integer)";
    }

}
