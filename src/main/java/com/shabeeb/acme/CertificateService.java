package com.shabeeb.acme;

import java.io.IOException;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.List;

import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Order;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Challenge;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shabeeb.challenge.ChallengeController;

@Service
public class CertificateService {
	@Autowired
	AccountService accountService;

	public String getCertificateDetails() throws IOException, AcmeException {
		List<String> domains = Arrays.asList("test.com");
		Account account = accountService.getAccount();
		KeyPair domainKeyPair = accountService.loadOrCreateKeyPair("domain.key");
		Order order = account.newOrder().domains(domains).create();
		for (Authorization auth : order.getAuthorizations()) {
			authorize(auth);
		}
		System.out.println();
		return "This is your certificate";
	}

	private void authorize(Authorization auth) throws AcmeException {
		System.out.println("Authorization for domain" + auth.getIdentifier().getDomain().toString());
		Challenge challenge = httpChallenge(auth);
		if (auth.getStatus() == Status.VALID)
			return;
		challenge.trigger();

		try {
			int attempts = 10;
			while (challenge.getStatus() != Status.VALID && attempts-- > 0) {
				if (challenge.getStatus() == Status.INVALID)
					throw new AcmeException("Challenge failed... Giving up.");
				Thread.sleep(3000L);
				challenge.update();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(challenge.getStatus() != Status.VALID) {
			throw new RuntimeException("domain not validated");
		}

	}

	private Challenge httpChallenge(Authorization auth) {
		Http01Challenge challenge = auth.findChallenge("http-01");
		ChallengeController.challengeResponse = challenge.getToken();
		return challenge;
	}

}
