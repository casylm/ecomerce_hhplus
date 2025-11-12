package me.seyoung.ecomerce.presentation.point;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.application.point.*;
import me.seyoung.ecomerce.presentation.point.dto.*;
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
    public ResponseEntity<ChargePointResponse> chargePoint(@RequestBody ChargePointRequest request) {
        PointInfo.ChargePointResponse pointInfo = chargePointUseCase.execute(request.userId(), request.amount());
        ChargePointResponse response = ChargePointResponse.from(pointInfo);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/use")
    public ResponseEntity<UsePointResponse> usePoint(@RequestBody UsePointRequest request) {
        PointInfo.UsePointResponse pointInfo = usePointUseCase.excute(request.userId(), request.amount());
        UsePointResponse response = UsePointResponse.from(pointInfo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<GetPointResponse> getPoint(@PathVariable Long userId) {
        PointInfo.GetPointResponse pointInfo = getPointUseCase.execute(userId, 0L);
        GetPointResponse response = GetPointResponse.from(pointInfo);
        return ResponseEntity.ok(response);
    }
}
