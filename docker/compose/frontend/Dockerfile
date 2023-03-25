FROM node:16-bullseye-slim
MAINTAINER av.pushking@gmail.com
RUN mkdir /frontend

COPY ./frontend/package*.json ./frontend/

WORKDIR /frontend
RUN npm install
COPY ./frontend/src /frontend/src/
COPY ./frontend/public /frontend/public/
EXPOSE 3000
CMD ["npm", "start"]