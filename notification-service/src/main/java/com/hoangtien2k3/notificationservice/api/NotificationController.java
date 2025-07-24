package com.hoangtien2k3.notificationservice.api;

import com.hoangtien2k3.notificationservice.entity.Notification;
import com.hoangtien2k3.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import datadog.trace.api.Trace;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping


    @Trace(operationName = "ecommerce-microservices.notification.getAllNotifications")
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/{id}")


    @Trace(operationName = "ecommerce-microservices.notification.getNotificationById")
    public ResponseEntity<Notification> getNotificationById(@PathVariable String id) {
        Optional<Notification> notification = notificationService.getNotificationById(id);
        return notification.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping


    @Trace(operationName = "ecommerce-microservices.notification.saveNotification")
    public ResponseEntity<Notification> saveNotification(@RequestBody Notification notification) {
        Notification savedNotification = notificationService.saveNotification(notification);
        return new ResponseEntity<>(savedNotification, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")


    @Trace(operationName = "ecommerce-microservices.notification.deleteNotification")
    public ResponseEntity<Boolean> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotificationById(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

}