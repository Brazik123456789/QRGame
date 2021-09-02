package com.kolomin.balansir.Services;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.kolomin.balansir.Config.ConfigHandler.QRsPath;

@Service
public class QRGenerate {

    public QRGenerate() {
    }

    public String QRGenerate(String url, String qrSuffix, String eventName){
        System.out.println("Метод генерации QR-кода");
        File file = QRCode.from(url).to(ImageType.PNG)
                .withSize(200, 200)
                .file();

//        Path path = Paths.get("src/main/resources/static/urls/" + eventName);
        Path path = Paths.get(QRsPath + eventName);

        if (Files.exists(path)) {
            // Если папка под QR существует
            System.out.println("Добавляем QR в ранее созданный каталог мероприятия " + eventName);
        } else {
            // Если папка не существует
            try {
                System.out.println("Создаем новый каталог под мероприятие " + eventName);
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        path = Paths.get(path + "/" + qrSuffix + ".png");

        if(Files.exists(path)){
            System.out.println("Удалили ранее существующий PNG с именем" + url);
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            System.out.println("Копируем QR-код в новый файл PNG");
            Files.copy(file.toPath(), path);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        return path.toString().substring(25).replace("\\", "/");
        return path.toString().replace("\\", "/");
    }

}
