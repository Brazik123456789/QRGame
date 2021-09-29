package com.kolomin.balansir.Controllers.User;

import com.kolomin.balansir.Entities.QR;
import com.kolomin.balansir.Entities.Resource;
import com.kolomin.balansir.Services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URI;

import static com.kolomin.balansir.Config.ConfigHandler.defaultResource;
import static com.kolomin.balansir.Config.ConfigHandler.thisHostPort;

@Controller
@Slf4j
public class BalansirController {

    private QRService qrService;
    private ResourceService resourceService;

    @Autowired
    public BalansirController(QRService qrService, ResourceService resourceService) {
        this.qrService = qrService;
        this.resourceService = resourceService;
    }

    @GetMapping("/{path}")
    public String getIndex(@PathVariable String path, Model model){
        System.out.println("Отсканировали");
        model.addAttribute("qr", path);
        model.addAttribute("url", thisHostPort + "go/");
        return "index";
    }

    @ResponseBody
    @GetMapping("/go/{path}")
    public synchronized String redirect(@PathVariable String path){
//        long m = System.currentTimeMillis();
//        log.info("Вызов урла с этим qr_suffix: " + path);
        QR qr = qrService.getBySuffix(path);
        try {
            try {
                try {
                    return getResource(qr, path);
                } catch (Exception e) {
                    qr.setDefault_resource_people_count(qr.getDefault_resource_people_count() + 1);
                    qrService.saveOrUpdate(qr);
                    return qr.getDefault_resource();
                }
            } catch (Exception e) {
                qr.setGeneral_default_resource_people_count(qr.getGeneral_default_resource_people_count() + 1);
                qrService.saveOrUpdate(qr);
                return defaultResource;
            }
        }catch (Exception e){
            log.info("Ненужный мусор");
            return defaultResource;
        }
    }

    private String getResource(QR qr, String path) {
        if (qr.isTeam()){
            Resource resource = resourceService.getByQRSuffixNotDeletedAndCamePeopleCountMin(path);
            Long Came_people_count = resource.getCame_people_count();
            resource.setCame_people_count(Came_people_count + 1);
            if (!resource.isInfinity()){
                if (Came_people_count + 1 == resource.getPeople_count())
                    resource.setDeleted(true);
            }
            resourceService.saveOrUpdate(resource);
//            System.out.println(System.currentTimeMillis() - m);

            return resource.getUrl();
        }
        else {
            Resource resource = resourceService.getByQRSuffixNotDeleted(path);
            Long Came_people_count = resource.getCame_people_count();
            resource.setCame_people_count(Came_people_count + 1);
            if (resource.isInfinity()){
                qr.setTeam(true);
                qrService.saveOrUpdate(qr);
            } else {
                if (Came_people_count + 1 == resource.getPeople_count())
                    resource.setDeleted(true);
            }
            resourceService.saveOrUpdate(resource);
//            System.out.println(System.currentTimeMillis() - m);

            return resource.getUrl();
        }
    }

//    @GetMapping("/go/{path}")
//    public synchronized ResponseEntity<Void> redirect(@PathVariable String path){
////        public ResponseEntity<String> redirect(HttpEntity<String> params){
////        System.out.println(params);
//        long m = System.currentTimeMillis();
//        System.out.println("Вызов урла с этим qr_suffix: " + path);
//        QR qr = qrService.getBySuffix(path);
//        try {
//            try {
//                try {
//                    return getResource(qr, path, m);
//                } catch (Exception e) {
//                    qr.setDefault_resource_people_count(qr.getDefault_resource_people_count() + 1);
//                    qrService.saveOrUpdate(qr);
//                    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(qr.getDefault_resource())).build();
//                }
//            } catch (Exception e) {
//                qr.setGeneral_default_resource_people_count(qr.getGeneral_default_resource_people_count() + 1);
//                qrService.saveOrUpdate(qr);
//                return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(defaultResource)).build();
//            }
//        }catch (Exception e){
//            System.out.println("Ненужный мусор");
//            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(defaultResource)).build();
//        }
//    }
//
//    private synchronized ResponseEntity<Void> getResource(QR qr, String path, long m) {
//        if (qr.isTeam()){
//            Resource resource = resourceService.getByQRSuffixNotDeletedAndCamePeopleCountMin(path);
//            Long Came_people_count = resource.getCame_people_count();
//            resource.setCame_people_count(Came_people_count + 1);
//            if (!resource.isInfinity()){
//                if (Came_people_count + 1 == resource.getPeople_count())
//                    resource.setDeleted(true);
//            }
//            resourceService.saveOrUpdate(resource);
//            System.out.println(System.currentTimeMillis() - m);
//
//            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(resource.getUrl())).build();
//        }
//        else {
//            Resource resource = resourceService.getByQRSuffixNotDeleted(path);
//            Long Came_people_count = resource.getCame_people_count();
//            resource.setCame_people_count(Came_people_count + 1);
//            if (resource.isInfinity()){
//                qr.setTeam(true);
//                qrService.saveOrUpdate(qr);
//            } else {
//                if (Came_people_count + 1 == resource.getPeople_count())
//                    resource.setDeleted(true);
//            }
//            resourceService.saveOrUpdate(resource);
//            System.out.println(System.currentTimeMillis() - m);
//
//            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(resource.getUrl())).build();
//        }
//    }
}
