ARG APP_INSIGHTS_AGENT_VERSION=2.5.0
ARG JAVA_OPTS=java.io.tmpdir/opt/app/tmp
FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.2

# Mandatory!
ENV APP service-auth-provider.jar

COPY lib/AI-Agent.xml /opt/app/
COPY build/libs/$APP /opt/app/

EXPOSE 8489

CMD ["service-auth-provider.jar"]
