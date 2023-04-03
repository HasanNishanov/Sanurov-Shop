package com.company.container;

import com.company.AvtoBot;
import com.company.enums.AdminStatus;
import com.company.enums.UserStatus;
import com.company.model.Preview;


import java.util.HashMap;
import java.util.Map;

public abstract class ComponentContainer {

    public static final String BOT_TOKEN = "";
    public static final String BOT_NAME = "";

    public static final String ADMIN_ID = "";

    public static AvtoBot MY_TELEGRAM_BOT;

    public static Map<String, Preview> productMap = new HashMap<>();

    public static Map<String, AdminStatus> productStepMap = new HashMap<>();



}
