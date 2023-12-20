package com.devicemanager.DeviceManager.service;

import com.devicemanager.DeviceManager.entities.Device;
import com.devicemanager.DeviceManager.entities.requests.DeviceGetRequest;
import com.devicemanager.DeviceManager.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    /*
    * This will return device by serial number
    * @param String serial number
    * @return Device
    * */
    public Mono<Device> getDeviceBySN(final String serialNumber) {
        return Mono.justOrEmpty(deviceRepository.findById(serialNumber));
    }

    /*
    * This returns all the AVAILABLE devices within given start and end date
    * Optionally - can filter AVAILABLE devices with device statuses (repair/ condemn/ in service)
    *               can filter AVAILABLE devices with device model (APPLE/ SAMSUNG..)
    *
    * @param validated DeviceGetRequest
    * @return available Devices
    * */
    public Flux<Device> getAvailableDevices(final DeviceGetRequest request) {
        Boolean isEmptyModelList = CollectionUtils.isEmpty(request.getModels());
        Boolean isEmptyStatusList = CollectionUtils.isEmpty(request.getStatuses());
        
        return Flux.fromIterable(deviceRepository.getAvailableDevices(request.getStartDate(),
                request.getEndDate(),
                isEmptyModelList ? null :
                        request.getModels()
                                .stream()
                                .map(model -> model.name())
                                .collect(Collectors.toList()),
                isEmptyModelList,
                isEmptyStatusList ? null :
                        request.getStatuses()
                                .stream()
                                .map(status -> status.name())
                                .collect(Collectors.toList()),
                isEmptyStatusList
                ));
    }

}
