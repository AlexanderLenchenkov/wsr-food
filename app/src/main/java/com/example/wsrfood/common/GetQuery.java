package com.example.wsrfood.common;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class GetQuery {
    public interface Inter {
        // возвращаем значение
        void returner(String str);
    }
    // URL-адресс по которому будет происходить запрос данных
    public static String url;

    // при инициализации класса принимаем URL-адресс
    public GetQuery(String url) {
        // запоминам URL адресс
        GetQuery.url = url;
    }


    public static class GetQueryJsoup extends AsyncTask<Inter, Void, Void> {
        // полученный Json-ответ
        String json = "";
        // Интерфейс который возвращается обратно
        Inter inter;

        @Override
        protected Void doInBackground(Inter... inters) {
            // функция загрузки данных
            // загруженный документ
            Document doc = null;
            // получаем интерфейс, для того чтобы вернуть его обратно
            inter = inters[0];

            try {
                // выполняем запрос и заносим данные в документ
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                // если возникли ошибки, выводим их в консоль
                e.printStackTrace();
            }
            // получаем Json из полученного документа
            json = doc.text();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            // функция обработки потока
            super.onPostExecute(unused);
            // возвращаем назад нтерфейс с полученными данными
            inter.returner(json);
        }
    }
}
