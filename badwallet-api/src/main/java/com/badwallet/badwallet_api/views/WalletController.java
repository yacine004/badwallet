package com.badwallet.badwallet_api.views;

import com.badwallet.badwallet_api.entity.Transaction;
import com.badwallet.badwallet_api.entity.Wallet;
import com.badwallet.badwallet_api.services.PaymentClientService;
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
    private final PaymentClientService paymentClientService;

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody Wallet wallet) {
        return ResponseEntity.ok(walletService.createWallet(wallet));
    }

    @GetMapping
    public ResponseEntity<Page<Wallet>> getAllWallets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(walletService.getAllWallets(page, size));
    }

    @GetMapping("/{phoneNumber}")
    public ResponseEntity<Wallet> getWalletByPhone(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(walletService.getWalletByPhone(phoneNumber));
    }

    @GetMapping("/{phoneNumber}/balance")
    public ResponseEntity<Double> getBalance(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(walletService.getBalance(phoneNumber));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<Wallet> deposit(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Double amount = Double.valueOf(body.get("amount").toString());
        String paymentMethod = body.get("paymentMethod").toString();
        return ResponseEntity.ok(transactionService.deposit(id, amount, paymentMethod));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Wallet> withdraw(@RequestBody Map<String, Object> body) {
        String phoneNumber = body.get("phoneNumber").toString();
        Double amount = Double.valueOf(body.get("amount").toString());
        return ResponseEntity.ok(transactionService.withdraw(phoneNumber, amount));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody Map<String, Object> body) {
        String senderPhone = body.get("senderPhone").toString();
        String receiverPhone = body.get("receiverPhone").toString();
        Double amount = Double.valueOf(body.get("amount").toString());
        return ResponseEntity.ok(transactionService.transfer(senderPhone, receiverPhone, amount));
    }

    @PostMapping("/pay")
    public ResponseEntity<String> pay(@RequestBody Map<String, Object> body) {
        String phoneNumber = body.get("phoneNumber").toString();
        String serviceName = body.get("serviceName").toString();
        Double amount = Double.valueOf(body.get("amount").toString());
        return ResponseEntity.ok(paymentClientService.payFactureMoisCourant(phoneNumber, serviceName, amount));
    }

    @PostMapping("/pay-factures")
    public ResponseEntity<String> payFactures(@RequestBody Map<String, Object> body) {
        String phoneNumber = body.get("phoneNumber").toString();
        String serviceName = body.get("serviceName").toString();
        List<String> references = (List<String>) body.get("factureReferences");
        return ResponseEntity.ok(paymentClientService.payFacturesParReferences(phoneNumber, serviceName, references));
    }

    @GetMapping("/{phoneNumber}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(transactionService.getTransactions(phoneNumber));
    }
}