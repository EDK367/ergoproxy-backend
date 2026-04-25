package com.ergoproxy.backend.controller;

import com.ergoproxy.backend.dto.response.QuoteResponse;
import com.ergoproxy.backend.dto.response.Response;
import com.ergoproxy.backend.service.QuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @GetMapping("/random")
    public ResponseEntity<Response<QuoteResponse>> getRandomQuote() {
        return ResponseEntity.ok(
                Response.<QuoteResponse>builder()
                        .status(200)
                        .message("OK")
                        .data(quoteService.getRandomQuote())
                        .build());
    }
}
