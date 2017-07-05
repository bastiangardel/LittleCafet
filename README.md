This project is comprehended by the following:
* Spring Boot REST controller
* Apache Shiro (1.2.4) session-management with MySQL-based authorizing realm
* Hazelcast (3.5.1) powered session distributed persistence
* MySQL Database with JPA and Hibernate
* Swagger Rest API Doc with Spring Fox plugin


# Summary
Ce projet a consisté au développement d’un système de paiement simple, léger et facilement déployable. Le système est composé de deux applications clientes, l’une pour les vendeurs et l’autre pour les acheteurs. Il comprend également un serveur qui constitue le cœur du système.
Un des points du cahier des charges est que les application clientes doivent tourner sur iOS. Elles ont par conséquent été développées avec le langage Swift (voir 8.4.1.6) et en utilisant les nombreuses librairies proposées par le gestionnaire de paquets CocoaPods (voir 8.5.1 ou 8.6.1).
Le serveur quant à lui a été développé en Java avec le Framework Spring (voir 8.4.1.1). Il expose une API Rest vers l’extérieur.
Toutes les fonctionnalités demandées dans le cahier des charges ont été implémentées. Pour que le système soit utilisable, il faudrait encore développer l’application administrateur qui, à la date de rendu de ce rapport, n’est pas encore disponible.

Des tests globaux des fonctionnalités et de la sécurité ont étés effectués. Quelques corrections ont dû être faites pour que tout fonctionne bien et que tout soit correctement sécurisé.
Une analyse sécuritaire complète du système de paiement a été également effectuée. Les menaces, les vulnérabilités et les risques d’un tel système y sont relevées. Les contremesures déjà implémentées de base par le fabriquant des appareils et celles implémentées au cours du développement y sont décrites.
A la fin du rapport vous trouverez également quelques idées pour les développements futurs. Comme par exemple la création d’un ticket à l’aide d’une base de données des produits vendus qui permettrait d’avoir une gestion des inventaires automatique.


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

