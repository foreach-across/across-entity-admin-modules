FROM node:12.7.0-alpine

# Setup folder
RUN mkdir -p /build/
WORKDIR /build/

# Install dependencies
COPY /bootstrap-ui-module-frontend ./package.json
COPY /bootstrap-ui-module-frontend ./yarn.lock
COPY /bootstrap-ui-module-frontend ./.npmrc

# Copy source files
COPY /bootstrap-ui-module-frontend ./tsconfig.json
COPY /bootstrap-ui-module-frontend ./test
COPY /bootstrap-ui-module-frontend ./src
