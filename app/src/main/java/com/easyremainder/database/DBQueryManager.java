package com.easyremainder.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.easyremainder.model.ModelTask;

import java.util.ArrayList;
import java.util.List;

public class DBQueryManager  {

private SQLiteDatabase database;

DBQueryManager(SQLiteDatabase database){

    this.database=database;
}

public ModelTask getTask(long timeStamp){
    ModelTask modelTask = null;
    Cursor cursor = database.query(DBHelper.TASKS_TABLE,null,DBHelper.SELECTION_TIME_STAMP,new String[]{Long.toString(timeStamp)},null,null,null);
    if (cursor.moveToFirst()){
        String title = cursor.getString(cursor.getColumnIndex(DBHelper.TASK_TITLE_COLUMN));
        long date = cursor.getLong(cursor.getColumnIndex(DBHelper.TASK_DATE_COLUMN));
        int priority = cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_PRIORITY_COLUMN));
        int status = cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_STATUS_COLUMN));

       modelTask = new ModelTask(title, date, priority, status, timeStamp);
    }
    cursor.close();
    return modelTask;
}



public List<ModelTask> getTasks (String selection, String[] selectionArgs, String orderBy){
    List<ModelTask> tasks = new ArrayList<>();

    Cursor c = database.query(DBHelper.TASKS_TABLE,null,selection,selectionArgs,null,null,orderBy );

    Log.d("live","DBQueryManager - работает ");
    if(c.moveToFirst()) {
        Log.d("live","DBQueryManager - выводим записи ");
        do {
            String title = c.getString(c.getColumnIndex(DBHelper.TASK_TITLE_COLUMN));
            Log.d("live","DBQueryManager - TITLE - "+c.getString(c.getColumnIndex(DBHelper.TASK_TITLE_COLUMN)));

            long date = c.getLong(c.getColumnIndex(DBHelper.TASK_DATE_COLUMN));
            Log.d("live","DBQueryManager - DATE -  "+c.getInt(c.getColumnIndex(DBHelper.TASK_DATE_COLUMN)));

            int priority = c.getInt(c.getColumnIndex(DBHelper.TASK_PRIORITY_COLUMN));
            Log.d("live","DBQueryManager - PRIORITY -  "+c.getInt(c.getColumnIndex(DBHelper.TASK_PRIORITY_COLUMN)));

            int status = c.getInt(c.getColumnIndex(DBHelper.TASK_STATUS_COLUMN));
            Log.d("live","DBQueryManager - STATUS -  "+c.getInt(c.getColumnIndex(DBHelper.TASK_STATUS_COLUMN)));

            long timeStamp = c.getLong(c.getColumnIndex(DBHelper.TASK_TIME_STAMP_COLUMN));
            Log.d("live","DBQueryManager - TimeStamp - "+c.getLong(c.getColumnIndex(DBHelper.TASK_TIME_STAMP_COLUMN)));

            ModelTask modelTask = new ModelTask(title, date, priority, status, timeStamp);
            tasks.add(modelTask);
            Log.d("live","DBQueryManage вывод завершен ");

        } while (c.moveToNext());
    }
        c.close();
        return tasks;
    }


}
