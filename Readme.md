**Device Management**

Some assumptions are made
* Added spring security but permit all for all the roles, since there are no authentication/ authorization applied.
* Enums all are casesensitive
* Throwing only KnownException for known and validation issues, instead of using several exception types.
* Default data.sql is loads when application starts.


Instructions
* All the service methods are commented with there respective functionalities.
* Just run the springboot project and can access http://localhost:81/

Available Api calls
  
        device-reservation/add-booking
        device-reservation/update-booking
        device-reservation/cancel-existing-booking
        device-reservation/available-devices
        device-reservation/return-device
        device-reservation/get-bookings
        device-reservation/get-All-closed-bookings

        device/get-device/{serialNumber}
        device/get-available-devices";

        # future api plans
        device/add
        device/change-status



Unit tests

  * covered most of unit tests
  * DeviceBookingService - added a test which cover most of functionality, that need to be separated. 
    But due to time constraint I just added to test for cover the functionality.
  * Unit tests were added only for functionalities, not for request schema validations.
  * Handler tests need to be added

In future 
  * Based on device return date need to provide a reminder or business function

challenges
  * Validations
  * need to track of data accuracy. (Ex: keep devices for test after expected return date without doing update the booking, that may cause inaccurate data for future bookings)

