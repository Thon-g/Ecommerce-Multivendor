package com.abs.app.integration;

import com.abs.app.application.order.command.CheckoutCommand;
import com.abs.app.application.order.command.CheckoutCommandHandler;
import com.abs.app.common.exception.OutOfStockException;
import com.abs.app.domain.entity.*;
import com.abs.app.infrastructure.persistence.jpa.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RaceConditionIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CheckoutCommandHandler checkoutCommandHandler;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private AddressJpaRepository addressRepository;

    @Autowired
    private CartJpaRepository cartRepository;

    @Autowired
    private CartItemJpaRepository cartItemRepository;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private ProductSkuJpaRepository productSkuRepository;

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @Autowired
    private SellerJpaRepository sellerRepository;

    @Autowired
    private OrderJpaRepository orderRepository;

    @Autowired
    private PaymentOrderJpaRepository paymentOrderRepository;

    private Long testSkuId;
    private final List<String> userIds = new ArrayList<>();
    private final List<Long> addressIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // Clear old data safely
        paymentOrderRepository.deleteAllInBatch();
        cartItemRepository.deleteAllInBatch();
        cartRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productSkuRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        sellerRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        addressRepository.deleteAllInBatch();

        // 1. Create 1 Category & 1 Seller
        Category category = new Category();
        category.setId("cat-1");
        category.setCategoryId("cat-1");
        category.setName("Flash Sale");
        category = categoryRepository.save(category);

        User sellerUser = new User();
        sellerUser.setUserId("seller-1");
        sellerUser.setUserName("seller1");
        sellerUser.setEmail("seller@test.com");
        sellerUser.setPassword("pass");
        sellerUser.setUpdateAt(LocalDateTime.now());
        sellerUser.setCreatedAt(LocalDateTime.now());
        sellerUser = userRepository.save(sellerUser);

        Seller seller = new Seller();
        seller.setSellerId("seller-1");
        seller.setShopName("Official Store");
        seller.setUser(sellerUser);
        seller = sellerRepository.save(seller);

        // 2. Create 1 Product with EXACTLY 5 ITEMS IN STOCK
        Product product = new Product();
        product.setId("product-flashsale");
        product.setTitle("Iphone 15 Pro Max 1K");
        product.setCategory(category);
        product.setSeller(seller);
        product.setMrpPrice(30000000);
        product.setSellingPrice(1000); // 1K
        product.setCreateAt(LocalDateTime.now());
        product = productRepository.save(product);

        ProductSku sku = new ProductSku();
        sku.setProduct(product);
        sku.setQuantity(5); // ONLY 5 ITEMS!
        sku.setSkuCode("SKU-IPHONE-15");
        sku = productSkuRepository.save(sku);
        this.testSkuId = sku.getId();

        // 3. Create 10 Users fighting for these 5 items
        for (int i = 0; i < 10; i++) {
            User user = new User();
            String uId = "user-" + i;
            user.setUserId(uId);
            user.setUserName(uId);
            user.setEmail(uId + "@test.com");
            user.setFirstName("Buyer");
            user.setLastName(String.valueOf(i));
            user.setPassword("pass");
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdateAt(LocalDateTime.now());
            user = userRepository.save(user);
            userIds.add(user.getUserId());

            Address address = new Address();
            address.setName("Home " + i);
            address.setLocality("Downtown");
            address.setAddress("Street " + i);
            address.setCity("Hanoi");
            address.setState("Hanoi");
            address.setPinCode("100000");
            address.setPhone("0123456789");
            address = addressRepository.save(address);
            addressIds.add(address.getId());

            user.setAddresses(new java.util.HashSet<>(Collections.singletonList(address)));
            userRepository.save(user);

            // Give each user a cart with 1 iphone
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setTotalItem(1);
            cart = cartRepository.save(cart);

            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setSku(sku);
            cartItem.setQuantity(1);
            cartItem.setSellingPrice(1000);
            cartItem.setMrpPrice(30000000);
            cartItem.setUserId(user.getUserId());
            cartItemRepository.save(cartItem);
        }
    }

    @Test
    void testFlashSale_RaceCondition_PreventedByLock() throws InterruptedException {
        // We have 10 threads (users) trying to checkout at the EXACT same time
        int numberOfUsers = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfUsers);
        
        CountDownLatch readyLatch = new CountDownLatch(numberOfUsers);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfUsers);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfUsers; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    // Ready at the starting line
                    readyLatch.countDown();
                    // Wait for the gunshot
                    startLatch.await();
                    
                    // Fire! All 10 threads call Checkout simultaneously
                    CheckoutCommand command = new CheckoutCommand(userIds.get(index), addressIds.get(index));
                    checkoutCommandHandler.handle(command);
                    
                    successCount.incrementAndGet();
                } catch (OutOfStockException e) {
                    // Out of stock exception is expected for 5 users
                    failCount.incrementAndGet();
                } catch (Exception e) {
                    // Any other DB Deadlock or Constraint errors go here
                    failCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // Wait until all threads are ready at the starting line
        readyLatch.await();
        // GUNSHOT! Release the latch, all threads run simultaneously
        startLatch.countDown();
        // Wait until all 10 threads finish their checkout
        doneLatch.await();
        
        executor.shutdown();

        // VERIFY: Out of 10 users, exactly 5 must succeed, and 5 must fail (since stock = 5)
        assertEquals(5, successCount.get(), "Chỉ được phép có 5 người mua thành công");
        assertEquals(5, failCount.get(), "5 người còn lại phải bị báo lỗi Hết Hàng");

        // VERIFY: The remaining stock in Database must be exactly 0 (no negative stock!)
        ProductSku finalSku = productSkuRepository.findById(this.testSkuId).orElseThrow();
        assertEquals(0, finalSku.getQuantity(), "Kho hàng phải về 0, không được bị âm");
    }
}
