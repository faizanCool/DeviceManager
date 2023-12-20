package com.devicemanager.DeviceManager.handlers.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.function.Function;

@Component
public class RequestValidator {

    private final Validator validator;

    @Autowired
    public RequestValidator(Validator validator) {
        this.validator = validator;
    }

    public <BODY> Mono<ServerResponse> validateRequest(
            Function<Mono<BODY>, Mono<ServerResponse>> block,
            ServerRequest request, Class<BODY> bodyClass) {

        return request
                .bodyToMono(bodyClass)
                .flatMap(
                        body -> {
                            Set<ConstraintViolation<BODY>> errors = validator.validate(body);
                            return errors.isEmpty()
                                    ? block.apply(Mono.just(body))
                                    : ServerResponse.badRequest().bodyValue("Validation failed for given fields or Not provided expected values");
                        }
                );
    }
}
