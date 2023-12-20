package com.devicemanager.DeviceManager.service;

import com.devicemanager.DeviceManager.entities.ActiveDeviceBooking;
import com.devicemanager.DeviceManager.entities.enums.Model;
import com.devicemanager.DeviceManager.entities.enums.Status;
import com.devicemanager.DeviceManager.entities.requests.DeviceGetRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@SpringBootTest
public class DeviceServiceTest {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceBookingService deviceBookingService;

    @Test
    void getDeviceBySerialNumber_noneExistingDeviceAndExistingDevice_success() {
        Assertions.assertNull(deviceService.getDeviceBySN("1334").block());
        Assertions.assertNotNull(deviceService.getDeviceBySN("1234").block());
    }

    @Test
    void getAvailableDevices_getDevices_success() {
        // get All devices
        Assertions.assertEquals(10, deviceService.getAvailableDevices(DeviceGetRequest.builder().build())
                .collectList()
                .block().size()
        );
        
        // get by model
        Assertions.assertEquals(4, deviceService.getAvailableDevices(DeviceGetRequest.builder()
                .models(new ArrayList<>(Arrays.asList(Model.APPLE)))
                .build())
                .collectList()
                .block().size()
        );

        // get by multiple models
        Assertions.assertEquals(5, deviceService.getAvailableDevices(DeviceGetRequest.builder()
                .models(new ArrayList<>(Arrays.asList(Model.APPLE, Model.NOKIA)))
                .build())
                .collectList()
                .block().size()
        );

        // get by status
        Assertions.assertEquals(1, deviceService.getAvailableDevices(DeviceGetRequest.builder()
                .statuses(new ArrayList<>(Arrays.asList(Status.INRPR)))
                .build())
                .collectList()
                .block().size()
        );

        // get by multiple statuses
        Assertions.assertEquals(9, deviceService.getAvailableDevices(DeviceGetRequest.builder()
                .statuses(new ArrayList<>(Arrays.asList(Status.INRPR, Status.INSRV)))
                .build())
                .collectList()
                .block().size()
        );

        // get by status and model
        Assertions.assertEquals(4, deviceService.getAvailableDevices(DeviceGetRequest.builder()
                .statuses(new ArrayList<>(Arrays.asList(Status.INRPR, Status.INSRV)))
                .models(new ArrayList<>(Arrays.asList(Model.APPLE, Model.MOTOROLA)))
                .build())
                .collectList()
                .block().size()
        );

    }

    @Test
    void getAvailableDevices_getAvailableDevicesByDates_success() {

        Date now = new Date();
        Date startDate = new Date(now.getTime() + 600000);
        Date endDate = new Date(now.getTime() + 900000);

        // booked an APPLE iPhone
        deviceBookingService.saveDeviceBooking(ActiveDeviceBooking.builder()
                .serialNumber("1230")
                .createdDate(now)
                .userId("Tester")
                .expectedStartDate(startDate)
                .expectedReturnDate(endDate)
                .build()).block();

        // only by start date
        Assertions.assertEquals(9, deviceService.getAvailableDevices(DeviceGetRequest.builder()
                .startDate(new Date(now.getTime() + 700000))
                .build())
                .collectList()
                .block().size()
        );

        // start date, model and status
        // total Apple phones -> 4 (1 condemn and 3 insrv phones)
        // one iphone booked in given time so there are only 2 available
        Assertions.assertEquals(2, deviceService.getAvailableDevices(DeviceGetRequest.builder()
                .startDate(new Date(now.getTime() + 700000))
                .statuses(new ArrayList<>(Arrays.asList(Status.INSRV)))
                .models(new ArrayList<>(Arrays.asList(Model.APPLE)))
                .build())
                .collectList()
                .block().size()
        );

        // only by start date but start date > existing booking end date
        // so booked device also available to start to given date
        Assertions.assertEquals(10, deviceService.getAvailableDevices(DeviceGetRequest.builder()
                .startDate(new Date(endDate.getTime() + 10000))
                .build())
                .collectList()
                .block().size()
        );

        // only by end date
        Assertions.assertEquals(9, deviceService.getAvailableDevices(DeviceGetRequest.builder()
                .endDate(new Date(endDate.getTime() - 10000))
                .build())
                .collectList()
                .block().size()
        );

        // end date, model and status
        // total Apple phones -> 4 (1 condemn and 3 insrv phones)
        // one iphone booked in given time so there are only 2 available
        Assertions.assertEquals(2, deviceService.getAvailableDevices(DeviceGetRequest.builder()
                .endDate(new Date(endDate.getTime() - 10000))
                .statuses(new ArrayList<>(Arrays.asList(Status.INSRV)))
                .models(new ArrayList<>(Arrays.asList(Model.APPLE)))
                .build())
                .collectList()
                .block().size()
        );

        // start date < existing booking start date and end date > existing bookking end date, model and status
        // total Apple phones -> 4 (1 condemn and 3 insrv phones)
        // one iphone booked in given time so there are only 2 available
        Assertions.assertEquals(2, deviceService.getAvailableDevices(DeviceGetRequest.builder()
                .startDate(new Date(startDate.getTime() - 10000))
                .endDate(new Date(endDate.getTime()  + 10000))
                .statuses(new ArrayList<>(Arrays.asList(Status.INSRV)))
                .models(new ArrayList<>(Arrays.asList(Model.APPLE)))
                .build())
                .collectList()
                .block().size()
        );

        // existing booking start date < start date < end date < existing bookking end date, model and status
        // total Apple phones -> 4 (1 condemn and 3 insrv phones)
        // one iphone booked in given time so there are only 2 available
        Assertions.assertEquals(2, deviceService.getAvailableDevices(DeviceGetRequest.builder()
                .startDate(new Date(startDate.getTime() + 10000))
                .endDate(new Date(endDate.getTime()  - 10000))
                .statuses(new ArrayList<>(Arrays.asList(Status.INSRV)))
                .models(new ArrayList<>(Arrays.asList(Model.APPLE)))
                .build())
                .collectList()
                .block().size()
        );

        Assertions.assertEquals(2, deviceService.getAvailableDevices(DeviceGetRequest.builder()
                .startDate(new Date(startDate.getTime() + 10000))
                .endDate(new Date(endDate.getTime()  - 10000))
                .statuses(new ArrayList<>(Arrays.asList(Status.INSRV)))
                .models(new ArrayList<>(Arrays.asList(Model.APPLE)))
                .build())
                .collectList()
                .block().size()
        );
    }
}

