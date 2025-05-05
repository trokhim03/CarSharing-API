package mate.academy.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.payment.PaymentRequestDto;
import mate.academy.carsharing.dto.payment.PaymentResponseDto;
import mate.academy.carsharing.service.payment.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment Management",
        description = "Endpoints for managing payments and payment processing")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get user payments",
            description = "Retrieve paginated list of payments for specific user")
    @GetMapping
    public Page<PaymentResponseDto> getWithUserId(Pageable pageable,
                                                  @RequestParam(name = "user_id") Long userId) {
        return paymentService.getWithUserId(pageable, userId);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create payment",
            description = "Create a new payment session for rental")
    @PostMapping
    public PaymentResponseDto createPayment(@RequestBody PaymentRequestDto paymentRequestDto) {
        return paymentService.createPayment(paymentRequestDto);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Payment success callback",
            description = "Endpoint for payment provider to redirect after successful payment")
    @GetMapping("/success/{paymentId}")
    public String paymentSuccessRedirect(@PathVariable(required = false) Long paymentId) {
        paymentService.checkSuccessfulPayment(paymentId);
        return "Success";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Payment cancellation callback",
            description = "Endpoint for payment provider to redirect after cancelled payment")
    @GetMapping("/cancel/{paymentId}")
    public String paymentCancelRedirect(@PathVariable(required = false) Long paymentId) {
        return "Payment with id " + paymentId
                + " was canceled.";
    }
}
