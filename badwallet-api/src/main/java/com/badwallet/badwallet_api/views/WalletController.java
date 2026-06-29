package com.badwallet.badwallet_api.views;

import com.badwallet.badwallet_api.entity.Transaction;
import com.badwallet.badwallet_api.entity.Wallet;
import com.badwallet.badwallet_api.services.TransactionService;
import com.badwallet.badwallet_api.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final TransactionService transactionService;

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

    // 1.6 Dépôt
    @PostMapping("/{id}/deposit")
    public ResponseEntity<Wallet> deposit(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Double amount = Double.valueOf(body.get("amount").toString());
        String paymentMethod = body.get("paymentMethod").toString();
        return ResponseEntity.ok(transactionService.deposit(id, amount, paymentMethod));
    }

    // 1.7 Retrait
    @PostMapping("/withdraw")
    public ResponseEntity<Wallet> withdraw(@RequestBody Map<String, Object> body) {
        String phoneNumber = body.get("phoneNumber").toString();
        Double amount = Double.valueOf(body.get("amount").toString());
        return ResponseEntity.ok(transactionService.withdraw(phoneNumber, amount));
    }

    // 1.8 Transfert
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody Map<String, Object> body) {
        String senderPhone = body.get("senderPhone").toString();
        String receiverPhone = body.get("receiverPhone").toString();
        Double amount = Double.valueOf(body.get("amount").toString());
        return ResponseEntity.ok(transactionService.transfer(senderPhone, receiverPhone, amount));
    }

    // 1.11 Historique des transactions
    @GetMapping("/{phoneNumber}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(transactionService.getTransactions(phoneNumber));
    }
}