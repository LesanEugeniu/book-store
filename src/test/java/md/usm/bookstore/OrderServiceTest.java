package md.usm.bookstore;

import md.usm.bookstore.dto.OrderDto;
import md.usm.bookstore.dto.PaymentDto;
import md.usm.bookstore.model.*;
import md.usm.bookstore.repository.*;
import md.usm.bookstore.service.OrderService;
import md.usm.bookstore.utils.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired

    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private Mapper mapper;

    @Autowired
    private OrderService orderService;

    private OrderDto orderDto;
    private User testUser;
    private Principal principal;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        authorRepository.deleteAll();
        userRepository.deleteAll();

        Book book = new Book();
        book.setIsbn("test-isbn");
        book.setTitle("Test Book");
        book.setPrice(99.99);
        bookRepository.save(book);

        testUser = new User();
        testUser.setUsername("john");
        testUser.setEmail("john@example.com");
        testUser.setPassword("pass");
        testUser.setRole(Role.ADMIN);
        userRepository.save(testUser);

        orderDto = new OrderDto(null, LocalDateTime.now(),
                Collections.singletonList(mapper.toDto(book)), null, OrderStatus.CREATED);

        principal = () -> "john";
    }

    @Test
    void create_ShouldReturnDto() {
        OrderDto result = orderService.create(orderDto, principal);

        assertNotNull(result.id());
        assertEquals(1, result.books().size());
    }

    @Test
    void getById_ShouldReturnOrder() {
        OrderDto created = orderService.create(orderDto, principal);
        OrderDto found = orderService.getById(created.id(), () -> testUser.getUsername());

        assertEquals(created.id(), found.id());
        assertEquals(created.books().size(), found.books().size());
    }

    @Test
    void getMyOrders_ShouldReturnOrders() {
        orderService.create(orderDto, principal);
        Page<OrderDto> page = orderService.getMyOrders(() -> testUser.getUsername(), Pageable.unpaged());

        assertEquals(1, page.getTotalElements());
    }

    @Test
    void update_ShouldModifyOrder() {
        OrderDto created = orderService.create(orderDto, principal);

        OrderDto updateDto = new OrderDto(null, LocalDateTime.now().plusDays(1), null, null,
                OrderStatus.CREATED);
        OrderDto updated = orderService.update(created.id(), updateDto, () -> testUser.getUsername());

        assertEquals(updateDto.orderDate(), updated.orderDate());
    }

    @Test
    void payOrder_ShouldChangeStatus() {
        OrderDto created = orderService.create(orderDto, principal);
        PaymentDto paymentDto = new PaymentDto(
                created.id(),
                "1234567812345678", "TestHolder",
                "12/25", "2003", "112", "12321");

        String message = orderService.payOrder(() -> testUser.getUsername(), created.id(), paymentDto);

        Order order = orderRepository.findById(created.id()).orElseThrow();
        assertEquals(OrderStatus.PAYED, order.getStatus());
        assertTrue(message.contains(paymentDto.zipCode()));
    }

    @Test
    void delete_ShouldRemoveOrder() {
        OrderDto created = orderService.create(orderDto, principal);
        orderService.delete(created.id());

        assertFalse(orderRepository.findById(created.id()).isPresent());
    }
}
