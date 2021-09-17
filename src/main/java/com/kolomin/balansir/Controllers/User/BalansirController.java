package com.kolomin.balansir.Controllers.User;

import com.kolomin.balansir.Entities.QR;
import com.kolomin.balansir.Entities.Resource;
import com.kolomin.balansir.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;

import static com.kolomin.balansir.Config.ConfigHandler.defaultResource;

@Controller
public class BalansirController {

    private QRService qrService;
    private ResourceService resourceService;

    @Autowired
    public BalansirController(QRService qrService, ResourceService resourceService) {
        this.qrService = qrService;
        this.resourceService = resourceService;
    }

//    @GetMapping("/{path}")
//    @ResponseBody
//    public String redirect(@PathVariable String path){
//        long m = System.currentTimeMillis();
//        metod();
//        double n =  System.currentTimeMillis() - m;
//
//        return m + " " + path + " " + n;
//    }
//
//    private synchronized void metod(){
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    @GetMapping("/{path}")
    public ResponseEntity<Void> redirect(@PathVariable String path){
        long m = System.currentTimeMillis();
        System.out.println("Вызов урла с этим qr_suffix: " + path);
        QR qr = qrService.getBySuffix(path);
        try {
            return getResource(qr, path, m);
        } catch (Exception e){
            qr.setDefault_resource_people_count(qr.getDefault_resource_people_count() + 1);
            qrService.saveOrUpdate(qr);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(defaultResource)).build();
        }
    }

    private synchronized ResponseEntity<Void> getResource(QR qr, String path, long m) {
        if (qr.isTeam()){
            Resource resource = resourceService.getByQRSuffixNotDeletedAndCamePeopleCountMin(path);
            Long Came_people_count = resource.getCame_people_count();
            resource.setCame_people_count(Came_people_count + 1);
            if (!resource.isInfinity()){
                if (Came_people_count + 1 == resource.getPeople_count())
                    resource.setDeleted(true);
            }
            resourceService.saveOrUpdate(resource);
            System.out.println(System.currentTimeMillis() - m);

            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(resource.getUrl())).build();

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
            System.out.println(System.currentTimeMillis() - m);

            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(resource.getUrl())).build();
        }
    }
}
