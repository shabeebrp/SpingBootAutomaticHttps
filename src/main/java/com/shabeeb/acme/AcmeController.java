package com.shabeeb.acme;

import java.io.IOException;

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

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping("/account")
	public String getAccountDetails() throws IOException, AcmeException {
		return accountService.getAccountDetails();
	}

}
