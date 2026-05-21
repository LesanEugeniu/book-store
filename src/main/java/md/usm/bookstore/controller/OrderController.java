package md.usm.bookstore.controller;

import jakarta.validation.Valid;
import md.usm.bookstore.dto.OrderDto;
import md.usm.bookstore.dto.PaymentDto;
import md.usm.bookstore.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDto> create(@RequestBody @Valid OrderDto dto, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(dto, principal));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<OrderDto>> getAll(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getAll(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDto> getById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(orderService.getById(id, principal));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDto> update(@PathVariable Long id,
                                           @RequestBody @Valid OrderDto dto,
                                           Principal principal) {
        return ResponseEntity.ok(orderService.update(id, dto, principal));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public Page<OrderDto> getMyOrders(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      Principal principal) {
        return orderService.getMyOrders(principal, PageRequest.of(page, size));
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> payOrder(@PathVariable Long id,
                                           @RequestBody @Valid PaymentDto payment,
                                           Principal principal) {
        return ResponseEntity.ok(orderService.payOrder(principal, id, payment));
    }
}
