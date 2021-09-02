package com.kolomin.balansir.Controllers.Admin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kolomin.balansir.Entities.*;
import com.kolomin.balansir.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.kolomin.balansir.BalansirApplication.*;
import static com.kolomin.balansir.Config.ConfigHandler.QRsPath;
import static com.kolomin.balansir.Config.ConfigHandler.thisHostPort;
/**
 * Контроллер для обработки данных администратора
 * */
@RestController
@RequestMapping("/admin")
public class AdminController {
    private EventSevice eventSevice;
    private QRService qrService;
    private ResourceService resourceService;
    private QRGenerate qrGenerate;
    private QRPathService qrPathService;
    private String[] bots;

    private Event eventForQRView;
    SimpleDateFormat myFormat;

    @Autowired
    public AdminController(EventSevice eventSevice, QRService qrService, ResourceService resourceService, QRGenerate qrGenerate, QRPathService qrPathService, String[] bots) {
        this.eventSevice = eventSevice;
        this.qrService = qrService;
        this.resourceService = resourceService;
        this.qrGenerate = qrGenerate;
        this.qrPathService = qrPathService;
        this.bots = bots;
        this.eventForQRView = new Event();
        myFormat = new SimpleDateFormat("dd-MM-yyyy");
    }

    /**
     * Данный метод ищет в БД дубликаты на суффикс(хвост урла дл QR-кода)
     * На этот маппинг кидает запросы фронт каждые 0.5 сек, и если есть дубликаты - подсвечивает введенный пользователем хвост красным
     * */
    @GetMapping("/searchSuffixInDB")
    public String searchSuffixInDB(@RequestParam String suffix){
        System.out.println("Запрос на сравнение суффикса " + suffix +" с БД");
        if (suffix.equals("pohohd")){
            System.out.println("{\"exist\": " + true + "}");
            return "{\"exist\": " + false + "}";
        } else {
            System.out.println("{\"exist\": " + false + "}");
            return "{\"exist\": " + false + "}";
        }

//        if (qrService.findQRSuffix(suffix)){
//            System.out.println("{\"exist\": " + true + "}");
//            return "{\"exist\": " + false + "}";
//        } else {
//            System.out.println("{\"exist\": " + false + "}");
//            return "{\"exist\": " + false + "}";
//        }
    }

    @GetMapping("/searchUrlInDB")
    public String searchUrlInDB(@RequestParam String suffix){
        System.out.println("Запрос на сравнение урла внешнего ресурса " + suffix + " с БД");
        if (suffix.equals("resource")){
            System.out.println("{\"exist\": " + true + "}");
            return "{\"exist\": " + true + "}";
        } else {
            System.out.println("{\"exist\": " + false + "}");
            return "{\"exist\": " + false + "}";
        }

//        if (resourceService.findUrl(suffix)){
//            System.out.println("{\"exist\": " + true + "}");
//            return "{\"exist\": " + true + "}";
//        } else {
//            System.out.println("{\"exist\": " + false + "}");
//            return "{\"exist\": " + false + "}";
//        }
    }

    @PostMapping("/addEvent")
    public String addEventPost(HttpEntity<String> params){
        System.out.println("Запрос на добавление мероприятия");
        return addEvent(params);
    }

    @GetMapping("/getQRs{eventId}")
    public String getQRView(@PathVariable Long eventId, Model model){
        Event event = eventSevice.getEventById(eventId);
        System.out.println("Запрос показ урлов для " + event.getName());
//        System.out.println("QRPaths для QR " + event.getQrs().get(0).getQrPath());
//        System.out.println("qrsPaths :" + event.getQrPaths());
        model.addAttribute("QRs", event.getQrs());
        return "QRView";
    }

