package com.example.config;

import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/** H2 コンソール用のセキュリティ設定 */
	@Bean
	@Order(1)
    SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.securityMatcher(PathRequest.toH2Console())
			.authorizeHttpRequests(authorize -> authorize
				.anyRequest().permitAll()
			)
			.headers(headers -> headers
				.frameOptions(FrameOptionsConfig::disable)
			)
			.csrf(csrf -> csrf
				.ignoringRequestMatchers(PathRequest.toH2Console())
			);
		return http.build();
	}

	/** このアプリのセキュリティ設定 */
	@Bean
	@Order(2)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.requestMatchers("/login").permitAll()
				.requestMatchers("/user/signup").permitAll()
				.requestMatchers("/admin").hasAuthority("ROLE_ADMIN")
				.anyRequest().authenticated()
			)
			.formLogin(login -> login
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.usernameParameter("userId")
				.passwordParameter("password")
				.defaultSuccessUrl("/user/list", true)
				.failureUrl("/login?error")
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
//			)
//			// CSRF 対策を無効に設定 (一時的)
//			.csrf(csrf -> csrf
//		        .disable()
			);
		return http.build();
	}
	
	/*
	@Bean
	InMemoryUserDetailsManager userDetailsService() {
        PasswordEncoder encoder = passwordEncoder();

		UserDetails user = User.withUsername("user")
            .password(encoder.encode("user"))
			.roles("GENERAL")
			.build();
		UserDetails admin = User.withUsername("admin")
            .password(encoder.encode("admin"))
			.roles("ADMIN")
			.build();
		return new InMemoryUserDetailsManager(user, admin);
	}
	*/
}
