package com.eshoppingzone.paymentservice.controller;

import com.eshoppingzone.paymentservice.dto.PaymentRequest;
import com.eshoppingzone.paymentservice.dto.PaymentResponse;
import com.eshoppingzone.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentStatus(@PathVariable Long orderId) {
        PaymentResponse response = paymentService.getPaymentStatus(orderId);
        return ResponseEntity.ok(response);
    }
}