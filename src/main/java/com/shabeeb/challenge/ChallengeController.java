package com.shabeeb.challenge;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChallengeController {
	
	@GetMapping("/.well-known/acme-challenge/{token}")
	public String respondToHttpChallenger(@PathVariable String token) {
		return "This is my response string to your challenge - "+token;
	}
}
