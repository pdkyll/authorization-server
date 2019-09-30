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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import nl.menninga.menno.as.entity.OauthClientDetails;
import nl.menninga.menno.as.service.OauthClientDetailsService;

@RestController
@CrossOrigin
public class OauthClientDetailsController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OauthClientDetailsController.class);
	
	@Autowired
	private OauthClientDetailsService oauthClientDetailsService;
	
	@GetMapping("api/oauthclientdetails")
    @PreAuthorize("hasRole('AS_ADMIN')")
    public ResponseEntity<List<OauthClientDetails>> users(Principal principal) {
        List<OauthClientDetails> userList = oauthClientDetailsService.getAllOauthClientDetails();
		return ResponseEntity.ok(userList);
    }
	
	@GetMapping("api/oauthclientdetail/{clientId}")
    public ResponseEntity<OauthClientDetails> userGet(@PathVariable String clientId) {
		OauthClientDetails oauthClientDetails = null;
        try {
        	oauthClientDetails = oauthClientDetailsService.getOauthClientDetails(clientId);
		} catch (NotFoundException e) {
			LOGGER.info("Entity could not be found!", e);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(oauthClientDetails);
    }
	
	@PostMapping("api/oauthclientdetail")
	@PreAuthorize("hasRole('AS_ADMIN')")
	public ResponseEntity<?> oauthclientdetailPost(Principal principal, @RequestBody OauthClientDetails oauthclientdetail) {
		try {
			oauthclientdetail = oauthClientDetailsService.createOauthClientDetails(oauthclientdetail);
		} catch (IllegalArgumentException e) {
			LOGGER.info("Entity could not be created!", e);
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
		}
		return ResponseEntity.ok(oauthclientdetail);
	}
	
	@PutMapping("api/oauthclientdetail")
	@PreAuthorize("hasRole('AS_ADMIN')")
	public ResponseEntity<?> oauthclientdetailPut(Principal principal, @RequestBody OauthClientDetails oauthclientdetail) {
		try {
			oauthclientdetail = oauthClientDetailsService.updateOauthClientDetails(oauthclientdetail);
		} catch (NotFoundException e) {
			LOGGER.info("Entity to update could not be found!", e);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(oauthclientdetail);
	}
	
	@DeleteMapping("api/oauthclientdetail/{clientId}")
	@PreAuthorize("hasRole('AS_ADMIN')")
    public ResponseEntity<?> oauthclientdetailDelete(@PathVariable String clientId) {
		try {
	        oauthClientDetailsService.deleteOauthClientDetails(clientId);
		} catch (IllegalArgumentException e) {
			LOGGER.info("Entity could not be deleted!", e);
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
		} catch (NotFoundException e) {
			LOGGER.info("Entity to delete could not be found!", e);
			return ResponseEntity.notFound().build();
		}
        return ResponseEntity.ok("OauthClientDetails deleted.");
    }
}