package com.abs.app.unit.application.publicapi.review.command;

import com.abs.app.application.publicapi.review.command.CreateReviewCommand;
import com.abs.app.application.publicapi.review.command.CreateReviewCommandHandler;
import com.abs.app.application.publicapi.review.dto.ReviewResponseDto;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.Review;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.OrderItemRepository;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.domain.repository.ReviewRepository;
import com.abs.app.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateReviewCommandHandlerTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private CreateReviewCommandHandler handler;

    private User mockUser;
    private Product mockProduct;
    private CreateReviewCommand command;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUserId("user1");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");

        mockProduct = new Product();
        mockProduct.setId("prod1");
        mockProduct.setNumRatings(0);
        mockProduct.setAverageRating(0.0);

        command = new CreateReviewCommand("user1", "prod1", "Great product!", 5.0, List.of("img1.png"));
    }

    @Test
    @DisplayName("Create Review Successfully")
    void shouldCreateReviewSuccessfully() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(mockUser));
        when(productRepository.findById("prod1")).thenReturn(Optional.of(mockProduct));
        when(orderItemRepository.hasPurchasedProductAndDelivered("user1", "prod1")).thenReturn(true);
        
        Review savedReview = new Review();
        savedReview.setId(1L);
        savedReview.setUser(mockUser);
        savedReview.setProduct(mockProduct);
        savedReview.setReviewText(command.getReviewText());
        savedReview.setRating(command.getRating());
        savedReview.setProductImages(command.getProductImages());
        
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        ReviewResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getReviewText()).isEqualTo("Great product!");
        
        verify(productRepository).save(mockProduct);
        assertThat(mockProduct.getNumRatings()).isEqualTo(1);
        assertThat(mockProduct.getAverageRating()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Fail to Create Review when Not Purchased")
    void shouldFailWhenNotPurchased() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(mockUser));
        when(productRepository.findById("prod1")).thenReturn(Optional.of(mockProduct));
        when(orderItemRepository.hasPurchasedProductAndDelivered("user1", "prod1")).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("You can only review a product after purchasing and receiving it.");
    }
}
