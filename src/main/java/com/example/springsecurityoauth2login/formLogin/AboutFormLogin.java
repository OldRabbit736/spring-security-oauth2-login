package com.example.springsecurityoauth2login.formLogin;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public class AboutFormLogin {

    /*
    Spring Security 는 form login 을 기본적으로 활성화 시킨다.
    하지만 만약 어떤 것이든 servlet 기반의 configuration 이 제공되면 form based login 은 명시적으로 제공되어야 한다.
    아래와 같이 제공할 수 있다.
     */

    // Example 1 - Form Log In
    @Bean
    public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception {
        http
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    // Example 2 - Custom Log In Form Configuration
    // loginPage 에 값을 세팅하게 되면, 해당 login page 를 제공해야 한다.
    // login html file, 해당 view 를 리턴할 controller 등이 필요하다.
    @Bean
    public SecurityFilterChain filterChain2(HttpSecurity http) throws Exception {
        return http
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .build();
    }

}
