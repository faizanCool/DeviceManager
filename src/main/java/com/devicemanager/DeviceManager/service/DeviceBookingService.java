package com.devicemanager.DeviceManager.service;

import com.devicemanager.DeviceManager.Exception.KnownException;
import com.devicemanager.DeviceManager.entities.ActiveDeviceBooking;
import com.devicemanager.DeviceManager.entities.ClosedDeviceBooking;
import com.devicemanager.DeviceManager.entities.enums.CloseEvent;
import com.devicemanager.DeviceManager.entities.requests.DeviceBookingGetRequest;
import com.devicemanager.DeviceManager.entities.requests.DeviceBookingUpdateRequest;
import com.devicemanager.DeviceManager.entities.requests.DeviceClosebookingRequest;
import com.devicemanager.DeviceManager.repositories.ClosedDeviceBookingRepository;
import com.devicemanager.DeviceManager.repositories.DeviceBookingRepository;
import com.devicemanager.DeviceManager.util.ErrorMessage;
import com.devicemanager.DeviceManager.util.validator.DateValidator;
import com.devicemanager.DeviceManager.validation.BookingValidations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class DeviceBookingService {
    @Autowired
    private DeviceBookingRepository deviceBookingRepository;

    @Autowired
    private ClosedDeviceBookingRepository closedDeviceBookingRepository;

    @Autowired
    private BookingValidations bookingValidations;

    /*
    * This will save the ActiveDeviceBooking
    *   ignores - invalid dates, non existing device, if have existing booking on given dates
    * @param : validated ActiveDeviceBooking
    * @return: saved ActiveDeviceBooking
    * */
    public Mono<ActiveDeviceBooking> saveDeviceBooking(final ActiveDeviceBooking deviceBooking) {
        return Mono.just(DateValidator.isStartAfterEnd(deviceBooking.getExpectedStartDate(), deviceBooking.getExpectedReturnDate())
                && DateValidator.isFutureDate(deviceBooking.getExpectedStartDate()))
                .flatMap(isValid -> {
                    if ( !isValid)
                        return Mono.error(new KnownException(ErrorMessage.ValidationErrorMessage.INVALID_DATES));
                    return bookingValidations.isDeviceAvailableAndCanUsable(deviceBooking.getSerialNumber())
                            .flatMapMany(device -> Flux.fromIterable(deviceBookingRepository.getAvailableDeviceBookings(
                                    deviceBooking.getSerialNumber(),
                                    deviceBooking.getExpectedStartDate(),
                                    deviceBooking.getExpectedReturnDate()))
                            )
                            .collectList()
                            .flatMap(activeDeviceBookings ->
                                    activeDeviceBookings.isEmpty() ?
                                            Mono.just(deviceBookingRepository.save(deviceBooking)) :
                                            Mono.error(new KnownException(ErrorMessage.ValidationErrorMessage.ALREADY_BOOKED))
                            );
                });
    }

    /*
    * This
    *   1. can fetch all the bookings
    *   2. can fetch by device serial number
    *   3. can fetch by booking start date - this fetch bookings effected to given dates
    *   4. can fetch by booking end date - this fetch bookings effected to given dates
    *
    *   method supports all the combinations of above 2,3,4
    *   ex :    can fetch given serial number (2) with start date (3) and end date (4)
    *           can fetch given start date (3) and end date (4)
    *
    * @param validated DeviceBookingGetRequest
    * @return existing ActiveDeviceBookings
    * */
    public Flux<ActiveDeviceBooking> getBookings(final DeviceBookingGetRequest deviceBookingGetRequest) {
        return Flux.fromIterable(deviceBookingRepository.getAvailableDeviceBookings(
                deviceBookingGetRequest.getSerialNumber(),
                deviceBookingGetRequest.getStartDate(),
                deviceBookingGetRequest.getEndDate()));
    }

    /*
    * this will give all the completed (returned or cancelled) bookings
    * */
    public Flux<ClosedDeviceBooking> getAllClosedBookings() {
        return Flux.fromIterable(closedDeviceBookingRepository.findAll());
    }

    /*
    * This updates the existing ActiveDeviceBooking
    *   ignores -   non existing device, invalid dates, if have any other available bookings in given dates,
    *               if there are no any update values
    * @param validated DeviceBookingUpdateRequest
    * @return updated ActiveDeviceBooking
    * */
    public Mono<ActiveDeviceBooking> updateBooking(final DeviceBookingUpdateRequest deviceBookingUpdateRequest) {
        return Mono.justOrEmpty(deviceBookingRepository.findById(deviceBookingUpdateRequest.getId()))
                .flatMap(deviceBooking -> bookingValidations.isDeviceAvailableAndCanUsable(deviceBooking.getSerialNumber())
                        .flatMap(device -> bookingValidations.validateDateForBookingUpdate(deviceBooking, deviceBookingUpdateRequest))
                        .flatMap(validatedDeviceBooking -> {
                            if (deviceBookingUpdateRequest.getStartDate() != null)
                                validatedDeviceBooking.setExpectedStartDate(deviceBookingUpdateRequest.getStartDate());
                            if (deviceBookingUpdateRequest.getEndDate() != null)
                                validatedDeviceBooking.setExpectedReturnDate(deviceBookingUpdateRequest.getEndDate());
                            return Flux.fromIterable(deviceBookingRepository.getAvailableDeviceBookings(
                                    validatedDeviceBooking.getSerialNumber(),
                                    validatedDeviceBooking.getExpectedStartDate(),
                                    validatedDeviceBooking.getExpectedReturnDate()))
                                    .filter(activeDeviceBooking -> activeDeviceBooking.getId() != deviceBookingUpdateRequest.getId())
                                    .collectList()
                                    .flatMap(activeDeviceBookings ->
                                            activeDeviceBookings.isEmpty() ?
                                                    Mono.just(deviceBookingRepository.save(deviceBooking)) :
                                                    Mono.error(new KnownException(ErrorMessage.ValidationErrorMessage.ALREADY_BOOKED))
                                    );
                        }))
                .switchIfEmpty(Mono.error(new KnownException(ErrorMessage.ValidationErrorMessage.INVALID_BOOKING_DETAILS)));
    }

    /*
    * This method executes when
    *   cancelling a booking
    *   return a device
    * So this changes ActiveDeviceBooking to ClosedDeviceBooking
    * @param DeviceClosebookingRequest
    * @param event
    * @return ClosedDeviceBooking
    * */
    public Mono<ClosedDeviceBooking> closeBooking(final DeviceClosebookingRequest deviceClosebookingRequest, final CloseEvent event) {
        return Mono.justOrEmpty(deviceBookingRepository.findById(deviceClosebookingRequest.getId()))
                .flatMap(activeDeviceBooking -> {
                    if (CloseEvent.RETURNED.equals(event) &&
                            DateValidator.isFutureDate(activeDeviceBooking.getExpectedStartDate())) {
                        return Mono.error(new KnownException(ErrorMessage.ValidationErrorMessage.INVALID_RETURN_DEVICE));
                    }
                    deleteActiveBookingById(activeDeviceBooking);
                    return Mono.just(closedDeviceBookingRepository.save(
                            ClosedDeviceBooking.builder()
                                    .serialNumber(activeDeviceBooking.getSerialNumber())
                                    .createdDate(activeDeviceBooking.getCreatedDate())
                                    .description(activeDeviceBooking.getDescription())
                                    .closedBy(deviceClosebookingRequest.getUserId())
                                    .createdBy(activeDeviceBooking.getUserId())
                                    .closedDate(new Date())
                                    .expectedStartDate(activeDeviceBooking.getExpectedStartDate())
                                    .expectedReturnDate(activeDeviceBooking.getExpectedReturnDate())
                                    .event(event)
                                    .build())
                    );
                })
                .switchIfEmpty(Mono.error(new KnownException(ErrorMessage.ValidationErrorMessage.INVALID_BOOKING_DETAILS)));
    }

    private Mono<Long> deleteActiveBookingById(final ActiveDeviceBooking activeDeviceBooking) {
        try {
            deviceBookingRepository.deleteById(activeDeviceBooking.getId());
        } catch (Exception e) {
            return Mono.error(e);
        }
        return Mono.just(activeDeviceBooking.getId());
    }
}
