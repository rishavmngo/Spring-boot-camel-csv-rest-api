# Spring Camel rest api

## Instructions

1. Create a database "bank"

```bash
psql -U postgres
```

```sql
create database bank;
```

2. run the application using

```bash
mvn spring-boot:run
```

## Operations

### Add data from csv file

1. Go to `data/input` folder for sample csv files
2. While the application is running put the file in the `data/inbox` folder.
3. This will move the csv file from `data/inbox` to `data/inbox/.done` folder denoting successfull insertion and you can also see console for message
4. If the file failed to insert data in database the file will move to .failed folder and throw error and print error message to console.

### Rest api

| Function                     | Route                                      | Type   | Return                            |
| ---------------------------- | ------------------------------------------ | ------ | --------------------------------- |
| Get all customers            | /api/customers/getAll                      | GET    | Customers[]                       |
| Delete a customers by id     | /api/customers/deleteById/{id}             | DELETE | Customer                          |
| Delete customers by filename | /api/customers/deleteByFilename/{filename} | DELETE | String denoting customers deleted |

_filename should be without the .csv extension_

## Unit Test

Test can be found in the `src/test` directory

1. I tested every code which is written by me.
2. I have written 20 unit test and try to cover all branchs and lines
   <a href="https://github.com/rishavmngo/Spring-boot-camel-csv-rest-api"/target/site/jacoco/index.html>Jacoco report</a>
