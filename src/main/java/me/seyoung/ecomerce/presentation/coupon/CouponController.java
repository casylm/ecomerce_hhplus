package me.seyoung.ecomerce.presentation.coupon;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.application.coupon.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final GetCouponListUseCase getCouponListUseCase;
    private final IssueCouponUseCase issueCouponUseCase;
    private final ApplyCouponUseCase useCouponUseCase;

    @GetMapping("/users/{userId}")
    public ResponseEntity<CouponInfo.Coupons> getUserCoupons(@PathVariable Long userId) {
        CouponInfo.Coupons response = getCouponListUseCase.execute(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/issue")
    public ResponseEntity<CouponInfo.CouponIssueResult> issueCoupon(@RequestBody IssueCouponRequest request) {
        CouponInfo.CouponIssueResult response = issueCouponUseCase.execute(request.userId(), request.couponId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/use")
    public ResponseEntity<CouponInfo.CouponUseResult> useCoupon(@RequestBody UseCouponRequest request) {
        CouponInfo.CouponUseResult response = useCouponUseCase.execute(request.userId(), request.userCouponId());
        return ResponseEntity.ok(response);
    }

    public record IssueCouponRequest(Long userId, Long couponId) {}
    public record UseCouponRequest(Long userId, Long userCouponId) {}
}
