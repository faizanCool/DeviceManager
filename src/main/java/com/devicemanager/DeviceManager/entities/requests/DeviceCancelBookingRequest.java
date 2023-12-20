package com.devicemanager.DeviceManager.entities.requests;

import com.devicemanager.DeviceManager.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceCancelBookingRequest {

    @NotNull
    private Long id;

    @NotBlank
    @Pattern(regexp = Constants.UserCons.USER_PATTERN)
    @Size(min = 5, max = 10)
    private String userId;
}
