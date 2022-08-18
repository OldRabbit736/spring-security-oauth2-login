package com.example.springsecurityoauth2login.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.Optional;
import java.util.function.Supplier;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;


public class AboutAuthorizationFilter {

    /*
    https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
    https://docs.spring.io/spring-security/site/docs/5.7.3/api/org/springframework/security/web/access/intercept/AuthorizationFilter.html
    AuthorizationFilter
    - SecurityFilterChain 을 구성하는 요소 필터가 AuthorizationFilter 인 것 같다.
    - AuthorizationFilter 는 AuthorizationManager 인스턴스를 가지고 있다.
    - An authorization filter that restricts access to the URL using AuthorizationManager.
    - URL 패턴에 따른 AuthorizationManager 배정 리스트를 모은 RequestMatcherDelegatingAuthorizationManager 를 가지고
      Authorization 을 수행하는 것 같다.
    - 즉, SecurityFilterChain 내부의 url 패턴과 그에 따른 AuthorizationManager 배정을 모두 모아
      RequestMatcherDelegatingAuthorizationManager 를 만드는 것이 아닐까..?

    https://docs.spring.io/spring-security/reference/servlet/architecture.html#servlet-securityfilterchain
    SecurityFilterChain
    - HttpServletRequest 에 대한 filter chain 을 제공한다.
    - 즉 특정 HttpServletRequest 에 대하여 authorization 을 수행하는 filter 를 제공하는 것이다.
    - FilterChainProxy 에 의해 호출되는 것 같다.

    SecurityFilterChain 의 HttpServletRequest 인가 (아래 링크 figure 1)
    - https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
    - SecurityContextHolder 로부터 Authentication 을 얻고, RequestMatcherDelegatingAuthorizationManager 를 이용해
      HttpServletRequest 를 Authorization 한다.
    - 인증 결과에 따라 AccessDeniedException 을 발생시키거나 다음 filter 를 정상적으로 호출한다.
     */


    // Example 1 - Use authorizeHttpRequest
    @Bean
    SecurityFilterChain web1(HttpSecurity http) throws Exception {
        http
                // use "authorizeHttpRequests" instead of "authorizeRequests"
                .authorizeHttpRequests(
                        // 특정 url 패턴에 특정 AuthorizationManager 배정 --> RequestMatcherDelegatingAuthorizationManager 를 생성

                        // 여기서는 모든 요청에 AuthenticatedAuthorizationManager 를 배정한다.
                        registry -> registry.anyRequest().authenticated()
                );

        // ...

        return http.build();
    }

    // Example 2 - Authorize Requests
    @Bean
    SecurityFilterChain web2(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        // 결국엔 RequestMatcherDelegatingAuthorizationManager 를 만드는 과정
                        registry -> registry
                                .mvcMatchers("/resources/**", "/signup", "/about").permitAll()
                                .mvcMatchers("/admin/**").hasRole("ADMIN")
                                .mvcMatchers("/db/**").access((authentication, request) ->
                                        Optional.of(hasRole("ADMIN").check(authentication, request))
                                                .filter(decision -> !decision.isGranted())
                                                .orElseGet(() -> hasRole("DBA").check(authentication, request))
                                ).anyRequest().denyAll()
                );

        return http.build();
    }


    // Example 3 - Configure RequestMatcherDelegatingAuthorizationManager


    // Example 4 - Custom Authorization Manager
    @Bean
    SecurityFilterChain web4(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(registry -> registry
                        .mvcMatchers("/my/authorized/endpoint").access(new CustomAuthorizationManager())
                );

        return http.build();
    }

    public static class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
        @Override
        public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
            // custom logic...
            return null;
        }
    }

    // Example 5 - Custom Authorization Manager for All Requests
    @Bean
    SecurityFilterChain web5(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(registry -> registry
                        .anyRequest().access(new CustomAuthorizationManager())
                );

        return http.build();
    }

    // Example 6 - Set shouldFilterAllDispatcherTypes to true
    // 기본적으로 AuthorizationFilter 는 DispatcherType.ERROR, DispatcherType.ASYNC 에 적용되지 않는다.
    // 이것을 적용되도록 설정할 수 있다.
    @Bean
    SecurityFilterChain web6(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(registry -> registry
                        .shouldFilterAllDispatcherTypes(true)
                        .anyRequest().authenticated()
                        // ...
                );

        return http.build();
    }

}
