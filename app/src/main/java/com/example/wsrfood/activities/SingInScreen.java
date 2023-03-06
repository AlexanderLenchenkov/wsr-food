package com.example.wsrfood.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.wsrfood.R;
import com.example.wsrfood.common.AlertDialogs;
import com.example.wsrfood.common.LoginData;
import com.example.wsrfood.common.NetworkStats;
import com.example.wsrfood.common.RequestBodyQuery;
import com.example.wsrfood.common.UserToken;
import com.example.wsrfood.common.Validation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.paperdb.Paper;

public class SingInScreen extends AppCompatActivity {
    // Текстовое поля E-mail
    TextView loginTextView;
    // Текстовое поле пароля
    TextView passwordTextView;

    // Gson билдер, необходим для парсера Json
    GsonBuilder builder = new GsonBuilder();
    // Gson билдер, необходим для парсера Json
    public Gson gson = builder.create();
    // Класс LoginData, хранящий в себе данные о пользователе
    LoginData loginData = new LoginData();
    // Класс UserToken, хранящий в себе данные о токине пользователя
    UserToken userToken = new UserToken();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // открываем активность activity_sing_in_screen
        setContentView(R.layout.activity_sing_in_screen);
        // получаем текстовое поле E-mail
        loginTextView = findViewById(R.id.login);
        // получаем текстовое поле пароля
        passwordTextView = findViewById(R.id.password);
        // инициализируем работу с Paper для хранения данных
        Paper.init(this);
    }

    // функция авторизации пользователя
    public void OnSingIn(View view) {
        // провреяем, если поле E-mail не пустое
        if (loginTextView.getText().length() != 0) {
            // проверяем, если поле password не пустое
            if (passwordTextView.getText().length() != 0) {
                // проверяем, если поле E-mail проходит валидаци на корректность (согластно заданным критериям)
                if (Validation.mailValidation(String.valueOf(loginTextView.getText()))) {
                    // проверяем, если интернет присутствует
                    if (NetworkStats.isOnline(this)) {
                        // заполняем поле email в классе данными с текстового поля
                        loginData.email = String.valueOf(loginTextView.getText());
                        // заполняем поле пароля в классе данными с текстового поля
                        loginData.password = String.valueOf(passwordTextView.getText());

                        // создаём экземпляр класса с запросом, указывая URL, и созданный Json из пользовательских значений
                        RequestBodyQuery requestBodyQuery = new RequestBodyQuery("https://food.madskill.ru/auth/login",
                                gson.toJson(loginData));

                        // Инициализируем поток
                        RequestBodyQuery.PostQueryJsoup postQueryJsoup = new RequestBodyQuery.PostQueryJsoup();
                        // выполняем поток, возвращая данные интерфейсом обратно в данный обработчик
                        postQueryJsoup.execute(new RequestBodyQuery.RequestBodyQueryInter() {
                            @Override
                            public void RequestBodyQueryReturner(String str) {
                                // если данные существуют
                                if (str.length() != 0) {
                                    // преобразовываем Json, в класс
                                    userToken = gson.fromJson(str, UserToken.class);
                                    // записываем пришедший токен, в переменную "token"
                                    Paper.book().write("token", userToken.token);
                                    // создаём новый Intent, ведущий на страницу Main
                                    Intent intent = new Intent(SingInScreen.this, MainActivity.class);
                                    // запускаем активность
                                    startActivity(intent);
                                    // закрываем активность авторизации
                                    SingInScreen.this.finish();
                                } else
                                    // если даные отсутствуют, выводим уведомление пользователю
                                    AlertDialogs.OpenAlertDialog(SingInScreen.this, "Пользователь не найден.");
                            }
                        });
                    } else
                        // если интернет отсутствует, выводим уведомление пользователю
                        AlertDialogs.OpenAlertDialog(this, "Отсутствует интернет соединение.");
                } else
                    // если e-mail не проходит валидацию, выводим уведомление пользователю
                    AlertDialogs.OpenAlertDialog(this, "E-mail не соответствует заданным критериям.");
            } else
                // если поле пароль пустое, выводим уведомление пользователю
                AlertDialogs.OpenAlertDialog(this, "Пожалуйста, введите пароль пользователя.");
        } else
            // если поле E-mail пустое, выводим уведомление пользователю
            AlertDialogs.OpenAlertDialog(this, "Пожалуйста, введите E-mail пользователя.");
    }
}