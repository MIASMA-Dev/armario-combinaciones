# Build:  podman build -f docker/frontend.Containerfile -t armario/frontend:latest .
# Run:    podman run --rm -p 4200:80 armario/frontend:latest

FROM docker.io/library/node:22-alpine AS build
WORKDIR /app

COPY frontend/package*.json ./
RUN npm ci || npm install

COPY frontend/ .
RUN npm run build -- --configuration production

FROM docker.io/library/nginx:1.27-alpine
COPY --from=build /app/dist/*/browser /usr/share/nginx/html
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]