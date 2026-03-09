FROM gradle:jdk21-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle assemble --no-daemon

FROM alpine:3.22 AS package
ARG TRACCAR_VERSION=6.12

RUN apk add --no-cache unzip wget && \
    wget -qO /tmp/traccar.zip \
      https://github.com/traccar/traccar/releases/download/v${TRACCAR_VERSION}/traccar-other-${TRACCAR_VERSION}.zip && \
    unzip -qo /tmp/traccar.zip -d /traccar && \
    rm -f /tmp/traccar.zip

FROM alpine:3.22
WORKDIR /opt/traccar

RUN apk add --no-cache --no-progress openjdk21-jre-headless

COPY --from=package /traccar /opt/traccar
COPY --from=build /home/gradle/src/build/libs/*.jar /opt/traccar/

ENTRYPOINT ["java", "-Xms512m", "-Xmx2g", "-Djava.net.preferIPv4Stack=true"]
CMD ["-jar", "tracker-server.jar", "conf/traccar.xml"]