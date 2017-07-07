This project is comprehended by the following:
* Spring Boot (1.5.4)
* Apache Shiro (1.4.0) session-management with MySQL-based authorizing realm
* Hazelcast (3.8.3) powered session distributed persistence
* MySQL Database with JPA and Hibernate
* Swagger Rest API Doc with Spring Fox plugin


# Summary



# Pre-requisites

* JDK 8
* Maven 3.2.3 or newer
* MySQL 

# Run

```
mvn clean package spring-boot:run
```

# Swagger Rest Doc
```
https://localhost:9000/swagger-ui.html 
```

# Testing

## Automatically

```mvn clean test```

## Manually

### Initialize test scenario

```
curl -i -H "Accept: application/json" -X PUT https://localhost:9000/users
```

### Access protected method without being authenticated

```
curl -i -H "Accept: application/json" -X GET https://localhost:9000/users
```

You should get a ```401 Unauthorized``` response status.

### Log-in

```
curl -i -c cookie.txt -H "Accept: application/json" -H "Content-type: application/json" -X POST -d '{"username":"test@test.com","password":"test"}' https://localhost:9000/users/auth
```

You should get a ```200 OK``` response status and have a valid cookie stored in ```cookie.txt```.

### Access protected method again

```
curl -i -b cookie.txt -H "Accept: application/json" -X GET https://localhost:9000/users
```

You should get a ```200 OK``` response status and some JSON representing existing users.


# Licence
Copyright (c) 2016 Bastian Gardel

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

