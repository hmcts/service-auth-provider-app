FROM hmcts/cnp-java-base:openjdk-jre-8-alpine-1.4

# Mandatory!
ENV APP service-auth-provider.jar
ENV APPLICATION_TOTAL_MEMORY 1024M
ENV APPLICATION_SIZE_ON_DISK_IN_MB 66

RUN mkdir -p /opt/app

COPY build/install/service-auth-provider /opt/app

EXPOSE 8489

ENTRYPOINT [ "/opt/app/bin/service-auth-provider" ]
