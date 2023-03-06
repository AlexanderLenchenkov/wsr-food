package com.example.wsrfood.common;

import android.util.Log;

public class Validation {
    // проверка входящей строки на волидность (согластно условиям)
    public static boolean mailValidation(String text) {
        // Проверяем существует ли у нас символ @
        if(text.contains("@")) {
            // Делим строку на две части
            String[] value = text.split("@");
            // если строка состоит из двух частей
            if(value.length == 2) {
                // проверяем наименование на наличие маленьких букв и цифр
                if(symbolValidation(value[0], true)) {
                    // проверяем существование символа.
                    if(value[1].contains(".")) {
                        // делим домен на уровни
                        String[] valueDomen = value[1].split("\\.");
                        // если домен состоит из двух уровней
                        if(valueDomen.length == 2) {
                            // проверяем домен второго уровня на наличие маленьких букв и цифр
                            if(symbolValidation(valueDomen[0], true)) {
                                // проверяяем верхнего уровня на наличие маленьких букв
                                if(symbolValidation(valueDomen[1], false)) {
                                    // если домен верхнего уровня менее 4 символов
                                    if(valueDomen[1].length() < 4) {
                                        // возвращаем true
                                        return true;
                                    } else Log.e("MV", "Верхний регист более трёх символов.");
                                } else Log.e("MV", "Символы используемые в домене верхнего уровня не являются символами нижнего регистра.");
                            } else Log.e("MV", "Символы используемые в домене второго уровня не являются символами нижнего регистра или цифрами.");
                        } else Log.e("MV", "Домен верхнего уровня отсутствуют.");
                    } else Log.e("MV", "Входная строка не содержит символ .");
                } else Log.e("MV", "Символы используемые в имени не являются символами нижнего регистра и цифрами.");
            } else Log.e("MV", "Домен второго уровня и домен верхнего уровня отсутствуют.");
        } else Log.e("MV", "Входная строка не содержит символ @.");
        // если входная строка не проходит проверки
        // возвращаем false
        return false;
    }
    // функция проверки символов на наличие нижнего регистра или цифр
    public static boolean symbolValidation(String value, boolean number) {
        // делим входящую строку на символы
        char[] charValues = value.toCharArray();
        // создаём переменную которая отвечает за то что в строке маленькие символы или цифры
        boolean boolValues = true;
        // перебираем символы
        for(int i = 0; i < charValues.length; i++) {
            // если символ не равен цифре и если цифры не учитываются
            if(charValues[i] != '0' &&
                    charValues[i] != '1' &&
                    charValues[i] != '2' &&
                    charValues[i] != '3' &&
                    charValues[i] != '4' &&
                    charValues[i] != '5' &&
                    charValues[i] != '6' &&
                    charValues[i] != '7' &&
                    charValues[i] != '8' &&
                    charValues[i] != '9' &&
                    number) {

                // если симыол не соответствует нижнему регистру
                if(!Character.isLowerCase(charValues[i])) {
                    // запоминаем и останавиваем цикл
                    boolValues = false;
                    break;
                }
                // если цифры не проверяются
            } else if(!number) {
                // если симыол не соответствует нижнему регистру
                if(!Character.isLowerCase(charValues[i])) {
                    // запоминаем и останавиваем цикл
                    boolValues = false;
                    break;
                }
            }
        }
        // возвращаем результат
        return boolValues;
    }
}
