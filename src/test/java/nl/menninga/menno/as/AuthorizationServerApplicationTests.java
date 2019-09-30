package nl.menninga.menno.as;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import nl.menninga.menno.as.entity.Role;
import nl.menninga.menno.as.entity.User;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"eureka.client.enabled:false"})
public class AuthorizationServerApplicationTests {

	@LocalServerPort
    int port;

    @Before
    public void setUp() {
    	RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }
	
	@Autowired
    private JdbcTokenStore tokenStore;
 
    @Test
    public void testAuthenticationAuthorizationAndResources() throws IOException, JSONException {
    	Response response = obtainAccessToken("apiClient", "93b1416b-c710-444f-a740-e56b31fbc75a", "mennowm", "Welkom01!");
        System.out.println("----Response obtain acces token----: \n" + response.prettyPrint() + "\n--------------------");
        readAndValidateResponses(response);
        
        response = refreshAccessToken("apiClient", "93b1416b-c710-444f-a740-e56b31fbc75a", response.jsonPath().getString("refresh_token"));
        System.out.println("----Response refresh acces token----: \n" + response.prettyPrint() + "\n--------------------");
        readAndValidateResponses(response);
        
        String accessToken = response.jsonPath().getString("access_token");
        String refreshToken = response.jsonPath().getString("refresh_token");
        

    	User user = createAndValidateUserResource(accessToken);
        
        response = checkUserResources(accessToken);
        
        user = checkAndValidateUserResource(response);
        
        response = refreshAccessToken("apiClient", "93b1416b-c710-444f-a740-e56b31fbc75a", refreshToken);
        accessToken = response.jsonPath().getString("access_token");
        refreshToken = response.jsonPath().getString("refresh_token");
        
        Role role = createAndValidateRoleResource(accessToken);
        
        role.setRole("ROLE_UPDATE_TEST");
        role = updateAndValidateRoleResource(accessToken, role);
        
        response = refreshAccessToken("apiClient", "93b1416b-c710-444f-a740-e56b31fbc75a", refreshToken);
        accessToken = response.jsonPath().getString("access_token");
        refreshToken = response.jsonPath().getString("refresh_token");
        
        user.addRoles(role);
        user.setUsername("testupdatedusername");
        user = updateAndValidateUserResource(accessToken, user);
        
        response = refreshAccessToken("apiClient", "93b1416b-c710-444f-a740-e56b31fbc75a", refreshToken);
        accessToken = response.jsonPath().getString("access_token");
        refreshToken = response.jsonPath().getString("refresh_token");
        
        deleteAndValidateRoleResource(accessToken, role);
        
        deleteAndValidateUserResource(accessToken, user);
    }
    
    private Role createAndValidateRoleResource(String accessToken) throws IOException, JSONException {
    	Role role = new Role();
    	role.setRole("ROLE_TEST");
        Response response = createRoleResource(accessToken, role);
        System.out.println("----Response creating resource----: \n" + response.prettyPrint() + "\n--------------------");
        ObjectMapper mapper = new ObjectMapper();
        role = mapper.readValue(response.getBody().asString(), Role.class);
        return role;
    }
    
    private Role updateAndValidateRoleResource(String accessToken, Role role) throws IOException, JSONException {
    	Response response = alterRoleResources(accessToken, role);
    	System.out.println("----Response updating resource----: \n" + response.prettyPrint() + "\n--------------------");
        ObjectMapper mapper = new ObjectMapper();
        role = mapper.readValue(response.getBody().asString(), Role.class);
        assertTrue("ROLE_UPDATE_TEST".equals(role.getRole()));
        return role;
    }
    
    private void deleteAndValidateRoleResource(String accessToken, Role role) {
    	Response response = deleteRoleResources(accessToken, role);
    	System.out.println("----Response deleting resource----: \n" + response.prettyPrint() + "\n--------------------");
    	assertTrue("Role deleted.".equals(response.getBody().asString()));
    }
 
    private Response deleteRoleResources(String accessToken, Role role) {
        return RestAssured.given()
			.header("Authorization", "Bearer " + accessToken)
			.delete("api/role/" + role.getId());
    }
    
    private void readAndValidateResponses(Response response) {
    	OAuth2Authentication auth = tokenStore.readAuthentication(response.jsonPath().getString("access_token"));
		User user = (User)auth.getUserAuthentication().getPrincipal();
        assertTrue("mennowm".equals(user.getUsername()));
    }
    
