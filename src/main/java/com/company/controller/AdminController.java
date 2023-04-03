package com.company.controller;

import com.company.container.ComponentContainer;
import com.company.database.Database;
import com.company.enums.AdminStatus;
import com.company.model.Preview;
import com.company.service.*;
import com.company.util.InlineButtonUtil;
import com.company.util.InlineKeyboardUtil;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.company.container.ComponentContainer.productMap;

public class AdminController {
    public static final String QUERY = "SELECT  from ";

    public void handleMessage(User user, Message message) throws SQLException {


        if (message.hasText()) {
            handleText(message);
        }  else if (message.hasPhoto()) {
            handlePhoto( message);
        }
    }
    private void handlePhoto( Message message) {
        List<PhotoSize> photoSizeList = message.getPhoto();
        String chatId = String.valueOf(message.getChatId());
        if (ComponentContainer.productStepMap.containsKey(chatId)) {
            Preview product = productMap.get(chatId);

            if (ComponentContainer.productStepMap.get(chatId).equals(AdminStatus.ENTERED_PRODUCT_PRICE)) {
                product.setImage(photoSizeList.get(photoSizeList.size() - 1).getFileId());

                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("📊 Категория: %s\n" +
                               "💰 Цена: %s\nНазвание: %s \n\n Добавить превью в базу данных?",
                        ProductCategoryService.getProductCategoryById(product.getProduct_category_id()).getName(),
                        product.getPrice(),product.getName(),product.getGame_category_id()));
                sendPhoto.setReplyMarkup(InlineKeyboardUtil.confirmAddProductMarkup());

                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
        }

    }
    private void handleText( Message message) throws SQLException {
        Connection connection = Database.getConnection();
        String text = message.getText();
        String chatId = String.valueOf(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        if (text.equals("/start")) {
            sendMessage.setText("*\uD83D\uDC4BПривет , выберите действие*");
            sendMessage.setParseMode(ParseMode.MARKDOWN);
            sendMessage.setReplyMarkup(InlineKeyboardUtil.productMenu());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }
        else if (ComponentContainer.productStepMap.containsKey(chatId)) {

            Preview product = productMap.get(chatId);
            if (ComponentContainer.productStepMap.get(chatId).equals(AdminStatus.SELECT_CATEGORY_FOR_ADD_PRODUCT)) {
                product.setName(text); // 55
                ComponentContainer.productStepMap.put(chatId, AdminStatus.ENTERED_PRODUCT_NAME);

                sendMessage.setText("Введите цену товара (только цифры больше нуля): ");

            }
             if (ComponentContainer.productStepMap.get(chatId).equals(AdminStatus.ENTERED_PRODUCT_NAME)) {
                Integer price = 0;
                try {
                    price = Integer.parseInt(text.trim());
                } catch (NumberFormatException e) {
                }

                if (price <= 0) {
                    sendMessage.setText(" Цена ведена не верна , повторите попытку: ");
                } else {
                    product.setPrice(price);
                    ComponentContainer.productStepMap.put(chatId, AdminStatus.ENTERED_PRODUCT_PRICE);

                    sendMessage.setText("Отправьте саму превьюшку: ");
                }
            }
             if (ComponentContainer.productStepMap.get(chatId).equals(AdminStatus.DELETE_PRODUCT)) {
                PreviewService.deleteProduct(Integer.valueOf(text));
                sendMessage.setText("*✅️ Товар стёрт с базы данных.\n\nВыберите действие*");
                sendMessage.setParseMode("Markdown");
                productMap.remove(chatId);
                ComponentContainer.productStepMap.remove(chatId);
                sendMessage.setReplyMarkup(InlineKeyboardUtil.productMenu());
            }

        }
        else if(text.startsWith("/add")) {
            ResultSet result = Database.getConnection().createStatement().executeQuery(QUERY);
            while (result.next()) {
                sendMessage.setText(text);
                sendMessage.setText(text.substring(5));
                String chatID = String.valueOf(result.getLong("id"));
                sendMessage.setChatId(chatID);
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
            }
        }

    }
    public void handleCallBack(User user, Message message, String data) throws SQLException {
        String chatId = String.valueOf(message.getChatId());
        EditMessageText editMessageText = new EditMessageText();
        if (data.equals("add_product")) {


            SendMessage sendMessage = new SendMessage(
                    chatId, "Выберите одну из категорий:"
            );
            sendMessage.setReplyMarkup(InlineKeyboardUtil.categoryInlineMarkup());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

            productMap.remove(chatId);
            ComponentContainer.productStepMap.remove(chatId);

            ComponentContainer.productStepMap.put(chatId, AdminStatus.CLICKED_ADD_PRODUCT);
            productMap.put(chatId,
                    new Preview(null, null, null, null, null, null, null));

        }
        else if (data.startsWith("add_product_category_id")) {
            DeleteMessage deleteMessage = new DeleteMessage(
                    chatId, message.getMessageId()
            );
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            int categoryId = Integer.parseInt(data.split("/")[1]);

            SendMessage sendMessage = new SendMessage(
                    chatId, "*❗️Введите информацию о товаре в формате*: \n ` - Название \n - Цена \n - Картинка` \n (все сообщения отпрявляйте по отдельности)"
            );
            sendMessage.setParseMode("Markdown");

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

            ComponentContainer.productStepMap.put(chatId, AdminStatus.SELECT_CATEGORY_FOR_ADD_PRODUCT);
            Preview product = productMap.get(chatId);
            product.setProduct_category_id(categoryId);
        }
        else if (data.equals("add_product_commit")) {
            DeleteMessage deleteMessage = new DeleteMessage(
                    chatId, message.getMessageId()
            );
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            Preview product = productMap.get(chatId);
            product.setName(product.getName());
            product.setIsDeleted("FALSE");
            product.setProduct_category_id(product.getProduct_category_id());
            System.out.println("product.getName() = " + product.getName());

            PreviewService.addPreview(product);

            productMap.remove(chatId);
            ComponentContainer.productStepMap.remove(chatId);

            SendMessage sendMessage = new SendMessage(
                    chatId, "\t \n ✅ Сохранено.\n\n" + "Выберите действие:"
            );
            sendMessage.setReplyMarkup(InlineKeyboardUtil.productMenu());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        }
        else if (data.equals("add_product_cancel")) {
            DeleteMessage deleteMessage = new DeleteMessage(
                    chatId, message.getMessageId()
            );
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            productMap.remove(chatId);
            ComponentContainer.productStepMap.remove(chatId);

            SendMessage sendMessage = new SendMessage(
                    chatId, "Выберите действие:"
            );
            sendMessage.setReplyMarkup(InlineKeyboardUtil.productMenu());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }
        else if(data.equals("backmenu")){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("*\uD83D\uDC4BПривет , выберите действие*");
            sendMessage.setParseMode(ParseMode.MARKDOWN);
            sendMessage.setReplyMarkup(InlineKeyboardUtil.productMenu());
            sendMessage.setChatId(chatId);
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }
        // ПОКАЗ ПРОДУКТА
        // GAMES
        else if (data.equals("show_product_list")) {
            editMessageText.setText("*\uD83D\uDCCA Выберите категорию товара*");
            editMessageText.setParseMode(ParseMode.MARKDOWN);
            InlineKeyboardButton car = InlineButtonUtil.button("GAME", "game", "\uD83D\uDD2B");
            InlineKeyboardButton ehq = InlineButtonUtil.button("SOFT", "soft", "⚙️");
            List<InlineKeyboardButton> row1 = InlineButtonUtil.row(car);
            List<InlineKeyboardButton> row2 = InlineButtonUtil.row(ehq);
            List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row1, row2);
            editMessageText.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
            editMessageText.setChatId(chatId);
            editMessageText.setMessageId(message.getMessageId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(editMessageText);
        }
        else if (data.equals("game")) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("❗️ Выберите нужную игру: ");
            sendMessage.setParseMode("Markdown");
            sendMessage.setChatId(chatId);
            InlineKeyboardButton fh = InlineButtonUtil.button("Fortnite Hack  ", "Fortnite Hack", "\uD83D\uDD2B");
            InlineKeyboardButton fs = InlineButtonUtil.button("Fortnite Skinchanger  ", "Fortnite Skinchanger", "\uD83D\uDD2B");
            InlineKeyboardButton gta = InlineButtonUtil.button("Gta V Mod Menu  ", "Gta V Mod Menu", "\uD83D\uDD2B");
            InlineKeyboardButton fifa = InlineButtonUtil.button("FIFA 23 crack  ", "Fifa 23 Crack", "\uD83D\uDD2B");
            InlineKeyboardButton nba = InlineButtonUtil.button("NBA 2k23  ", "NBA 2k23", "\uD83D\uDD2B");
            InlineKeyboardButton valorant = InlineButtonUtil.button("Valorant Hack  ", "Valorant Hack", "\uD83D\uDD2B");
            InlineKeyboardButton roblox = InlineButtonUtil.button("Roblox synapse Hack  ", "Roblox Synapse Hack", "\uD83D\uDD2B");
            InlineKeyboardButton warzone = InlineButtonUtil.button("Warzone 2 Hack  ", "Warzone 2 Hack", "\uD83D\uDD2B");
            InlineKeyboardButton eft = InlineButtonUtil.button("Escape From Tarkov Hack  ", "Escape From Tarkov Hack", "\uD83D\uDD2B");
            InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
            List<InlineKeyboardButton> row = InlineButtonUtil.row(fh);
            List<InlineKeyboardButton> row1 = InlineButtonUtil.row(fs);
            List<InlineKeyboardButton> row2 = InlineButtonUtil.row(gta);
            List<InlineKeyboardButton> row3 = InlineButtonUtil.row(fifa);
            List<InlineKeyboardButton> row4 = InlineButtonUtil.row(nba);
            List<InlineKeyboardButton> row5 = InlineButtonUtil.row(valorant);
            List<InlineKeyboardButton> row6 = InlineButtonUtil.row(roblox);
            List<InlineKeyboardButton> row7 = InlineButtonUtil.row(warzone);
            List<InlineKeyboardButton> row8 = InlineButtonUtil.row(eft);
            List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
            List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row, row1,row2,row3,row4,row5,row6,row7,row8,row9);
            sendMessage.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        }
        else if (data.equals("soft")){
            editMessageText.setText("❗️ Выберите нужную программу: ");
            editMessageText.setParseMode("Markdown");
            editMessageText.setChatId(chatId);
            InlineKeyboardButton phc = InlineButtonUtil.button("Photoshop Crack  ", "Photoshop Crack", "\uD83D\uDDA5");
            InlineKeyboardButton prc = InlineButtonUtil.button("Premiere Pro Crack  ", "Premiere Pro Crack", "\uD83D\uDDA5");
            InlineKeyboardButton spt = InlineButtonUtil.button("Spotify Crack  ", "Spotify Crack", "\uD83D\uDDA5");
            InlineKeyboardButton aec = InlineButtonUtil.button("After Effects Crack  ", "After Effects Crack", "\uD83D\uDDA5");
            InlineKeyboardButton svp = InlineButtonUtil.button("Sony Vegas pro 19 Crack  ", "Sony Vegas Pro 19 Crack", "\uD83D\uDDA5");
            InlineKeyboardButton moc = InlineButtonUtil.button("Microsoft office Crack  ", "Microsoft Office Crack", "\uD83D\uDDA5");
            InlineKeyboardButton acr = InlineButtonUtil.button("Acrobat 2022 Crack  ", "Acrobat 2022 Crack", "\uD83D\uDDA5");
            InlineKeyboardButton fl = InlineButtonUtil.button("Fl Studio 20 Crack  ", "Fl Studio 20 Crack", "\uD83D\uDDA5");
            InlineKeyboardButton fcr = InlineButtonUtil.button("Filmora 11 Crack  ", "Filmora 11 Crack", "\uD83D\uDDA5");
            InlineKeyboardButton aic = InlineButtonUtil.button("Adobe Indesign Crack ", "Adobe Indesign Crack", "\uD83D\uDDA5");
            InlineKeyboardButton ac = InlineButtonUtil.button("Autocad Crack  ", "Autocad Crack", "\uD83D\uDDA5");
            InlineKeyboardButton lc = InlineButtonUtil.button("Lightroom Crack  ", "Lightroom Crack", "\uD83D\uDDA5");
            InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
            List<InlineKeyboardButton> row = InlineButtonUtil.row(phc);
            List<InlineKeyboardButton> row1 = InlineButtonUtil.row(prc);
            List<InlineKeyboardButton> row2 = InlineButtonUtil.row(spt);
            List<InlineKeyboardButton> row3 = InlineButtonUtil.row(aec);
            List<InlineKeyboardButton> row4 = InlineButtonUtil.row(svp);
            List<InlineKeyboardButton> row5 = InlineButtonUtil.row(moc);
            List<InlineKeyboardButton> row6 = InlineButtonUtil.row(acr);
            List<InlineKeyboardButton> row7 = InlineButtonUtil.row(fl);
            List<InlineKeyboardButton> row8 = InlineButtonUtil.row(fcr);
            List<InlineKeyboardButton> row9 = InlineButtonUtil.row(aic);
            List<InlineKeyboardButton> row10 = InlineButtonUtil.row(ac);
            List<InlineKeyboardButton> row11 = InlineButtonUtil.row(lc);
            List<InlineKeyboardButton> row12 = InlineButtonUtil.row(back);
            List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row, row1,row2,row3,row4,row5,row6,row7,row8,row9,row10,row11,row12);
            editMessageText.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
            editMessageText.setMessageId(message.getMessageId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(editMessageText);
        }
        else if(data.equals("Fortnite Hack")){


                PreviewService.loadFortniteHack();

                for (Preview product : Database.previewList) {
                    SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                    sendPhoto.setCaption(String.format("*📊 Категория: %s\n" +
                                    "💾 Товар: %s \n💰 Цена: %s\n*",
                            GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                            product.getName(), product.getPrice()));
                    sendPhoto.setParseMode(ParseMode.MARKDOWN);
                    InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                    List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                    List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                    sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                    sendPhoto.setChatId(chatId);
                    ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
                }
        }
        else if(data.equals("Fortnite Skinchanger")){
            PreviewService.loadFortniteSkinchanger();

            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*📊 Категория: %s\n" +
                                "💾 Товар: %s \n💰 Цена: %s\n*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                    List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                    List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                    sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                sendPhoto.setChatId(chatId);
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
        }
        else if(data.equals("Gta V Mod Menu")){


            PreviewService.loadGTA();

            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*📊 Категория: %s\n" +
                                "💾 Товар: %s \n💰 Цена: %s\n*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                    List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                    List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                    sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                sendPhoto.setChatId(chatId);
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
        }
        else if(data.equals("Fifa 23 Crack")){
            PreviewService.loadFifa();

            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*📊 Категория: %s\n" +
                                "💾 Товар: %s \n💰 Цена: %s\n*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                    List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                    List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                    sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                sendPhoto.setChatId(chatId);
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
        }
        else if(data.equals("NBA 2k23")){
            PreviewService.loadNBA();

            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*📊 Категория: %s\n" +
                                "💾 Товар: %s \n💰 Цена: %s\n*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                    List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                    List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                    sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                sendPhoto.setChatId(chatId);
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
        }
        else if(data.equals("Valorant hack")){
            PreviewService.loadVALORANT();

            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*📊 Категория: %s\n" +
                                "💾 Товар: %s \n💰 Цена: %s\n*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                    List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                    List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                    sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                sendPhoto.setChatId(chatId);
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
        }
        else if(data.equals("Genshin hack")){
            PreviewService.loadGENSHIN();

            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*📊 Категория: %s\n" +
                                "💾 Товар: %s \n💰 Цена: %s\n*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                    List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                    List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                    sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                sendPhoto.setChatId(chatId);
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
        }
        else if(data.equals("Roblox synapse hack")){
            PreviewService.loadROBLOX();
            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*📊 Категория: %s\n" +
                                "💾 Товар: %s \n💰 Цена: %s\n*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                    List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                    List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                    sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                sendPhoto.setChatId(chatId);
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
        }
        else if(data.equals("Warzone 2 hack")){
            PreviewService.loadWARZONE();
            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*📊 Категория: %s\n" +
                                "💾 Товар: %s \n💰 Цена: %s\n*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                    List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                    List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                    sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                sendPhoto.setChatId(chatId);
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
        }
        else if(data.equals("Escape From Tarkov hack")){
            PreviewService.loadEFT();
            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*📊 Категория: %s\n" +
                                "💾 Товар: %s \n💰 Цена: %s\n*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                    List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                    List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                    sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                sendPhoto.setChatId(chatId);
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
        }
        // SOFT

