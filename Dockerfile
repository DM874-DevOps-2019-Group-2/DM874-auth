FROM bigtruedata/sbt as build-env

COPY . /

WORKDIR /

RUN sbt -J-Xms2000M -J-Xmx4G -J-Xss10M -J-XX:+CMSClassUnloadingEnabled -J-XX:MaxMetaspaceSize=3000m test

RUN sbt -J-Xms2000M -J-Xmx4G -J-Xss10M -J-XX:+CMSClassUnloadingEnabled -J-XX:MaxMetaspaceSize=3000m assembly

FROM adoptopenjdk/openjdk11:jre

COPY --from=build-env /target/scala-2.12/dm874-auth-assembly-1.0.jar /run.jar
COPY --from=build-env /src/main/resources/application.conf /application.conf
COPY --from=build-env /src/main/resources/log4j.properties /log4j.properties

RUN ls -la /

CMD ["java", "-jar", "/run.jar", "-Dlog4j.configuration=/log4j.properties", "-Dconfig.file=/application.conf"]
