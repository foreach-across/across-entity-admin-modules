FROM node:12.7.0-alpine

# Setup folder
RUN mkdir -p /build/
WORKDIR /build/

# Install dependencies
COPY /bootstrap-ui-module-frontend1 ./package.json
COPY /bootstrap-ui-module-frontend1 ./yarn.lock
COPY /bootstrap-ui-module-frontend1 ./.npmrc

# Copy source files
COPY /bootstrap-ui-module-frontend1 ./tsconfig.json
COPY /bootstrap-ui-module-frontend1 ./test
COPY /bootstrap-ui-module-frontend1 ./src
