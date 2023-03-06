package com.example.wsrfood.common;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.example.wsrfood.R;

public class AlertDialogs {
    public static void OpenAlertDialog(Context context, String message) {
        // Создаём билдет (окно)
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // задаём титульный текст уведомлению
        builder.setTitle("Уведомление")
                // задаём текстовое сообщение уведомлению
                .setMessage(message)
                // задаём иконку приложению
                .setIcon(R.drawable.icon)
                // добавляем кнопку ОК, при нажатии на которую закрываем диалог
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // закрываем диалог
                        dialog.cancel();
                    }
                });
        // создаём диалоговое окно
        builder.create();
        // отображаем диалоговое окно
        builder.show();
    }
}
