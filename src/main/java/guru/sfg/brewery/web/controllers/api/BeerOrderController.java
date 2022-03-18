package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/{customerId}")
public class BeerOrderController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;
    public BeerOrderController(BeerOrderService beerOrderService){ this.beerOrderService = beerOrderService; }

    @GetMapping("orders")
    public BeerOrderPagedList listOrders(@PathVariable("customerId")String customerId,
                                         @RequestParam(value = "pageNumber", required = true) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = true) Integer pageSize){
        if(pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if(pageSize == null || pageSize < 0){
            pageNumber = DEFAULT_PAGE_SIZE;
        }

        return this.beerOrderService.listOrders(UUID.fromString(customerId), PageRequest.of(pageNumber, pageSize));
    }

    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    public BeerOrderDto placeOrder(@PathVariable("customerId") UUID customerId, @RequestBody BeerOrderDto beerOrderDto) {
        return this.beerOrderService.placeOrder(customerId, beerOrderDto);
    }

    @GetMapping("orders/{orderId}")
    public BeerOrderDto getOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId) {
        return this.beerOrderService.getOrderById(customerId, orderId);
    }

    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void placeOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId) {
        this.beerOrderService.pickupOrder(customerId, orderId);
    }
}
