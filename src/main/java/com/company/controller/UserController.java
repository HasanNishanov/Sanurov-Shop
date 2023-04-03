package com.company.controller;

import com.company.container.ComponentContainer;
import com.company.database.Database;
import com.company.model.Preview;
import com.company.util.InlineButtonUtil;
import com.company.util.InlineKeyboardUtil;
import com.company.util.KeyboardUtil;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.company.util.InlineKeyboardUtil.categoryInlineMarkupGames;



public class UserController {

    // Статичные методы для работы во всем классе
    public static final List<Long> customer = new ArrayList<>();  // <-- Для хранения пользователей в списке
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/M/d H:mm"); // <-- Для установки формата времени (предел: минуты
    private static LocalDateTime now = LocalDateTime.now(); // <-- Для установки реального времени то есть сейчас
    private static String status; // <-- Статус платежа
    private static String payment_id; // <-- Иденфикатор платежка
    private static String urlR; // <-- Ссылка перенаправления на crystalpay.io для оплаты
    private static Integer balance; // <-- Переменная хранящяя старый баланс пользователя
    private static Integer newBalance; // <-- Переменная хранящяя новый баланс пользователя (старый баланс + сумма пополнения)
    private static Integer numForPlusOfBalance; // <-- Для цикла(оптимизация)
    private static String naeed;
    private static Integer countOfObjectInDataBase;


    // Методы для создания счёта/проверки оплаты
    // ↓↓ Метод для возврата ссылка на платёж
    public String returnUrlForPayment(int amount, String tg_id) throws IOException, SQLException {
        URL url = new URL("https://api.crystalpay.io/v2/invoice/create/");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        String data = "{\"amount\": \"" + amount + "\"," +
                " \"currency\": \"USD\"," +
                " \"description\": \"Оплата заказа\"," +
                " \"auth_login\": \"\"," +
                " \"auth_secret\": \"\"," +
                " \"type\": \"\"," +
                " \"lifetime\": \"\"}";
        OutputStream os = con.getOutputStream();
        os.write(data.getBytes());
        os.flush();
        os.close();

        int responseCode = con.getResponseCode();
        // Меняем

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response.toString().substring(33, 53)); //id
            System.out.println(response.toString().substring(62, 114)); // url
            System.out.println(response);

            payment_id = response.toString().substring(33, 53);
            urlR = response.toString().substring(62, 114).replace("\\", "");

