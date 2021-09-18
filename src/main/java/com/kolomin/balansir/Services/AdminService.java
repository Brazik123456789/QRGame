package com.kolomin.balansir.Services;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ibm.icu.text.Transliterator;
import com.kolomin.balansir.Entities.Event;
import com.kolomin.balansir.Entities.QR;
import com.kolomin.balansir.Entities.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import static com.kolomin.balansir.Config.ConfigHandler.QRsPath;
import static com.kolomin.balansir.Config.ConfigHandler.thisHostPort;

/**
 * Сервис для обработки данных администратора
 * */
@Service
public class AdminService {

    private EventSevice eventSevice;
    private QRService qrService;
    private ResourceService resourceService;
    private QRGenerate qrGenerate;
    public static SimpleDateFormat myFormat;
    public static final String CYRILLIC_TO_LATIN = "Cyrillic-Latin";
    Transliterator toLatinTrans;

    @Autowired
    public AdminService(EventSevice eventSevice, QRService qrService, ResourceService resourceService, QRGenerate qrGenerate) {
        this.eventSevice = eventSevice;
        this.qrService = qrService;
        this.resourceService = resourceService;
        this.qrGenerate = qrGenerate;
        this.myFormat = new SimpleDateFormat("dd-MM-yyyy");
        this.toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);;
    }

    /**
     * Данный метод ищет в БД дубликаты на суффикс(хвост урла дл QR-кода)
     * На этот маппинг кидает запросы фронт каждые 0.5 сек, и если есть дубликаты - подсвечивает введенный пользователем хвост красным
     * */
    public String searchSuffixInDB(String suffix, Long id) {
        System.out.println("Запрос на сравнение суффикса " + suffix +" с БД");

        if (id == 0){
            if (qrService.existsByQRSuffix(suffix)){
                System.out.println("{\"exist\": " + true + ", \"suffix\": \"" + suffix + "\"}");
                return "{\"exist\": " + true + ", \"suffix\": \"" + suffix + "\"}";
            } else {
                System.out.println("{\"exist\": " + false + ", \"suffix\": \"" + suffix + "\"}");
                return "{\"exist\": " + false + ", \"suffix\": \"" + suffix + "\"}";
            }
        } else {    //  суффикса есть в БД (изменение существующего суффикса)
            if (qrService.getById(id).getQr_suffix().equals(suffix)){
                System.out.println("{\"exist\": " + false + ", \"suffix\": \"" + suffix + "\"}");
                return "{\"exist\": " + false + ", \"suffix\": \"" + suffix + "\"}";
            } else {
                if (qrService.existsByQRSuffix(suffix)){
                    System.out.println("{\"exist\": " + true + ", \"suffix\": \"" + suffix + "\"}");
                    return "{\"exist\": " + true + ", \"suffix\": \"" + suffix + "\"}";
                } else {
                    System.out.println("{\"exist\": " + false + ", \"suffix\": \"" + suffix + "\"}");
                    return "{\"exist\": " + false + ", \"suffix\": \"" + suffix + "\"}";
                }
            }
        }
    }

    /**
     * Данный метод ищет в БД дубликаты на внешний ресурс(урл до ботов например)
     * На этот маппинг кидает запросы фронт каждые 0.5 сек, и если есть дубликаты - подсвечивает введенный пользователем адрес красным
     * */
    public String searchUrlInDB(String suffix, Long id) {
        System.out.println("Запрос на сравнение урла внешнего ресурса " + suffix + " с БД");
//        if (resourceService.findUrl(suffix)){
//            System.out.println("{\"exist\": " + true + "}");
//            return "{\"exist\": " + true + "}";
//        } else {
//            System.out.println("{\"exist\": " + false + "}");
//            return "{\"exist\": " + false + "}";
//        }

        if (id == 0){
            if (resourceService.findUrl(suffix)){
                System.out.println("{\"exist\": " + true + ", \"suffix\": \"" + suffix + "\"}");
                return "{\"exist\": " + true + ", \"suffix\": \"" + suffix + "\"}";
            } else {
                System.out.println("{\"exist\": " + false + ", \"suffix\": \"" + suffix + "\"}");
                return "{\"exist\": " + false + ", \"suffix\": \"" + suffix + "\"}";
            }
        } else {   //  урл есть в БД (изменение существующего суффикса)
            if (resourceService.getById(id).getUrl().equals(suffix)){
                System.out.println("{\"exist\": " + false + ", \"suffix\": \"" + suffix + "\"}");
                return "{\"exist\": " + false + ", \"suffix\": \"" + suffix + "\"}";
            } else {
                if (resourceService.findUrl(suffix)){
                    System.out.println("{\"exist\": " + true + ", \"suffix\": \"" + suffix + "\"}");
                    return "{\"exist\": " + true + ", \"suffix\": \"" + suffix + "\"}";
                } else {
                    System.out.println("{\"exist\": " + false + ", \"suffix\": \"" + suffix + "\"}");
                    return "{\"exist\": " + false + ", \"suffix\": \"" + suffix + "\"}";
                }
            }
        }
    }

    /**
     * Данный метод вытаскивает из БД все неудаленные мероприятия для показа на главной странице
     * */
    public String getAllNotDeletedEvents() {
        return eventSevice.findAllNotDeleted().toString();
    }

    /**
     * Данный метод вытаскивает из БД все удаленные мероприятия для показа в корзине
     * */
    public String getAllDeletedEvents() {
        return eventSevice.findAllDeleted().toString();
    }

    /**
     * Данный метод выдаёт полную информацию по определенному мероприятию
     * */
    public String getInfoByEventId(Long id) {
        Event event = eventSevice.getEventById(id);
        System.out.println("Запрос информации по мероприятию " + event.getName());
        return event.toString();
    }

    /**
     * Данный метод добавляет в БД новое мероприятие
     * */
    public String addEvent(HttpEntity<String> params) {
        System.out.println("Запрос на добавление мероприятия");
        String response = "{\"success\": " + true + "\n";

        JsonElement request = new JsonParser().parse(params.getBody());
        System.out.println("requestBody" + request);

        Event newEvent = new Event();
        if (eventSevice.findEventName(request.getAsJsonObject().get("name").toString().replaceAll("\"",""))){
            return "{\"success\": \"false\", \"errorText\": \"Мероприятие с названием " + request.getAsJsonObject().get("name").toString().replaceAll("\"","") + " уже существует. Мероприятие не добавлено.\"}";
        }
        newEvent.setName(request.getAsJsonObject().get("name").toString().replaceAll("\"",""));
        newEvent.setCity(request.getAsJsonObject().get("city").toString().replaceAll("\"",""));
        try {
            newEvent.setDate(myFormat.parse(request.getAsJsonObject().get("date").toString().replaceAll("\"","")));
        } catch (ParseException e) {
            System.out.println("Ошибка в конвертации даты");
            return "{\"success\": \"false\", \"errorText\": \"Ошибка в конвертации даты. Мероприятие не добавлено.\"}";
        }
        newEvent.setArea(request.getAsJsonObject().get("area").toString().replaceAll("\"",""));
        newEvent.setDeleted(false);
        newEvent.setQrs(new ArrayList<>());
        //  создаю каталог для папки
        String [] dateForPath = request.getAsJsonObject().get("date").toString().replaceAll("\"","").split("-");
        File qr_path = new File(toLatinTrans.transliterate(QRsPath  + newEvent.getName() + newEvent.getCity() + newEvent.getArea() + dateForPath[0] + dateForPath[1]+ dateForPath[2]));
        qr_path.mkdir();    //  создали каталог
        newEvent.setQr_path(qr_path.toString().replace("\\", "/"));
        eventSevice.saveOrUpdate(newEvent);

//        for (JsonElement qr: request.getAsJsonObject().get("QRs").getAsJsonArray()) {
        for (JsonElement qr: request.getAsJsonObject().get("qrs").getAsJsonArray()) {
            QR newQR = new QR();
            if (qrService.existsByQRSuffix(qr.getAsJsonObject().get("qr_suffix").toString().replaceAll("\"",""))){
                response += ",\"errorText\": \"Суффикс " + qr.getAsJsonObject().get("qr_suffix").toString().replaceAll("\"","") + " уже существует. Данный суффикс не добавился\",\n";
                continue;
            }
            newQR.setQr_suffix(qr.getAsJsonObject().get("qr_suffix").toString().replaceAll("\"",""));
            newQR.setQr_url(thisHostPort + newQR.getQr_suffix());
            newQR.setEvent(newEvent);
            if (qr.getAsJsonObject().get("team").toString().equals("true")){
                newQR.setTeam(true);
                newQR.setTeam_for_front(true);
            } else {
                newQR.setTeam(false);
                newQR.setTeam_for_front(false);
            }
            newQR.setDeleted(false);
            newQR.setDefault_resource_people_count(0L);
            newQR.setResources(new ArrayList<>());
            qrService.saveOrUpdate(newQR);
            qrGenerate.QRGenerate(newQR.getQr_url(), newEvent.getQr_path(), newQR.getQr_suffix());

            ArrayList<Resource> resources = new ArrayList<>();

            for (JsonElement resource: qr.getAsJsonObject().get("resources").getAsJsonArray()) {
                Resource newResource = new Resource();
                if (resourceService.findUrl(resource.getAsJsonObject().get("url").toString().replaceAll("\"",""))){
                    response += ",\"errorText\": \"Ресурс " + resource.getAsJsonObject().get("url").toString().replaceAll("\"","") + " уже существует. Данный ресурс не добавился\",\n";
                    continue;
                }
                newResource.setQr(newQR);
                newResource.setQr_suffix(newQR.getQr_suffix());
                if (resource.getAsJsonObject().get("people_count").toString().replaceAll("\"","").equals("")){
                    newResource.setInfinity(true);
                    newResource.setPeople_count(0L);
                } else {
                    newResource.setInfinity(false);
                    newResource.setPeople_count(Long.valueOf(resource.getAsJsonObject().get("people_count").toString().replaceAll("\"","")));
                }
                newResource.setUrl(resource.getAsJsonObject().get("url").toString().replaceAll("\"",""));
                newResource.setCame_people_count(0L);
                newResource.setDeleted(false);

                if (newResource.isInfinity()){      //  тут добавляю все ресурсы в лист чтоб бесконечные оказались в самом конце массива и при вставке в БД оказались в самом низу
                    resources.add(newResource);     //  таким образом для некомандного QR-кода люди сначала перейдут на всех конечных, потом на бесконечных
                } else {
                    resources.add(0, newResource);
                }

//                resourceService.saveOrUpdate(newResource);
//                newQR.getResources().add(newResource);
            }

            for (Resource newResource:resources) {
                resourceService.saveOrUpdate(newResource);
                newQR.getResources().add(newResource);
            }

            qrService.saveOrUpdate(newQR);
            newEvent.getQrs().add(newQR);

            System.out.println("newQR\n" + newQR);
        }

        eventSevice.saveOrUpdate(newEvent);
        System.out.println("newEvent saved\n" + newEvent);

        return response += "}";
    }

    /**
     * Данный метод полностью удаляет мероприятия и все его зависимости
     * */
    public String deleteEvent(Long id) {
        Event event = eventSevice.getEventById(id);
        System.out.println("Запрос на полное удаление мероприятия \"" + event.getName() + "\" из БД");
        String eventName = event.getName();
        String eventCity = event.getCity();
        String eventArea = event.getArea();
        String eventDate = event.getDate().toString();
        recursiveDelete(new File(event.getQr_path() + "/"));   //  удаляем папку с QR-кодами .png данного мероприятия

        eventSevice.deleteEvent(event);     //  удаляем полностью мероприятие со всеми зависимостями
        System.out.println("Мероприятие \"" + eventName + " " + eventCity + " " + eventArea + " " + eventDate +"\" полностью удалено из БД");
        return "{\"success\": true}";
    }

    /**
     * Данный метод изменяет существующее мероприятие в БД
     * */
    public String editEvent(Long id, HttpEntity<String> params){
//        Event event = eventSevice.getEventById(id);
//        System.out.println("Запрос на изменение мероприятия \"" + event.getName() + "\"");
//        deleteEvent(id);    //  для простоты и лишней ебли полностью удаляю существующее мероприятие и добавляю его заново уже обновленным
//        return addEvent(params);
        Event oldEvent = eventSevice.getEventById(id);
        System.out.println("oldEvent :" + oldEvent);
        System.out.println("Запрос на изменение мероприятия \"" + oldEvent.getName() + "\"");
        JsonElement request = new JsonParser().parse(params.getBody());
        System.out.println("requestBody" + request);

        String response = "{\"success\": " + true + "\n";

        oldEvent.setName(request.getAsJsonObject().get("name").toString().replaceAll("\"",""));
        oldEvent.setCity(request.getAsJsonObject().get("city").toString().replaceAll("\"",""));
        try {
            oldEvent.setDate(myFormat.parse(request.getAsJsonObject().get("date").toString().replaceAll("\"","")));
        } catch (ParseException e) {
            System.out.println("Ошибка в конвертации даты");
            return "{\"success\": \"false\", \"errorText\": \"Ошибка в конвертации даты. Мероприятие не добавлено.\"}";
        }
        oldEvent.setArea(request.getAsJsonObject().get("area").toString().replaceAll("\"",""));
        oldEvent.setDeleted(false);

        //  удаляем папку со старыми QR-кодами и генерируем новый каталог
        recursiveDelete(new File(oldEvent.getQr_path() + "/"));   //  удаляем папку с QR-кодами .png данного мероприятия
        //  создаю каталог для папки
        String [] dateForPath = request.getAsJsonObject().get("date").toString().replaceAll("\"","").split("-");
        File qr_path = new File(toLatinTrans.transliterate(QRsPath  + oldEvent.getName() + oldEvent.getCity() + oldEvent.getArea() + dateForPath[0] + dateForPath[1]+ dateForPath[2]));
        qr_path.mkdir();    //  создали каталог
        oldEvent.setQr_path(qr_path.toString().replace("\\", "/"));
//        oldEvent.setQrs(new ArrayList<>());
        eventSevice.saveOrUpdate(oldEvent);


        ArrayList<QR> oldAndNewQrs = new ArrayList<>();   //  лист для айдишников пришедших суффиксов, чтобы потом удалить из БД те суффиксы, айдишники которых не будут присутствовать здесь
        ArrayList<Resource> oldAndNewResources = new ArrayList<>();   //  лист для айдишников пришедших ресурсов, чтобы потом удалить из БД те ресурсы, айдишники которых не будут присутствовать здесь

        for (JsonElement qr: request.getAsJsonObject().get("qrs").getAsJsonArray()) {
            if (!qr.getAsJsonObject().get("id").toString().replaceAll("\"", "").equals("add")) {  //  если существующий код
                QR oldQR = qrService.getById(Long.valueOf(qr.getAsJsonObject().get("id").toString().replaceAll("\"", "")));
                oldAndNewQrs.add(oldQR);
                oldQR.setQr_suffix(qr.getAsJsonObject().get("qr_suffix").toString().replaceAll("\"", ""));
                oldQR.setQr_url(thisHostPort + oldQR.getQr_suffix());
//                oldQR.setEvent(oldEvent);
                if (qr.getAsJsonObject().get("team").toString().replaceAll("\"", "").equals("true")) {
                    oldQR.setTeam(true);
                    oldQR.setTeam_for_front(true);
                } else {
                    oldQR.setTeam(false);
                    oldQR.setTeam_for_front(false);
                }

//                if (qr.getAsJsonObject().get("teamForFront").toString().replaceAll("\"", "").equals("true")) {
//                    oldQR.setTeam_for_front(true);
//                } else {
//                    oldQR.setTeam_for_front(false);
//                }

                oldQR.setDeleted(false);
                oldQR.setDefault_resource_people_count(Long.valueOf(qr.getAsJsonObject().get("default_resource_people_count").toString()));
//                oldQR.setResources(new ArrayList<>());
                qrService.saveOrUpdate(oldQR);

                qrGenerate.QRGenerate(oldQR.getQr_url(), oldEvent.getQr_path(), oldQR.getQr_suffix());

                ArrayList<Resource> resources = new ArrayList<>();

                for (JsonElement resource : qr.getAsJsonObject().get("resources").getAsJsonArray()) {
                    if (!resource.getAsJsonObject().get("id").toString().replaceAll("\"", "").equals("add")) {  //  если существующий ресурс
                        Resource oldResource = resourceService.getById(Long.valueOf(resource.getAsJsonObject().get("id").toString().replaceAll("\"", "")));

//                        oldResource.setQr(oldQR);
                        oldResource.setQr_suffix(oldQR.getQr_suffix());
                        if (resource.getAsJsonObject().get("people_count").toString().replaceAll("\"", "").equals("")) {
                            oldResource.setInfinity(true);
                            oldResource.setPeople_count(0L);
                        } else {
                            oldResource.setInfinity(false);
                            oldResource.setPeople_count(Long.valueOf(resource.getAsJsonObject().get("people_count").toString().replaceAll("\"", "")));
                        }
                        oldResource.setUrl(resource.getAsJsonObject().get("url").toString().replaceAll("\"", ""));
                        oldResource.setDeleted(false);

                        if (oldResource.isInfinity()) {      //  тут добавляю все ресурсы в лист чтоб бесконечные оказались в самом конце массива и при вставке в БД оказались в самом низу
                            resources.add(oldResource);     //  таким образом для некомандного QR-кода люди сначала перейдут на всех конечных, потом на бесконечных
                        } else {
                            resources.add(0, oldResource);
                        }
                    } else {
                        Resource newResource = new Resource();
                        if (resourceService.findUrl(resource.getAsJsonObject().get("url").toString().replaceAll("\"", ""))) {
                            response += ",\"errorText\": \"Ресурс " + resource.getAsJsonObject().get("url").toString().replaceAll("\"", "") + " уже существует. Данный ресурс не добавился\",\n";
                            continue;
                        }
                        newResource.setQr(oldQR);
                        newResource.setQr_suffix(oldQR.getQr_suffix());
                        if (resource.getAsJsonObject().get("people_count").toString().replaceAll("\"", "").equals("")) {
                            newResource.setInfinity(true);
                            newResource.setPeople_count(0L);
                        } else {
                            newResource.setInfinity(false);
                            newResource.setPeople_count(Long.valueOf(resource.getAsJsonObject().get("people_count").toString().replaceAll("\"", "")));
                        }
                        newResource.setUrl(resource.getAsJsonObject().get("url").toString().replaceAll("\"", ""));
                        newResource.setCame_people_count(0L);
                        newResource.setDeleted(false);

                        if (newResource.isInfinity()) {      //  тут добавляю все ресурсы в лист чтоб бесконечные оказались в самом конце массива и при вставке в БД оказались в самом низу
                            resources.add(newResource);     //  таким образом для некомандного QR-кода люди сначала перейдут на всех конечных, потом на бесконечных
                        } else {
                            resources.add(0, newResource);
                        }
                    }
                }

                for (Resource newResource : resources) {
                    resourceService.saveOrUpdate(newResource);
                    oldAndNewResources.add(newResource);
                    if (!oldQR.getResources().contains(newResource))
                        oldQR.getResources().add(newResource);
                }

                qrService.saveOrUpdate(oldQR);
            }
            else {    //  если не существующий код
                QR newQR = new QR();
                if (qrService.existsByQRSuffix(qr.getAsJsonObject().get("qr_suffix").toString().replaceAll("\"",""))){
                    response += ",\"errorText\": \"Суффикс " + qr.getAsJsonObject().get("qr_suffix").toString().replaceAll("\"","") + " уже существует. Данный суффикс не добавился\",\n";
                    continue;
                }
                newQR.setQr_suffix(qr.getAsJsonObject().get("qr_suffix").toString().replaceAll("\"",""));
                newQR.setQr_url(thisHostPort + newQR.getQr_suffix());
                newQR.setEvent(oldEvent);
                if (qr.getAsJsonObject().get("team").toString().equals("true")){
                    newQR.setTeam(true);
                    newQR.setTeam_for_front(true);
                } else {
                    newQR.setTeam(false);
                    newQR.setTeam_for_front(false);
                }
                newQR.setDeleted(false);
                newQR.setDefault_resource_people_count(0L);
                newQR.setResources(new ArrayList<>());
                qrService.saveOrUpdate(newQR);
                oldAndNewQrs.add(newQR);
                qrGenerate.QRGenerate(newQR.getQr_url(), oldEvent.getQr_path(), newQR.getQr_suffix());

                ArrayList<Resource> resources = new ArrayList<>();

                for (JsonElement resource: qr.getAsJsonObject().get("resources").getAsJsonArray()) {
                    Resource newResource = new Resource();
                    if (resourceService.findUrl(resource.getAsJsonObject().get("url").toString().replaceAll("\"",""))){
                        response += ",\"errorText\": \"Ресурс " + resource.getAsJsonObject().get("url").toString().replaceAll("\"","") + " уже существует. Данный ресурс не добавился\",\n";
                        continue;
                    }
                    newResource.setQr(newQR);
                    newResource.setQr_suffix(newQR.getQr_suffix());
                    if (resource.getAsJsonObject().get("people_count").toString().replaceAll("\"","").equals("")){
                        newResource.setInfinity(true);
                        newResource.setPeople_count(0L);
                    } else {
                        newResource.setInfinity(false);
                        newResource.setPeople_count(Long.valueOf(resource.getAsJsonObject().get("people_count").toString().replaceAll("\"","")));
                    }
                    newResource.setUrl(resource.getAsJsonObject().get("url").toString().replaceAll("\"",""));
                    newResource.setCame_people_count(0L);
                    newResource.setDeleted(false);

                    if (newResource.isInfinity()){      //  тут добавляю все ресурсы в лист чтоб бесконечные оказались в самом конце массива и при вставке в БД оказались в самом низу
                        resources.add(newResource);     //  таким образом для некомандного QR-кода люди сначала перейдут на всех конечных, потом на бесконечных
                    } else {
                        resources.add(0, newResource);
                    }
                }

                for (Resource newResource:resources) {
                    resourceService.saveOrUpdate(newResource);
                    oldAndNewResources.add(newResource);
                    if (!newQR.getResources().contains(newResource))
                        newQR.getResources().add(newResource);
                }

                qrService.saveOrUpdate(newQR);
                oldEvent.getQrs().add(newQR);
            }
        }

        eventSevice.saveOrUpdate(oldEvent);

        for (int i = 0; i < oldEvent.getQrs().size(); i++) {
            QR qr = oldEvent.getQrs().get(i);
            if (!oldAndNewQrs.contains(qr)){
                oldEvent.getQrs().remove(qr);
            } else {
                for (Resource res:  qr.getResources()) {
                    System.out.println("res" + res);
                    if (!oldAndNewResources.contains(res)) {
                        qr.getResources().remove(res);
                        qrService.saveOrUpdate(qr);
                    }
                }
            }
        }

        eventSevice.saveOrUpdate(oldEvent);
        System.out.println("oldEvent saved\n" + oldEvent);

        return response += "}";
    }

    /**
     * Данный метод переводит флаг deleted в true/false у мероприятия и всех его зависимостей
     * Показываем/скрываем мероприятие в/из корзины, убираем/добавляем на главную страницу
     * */
    public String deleteActiveEventOrRestoreEvent(Long id, boolean flag) {
        Event event = eventSevice.getEventById(id);
        if (flag){
            System.out.println("Запрос на удаление активного мероприятия \"" + event.getName() + " " + event.getCity() + " " + event.getArea() + " " + event.getDate().toString().split(" ")[0] +"\"");
        } else {
            System.out.println("Запрос на восстановление мероприятия \"" + event.getName() + " " + event.getCity() + " " + event.getArea() + " " + event.getDate().toString().split(" ")[0] +"\" из корзины");
        }

        event.setDeleted(flag); //  ставим флаг удаленного мероприятия

        for (QR qr:event.getQrs()) {
            qr.setDeleted(flag);    //  ставим флаг удаленного QR-кода
            for (Resource r:qr.getResources()) {
                r.setDeleted(flag); //  ставим флаг удаленного внешнего ресурса
                resourceService.saveOrUpdate(r);
                if (flag){
                    System.out.println("Перенесли внешний ресурс \"" + r.getUrl() + "\" в корзину");
                } else {
                    System.out.println("Восстановили внешний ресурс \"" + r.getUrl() + "\" из корзины");
                }
            }
            qrService.saveOrUpdate(qr);
            if (flag){
                System.out.println("Перенесли QR-код \"" + qr.getQr_url() + "\" в корзину");
            } else {
                System.out.println("Восстановили QR-код \"" + qr.getQr_url() + "\" из корзины");
            }
        }

        eventSevice.saveOrUpdate(event);
        if (flag){
            System.out.println("Перенесли мероприятие \"" + event.getName() + " " + event.getCity() + " " + event.getArea() + " " + event.getDate().toString().split(" ")[0] +"\" в корзину");
        } else {
            System.out.println("Восстановили мероприятие \"" + event.getName() + " " + event.getCity() + " " + event.getArea() + " " + event.getDate().toString().split(" ")[0] +"\" из корзины");
        }

        return "{\"success\": true}";
    }

    /**
     * Данный метод полностью очищает БД
     * */
    public String deleteConfig(){
        eventSevice.deleteAll();
        recursiveDelete(new File(QRsPath));
        System.out.println("Текущий конфиг очищен");
        return "{\"success\": true}";
    }

    /**
     * Данный метод удаляет .png и всех папок мероприятия из папки с QR-кодами
     * */
    public static void recursiveDelete(File file) {
        // до конца рекурсивного цикла
        if (!file.exists())
            return;

        //если это папка, то идем внутрь этой папки и вызываем рекурсивное удаление всего, что там есть
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                // рекурсивный вызов
                recursiveDelete(f);
            }
        }
        // вызываем метод delete() для удаления файлов и пустых(!) папок
        if (!file.getAbsolutePath().endsWith(QRsPath.substring(0,QRsPath.length()-1))){     //  прописать без / в конце
            file.delete();
            System.out.println("Удаленный файл или папка: " + file.getAbsolutePath());
        }
    }

    /**
     * Данный метод вытаскивает мероприятия из БД согласно пришедшим данным из фильтра
     * */
    public String eventsFilter(Map<String, String> map) {
        System.out.println(map.toString());
        String name = "%";
        String city = "%";
        String area = "%";
        String date = "%";
        boolean deleted;
        int count = 0;

        if (map.containsKey("name") && !map.get("name").isEmpty()) {
            name = map.get("name");
            count++;
        }
        if (map.containsKey("city") && !map.get("city").isEmpty()) {
            city = map.get("city");
            count++;
        }
        if (map.containsKey("area") && !map.get("area").isEmpty()) {
            area = map.get("area");
            count++;
        }
        if (map.containsKey("date") && !map.get("date").isEmpty()) {
            String [] dateArr =  map.get("date").split("-");
            date = dateArr[2] + "-" + dateArr[1] + "-" + dateArr[0];
            count++;
        }

        deleted = Boolean.valueOf(map.get("deleted"));

        if (count > 0){
            if (deleted){
                return eventSevice.findAllByQueryDeletedTrue(name, city, area, date).toString();
            } else {
                return eventSevice.findAllByQueryDeletedFalse(name, city, area, date).toString();
            }
        } else {
            if (deleted){
                return getAllDeletedEvents();
            } else {
                return getAllNotDeletedEvents();
            }
        }
    }

    /**
     * Данный метод обнуляет кол-во пришедших пользователей / перешедших на ресурсы
     * */
    public String resetResources(Long id) {
        for (QR qr: eventSevice.getEventById(id).getQrs()) {
            for (Resource r: qr.getResources()) {
                r.setCame_people_count(0L);
                r.setDeleted(false);
                resourceService.saveOrUpdate(r);
            }
            qr.setDefault_resource_people_count(0L);
            qrService.saveOrUpdate(qr);
        }
        return "{\"success\": true}";
    }

    public byte[] getImage(String qr_suffix) throws IOException {
        System.out.println("getImage");
        QR qr = qrService.getBySuffix(qr_suffix);
        File fi = new File(qr.getEvent().getQr_path() + "/" + qr_suffix + ".png");
        byte[] fileContent = Files.readAllBytes(fi.toPath());
        return fileContent;
    }
}
