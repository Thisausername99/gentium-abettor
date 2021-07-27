package com.international.codyweb.web.payload.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor

public class TokenRefreshResponse {
	  private String accessToken;
	  private String refreshToken;
	  private String tokenType = "Bearer";

	  public TokenRefreshResponse(String accessToken, String refreshToken) {
	    this.accessToken = accessToken;
	    this.refreshToken = refreshToken;
	  }
}