package nl.menninga.menno.as.service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import nl.menninga.menno.as.entity.OauthAccessToken;
import nl.menninga.menno.as.entity.User;
import nl.menninga.menno.as.repository.UserRepository;

@Service(value = "userDetailsService")
public class ASUserDetailService implements UserDetailsService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ASUserDetailService.class);

	private PasswordEncoder passwordEncoder;

	/**
	 * Set the encoder lazy
	 * @param encoder the {@link PasswordEncoder}
	 */
	@Autowired
	public void setEncoder(@Lazy PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OauthAccessTokenService oauthAccessTokenService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = null;

		if (username.contains("@")){
			user = userRepository.findByEmailIgnoreCase(username);
		}else {
			user = userRepository.findByUsernameIgnoreCase(username);
		}
		
		if (user == null) {
			throw new BadCredentialsException("Bad credentials");
		}

		new AccountStatusUserDetailsChecker().check(user);

		return user;
	}

	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	public User getUser(Long id) throws NotFoundException {
		User user = userRepository.getOne(id);
		if(Objects.isNull(user)) {
			throw new NotFoundException();
		}
		return user;
	}

	public void deleteUser(Long id) throws NotFoundException {
		User user = userRepository.getOne(id);
		if(Objects.isNull(user)) {
			throw new NotFoundException();
		}
		try {
			OauthAccessToken oauthAccessToken = oauthAccessTokenService.getOauthAccessToken(user.getUsername());
			oauthAccessTokenService.deleteOauthAccessToken(oauthAccessToken.getAuthenticationId());
		}catch(NotFoundException e) {
			LOGGER.info("No oauth acces tokens found skip te delete user resource.");
		}
		userRepository.delete(user);
		
	}

	public User createUser(User user) throws IllegalArgumentException {
		if(user.getPassword() != null && !user.getPassword().isEmpty()) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}else {
			throw new IllegalArgumentException("Password cannot be empty!");
		}
		return userRepository.saveAndFlush(user);
	}

	public User updateUser(User user) throws NotFoundException {
		User origUser = userRepository.getOne(user.getId());
		
		if(Objects.isNull(origUser)) {
			throw new NotFoundException();
		}
		
		if(user.getPassword() != null && !user.getPassword().isEmpty()) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			deleteExisitingAccessAndRefreshTokens(user);
		}else {
			user.setPassword(origUser.getPassword());
			if(!user.getUsername().equals(origUser.getUsername()) || 
					!user.getEmail().equals(origUser.getEmail()) || 
					user.isAccountNonExpired() != origUser.isAccountNonExpired() || 
					user.isAccountNonLocked() != origUser.isAccountNonLocked() || 
					user.isCredentialsNonExpired() != origUser.isCredentialsNonExpired() || 
					user.isEnabled() != origUser.isEnabled() || 
					(!origUser.getRoles().containsAll(user.getRoles()) || !user.getRoles().containsAll(origUser.getRoles()))) {
				deleteExisitingAccessAndRefreshTokens(user);
			}
		}
		
		return userRepository.saveAndFlush(user);
	}
	
	private void deleteExisitingAccessAndRefreshTokens(User user) {
		try {
			OauthAccessToken oauthAccessToken = oauthAccessTokenService.getOauthAccessTokenByUserName(user.getUsername());
			oauthAccessTokenService.deleteOauthAccessToken(oauthAccessToken.getAuthenticationId());
		}catch(NotFoundException e) {
			// Nothing to do
		}
	}
}
