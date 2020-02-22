FROM node:10.13-alpine
ENV NODE_ENV production
WORKDIR /usr/src/app
COPY ["package.json", "package-lock.json*", "npm-shrinkwrap.json*", "./"]
RUN npm install --silent
COPY . .
RUN npm run build

FROM node:10.13-alpine
ENV NODE_ENV production
WORKDIR /app
COPY --from=0 /usr/src/app/build ./app/app-1
RUN npm i live-server -g
EXPOSE 8080
CMD live-server