package com.example.wsrfood.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase {
    // класс расширяющий SQLiteOpenHelper
    public static class DataBaseHelper extends SQLiteOpenHelper {
        // Стандартные функции
        public DataBaseHelper(Context context) {
            super(context, "dataBase", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            // функция создания
            // создаём таблицу, содержащую информацию о версии блюд
            database.execSQL("CREATE TABLE db_dish_version (id integer primary key autoincrement, version text);");
            // создаём таблицу, содержащую информацию о блюдах
            database.execSQL("CREATE TABLE db_dish (id integer primary key autoincrement, " +
                    "dishId integer," +
                    "category text," +
                    "nameDish text," +
                    "price ineger," +
                    "icon text," +
                    "version text);");
        }

        // стандартная функция
        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }
}
