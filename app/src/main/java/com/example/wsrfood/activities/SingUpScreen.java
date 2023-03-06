package com.example.wsrfood.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wsrfood.R;
import com.example.wsrfood.common.AlertDialogs;
import com.example.wsrfood.common.LoginData;
import com.example.wsrfood.common.NetworkStats;
import com.example.wsrfood.common.ReginData;
import com.example.wsrfood.common.RequestBodyQuery;
import com.example.wsrfood.common.UserToken;
import com.example.wsrfood.common.Validation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.paperdb.Paper;

public class SingUpScreen extends AppCompatActivity {
    // текстовое поле E-mail
    TextView emailTextView;
    // текстовое поле пароля
    TextView passwordTextView;
    // текстовое поле имени пользователя (логина)
    TextView fullnameTextView;
    // инициализируем класс для авторизации
    LoginData loginData = new LoginData();
    // инициализируем класс для регистрации
    ReginData reginData = new ReginData();
    // инициализируем класс для получения токена
    UserToken userToken = new UserToken();
    // Gson билдер, необходим для парсера Json
    GsonBuilder builder = new GsonBuilder();
    // Gson билдер, необходим для парсера Json
    public Gson gson = builder.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up_screen);

        // получаем текстовое поле E-mail
        emailTextView = findViewById(R.id.login);
        // получаем текстовое поле пароля
        passwordTextView = findViewById(R.id.password);
        // получаем текстовое поле имени пользователя (логина)
        fullnameTextView = findViewById(R.id.fullname);
        // инициализируем работу с Paper для хранения данных
        Paper.init(this);
    }

    // Функция повторения пароля (AlertDialog с текстовым полем)
    public void RepeatPassword() {
        // Создаём текстовое поле которое будет отображаться в AlertDialog
        final EditText input = new EditText(this);
        // Создаём AlertDialog
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // указываем наименование AlertDialog
        alert.setTitle("Уведомление");
        // указываем текстовое сообщение AlertDialog
        alert.setMessage("Пожалуйста, повторите пароль");
        // добавляем текстовое поле в отображение AlertDialog
        alert.setView(input);
        // добавляем кнопку ОК в AlertDialog
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            // определяем действие при нажатии на кнопку
            public void onClick(DialogInterface dialog, int whichButton) {
                // если введёный пароль в текстовое поле совпадает с паролем введёным в текстовое поле на AlertDialog
                if (String.valueOf(passwordTextView.getText()).equals(String.valueOf(input.getText()))) {
                    // переходим к регистрации
                    SingUp();
                } else
                    // если не совпадает отображаем уведомление
                    AlertDialogs.OpenAlertDialog(SingUpScreen.this, "Пароли не совпадают.");
            }
        });
        // показываем созданный AlertDialog
        alert.show();
    }

    // функция регистрации выполняемая при нажатии на кнопку Register Now
    public void OnSingUp(View view) {
        // провреяем, если поле E-mail не пустое
        if (emailTextView.getText().length() != 0) {
            // провреяем, если поле пароля не пустое
            if (passwordTextView.getText().length() != 0) {
                // провреяем, если поле имени пользователя не пустое
                if (fullnameTextView.getText().length() != 0) {
                    // провреяем, если поле E-mail проходит заданным критериям (согластно заданным критериям)
                    if (Validation.mailValidation(String.valueOf(emailTextView.getText()))) {
                        // проверяем если есть интернет
                        if (NetworkStats.isOnline(this)) {
                            // вызываем проверку на совпадение пароля
                            RepeatPassword();
                        } else
                            // если интернет отсутствует, выводим уведомление пользователю
                            AlertDialogs.OpenAlertDialog(this, "Отсутствует интернет соединение.");
                    } else
                        // если e-mail не проходит валидацию, выводим уведомление пользователю
                        AlertDialogs.OpenAlertDialog(this, "E-mail не соответствует заданным критериям.");
                } else
                    // если поле имя пользователя пустое, выводим уведомление пользователю
                    AlertDialogs.OpenAlertDialog(this, "Пожалуйста, введите имя пользователя");
            } else
                // если поле пароль пустое, выводим уведомление пользователю
                AlertDialogs.OpenAlertDialog(this, "Пожалуйста, введите пароль пользователя");
        } else
            // если поле E-mail пустое, выводим уведомление пользователю
            AlertDialogs.OpenAlertDialog(this, "Пожалуйста, введите E-mail пользователя");
    }

    // функция регистрации
    public void SingUp() {
        // заполняем поле email в классе данными с текстового поля
        reginData.email = String.valueOf(emailTextView.getText());
        // заполняем поле пароля в классе данными с текстового поля
        reginData.password = String.valueOf(passwordTextView.getText());
        // заполняем поле имя пользователя (логина) в классе данными с текстового поля
        reginData.login = String.valueOf(fullnameTextView.getText());

        // создаём экземпляр класса с запросом, указывая URL, и созданный Json из пользовательских значений
        RequestBodyQuery requestBodyQuery = new RequestBodyQuery("https://auth-api-xgdk.onrender.com/auth/register",
                gson.toJson(reginData));
        // Инициализируем поток
        RequestBodyQuery.PostQueryJsoup postQueryJsoup = new RequestBodyQuery.PostQueryJsoup();
        // выполняем поток, возвращая данные интерфейсом обратно в данный обработчик
        postQueryJsoup.execute(new RequestBodyQuery.RequestBodyQueryInter() {
            @Override
            public void RequestBodyQueryReturner(String str) {
                // если данные существуют
                if (str.length() != 0) {
                    // создаём уведомлениие об успешной регистрации
                    Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                    // заполняем поле email в классе данными с текстового поля
                    loginData.email = reginData.email;
                    // заполняем поле пароля в классе данными с текстового поля
                    loginData.password = reginData.password;
                    // вызываем функцию авторизации
                    SingIn();
                } else
                    // если даные отсутствуют, выводим уведомление пользователю
                    AlertDialogs.OpenAlertDialog(SingUpScreen.this, "Пользователь не найден.");
            }
        });
    }

    // функция авторизации
    public void SingIn() {
        // создаём экземпляр класса с запросом, указывая URL, и созданный Json из пользовательских значений
        RequestBodyQuery requestBodyQuery = new RequestBodyQuery("https://auth-api-xgdk.onrender.com/auth/login",
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
                    Intent intent = new Intent(SingUpScreen.this, MainActivity.class);
                    // запускаем активность
                    startActivity(intent);
                    // завершаем существование активности регистрации
                    SingUpScreen.this.finish();
                } else
                    // если даные отсутствуют, выводим уведомление пользователю
                    AlertDialogs.OpenAlertDialog(SingUpScreen.this, "Пользователь не найден.");
            }
        });
    }

    // функция выхода
    public void OnCancel(View view) {
        // создаём новый Intent, ведущий на страницу SingIn
        Intent intent = new Intent(SingUpScreen.this, SingInScreen.class);
        // запускаем активность
        startActivity(intent);
        // завершаем существование активности регистрации
        this.finish();
    }
}