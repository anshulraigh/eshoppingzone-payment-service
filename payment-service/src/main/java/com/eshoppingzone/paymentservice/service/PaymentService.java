package com.eshoppingzone.paymentservice.service;

import com.eshoppingzone.paymentservice.dto.PaymentRequest;
import com.eshoppingzone.paymentservice.dto.PaymentResponse;
import com.eshoppingzone.paymentservice.dto.ProductRequest;
import com.eshoppingzone.paymentservice.dto.StripeResponse;
import com.eshoppingzone.paymentservice.entity.Payment;
import com.eshoppingzone.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final StripeService stripeService;
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        try {
            // Convert to Stripe product request
            ProductRequest productRequest = new ProductRequest();
            productRequest.setAmount((long)(paymentRequest.getAmount() * 100)); // Convert to cents
            productRequest.setQuantity(1L); // Single payment
            productRequest.setName("Order #" + paymentRequest.getOrderId());
            productRequest.setCurrency("USD");

            StripeResponse stripeResponse = stripeService.checkoutProducts(productRequest);

            // Save payment record
            Payment payment = new Payment();
            payment.setOrderId(paymentRequest.getOrderId());
            payment.setUserId(paymentRequest.getUserId());
            payment.setAmount(paymentRequest.getAmount());
            payment.setPaymentMethod(paymentRequest.getPaymentMethod());
            payment.setStatus("PENDING");
            payment.setPaymentReference(stripeResponse.getSessionId());
            payment.setCreatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            return PaymentResponse.builder()
                    .status("PENDING")
                    .message("Payment initiated successfully")
                    .paymentLink(stripeResponse.getSessionUrl())
                    .build();
        } catch (Exception e) {
            return PaymentResponse.builder()
                    .status("FAILED")
                    .message("Payment processing failed: " + e.getMessage())
                    .paymentLink(null)
                    .build();
        }
    }

    public PaymentResponse getPaymentStatus(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

        // In a real implementation, you would verify with Stripe API
        return PaymentResponse.builder()
                .status(payment.getStatus())
                .message("Payment status retrieved")
                .paymentLink(null)
                .build();
    }
}