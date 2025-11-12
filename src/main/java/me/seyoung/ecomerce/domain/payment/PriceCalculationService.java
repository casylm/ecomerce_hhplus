package me.seyoung.ecomerce.domain.payment;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.order.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceCalculationService {

    public Price calculate(List<OrderItem> items, Coupon coupon, Price pointToUse) {

        // 1) 상품 총액 = sum(item.price * quantity)
        Price totalPrice = calculateTotal(items);

        // 2) 쿠폰 적용
        if (coupon != null) {
            totalPrice = applyCouponDiscount(totalPrice, coupon);
        }

        // 3) 포인트 차감 적용
        return applyPointDiscount(totalPrice, pointToUse);
    }

    private Price calculateTotal(List<OrderItem> items) {
        Price total = new Price(0);
        for (OrderItem item : items) {
            Price itemPrice = new Price(item.calculatePrice());
            total = total.add(itemPrice);
        }
        return total;
    }

    private Price applyCouponDiscount(Price price, Coupon coupon) {
        Price discount = new Price(coupon.getDiscountAmount());
        return price.subtract(discount);
    }

    private Price applyPointDiscount(Price price, Price pointToUse) {
        return price.subtract(pointToUse);
    }

}