    private User createAndValidateUserResource(String accessToken) throws IOException, JSONException {
    	User user = new User("test@test.nl", "testusername", "testpassword", true, true, true, true);
    	List<Role> roleList = new ArrayList<>();
    	roleList.add(new Role(1L, "ROLE_AS_ADMIN"));
    	user.setRoles(roleList);
        Response response = createUserResource(accessToken, user);
        System.out.println("----Response creating resource----: \n" + response.prettyPrint() + "\n--------------------");
        ObjectMapper mapper = new ObjectMapper();
        user = mapper.readValue(response.getBody().asString(), User.class);
        return user;
    }
    
    private Response createRoleResource(String accessToken, Role role) throws JsonProcessingException, JSONException {
    	ObjectMapper mapper = new ObjectMapper();
    	return RestAssured.given()
			.header("Authorization", "Bearer " + accessToken)
			.contentType("application/json")
			.and().with().body(mapper.writeValueAsString(role))
			.post("api/role");
    }
    
    private Response alterRoleResources(String accessToken, Role role) throws JsonProcessingException, JSONException {
    	ObjectMapper mapper = new ObjectMapper();
    	return RestAssured.given()
			.header("Authorization", "Bearer " + accessToken)
			.contentType("application/json")
			.and().with().body(mapper.writeValueAsString(role))
			.put("api/role");
    }
    
    private User updateAndValidateUserResource(String accessToken, User user) throws IOException, JSONException {
    	Response response = alterUserResources(accessToken, user);
    	System.out.println("----Response updating resource----: \n" + response.prettyPrint() + "\n--------------------");
        ObjectMapper mapper = new ObjectMapper();
        user = mapper.readValue(response.getBody().asString(), User.class);
        assertTrue("testupdatedusername".equals(user.getUsername()));
        return user;
    }
    
    private void deleteAndValidateUserResource(String accessToken, User user) {
    	Response response = deleteUserResources(accessToken, user);
    	System.out.println("----Response deleting resource----: \n" + response.prettyPrint() + "\n--------------------");
    	assertTrue("User deleted.".equals(response.getBody().asString()));
    }
    
    private User checkAndValidateUserResource(Response response) {
    	System.out.println("----Response resource request----: \n" + response.prettyPrint() + "\n--------------------");
    	List<User> userList = response.jsonPath().getList(".", User.class);
    	assertTrue(userList.size() == 2);
    	for(User user : userList) {
    		if(!"mennowm".equals(user.getUsername())) {
    			assertTrue("testusername".equals(user.getUsername()));
    			return user;
    		}
    	}
    	return null;
    }
 
    private Response obtainAccessToken(String clientId, String secret, String username, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "password");
        params.put("scope", "read+write+trust");
        params.put("username", username);
        params.put("password", password);
        return RestAssured.given()
			.auth().preemptive().basic(clientId, secret)
			.and().with().params(params).when()
			.post("/oauth/token");
    }
 
    private Response refreshAccessToken(String clientId, String secret, String refreshToken) {
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "refresh_token");
        params.put("scope", "read+write+trust");
        params.put("refresh_token", refreshToken);
        return RestAssured.given()
			.auth().preemptive().basic(clientId, secret)
			.and().with().params(params).when()
			.post("/oauth/token");
    }
    
    private Response checkUserResources(String accessToken) {
    	return RestAssured.given()
			.header("Authorization", "Bearer " + accessToken)
			.get("api/users");
    }
    
    private Response createUserResource(String accessToken, User user) throws JsonProcessingException, JSONException {
    	ObjectMapper mapper = new ObjectMapper();
    	JSONObject jsonObject = new JSONObject(mapper.writeValueAsString(user));
    	jsonObject.put("password", user.getPassword());
    	return RestAssured.given()
			.header("Authorization", "Bearer " + accessToken)
			.contentType("application/json")
			.and().with().body(jsonObject.toString())
			.post("api/user");
    }
    
    private Response alterUserResources(String accessToken, User user) throws JsonProcessingException, JSONException {
    	ObjectMapper mapper = new ObjectMapper();
    	JSONObject jsonObject = new JSONObject(mapper.writeValueAsString(user));
    	if(user.getPassword() != null && !user.getPassword().isEmpty()) {
    		jsonObject.put("password", user.getPassword());
    	}
    	return RestAssured.given()
			.header("Authorization", "Bearer " + accessToken)
			.contentType("application/json")
			.and().with().body(jsonObject.toString())
			.put("api/user");
    }
 
    private Response deleteUserResources(String accessToken, User user) {
    	System.out.println("----Deleting user resource----: \n" + user.getId() + "\n--------------------");
        return RestAssured.given()
			.header("Authorization", "Bearer " + accessToken)
			.delete("api/user/" + user.getId());
    }
}
