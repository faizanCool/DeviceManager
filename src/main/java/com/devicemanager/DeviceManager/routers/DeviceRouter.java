package com.devicemanager.DeviceManager.routers;

import com.devicemanager.DeviceManager.handlers.DeviceHandler;
import com.devicemanager.DeviceManager.util.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class DeviceRouter {

    @Bean
    public RouterFunction<ServerResponse> deviceRoutes(final DeviceHandler deviceHandler) {
        return RouterFunctions.
                route(GET(Constants.RouterCons.GET_DEVICE).and(accept(APPLICATION_JSON)), deviceHandler::getDeviceBySN)
                .andRoute(GET(Constants.RouterCons.GET_AVAILABLE_DEVICES).and(accept(APPLICATION_JSON)), deviceHandler::getAvailableDevices);
    }
}
