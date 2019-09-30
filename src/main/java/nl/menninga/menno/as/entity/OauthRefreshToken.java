package nl.menninga.menno.as.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "oauth_refresh_token", schema="public")
public class OauthRefreshToken implements Serializable {

	private static final long serialVersionUID = -6216442194141916557L;

	@Id
	private String tokenId;
	private byte[] token;
	private byte[] authentication;
	
	public OauthRefreshToken() {}

	public OauthRefreshToken(String tokenId, byte[] token, byte[] authentication) {
		super();
		this.tokenId = tokenId;
		this.token = token;
		this.authentication = authentication;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public byte[] getToken() {
		return token;
	}

	public void setToken(byte[] token) {
		this.token = token;
	}

	public byte[] getAuthentication() {
		return authentication;
	}

	public void setAuthentication(byte[] authentication) {
		this.authentication = authentication;
	}
}