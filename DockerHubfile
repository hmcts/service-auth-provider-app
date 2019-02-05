#### Create Jar
FROM gradle:4.10.2-jdk8 as builder

COPY . /home/gradle/app
USER root
RUN chown -R gradle:gradle /home/gradle/app

USER gradle
WORKDIR /home/gradle/app

RUN mkdir -p api/src/main/resources/META-INF \
&& echo "build.version=$(gradle -q printVersion)" > api/src/main/resources/META-INF/build-info.properties \
&& echo "build.number=${BUILD_NUMBER:=docker}" >> api/src/main/resources/META-INF/build-info.properties \
&& echo "build.commit=$(git rev-parse HEAD)" >> api/src/main/resources/META-INF/build-info.properties \
&& echo "build.date=$(date)" >> api/src/main/resources/META-INF/build-info.properties

RUN gradle assemble

#### Actual DockerFile
FROM hmcts/cnp-java-base:openjdk-8u181-jre-alpine3.8-1.0

MAINTAINER "HMCTS <https://github.com/hmcts>"
LABEL maintainer = "HMCTS <https://github.com/hmcts>"

ENV APP service-auth-provider.jar
ENV APPLICATION_TOTAL_MEMORY 1024M
ENV APPLICATION_SIZE_ON_DISK_IN_MB 66

WORKDIR /opt/app
COPY --from=builder /home/gradle/app/build/libs/service-auth-provider.jar .

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" curl --silent --fail http://localhost:8489/health

EXPOSE 8489
