FROM node:22-bookworm-slim
RUN apt-get update && apt-get install -y --no-install-recommends openssl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY package*.json ./
COPY apps/backend/package.json apps/backend/package.json
COPY apps/admin/package.json apps/admin/package.json
COPY packages/contracts/package.json packages/contracts/package.json
RUN npm ci
COPY packages packages
COPY apps/backend apps/backend
RUN npm run db:generate && npm run build
USER node
EXPOSE 3000
CMD ["npm", "run", "start", "-w", "@iatv/backend"]
