package com.devicemanager.DeviceManager.entities;

import com.devicemanager.DeviceManager.entities.enums.Model;
import com.devicemanager.DeviceManager.entities.enums.Status;
import com.devicemanager.DeviceManager.util.Constants;
import com.devicemanager.DeviceManager.util.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Device {

    @Id
    @NotBlank
    @Column
    @Pattern(regexp = Constants.DeviceCons.SERIAL_NUMBER_PATTERN,
            message = ErrorMessage.ValidationErrorMessage.INVALID_SERIAL_NUMBER)
    @Size(min = 4, max = 4)
    private String serialNumber;

    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    private Model model;

    @NotBlank
    @Column
    @Pattern(regexp = Constants.DeviceCons.VERSION_PATTERN,
            message = ErrorMessage.ValidationErrorMessage.VERSION_PATTERN)
    @Size(min = 1, max = 20)
    private String version;

    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column
    @Size(max = 200 )
    private String description;
}
