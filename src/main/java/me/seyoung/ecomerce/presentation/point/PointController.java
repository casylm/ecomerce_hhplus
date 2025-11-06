package me.seyoung.ecomerce.presentation.point;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.application.point.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointController {

    private final ChargePointUseCase chargePointUseCase;
    private final UsePointUseCase usePointUseCase;
    private final GetPointUseCase getPointUseCase;

    @PostMapping("/charge")
    public ResponseEntity<PointInfo.ChargePointResponse> chargePoint(@RequestBody ChargePointRequest request) {
        PointInfo.ChargePointResponse response = chargePointUseCase.execute(request.userId(), request.amount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/use")
    public ResponseEntity<PointInfo.UsePointResponse> usePoint(@RequestBody UsePointRequest request) {
        PointInfo.UsePointResponse response = usePointUseCase.excute(request.userId(), request.amount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<PointInfo.GetPointResponse> getPoint(@PathVariable Long userId) {
        PointInfo.GetPointResponse response = getPointUseCase.execute(userId, 0L);
        return ResponseEntity.ok(response);
    }

    public record ChargePointRequest(Long userId, long amount) {}
    public record UsePointRequest(Long userId, long amount) {}
}