    @GetMapping("/getConfig")
    public String getConfigRest(Model model){
        System.out.println("Запрос конфига балансира");
        model.addAttribute("events", eventSevice.findAll());
        System.out.println(eventSevice.findAll());
        return "balansirConfig";
    }

//    готово. Очищает полностью БД и сохраненные QR-коды
    @PostMapping("/deleteConfig")
    public String deleteConfigRest(Model model){
        System.out.println("Запрос на чистку конфига балансира");
        deleteConfig();
        return "redirect:/admin/getConfig";
    }
    //    подшаманить
    @GetMapping("editEvent{eventName}")
    public String editEventGet(@PathVariable String eventName, Model model){
        System.out.println("Запрос на страницу изменения мероприятия \"" + eventName + "\"");
        model.addAttribute("eventName", eventName);
        model.addAttribute("date", events.get(eventName).get("date"));
        model.addAttribute("peopleInBot", events.get(eventName).get("peopleInBot"));
        model.addAttribute("bots", events.get(eventName).get("bots").toString().substring(1,events.get(eventName).get("bots").toString().length()-1).replaceAll("\\s+",""));
        model.addAttribute("eventLink", events.get(eventName).get("eventLink").toString().substring(34));   //  Изменить потом 34 на другое число, тк хост и порт будут другие
        return "editEventView";
    }
    //    подшаманить
    @PostMapping("editEvent{eventName}")
    public String editEventPost(@PathVariable String eventName, @RequestParam Map<String, String> params, Model model){
        System.out.println("Запрос на изменение мероприятия \"" + eventName + "\"");
        editEvent(params);
        return "redirect:/getConfig";
    }

