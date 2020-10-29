package com.artarkatesoft.securitystudy.web.controllers.api;

import com.artarkatesoft.securitystudy.domain.security.User;
import com.artarkatesoft.securitystudy.security.perms.BeerOrderReadPermissionV2;
import com.artarkatesoft.securitystudy.services.BeerOrderService;
import com.artarkatesoft.securitystudy.web.model.BeerOrderDto;
import com.artarkatesoft.securitystudy.web.model.BeerOrderPagedList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Beer Order Controller
 */
@RequestMapping("/api/v2/orders")
@RestController
@RequiredArgsConstructor
public class BeerOrderControllerV2 {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;

    @BeerOrderReadPermissionV2
    @GetMapping
    public BeerOrderPagedList listOrders(@AuthenticationPrincipal User user,
                                         @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (user.getCustomer() != null) {
            return beerOrderService.listOrders(user.getCustomer().getId(), PageRequest.of(pageNumber, pageSize));
        } else
            return beerOrderService.listOrders(PageRequest.of(pageNumber, pageSize));
    }

    @BeerOrderReadPermissionV2
    @GetMapping("{orderId}")
    public BeerOrderDto getOrder(@PathVariable("orderId") UUID orderId) {
        throw new RuntimeException("Not implemented yet");
//        return beerOrderService.getOrderById(customerId, orderId);
    }
}
