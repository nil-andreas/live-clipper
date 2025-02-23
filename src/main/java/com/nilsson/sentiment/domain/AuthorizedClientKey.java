package com.nilsson.sentiment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@Value
@Builder
@AllArgsConstructor
public class AuthorizedClientKey {
    String registrationId;
    String principalName;

    public AuthorizedClientKey(OAuth2AuthenticationToken token) {
        this(token.getAuthorizedClientRegistrationId(), token.getName());
    }
}
