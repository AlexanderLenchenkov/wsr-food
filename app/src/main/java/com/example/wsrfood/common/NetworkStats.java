package com.example.wsrfood.common;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStats {
    public static boolean isOnline(Context context)
    {
        // Создаём подключение
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Получаем информацию об активном состоянии
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        // Проверяем существует ли у нас информация об активном состоянии
        // Проверяем статус подключения у информации об активном состоянии
        if (netInfo != null && netInfo.isConnected())
        {
            // Возвращаем true если подключение существует
            return true;
        }
        // Возвращаем false
        return false;
    }
}
