FROM node:8.16.2-alpine 
WORKDIR /opt/app
COPY ./app .
RUN npm install
CMD ["npm", "start"]
EXPOSE 9999