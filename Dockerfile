FROM openjdk:8-jre

RUN mkdir -p /opt/app

COPY api/build/install/service-auth-provider-api /opt/app

EXPOSE 8080

ENTRYPOINT [ "/opt/app/bin/service-auth-provider-api" ]
