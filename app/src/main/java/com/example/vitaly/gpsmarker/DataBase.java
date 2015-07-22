package com.example.vitaly.gpsmarker;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase {

    private static final String DB_NAME = "Marker";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "Marker_tab";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_LOC = "Loc";

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_NAME + " txt, " +
                    COLUMN_LOC + " text" +
                    ");";

    private final Context mCtx;

    private DBHelper mDBHelper;

    private SQLiteDatabase mDB;

    public DataBase(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    // добавить запись в DB_TABLE
    public void addRec(String name, String loc) {

        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_LOC, loc);

        mDB.insert(DB_TABLE, null, cv);
    }

    //получить данные об одной точке в виде массива строк {id,name,loc}
    public String [] getPoint (long id){

        Cursor cursor = mDB.query(DB_TABLE,new String[]{COLUMN_ID,COLUMN_NAME,COLUMN_LOC},COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},null,null,null,null);

        if (cursor != null){
            cursor.moveToFirst();

        }

        String [] point = new String[] {cursor.getString(0), cursor.getString(1),cursor.getString(2)};
        cursor.close();
        return point;
    }

    // редактировать запись в DB_TABLE
    public void editRec(long id,String name, String loc) {

        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_LOC, loc);

         mDB.update(DB_TABLE, cv, "_id=?", new String[]{String.valueOf(id)});

    }

    // удалить запись из DB_TABLE
    public void delRec(long id) {

        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            if (isTableExist(db, DB_TABLE))
                db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

            onCreate(db);
        }

        public boolean isTableExist(SQLiteDatabase db, String tableName) {

            Cursor cursor = db.rawQuery("Select distinct tbl_name from sqlite_master where tbl_name = '" +
                    tableName + "';", null);

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.close();
                    return true;
                }
                cursor.close();
            }
            return false;
        }
    }
}




