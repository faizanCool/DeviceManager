package com.devicemanager.DeviceManager.entities.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceBookingUpdateRequest {

    @NotNull
    private Long id;

    private Date startDate;

    private Date endDate;
}
