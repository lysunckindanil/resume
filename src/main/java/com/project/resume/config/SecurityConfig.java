package com.project.resume.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityFilterChain customSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .requestMatchers(
                        "/repository", "/repository/add", "/repository/add-static",
                        "/repository/*/delete", "/repository/*/delete-static",
                        "/projects/*/edit", "/projects/*/delete", "/projects/add")
                .authenticated()
                .anyRequest()
                .permitAll()
                .and()
                .formLogin();

        return http.build();
    }

    @Bean
    public JdbcUserDetailsManager users(DataSource dataSource) {
        UserDetails admin = User.builder()
                .username("admin")
                .password("{bcrypt}$2y$10$.bOnDBxF/BW5vub44x.zO.IQTzFarMiSrzWjoljmrSSfm3078xXZO")
                .roles("ADMIN")
                .build();
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);

        if (users.userExists(admin.getUsername())) users.deleteUser(admin.getUsername());

        users.createUser(admin);
        return users;
    }

}
