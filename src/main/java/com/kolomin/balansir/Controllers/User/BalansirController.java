//package com.kolomin.balansir.Controllers.User;
//
//import com.kolomin.balansir.Services.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.net.URI;
//
//@Controller
//public class BalansirController {
//
//    private UrlService urlService;
//    private EventSevice eventSevice;
//    private QRService qrService;
//    private ResourceService resourceService;
//
//    @Autowired
//    public BalansirController(UrlService urlService, EventSevice eventSevice, QRService qrService, ResourceService resourceService) {
//        this.urlService = urlService;
//        this.eventSevice = eventSevice;
//        this.qrService = qrService;
//        this.resourceService = resourceService;
//    }
//
//    @GetMapping("/{path}")
//    public ResponseEntity<Void> redirect(@PathVariable String path){
//        System.out.println("Вызов урла с этим qr_suffix: " + path);
//        try {
//            Url url = urlService.getByQRSuffix(path);
//            String link = url.getUrl();
//            new DropUrl(url,urlService,eventSevice,qrService,resourceService).start();
//            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(link)).build();
//        } catch (Exception e){
//            //  Здесь надо подумать куда перенаправлять в таком случае, хотя админ должен заранее расcчитать кол-во ботов по кол-ву человек
//            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("https://www.google.ru/")).build();
//        }
//
//    }
//}
