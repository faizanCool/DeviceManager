package com.devicemanager.DeviceManager.entities.requests;

import com.devicemanager.DeviceManager.util.Constants;
import com.devicemanager.DeviceManager.util.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceBookingGetRequest {
    @Pattern(regexp = Constants.DeviceCons.SERIAL_NUMBER_PATTERN_NULLABLE,
            message = ErrorMessage.ValidationErrorMessage.INVALID_SERIAL_NUMBER)
    @Size(min = 4, max = 4)
    private String serialNumber;

    private Date startDate;

    private Date endDate;
}
