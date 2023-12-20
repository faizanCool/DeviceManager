package com.devicemanager.DeviceManager.entities;

import com.devicemanager.DeviceManager.entities.enums.CloseEvent;
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
import java.util.Date;

/*
* This table keeps data of
*   1. cancelled bookings
*   2. returned bookings
*
* This only used for audit, and reduce the load of ActiveDeviceBooking
* */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class ClosedDeviceBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column
    @Pattern(regexp = Constants.DeviceCons.SERIAL_NUMBER_PATTERN,
            message = ErrorMessage.ValidationErrorMessage.INVALID_SERIAL_NUMBER)
    @Size(min = 4, max = 4)
    private String serialNumber;

    @NotBlank
    @Pattern(regexp = Constants.UserCons.USER_PATTERN)
    @Size(min = 5, max = 10)
    private String createdBy;

    @NotBlank
    @Pattern(regexp = Constants.UserCons.USER_PATTERN)
    @Size(min = 5, max = 10)
    private String closedBy;

    @NotNull
    private Date expectedStartDate;

    @NotNull
    private Date expectedReturnDate;

    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    private CloseEvent event;

    @Size(max = 200)
    private String description;

    @NotNull
    private Date createdDate;

    @NotNull
    private Date closedDate;
}
