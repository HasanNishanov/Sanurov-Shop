package com.company;


import com.company.container.ComponentContainer;
import com.company.controller.AdminController;
import com.company.controller.UserController;
import com.company.model.Customer;
import com.company.service.CustomerService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;


public class AvtoBot extends TelegramLongPollingBot {
    public UserController userController = new UserController();
    public AdminController adminController = new AdminController();

    public AvtoBot() throws URISyntaxException {
    }

    @Override
    public String getBotUsername() {
        return ComponentContainer.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return ComponentContainer.BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

            System.out.println("Message");
            Message message = update.getMessage();
            SendMessage sendMessage = new SendMessage();
            String chatId = String.valueOf(message.getChatId());
           String message1 =  update.getMessage().getText();
            User user = message.getFrom();


            if (String.valueOf(user.getId()).equals(ComponentContainer.ADMIN_ID)) {
                try {
                    System.out.println("ADMIN IS HERE");
                    adminController.handleMessage(user, message);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                try {
                    userController.handleMessage(user, message);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if (message1.startsWith("@") && message1.length() <=25) {

                String contact = String.valueOf(message.getFrom().getId());
                String customerId = contact;

                Customer customer = CustomerService.getCustomerById(customerId);
                if (customer == null) {
                    customer = new Customer(customerId, message1, "0");
                    CustomerService.addCustomer(customer);
                }

                if(customer != null){
                    sendMessage.setText("✅ * Регистрация прошла успешна! Введите /go - для просмотра функционала бота! *");
                    sendMessage.setParseMode("Markdown");
                    sendMessage.setChatId(chatId);
                    ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
                    }
            }
           else if (message1.startsWith("@") && message1.length() >25 ){
               sendMessage.setText("\uD83D\uDC4B * Ваш ник больше 25 символов!" +
                       "\n  Введи свой ник в формате: @nickname* _(ограничения в 25 символов)_");
               sendMessage.setParseMode("Markdown");
               sendMessage.setChatId(chatId);
               ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
           }


        }
        if (update.hasCallbackQuery()) {
            System.out.println("CallbackQuery");
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Message message = callbackQuery.getMessage();
            User user = callbackQuery.getFrom();
            String data = callbackQuery.getData();
            if (String.valueOf(user.getId()).equals(ComponentContainer.ADMIN_ID)) {
                try {
                    adminController.handleCallBack(user, message, data);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            } else {
                try {
                    userController.handleCallBack(user, message, data);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }






    public void sendMsg(Object message) {

        try {
            if (message instanceof SendMessage) {
                execute((SendMessage) message);
            }
            if (message instanceof SendDocument) {
                execute((SendDocument) message);
            }
            if (message instanceof DeleteMessage) {
                execute((DeleteMessage) message);
            }
            if (message instanceof EditMessageText) {
                execute((EditMessageText) message);
            }
            if (message instanceof SendSticker) {
                execute((SendSticker) message);
            }
            if (message instanceof SendPhoto) {
                execute((SendPhoto) message);
            }
            if (message instanceof SendDice) {
                execute((SendDice) message);
            }
            if (message instanceof SendVenue) {
                execute((SendVenue) message);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("Xatolik bor");
        }
    }
}


