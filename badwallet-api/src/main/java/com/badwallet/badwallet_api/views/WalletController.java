package com.badwallet.badwallet_api.views;

import com.badwallet.badwallet_api.entity.Wallet;
import com.badwallet.badwallet_api.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // 1.2 Créer un wallet
    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody Wallet wallet) {
        return ResponseEntity.ok(walletService.createWallet(wallet));
    }

    // 1.3 Lister tous les wallets (paginé)
    @GetMapping
    public ResponseEntity<Page<Wallet>> getAllWallets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(walletService.getAllWallets(page, size));
    }

    // 1.4 Consulter un wallet par téléphone
    @GetMapping("/{phoneNumber}")
    public ResponseEntity<Wallet> getWalletByPhone(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(walletService.getWalletByPhone(phoneNumber));
    }

    // 1.5 Consulter uniquement le solde
    @GetMapping("/{phoneNumber}/balance")
    public ResponseEntity<Double> getBalance(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(walletService.getBalance(phoneNumber));
    }
}