package com.devicemanager.DeviceManager.util.validator;

import java.util.Date;

public class DateValidator {

    public static boolean isStartAfterEnd(final Date start, final Date end) {
        return start != null && end != null && start.before(end);
    }

    public static boolean isFutureDate(final Date date) {
        return date != null && date.after(new Date());
    }

    public static boolean isPassedDate(final Date date) {
        return date != null && date.before(new Date());
    }
}
