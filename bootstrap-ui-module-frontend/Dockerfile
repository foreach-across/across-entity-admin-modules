FROM node:12.7.0-alpine

# Setup folder
RUN mkdir -p /build/
WORKDIR /build/

# Install dependencies
COPY /bootstrap-ui-module-frontend/package.json ./package.json
COPY /bootstrap-ui-module-frontend/yarn.lock ./yarn.lock
COPY /bootstrap-ui-module-frontend/.npmrc ./.npmrc

# Copy source files
COPY /bootstrap-ui-module-frontend/tsconfig.json ./tsconfig.json
COPY /bootstrap-ui-module-frontend/test ./test
COPY /bootstrap-ui-module-frontend/src ./src
