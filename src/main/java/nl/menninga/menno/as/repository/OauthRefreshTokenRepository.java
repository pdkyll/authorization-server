package nl.menninga.menno.as.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nl.menninga.menno.as.entity.OauthRefreshToken;

@Repository
@Transactional
public interface OauthRefreshTokenRepository extends JpaRepository<OauthRefreshToken, Long> {

	OauthRefreshToken findByTokenIdIgnoreCase(String tokenId);
}