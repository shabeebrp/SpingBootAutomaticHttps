package com.shabeeb.acme;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.Order;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Challenge;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.CSRBuilder;
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
		System.out.println("Your domain is authorized");

		// Generate a CSR for all of the domains, and sign it with the domain key pair.
        CSRBuilder csrb = new CSRBuilder();
        csrb.addDomains(domains);
        csrb.sign(domainKeyPair);
         // Write the CSR to a file, for later use.
         try (Writer out = new FileWriter("Domain.csr")) {
             csrb.write(out);
         }
 
         // Order the certificate
         order.execute(csrb.getEncoded());
 
         // Wait for the order to complete
         try {
             int attempts = 10;
             while (order.getStatus() != Status.VALID && attempts-- > 0) {
                 // Did the order fail?
                 if (order.getStatus() == Status.INVALID) {
                     throw new AcmeException("Order failed... Giving up.");
                 }
 
                 // Wait for a few seconds
                 Thread.sleep(3000L);
 
                 // Then update the status
                 order.update();
             }
         } catch (InterruptedException ex) {
            ex.printStackTrace();
         }
 
         // Get the certificate
         Certificate certificate = order.getCertificate();
 
         System.out.println("Success! The certificate for domains {} has been generated! "+domains);
         System.out.println("Certificate URL: {} "+ certificate.getLocation().toString());
 
         // Write a combined file containing the certificate and chain.
         try (FileWriter fw = new FileWriter("domain-chain.crt")) {
             certificate.writeCertificate(fw);
         }
 
		return certificate.getCertificate().toString();
	}

	private void authorize(Authorization auth) throws AcmeException {
		System.out.println("Authorization for domain " + auth.getIdentifier().getDomain().toString());
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
		ChallengeController.challengeResponse = challenge.getAuthorization();
		return challenge;
	}

}
