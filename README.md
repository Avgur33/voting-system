[Выпускной Проект курса TopJava](https://javaops.ru/view/topjava)
===============================

###  Stack
- Spring Boot 2.5, Spring MVC, Security, Lombok, H2, Swagger/OpenAPI 3.0, Caffeine Cache 

### Task:
Design and implement a REST API using Hibernate/Spring/SpringMVC (or Spring-Boot) without frontend.

The task is:

Build a voting system for deciding where to have lunch.

- 2 types of users: admin and regular users
- Admin can input a restaurant and it's lunch menu of the day (2-5 items usually, just a dish name and price)
- Menu changes each day (admins do the updates)
- Users can vote on which restaurant they want to have lunch at
- Only one vote counted per user
- If user votes again the same day:
- If it is before 11:00 we assume that he changed his mind.
- If it is after 11:00 then it is too late, vote can't be changed
- Each restaurant provides a new menu each day.

As a result, provide a link to github repository. It should contain the code, README.md with API documentation and couple curl commands to test it (better - Swagger).

P.S.: Make sure everything works with latest version that is on github :)
P.P.S.: Assume that your API will be used by a frontend developer to build frontend on top of that.

### Description
- DB - H2 (in memory)
- time limit for deciding given by "limit-time:vote" in application.yaml 
- Admin - create restaurant - create dishes for restaurant - create menu for restaurant with its dishes 
- User - create vote


### For run
- java version: openjdk 16.0.1
- mvn version: apache maven 3.8.1

1) clone project from github (https://github.com/Avgur33/voting-system.git)
2) execute command: mvn clean install
3) execute command: mvn spring-boot:run

default port - http://localhost:8080/

### For test 
- DB - http://localhost:8080/h2-console 
- Swagger - http://localhost:8080/swagger-ui
- Documentation for api in swagger.json
- Test users credentials:
  - user with USER role: 
    - login: user@yandex.ru 
    - password: password
  - user with ADMIN role: 
    - login: admin@gmail.com 
    - password: admin











