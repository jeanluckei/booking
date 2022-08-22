# Booking API

[Spring Boot](http://projects.spring.io/spring-boot/) microservice responsible to ensure the operation of the last hotel
in Cancun.
It was developed with one of the best solutions for high quality of service, it includes a reactive API based on 
webflux, it uses Bucket4j to secure rate limit by user, redis to take care of the rate limit cache in a distributed way, 
Kafka streams to queue the user actions and mongo database to persist the bookings.

### Why using Reactive types?
Reactive types are not intended to allow you to process your requests or data faster, in fact they will introduce a
small overhead compared to regular blocking processing. Their strength lies in their capacity to serve more request
concurrently, and to handle operations with latency, such as requesting data from a remote server, more efficiently.
They allow you to provide a better **QUALITY OF SERVICE** and a predictable capacity planning by dealing natively with time
and latency without consuming more resources. Unlike traditional processing that blocks the current thread while waiting
a result, a Reactive API that waits costs nothing, requests only the amount of data it is able to process and bring new
capabilities since it deals with stream of data, not only with individual elements one by one.

### Technology
* [Java 11](https://docs.oracle.com/en/java/javase/11/)
* [Spring Boot 2.7.3](https://spring.io/projects/spring-boot)
* [Bucket4j](https://github.com/bucket4j/bucket4j)
* [Redis 7](https://redis.com/)
* [MongoDB](https://www.mongodb.com/)
* [Kafka](https://kafka.apache.org/)
* ["Semi Hexagonal Architecture"](https://www.baeldung.com/hexagonal-architecture-ddd-spring)
  
### Running application:
Requirements: [Docker compose](https://docs.docker.com/compose/install/)

This command is responsible for starting the kafka topics, mongodb and redis cache:
```` 
docker-compose up
````

When all dockers up, start two instances of the service:
````
./gradlew bootRun --args='--server.port=8080'
./gradlew bootRun --args='--server.port=8081'
````
Check the swaggers: http://localhost:8080/swagger-ui.html and http://localhost:8081/swagger-ui.html.

To test the rate limit and the cache, it's interesting to run commands in different ports at each time.

And if you want, run the unit tests with the following command:
````
./gradlew test
````

The tests developed were all based on business rules, so there will be unit tests only in the service layer and in
the controller.

### Examples:
````
POST /booking
header: "test"
{
  "roomNumber": 237,
  "username": "test",
  "startDate": "2022-08-27",
  "endDate": "2022-08-29"
}

PUT /booking/6303bd67ef22d66c2ea11693
header: "test"
{
  "roomNumber": 237,
  "username": "test",
  "startDate": "2022-08-27",
  "endDate": "2022-08-29"
}

GET /booking header: "test"
GET /booking/room/237?status=BOOKED
GET /booking/room/237/availability?startDate=2022-08-26&endDate=2022-08-27
````

### Post-Covid scenario:
People are now free to travel everywhere but because of the pandemic, a lot of hotels went
bankrupt. Some former famous travel places are left with only one hotel.
You’ve been given the responsibility to develop a booking API for the very last hotel in Cancun.
The requirements are:
- API will be maintained by the hotel’s IT department.
- As it’s the very last hotel, the quality of service must be 99.99 to 100% => no downtime
- For the purpose of the test, we assume the hotel has only one room available
- To give a chance to everyone to book the room, the stay can’t be longer than 3 days and
  can’t be reserved more than 30 days in advance.
- All reservations start at least the next day of booking,
- To simplify the use case, a “DAY’ in the hotel room starts from 00:00 to 23:59:59.
- Every end-user can check the room availability, place a reservation, cancel it or modify it.
- To simplify the API is insecure.

### Instructions:
- Pas de limite de temps (très bien fait il faut au moins 3 à 4 soirées)
- Le minimum requis est un README et du code.
- Tous les shortcuts pour gagner du temps sont autorisés dans la mesure où c’est
  documenté. Tout shortcut non expliqué doit etre consideré comme une erreur. On
  pourrait accepter un rendu avec 3 lignes de code si elles ont du sens et que tout le
  raisonnement et les problèmatiques à prendre en compte sont decrites. 
