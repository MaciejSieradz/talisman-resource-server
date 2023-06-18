# Talisman resource server

This is a resource server for my Talisman Magic and Sword application. 
I've decided to hide previous version, because I think that it felt a little bit outdated. I am still
eagerly developing this project, so many changes, upgrades and features are about to come soon. 

## Requirements

I've created a simple authorization server, but for now it is not available, because
there is no endpoints to protect. I strongly suggest to retrieve cards from my
[Talisman-scrapper](https://github.com/MaciejSieradz/talisman-scrapper) application(Unfortunately, cards are only in polish)
and save them to any MongoDB database you like.

## Changes

This version is a reactive one. I also decided to switch from `.properties` to `.yml` files, 
as they seem to be much clearer. Moreover, I am currently exploring gradle as the alternative to maven, so I built this application
with gradle instead of maven. My goal was to try to write Spring application in more recent programming style.

## Run the application

To run this application, you can simply run it inside your IDE. You can also use `./gradlew bootRun` (remember to be inside main directory).

## Build

If you want to have just `.jar` file you can simply use `./gradlew build` command. \
For now, I didn't create a seperate `Dockerfile`. If you want to create a docker image, just use a default gradle build

```shell
./gradlew bootBuildImage --imageName=myorg/myapp
```

After that you can just simply run docker container from this image: 

```shell
docker run -p 8080:8080 -t myorg/myapp
```

When I will create more applications for backend(make use
of authorization server, service for more advanced statistics and maybe some other),
I will most likely merge all this services to one repository and create `docker-compose.yml` file, so it will
be easy to just run everything with only one command. Also, I will create `Dockerfiles`. But for now, stay tunned!