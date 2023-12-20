package com.devicemanager.DeviceManager.repositories;

import com.devicemanager.DeviceManager.entities.ActiveDeviceBooking;
import com.devicemanager.DeviceManager.entities.Device;
import com.devicemanager.DeviceManager.entities.enums.Model;
import com.devicemanager.DeviceManager.entities.enums.Status;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DeviceRepository extends CrudRepository<Device, String> {
    @Query(value = "SELECT d.* FROM Device d WHERE d.serial_number NOT IN (" +
            "SELECT adb.serial_number FROM active_device_booking adb WHERE " +
            "(:startDate BETWEEN adb.expected_start_date AND adb.expected_return_date) OR " +
            "(:endDate BETWEEN adb.expected_start_date AND adb.expected_return_date) OR " +
            "(adb.expected_start_date > :startDate AND adb.expected_return_date < :endDate)) AND " +
            "(:isEmptyModel = TRUE  OR d.model IN (:models)) AND" +
            "(:isEmptyStatus = TRUE  OR d.status IN (:statuses))"
            , nativeQuery = true)
    public List<Device> getAvailableDevices(@Param("startDate") Date startDate,
                                            @Param("endDate") Date endDate,
                                            @Param("models") List<String> models,
                                            @Param("isEmptyModel") Boolean isEmptyModelList,
                                            @Param("statuses") List<String> statuses,
                                            @Param("isEmptyStatus") Boolean isEmptyStatusList);
}
