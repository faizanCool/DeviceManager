API Instruction Guide

1. Reserve or book a device for future date

         POST - {host}/device-reservation/add-booking
         Request Class - DeviceBookingRequest.java
         Body - 
            {
               serialNumber: required 
                  (device serial number, accepts only hexa-decimals, must be of length of 4 characters)
               userId: required 
                  (booking added, accepts only alphanumeric, must be length between 5-10)
               expectedStartDate: required (Device booking starts on date)
               expectedreturnDate: required (Device return date)
               description: optional
            }

2. Update existing booking

         PUT - {host}/device-reservation/update-booking
         Request Class - DeviceBookingUpdateRequest.java
         Body - 
            {
               id: required (booking id)
               startDate: optional (updating start date)
               endDate: optional (updateing end date)
            }

3. Get existing available bookings

         GET - {host}/device-reservation/get-bookings
         Request Class - DeviceBookingGetRequest.java
         Body - 
            {
               serialNumber: optional 
                  (device serial number, accepts only hexa-decimals or empty, must be of length of 4 characters)
               startDate: optional (from date)
               endDate: optional (to date)
            }

4. Get ALL closed bookings (didn't add any filters)

         GET - {host}/device-reservation/get-All-closed-bookings
         Request Class - DeviceBookingGetRequest.java
         Body - {}


5. Cancel an existing booking

         POST - {host}/device-reservation/cancel-existing-booking
         Request Class - DeviceClosebookingRequest.java
         Body - 
            {
               id: required (booking id)
               userId: required 
                  (user who cancel the booking, accepts only alphanumeric, must be length between 5-10)
            }

6. Return a device for a started booking

         POST - {host}/device-reservation/return-device
         Request Class - DeviceClosebookingRequest.java
         Body - 
            {
               id: required (booking id)
               userId: required 
                  (user who returns the device, accepts only alphanumeric, must be length between 5-10)
            }

7. Get existing available bookings

         GET - {host}/device/get-available-devices
         Request Class - DeviceGetRequest.java
         Body - 
            {
               startDate: required (from date)
               endDate: required (to date)
               statuses: optional 
                  (device statuses as a list, filters only the devices belongs to given status )
               models: optional 
                  (device models as a list, filters only the devices belongs to given models )
            }

8. Get device by serial number

         GET - {host}/device/{serailNumber}
        


