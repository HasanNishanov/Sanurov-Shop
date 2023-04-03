package com.company.util;

import com.company.database.Database;
import com.company.model.ProductCategory;
import com.company.service.ProductCategoryService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InlineKeyboardUtil {
    public static InlineKeyboardMarkup productMenu() {
        InlineKeyboardButton addButton = getButton("➕Добавить товар➕", "add_product");
        InlineKeyboardButton deleteButton = getButton("➖Удалить товар➖", "delete_product");
        InlineKeyboardButton listButton = getButton("\uD83D\uDC41Просмотреть все товары\uD83D\uDC41", "show_product_list");
        InlineKeyboardButton adverd = getButton("\uD83D\uDCE9 Разослать рекламу ", "adverd");

        List<InlineKeyboardButton> row1 = getRow(addButton,deleteButton);
        List<InlineKeyboardButton> row3 = getRow(listButton);
        List<InlineKeyboardButton> row4 = getRow(adverd);

        List<List<InlineKeyboardButton>> rowList = getRowList(row1, row3, row4);
        return new InlineKeyboardMarkup(rowList);
    }
    private static InlineKeyboardButton getButton(String demo, String data) {
        InlineKeyboardButton button = new InlineKeyboardButton(demo);
        button.setCallbackData(data);
        return button;
    }

    private static List<InlineKeyboardButton> getRow(InlineKeyboardButton... buttons) {
        return Arrays.asList(buttons);
    }

    private static List<List<InlineKeyboardButton>> getRowList(List<InlineKeyboardButton>... rows) {
        return Arrays.asList(rows);
    }

    public static InlineKeyboardMarkup categoryInlineMarkup() {

        ProductCategoryService.loadCategoryList();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (ProductCategory category : Database.productCategoryList) {
            List<InlineKeyboardButton> buttonList = new ArrayList<>();

            InlineKeyboardButton button = new InlineKeyboardButton(category.getName());
            button.setCallbackData("add_product_category_id/" + category.getId());
            buttonList.add(button);
            rowList.add(buttonList);
        }
        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup categoryInlineMarkupGames() {

        ProductCategoryService.loadCategoryListGames();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (ProductCategory category : Database.productCategoryList) {
            List<InlineKeyboardButton> buttonList = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton("\uD83C\uDFAE " + category.getName());
            button.setCallbackData(category.getName());
            buttonList.add(button);
            rowList.add(buttonList);
        }
        return new InlineKeyboardMarkup(rowList);
    }



    public static InlineKeyboardMarkup categoryInlineMarkupApp() {

        ProductCategoryService.loadCategoryListApp();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (ProductCategory category : Database.productCategoryList) {
            List<InlineKeyboardButton> buttonList = new ArrayList<>();

            InlineKeyboardButton button = new InlineKeyboardButton("\uD83D\uDDA5️ " + category.getName());
            button.setCallbackData(category.getName());
            buttonList.add(button);
            rowList.add(buttonList);
        }
        return new InlineKeyboardMarkup(rowList);
    }



    public static InlineKeyboardMarkup confirmAddProductMarkup() {

        InlineKeyboardButton commit = getButton("Да", "add_product_commit");
        InlineKeyboardButton cancel = getButton("Нет", "add_product_cancel");

        return new InlineKeyboardMarkup(getRowList(getRow(commit, cancel)));
    }
}