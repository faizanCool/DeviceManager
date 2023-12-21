package com.devicemanager.DeviceManager.security;

import com.devicemanager.DeviceManager.util.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {

        http.csrf().disable()
                .authorizeExchange((authorize) -> authorize
                        .pathMatchers("/login", "/logout"
                        ).permitAll()
                        // adding devices and remove devices will be added in future implementation
                        .pathMatchers("/device/add",
                                "/device/remove",
                                "/device/change-status")
                        //.hasRole("ADMIN_ROLE")
                        .permitAll()
                        .pathMatchers("/"+ Constants.RouterCons.ADD_BOOKING,
                                "/" + Constants.RouterCons.UPDATE_BOOKING,
                                "/" + Constants.RouterCons.CANCEL_BOOKING,
                                "/" + Constants.RouterCons.RETURN_DEVICE,
                                "/" + Constants.RouterCons.GET_BOOKINGS,
                                "/" + Constants.RouterCons.GET_DEVICE,
                                "/" + Constants.RouterCons.GET_ALL_CLOSED_BOOKINGS,
                                "/" + Constants.RouterCons.GET_AVAILABLE_DEVICES)
                        // .hasAnyRole("ADMIN_ROLE", "DEVICE_RESERVATION_ROLE")
                        .permitAll()
                        .anyExchange()
                        .denyAll()
                );
        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

}
