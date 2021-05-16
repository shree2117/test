package com.oi.ai.dashboard.service;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oi.ai.dashboard.model.User;
import com.oi.ai.dashboard.model.UserEntity;

@Service
public class UserService {
	@Autowired
	UserRepository userRepository;
	
	public UserEntity save(UserEntity user) throws NoSuchAlgorithmException, InvalidKeySpecException {
		Optional<UserEntity> userTemp = userRepository.findByUserName(user.getUserName());
		if (userTemp.isPresent()) {
			throw new EntityExistsException("User already exists");
		}
		user.setPassword(generateStorngPasswordHash(user.getPassword()));
		UserEntity userEntity=userRepository.save(user);
		userEntity.setPassword("********");
		return userEntity;
		

	}

	public UserEntity getUser(String userName) {
		Optional<UserEntity> user = userRepository.findByUserName(userName);
		if (user.isPresent()) {
			return user.get();
		} else {
			throw new EntityNotFoundException();
		}

	}

	public void delete(String userName) {
		userRepository.deleteById(userName);

	}

	
	public UserEntity authenticateUser(User user) throws NoSuchAlgorithmException, InvalidKeySpecException {
		Optional<UserEntity> userTemp = userRepository.findById(user
				.getUserName());
		if (userTemp.isPresent()) {
			if (validatePassword(user.getPassword(), userTemp.get()
					.getPassword())) {
				userTemp.get().setPassword("********");
				return userTemp.get();
			}else{
				throw new RuntimeException("Invalid user credentials");
			}

		} else {
			throw new EntityNotFoundException();
		}
	}
	 private static String generateStorngPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException
	    {
	        int iterations = 1000;
	        char[] chars = password.toCharArray();
	        byte[] salt = getSalt();
	         
	        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
	        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	        byte[] hash = skf.generateSecret(spec).getEncoded();
	        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
	    }
	     
	    private static byte[] getSalt() throws NoSuchAlgorithmException
	    {
	        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
	        byte[] salt = new byte[16];
	        sr.nextBytes(salt);
	        return salt;
	    }
	     
	    private static String toHex(byte[] array) throws NoSuchAlgorithmException
	    {
	        BigInteger bi = new BigInteger(1, array);
	        String hex = bi.toString(16);
	        int paddingLength = (array.length * 2) - hex.length();
	        if(paddingLength > 0)
	        {
	            return String.format("%0"  +paddingLength + "d", 0) + hex;
	        }else{
	            return hex;
	        }
	    }
	    
	    private static boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException
	    {
	        String[] parts = storedPassword.split(":");
	        int iterations = Integer.parseInt(parts[0]);
	        byte[] salt = fromHex(parts[1]);
	        byte[] hash = fromHex(parts[2]);
	         
	        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
	        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	        byte[] testHash = skf.generateSecret(spec).getEncoded();
	         
	        int diff = hash.length ^ testHash.length;
	        for(int i = 0; i < hash.length && i < testHash.length; i++)
	        {
	            diff |= hash[i] ^ testHash[i];
	        }
	        return diff == 0;
	    }
	    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException
	    {
	        byte[] bytes = new byte[hex.length() / 2];
	        for(int i = 0; i<bytes.length ;i++)
	        {
	            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
	        }
	        return bytes;
	    }
}
