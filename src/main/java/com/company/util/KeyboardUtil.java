package com.company.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Arrays;
import java.util.List;

public class KeyboardUtil {
    public static ReplyKeyboardMarkup contactMarkup(){
        KeyboardButton contactButton = new KeyboardButton("Личный кабинет");
        KeyboardButton contactButton1 = new KeyboardButton("Меню");
        contactButton.setText("Личный кабинет");
        contactButton1.setText("Меню");
        KeyboardRow row = getRow(contactButton,contactButton1);
        List<KeyboardRow> rowList = getRowList(row);
        return getMarkup(rowList);

    }

    private static KeyboardButton getButton(String demo){
        return new KeyboardButton(demo);
    }

    private static KeyboardRow getRow(KeyboardButton ... buttons){
        return new KeyboardRow(Arrays.asList(buttons));
    }

    private static List<KeyboardRow> getRowList(KeyboardRow ... rows){
        return Arrays.asList(rows);
    }

    private static ReplyKeyboardMarkup getMarkup(List<KeyboardRow> keyboard){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }
}