            Database.getConnection().createStatement().execute
                    ("insert into () values() ");
        } else {
            System.out.println("POST request not worked");
        }
        return urlR;
    }

    // ↓↓ Метод для возврата статуса платежа
    public String returnStatusOfPayment(String id) throws IOException {

        URL url1 = new URL("https://api.crystalpay.io/v2/invoice/info/");
        HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
        con1.setRequestMethod("POST");
        con1.setRequestProperty("Content-Type", "application/json");
        con1.setDoOutput(true);

        // Здесь мы создаем JSON-строку для передачи в теле запроса
        String data1 = "{\"id\": \"" + id + " \", \"auth_login\": \"\", \"auth_secret\": \"\"}";
        System.out.println(data1);
        ;
        OutputStream os1 = con1.getOutputStream();
        os1.write(data1.getBytes());
        os1.flush();
        os1.close();

        int responseCode1 = con1.getResponseCode();
        if (responseCode1 == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con1.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(response.toString());
            status = response.toString().substring(125, 133);
        } else {
            System.out.println("POST request not worked");
        }
        return status;
    }

    // ↓↓ Метод для возврата баланса пользователя
    public Integer returnUserBalance(String id) throws SQLException {
        ResultSet result = Database.getConnection().createStatement().executeQuery(" SELECT  FROM  where id =" + "'" + id + "'");
        while (result.next()) {
            balance = result.getInt("balance");
        }
        return balance;
    }

    // ↓↓ Выгрузка с баз данных

    public Integer returnCountOfPreviews(String prefix,Integer limit) throws SQLException {
        Connection connection = Database.getConnection();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                Database.previewList.clear();
                String query = "SELECT (*) FROM  WHERE   = '' AND name LIKE " + "'" + prefix + "%'"  + "LIMIT " + limit;
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    countOfObjectInDataBase = resultSet.getInt("count");
                }
            }
        }
        return countOfObjectInDataBase;
    }
    public static void loadEFT(String prefix, Integer limit) {
        Connection connection = Database.getConnection();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                Database.previewList.clear();
                String query = " SELECT * FROM  WHERE  LIKE " + "'" + prefix + "%'" + " and  = "+ "'FALSE'" +  "  LIMIT " + limit;

                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int price = resultSet.getInt("price");
                    String image = resultSet.getString("image");
                    String name = resultSet.getString("name");
                    String isDeleted = resultSet.getString("isDeleted");
                    Integer product_category_id = resultSet.getInt("product_category_id");
                    Integer game_category_id = resultSet.getInt("game_category_id");

                    Preview product = new Preview(id, price, image, name, isDeleted, product_category_id, game_category_id);

                    Database.previewList.add(product);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }


    // ↓↓ Метод для оптимизации
    public List<List<InlineKeyboardButton>> returnButtonsOfBuy(String prefix) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(i + " шт");
            button.setCallbackData(i + prefix);
            System.out.println(i + prefix);
            buttons.add(button);
        }
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        for (int i = 6; i <= 10; i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(i + " шт");
            button.setCallbackData(i + prefix);
            buttons1.add(button);
        }
        InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(back);
        List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(buttons, buttons1, row2);
        ;
        return rowcollection;
    }
    public List<List<InlineKeyboardButton>> returnBasicCheckBox(String url) {
        InlineKeyboardButton oplata = new InlineKeyboardButton();
        oplata.setText("Оплатить");
        oplata.setUrl(url);
        InlineKeyboardButton prverka = InlineButtonUtil.button("Проверить оплату", "check", "");
        List<InlineKeyboardButton> row1 = InlineButtonUtil.row(oplata);
        List<InlineKeyboardButton> row2 = InlineButtonUtil.row(prverka);
        List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row1, row2);
        return rowcollection;
    }


    // Методы для бота
    public void handleMessage(User user, Message message) throws Exception {
        if (message.hasText()) {
            handleText(user, message);
        }
    }
    private void handleText(User user, Message message) throws Exception {
        String text = message.getText();

        String chatId = String.valueOf(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        Long contact = message.getChatId();

        sendMessage.setChatId(String.valueOf(message.getChatId()));
        ResultSet resultSet = Database.getConnection().createStatement().executeQuery("Select id from Customer where id =" + "'" + contact.toString() + "'");
        if (text.equals("/start")) {
            if (!resultSet.next()) {
                customer.add(message.getChatId());
                Database.getConnection().createStatement().execute("INSERT INTO customer VALUES " + "(" + message.getChatId() + ",'" + message.getFrom().getUserName() + "'," + 0 + ")");
                sendMessage.setText("*✅ Вы успешно зарегистрировались!* \n Введите /start для продолжения. ");
                sendMessage.setParseMode(ParseMode.MARKDOWN);
                sendMessage.setChatId(chatId);
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
            } else {
                sendMessage.setText("Куда отправимся?");
                sendMessage.setChatId(chatId);
                sendMessage.setReplyMarkup(KeyboardUtil.contactMarkup());
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
            }
        }
        if (text.equals("Личный кабинет")) {
            ResultSet results = Database.getConnection().createStatement().executeQuery("Select * from Customer where id =" + "'" + user.getId() + "'");
            while (results.next()) {
                sendMessage.setText("*⚙️ Вы в соём личном кабинете*" +
                        "*\n 1. Ваш TELEGRAM-ID: *`" + contact + "`*\n 2. Ваш никнейм: *`" + results.getString("username") + "`*\n 3. Ваш баланс: *`" + results.getString("balance") + "`");
                sendMessage.setParseMode("Markdown");
            }
            sendMessage.setChatId(chatId);
            InlineKeyboardButton cache = InlineButtonUtil.button("Пополнение баланса", "Пополнение баланса", "\uD83D\uDCB3");
            InlineKeyboardButton so = InlineButtonUtil.button("Индивидуальный заказ", "Индивидуальный заказ", "\uD83D\uDC8E");
            List<InlineKeyboardButton> row3 = InlineButtonUtil.row(cache, so);
            List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row3);
            sendMessage.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }
        if (text.equals("Меню")) {
            sendMessage.setText("Привет! Это бот, который предоставит тебе *превью* для пролива. \n" + "Почему стоит покупать здесь? Да вот почему:\n" + "\n" + "\uD83D\uDCB0*Низкая* цена\n" + "\uD83C\uDF87*Сочные* картинки\n" + "✅Уникальность *100%*\n" + "\uD83D\uDC40*Большой* ассортимент\n" + "\uD83D\uDC4DМного довольных клиентов, которым я предлагал услугу");
            sendMessage.setParseMode("Markdown");
            sendMessage.setChatId(chatId);
            InlineKeyboardButton cl = InlineButtonUtil.button("Приобрести превью для залива", "Список категорий", "\uD83D\uDCC4");
            InlineKeyboardButton cache = InlineButtonUtil.button("Пополнение баланса", "Пополнение баланса", "\uD83D\uDCB3");
            InlineKeyboardButton faq = InlineButtonUtil.button("F.A.Q", "F.A.Q", "❓");
            InlineKeyboardButton so = InlineButtonUtil.button("Индивидуальный заказ", "Индивидуальный заказ", "\uD83D\uDC8E");
            List<InlineKeyboardButton> row = InlineButtonUtil.row(cl);
            List<InlineKeyboardButton> row2 = InlineButtonUtil.row(cache, faq);
            List<InlineKeyboardButton> row3 = InlineButtonUtil.row(so);
            List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row, row2, row3);
            sendMessage.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }

    }

    public void handleCallBack(User user, Message message, String data) throws SQLException, IOException {
        SendMessage sendMessage = new SendMessage();
        EditMessageText editMessageText = new EditMessageText();
        String chatId = String.valueOf(message.getChatId());
        if (data.equals("backmenu")) {
            editMessageText.setText("Привет! Это бот, который предоставит тебе *превью* для пролива. \n" + "Почему стоит покупать здесь? Да вот почему:\n" + "\n" + "\uD83D\uDCB0*Низкая* цена\n" + "\uD83C\uDF87*Сочные* картинки\n" + "✅Уникальность *100%*\n" + "\uD83D\uDC40*Большой* ассортимент\n" + "\uD83D\uDC4DМного довольных клиентов, которым я предлагал услугу");
            editMessageText.setParseMode("Markdown");
            editMessageText.setChatId(chatId);
            InlineKeyboardButton cl = InlineButtonUtil.button("Приобрести превью для залива", "Список категорий", "\uD83D\uDCC4");
            InlineKeyboardButton cache = InlineButtonUtil.button("Пополнение баланса", "Пополнение баланса", "\uD83D\uDCB3");
            InlineKeyboardButton faq = InlineButtonUtil.button("F.A.Q", "F.A.Q", "❓");
            InlineKeyboardButton so = InlineButtonUtil.button("Индивидуальный заказ", "Индивидуальный заказ", "\uD83D\uDC8E");
            List<InlineKeyboardButton> row = InlineButtonUtil.row(cl);
            List<InlineKeyboardButton> row2 = InlineButtonUtil.row(cache, faq);
            List<InlineKeyboardButton> row3 = InlineButtonUtil.row(so);
            List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row, row2, row3);
            editMessageText.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
            editMessageText.setMessageId(message.getMessageId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(editMessageText);
        }
        // ↓↓ Меню
        if (data.equals("Индивидуальный заказ")) {
            editMessageText.setText("❗️ Для индивидуального заказа вам необходимо обговорить всё с владельцем: https://t.me/sanurov");
            editMessageText.setParseMode("Markdown");
            editMessageText.setChatId(chatId);
            InlineKeyboardButton cl = InlineButtonUtil.button("Список категорий", "Список категорий", "\uD83D\uDCC4");
            InlineKeyboardButton cache = InlineButtonUtil.button("Пополнение баланса", "Пополнение баланса", "\uD83D\uDCB8");
            InlineKeyboardButton faq = InlineButtonUtil.button("F.A.Q", "F.A.Q", "❓");
            InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
            List<InlineKeyboardButton> row = InlineButtonUtil.row(cl);
            List<InlineKeyboardButton> row2 = InlineButtonUtil.row(cache, faq);
            List<InlineKeyboardButton> row3 = InlineButtonUtil.row(back);
            List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row, row2, row3);
            editMessageText.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
            editMessageText.setMessageId(message.getMessageId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(editMessageText);
        }
        if (data.equals("Список категорий")) {
            editMessageText.setText("❗️ Выберите нужную категорию");
            editMessageText.setParseMode("Markdown");
            editMessageText.setChatId(chatId);
            InlineKeyboardButton games = InlineButtonUtil.button("Игры", "Games", "\uD83D\uDD2B");
            InlineKeyboardButton softs = InlineButtonUtil.button("Приложения", "Softs", "\uD83D\uDDA5");
            InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
            List<InlineKeyboardButton> row = InlineButtonUtil.row(games, softs);
            List<InlineKeyboardButton> row2 = InlineButtonUtil.row(back);
            List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row, row2);
            editMessageText.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
            editMessageText.setMessageId(message.getMessageId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(editMessageText);
        }
        if (data.equals("F.A.Q")) {
            editMessageText.setText("❓FAQ❓\n" +
                    "\n" +
                    "- *Превью делается с помощью программы?*\n" +
                    "- Нет, абсолютно каждое превью делается с нуля, с помощью моих рук. \n" +
                    "\n" +
                    "- *Нет той тематики, которую я хотел бы заказать/хочу превью с лого своего сайта, что делать?*\n" +
                    "- Выберите пункт \"Индивидуальный заказ\", там вы связывайтесь напрямую со мной. Если ваша тематика популярна, то добавлю ее в список. Если нет, то сделаю вам отдельно. За превью с вашим лого беру небольшую наценку.\n" +
                    "\n" +
                    "- *Что если нет в наличии превью определенной тематики?*\n" +
                    "- Вы можете либо подождать(в течении суток будет пополнение), либо же отписать мне и я по возможности залью превью\n" +
                    "\n" + "- *Возникли проблемы с оплатой, куда мне обращаться? *\n" +
                    "- Вы можете связаться с разработчиком бота @nishanovhasan или с основателем @sanurov. Обычно такие проблемы решаются в течение 1-го рабочего дня");
            editMessageText.setParseMode("Markdown");
            editMessageText.setChatId(chatId);
            InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
            List<InlineKeyboardButton> row9 = InlineButtonUtil.row(back);
            List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row9);
            editMessageText.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
            editMessageText.setMessageId(message.getMessageId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(editMessageText);
        }
        // ↓↓ Баланс
        if (data.equals("Пополнение баланса")) {
            editMessageText.setText("*\uD83D\uDCB8 Выберите сервис*");
            editMessageText.setParseMode("Markdown");
            editMessageText.setChatId(chatId);
            InlineKeyboardButton crystal = InlineButtonUtil.button("\uD83D\uDC8E CRYSTAL PAY", "ОплатаCRYSTAL");
            InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
            List<InlineKeyboardButton> row = InlineButtonUtil.row(crystal);
            List<InlineKeyboardButton> row1 = InlineButtonUtil.row(back);
            List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row,row1);
            editMessageText.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
            editMessageText.setMessageId(message.getMessageId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(editMessageText);

        }
        if(data.equals("ОплатаCRYSTAL")){
            editMessageText.setText("*\uD83D\uDCB8 Выберите сумму пополнения*");
            editMessageText.setParseMode("Markdown");
            editMessageText.setChatId(chatId);
            InlineKeyboardButton c1 = InlineButtonUtil.button("100₽", "100₽", "");
            InlineKeyboardButton c2 = InlineButtonUtil.button("200₽", "200₽", "");
            InlineKeyboardButton c3 = InlineButtonUtil.button("300₽", "300₽", "");
            InlineKeyboardButton c4 = InlineButtonUtil.button("400₽", "400₽", "");
            InlineKeyboardButton c5 = InlineButtonUtil.button("500₽", "500₽", "");
            InlineKeyboardButton c6 = InlineButtonUtil.button("600₽", "600₽", "");
            InlineKeyboardButton c7 = InlineButtonUtil.button("700₽", "700₽", "");
            InlineKeyboardButton c8 = InlineButtonUtil.button("800₽", "800₽", "");
            InlineKeyboardButton c9 = InlineButtonUtil.button("900₽", "900₽", "");
            InlineKeyboardButton c10 = InlineButtonUtil.button("1000₽", "1000₽", "");
            InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
            List<InlineKeyboardButton> row1 = InlineButtonUtil.row(c1, c2, c3, c4, c5);
            List<InlineKeyboardButton> row2 = InlineButtonUtil.row(c6, c7, c8, c9, c10);
            List<InlineKeyboardButton> row = InlineButtonUtil.row(back);
            List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row1, row2, row);
            editMessageText.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
            editMessageText.setMessageId(message.getMessageId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(editMessageText);
        }
        for (int i = 100; i <= 1000; i++) {

            if (data.equals(i + "₽")) {
                numForPlusOfBalance = i;
                String url = returnUrlForPayment(i, chatId);
                editMessageText.setText("*\uD83D\uDCB8 Оплатите " + i + "₽ в течение 10-ти минут!*");
                editMessageText.setParseMode("Markdown");
                editMessageText.setChatId(chatId);
                editMessageText.setReplyMarkup(InlineButtonUtil.keyboard(returnBasicCheckBox(url)));
                editMessageText.setMessageId(message.getMessageId());
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(editMessageText);
            }

        }
        if (data.equals("check")) {
            String ans = returnStatusOfPayment(payment_id);
            System.out.println(ans);
            if (ans.equals("notpayed")) {
                sendMessage.setText("\uD83D\uDEABСчёт статуса: *Не оплачен* \n Оплатите заказ и нажмите на *Проверить* повторно!");
                sendMessage.setParseMode(ParseMode.MARKDOWN);
                sendMessage.setChatId(chatId);
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
            }
            if (ans.equals("payed")) {
                newBalance = returnUserBalance(chatId) + numForPlusOfBalance;
                Database.getConnection().createStatement().execute
                        ("delete from payment where tg_id =" + "'" + chatId + "'");
                DeleteMessage deleteMessage = new DeleteMessage(
                        chatId, message.getMessageId()
                );
                Database.getConnection().createStatement().execute
                        ("UPDATE customer SET balance=" + newBalance + " WHERE id=" + "'" + chatId + "'");

                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);
                sendMessage.setText("✅ *Успешно оплачено!*");
                sendMessage.setChatId(chatId);
                sendMessage.setParseMode(ParseMode.MARKDOWN);
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
            }

        }
        // ↓↓ Категории
        if (data.equals("Games")) {
            editMessageText.setText("* ✅ Выберите игру:*");
            editMessageText.setParseMode("Markdown");
            editMessageText.setChatId(chatId);
            editMessageText.setReplyMarkup(categoryInlineMarkupGames());
            editMessageText.setMessageId(message.getMessageId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(editMessageText);
        }
        if (data.equals("Softs")) {
            editMessageText.setText("* ✅ Выберите приложение:*");
            editMessageText.setParseMode("Markdown");
            editMessageText.setChatId(chatId);
            editMessageText.setReplyMarkup(InlineKeyboardUtil.categoryInlineMarkupApp());
            editMessageText.setMessageId(message.getMessageId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(editMessageText);
        }
        // ↓↓ Для покупки
        SendMessage order = new SendMessage();
        if (data.equals("Fortnite Hack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Fo")));
        }
        if (data.equals("Gta V Mod Menu")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Gt")));
        }
        if (data.equals("Fifa 23 Crack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Fi")));
        }
        if (data.equals("NBA 2k23")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("NB")));
        }
        if (data.equals("Valorant Hack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Va")));
        }
        if (data.equals("Genshin Hack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Ge")));
        }
        if (data.equals("Roblox synapse Hack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Ro")));
        }
        if (data.equals("Warzone 2 Hack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Wa")));
        }
        if (data.equals("Escape From Tarkov Hack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Es")));
        }
        if (data.equals("Photoshop Crack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Fo")));
        }
        if (data.equals("Premiere Pro Crack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Pr")));
        }
        if (data.equals("Spotify Crack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Sp")));
        }
        if (data.equals("After Effects Crack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Af")));
        }
        if (data.equals("Sony Vegas Pro 19 Crack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Sn")));
        }
        if (data.equals("Microsoft Office Crack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Mi")));
        }
        if (data.equals("Acrobat 2022 Crack")) {
            System.out.println(data);
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Ac")));
        }
        if (data.equals("Fl Studio 20 Crack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("FL")));
        }
        if (data.equals("Filmora 11 Crack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Fi")));
        }
        if (data.equals("Adobe Indesign Crack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Ad")));
        }
        if (data.equals("Autocad Crack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Au")));
        }
        if (data.equals("Lightroom Crack")) {
            order.setText("✅ *" + data + "*" + "\n Выберите количетсво для  ");
            naeed = data;
            order.setReplyMarkup(InlineButtonUtil.keyboard(returnButtonsOfBuy("Li")));
        }
        order.setChatId(chatId);
        order.setParseMode(ParseMode.MARKDOWN);
        ComponentContainer.MY_TELEGRAM_BOT.sendMsg(order);


        for (int i = 0; i <= 10; i++) {
            if (data.equals(i + data.substring(1, 3))) {
                System.out.println("ok");
                System.out.println(data);
                sendMessage.setText("♻️* Потвердите заказ: \n Название: " + naeed + " \n Количество: " + data.substring(0, 1) + "*");
                InlineKeyboardButton accept = InlineButtonUtil.button("Потвердить", data.substring(0, 1) + "Потвердить", "✅ ");
                System.out.println(accept);
                InlineKeyboardButton reject = InlineButtonUtil.button("Отклонить", "backmenu", "❌");
                List<InlineKeyboardButton> row1 = InlineButtonUtil.row(accept);
                List<InlineKeyboardButton> row2 = InlineButtonUtil.row(reject);
                List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row1, row2);
                sendMessage.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                sendMessage.setParseMode(ParseMode.MARKDOWN);
                sendMessage.setChatId(chatId);
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
            }
        }
        for (int i = 0; i <= 10; i++) {

            if (data.equals(i + "Потвердить")) {
                Integer balance = returnUserBalance(chatId);
                Integer minus = Integer.valueOf(i + "00");
                System.out.println("User balance" + balance);
                System.out.println("Balance minus sum " + minus);
                if (balance < minus) {
                    sendMessage.setText("*На вашем счёте не достаточно денег!*");
                    sendMessage.setParseMode(ParseMode.MARKDOWN);
                    sendMessage.setChatId(chatId);
                    InlineKeyboardButton cache = InlineButtonUtil.button("Пополнение баланса", "Пополнение баланса", "\uD83D\uDCB3");
                    InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                    List<InlineKeyboardButton> row = InlineButtonUtil.row(cache);
                    List<InlineKeyboardButton> row2 = InlineButtonUtil.row(back);
                    List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection(row, row2);
                    sendMessage.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                    ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

                }
                if (returnCountOfPreviews(naeed, i) < i) {
                    sendMessage.setText("*\uD83E\uDD15 К сожелению в разделе " + naeed + " осталось лишь " + returnCountOfPreviews(naeed, i) + " превью-ха(юх)* " +
                            "\n *Свяжитесь с администратором или дождитесь появление товаров(лучше уточнить у администрации*");
                    InlineKeyboardButton back = InlineButtonUtil.button("Назад ⬅️", "backmenu", "➡️");
                    List<InlineKeyboardButton> row2 = InlineButtonUtil.row(back);
                    List<List<InlineKeyboardButton>> rowcollection = InlineButtonUtil.collection( row2);
                    sendMessage.setReplyMarkup(InlineButtonUtil.keyboard(rowcollection));
                    sendMessage.setChatId(chatId);
                    sendMessage.setParseMode(ParseMode.MARKDOWN);
                    ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
                }

                else {

                    balance -= Integer.valueOf(i + "00");
                    System.out.println(balance);
                    Database.getConnection().createStatement().execute
                            ("UPDATE customer SET balance=" + balance + " WHERE id=" + "'" + chatId + "'");
                    System.out.println("UPDATE customer SET balance=" + balance + " WHERE id=" + "'" + chatId + "'");


                    System.out.println(data);
                    loadEFT(naeed, i);
                    for (Preview product : Database.previewList) {
                        SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                        sendPhoto.setParseMode(ParseMode.MARKDOWN);
                        sendPhoto.setChatId(chatId);
                        ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
                    }
                    String queryForDeleteAlreadyBuyPreview =
                            "UPDATE  SET  = 'TRUE' WHERE  LIKE '" + naeed + "%' AND id = (SELECT  FROM  WHERE  LIKE '" + naeed + "%' LIMIT "+ i + ")";
                    Database.getConnection().createStatement().execute(queryForDeleteAlreadyBuyPreview);

                }
            }
        }
    }
}