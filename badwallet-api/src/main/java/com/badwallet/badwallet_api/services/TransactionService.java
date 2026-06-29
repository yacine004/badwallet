package com.badwallet.badwallet_api.services;

import com.badwallet.badwallet_api.entity.Transaction;
import com.badwallet.badwallet_api.entity.Wallet;
import com.badwallet.badwallet_api.repository.TransactionRepository;
import com.badwallet.badwallet_api.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public Wallet deposit(Long walletId, Double amount, String paymentMethod) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet introuvable : " + walletId));

        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);

        Transaction tx = Transaction.builder()
                .type("DEPOSIT")
                .amount(amount)
                .senderPhone(paymentMethod)
                .receiverPhone(wallet.getPhoneNumber())
                .description("Dépôt via " + paymentMethod)
                .wallet(wallet)
                .build();
        transactionRepository.save(tx);

        return wallet;
    }

    public Wallet withdraw(String phoneNumber, Double amount) {
        Wallet wallet = walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Wallet introuvable : " + phoneNumber));

        double frais = Math.min(amount * 0.01, 5000);
        double total = amount + frais;

        if (wallet.getBalance() < total) {
            throw new RuntimeException("Solde insuffisant. Besoin: " + total + ", Disponible: " + wallet.getBalance());
        }

        wallet.setBalance(wallet.getBalance() - total);
        walletRepository.save(wallet);

        Transaction tx = Transaction.builder()
                .type("WITHDRAW")
                .amount(amount)
                .senderPhone(wallet.getPhoneNumber())
                .receiverPhone("RETRAIT")
                .description("Retrait de " + amount + " XOF, frais: " + frais + " XOF")
                .wallet(wallet)
                .build();
        transactionRepository.save(tx);

        return wallet;
    }

    public String transfer(String senderPhone, String receiverPhone, Double amount) {
        Wallet sender = walletRepository.findByPhoneNumber(senderPhone)
                .orElseThrow(() -> new RuntimeException("Wallet expéditeur introuvable : " + senderPhone));
        Wallet receiver = walletRepository.findByPhoneNumber(receiverPhone)
                .orElseThrow(() -> new RuntimeException("Wallet destinataire introuvable : " + receiverPhone));

        if (sender.getBalance() < amount) {
            throw new RuntimeException("Solde insuffisant. Disponible: " + sender.getBalance());
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);
        walletRepository.save(sender);
        walletRepository.save(receiver);

        Transaction tx = Transaction.builder()
                .type("TRANSFER")
                .amount(amount)
                .senderPhone(senderPhone)
                .receiverPhone(receiverPhone)
                .description("Transfert de " + amount + " XOF vers " + receiverPhone)
                .wallet(sender)
                .build();
        transactionRepository.save(tx);

        return "Transfert de " + amount + " XOF effectué de " + senderPhone + " vers " + receiverPhone;
    }

    public List<Transaction> getTransactions(String phoneNumber) {
        return transactionRepository.findByWallet_PhoneNumber(phoneNumber);
    }
}