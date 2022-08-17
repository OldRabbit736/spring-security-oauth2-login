package com.example.springsecurityoauth2login.authentication;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;


// https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html#servlet-authentication-securitycontextholder

public class AboutSecurityContextHolder {

    public void securityContextHolder() {

        /*
        empty SecurityContext 생성
        SecurityContextHolder.getContext().setAuthentication(authentication) 의 방법은 피하는 게 좋다.
        여러개의 스레드에서 race condition 이 발생할 수 있다.
         */
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        /*
        Authentication 인터페이스를 구현한 구현체는 몇 가지가 있다.
        가장 단순한 TestingAuthenticationToken 이 있다.
        다른 예로는 흔하게 사용되는 UsernamePasswordAuthenticationToken(userDetails, password, authorities) 가 있다.
         */
        Authentication authentication
                = new TestingAuthenticationToken("username", "password", "ROLE_USER");

        securityContext.setAuthentication(authentication);

        /*
        SecurityContextHolder 에 SecurityContext 를 세팅한다.
        이제 Spring Security 는 이 정보를 이용하여 authorization 을 수행할 것이다.
         */
        SecurityContextHolder.setContext(securityContext);

    }

    public void accessingCurrentlyAuthenticatedUser() {

        /*
        SecurityContextHolder 를 통해 현재 authenticated 된 principal 에 대한 정보를 얻을 수 있다.
         */

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();
        Object principal = authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

    }

    /*
    By default the SecurityContextHolder uses a ThreadLocal to store these details,
    which means that the SecurityContext is always available to methods in the same thread,
    even if the SecurityContext is not explicitly passed around as an argument to those methods.
    Using a ThreadLocal in this way is quite safe if care is taken to clear the thread
    after the present principal’s request is processed.
    Spring Security’s FilterChainProxy ensures that the SecurityContext is always cleared.

    Some applications aren’t entirely suitable for using a ThreadLocal,
    because of the specific way they work with threads.
    For example, a Swing client might want all threads in a Java Virtual Machine to use the same security context.
    SecurityContextHolder can be configured with a strategy on startup to specify
    how you would like the context to be stored.
    For a standalone application you would use the SecurityContextHolder.MODE_GLOBAL strategy.
    Other applications might want to have threads spawned by the secure thread also assume the same security identity.
    This is achieved by using SecurityContextHolder.MODE_INHERITABLETHREADLOCAL.
    You can change the mode from the default SecurityContextHolder.MODE_THREADLOCAL in two ways.
    The first is to set a system property, the second is to call a static method on SecurityContextHolder.
    Most applications won’t need to change from the default, but if you do,
    take a look at the Javadoc for SecurityContextHolder to learn more.
     */
}
