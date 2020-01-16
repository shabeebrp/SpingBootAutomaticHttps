package com.shabeeb.acme;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.KeyPair;

import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.AccountBuilder;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

	public URL getAccountDetails() throws IOException, AcmeException {
		Account account = getAccount();
		return account.getLocation();
	}
	
	public Account getAccount() throws  IOException, AcmeException {
		System.out.println("load  key pair (pubKey, privKey)");
		KeyPair accountKeyPair = loadOrCreateKeyPair("account.key");
		System.out.println("Creating a Session");
		Session session = new Session("https://localhost:14000/dir");
		System.out.println("Getting account details for key pair from ca");
		Account account = new AccountBuilder().agreeToTermsOfService().useKeyPair(accountKeyPair).create(session);
		System.out.println(accountKeyPair.getPublic().toString());
		System.out.println(accountKeyPair.getPrivate().toString());
		return account;
	}

	public KeyPair loadOrCreateKeyPair(String filename) throws IOException {
		File accountKeyFile = new File(filename);
		System.out.println("try to read the key pair from file account.key");
		if (accountKeyFile.exists()) {
			try (FileReader fr = new FileReader(accountKeyFile)) {
				System.out.println("reading  from "+filename);
				return KeyPairUtils.readKeyPair(fr);
			}
		} else {
			
			KeyPair accountKeyPair = KeyPairUtils.createKeyPair(2048);
			try (FileWriter fw = new FileWriter(accountKeyFile)) {
				System.out.println(filename +"doesn't exists so creating the file");
				KeyPairUtils.writeKeyPair(accountKeyPair, fw);
			}
			return accountKeyPair;
		}
	}


}
