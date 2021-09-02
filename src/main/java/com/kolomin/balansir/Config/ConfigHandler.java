package com.kolomin.balansir.Config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;

@Service
@Data
@Slf4j
public class ConfigHandler {
    private static String configPath = "config.json";

    private JsonElement config;
    public static String thisHostPort;
    public static String QRsPath;

    public ConfigHandler() {
        readConfigFromfile();
    }

    private void readConfigFromfile() {
        Gson gson = new Gson();
        {
            try {
                config = new JsonParser().parse(new JsonReader(new FileReader(configPath)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        thisHostPort = config.getAsJsonObject().get("thisUrl").toString().substring(1,config.getAsJsonObject().get("thisUrl").toString().length() - 1);
        QRsPath = config.getAsJsonObject().get("QRsPath").toString().substring(1,config.getAsJsonObject().get("QRsPath").toString().length() - 1);
//        System.out.println(QRsPath);
    }
}
