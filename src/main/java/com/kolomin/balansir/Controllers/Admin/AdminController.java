package com.kolomin.balansir.Controllers.Admin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;import com.kolomin.balansir.Services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static com.kolomin.balansir.Config.ConfigHandler.defaultResource;
import static com.kolomin.balansir.Config.ConfigHandler.defaultResourceDate;

/**
 * Контроллер для обработки данных администратора
 * */
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private AdminService adminService;
    private UserService userService;

    @Autowired
    public AdminController(AdminService adminService, UserService userService) {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("GMT+3"));
        this.adminService = adminService;
        this.userService = userService;
    }

    /**
     * Данный маппинг ищет в БД дубликаты на суффикс(хвост урла дл QR-кода)
     * На этот маппинг кидает запросы фронт каждые 0.5 сек, и если есть дубликаты - подсвечивает введенный пользователем хвост красным
     * */
    @GetMapping("/searchSuffixInDB")
    public ResponseEntity searchSuffixInDB(HttpEntity<String> rq, @RequestParam String suffix, @RequestParam (defaultValue = "0") Long id){
        if (userService.chekToken(rq)){
            return ResponseEntity.ok(adminService.searchSuffixInDB(suffix, id));
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг ищет в БД дубликаты на внешний ресурс(урл до ботов например)
     * На этот маппинг кидает запросы фронт каждые 0.5 сек, и если есть дубликаты - подсвечивает введенный пользователем адрес красным
     * */
    @GetMapping("/searchUrlInDB")
    public ResponseEntity searchUrlInDB(HttpEntity<String> rq, @RequestParam String suffix, @RequestParam (defaultValue = "0") Long id){
        if (userService.chekToken(rq)){
            return ResponseEntity.ok(adminService.searchUrlInDB(suffix, id));
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг добавляет в БД новое мероприятие
     * */
    @PostMapping("/addEvent")
    public ResponseEntity addEventPost(HttpEntity<String> rq){
        if (userService.chekToken(rq)){
            return ResponseEntity.ok(adminService.addEvent(rq));
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг изменяет существующее мероприятие в БД
     * */
    @PutMapping("editEvent")
    public ResponseEntity editEvent(HttpEntity<String> rq){
        if (userService.chekToken(rq)){
            JsonElement request = new JsonParser().parse(rq.getBody());
            return ResponseEntity.ok(adminService.editEvent(Long.valueOf(request.getAsJsonObject().get("id").toString().replaceAll("\"","")), rq));
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг вытаскивает из БД все неудаленные мероприятия для показа на главной странице
     * */
    @GetMapping("/getAllNotDeletedEvents")
    public ResponseEntity getAllNotDeletedEvents(HttpEntity<String> rq){
        if (userService.chekToken(rq)){
            log.info("Запрос на показ всех неудаленных мероприятий");
            return ResponseEntity.ok(adminService.getAllNotDeletedEvents());
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг вытаскивает из БД все удаленные мероприятия для показа в корзине
     * */
    @GetMapping("/getAllDeletedEvents")
    public ResponseEntity getAllDeletedEvents(HttpEntity<String> rq){
        if (userService.chekToken(rq)){
            log.info("Запрос на показ всех удаленных мероприятий");
            return ResponseEntity.ok(adminService.getAllDeletedEvents());
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг выдаёт полную информацию по определенному мероприятию
     * */
    @GetMapping("/getInfoByEventId")
    public ResponseEntity getInfoByEventId(HttpEntity<String> rq, @RequestParam Long id){
        if (userService.chekToken(rq)){
            return ResponseEntity.ok(adminService.getInfoByEventId(id));
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг переводит флаг deleted в true у мероприятия и всех его зависимостей
     * Показываем мероприятие в корзине, убираем из главной страницы
     * */
    @DeleteMapping("/deleteActiveEvent")
    public ResponseEntity deleteActiveEvent(HttpEntity<String> rq, @RequestParam Long id){
        if (userService.chekToken(rq)){
            return ResponseEntity.ok(adminService.deleteActiveEventOrRestoreEvent(id, true));
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг переводит флаг deleted в false у мероприятия и всех его зависимостей
     * Показываем мероприятие на главной странице, убираем из корзины
     * */
    @PutMapping("/restoreEvent")
    public ResponseEntity deleteActiveEventOrRestoreEvent(HttpEntity<String> rq, @RequestParam Long id){
        if (userService.chekToken(rq)){
            return ResponseEntity.ok(adminService.deleteActiveEventOrRestoreEvent(id, false));
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг полностью удаляет мероприятия и все его зависимости
     * */
    @DeleteMapping("/deleteMarkedEvent")
    public ResponseEntity deleteMarkedEvent(HttpEntity<String> rq, @RequestParam Long id){
        if (userService.chekToken(rq)){
            return ResponseEntity.ok(adminService.deleteEvent(id));
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг полностью очищает БД
     * */
    @PostMapping("/deleteConfig")
    public ResponseEntity deleteConfigRest(HttpEntity<String> rq){
        if (userService.chekToken(rq)){
            log.info("Запрос на полную чистку конфига балансира");
            return ResponseEntity.ok(adminService.deleteConfig());
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг - фильтр мероприятий, выдаёт все мероприятия по пришедшим в запросе значениям
     * */
    @GetMapping("/eventsFilter")
    public ResponseEntity eventsFilter(HttpEntity<String> rq, @RequestParam Map<String, String> requestParams){
        if (userService.chekToken(rq)){
            log.info("Запрос фильтра по данным " + requestParams.toString());
            return ResponseEntity.ok(adminService.eventsFilter(requestParams));
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг обнуляет кол-во пришедших пользователей / перешедших на ресурсы
     * */
    @PutMapping("/resetResources")
    public ResponseEntity resetResources(HttpEntity<String> rq,@RequestParam Long id){
        if (userService.chekToken(rq)){
            return ResponseEntity.ok(adminService.resetResources(id));
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг выдает инфу по дефолтному внешнему ресурсу. При рестарте программы дефолтный параметр берется из конфиг-файла
     * */
    @GetMapping("/getinfodefaultresource")
    public ResponseEntity getinfodefaultresource(HttpEntity<String> rq){
        if (userService.chekToken(rq)){
            return ResponseEntity.ok("{\"defaultResource\": \"" + defaultResource + "\", \"date\": \"" + defaultResourceDate.toString() + "\"}");
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг обновляет инфу по дефолтному внешнему ресурсу. При рестарте программы дефолтный параметр берется из конфиг-файла
     * */
    @PutMapping("/putinfodefaultresource")
    public ResponseEntity putinfodefaultresource(HttpEntity<String> rq){
        if (userService.chekToken(rq)){
            JsonElement request = new JsonParser().parse(rq.getBody());
            defaultResource = request.getAsJsonObject().get("defaultResource").toString().replaceAll("\"","");
            defaultResourceDate = new Date();
            return ResponseEntity.ok("{\"defaultResource\": \"" + defaultResource + "\", \"date\": \"" + defaultResourceDate.toString() + "\"}");
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг выдает в ответ .png QR-код
     * */
    @GetMapping(value = "/getpng/{qr_suffix}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getTableImageFile(HttpEntity<String> rq, @PathVariable String qr_suffix) throws IOException {
        if (userService.chekToken(rq)){
            log.info("Запрос на получение QR-кода .png");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(adminService.getImage(qr_suffix), headers, HttpStatus.OK);
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг для показа всех пользователей администратору
     * */
    @GetMapping("/getusers")
    public ResponseEntity getusers(HttpEntity<String> rq){
        if (userService.chekToken(rq)){
            System.out.println("прошли chekToken");
            if (userService.checkadmin(rq)) {
                System.out.println("прошли checkadmin");
                log.info("Запрос на показ всех пользователей администратору");
                return ResponseEntity.ok(userService.getusers());
            } else {
                System.out.println("не прошли checkadmin");
                return ResponseEntity.status(403).build();
            }
        } else {
            System.out.println("не прошли chekToken");
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг для добавления пользователя администратором
     * */
    @PostMapping("/adduser")
    public ResponseEntity adduser(HttpEntity<String> rq){
        if (userService.chekToken(rq)){
            if (userService.checkadmin(rq)) {
                log.info("Запрос на добавление пользователя");
                return ResponseEntity.ok(userService.adduser(rq));
            } else {
                return ResponseEntity.status(403).build();
            }
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Данный маппинг для удаления пользователя администратором
     * */
    @PostMapping("/deleteuser")
    public ResponseEntity deleteuser(HttpEntity<String> rq){
        if (userService.chekToken(rq)){
            if (userService.checkadmin(rq)) {
                log.info("Запрос на удаление пользователя");
                return ResponseEntity.ok(userService.deleteuser(rq));
            } else {
                return ResponseEntity.status(403).build();
            }
        } else {
            return ResponseEntity.status(401).build();
        }
    }
}

