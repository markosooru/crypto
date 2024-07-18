package com.prax.crypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableConfigurationProperties(RsaKeyProperties.class)
public class CryptoApplication {

    public static void main(String[] args) {
		SpringApplication.run(CryptoApplication.class, args);
	}
}
