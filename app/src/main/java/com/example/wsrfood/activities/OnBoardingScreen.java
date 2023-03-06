package com.example.wsrfood.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.wsrfood.R;
import com.example.wsrfood.common.NetworkStats;

public class OnBoardingScreen extends AppCompatActivity {

    Integer step = 0;
    // координата при нажатии на экран
    Integer start_x = -1;
    // координата при отжатии экрана
    Integer end_x = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // открываем первый слой
        setContentView(R.layout.activity_on_boarding_screen);
    }


    @Override
    // Функция позволяющая определить нажатия на экран
    public boolean onTouchEvent(MotionEvent event) {
        // проверяем нажатие
        switch (event.getAction()) {
            // если пользователь нажал на экран
            case MotionEvent.ACTION_DOWN:
                // запоминаем координату нажатия
                start_x = (int) event.getX();
                break;
            // если пользователь отжал палец
            case MotionEvent.ACTION_UP:
                // запоминаем координату нажатия
                end_x = (int) event.getX();
        }

        // если начальная координата и конечная координата не равна нулю
        if(start_x != -1 && end_x != -1) {
            // если разница между двумя координатами меньше |10|
            if(Math.abs(start_x - end_x) > 10) {
                // если координата нажатия меньше координата отжатия
                if(start_x < end_x) {
                    // свайп влево
                    // если включен не первый слой
                    if(step != 0) {
                        // включаем первый слой
                        setContentView(R.layout.activity_on_boarding_screen);
                        // запоминаем что включили первый слой
                        step = 0;
                    }
                } else {
                    // свайп вправо
                    // если включен не второй слой
                    if(step != 1) {
                        // включаем второй слой
                        setContentView(R.layout.activity_on_boarding_screen_2);
                        // проверяем если у нас нет интернета
                        if(!NetworkStats.isOnline(this)) {
                            // находим текстовое поле, позволяющее пропустить экран авторизации и регистрации
                            TextView skip = findViewById(R.id.textView3);
                            // включаем текстовое поле
                            skip.setVisibility(View.VISIBLE);
                        }
                        // запоминаем что включили второй слой
                        step = 1;
                    }
                }
            }
            // обнуляем координаты нажатий
            start_x = -1;
            end_x =-1;
        }

        return false;
    }

    // функция открытия окна авторизации
    public void OnSingIn(View view) {
        // создайм Intent
        Intent intent = new Intent(OnBoardingScreen.this, SingInScreen.class);
        // открываем активность авторизации
        startActivity(intent);
    }
    // функция открытия окна регистрации
    public void OnSingUp(View view) {
        // создайм Intent
        Intent intent = new Intent(OnBoardingScreen.this, SingUpScreen.class);
        // открываем активность регистрации
        startActivity(intent);
    }
    // функция открытия главного окна
    public void OnSkipSing(View view) {
        // создайм Intent
        Intent intent = new Intent(OnBoardingScreen.this, MainActivity.class);
        // открываем главное окно
        startActivity(intent);
        // закрываем окно мануала
        this.finish();
    }
}