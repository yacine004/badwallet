package com.badwallet.badwallet_api.services;

import com.badwallet.badwallet_api.entity.Transaction;
import com.badwallet.badwallet_api.entity.Wallet;
import com.badwallet.badwallet_api.repository.TransactionRepository;
import com.badwallet.badwallet_api.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class WalletSeederService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public void seed(int numWallets, int eventsPerWallet) {
        Random random = new Random();
        String[] types = {"DEPOSIT", "WITHDRAW", "TRANSFER", "PAYMENT"};

        for (int i = 1; i <= numWallets; i++) {
            String phone = "+22177000000" + i;
            String code = String.format("WLT-%07d", i);

            if (walletRepository.existsByPhoneNumber(phone)) continue;

            Wallet wallet = Wallet.builder()
                    .phoneNumber(phone)
                    .email("client" + i + "@badwallet.com")
                    .balance(50000.0 + random.nextInt(200000))
                    .code(code)
                    .currency("XOF")
                    .build();

            walletRepository.save(wallet);

            List<Transaction> transactions = new ArrayList<>();
            for (int j = 0; j < eventsPerWallet; j++) {
                String type = types[random.nextInt(types.length)];
                Transaction tx = Transaction.builder()
                        .type(type)
                        .amount(1000.0 + random.nextInt(50000))
                        .senderPhone(phone)
                        .receiverPhone("+22177000000" + (random.nextInt(numWallets) + 1))
                        .description("Seeded transaction " + j)
                        .wallet(wallet)
                        .build();
                transactions.add(tx);
            }
            transactionRepository.saveAll(transactions);
        }
    }
}