package com.hoangtien2k3qx1.favouriteservice.api;

import datadog.trace.api.Trace;
import com.hoangtien2k3qx1.favouriteservice.constant.ConfigConstant;
import com.hoangtien2k3qx1.favouriteservice.dto.FavouriteDto;
import com.hoangtien2k3qx1.favouriteservice.dto.response.collection.DtoCollectionResponse;
import com.hoangtien2k3qx1.favouriteservice.entity.id.FavouriteId;
import com.hoangtien2k3qx1.favouriteservice.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/api/favourites")
@RequiredArgsConstructor
public class FavouriteApi {

    @Autowired
    private final FavouriteService favouriteService;

    @GetMapping
    @Trace(operationName = "favourite.controller.findAll")
    public ResponseEntity<DtoCollectionResponse<FavouriteDto>> findAll() {
        return ResponseEntity.ok(new DtoCollectionResponse<>(this.favouriteService.findAll()));
    }

    @GetMapping("/{userId}/{productId}/{likeDate}")
    @Trace(operationName = "favourite.controller.findById")
    public ResponseEntity<FavouriteDto> findById(@PathVariable("userId") final String userId,
                                                 @PathVariable("productId") final String productId,
                                                 @PathVariable("likeDate") final String likeDate) {
        return ResponseEntity.ok(this.favouriteService.findById(
                new FavouriteId(Integer.parseInt(userId), Integer.parseInt(productId),
                        LocalDateTime.parse(likeDate, DateTimeFormatter.ofPattern(ConfigConstant.LOCAL_DATE_TIME_FORMAT)))));
    }

    @GetMapping("/find")
    @Trace(operationName = "favourite.controller.findByIdBody")
    public ResponseEntity<FavouriteDto> findById(@RequestBody
                                                 @NotNull(message = "Input must not be NULL")
                                                 @Valid final FavouriteId favouriteId) {
        return ResponseEntity.ok(this.favouriteService.findById(favouriteId));
    }

    @PostMapping
    @Trace(operationName = "favourite.controller.save")
    public ResponseEntity<FavouriteDto> save(@RequestBody
                                             @NotNull(message = "Input must not be NULL")
                                             @Valid final FavouriteDto favouriteDto) {
        return ResponseEntity.ok(this.favouriteService.save(favouriteDto));
    }

    @PutMapping
    @Trace(operationName = "favourite.controller.update")
    public ResponseEntity<FavouriteDto> update(@RequestBody
                                               @NotNull(message = "Input must not be NULL")
                                               @Valid final FavouriteDto favouriteDto) {
        return ResponseEntity.ok(this.favouriteService.update(favouriteDto));
    }

    @DeleteMapping("/{userId}/{productId}/{likeDate}")
    @Trace(operationName = "favourite.controller.deleteById")
    public ResponseEntity<Boolean> deleteById(@PathVariable("userId") final String userId,
                                              @PathVariable("productId") final String productId,
                                              @PathVariable("likeDate") final String likeDate) {
        favouriteService.deleteById(
                new FavouriteId(Integer.parseInt(userId), Integer.parseInt(productId),
                        LocalDateTime.parse(likeDate, DateTimeFormatter.ofPattern(ConfigConstant.LOCAL_DATE_TIME_FORMAT)))
        );
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/delete")
    @Trace(operationName = "favourite.controller.deleteByIdBody")
    public ResponseEntity<Boolean> deleteById(@RequestBody
                                              @NotNull(message = "Input must not be NULL")
                                              @Valid final FavouriteId favouriteId) {
        favouriteService.deleteById(favouriteId);
        return ResponseEntity.ok(true);
    }

}
