package com.devicemanager.DeviceManager.entities.requests;

import com.devicemanager.DeviceManager.entities.enums.Model;
import com.devicemanager.DeviceManager.entities.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceGetRequest {
    @NotNull
    private Date startDate;

    @NotNull
    private Date endDate;

    private List<Status> statuses;

    private List<Model> models;
}
