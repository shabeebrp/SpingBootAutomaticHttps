package com.shabeeb.challenge;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChallengeController {
	public static String challengeResponse = "Hello";
	@GetMapping("/.well-known/acme-challenge/{token}")
	public String respondToHttpChallenge(@PathVariable String token) {
		return challengeResponse;
	}
}
