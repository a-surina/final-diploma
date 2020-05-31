FROM openjdk:11-jre-slim
WORKDIR /opt/app
COPY ./app .
RUN set -ex; \
    apt-get update; \
    apt-get install -y --no-install-recommends netcat; \
    chmod a+x wait-for.sh
CMD ["./wait-for.sh", "mysql:3306", "-t", "60", "--", "java", "-jar", "aqa-shop.jar"]
EXPOSE 8081