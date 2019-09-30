package nl.menninga.menno.as.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import nl.menninga.menno.as.entity.OauthRefreshToken;
import nl.menninga.menno.as.repository.OauthRefreshTokenRepository;

@Service
public class OauthRefreshTokenService {
	
	@Autowired
	private OauthRefreshTokenRepository oauthRefreshTokenRepository;
	
	public List<OauthRefreshToken> getAllOauthRefreshToken(){
		return oauthRefreshTokenRepository.findAll();
	}
	
	public OauthRefreshToken getOauthRefreshToken(String tokenId) throws NotFoundException {
		OauthRefreshToken oauthRefreshToken = oauthRefreshTokenRepository.findByTokenIdIgnoreCase(tokenId);
		if(Objects.isNull(oauthRefreshToken)) {
			throw new NotFoundException();
		}
		return oauthRefreshToken;
	}

	@Transactional
	public void deleteOauthRefreshToken(String tokenId) throws NotFoundException {
		OauthRefreshToken oauthRefreshToken = oauthRefreshTokenRepository.findByTokenIdIgnoreCase(tokenId);
		if(Objects.isNull(oauthRefreshToken)) {
			throw new NotFoundException();
		}
		oauthRefreshTokenRepository.delete(oauthRefreshToken);
	}
}
