package com.example.wsrfood.common;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class RequestBodyQuery {
    // адрес на который отправляется запрос
    public static String url;
    // данные которые передаются в запросе
    public static String requestBody;

    // интерфейс, возвращаемый полученное значение обратно
    public interface RequestBodyQueryInter {
        // возвращаем значение
        void RequestBodyQueryReturner(String str);
    }

    // инициализация класса RequestBodyQuery
    public RequestBodyQuery(String url, String requestBody) {
        // запоминаем адрес куда необходимо отправить запрос
        RequestBodyQuery.url = url;
        // запоминаем данные которые необходимо отправить
        RequestBodyQuery.requestBody = requestBody;
    }


    public static class PostQueryJsoup extends AsyncTask<RequestBodyQueryInter, Void, Void> {
        // Переменная которая получит Json ответ от сервреа
        String json = "";
        // Интерфейс который возвращается обратно
        RequestBodyQuery.RequestBodyQueryInter inter;


        @Override
        // функция выполняющаяся в потоке
        protected Void doInBackground(RequestBodyQueryInter... inters) {
            // получаем интерфейс
            inter = inters[0];
            // создаём переменную которая будет хранить данные об ответе
            Document doc = null;

            try {
                // создаём соединение
                doc = Jsoup.connect(url)
                        // указываем пользовательского агента (в нашем случае Mozilla)
                        .userAgent("Mozilla")
                        // указываем заголовки запроса
                        .header("content-type", "application/json")
                        .header("accept", "application/json")
                        // указываем данные которые передаём
                        .requestBody(requestBody)
                        .post();
            } catch (IOException e) {
                // при возникновении ошибки, выводим данные в консоль
                e.printStackTrace();
            }

            // если в ответ приходит что-то
            if(doc != null)
                // записываем данные
                json = doc.text();

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            // возвращаем данные
            inter.RequestBodyQueryReturner(json);
        }
    }
}
