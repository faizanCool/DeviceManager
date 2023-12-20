package com.devicemanager.DeviceManager.routers;

import com.devicemanager.DeviceManager.handlers.DeviceReservationHandler;
import com.devicemanager.DeviceManager.util.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class DeviceReservationRouter {
    @Bean
    public RouterFunction<ServerResponse> deviceManagerRoutes(final DeviceReservationHandler deviceReservationHandler) {
        return RouterFunctions.
                route(POST(Constants.RouterCons.ADD_BOOKING).and(accept(APPLICATION_JSON)), deviceReservationHandler::bookADevice)
                .andRoute(POST(Constants.RouterCons.RETURN_DEVICE).and(accept(APPLICATION_JSON)), deviceReservationHandler::returnDevice)
                .andRoute(POST(Constants.RouterCons.CANCEL_BOOKING).and(accept(APPLICATION_JSON)), deviceReservationHandler::cancelBooking)
                .andRoute(GET(Constants.RouterCons.GET_BOOKINGS).and(accept(APPLICATION_JSON)), deviceReservationHandler::getBookings)
                .andRoute(GET(Constants.RouterCons.GET_ALL_CLOSED_BOOKINGS).and(accept(APPLICATION_JSON)), deviceReservationHandler::getAllClosedBookings)
                .andRoute(PUT(Constants.RouterCons.UPDATE_BOOKING).and(accept(APPLICATION_JSON)), deviceReservationHandler::updateBooking);
    }
}
