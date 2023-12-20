package com.devicemanager.DeviceManager.entities.requests;

import com.devicemanager.DeviceManager.util.Constants;
import com.devicemanager.DeviceManager.util.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceBookingRequest {
    @NotBlank
    @Pattern(regexp = Constants.DeviceCons.SERIAL_NUMBER_PATTERN,
            message = ErrorMessage.ValidationErrorMessage.INVALID_SERIAL_NUMBER)
    @Size(min = 4, max = 4)
    private String serialNumber;

    @NotBlank
    @Pattern(regexp = Constants.UserCons.USER_PATTERN)
    @Size(min = 5, max = 10)
    private String userId;

    @NotNull
    private Date expectedStartDate;

    @NotNull
    private Date expectedReturnDate;

    @Size(max = 200)
    private String description;

}
