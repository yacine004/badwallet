package com.badwallet.badwallet_api.views;

import com.badwallet.badwallet_api.services.WalletSeederService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletSeederController {

    private final WalletSeederService walletSeederService;

    @PostMapping("/seed")
    public ResponseEntity<String> seed(
            @RequestParam(defaultValue = "10") int numWallets,
            @RequestParam(defaultValue = "100") int eventsPerWallet) {

        new Thread(() -> walletSeederService.seed(numWallets, eventsPerWallet)).start();

        return ResponseEntity.ok("Seeding démarré en arrière-plan : "
                + numWallets + " wallets, " + eventsPerWallet + " transactions chacun.");
    }
}