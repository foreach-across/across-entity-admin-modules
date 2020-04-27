# 1.) Do not use the alpine image as it fails frontend-maven-plugin:install-node-and-yarn
#     https://github.com/eirslett/frontend-maven-plugin/issues/743
# 2.) Avoid 3.5.4 or higher for now, surefire fails due to a broken openjdk package
#     https://github.com/carlossg/docker-maven/issues/90
# 3.) Using > 3.5.0 because of timestamp issues with -SNAPSHOT dependencies
#     https://issues.apache.org/jira/browse/MDEPLOY-221
FROM maven:3.5.2-jdk-8

# Might not be needed anymore ?
# RUN apt-get update && apt-get install -y build-essential