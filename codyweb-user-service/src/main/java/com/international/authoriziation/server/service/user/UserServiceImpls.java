package com.international.authoriziation.server.service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


//import org.springframework.data.domain.Example;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
//import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.international.authoriziation.server.exception.InvalidTokenException;
import com.international.authoriziation.server.exception.ResourceNotFoundException;
import com.international.authoriziation.server.exception.UserAlreadyExistException;
import com.international.authoriziation.server.exception.UserNotVerifiedException;
import com.international.authoriziation.server.model.dto.SignupRequest;
import com.international.authoriziation.server.model.mapper.UserMapper;
//import com.international.authoriziation.server.model.entity.User;
import com.international.authoriziation.server.model.repository.UserRepository;
import com.international.authoriziation.server.role.ERole;
import com.international.authoriziation.server.role.Role;
import com.international.authoriziation.server.role.RoleRepository;
import com.international.authoriziation.server.token.pojo.VerificationToken;
import com.international.authoriziation.server.token.service.VerificationTokenService;
import com.international.authoriziation.server.util.email.AccountVerificationEmailContext;
import com.international.authoriziation.server.model.entity.UserEntity;

//import com.international.codyweb.util.RedisUtil;
//import com.international.codyweb.web.payload.request.SignupRequest;

@Service
@Transactional
public class UserServiceImpls implements UserService {

	//	private final String USER_ = "USER_";
	//
	//	private final String TABLE_USER = "TABLE_USER";

	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpls.class);

	private UserMapper userMapper = new UserMapper();
	
	@Autowired
	private EmailService emailService;

	


	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private VerificationTokenService verificationTokenService;


	@Autowired
	private PasswordEncoder passwordEncoder;


	@Override
	public List<UserEntity> getUser() {
		return userRepository.findAll();
	}



	//	@Autowired
	//	private RedisUtil<User> userRedisUtil;

	@Value("${cody.app.base.url}")
	private String baseURL;

    
	//    @Autowired
	//    RedisTemplate<String, Object> redisTemplate;
	//    private HashOperations<String, Long, User> hashOperations;




	//    // This annotation makes sure that the method needs to be executed after 
	//    // dependency injection is done to perform any initialization.
	//    @PostConstruct
	//    private void intializeHashOperations() {
	//        hashOperations = redisTemplate.opsForHash();
	//    }

	//save to redis first then db
	//    public void saveUser(final User user) {
	//        userRepository.save(user);
	//    }
	//        


	public UserEntity findUserByEmail(final String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User is not found with email: " + email));
	}


	public UserEntity findById(Long id) throws ResourceNotFoundException {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Error: User is not found."));
	}
	// Find all employees' operation.
	//    public Map<Long, User> findAllUser() {
	//        return hashOperations.entries(USER_CACHE);
	//    }

	// Delete employee by id operation.
	//	public void delete(Long id) {
	//		//        hashOperations.delete(USER_CACHE, id);
	//		userRepository.deleteById(id);
	//	}

	@Override
	public void register(SignupRequest signupRequest) throws UserAlreadyExistException {
		if(checkIfUserExist(signupRequest.getEmail())){
			throw new UserAlreadyExistException("User already exists for this email");
		}
		LOG.info("the email is " + signupRequest.getEmail());
		LOG.info("the username is " + signupRequest.getUsername());
		LOG.info("the pw is " + signupRequest.getPassword());
		UserEntity user = userMapper.convertToEntity(signupRequest, new UserEntity());
		encodePassword(signupRequest, user);
		LOG.info("the user email is: " + user.getEmail());
		updateUserRoles(user);
		userRepository.save(user);
		System.out.println("GOT HERE");
		sendRegistrationConfirmationEmail(user);

	}


	private void updateUserRoles(UserEntity user){ 
		Role userRole= roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));;
		user.addUserRoles(userRole);
	}


	@Override
	public boolean checkIfUserExist(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public void sendRegistrationConfirmationEmail(UserEntity user) {
		VerificationToken verificationToken = verificationTokenService.createVerificationToken();
		verificationToken.setUser(user);

		//		System.out.println(user.getEmail());
		verificationTokenService.saveVerificationToken(verificationToken);
		AccountVerificationEmailContext emailContext = new AccountVerificationEmailContext();
		emailContext.init(user);
		emailContext.setToken(verificationToken.getToken());
		emailContext.buildVerificationUrl(baseURL, verificationToken.getToken());
		System.out.println(verificationToken.getToken());
//		try {
//			System.out.println("Stuck here");
//			emailService.sendMail(emailContext);
//		} catch (MessagingException e) {
//
//			e.printStackTrace();
//		}

	}



	//	@Async
	@Override
	public boolean verifyUser(String token) throws InvalidTokenException {
		VerificationToken verificationToken = verificationTokenService.findByToken(token);
		if(Objects.isNull(verificationToken) || !StringUtils.equals(token, verificationToken.getToken()) || verificationToken.isExpired()){
			throw new InvalidTokenException("Token is not valid");
		}
		try {
			UserEntity user = findById(verificationToken.getUser().getId());
			user.setAccountVerified(true);


			//memberRepository.save(memberEntity);
			userRepository.save(user); // let's same user details

			// we don't need invalid password now
			verificationTokenService.removeToken(verificationToken);
			//		try {
			//			userRedisUtil.putMap(TABLE_USER, USER_ + userEntity.getId(), userEntity);
			//			userRedisUtil.setExpire(TABLE_USER, 2, TimeUnit.MINUTES);
			//		} catch (Exception e) {
			//			System.out.println(e.getCause());
			//		}


			return true;
		}
		catch (ResourceNotFoundException e) {
			return false;
		}
	}





	private void encodePassword(SignupRequest source, UserEntity target){
		target.setPassword(passwordEncoder.encode(source.getPassword()));
	}


	@Override
	public void checkIfUserVerified(String email) throws UserNotVerifiedException {
		try {
			UserEntity userEntity = findUserByEmail(email);
			if (!userEntity.isAccountVerified()) {
				throw new UserNotVerifiedException("User is not verified");
			}
		} catch (ResourceNotFoundException e){
			e.getCause();
		}

	}




}