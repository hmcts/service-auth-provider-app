FROM openjdk:8-jre

RUN mkdir -p /opt/app

COPY build/install/service-auth-provider /opt/app

EXPOSE 8489

ENTRYPOINT [ "/opt/app/bin/service-auth-provider" ]
