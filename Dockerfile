FROM hmcts/cnp-java-base:openjdk-jre-8-alpine-1.4

RUN mkdir -p /opt/app

COPY build/install/service-auth-provider /opt/app

EXPOSE 8489

ENTRYPOINT [ "/opt/app/bin/service-auth-provider" ]
