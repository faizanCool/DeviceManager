package com.devicemanager.DeviceManager.handlers;

import com.devicemanager.DeviceManager.Exception.KnownException;
import com.devicemanager.DeviceManager.entities.requests.DeviceGetRequest;
import com.devicemanager.DeviceManager.handlers.validators.RequestValidator;
import com.devicemanager.DeviceManager.service.DeviceService;
import com.devicemanager.DeviceManager.util.Constants;
import com.devicemanager.DeviceManager.util.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;


@Component
public class DeviceHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceHandler.class);

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private RequestValidator validator;

    public Mono<ServerResponse> getDeviceBySN(final ServerRequest request) {
        final String serialNumber = request.pathVariable(Constants.RouterCons.SERIAL_NUMBER_PATH_VAR);
        return (serialNumber != null && Pattern.matches(Constants.DeviceCons.SERIAL_NUMBER_PATTERN_WITH_SIZE, serialNumber)) ?
                deviceService.getDeviceBySN(serialNumber)
                        .flatMap(device -> ServerResponse.ok().bodyValue(device))
                        .doOnError(Exception.class, e -> LOGGER.error(e.getMessage()))
                        .onErrorResume(Exception.class, e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage())) :
                ServerResponse.badRequest().bodyValue(ErrorMessage.ValidationErrorMessage.INVALID_SERIAL_NUMBER);
    }

    public Mono<ServerResponse> getAvailableDevices(final ServerRequest request) {
        return validator.validateRequest(deviceGetRequestMono ->
                        deviceGetRequestMono
                                .flatMapMany(deviceGetRequest -> this.deviceService.getAvailableDevices(deviceGetRequest))
                                .collectList()
                                .flatMap(devices -> ServerResponse.ok().bodyValue(devices))
                                .doOnError(KnownException.class, e -> LOGGER.warn(e.getMessage()))
                                .doOnError(Exception.class, e -> LOGGER.error(e.getMessage()))
                                .onErrorResume(KnownException.class, e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(e.getMessage()))
                                .onErrorResume(Exception.class, e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage()))
                , request, DeviceGetRequest.class);
    }



}
