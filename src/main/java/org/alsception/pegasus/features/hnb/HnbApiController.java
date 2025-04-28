package org.alsception.pegasus.features.hnb;

import org.alsception.pegasus.features.hnb.HnbApiService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/hnbapi")
public class HnbApiController 
{
    private final HnbApiService hnbApiService;

    public HnbApiController(HnbApiService hnbApiService) 
    {
        this.hnbApiService = hnbApiService;
    }

    @GetMapping
    public Mono<HnbApiExchangeRateResponse> fetchData() {
        return hnbApiService.fetchData();
    }
}