FROM openjdk:17-alpine
RUN mkdir /opt/app
WORKDIR /opt/app
COPY target/fhirplayground.jar .
CMD java -jar fhirplayground.jar
