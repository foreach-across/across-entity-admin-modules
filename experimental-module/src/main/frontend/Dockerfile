FROM node:12.18.0-alpine

# Setup folder
RUN mkdir -p /build/src/main/frontend/

WORKDIR /build/src/main/frontend

# Install dependencies
COPY ./package.json package.json
COPY ./yarn.lock yarn.lock

RUN yarn --frozen-lockfile
