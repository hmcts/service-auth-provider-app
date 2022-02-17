ARG APP_INSIGHTS_AGENT_VERSION=3.2.6
FROM hmctspublic.azurecr.io/base/java:17-distroless

# Mandatory!
ENV APP service-auth-provider.jar

COPY lib/applicationinsights.json /opt/app/
COPY build/libs/$APP /opt/app/

EXPOSE 8489

CMD ["service-auth-provider.jar"]
