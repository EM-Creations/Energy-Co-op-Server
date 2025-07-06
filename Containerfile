FROM azul/zulu-openjdk-debian:21.0.7
MAINTAINER em-creations.co.uk
COPY build/libs/energycoop-*.jar application.jar
ENTRYPOINT ["java", "-jar", "/application.jar"]