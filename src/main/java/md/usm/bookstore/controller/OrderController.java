package md.usm.bookstore.controller;

import jakarta.validation.Valid;
import md.usm.bookstore.dto.OrderDto;
import md.usm.bookstore.dto.PaymentDto;
import md.usm.bookstore.model.Role;
import md.usm.bookstore.service.OrderService;
import md.usm.bookstore.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<OrderDto> create(@RequestHeader("Authorization") String token,
                                           @RequestBody @Valid OrderDto dto) {
        userService.validateRole(token, Role.ADMIN);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(dto, userService.getUserByToken(token)));
    }

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getAll(@RequestHeader("Authorization") String token,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        userService.validateRole(token, Role.ADMIN);
        return ResponseEntity.ok(orderService.getAll(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(@RequestHeader("Authorization") String token,
                                            @PathVariable Long id) {
        userService.validateRoles(token, Role.USER, Role.ADMIN);
        return ResponseEntity.ok(orderService.getById(id, userService.getUserByToken(token)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> update(@RequestHeader("Authorization") String token,
                                           @PathVariable Long id,
                                           @RequestBody @Valid OrderDto dto) {
        userService.validateRoles(token, Role.USER, Role.ADMIN);
        return ResponseEntity.ok(orderService.update(id, dto, userService.getUserByToken(token)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader("Authorization") String token,
                                       @PathVariable Long id) {
        userService.validateRole(token, Role.ADMIN);
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public Page<OrderDto> getMyOrders(@RequestHeader("Authorization") String token,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        userService.validateRoles(token, Role.USER, Role.ADMIN);
        return orderService.getMyOrders(userService.getUserByToken(token), PageRequest.of(page, size));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<String> payOrder(@RequestHeader("Authorization") String token,
                                           @PathVariable Long id,
                                           @RequestBody @Valid PaymentDto payment) {
        userService.validateRoles(token, Role.USER, Role.ADMIN);
        return ResponseEntity.ok(orderService.payOrder(userService.getUserByToken(token), id, payment));
    }

}
