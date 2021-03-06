package com.oi.ai.dashboard.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.oi.ai.dashboard.model.UserEntity;
import com.oi.ai.dashboard.service.UserService;

@RestController
public class AIDashboardController {
	public static final Logger logger = LoggerFactory
			.getLogger(AIDashboardController.class);
	@Autowired
	UserService userService;

	@PostMapping("/users")
	private UserEntity registerUser(@RequestBody UserEntity user) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return userService.save(user);

	}

	@GetMapping("/users/{id}")
	private UserEntity getUser(@PathVariable("id") String id) {
		return userService.getUser(id);

	}
}
