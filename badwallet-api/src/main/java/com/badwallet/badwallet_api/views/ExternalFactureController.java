package com.badwallet.badwallet_api.views;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/external")
@RequiredArgsConstructor
public class ExternalFactureController {

    private final RestTemplate restTemplate;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    // 2.2 Factures impayées du mois en cours
    @GetMapping("/factures/{walletCode}/current")
    public ResponseEntity<List> getFacturesCurrent(
            @PathVariable String walletCode,
            @RequestParam(required = false) String unite) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(paymentServiceUrl + "/api/factures/" + walletCode + "/current");

        if (unite != null && !unite.isEmpty()) {
            builder.queryParam("unite", unite);
        }

        List result = restTemplate.getForObject(builder.toUriString(), List.class);
        return ResponseEntity.ok(result);
    }

    // 2.4 Factures par période
    @GetMapping("/factures/{walletCode}/periode")
    public ResponseEntity<List> getFacturesByPeriode(
            @PathVariable String walletCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(paymentServiceUrl + "/api/factures/" + walletCode + "/periode")
                .queryParam("debut", debut.toString())
                .queryParam("fin", fin.toString());

        List result = restTemplate.getForObject(builder.toUriString(), List.class);
        return ResponseEntity.ok(result);
    }
}