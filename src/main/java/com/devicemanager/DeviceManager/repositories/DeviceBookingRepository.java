package com.devicemanager.DeviceManager.repositories;

import com.devicemanager.DeviceManager.entities.ActiveDeviceBooking;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DeviceBookingRepository extends CrudRepository<ActiveDeviceBooking, Long> {

    @Query(value = "SELECT adb.* FROM active_device_booking adb " +
            "WHERE (:serialNumber is NULL OR adb.serial_number = :serialNumber) AND (" +
            "(:startDate is NULL AND :endDate is NULL) " +
            " OR " +
            "((:startDate is NULL OR (:startDate BETWEEN adb.expected_start_date AND adb.expected_return_date)) AND " +
            "(:endDate is NULL OR (:endDate BETWEEN adb.expected_start_date AND adb.expected_return_date)))" +
            " OR " +
            "(:startDate is NOT NULL AND :endDate is NOT NULL AND " +
            "((adb.expected_start_date > :startDate AND adb.expected_return_date < :endDate) OR " +
            "(:startDate BETWEEN adb.expected_start_date AND adb.expected_return_date) OR " +
            "(:endDate BETWEEN adb.expected_start_date AND adb.expected_return_date)))" +
            ")"
            , nativeQuery = true)
    public List<ActiveDeviceBooking> getAvailableDeviceBookings(@Param("serialNumber") String serialNumber,
                                                                 @Param("startDate") Date startDate,
                                                                 @Param("endDate") Date endDate);
}
