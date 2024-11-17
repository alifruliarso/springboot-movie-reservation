# SpringbootMovieReservation

> **Connect** with me through [Upwork](https://www.upwork.com/freelancers/~018d8a1d9dcab5ac61), [LinkedIn](https://linkedin.com/in/alifruliarso), [Email](mailto:alif.ruliarso@gmail.com), [Twitter](https://twitter.com/alifruliarso)

## Technology Stack
Spring Boot, Docker, Thymeleaf, Maven\
Database: GridDB 5.6.0

## Run Application with Docker Compose

Start the application with the following command - here with build docker images

```shell
docker compose up --build
```

## Further readings

* [Maven docs](https://maven.apache.org/guides/index.html)  
* [Spring Boot reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)  
* [Spring Data JPA reference](https://docs.spring.io/spring-data/jpa/reference/jpa.html)
* [Thymeleaf docs](https://www.thymeleaf.org/documentation.html)  
* [Bootstrap docs](https://getbootstrap.com/docs/5.3/getting-started/introduction/)  
* [Learn Spring Boot with Thymeleaf](https://www.wimdeblauwe.com/books/taming-thymeleaf/)  
* [Bootify.io](https://bootify.io/next-steps/)
* [JEP 310: Application Class-Data Sharing](https://openjdk.org/jeps/310)
* [Unpacking the Executable jar](https://docs.spring.io/spring-boot/reference/packaging/efficient.html)
* [Efficient Container Images, Layering Docker Images](https://docs.spring.io/spring-boot/reference/packaging/container-images/efficient-images.html)


### Helpful commands

```shell
docker run --name griddbmovieres \
    -e GRIDDB_CLUSTER_NAME=dockerGridDB \
    -e GRIDDB_PASSWORD=admin \
    -e NOTIFICATION_MEMBER=1 \
    griddb/griddb:5.6.0-jammy


export $(cat .env | xargs)

CONT=`docker ps | grep griddbmovieres | awk '{ print $1 }'`; docker exec $CONT cat /etc/hosts | grep $CONT | awk '{ print $1 }'

CONT=`docker ps | grep griddbmovieres | awk '{ print $1 }'`; docker exec -it $CONT /bin/bash

CONT=`docker ps | grep moviereservbackend | awk '{ print $1 }'`; docker exec -it $CONT /bin/bash

```