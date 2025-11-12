package me.seyoung.ecomerce.domain.product;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ProductTest {

    @Test
    void 상품을_생성한다() {
        // given
        Long id = 1L;
        String name = "노트북";
        long price = 1500000L;
        int stock = 10;
        String category = "전자제품";

        // when
        Product product = new Product(id, name, price, stock, category);

        // then
        assertThat(product.getId()).isEqualTo(id);
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getStock()).isEqualTo(stock);
        assertThat(product.getCategory()).isEqualTo(category);
    }

    @Test
    void 재고가_음수이면_예외가_발생한다() {
        // given
        Long id = 1L;
        String name = "노트북";
        long price = 1500000L;
        int stock = -1;
        String category = "전자제품";

        // when & then
        assertThatThrownBy(() -> new Product(id, name, price, stock, category))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고는 0 이상이어야 합니다.");
    }

    @Test
    void 가격이_음수이면_예외가_발생한다() {
        // given
        Long id = 1L;
        String name = "노트북";
        long price = -1000L;
        int stock = 10;
        String category = "전자제품";

        // when & then
        assertThatThrownBy(() -> new Product(id, name, price, stock, category))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가격은 0 이상이어야 합니다.");
    }

    @Test
    void 재고가_0인_상품을_생성할_수_있다() {
        // given
        Long id = 1L;
        String name = "품절 상품";
        long price = 10000L;
        int stock = 0;
        String category = "기타";

        // when
        Product product = new Product(id, name, price, stock, category);

        // then
        assertThat(product.getStock()).isEqualTo(0);
        assertThat(product.isAvailable()).isFalse();
    }

    @Test
    void 가격이_0인_상품을_생성할_수_있다() {
        // given
        Long id = 1L;
        String name = "무료 상품";
        long price = 0L;
        int stock = 10;
        String category = "기타";

        // when
        Product product = new Product(id, name, price, stock, category);

        // then
        assertThat(product.getPrice()).isEqualTo(0L);
    }

    @Test
    void 재고를_차감한다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 10, "전자제품");
        int quantity = 3;

        // when
        product.decreaseStock(quantity);

        // then
        assertThat(product.getStock()).isEqualTo(7);
    }

    @Test
    void 재고를_여러번_차감한다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 10, "전자제품");

        // when
        product.decreaseStock(2);
        product.decreaseStock(3);
        product.decreaseStock(1);

        // then
        assertThat(product.getStock()).isEqualTo(4);
    }

    @Test
    void 재고를_모두_차감할_수_있다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 5, "전자제품");

        // when
        product.decreaseStock(5);

        // then
        assertThat(product.getStock()).isEqualTo(0);
        assertThat(product.isAvailable()).isFalse();
    }

    @Test
    void 재고보다_많은_수량을_차감하면_예외가_발생한다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 5, "전자제품");
        int quantity = 10;

        // when & then
        assertThatThrownBy(() -> product.decreaseStock(quantity))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("재고가 부족합니다. 현재 재고: 5, 요청 수량: 10");
    }

    @Test
    void 차감_수량이_0이면_예외가_발생한다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 10, "전자제품");

        // when & then
        assertThatThrownBy(() -> product.decreaseStock(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("차감 수량은 0보다 커야 합니다.");
    }

    @Test
    void 차감_수량이_음수이면_예외가_발생한다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 10, "전자제품");

        // when & then
        assertThatThrownBy(() -> product.decreaseStock(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("차감 수량은 0보다 커야 합니다.");
    }

    @Test
    void 재고를_증가시킨다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 10, "전자제품");
        int quantity = 5;

        // when
        product.increaseStock(quantity);

        // then
        assertThat(product.getStock()).isEqualTo(15);
    }

    @Test
    void 재고를_여러번_증가시킨다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 5, "전자제품");

        // when
        product.increaseStock(3);
        product.increaseStock(2);
        product.increaseStock(5);

        // then
        assertThat(product.getStock()).isEqualTo(15);
    }

    @Test
    void 품절_상품의_재고를_증가시킬_수_있다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 0, "전자제품");

        // when
        product.increaseStock(10);

        // then
        assertThat(product.getStock()).isEqualTo(10);
        assertThat(product.isAvailable()).isTrue();
    }

    @Test
    void 증가_수량이_0이면_예외가_발생한다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 10, "전자제품");

        // when & then
        assertThatThrownBy(() -> product.increaseStock(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("증가 수량은 0보다 커야 합니다.");
    }

    @Test
    void 증가_수량이_음수이면_예외가_발생한다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 10, "전자제품");

        // when & then
        assertThatThrownBy(() -> product.increaseStock(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("증가 수량은 0보다 커야 합니다.");
    }

    @Test
    void 재고가_있으면_판매_가능하다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 1, "전자제품");

        // when
        boolean available = product.isAvailable();

        // then
        assertThat(available).isTrue();
    }

    @Test
    void 재고가_0이면_판매_불가능하다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 0, "전자제품");

        // when
        boolean available = product.isAvailable();

        // then
        assertThat(available).isFalse();
    }

    @Test
    void 재고_차감_후_판매_가능_여부가_변경된다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 1, "전자제품");

        // when
        product.decreaseStock(1);

        // then
        assertThat(product.isAvailable()).isFalse();
    }

    @Test
    void 재고_증가_후_판매_가능해진다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 0, "전자제품");

        // when
        product.increaseStock(1);

        // then
        assertThat(product.isAvailable()).isTrue();
    }

    @Test
    void 재고_차감과_증가를_함께_처리한다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 10, "전자제품");

        // when
        product.decreaseStock(3);  // 10 - 3 = 7
        product.increaseStock(5);  // 7 + 5 = 12
        product.decreaseStock(2);  // 12 - 2 = 10

        // then
        assertThat(product.getStock()).isEqualTo(10);
    }
}
