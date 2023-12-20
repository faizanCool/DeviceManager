package com.devicemanager.DeviceManager.util;

public class Constants {

    public static class DeviceCons {
        public static final String SERIAL_NUMBER_PATTERN = "^[0-9A-Fa-f]+$";
        public static final String SERIAL_NUMBER_PATTERN_WITH_SIZE = "^[0-9A-Fa-f]{4}$";
        public static final String SERIAL_NUMBER_PATTERN_NULLABLE = "^[0-9A-Fa-f]*$|";
        public static final String VERSION_PATTERN = "^[0-9A-Za-z ]+$";

    }

    public static class UserCons {
        public static final String USER_PATTERN = "^[0-9A-Za-z]+$";
    }

    public static class RouterCons {
        public static final String SERIAL_NUMBER_PATH_VAR = "serialNumber";
        public static final String SERIAL_NUMBER_PARAM = "{" + SERIAL_NUMBER_PATH_VAR + "}";

        // main urls
        public static final String DEVICE_RESERVATION = "device-reservation";
        public static final String DEVICE = "device";

        // device-reservation
        public static final String ADD_BOOKING = DEVICE_RESERVATION + "/add-booking";
        public static final String UPDATE_BOOKING = DEVICE_RESERVATION + "/update-booking";
        public static final String CANCEL_BOOKING = DEVICE_RESERVATION + "/cancel-existing-booking";
        public static final String AVAILABLE_DEVICES = DEVICE_RESERVATION + "/available-devices";
        public static final String RETURN_DEVICE = DEVICE_RESERVATION + "/return-device";
        public static final String GET_BOOKINGS = DEVICE_RESERVATION + "/get-bookings";
        public static final String GET_ALL_CLOSED_BOOKINGS = DEVICE_RESERVATION + "/get-All-closed-bookings";

        // device
        public static final String GET_DEVICE = DEVICE + "/get-device/" + Constants.RouterCons.SERIAL_NUMBER_PARAM;
        public static final String GET_AVAILABLE_DEVICES = DEVICE + "/get-available-devices";
    }
}
