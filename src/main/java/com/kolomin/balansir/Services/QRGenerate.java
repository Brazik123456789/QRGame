package com.kolomin.balansir.Services;

import com.kolomin.balansir.Entities.Event;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class QRGenerate {

    public QRGenerate() {
    }

    public String QRGenerate(String qr_url, String qrPath, String qrSuffix){
        log.info("Метод генерации QR-кода");
        File file = QRCode.from(qr_url).to(ImageType.PNG)
                .withSize(200, 200)
                .file();

        Path path = Paths.get(qrPath);

        if (Files.exists(path)) {
            // Если папка под QR существует
            log.debug("Добавляем QR в ранее созданный каталог мероприятия " + qrPath);
        } else {
            // Если папка не существует
            try {
                log.debug("Создаем новый каталог под мероприятие " + qrPath);
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        path = Paths.get(path + "/" + qrSuffix + ".png");

        if(Files.exists(path)){
            log.debug("Удалили ранее существующий PNG с именем" + qr_url);
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            log.debug("Копируем (добавляем) QR-код в новый файл PNG");
            Files.copy(file.toPath(), path);
        } catch (IOException e) {
            e.printStackTrace();
        }



//        return path.toString().substring(25).replace("\\", "/");
        return path.toString().replace("\\", "/");
    }

}
