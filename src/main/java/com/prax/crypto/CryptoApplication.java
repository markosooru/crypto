package com.prax.crypto;

import com.prax.crypto.account.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CryptoApplication {

    public static void main(String[] args) {
		SpringApplication.run(CryptoApplication.class, args);
	}
}
