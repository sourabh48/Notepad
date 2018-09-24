package com.example.soura.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;


/**
 * Created by Soura on 30-05-2017
 */

public class DataBase extends SQLiteOpenHelper {

    public static final String dbName = "MyNotes";
    public static final String Id = "_id";
    public static final String Name = "name";
    public static final String Contents = "content";
    public static final String Date = "dates";
    public static final String table_name = "myNotes";
    private HashMap hp;
    SQLiteDatabase db;
    String cq = "create table " + table_name + "(" + Id + " integer primary key autoincrement," + Name + " varchar(200)," + Contents + " varchar(200)," + Date + " varchar(200));";
    private Context context;


    public DataBase(Context context) {
        super(context, dbName, null, 2);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(cq);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreate(db);

    }

    public Cursor fetchAll() {
        db = this.getReadableDatabase();
        Cursor cursor = db.query(table_name, new String[]{Id, Name, Contents, Date}, null, null, null, null, null);
        return cursor;

    }

    public boolean insertNotes(String name, String date, String content) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(Name, name);
        contentValues.put(Date, date);
        contentValues.put(Contents, content);

        long c = getWritableDatabase().insert(table_name, null, contentValues);

        if (c > 0)
            return true;
        else
            return false;

    }


    public Cursor getData(String id) {

        String col[] = {Id,Name,Contents,Date};

        String row = Id +" =?";
        String args[] = {id};

        Cursor cursor = getWritableDatabase().query(table_name,col,row,args,null,null,null);

        return cursor;


    }

    public boolean updateNotes(Integer id,String name,String dates,String content)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        if(!name.isEmpty())
            contentValues.put(Name,name);

        if(!content.isEmpty())
            contentValues.put(Contents,content);

        contentValues.put(Date,dates);


        db.update(table_name, contentValues, Id+ "= ? ",new String[]{Integer.toString(id)} );

        return true;
    }


    public Integer deleteNotes(Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(table_name,Id +"= ? ",new String[]{Integer.toString(id)});
    }


}
