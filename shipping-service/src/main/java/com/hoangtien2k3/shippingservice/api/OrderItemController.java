package com.hoangtien2k3.shippingservice.api;

import com.hoangtien2k3.shippingservice.domain.id.OrderItemId;
import com.hoangtien2k3.shippingservice.dto.OrderItemDto;
import com.hoangtien2k3.shippingservice.dto.response.collection.DtoCollectionResponse;
import com.hoangtien2k3.shippingservice.security.JwtValidate;
import com.hoangtien2k3.shippingservice.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import datadog.trace.api.Trace;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/shippings")
public class OrderItemController {

    @Autowired
    private final OrderItemService orderItemService;
    @Autowired
    private final JwtValidate jwtValidate;

    @GetMapping


    @Trace(operationName = "ecommerce-microservices.orderitem.findAll")
    public ResponseEntity<DtoCollectionResponse<OrderItemDto>> findAll() {
        log.info("OrderItemDto List, controller; fetch all orderItems");
        return ResponseEntity.ok(new DtoCollectionResponse<>(this.orderItemService.findAll()));
    }

    @GetMapping("/{orderId}/{productId}")


    @Trace(operationName = "ecommerce-microservices.orderitem.findById")
    public ResponseEntity<OrderItemDto> findById(@PathVariable("orderId") final String orderId,
                                                 @PathVariable("productId") final String productId) {
        log.info("OrderItemDto, resource; fetch orderItem by id");
        return ResponseEntity.ok(this.orderItemService.findById(
                new OrderItemId(Integer.parseInt(orderId), Integer.parseInt(productId))));
    }

    @GetMapping("/find")


    @Trace(operationName = "ecommerce-microservices.orderitem.findById")
    public ResponseEntity<OrderItemDto> findById(@RequestBody
                                                 @NotNull(message = "Input must not be NULL")
                                                 @Valid final OrderItemId orderItemId) {
        log.info("OrderItemDto, resource; fetch orderItem by id");
        return ResponseEntity.ok(this.orderItemService.findById(orderItemId));
    }

    @PostMapping


    @Trace(operationName = "ecommerce-microservices.orderitem.save")
    public ResponseEntity<OrderItemDto> save(@RequestHeader(name = "Authorization") String authorizationHeader,
                                             @RequestBody @NotNull(message = "Input must not be NULL")
                                             @Valid final OrderItemDto orderItemDto) {
        if (!jwtValidate.validateTokenUserService(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("OrderItemDto, resource; save orderItem");
        return ResponseEntity.ok(this.orderItemService.save(orderItemDto));
    }

    @PutMapping


    @Trace(operationName = "ecommerce-microservices.orderitem.update")
    public ResponseEntity<OrderItemDto> update(@RequestHeader(name = "Authorization") String authorizationHeader,
                                               @RequestBody
                                               @NotNull(message = "Input must not be NULL")
                                               @Valid final OrderItemDto orderItemDto) {
        if (!jwtValidate.validateTokenUserService(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("OrderItemDto, resource; update orderItem");
        return ResponseEntity.ok(this.orderItemService.update(orderItemDto));
    }

    @DeleteMapping("/{orderId}/{productId}")


    @Trace(operationName = "ecommerce-microservices.orderitem.deleteById")
    public ResponseEntity<Boolean> deleteById(@RequestHeader(name = "Authorization") String authorizationHeader,
                                              @PathVariable("orderId") final String orderId,
                                              @PathVariable("productId") final String productId) {
        if (!jwtValidate.validateTokenUserService(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("Boolean, resource; delete orderItem by id");
        this.orderItemService.deleteById(new OrderItemId(Integer.parseInt(orderId), Integer.parseInt(productId)));
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/delete")


    @Trace(operationName = "ecommerce-microservices.orderitem.deleteById")
    public ResponseEntity<Boolean> deleteById(@RequestHeader(name = "Authorization") String authorizationHeader,
                                              @RequestBody
                                              @NotNull(message = "Input must not be NULL")
                                              @Valid final OrderItemId orderItemId) {
        if (!jwtValidate.validateTokenUserService(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("Boolean, resource; delete orderItem by id");
        this.orderItemService.deleteById(orderItemId);
        return ResponseEntity.ok(true);
    }

}
