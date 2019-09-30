package nl.menninga.menno.as.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nl.menninga.menno.as.entity.OauthClientDetails;

@Repository
@Transactional
public interface OauthClientDetailsRepository extends JpaRepository<OauthClientDetails, Long> {

	OauthClientDetails findByClientIdIgnoreCase(String clientId);
}