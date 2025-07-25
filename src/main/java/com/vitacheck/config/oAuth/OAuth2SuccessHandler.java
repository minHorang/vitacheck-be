package com.vitacheck.config.oAuth;

import com.vitacheck.config.jwt.JwtUtil;
import com.vitacheck.domain.user.User;
import com.vitacheck.dto.OAuthAttributes;
import com.vitacheck.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 1. 어떤 소셜 로그인인지 파악
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String provider = token.getAuthorizedClientRegistrationId();

        // 2. OAuthAttributes 클래스 사용 -> 사용자 정보 통일
        OAuthAttributes attributes = OAuthAttributes.of(provider, provider, oAuth2User.getAttributes());

        // DB에서 사용자 조회
        User user = userRepository.findByEmail(attributes.getEmail()).orElse(null);

        String targetUrl;

        // 사용자가 존재하지 않는 경우
        if (user == null) {
            log.info("신규 사용자입니다. 추가 정보 입력 페이지로 리다이렉션합니다.");
            targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/social-signup")
                    .queryParam("email", attributes.getEmail())
                    .queryParam("fullName", attributes.getName())
                    .queryParam("provider", attributes.getProvider())
                    .queryParam("providerId", attributes.getProviderId())
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
        }
        // 사용자가 이미 존재
        else {
            log.info("기존 사용자입니다. JWT 발급 후 메인 페이지로 이동합니다.");
            String accessToken = jwtUtil.createAccessToken(user.getEmail());
            String refreshToken = jwtUtil.createRefreshToken(user.getEmail());

            targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth-redirect")
                    .queryParam("accessToken", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .build()
                    .toUriString();
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