        // УДАЛЕНИЕ ПРОДУКТА
        // GAMES
        else if (data.equals("delete_product")) {
            editMessageText.setText("*\uD83D\uDCCA Выберите категорию товара*");
            editMessageText.setParseMode(ParseMode.MARKDOWN);
            InlineKeyboardButton car = InlineButtonUtil.button("Game", "gamed", "\uD83D\uDD2B");
            InlineKeyboardButton ehq = InlineButtonUtil.button("Soft", "softd", "⚙️");
            List<InlineKeyboardButton> row1 = InlineButtonUtil.row(car);
            List<InlineKeyboardButton> row2 = InlineButtonUtil.row(ehq);
            List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row1, row2);
            editMessageText.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
            editMessageText.setChatId(chatId);
            editMessageText.setMessageId(message.getMessageId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(editMessageText);

        }
        else if (data.equals("gamed")) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("❗️ Выберите нужную игру: ");
            sendMessage.setParseMode("Markdown");
            sendMessage.setChatId(chatId);
            InlineKeyboardButton fh = InlineButtonUtil.button("Fortnite Hack  ", "Fortnite Hackd", "\uD83D\uDD2B");
            InlineKeyboardButton fs = InlineButtonUtil.button("Fortnite Skinchanger  ", "Fortnite Skinchangerd", "\uD83D\uDD2B");
            InlineKeyboardButton gta = InlineButtonUtil.button("Gta V Mod Menu  ", "Gta V Mod Menud", "\uD83D\uDD2B");
            InlineKeyboardButton fifa = InlineButtonUtil.button("FIFA 23 crack  ", "Fifa 23 Crackd", "\uD83D\uDD2B");
            InlineKeyboardButton nba = InlineButtonUtil.button("NBA 2k23  ", "NBA 2k23d", "\uD83D\uDD2B");
            InlineKeyboardButton valorant = InlineButtonUtil.button("Valorant Hack  ", "Valorant Hackd", "\uD83D\uDD2B");
            InlineKeyboardButton roblox = InlineButtonUtil.button("Roblox synapse Hack  ", "Roblox Synapse Hackd", "\uD83D\uDD2B");
            InlineKeyboardButton warzone = InlineButtonUtil.button("Warzone 2 Hack  ", "Warzone 2 Hackd", "\uD83D\uDD2B");
            InlineKeyboardButton eft = InlineButtonUtil.button("Escape From Tarkov Hack  ", "Escape From Tarkov Hackd", "\uD83D\uDD2B");
            InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
            List<InlineKeyboardButton> row = InlineButtonUtil.row(fh);
            List<InlineKeyboardButton> row1 = InlineButtonUtil.row(fs);
            List<InlineKeyboardButton> row2 = InlineButtonUtil.row(gta);
            List<InlineKeyboardButton> row3 = InlineButtonUtil.row(fifa);
            List<InlineKeyboardButton> row4 = InlineButtonUtil.row(nba);
            List<InlineKeyboardButton> row5 = InlineButtonUtil.row(valorant);
            List<InlineKeyboardButton> row6 = InlineButtonUtil.row(roblox);
            List<InlineKeyboardButton> row7 = InlineButtonUtil.row(warzone);
            List<InlineKeyboardButton> row8 = InlineButtonUtil.row(eft);
            List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
            List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row, row1,row2,row3,row4,row5,row6,row7,row8,row9);
            sendMessage.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        }
        else if (data.equals("Fortnite Hackd")) {
            PreviewService.loadFortniteHack();

            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*\uD83D\uDCCAКатегория: %s\n" +
                                "\uD83D\uDCBEMahsulot: %s \n\uD83D\uDCB0Цена: %s\nID: %s*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice(), product.getId()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
            SendMessage sendMessage = new SendMessage(chatId, "_Выберите ID товара которую хотите удалить:_ ");
            sendMessage.setParseMode("Markdown");
            ComponentContainer.productStepMap.put(chatId, AdminStatus.DELETE_PRODUCT);

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        }
        else if(data.equals("Fortnite Skinchangerd")){
            PreviewService.loadFortniteSkinchanger();

            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*\uD83D\uDCCAКатегория: %s\n" +
                                "\uD83D\uDCBEMahsulot: %s \n\uD83D\uDCB0Цена: %s\nID: %s*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice(), product.getId()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
            SendMessage sendMessage = new SendMessage(chatId, "_Выберите ID товара которую хотите удалить:_ ");
            sendMessage.setParseMode("Markdown");
            ComponentContainer.productStepMap.put(chatId, AdminStatus.DELETE_PRODUCT);

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }
        else if(data.equals("Gta V Mod Menud")){

            PreviewService.loadGTA();

            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*\uD83D\uDCCAКатегория: %s\n" +
                                "\uD83D\uDCBEMahsulot: %s \n\uD83D\uDCB0Цена: %s\nID: %s*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice(), product.getId()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
            SendMessage sendMessage = new SendMessage(chatId, "_Выберите ID товара которую хотите удалить:_ ");
            sendMessage.setParseMode("Markdown");
            ComponentContainer.productStepMap.put(chatId, AdminStatus.DELETE_PRODUCT);

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }
        else if(data.equals("Fifa 23 Crackd")){
            PreviewService.loadFifa();

            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*\uD83D\uDCCAКатегория: %s\n" +
                                "\uD83D\uDCBEMahsulot: %s \n\uD83D\uDCB0Цена: %s\nID: %s*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice(), product.getId()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
            SendMessage sendMessage = new SendMessage(chatId, "_Выберите ID товара которую хотите удалить:_ ");
            sendMessage.setParseMode("Markdown");
            ComponentContainer.productStepMap.put(chatId, AdminStatus.DELETE_PRODUCT);

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }
        else if(data.equals("NBA 2k23d")){

            PreviewService.loadNBA();

            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*\uD83D\uDCCAКатегория: %s\n" +
                                "\uD83D\uDCBEMahsulot: %s \n\uD83D\uDCB0Цена: %s\nID: %s*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice(), product.getId()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
            SendMessage sendMessage = new SendMessage(chatId, "_Выберите ID товара которую хотите удалить:_ ");
            sendMessage.setParseMode("Markdown");
            ComponentContainer.productStepMap.put(chatId, AdminStatus.DELETE_PRODUCT);

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }
        else if(data.equals("Valorant hackd")){
            PreviewService.loadVALORANT();
            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*\uD83D\uDCCAКатегория: %s\n" +
                                "\uD83D\uDCBEMahsulot: %s \n\uD83D\uDCB0Цена: %s\nID: %s*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice(), product.getId()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
            SendMessage sendMessage = new SendMessage(chatId, "_Выберите ID товара которую хотите удалить:_ ");
            sendMessage.setParseMode("Markdown");
            ComponentContainer.productStepMap.put(chatId, AdminStatus.DELETE_PRODUCT);

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }
        else if(data.equals("Genshin hackd")){

            PreviewService.loadGENSHIN();

            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*\uD83D\uDCCAКатегория: %s\n" +
                                "\uD83D\uDCBEMahsulot: %s \n\uD83D\uDCB0Цена: %s\nID: %s*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice(), product.getId()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
            SendMessage sendMessage = new SendMessage(chatId, "_Выберите ID товара которую хотите удалить:_ ");
            sendMessage.setParseMode("Markdown");
            ComponentContainer.productStepMap.put(chatId, AdminStatus.DELETE_PRODUCT);

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }
        else if(data.equals("Roblox synapse hackd")){
            PreviewService.loadROBLOX();

            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*\uD83D\uDCCAКатегория: %s\n" +
                                "\uD83D\uDCBEMahsulot: %s \n\uD83D\uDCB0Цена: %s\nID: %s*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice(), product.getId()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
            SendMessage sendMessage = new SendMessage(chatId, "_Выберите ID товара которую хотите удалить:_ ");
            sendMessage.setParseMode("Markdown");
            ComponentContainer.productStepMap.put(chatId, AdminStatus.DELETE_PRODUCT);

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }
        else if(data.equals("Warzone 2 hackd")){

            PreviewService.loadWARZONE();
            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*\uD83D\uDCCAКатегория: %s\n" +
                                "\uD83D\uDCBEMahsulot: %s \n\uD83D\uDCB0Цена: %s\nID: %s*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice(), product.getId()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
            SendMessage sendMessage = new SendMessage(chatId, "_Выберите ID товара которую хотите удалить:_ ");
            sendMessage.setParseMode("Markdown");
            ComponentContainer.productStepMap.put(chatId, AdminStatus.DELETE_PRODUCT);

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }
        else if(data.equals("Escape From Tarkov hackd")){
            PreviewService.loadEFT();
            for (Preview product : Database.previewList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("*\uD83D\uDCCAКатегория: %s\n" +
                                "\uD83D\uDCBEMahsulot: %s \n\uD83D\uDCB0Цена: %s\nID: %s*",
                        GameCategoryService.getGameCategoryById(product.getGame_category_id()).getName(),
                        product.getName(), product.getPrice(), product.getId()));
                sendPhoto.setParseMode(ParseMode.MARKDOWN);
                InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
                List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
                sendPhoto.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
            SendMessage sendMessage = new SendMessage(chatId, "_Выберите ID товара которую хотите удалить:_ ");
            sendMessage.setParseMode("Markdown");
            ComponentContainer.productStepMap.put(chatId, AdminStatus.DELETE_PRODUCT);

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }

         if(data.equals("adverd")) {
            SendMessage sendMessage = new SendMessage();
            CustomerService.loadAllId();
            sendMessage.setText("Введите то что хотите отправить ");
            sendMessage.setChatId(CustomerService.loadAllId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
          ComponentContainer.productStepMap.put(chatId, AdminStatus.WRITE_TEXT_TO_ADD);
        }

    }

}