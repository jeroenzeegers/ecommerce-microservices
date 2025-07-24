package com.hoangtien2k3.notificationservice.api;

import com.hoangtien2k3.notificationservice.dto.PaymentDto;
import com.hoangtien2k3.notificationservice.entity.Payment;
import com.hoangtien2k3.notificationservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import datadog.trace.api.Trace;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping


    @Trace(operationName = "ecommerce-microservices.payment.savePayment")
    public Mono<Payment> savePayment(@RequestBody PaymentDto paymentDto) {
        return paymentService.savePayment(paymentDto);
    }

    @GetMapping("/{paymentId}")


    @Trace(operationName = "ecommerce-microservices.payment.getPayment")
    public Mono<Payment> getPayment(@PathVariable Integer paymentId) {
        return paymentService.getPayment(paymentId);
    }

    @GetMapping


    @Trace(operationName = "ecommerce-microservices.payment.getAllPayments")
    public Mono<List<Payment>> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @DeleteMapping("/{paymentId}")


    @Trace(operationName = "ecommerce-microservices.payment.deletePayment")
    public Mono<Void> deletePayment(@PathVariable Integer paymentId) {
        return paymentService.deletePayment(paymentId);
    }

}