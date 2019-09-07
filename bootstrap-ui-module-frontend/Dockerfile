FROM node:12.9.1-alpine

RUN apk update && apk add bash

# Setup folder
RUN mkdir -p /build/src/main/frontend/

WORKDIR /build/

# Copy build scripts
COPY build-local.sh ./build-local.sh
RUN chmod +x ./build-local.sh

COPY build-prod.sh ./build-prod.sh
RUN chmod +x ./build-prod.sh

# Install dependencies
COPY ./src/main/frontend/package.json ./src/main/frontend/package.json
COPY ./src/main/frontend/yarn.lock ./src/main/frontend/yarn.lock
COPY ./src/main/frontend/.npmrc ./src/main/frontend/.npmrc

WORKDIR /build/src/main/frontend
RUN yarn --frozen-lockfile
WORKDIR /build/

# Copy source files
#COPY ./src/main/frontend/tsconfig.json ./src/main/frontend/tsconfig.json
#COPY ./src/main/frontend/test ./src/main/frontend/test
#COPY ./src/main/frontend/src ./src/main/frontend/src
