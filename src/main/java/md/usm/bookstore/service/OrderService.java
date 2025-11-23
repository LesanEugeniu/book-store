package md.usm.bookstore.service;


import md.usm.bookstore.dto.BookDto;
import md.usm.bookstore.dto.OrderDto;
import md.usm.bookstore.dto.PaymentDto;
import md.usm.bookstore.exception.StoreException;
import md.usm.bookstore.model.*;
import md.usm.bookstore.repository.OrderRepository;
import md.usm.bookstore.utils.Mapper;
import md.usm.bookstore.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static md.usm.bookstore.utils.ErrorType.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final Mapper mapper;
    private final BookService bookService;

    public OrderService(OrderRepository orderRepository, Mapper mapper, BookService bookService) {
        this.orderRepository = orderRepository;
        this.mapper = mapper;
        this.bookService = bookService;
    }

    @Transactional
    public OrderDto create(OrderDto orderDto, User user) {
        if (Utils.isNullOrEmpty(orderDto.books())) {
            throw new StoreException(
                    "Books cannot be null or empty",
                    VALIDATION_ERROR.name(),
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        Order order = new Order();

        Set<Book> books = bookService.getAllByIds(
                orderDto.books().stream().map(BookDto::id).filter(Objects::nonNull).toList()
        );
        order.setBooks(books);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setUser(user);

        return mapper.toDto(orderRepository.save(order));
    }

    public Page<OrderDto> getAll(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    public OrderDto getById(Long id, User user) {
        Order order = orderRepository.findByIdWithBooksAndUser(id);
        if (order == null) {
            throw new StoreException(
                    "Order not found with id " + id,
                    NOT_FOUND.name(),
                    HttpStatus.NOT_FOUND.value()
            );
        }

        checkPermission(order.getUser(), user);

        return mapper.toDto(order);
    }

    public Order getEntityById(Long id) {
        return orderRepository.findByIdWithUser(id)
                .orElseThrow(() -> new StoreException(
                        "Order not found with id " + id,
                        NOT_FOUND.name(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    @Transactional
    public OrderDto update(Long id, OrderDto orderDto, User user) {
        Order order = getEntityById(id);
        User userOfTheOrder = order.getUser();

        checkPermission(userOfTheOrder, user);
        checkStatus(order);

        if (orderDto.orderDate() != null) order.setOrderDate(orderDto.orderDate());
        if (orderDto.books() != null) {
            Set<Book> books = bookService.getAllByIds(
                    orderDto.books().stream().map(BookDto::id).filter(Objects::nonNull).toList()
            );
            order.setBooks(books);
        }

        Order saved = orderRepository.save(order);
        return mapper.toDto(saved);
    }

    public void delete(Long id) {
        Order existing = getEntityById(id);
        orderRepository.delete(existing);
    }

    public Page<OrderDto> getMyOrders(User user, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserId(user.getId(), pageable);
        List<Order> allWithBooks = orderRepository.findAllWithBooks(orders.getContent());
        long total = orderRepository.countByUserId(user.getId());

        return new PageImpl<>(allWithBooks.stream().map(mapper::toDto).toList(), pageable, total);
    }

    public String payOrder(User user, Long orderId, PaymentDto paymentDto) {
        Order order = getEntityById(orderId);
        checkPermission(order.getUser(), user);
        checkStatus(order);

        order.setStatus(OrderStatus.PAYED);
        orderRepository.save(order);

        return String.format("Order successfully PAYED, book will be delivered at zip_code: %s", paymentDto.zipCode());
    }

    private void checkPermission(User userOfTheOrder, User authenticatedUser) {
        if (!authenticatedUser.getUsername().equals(userOfTheOrder.getUsername())
                && !authenticatedUser.getRole().equals(Role.ADMIN)) {
            throw new StoreException(
                    "No permission",
                    FORBIDDEN.name(),
                    HttpStatus.FORBIDDEN.value()
            );
        }
    }


    private void checkStatus(Order order) {
        if (order.getStatus().equals(OrderStatus.PAYED)) {
            throw new StoreException(
                    "Order already payed",
                    BAD_REQUEST.name(),
                    HttpStatus.BAD_REQUEST.value()
            );
        }
    }
}
