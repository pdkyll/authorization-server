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

import nl.menninga.menno.as.entity.OauthAccessToken;
import nl.menninga.menno.as.service.OauthAccessTokenService;

@RestController
@CrossOrigin
public class OauthAccessTokenController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OauthAccessTokenController.class);
	
	@Autowired
	private OauthAccessTokenService oauthAccessTokenService;
	
	@GetMapping("api/oauthaccesstokens")
    @PreAuthorize("hasRole('AS_ADMIN')")
    public ResponseEntity<List<OauthAccessToken>> users(Principal principal) {
        List<OauthAccessToken> userList = oauthAccessTokenService.getAllOauthAccessToken();
		return ResponseEntity.ok(userList);
    }
	
	@GetMapping("api/oauthaccesstoken/{authenticationId}")
    public ResponseEntity<OauthAccessToken> userGet(@PathVariable String authenticationId) {
		OauthAccessToken oauthAccessToken = null;
        try {
        	oauthAccessToken = oauthAccessTokenService.getOauthAccessToken(authenticationId);
		} catch (NotFoundException e) {
			LOGGER.info("Entity could not be found!", e);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(oauthAccessToken);
    }
	
	@DeleteMapping("api/oauthaccesstoken/{authenticationId}")
	@PreAuthorize("hasRole('AS_ADMIN')")
    public ResponseEntity<?> oauthclientdetailDelete(@PathVariable String authenticationId) {
		try {
	        oauthAccessTokenService.deleteOauthAccessToken(authenticationId);
		} catch (IllegalArgumentException e) {
			LOGGER.info("Entity could not be deleted!", e);
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
		} catch (NotFoundException e) {
			LOGGER.info("Entity to delete could not be found!", e);
			return ResponseEntity.notFound().build();
		}
        return ResponseEntity.ok("OauthAccessToken deleted.");
    }
}