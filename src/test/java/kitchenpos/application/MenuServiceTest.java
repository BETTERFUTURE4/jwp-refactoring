package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.dto.MenuRequest;
import kitchenpos.dto.MenuResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class MenuServiceTest extends ServiceTest {

    @DisplayName("특정 메뉴를 추가할 시 메뉴 목록에 추가된다.")
    @Test
    void createAndList() {
        init();
        MenuResponse 메뉴_후라이드치킨 = menuService.create(변환(메뉴_후라이드치킨(), MenuRequest.class));

        List<MenuResponse> 메뉴_목록 = menuService.list();

        검증_필드비교_값포함(assertThat(메뉴_목록), 메뉴_후라이드치킨);
    }

    @DisplayName("여러 메뉴를 추가할 시 메뉴 목록에 추가된다.")
    @Test
    void createAndList_multi() {
        init();
        MenuRequest 변환 = 변환(메뉴_후라이드치킨(), MenuRequest.class);
        MenuResponse 메뉴_후라이드치킨 = menuService.create(변환);
        MenuResponse 메뉴_후라이드치킨2 = menuService.create(변환);

        List<MenuResponse> 메뉴_목록 = menuService.list();

        검증_필드비교_동일_목록(메뉴_목록, List.of(메뉴_후라이드치킨, 메뉴_후라이드치킨2));
    }

    @DisplayName("메뉴 그룹은 DB에 등록되어야 한다.")
    @Test
    void createAndList_invalidMenuGroup() {
        init();
        long 잘못된_메뉴그룹_ID = 100L;
        Menu 메뉴_후라이드치킨 = new Menu(1L, "후라이드", BigDecimal.valueOf(10000),
                잘못된_메뉴그룹_ID, List.of(메뉴상품_후라이드()));

        assertThatThrownBy(() -> menuService.create(변환(메뉴_후라이드치킨, MenuRequest.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("메뉴 그룹은 DB에 등록되어야 한다.");
    }


    @DisplayName("메뉴 속 상품들은 모두 DB에 등록되어야 한다.")
    @Test
    void createAndList_invalidProduct() {
        init();
        long 잘못된_상품_id = 100L;
        MenuRequest 메뉴_치킨그룹 = 변환(메뉴_치킨그룹(new MenuProduct(1L, 1L, 잘못된_상품_id, 10)), MenuRequest.class);

        assertThatThrownBy(() -> menuService.create(메뉴_치킨그룹))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("메뉴 속 상품들은 모두 DB에 등록되어야 한다");
    }

    @DisplayName("메뉴 가격은 내부 모든 상품가격보다 낮아야 한다.")
    @Test
    void createAndList_invalidDiscount() {
        init();
        BigDecimal 너무비싼_가격 = BigDecimal.valueOf(100000);
        Menu 메뉴_후라이드치킨 = new Menu(1L, "후라이드", 너무비싼_가격,
                1L, List.of(메뉴상품_후라이드()));

        assertThatThrownBy(() -> menuService.create(변환(메뉴_후라이드치킨, MenuRequest.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("메뉴 가격은 내부 모든 상품가격보다 낮아야 한다.");
    }

    void init() {
        menuGroupDao.save(new MenuGroup(null, "한마리메뉴"));
        productDao.save(new Product(null, "후라이드", BigDecimal.valueOf(16000)));
    }
}
