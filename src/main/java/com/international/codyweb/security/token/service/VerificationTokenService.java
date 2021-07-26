package com.international.codyweb.security.token.service;

import com.international.codyweb.security.token.model.VerificationToken;

public interface VerificationTokenService {
	VerificationToken createVerificationToken();
    void saveVerificationToken(final VerificationToken token);
    VerificationToken findByToken(final String token);
    void removeToken(final VerificationToken token);
    void removeTokenByToken(final String token);
}