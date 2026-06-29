package com.badwallet.payment_service.views;

import com.badwallet.payment_service.entity.Facture;
import com.badwallet.payment_service.services.FactureSeederService;
import com.badwallet.payment_service.services.FactureService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;
    private final FactureSeederService factureSeederService;

    // Seeder
    @PostMapping("/api/factures/seed")
    public ResponseEntity<String> seed(@RequestParam(defaultValue = "10") int numWallets) {
        new Thread(() -> factureSeederService.seed(numWallets)).start();
        return ResponseEntity.ok("Seeding factures démarré pour " + numWallets + " wallets.");
    }

    // Factures impayées du mois en cours
    @GetMapping("/api/factures/{walletCode}/current")
    public ResponseEntity<List<Facture>> getFacturesCurrent(
            @PathVariable String walletCode,
            @RequestParam(required = false) String unite) {
        return ResponseEntity.ok(factureService.getFacturesMoisCourant(walletCode, unite));
    }

    // Factures par période
    @GetMapping("/api/factures/{walletCode}/periode")
    public ResponseEntity<List<Facture>> getFacturesByPeriode(
            @PathVariable String walletCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(factureService.getFacturesByPeriode(walletCode, debut, fin));
    }

    // Payer facture du mois en cours
    @PostMapping("/api/factures/pay")
    public ResponseEntity<Facture> payerMoisCourant(@RequestBody Map<String, String> body) {
        String walletCode = body.get("walletCode");
        String unite = body.get("serviceName");
        return ResponseEntity.ok(factureService.payerFactureMoisCourant(walletCode, unite));
    }

    // Payer factures par références
    @PostMapping("/api/factures/pay-references")
    public ResponseEntity<List<Facture>> payerParReferences(@RequestBody Map<String, Object> body) {
        List<String> references = (List<String>) body.get("factureReferences");
        return ResponseEntity.ok(factureService.payerFacturesParReferences(references));
    }
}