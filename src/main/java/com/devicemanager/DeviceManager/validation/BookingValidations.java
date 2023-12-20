package com.devicemanager.DeviceManager.validation;

import com.devicemanager.DeviceManager.Exception.KnownException;
import com.devicemanager.DeviceManager.entities.ActiveDeviceBooking;
import com.devicemanager.DeviceManager.entities.Device;
import com.devicemanager.DeviceManager.entities.enums.Status;
import com.devicemanager.DeviceManager.entities.requests.DeviceBookingUpdateRequest;
import com.devicemanager.DeviceManager.service.DeviceService;
import com.devicemanager.DeviceManager.util.ErrorMessage;
import com.devicemanager.DeviceManager.util.validator.DateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Date;

@Component
public class BookingValidations {

    @Autowired
    private DeviceService deviceService;

    public Mono<Device> isDeviceAvailableAndCanUsable(final String serialNumber) {
        return deviceService.getDeviceBySN(serialNumber)
                .flatMap(device -> {
                    if (!Status.INSRV.equals(device.getStatus()))
                        return Mono.error(new KnownException(
                                ErrorMessage.DataIntegrityErrorMessage.DEVICE_IS_NOT_IN_WORKING_CONDITION));
                    return Mono.just(device);
                })
                .switchIfEmpty(Mono.error(new KnownException(
                        ErrorMessage.DataIntegrityErrorMessage.NOT_EXIST_DEVICE_SERIAL_NUMBER)));
    }

    public Mono<ActiveDeviceBooking> validateDateForBookingUpdate(final ActiveDeviceBooking existBooking
            , final DeviceBookingUpdateRequest updateRequest) {
        return Mono.just(updateRequest)
                .flatMap(deviceBookingUpdateRequest -> {
                    if (updateRequest.getEndDate() == null && updateRequest.getStartDate() == null)
                        return Mono.error(new KnownException(ErrorMessage.ValidationErrorMessage.NO_UPDATES));

                    if (updateRequest.getStartDate() != null) {
                        if (DateValidator.isPassedDate(existBooking.getExpectedStartDate()))
                            return Mono.error(new KnownException(ErrorMessage.ValidationErrorMessage.INVALID_UPDATE_START_DATE));
                        if (DateValidator.isPassedDate(updateRequest.getStartDate()))
                            return Mono.error(new KnownException(ErrorMessage.ValidationErrorMessage.START_DATE_NOT_IN_FUTURE));

                        Date endDate = updateRequest.getEndDate() != null ? updateRequest.getEndDate() : existBooking.getExpectedReturnDate();
                        if (!DateValidator.isStartAfterEnd(updateRequest.getStartDate(), endDate) || updateRequest.getStartDate().equals(endDate))
                            return Mono.error(new KnownException(ErrorMessage.ValidationErrorMessage.INVALID_START_DATE));
                    }

                    Date startDate = updateRequest.getStartDate() != null ? updateRequest.getStartDate() : existBooking.getExpectedStartDate();
                    if (updateRequest.getEndDate() != null &&
                            (!DateValidator.isStartAfterEnd(startDate, updateRequest.getEndDate()) ||
                            updateRequest.getEndDate().equals(startDate)))
                        return Mono.error(new KnownException(ErrorMessage.ValidationErrorMessage.INVALID_END_DATE));
                    return Mono.just(existBooking);
                });
    }
}
