package com.example.wsrfood.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.wsrfood.R;
import com.example.wsrfood.common.AlertDialogs;
import com.example.wsrfood.common.DataBase;
import com.example.wsrfood.common.DishesVersion;
import com.example.wsrfood.common.Dishs;
import com.example.wsrfood.common.GetQuery;
import com.example.wsrfood.common.NetworkStats;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LaunchScreen extends AppCompatActivity {

    // Элемент ProgressBar
    ProgressBar progressBar;
    // Элемент ImageView (фейковый ProgressBar)
    ImageView faceProgressBar;
    // инициализируем таймер
    Timer timer = new Timer();
    // инициализируем задание выполняеиое таймером
    MyTimerTask myTimerTask = new MyTimerTask();
    // создаём запрос передавая ссылку на получения версий блюд
    GetQuery getQuery = new GetQuery("https://auth-api-xgdk.onrender.com/versions");
    // инициализируем класс содержащий в себе данные версий блюд
    DishesVersion dishesVersion = new DishesVersion();
    // инициализируем GsonBuilder
    GsonBuilder builder = new GsonBuilder();
    // создаём gson
    Gson gson = builder.create();
    // Инициализируем DataBaseHelper
    DataBase.DataBaseHelper dataBaseHelper  = new DataBase.DataBaseHelper(this);
    // База данных
    SQLiteDatabase database;
    // счётчик загрузки блюд
    Integer countVersionDownload = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        // получаем ProgressBar
        progressBar = findViewById(R.id.progressBar);
        // получаем ImageView (фейковый ProgressBar)
        faceProgressBar = findViewById(R.id.imageView1);

        // Получаем базу данных чтобы в последующем работать с ней
        // Если база данных отсутсвует, то она будет создана вновь
        database = dataBaseHelper.getWritableDatabase();

        // Проверяем наличие интернета
        if ( !NetworkStats.isOnline(this) ) {
            // если интернета нет
            // скрываем ProgressBar
            progressBar.setVisibility(View.GONE);
            // отображаем фейковый ProgressBar
            faceProgressBar.setVisibility(View.VISIBLE);
            // выводим уведомление об отсутствии интернета
            AlertDialogs.OpenAlertDialog(this, "Отсутствует интернет!");
            // запускаем таймер перехода на следующее окно
            timer.schedule(myTimerTask, 3000);
        } else {
            // если интернет есть
            // инициализируем поток, который будет подгружать данные с Rest API
            GetQuery.GetQueryJsoup getDishesVersion = new GetQuery.GetQueryJsoup();
            // запускаем поток, по результату которого данные будут возвращены в returner
            getDishesVersion.execute(new GetQuery.Inter() {
                @Override
                public void returner(String result) {
                    // преобразовываем данные из Json в class
                    dishesVersion = gson.fromJson(result, DishesVersion.class);
                    // вызываем функцию проверки версий
                    CheckDishVersion();
                }
            });
        }
    }

    // проверка версий меню RestAPI с локальными в БД
    public void CheckDishVersion() {
        // перебираем данные проверяя есть ли они БД
        // получаем данные из таблицы
        Cursor cursorDataBase = database.query("db_dish_version",
                null,
                null,
                null,
                null,
                null, null);
        // если есть данные которые можно читать
        if(cursorDataBase.moveToFirst()) {
            do {
                // получаем версию блюда хранящуюся в БД
                String version = cursorDataBase.getString(1);
                // проверяем есть и версия с БД в версиях полученных с RestAPI
                if(dishesVersion.version.contains(version)) {
                    // если версия существует в списке полученном с RestAPI
                    // удаляем её из списка
                    dishesVersion.version.remove(version);
                }
                // читаем следующий элемент в БД
            } while (cursorDataBase.moveToNext());
        }

        // если оставшихся версий полученных с RestAPI больше 0
        if(dishesVersion.version.size() > 0) {
            // очищаем таблицу хранящую версии
            database.execSQL("DELETE FROM db_dish_version");

            // загружаем новое меню
            for(int i = 0; i < dishesVersion.version.size(); i ++) {
                // создаём объект для данных
                ContentValues contentValues = new ContentValues();
                // добавляем переменную со значением
                contentValues.put("version", dishesVersion.version.get(i));
                // добавляем данные в Базу данных
                database.insert("db_dish_version", null, contentValues);
            }

            // вызываем загрузку блюд
            CheckDish();
        } else {
            // если данные совпали
            // переходим на новую активность
            Intent intent = new Intent(LaunchScreen.this, OnBoardingScreen.class);
            startActivity(intent);
            closeActivity();
        }
    }

    // загрузка блюд
    public void CheckDish() {
        // очищаем базу данных с блюдами
        database.execSQL("DELETE FROM db_dish");
        // выставляем счётчик количеству запросам отправляемым на сервер
        countVersionDownload = dishesVersion.version.size();
        // перебираем версии блюда и выгружаем данные
        for(int i = 0; i < dishesVersion.version.size(); i ++) {
            // изменяем ссылку для получения данных
            getQuery = new GetQuery("https://auth-api-xgdk.onrender.com/dishes?version="+dishesVersion.version.get(i));
            // инициализируем поток, который будет подгружать данные с Rest API
            GetQuery.GetQueryJsoup getDishesVersion = new GetQuery.GetQueryJsoup();
            // запускаем поток, по результату которого данные будут возвращены в returner
            getDishesVersion.execute(new GetQuery.Inter() {
                @Override
                public void returner(String result) {
                    // преобразовываем данные из Json в class
                    List<Dishs> newDish = Arrays.asList(gson.fromJson(result, Dishs[].class));
                    Log.d("dataDish ", newDish.toString());
                    // перебираем полученные блюда
                    for(int i = 0; i < newDish.size(); i ++) {
                        Log.d("dataDish ", newDish.get(i).nameDish);
                        // создаём объект для данных
                        ContentValues contentValues = new ContentValues();
                        // добавляем переменную со значением
                        contentValues.put("dishId", newDish.get(i).toString());
                        contentValues.put("category", newDish.get(i).category);
                        contentValues.put("nameDish", newDish.get(i).nameDish);
                        contentValues.put("price", newDish.get(i).price);
                        contentValues.put("icon", newDish.get(i).icon);
                        contentValues.put("version", newDish.get(i).version);
                        // добавляем данные в Базу данных
                        database.insert("db_dish", null, contentValues);
                    }
                    // при каждом ответе уменьшаем счётчик
                    countVersionDownload--;
                    // если пришли все ответы от сервера
                    if(countVersionDownload == 0) {
                        // переходим на новую активность
                        Intent intent = new Intent(LaunchScreen.this, OnBoardingScreen.class);
                        startActivity(intent);
                        closeActivity();
                    }
                }
            });

            // для того чтобы программа успела обработать данные, приостанавливаем на время выполнение основного оптока
            // это позволит создать несколько потоков, в результате чего подгрузятся все версии меню
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    // задача выполняемая через какое-то время
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            // выполнение в не потока
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // перенаправляем на другую активность
                    Intent intent = new Intent(LaunchScreen.this, OnBoardingScreen.class);
                    startActivity(intent);
                    closeActivity();
                }
            });
        }
    }
    // функция закрытия активности
    private void closeActivity() {
        // закрываем текущую активность
        this.finish();
    }
}