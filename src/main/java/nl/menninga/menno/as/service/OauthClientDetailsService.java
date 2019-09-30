package nl.menninga.menno.as.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import nl.menninga.menno.as.entity.OauthClientDetails;
import nl.menninga.menno.as.repository.OauthClientDetailsRepository;

@Service
public class OauthClientDetailsService {

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
	private OauthClientDetailsRepository oauthClientDetailsRepository;
	
	public List<OauthClientDetails> getAllOauthClientDetails(){
		return oauthClientDetailsRepository.findAll();
	}
	
	public OauthClientDetails getOauthClientDetails(String clientId) throws NotFoundException {
		OauthClientDetails oauthClientDetails = oauthClientDetailsRepository.findByClientIdIgnoreCase(clientId);
		if(Objects.isNull(oauthClientDetails)) {
			throw new NotFoundException();
		}
		return oauthClientDetails;
	}

	@Transactional
	public void deleteOauthClientDetails(String clientId) throws NotFoundException {
		OauthClientDetails oauthClientDetails = oauthClientDetailsRepository.findByClientIdIgnoreCase(clientId);
		if(Objects.isNull(oauthClientDetails)) {
			throw new NotFoundException();
		}
		oauthClientDetailsRepository.delete(oauthClientDetails);
	}

	public OauthClientDetails createOauthClientDetails(OauthClientDetails oauthClientDetails) throws IllegalArgumentException {
		if(oauthClientDetails.getClientSecret() != null && !oauthClientDetails.getClientSecret().isEmpty()) {
			oauthClientDetails.setClientSecret(passwordEncoder.encode(oauthClientDetails.getClientSecret()));
		}else {
			throw new IllegalArgumentException("Client secret cannot be empty!");
		}
		return oauthClientDetailsRepository.saveAndFlush(oauthClientDetails);
	}

	public OauthClientDetails updateOauthClientDetails(OauthClientDetails oauthClientDetails) throws NotFoundException {
		OauthClientDetails origOauthClientDetails = oauthClientDetailsRepository.findByClientIdIgnoreCase(oauthClientDetails.getClientId());
		if(Objects.isNull(origOauthClientDetails)) {
			throw new NotFoundException();
		}
		if(oauthClientDetails.getClientSecret() != null && !oauthClientDetails.getClientSecret().isEmpty()) {
			oauthClientDetails.setClientSecret(passwordEncoder.encode(oauthClientDetails.getClientSecret()));
		}else {
			oauthClientDetails.setClientSecret(origOauthClientDetails.getClientSecret());
		}
		return oauthClientDetailsRepository.saveAndFlush(oauthClientDetails);
	}
}
