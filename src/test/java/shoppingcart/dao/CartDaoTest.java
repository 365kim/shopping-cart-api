package shoppingcart.dao;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

import shoppingcart.dto.ProductRequestDto;

@JdbcTest
@Sql("classpath:InitCartTable.sql")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class CartDaoTest {
    private final CartDao cartDao;
    private final ProductDao productDao;
    private final JdbcTemplate jdbcTemplate;

    public CartDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        cartDao = new CartDao(jdbcTemplate);
        productDao = new ProductDao(jdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        productDao.save(new ProductRequestDto("banana", 1_000, "woowa1.com"));
        productDao.save(new ProductRequestDto("apple", 2_000, "woowa2.com"));

        jdbcTemplate.update( "INSERT INTO CART(customer_id, product_id) VALUES(?, ?)", 1L, 1L);
        jdbcTemplate.update( "INSERT INTO CART(customer_id, product_id) VALUES(?, ?)", 1L, 2L);
    }

    @DisplayName("카트에 아이템을 담으면, 담긴 카트 아이디를 반환한다. ")
    @Test
    void addCartItem(){

        // given
        long customerId = 1L;
        long productId = 1L;

        // when
        long cartId = cartDao.addCartItem(customerId, productId);

        // then
        assertThat(cartId).isEqualTo(3L);
    }

    @DisplayName("커스터머 아이디를 넣으면, 해당 커스터머가 구매한 상품의 아이디 목록을 가져온다.")
    @Test
    void findProductIdsByCustomerId() {

        // given
        long customerId = 1L;

        // when
        final List<Long> productsIds = cartDao.findProductIdsByCustomerId(customerId);

        // then
        assertThat(productsIds).containsExactly(1L, 2L);
    }

    @DisplayName("Customer Id를 넣으면, 해당 장바구니 Id들을 가져온다.")
    @Test
    void findIdsByCustomerId() {

        // given
        long customerId = 1L;

        // when
        final List<Long> cartIds = cartDao.findIdsByCustomerId(customerId);

        // then
        assertThat(cartIds).containsExactly(1L, 2L);
    }

    @DisplayName("Customer Id를 넣으면, 해당 장바구니 Id들을 가져온다.")
    @Test
    void deleteCartItem() {

        // given
        long cartId = 1L;

        // when
        cartDao.deleteCartItem(cartId);

        // then
        long customerId = 1L;
        final List<Long> productIds = cartDao.findProductIdsByCustomerId(customerId);
        assertThat(productIds).containsExactly(2L);
    }
}
