package com.devicemanager.DeviceManager.repositories;


import com.devicemanager.DeviceManager.entities.ClosedDeviceBooking;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClosedDeviceBookingRepository extends CrudRepository<ClosedDeviceBooking, String> {
}
