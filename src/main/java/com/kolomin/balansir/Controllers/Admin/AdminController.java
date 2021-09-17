package com.kolomin.balansir.Controllers.Admin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kolomin.balansir.Services.*;
import com.kolomin.balansir.utils.EventFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

import static com.kolomin.balansir.Config.ConfigHandler.defaultResource;
import static com.kolomin.balansir.Config.ConfigHandler.defaultResourceDate;

/**
 * Контроллер для обработки данных администратора
 * */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Данный маппинг ищет в БД дубликаты на суффикс(хвост урла дл QR-кода)
     * На этот маппинг кидает запросы фронт каждые 0.5 сек, и если есть дубликаты - подсвечивает введенный пользователем хвост красным
     * */
    @GetMapping("/searchSuffixInDB")
    public String searchSuffixInDB(@RequestParam String suffix, @RequestParam (defaultValue = "0") Long id){
        return adminService.searchSuffixInDB(suffix, id);
    }

    /**
     * Данный маппинг ищет в БД дубликаты на внешний ресурс(урл до ботов например)
     * На этот маппинг кидает запросы фронт каждые 0.5 сек, и если есть дубликаты - подсвечивает введенный пользователем адрес красным
     * */
    @GetMapping("/searchUrlInDB")
    public String searchUrlInDB(@RequestParam String suffix, @RequestParam (defaultValue = "0") Long id){
        return adminService.searchUrlInDB(suffix, id);
    }

    /**
     * Данный маппинг добавляет в БД новое мероприятие
     * */
    @PostMapping("/addEvent")
    public String addEventPost(HttpEntity<String> params){
        return adminService.addEvent(params);
    }

    /**
     * Данный маппинг изменяет существующее мероприятие в БД
     * */
    @PutMapping("editEvent")
    public String editEvent(HttpEntity<String> params){
        JsonElement request = new JsonParser().parse(params.getBody());
        return adminService.editEvent(Long.valueOf(request.getAsJsonObject().get("id").toString().replaceAll("\"","")), params);
    }

    /**
     * Данный маппинг вытаскивает из БД все неудаленные мероприятия для показа на главной странице
     * */
    @GetMapping("/getAllNotDeletedEvents")
    public String getAllNotDeletedEvents(){
        System.out.println("Запрос на показ всех неудаленных мероприятий");
        return adminService.getAllNotDeletedEvents();
    }

    /**
     * Данный маппинг вытаскивает из БД все удаленные мероприятия для показа в корзине
     * */
    @GetMapping("/getAllDeletedEvents")
    public String getAllDeletedEvents(){
        System.out.println("Запрос на показ всех удаленных мероприятий");
        return adminService.getAllDeletedEvents();
    }

    /**
     * Данный маппинг выдаёт полную информацию по определенному мероприятию
     * */
    @GetMapping("/getInfoByEventId")
    public String getInfoByEventId(@RequestParam Long id){
        return adminService.getInfoByEventId(id);
    }

    /**
     * Данный маппинг переводит флаг deleted в true у мероприятия и всех его зависимостей
     * Показываем мероприятие в корзине, убираем из главной страницы
     * */
    @DeleteMapping("/deleteActiveEvent")
    public String deleteActiveEvent(@RequestParam Long id){
        return adminService.deleteActiveEventOrRestoreEvent(id, true);
    }

    /**
     * Данный маппинг переводит флаг deleted в false у мероприятия и всех его зависимостей
     * Показываем мероприятие на главной странице, убираем из корзины
     * */
    @PutMapping("/restoreEvent")
    public String deleteActiveEventOrRestoreEvent(@RequestParam Long id){
//        JsonElement request = new JsonParser().parse(params.getBody());
        return adminService.deleteActiveEventOrRestoreEvent(id, false);
    }

    /**
     * Данный маппинг полностью удаляет мероприятия и все его зависимости
     * */
    @DeleteMapping("/deleteMarkedEvent")
    public String deleteMarkedEvent(@RequestParam Long id){
        return adminService.deleteEvent(id);
    }

    /**
     * Данный маппинг полностью очищает БД
     * */
    @PostMapping("/deleteConfig")
    public String deleteConfigRest(){
        System.out.println("Запрос на полную чистку конфига балансира");
        return adminService.deleteConfig();
    }

    /**
     * Данный маппинг - фильтр мероприятий, выдаёт все мероприятия по пришедшим в запросе значениям
     * */
    @GetMapping("/eventsFilter")
    public String eventsFilter(@RequestParam Map<String, String> requestParams){
        return adminService.eventsFilter(requestParams);
    }

    /**
     * Данный маппинг обнуляет кол-во пришедших пользователей / перешедших на ресурсы
     * */
    @PutMapping("/resetResources")
    public String resetResources(@RequestParam Long id){
        return adminService.resetResources(id);
    }

    /**
     * Данный маппинг выдает инфу по дефолтному внешнему ресурсу. При рестарте программы дефолтный параметр берется из конфиг-файла
     * */
    @GetMapping("/getinfodefaultresource")
    public String getinfodefaultresource(){
//        String[] date = defaultResourceDate.
        return "{\"defaultResource\": \"" + defaultResource + "\", \"date\": \"" + defaultResourceDate.toString() + "\"}";
    }

    /**
     * Данный маппинг обновляет инфу по дефолтному внешнему ресурсу. При рестарте программы дефолтный параметр берется из конфиг-файла
     * */
    @PutMapping("/putinfodefaultresource")
    public String putinfodefaultresource(HttpEntity<String> params){
        JsonElement request = new JsonParser().parse(params.getBody());
        defaultResource = request.getAsJsonObject().get("defaultResource").toString().replaceAll("\"","");
        defaultResourceDate = new Date();
        return "{\"defaultResource\": \"" + defaultResource + "\", \"date\": \"" + defaultResourceDate.toString() + "\"}";
    }
}

