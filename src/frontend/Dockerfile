# Build stage
FROM node:18-alpine as build
WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm ci --silent

COPY . .

RUN npm run build

# Nginx stage
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

COPY --from=build /app/build ./

COPY nginx.conf /etc/nginx/conf.d/default.conf

# copy env.sh script
COPY env.sh /docker-entrypoint.d/40-env.sh
RUN chmod +x /docker-entrypoint.d/40-env.sh

EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]