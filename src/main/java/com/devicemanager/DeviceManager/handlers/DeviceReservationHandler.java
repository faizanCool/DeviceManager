package com.devicemanager.DeviceManager.handlers;

import com.devicemanager.DeviceManager.Exception.KnownException;
import com.devicemanager.DeviceManager.entities.ActiveDeviceBooking;
import com.devicemanager.DeviceManager.entities.enums.CloseEvent;
import com.devicemanager.DeviceManager.entities.requests.DeviceBookingRequest;
import com.devicemanager.DeviceManager.entities.requests.DeviceBookingUpdateRequest;
import com.devicemanager.DeviceManager.entities.requests.DeviceClosebookingRequest;
import com.devicemanager.DeviceManager.entities.requests.DeviceBookingGetRequest;
import com.devicemanager.DeviceManager.handlers.validators.RequestValidator;
import com.devicemanager.DeviceManager.service.DeviceBookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Date;


@Component
public class DeviceReservationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceReservationHandler.class);

    @Autowired
    private DeviceBookingService deviceBookingService;

    @Autowired
    private RequestValidator validator;

    public Mono<ServerResponse> bookADevice(final ServerRequest request) {
        return validator.validateRequest(deviceBookingRequestMono -> deviceBookingRequestMono
                        .flatMap(deviceBookingRequest -> this.deviceBookingService.saveDeviceBooking(
                                ActiveDeviceBooking.builder()
                                        .serialNumber(deviceBookingRequest.getSerialNumber())
                                        .userId(deviceBookingRequest.getUserId())
                                        .createdDate(new Date())
                                        .expectedStartDate(deviceBookingRequest.getExpectedStartDate())
                                        .expectedReturnDate(deviceBookingRequest.getExpectedReturnDate())
                                        .description(deviceBookingRequest.getDescription())
                                        .build())
                        .flatMap(deviceBooking -> ServerResponse.ok().bodyValue(deviceBooking)))
                        .doOnError(KnownException.class, e -> LOGGER.warn(e.getMessage()))
                        .doOnError(Exception.class, e -> LOGGER.error(e.getMessage()))
                        .onErrorResume(KnownException.class, e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(e.getMessage()))
                        .onErrorResume(Exception.class, e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage()))
                , request, DeviceBookingRequest.class);
    }

    public Mono<ServerResponse> getBookings(final ServerRequest request) {
        return validator.validateRequest(deviceBookingGetRequestMono ->
                        deviceBookingGetRequestMono.flatMap(
                                deviceBookingGetRequest -> this.deviceBookingService.getBookings(deviceBookingGetRequest)
                                .collectList()
                                .flatMap(bookings -> ServerResponse.ok().bodyValue(bookings))
                        )
                        .doOnError(KnownException.class, e -> LOGGER.warn(e.getMessage()))
                        .doOnError(Exception.class, e -> LOGGER.error(e.getMessage()))
                        .onErrorResume(KnownException.class, e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(e.getMessage()))
                        .onErrorResume(Exception.class, e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage()))
                , request, DeviceBookingGetRequest.class);
    }

    public Mono<ServerResponse> getAllClosedBookings(final ServerRequest request) {
        return deviceBookingService.getAllClosedBookings()
                .collectList()
                .flatMap(closedDeviceBookings -> ServerResponse.ok().bodyValue(closedDeviceBookings))
                .doOnError(Exception.class, e -> LOGGER.error(e.getMessage()))
                .onErrorResume(Exception.class, e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> updateBooking(final ServerRequest request) {
        return validator.validateRequest(deviceBookingUpdateRequestMono ->
                        deviceBookingUpdateRequestMono
                                .flatMap(deviceBookingUpdateRequest -> this.deviceBookingService.updateBooking(deviceBookingUpdateRequest))
                                .flatMap(deviceBooking -> ServerResponse.ok().bodyValue(deviceBooking))
                                .doOnError(KnownException.class, e -> LOGGER.warn(e.getMessage()))
                                .doOnError(Exception.class, e -> LOGGER.error(e.getMessage()))
                                .onErrorResume(KnownException.class, e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(e.getMessage()))
                                .onErrorResume(Exception.class, e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage()))
                , request, DeviceBookingUpdateRequest.class);
    }

    public Mono<ServerResponse> returnDevice(final ServerRequest request) {
        return closeBooking(request, CloseEvent.RETURNED);
    }

    public Mono<ServerResponse> cancelBooking(final ServerRequest request) {
        return closeBooking(request, CloseEvent.CANCELLED);
    }

    private Mono<ServerResponse> closeBooking(final ServerRequest request, CloseEvent event) {
        return validator.validateRequest(deviceReturnRequestMono -> deviceReturnRequestMono
                        .flatMap(deviceCloseBookingRequest -> this.deviceBookingService.closeBooking(deviceCloseBookingRequest, event)
                                .flatMap(closedDeviceBooking -> ServerResponse.ok().bodyValue(closedDeviceBooking)))
                        .doOnError(KnownException.class, e -> LOGGER.warn(e.getMessage()))
                        .doOnError(Exception.class, e -> LOGGER.error(e.getMessage()))
                        .onErrorResume(KnownException.class, e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(e.getMessage()))
                        .onErrorResume(Exception.class, e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage()))
                , request, DeviceClosebookingRequest.class);
    }
}
