# Stage 1: Build the application
FROM nginx:1.25.4-alpine-perl
COPY target /usr/share/nginx/springboot
