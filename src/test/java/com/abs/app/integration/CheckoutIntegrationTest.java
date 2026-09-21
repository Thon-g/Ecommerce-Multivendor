package com.abs.app.integration;

import com.abs.app.application.order.command.CheckoutCommand;
import com.abs.app.application.order.command.CheckoutCommandHandler;
import com.abs.app.application.order.dto.PaymentOrderResponseDto;
import com.abs.app.domain.entity.*;
import com.abs.app.infrastructure.persistence.jpa.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
public class CheckoutIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CheckoutCommandHandler checkoutCommandHandler;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private AddressJpaRepository addressRepository;

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @Autowired
    private SellerJpaRepository sellerRepository;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private ProductSkuJpaRepository productSkuRepository;

    @Autowired
    private CartJpaRepository cartRepository;

    @Autowired
    private OrderJpaRepository orderRepository;

    private Long testAddressId;
    private String testUserId = "user-1";

    @BeforeEach
    void setUp() {
        // Clear old data safely (order matters for constraints)
        orderRepository.deleteAllInBatch();
        cartRepository.deleteAllInBatch();
        productSkuRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        sellerRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        addressRepository.deleteAllInBatch();

        // 1. Create User & Address
        Address address = new Address();
        address.setName("Home");
        address.setLocality("Downtown");
        address.setAddress("123 Street");
        address.setCity("Hanoi");
        address.setState("Hanoi");
        address.setPinCode("100000");
        address.setPhone("0123456789");
        address = addressRepository.save(address);
        testAddressId = address.getId();

        User user = new User();
        user.setUserId(testUserId);
        user.setUserName("testuser");
        user.setEmail("test@gmail.com");
        user.setPassword("password");
        user.setUpdateAt(java.time.LocalDateTime.now());
        
        Set<Address> addresses = new HashSet<>();
        addresses.add(address);
        user.setAddresses(addresses);
        user = userRepository.save(user);

        // 2. Create Category with 10% fee
        Category category = new Category();
        category.setId("cat-1");
        category.setCategoryId("cat-1");
        category.setName("Electronics");
        category.setCommissionRate(10.0);
        category = categoryRepository.save(category);

        // 3. Create Seller
        Seller seller = new Seller();
        seller.setSellerId("seller-1");
        seller.setShopName("Test Shop");
        seller.setUser(user);
        seller = sellerRepository.save(seller);

        // 4. Create Product & SKU
        Product product = new Product();
        product.setId("product-1");
        product.setTitle("Test Phone");
        product.setCategory(category);
        product.setSeller(seller);
        product.setMrpPrice(120);
        product.setSellingPrice(100);
        product.setCreateAt(java.time.LocalDateTime.now());
        product = productRepository.save(product);

        ProductSku sku = new ProductSku();
        sku.setProduct(product);
        sku.setQuantity(50);
        sku.setSkuCode("sku-1");
        sku = productSkuRepository.save(sku);

        // 5. Create Cart & CartItem
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setSku(sku);
        cartItem.setQuantity(2);
        cartItem.setSellingPrice(100);
        cartItem.setMrpPrice(120);
        cartItem.setUserId(user.getUserId());
        cartItemRepository.save(cartItem);

        Cart cart = new Cart();
        cart.setUser(user);
        cartItem.setCart(cart);
        cart.setCartItems(new HashSet<>(Collections.singletonList(cartItem)));
        cartRepository.saveAndFlush(cart);
        productSkuRepository.flush();
    }

    @Test
    void testCheckoutFlow_Success_And_CalculatesPlatformFee() {
        // Prepare command
        CheckoutCommand command = new CheckoutCommand(testUserId, testAddressId);

        // Execute Handler
        PaymentOrderResponseDto response = checkoutCommandHandler.handle(command);

        // Verify Output
        assertNotNull(response);
        assertEquals(200L, response.getAmount());

        // Verify Data in DB (Fee calculation)
        var orders = orderRepository.findAll();
        assertEquals(1, orders.size());
        
        var order = orders.get(0);
        assertEquals(1, order.getOrderItems().size());
        
        var orderItem = order.getOrderItems().iterator().next();
        // 2 items * 100 price = 200 total -> 10% fee = 20
        assertEquals(20, orderItem.getPlatformFee());
    }
}
