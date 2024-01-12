# Spring Camel rest api

## Instructions

1. Create a database "bank"
    1. `psql -U postgres`
    2. `create database bank;`
2. run the application using `mvn spring-boot:run`

## Check

### Add data from csv file

1. go to data/inbox/.done folder for sample csv files
2. while the application is running put the file in the inbox folder from .done folder.
3. This will move the csv file from inbox to .done folder denoting successfull insertion and you can also see console for message
4. If the file failed to insert data in database the file will move to .failed folder

### Rest api

| Function                     | Route                                      | Type   | Return                            |
| ---------------------------- | ------------------------------------------ | ------ | --------------------------------- |
| Get all customers            | /api/customers/getAll                      | GET    | Customers[]                       |
| Delete a customers by id     | /api/customers/deleteById/{id}             | DELETE | Customer                          |
| Delete customers by filename | /api/customers/deleteByFilename/{filename} | DELETE | String denoting customers deleted |
