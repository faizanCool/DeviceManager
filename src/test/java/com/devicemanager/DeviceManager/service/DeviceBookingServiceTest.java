package com.devicemanager.DeviceManager.service;

import com.devicemanager.DeviceManager.entities.ActiveDeviceBooking;
import com.devicemanager.DeviceManager.entities.ClosedDeviceBooking;
import com.devicemanager.DeviceManager.entities.enums.CloseEvent;
import com.devicemanager.DeviceManager.entities.requests.DeviceBookingGetRequest;
import com.devicemanager.DeviceManager.entities.requests.DeviceBookingUpdateRequest;
import com.devicemanager.DeviceManager.entities.requests.DeviceClosebookingRequest;
import com.devicemanager.DeviceManager.repositories.DeviceBookingRepository;
import com.devicemanager.DeviceManager.util.ErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class DeviceBookingServiceTest {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceBookingService deviceBookingService;

    @Autowired
    DeviceBookingRepository deviceBookingRepository;

    @BeforeEach
    public void beforeEach() {
        deviceBookingRepository.deleteAll();
    }

    Date now = new Date();
    Date startDate = new Date(now.getTime() + 600000);
    Date endDate = new Date(now.getTime() + 900000);



    @Test
    public void saveDeviceBooking_invalidDates_throwsError() {
        // start date > end date
        try {
            deviceBookingService.saveDeviceBooking(
                    ActiveDeviceBooking.builder().serialNumber("1234").userId("user123").expectedStartDate(endDate)
                            .expectedReturnDate(startDate).createdDate(now).build()).block();
        } catch (Exception e){
            Assertions.assertTrue(e.getMessage().contains(ErrorMessage.ValidationErrorMessage.INVALID_DATES));
        }

        // start date is in passed
        try {
            deviceBookingService.saveDeviceBooking(
                    ActiveDeviceBooking.builder().serialNumber("1234").userId("user123").expectedStartDate(new Date(now.getTime() - 60000))
                            .expectedReturnDate(endDate).createdDate(now).build()).block();
        } catch (Exception e){
            Assertions.assertTrue(e.getMessage().contains(ErrorMessage.ValidationErrorMessage.INVALID_DATES));
        }

        try {
            deviceBookingService.saveDeviceBooking(
                    ActiveDeviceBooking.builder().serialNumber("1244").userId("user123").expectedStartDate(startDate)
                            .expectedReturnDate(endDate).createdDate(now).build()).block();
        } catch (Exception e){
            Assertions.assertTrue(e.getMessage().contains(ErrorMessage.DataIntegrityErrorMessage.NOT_EXIST_DEVICE_SERIAL_NUMBER));
        }
    }

    @Test
    public void saveDeviceBooking_invalidDevices_throwsError() {
        // device not exist
        try {
            deviceBookingService.saveDeviceBooking(
                    ActiveDeviceBooking.builder().serialNumber("1244").userId("user123").expectedStartDate(startDate)
                            .expectedReturnDate(endDate).createdDate(now).build()).block();
        } catch (Exception e){
            Assertions.assertTrue(e.getMessage().contains(ErrorMessage.DataIntegrityErrorMessage.NOT_EXIST_DEVICE_SERIAL_NUMBER));
        }

        // device is exist but not in working condition condemn or in repair
        Assertions.assertNotNull(deviceService.getDeviceBySN("1231").block());
        try {
            deviceBookingService.saveDeviceBooking(
                    ActiveDeviceBooking.builder().serialNumber("1231").userId("user123").expectedStartDate(startDate)
                            .expectedReturnDate(endDate).createdDate(now).build()).block();
        } catch (Exception e){
            Assertions.assertTrue(e.getMessage().contains(ErrorMessage.DataIntegrityErrorMessage.DEVICE_IS_NOT_IN_WORKING_CONDITION));
        }
    }

    @Test
    public void saveDeviceBooking_saveBooking_success() {
        ActiveDeviceBooking a_1 = deviceBookingService.saveDeviceBooking(
                ActiveDeviceBooking.builder().serialNumber("1234").userId("user123").expectedStartDate(startDate)
                        .expectedReturnDate(endDate).createdDate(now).build()).block();

        ActiveDeviceBooking a_2 = deviceBookingService.getBookings(DeviceBookingGetRequest.builder()
                .serialNumber("1234").startDate(startDate).endDate(endDate)
                .build()).blockFirst();

        Assertions.assertEquals(a_1.getId(), a_2.getId());
    }

    @Test
    public void saveDeviceBooking_collideBookings_throwsError() {
        deviceBookingService.saveDeviceBooking(
                ActiveDeviceBooking.builder().serialNumber("1234").userId("user123").expectedStartDate(startDate)
                        .expectedReturnDate(endDate).createdDate(now).build()).block();

        // existing start date < given start date < given end date < existing end date
        try {
            deviceBookingService.saveDeviceBooking(
                    ActiveDeviceBooking.builder().serialNumber("1234").userId("user123")
                            .expectedStartDate(new Date(startDate.getTime() + 10000))
                            .expectedReturnDate(new Date(endDate.getTime() - 10000)).createdDate(now).build()).block();
        } catch (Exception e){
            Assertions.assertTrue(e.getMessage().contains(ErrorMessage.ValidationErrorMessage.ALREADY_BOOKED));
        }

        //  given start date < existing start date < existing end date < given end date
        try {
            deviceBookingService.saveDeviceBooking(
                    ActiveDeviceBooking.builder().serialNumber("1234").userId("user123")
                            .expectedStartDate(new Date(startDate.getTime() - 10000))
                            .expectedReturnDate(new Date(endDate.getTime() + 10000))
                            .createdDate(now).build()).block();
        } catch (Exception e){
            Assertions.assertTrue(e.getMessage().contains(ErrorMessage.ValidationErrorMessage.ALREADY_BOOKED));
        }

        //  existing start date < given start date <  existing end date < given end date
        try {
            deviceBookingService.saveDeviceBooking(
                    ActiveDeviceBooking.builder().serialNumber("1234").userId("user123")
                            .expectedStartDate(new Date(startDate.getTime() + 10000))
                            .expectedReturnDate(new Date(endDate.getTime() + 10000))
                            .createdDate(now).build()).block();
        } catch (Exception e){
            Assertions.assertTrue(e.getMessage().contains(ErrorMessage.ValidationErrorMessage.ALREADY_BOOKED));
        }
    }



    /* did not add all scenario tests - updateBooking */
    @Test
    public void updateDeviceBooking_invalidDates_throwError() {
        // this didn't collide with same booking
        ActiveDeviceBooking a_1 = deviceBookingService.saveDeviceBooking(
                ActiveDeviceBooking.builder().serialNumber("1234").userId("user123").expectedStartDate(startDate)
                        .expectedReturnDate(endDate).createdDate(now).build()).block();

        deviceBookingService.saveDeviceBooking(
                ActiveDeviceBooking.builder().serialNumber("1234").userId("user123")
                        .expectedStartDate(new Date(startDate.getTime() + 1000000))
                        .expectedReturnDate(new Date(endDate.getTime() + 1000000))
                        .createdDate(now).build()).block();

        try {
            // a_1 collides with a_2 when a1 updating
            deviceBookingService.updateBooking(DeviceBookingUpdateRequest.builder()
                    .id(a_1.getId())
                    .startDate(new Date(startDate.getTime() + 1200000))
                    .endDate(new Date(endDate.getTime() + 1000000))
                    .build()).block();
        } catch (Exception e){
            Assertions.assertTrue(e.getMessage().contains(ErrorMessage.ValidationErrorMessage.ALREADY_BOOKED));
        }

    }

    @Test
    public void updateDeviceBooking_updateBooking_success() {
        ActiveDeviceBooking a_1 = deviceBookingService.saveDeviceBooking(
                ActiveDeviceBooking.builder().serialNumber("1234").userId("user123").expectedStartDate(startDate)
                        .expectedReturnDate(endDate).createdDate(now).build()).block();
        // start date only
        ActiveDeviceBooking a_2 = deviceBookingService.updateBooking(DeviceBookingUpdateRequest.builder()
                .id(a_1.getId())
                .startDate(new Date(startDate.getTime() - 100000))
                .build()).block();

        Assertions.assertEquals(a_1.getExpectedReturnDate(), a_2.getExpectedReturnDate());
        Assertions.assertEquals(startDate, a_1.getExpectedStartDate());
        Assertions.assertEquals(new Date(startDate.getTime() - 100000), a_2.getExpectedStartDate());

        // start and end dates
        ActiveDeviceBooking a_3 = deviceBookingService.updateBooking(DeviceBookingUpdateRequest.builder()
                .id(a_1.getId())
                .startDate(new Date(startDate.getTime() + 150000))
                .endDate(new Date(endDate.getTime() + 150000))
                .build()).block();

        Assertions.assertEquals(new Date(endDate.getTime() + 150000), a_3.getExpectedReturnDate());
        Assertions.assertEquals(new Date(startDate.getTime() + 150000), a_3.getExpectedStartDate());
    }

    @Test
    public void allTests_bookingService_success() {
        ActiveDeviceBooking a_1 = deviceBookingService.saveDeviceBooking(
                ActiveDeviceBooking.builder().serialNumber("1234").userId("user123").expectedStartDate(startDate)
                        .expectedReturnDate(endDate).createdDate(now).build()).block();

        ActiveDeviceBooking a_2 = deviceBookingService.saveDeviceBooking(
                ActiveDeviceBooking.builder().serialNumber("1230").userId("user123")
                        .expectedStartDate(new Date(startDate.getTime() + 1000000))
                        .expectedReturnDate(new Date(endDate.getTime() + 1000000)).createdDate(now).build()).block();

        ActiveDeviceBooking a_3 = deviceBookingService.saveDeviceBooking(
                ActiveDeviceBooking.builder().serialNumber("1234").userId("user123")
                        .expectedStartDate(new Date(startDate.getTime() + 900000))
                        .expectedReturnDate(new Date(endDate.getTime() + 900000)).createdDate(now).build()).block();
        List<ActiveDeviceBooking> bookings = deviceBookingService.getBookings(DeviceBookingGetRequest.builder()
                .serialNumber("1230")
                .build()).collectList().block();
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(a_2.getId(), bookings.get(0).getId() );

        List<ActiveDeviceBooking> bookings_1 = deviceBookingService.getBookings(DeviceBookingGetRequest.builder()
                .serialNumber("1234")
                .build()).collectList().block();
        Assertions.assertEquals(2, bookings_1.size());
        Assertions.assertTrue(!bookings_1.get(0).getId().equals(a_2.getId()) &&
                (bookings_1.get(0).getId().equals(a_1.getId()) || bookings_1.get(0).getId().equals(a_3.getId())));
        Assertions.assertTrue(!bookings_1.get(1).getId().equals(a_2.getId()) &&
                (bookings_1.get(1).getId().equals(a_1.getId()) || bookings_1.get(1).getId().equals(a_3.getId())));
        Assertions.assertNotEquals(a_1.getId(), a_3.getId());
        Assertions.assertEquals("1234", a_3.getSerialNumber());
        Assertions.assertEquals(a_1.getSerialNumber(), a_3.getSerialNumber());

        // get bookings with range
        List<ActiveDeviceBooking> bookings_2 = deviceBookingService.getBookings(DeviceBookingGetRequest.builder()
                .startDate(startDate).endDate(new Date(endDate.getTime() + 1200000))
                .build()).collectList().block();

        Assertions.assertEquals(3, bookings_2.size());

        List<ActiveDeviceBooking> bookings_3 = deviceBookingService.getBookings(DeviceBookingGetRequest.builder()
                .startDate(new Date(endDate.getTime() + 900000))
                .build()).collectList().block();

        Assertions.assertEquals(2, bookings_3.size());
        Assertions.assertTrue(!bookings_3.get(0).getId().equals(a_1.getId()) &&
                (bookings_3.get(0).getId().equals(a_2.getId()) || bookings_3.get(0).getId().equals(a_3.getId())));
        Assertions.assertTrue(!bookings_3.get(1).getId().equals(a_1.getId()) &&
                (bookings_3.get(1).getId().equals(a_2.getId()) || bookings_3.get(1).getId().equals(a_3.getId())));
        Assertions.assertNotEquals(a_2.getId(), a_3.getId());
        Assertions.assertNotEquals(bookings_3.get(1).getSerialNumber(), bookings_3.get(0).getSerialNumber());

        ClosedDeviceBooking returnedClosedDeviceBooking = deviceBookingService.closeBooking(
                DeviceClosebookingRequest.builder()
                        .id(a_1.getId()).userId("user9321")
                        .build(),
                CloseEvent.RETURNED).block();

        Assertions.assertEquals(CloseEvent.RETURNED, returnedClosedDeviceBooking.getEvent());
        Assertions.assertEquals("user9321", returnedClosedDeviceBooking.getClosedBy());
        Assertions.assertEquals("user123", returnedClosedDeviceBooking.getCreatedBy());

        Assertions.assertEquals(2, deviceBookingService.getBookings(DeviceBookingGetRequest.builder()
                .build()).collectList().block().size());
        Assertions.assertEquals(1, deviceBookingService.getAllClosedBookings().collectList().block().size());

        ClosedDeviceBooking canceledClosedDeviceBooking = deviceBookingService.closeBooking(
                DeviceClosebookingRequest.builder()
                        .id(a_2.getId()).userId("user9321")
                        .build(),
                CloseEvent.CANCELLED).block();

        Assertions.assertEquals(CloseEvent.CANCELLED, canceledClosedDeviceBooking.getEvent());
        Assertions.assertEquals("user9321", canceledClosedDeviceBooking.getClosedBy());
        Assertions.assertEquals("user123", canceledClosedDeviceBooking.getCreatedBy());

        Assertions.assertEquals(1, deviceBookingService.getBookings(DeviceBookingGetRequest.builder()
                .build()).collectList().block().size());
        Assertions.assertEquals(2, deviceBookingService.getAllClosedBookings().collectList().block().size());

    }
}
