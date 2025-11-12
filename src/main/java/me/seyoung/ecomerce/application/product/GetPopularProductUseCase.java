package me.seyoung.ecomerce.application.product;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.order.OrderRepository;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GetPopularProductUseCase {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public List<ProductInfo.ProductDetailInfo> execute() {

        // 1. 최근 3일 주문 목록 조회
        List<Order> orders = orderRepository.findOrdersWithinDays(3);

        // 2. 상품별 판매 수량 집계
        Map<Long, Integer> salesMap = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                Long productId = item.getProductId();
                int quantity = item.getQuantity();

                salesMap.put(productId, salesMap.getOrDefault(productId, 0) + quantity);
            }
        }

        // 3. 판매량 기준 Top 5 상품 식별
        List<Map.Entry<Long, Integer>> sortedList = new ArrayList<>(salesMap.entrySet());
        sortedList.sort((a, b) -> b.getValue() - a.getValue()); // 내림차순 정렬

        List<Long> topProductIds = new ArrayList<>();
        int limit = Math.min(5, sortedList.size());
        for (int i = 0; i < limit; i++) {
            topProductIds.add(sortedList.get(i).getKey());
        }

        // 4. Product 조회 + DTO 변환
        List<ProductInfo.ProductDetailInfo> result = new ArrayList<>();

        for (Long productId : topProductIds) {
            Product product = productRepository.findById(productId)
                    .orElse(null); // 삭제된 상품은 스킵할 수도 있음

            if (product != null) {
                result.add(ProductInfo.ProductDetailInfo.from(product));
            }
        }

        return result;
    }
}
