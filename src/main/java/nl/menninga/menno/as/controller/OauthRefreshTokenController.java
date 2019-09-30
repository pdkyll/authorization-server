package nl.menninga.menno.as.controller;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import nl.menninga.menno.as.entity.OauthRefreshToken;
import nl.menninga.menno.as.service.OauthRefreshTokenService;

@RestController
@CrossOrigin
public class OauthRefreshTokenController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OauthRefreshTokenController.class);
	
	@Autowired
	private OauthRefreshTokenService oauthRefreshTokenService;
	
	@GetMapping("api/oauthrefreshtokens")
    @PreAuthorize("hasRole('AS_ADMIN')")
    public ResponseEntity<List<OauthRefreshToken>> users(Principal principal) {
        List<OauthRefreshToken> userList = oauthRefreshTokenService.getAllOauthRefreshToken();
		return ResponseEntity.ok(userList);
    }
	
	@GetMapping("api/oauthrefreshtoken/{tokenId}")
    public ResponseEntity<OauthRefreshToken> userGet(@PathVariable String tokenId) {
		OauthRefreshToken oauthRefreshToken = null;
        try {
        	oauthRefreshToken = oauthRefreshTokenService.getOauthRefreshToken(tokenId);
		} catch (NotFoundException e) {
			LOGGER.info("Entity could not be found!", e);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(oauthRefreshToken);
    }
	
	@DeleteMapping("api/oauthrefreshtoken/{tokenId}")
	@PreAuthorize("hasRole('AS_ADMIN')")
    public ResponseEntity<?> oauthclientdetailDelete(@PathVariable String tokenId) {
		try {
	        oauthRefreshTokenService.deleteOauthRefreshToken(tokenId);
		} catch (IllegalArgumentException e) {
			LOGGER.info("Entity could not be deleted!", e);
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
		} catch (NotFoundException e) {
			LOGGER.info("Entity to delete could not be found!", e);
			return ResponseEntity.notFound().build();
		}
        return ResponseEntity.ok("OauthRefreshToken deleted.");
    }
}