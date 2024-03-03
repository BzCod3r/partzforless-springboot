# Stage 1: Build the application
FROM nginx:1.25.4-alpine-perl
EXPOSE 80
COPY target /usr/share/nginx/springboot
COPY ./default.conf /etc/nginx/conf.d/default.conf
