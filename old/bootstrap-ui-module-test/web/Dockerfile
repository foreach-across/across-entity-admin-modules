# TODO delete
# Using the hardcoded gitlab URL here, so it's easy to build locally as well TODO: this needs docker login ...
# FROM gitlab.isaac.nl:443/antwerpen/dependency_proxy/containers/eclipse-temurin:8
FROM eclipse-temurin:8

ENV TZ=Europe/Brussels
ENV LC_ALL en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /web/bootstrap-ui-module-test-exec.jar" ]
