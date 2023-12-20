**Device Management**

Some assumptions are made
* Added spring security but permit all for all the roles, since there are no authentication/ authorization applied.
* Enums all are casesensitive
* Throwing only KnownException for known and validation issues, instead of using several exception types.
* unit tests were added only for functionalies, not for request schema validations.

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