    @PostMapping("deleteEvent{eventId}")
    public String deleteEvent(@PathVariable Long eventId){
        System.out.println("Запрос на удаление мероприятия \"" + eventSevice.getEventById(eventId) + "\"");
        Event event = eventSevice.getEventById(eventId);
//        recursiveDelete(new File("src/main/resources/static/urls/" + event.getName()));
        recursiveDelete(new File(QRsPath + event.getName()));
        eventSevice.deleteEventById(eventId);
        return "redirect:/admin/getConfig";
    }

//    метод создания нового мероприятия
    public String addEvent(HttpEntity<String> params) {
        String response = "{\"succes\": \"true\",\n";

        JsonElement request = new JsonParser().parse(params.getBody());
        System.out.println("requestBody" + request);

        Event newEvent = new Event();
        if (eventSevice.findEventName(request.getAsJsonObject().get("event").toString().replaceAll("\"",""))){
            return "{\"succes\": \"false\", \"errorText\": \"Мероприятие с названием " + request.getAsJsonObject().get("event").toString().replaceAll("\"","") + " уже существует. Мероприятие не добавлено.\"}";
        }
        newEvent.setName(request.getAsJsonObject().get("event").toString().replaceAll("\"",""));
        newEvent.setCity(request.getAsJsonObject().get("city").toString().replaceAll("\"",""));
        try {
            newEvent.setDate(myFormat.parse(request.getAsJsonObject().get("date").toString().replaceAll("\"","")));
        } catch (ParseException e) {
            System.out.println("Ошибка в конвертации даты");
            return "{\"succes\": \"false\", \"errorText\": \"Ошибка в конвертации даты. Мероприятие не добавлено.\"}";
        }
        newEvent.setArea(request.getAsJsonObject().get("area").toString().replaceAll("\"",""));
        newEvent.setPeople_count(0L);
        newEvent.setDeleted(false);
        newEvent.setQrs(new ArrayList<>());
        eventSevice.saveOrUpdate(newEvent);

        for (JsonElement qr: request.getAsJsonObject().get("QRs").getAsJsonArray()) {
            QR newQR = new QR();
            if (qrService.findQRSuffix(qr.getAsJsonObject().get("suffix").toString().replaceAll("\"",""))){
                response += "\"errorText\": \"Суффикс " + qr.getAsJsonObject().get("suffix").toString().replaceAll("\"","") + " уже существует. Данный суффикс не добавился\",\n";
                continue;
            }
            newQR.setQr_suffix(qr.getAsJsonObject().get("suffix").toString().replaceAll("\"",""));
            newQR.setPeople_count(0L);
            newQR.setQr_url(thisHostPort + newQR.getQr_suffix());    //не тянется урл из ConfigHandler
            newQR.setEvent(newEvent);
            if (qr.getAsJsonObject().get("team").toString().replaceAll("\"","").equals("yes")){
                newQR.setTeam(true);
            } else {
                newQR.setTeam(false);
            }
            newQR.setDeleted(false);
            newQR.setResources(new ArrayList<>());
            qrService.saveOrUpdate(newQR);

            ArrayList<Resource> resources = new ArrayList<>();

            for (JsonElement resource: qr.getAsJsonObject().get("resources").getAsJsonArray()) {
                Resource newResource = new Resource();
                if (resourceService.findUrl(resource.getAsJsonObject().get("url").toString().replaceAll("\"",""))){
                    response += "\"errorText\": \"Ресурс " + resource.getAsJsonObject().get("url").toString().replaceAll("\"","") + " уже существует. Данный ресурс не добавился\",\n";
                    continue;
                }
                newResource.setQr(newQR);
                newResource.setQr_suffix(newQR.getQr_suffix());
                if (resource.getAsJsonObject().get("people_count").toString().replaceAll("\"","").equals("")){
                    newResource.setInfinity(true);
                } else {
                    newResource.setInfinity(false);
                }
                newResource.setUrl(resource.getAsJsonObject().get("url").toString().replaceAll("\"",""));
                newResource.setPeople_count(0L);
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

//            Генерирую QR-код
            QRPath qrPath = new QRPath();
            qrPath.setPath(qrGenerate.QRGenerate(newQR.getQr_url(), newQR.getQr_suffix(), newEvent.getName()));
            qrPath.setQr(newQR);
            qrPath.setDeleted(false);
            qrPathService.saveOrUpdate(qrPath);
            newQR.setQrPath(qrPath);
            qrService.saveOrUpdate(newQR);
            System.out.println("newQR\n" + newQR);
        }

        eventSevice.saveOrUpdate(newEvent);
        System.out.println("newEvent saved\n" + newEvent);

        System.out.println("\n\nevents:\n" + eventSevice.findAll());
        return response + "\"events\": " + eventSevice.findAllNotDeleted() + "\n}";
    }

    private String editEvent(Map<String, String> params){
            System.out.println("Обновление ранее существующего мероприятия");

            Map<String, String> eventMap = events.get(params.get("event"));  //  вытаскиваю это мероприятие
            urls.remove(eventMap.get("eventLink"));  //  удаляю из мапы с ссылками запись ключ-значение по старой eventLink, для обновления мероприятия его название менять не нужно ни в коем случае, иначе оно не обновится
//            System.out.println("Мапа урлс после удаления ключа\n" + urls);
            bots = params.get("bots").split(",");   //  получаю массив из ботов

            eventMap.put("date", params.get("date"));     //  дата мероприятия
            eventMap.put("botsCount", String.valueOf(bots.length)); //  количество ботов
            eventMap.put("peopleInBot", params.get("peopleInBot"));  //  количество людей на одного бота
            eventMap.put("eventLink", thisUrl + params.get("eventLink"));    //  мероприятие на английском без пробелов для ссылки на балансир
            eventMap.put("bots", Arrays.toString(bots));    //  кладу ботов одним значением в мапу мероприятия

            System.out.println(events);

            //  Далее создаю стринговый ArrayList с ссылками на ботов и наполняю его в циклу
            ArrayList<String> botsLinkArr = new ArrayList<>();
            for (int i = 0; i < bots.length; i++) {
                for (int j = 0; j <  Integer.parseInt(eventMap.get("peopleInBot")); j++) {
                    botsLinkArr.add("https://telegram.me/" + bots[i]);
                }
            }

            urls.put(params.get("eventLink"), botsLinkArr);    //  кладу ArrayList с ссылками на ботов как value для ключа eventLink
            System.out.println("static/urls\n" + urls);

//            model.addAttribute("link",eventMap.get("eventLink"));
            return eventMap.get("eventLink");



    }

    private void deleteConfig (){
        eventSevice.deleteAll();
        recursiveDelete(new File(QRsPath));
        System.out.println("Текущий конфиг очищен");
    }

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
}

