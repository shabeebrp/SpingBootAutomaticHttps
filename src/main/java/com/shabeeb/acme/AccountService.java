package com.shabeeb.acme;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;

import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.AccountBuilder;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

	public String getAccountDetails() throws IOException, AcmeException {
		System.out.println("load  key pair (pubKey, privKey)");
		KeyPair accountKeyPair = loadOrCreateAccountKeyPair();
		System.out.println("Creating a Session");
		Session session = new Session("acme://letsencrypt.org/staging");
		System.out.println("Getting account details for key pair from ca");
		Account account = new AccountBuilder().agreeToTermsOfService().useKeyPair(accountKeyPair).create(session);
		System.out.println(accountKeyPair.getPublic().toString());
		System.out.println(accountKeyPair.getPrivate().toString());
		return account.getJSON().toString();
	}

	private KeyPair loadOrCreateAccountKeyPair() throws IOException {
		File accountKeyFile = new File("account.key");
		System.out.println("try to read the key pair from file account.key");
		if (accountKeyFile.exists()) {
			try (FileReader fr = new FileReader(accountKeyFile)) {
				System.out.println("reading  from account.key");
				return KeyPairUtils.readKeyPair(fr);
			}
		} else {
			
			KeyPair accountKeyPair = KeyPairUtils.createKeyPair(2048);
			try (FileWriter fw = new FileWriter(accountKeyFile)) {
				System.out.println("account.key file doesn't exists so creating the file");
				KeyPairUtils.writeKeyPair(accountKeyPair, fw);
			}
			return accountKeyPair;
		}
	}


}
