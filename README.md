# restfulHw
Assignment Details:
A customer receives 2 points for every dollar spent over $100 in each transaction, plus 1 point for every dollar spent over $50 in each transaction
(e.g. a $120 purchase = 2x$20 + 1x$50 = 90 points).
Given a record of every transaction during a three month period, calculate the reward points earned for each customer per month and total.

Api summary: Takes list of transactions, creates list of customers with mapped monthly points and total.

Build project: `./mvnw clean package`
Run Project: `java -jar /target/jarfile`

## Api Request url: http://localhost:8080/customer/perMonth

2 request types created Get & Post:

1.) Get request will use transaction.json file within project to create return list. 
2.) Post request expects a RequestBody with list of transactions via json. 
    (I recommend using postman to easily create and test your own data set).
###### Transaction Json Example: 
```
[
    {
        "customerId": 4,
        "firstName": "Doug",
        "lastName": "Stroer",
        "date": "2020-01-01",
        "amount": 1000.32
    }
]
```

Logs are logged inside /logs folder within project

Exception handling for incorrect/missing json values, also posted to logs.
