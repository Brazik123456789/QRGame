//package com.kolomin.balansir.Services;
//
//import com.kolomin.balansir.Entities.Event;
//import com.kolomin.balansir.Entities.QR;
//import com.kolomin.balansir.Entities.Resource;
//
//public class DropUrl extends Thread{
//    private EventSevice eventSevice;
//    private QRService qrService;
//    private ResourceService resourceService;
//
//    public DropUrl(EventSevice eventSevice, QRService qrService, ResourceService resourceService) {
//        this.url = url;
//        this.eventSevice = eventSevice;
//        this.qrService = qrService;
//        this.resourceService = resourceService;
//    }
//
//    @Override
//    public synchronized void run() {
//        Resource resource = url.getResource();
//        System.out.println("url.isInfinity() :" + url.isInfinity());
//        if (!url.isInfinity()){
//            System.out.println("dropUrl");
//            urlService.delete(url);
//        }
//
//        resource.setPeople_count(resource.getPeople_count() + 1);
//        QR qr = resource.getQr();
//        qr.setPeople_count(qr.getPeople_count() + 1);
//        Event event = qr.getEvent();
//        event.setPeople_count(event.getPeople_count() + 1);
//
//        resourceService.saveOrUpdate(resource);
//        qrService.saveOrUpdate(qr);
//        eventSevice.saveOrUpdate(event);
//    }
//}
