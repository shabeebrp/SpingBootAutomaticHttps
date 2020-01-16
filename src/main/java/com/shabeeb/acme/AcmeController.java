package com.shabeeb.acme;

import java.io.IOException;
import java.net.URL;

import org.shredzone.acme4j.exception.AcmeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AcmeController {
	@Autowired
	private AccountService accountService;
	@Autowired
	private CertificateService certificateService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping("/account")
	public URL getAccountDetails() throws IOException, AcmeException {
		return accountService.getAccountDetails();
	}
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping("/certificate")
	public String getCertificateDetails() throws IOException, AcmeException {
		return certificateService.getCertificateDetails();
	}	

}
