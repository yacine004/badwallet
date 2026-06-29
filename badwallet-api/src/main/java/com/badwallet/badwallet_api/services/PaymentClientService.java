package com.badwallet.badwallet_api.services;

import com.badwallet.badwallet_api.entity.Transaction;
import com.badwallet.badwallet_api.entity.Wallet;
import com.badwallet.badwallet_api.repository.TransactionRepository;
import com.badwallet.badwallet_api.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentClientService {

    private final RestTemplate restTemplate;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    // 1.9 Payer facture du mois en cours
    public String payFactureMoisCourant(String phoneNumber, String serviceName, Double amount) {
        Wallet wallet = walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Wallet introuvable : " + phoneNumber));

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Solde insuffisant. Disponible: " + wallet.getBalance());
        }

        // Appel payment-service
        Map<String, String> body = new HashMap<>();
        body.put("walletCode", wallet.getCode());
        body.put("serviceName", serviceName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                paymentServiceUrl + "/api/factures/pay", request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Erreur payment-service");
        }

        // Débit du wallet
        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);

        Transaction tx = Transaction.builder()
                .type("PAYMENT")
                .amount(amount)
                .senderPhone(phoneNumber)
                .receiverPhone(serviceName)
                .description("Paiement facture " + serviceName + " mois en cours")
                .wallet(wallet)
                .build();
        transactionRepository.save(tx);

        return "Paiement de " + amount + " XOF effectué pour " + serviceName;
    }

    // 1.10 Payer factures spécifiques par références
    public String payFacturesParReferences(String phoneNumber, String serviceName, List<String> references) {
        Wallet wallet = walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Wallet introuvable : " + phoneNumber));

        // Appel payment-service
        Map<String, Object> body = new HashMap<>();
        body.put("factureReferences", references);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<List> response = restTemplate.postForEntity(
                paymentServiceUrl + "/api/factures/pay-references", request, List.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Erreur payment-service");
        }

        List factures = response.getBody();
        double totalMontant = factures.size() * 1000.0; // montant symbolique si pas retourné

        if (wallet.getBalance() < totalMontant) {
            throw new RuntimeException("Solde insuffisant");
        }

        wallet.setBalance(wallet.getBalance() - totalMontant);
        walletRepository.save(wallet);

        Transaction tx = Transaction.builder()
                .type("PAYMENT")
                .amount(totalMontant)
                .senderPhone(phoneNumber)
                .receiverPhone(serviceName)
                .description("Paiement factures: " + String.join(", ", references))
                .wallet(wallet)
                .build();
        transactionRepository.save(tx);

        return "Paiement de " + references.size() + " facture(s) effectué pour " + serviceName;
    }
}