package nl.menninga.menno.as.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import nl.menninga.menno.as.entity.OauthAccessToken;
import nl.menninga.menno.as.entity.OauthRefreshToken;
import nl.menninga.menno.as.repository.OauthAccessTokenRepository;
import nl.menninga.menno.as.repository.OauthRefreshTokenRepository;

@Service
public class OauthAccessTokenService {
	
	@Autowired
	private OauthAccessTokenRepository oauthAccessTokenRepository;
	
	@Autowired
	private OauthRefreshTokenRepository oauthRefreshTokenRepository;
	
	public List<OauthAccessToken> getAllOauthAccessToken(){
		return oauthAccessTokenRepository.findAll();
	}
	
	public OauthAccessToken getOauthAccessToken(String authenticationId) throws NotFoundException {
		OauthAccessToken oauthAccessToken = oauthAccessTokenRepository.findByAuthenticationIdIgnoreCase(authenticationId);
		if(Objects.isNull(oauthAccessToken)) {
			throw new NotFoundException();
		}
		return oauthAccessToken;
	}
	
	public OauthAccessToken getOauthAccessTokenByUserName(String userName) throws NotFoundException {
		OauthAccessToken oauthAccessToken = oauthAccessTokenRepository.findByUserNameIgnoreCase(userName);
		if(Objects.isNull(oauthAccessToken)) {
			throw new NotFoundException();
		}
		return oauthAccessToken;
	}

	@Transactional
	public void deleteOauthAccessToken(String authenticationId) throws NotFoundException {
		OauthAccessToken oauthAccessToken = oauthAccessTokenRepository.findByAuthenticationIdIgnoreCase(authenticationId);
		if(Objects.isNull(oauthAccessToken)) {
			throw new NotFoundException();
		}
		OauthRefreshToken oauthRefresToken = oauthRefreshTokenRepository.findByTokenIdIgnoreCase(oauthAccessToken.getRefreshToken());
		if(!Objects.isNull(oauthRefresToken)) {
			oauthRefreshTokenRepository.delete(oauthRefresToken);
		}
		oauthAccessTokenRepository.delete(oauthAccessToken);
	}
}
