package me.seyoung.ecomerce.facade;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.application.product.DeductStockUseCase;
import me.seyoung.ecomerce.application.product.RestoreStockUseCase;
import me.seyoung.ecomerce.application.product.dto.ProductInfo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonLockStockFacade {

    private final RedissonClient redissonClient;

    private final DeductStockUseCase deductStockUseCase;
    private final RestoreStockUseCase restoreStockUseCase;

    public ProductInfo.StockDecrease decrease(Long productId, int quantitiy) {
        RLock lock = redissonClient.getLock("stock:lock:" + productId);

        try {
            boolean available = lock.tryLock(5, 5, TimeUnit.SECONDS);

            if (!available) {
                throw new IllegalStateException("Lock을 획득하지 못했습니다.");
            }

            return deductStockUseCase.execute(productId, quantitiy);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public ProductInfo.StockIncrease restore(Long productId, int quantity) {

        RLock lock = redissonClient.getLock(productId.toString());

        try {
            boolean available = lock.tryLock(5, TimeUnit.SECONDS);

            if (!available) {
                throw new IllegalStateException("Lock을 획득하지 못했습니다.");
            }

            return restoreStockUseCase.execute(productId, quantity);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
