package nl.menninga.menno.as.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nl.menninga.menno.as.entity.OauthAccessToken;

@Repository
@Transactional
public interface OauthAccessTokenRepository extends JpaRepository<OauthAccessToken, Long> {

	OauthAccessToken findByAuthenticationIdIgnoreCase(String authenticationId);
	OauthAccessToken findByUserNameIgnoreCase(String userName);
}