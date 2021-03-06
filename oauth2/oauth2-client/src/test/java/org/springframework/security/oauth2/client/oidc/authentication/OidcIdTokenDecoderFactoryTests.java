/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.oauth2.client.oidc.authentication;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.TestClientRegistrations;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Joe Grandja
 * @author Rafael Dominguez
 * @since 5.2
 */
public class OidcIdTokenDecoderFactoryTests {

	private ClientRegistration.Builder registration = TestClientRegistrations.clientRegistration()
			.scope("openid");

	private OidcIdTokenDecoderFactory idTokenDecoderFactory;

	private Function<ClientRegistration, OAuth2TokenValidator<Jwt>> defaultJwtValidatorFactory = OidcIdTokenValidator::new;

	@Before
	public void setUp() {
		this.idTokenDecoderFactory = new OidcIdTokenDecoderFactory();
	}

	@Test
	public void setJwtValidatorFactoryWhenNullThenThrowIllegalArgumentException() {
		assertThatThrownBy(() -> this.idTokenDecoderFactory.setJwtValidatorFactory(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void createDecoderWhenClientRegistrationNullThenThrowIllegalArgumentException() {
		assertThatThrownBy(() -> this.idTokenDecoderFactory.createDecoder(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void createDecoderWhenJwkSetUriEmptyThenThrowOAuth2AuthenticationException() {
		assertThatThrownBy(() -> this.idTokenDecoderFactory.createDecoder(this.registration.jwkSetUri(null).build()))
				.isInstanceOf(OAuth2AuthenticationException.class);
	}

	@Test
	public void createDecoderWhenClientRegistrationValidThenReturnDecoder() {
		assertThat(this.idTokenDecoderFactory.createDecoder(this.registration.build()))
				.isNotNull();
	}

	@Test
	public void createDecoderWhenCustomJwtValidatorFactorySetThenApplied() {
		Function<ClientRegistration, OAuth2TokenValidator<Jwt>> customJwtValidatorFactory = mock(Function.class);
		this.idTokenDecoderFactory.setJwtValidatorFactory(customJwtValidatorFactory);

		when(customJwtValidatorFactory.apply(any(ClientRegistration.class)))
				.thenReturn(this.defaultJwtValidatorFactory.apply(this.registration.build()));

		this.idTokenDecoderFactory.createDecoder(this.registration.build());

		verify(customJwtValidatorFactory).apply(any(ClientRegistration.class));
	}
}
