package com.kolomin.balansir.Config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;

@Service
@Data
@Slf4j
public class ConfigHandler {
    private static String configPath = "config.json";

    private JsonElement config;
    public static String thisHostPort;
    public static String QRsPath;
    public static String beforeQRsPath;
    public static String defaultResource;
    public static Date defaultResourceDate;

    public ConfigHandler() {
        readConfigFromfile();
    }

    private void readConfigFromfile() {
        {
            try {
                config = new JsonParser().parse(new JsonReader(new FileReader(configPath)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        thisHostPort = config.getAsJsonObject().get("thisUrl").toString().substring(1,config.getAsJsonObject().get("thisUrl").toString().length() - 1);
        QRsPath = config.getAsJsonObject().get("QRsPath").toString().substring(1,config.getAsJsonObject().get("QRsPath").toString().length() - 1);
        beforeQRsPath = config.getAsJsonObject().get("beforeQRsPath").toString().substring(1,config.getAsJsonObject().get("beforeQRsPath").toString().length() - 1);
        defaultResource = config.getAsJsonObject().get("defaultResource").toString().substring(1,config.getAsJsonObject().get("defaultResource").toString().length() - 1);
        defaultResourceDate = new Date();
    }
}
