package com.devicemanager.DeviceManager.util;

public class ErrorMessage {

    public static class ValidationErrorMessage {
        public static final String INVALID_SERIAL_NUMBER =
                "serial number only accepts hex-decimal values and should be 4 character sized";
        public static final String VERSION_PATTERN = "version only accepts alphanumeric and white space";
        public static final String INVALID_DATES = "invalid dates were added";
        public static final String ALREADY_BOOKED ="Device already book in given time frame";
        public static final String INVALID_BOOKING_DETAILS ="Given booking id is not exist";
        public static final String NO_UPDATES ="No any updates to do with request";
        public static final String INVALID_START_DATE = "Start date is after end date";
        public static final String INVALID_END_DATE = "End date is before start date";
        public static final String INVALID_UPDATE_START_DATE = "Can't update start date since already started the using the device";
        public static final String START_DATE_NOT_IN_FUTURE = "Start date should be in future date";
        public static final String INVALID_RETURN_DEVICE = "can't return device, without starting a booking. Please cancel the booking";
    }

    public static class DataIntegrityErrorMessage {
        public static final String NOT_EXIST_DEVICE_SERIAL_NUMBER = "There is no device for given serial number";
        public static final String DEVICE_IS_NOT_IN_WORKING_CONDITION = "Given device is not in working condition";
    }
}
