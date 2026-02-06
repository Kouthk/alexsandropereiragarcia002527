# Processo Seletivo Simplificado Conjunto nº 001/2026 – SEPLAG/MT
### Projeto Prático – Back End Java (Engenheiro da Computação – Sênior)

![Java](https://img.shields.io/badge/Java-17%2B-007396?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-1.27-009639?logo=nginx&logoColor=white)

## Dados de Inscrição
- **Nome completo**: Alex Sandro Pereira Garcia
- **Vaga**: Analista de TI – Engenheiro da Computação (Sênior)
- **Inscrição**: 16480

## Objetivo
API REST para gestão de artistas e álbuns, com relacionamento N:N, paginação e upload de capas em MinIO, conforme edital.

## Stack
- Java 17+ / Spring Boot 3
- PostgreSQL
- Flyway
- MinIO (S3)
- OpenAPI/Swagger
- WebSocket (STOMP)
- Docker / docker-compose
- Nginx

## Como executar

### 1) Baixar o repositório
```bash
git clone https://github.com/Kouthk/alexsandropereiragarcia002527.git
cd alexsandropereiragarcia002527
```

### 2) Limpar o cache/volumes do Docker
(Recomendado) Limpe o cache/volumes do Docker antes de subir, para evitar conflitos de banco/porta:
```bash
docker compose down -v --remove-orphans
docker volume prune -f
docker builder prune -f
```

### 3) Subir tudo via Docker Compose (API + DB + MinIO + Nginx)
```bash
docker-compose up -d --build
```
- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- MinIO Console: `http://localhost:9001`

- NGINX:
- API (via Nginx): `http://localhost/`
- Swagger (via Nginx): `http://localhost/swagger-ui.html`
-  MinIO (via Nginx): `http://localhost/minio`

Se estiver em IDE/Windows, reimporte o projeto Maven para garantir o classpath de testes.

## Autenticação
- JWT com expiração configurável por `security.jwt.access-token-expiration-minutes`.
- Refresh token persistido em tabela `tokens`.
- Endpoints:
  - `POST /api/v1/auth/login`
  - `POST /api/v1/auth/refresh`
  - `POST /api/v1/auth/logout`
  
## Credenciais padrão

| Serviço | Usuário | Senha | Observação |
| --- | --- | --- | --- |
| Admin (carga inicial) | `admin` | `admin` | Login para autenticação na API |
| MinIO | `minioadmin` | `minioadmin` | Console: `http://localhost:9001` |

## Como testar no Swagger
1. Acesse `http://localhost:8080/swagger-ui.html`.
2. Faça login em `POST /api/v1/auth/login` com o usuário `admin` para obter o `accessToken`.
3. Clique em **Authorize** e informe: `Bearer <accessToken>`.
4. Execute os endpoints desejados (ex.: `/api/v1/artistas`, `/api/v1/albuns`).

## Endpoints principais (versionados)
- **Artistas**: 
  - `/api/v1/artistas`
  - `GET /api/v1/artistas` (ordenar via `sort=nome,asc|desc`)
  - `GET /api/v1/artistas/{id}`
  - `POST /api/v1/artistas`
  - `PUT /api/v1/artistas/{id}`
  
- **Álbuns**: 
  - `/api/v1/albuns`
  - `GET /api/v1/albuns` (paginado)
  - `GET /api/v1/albuns/{id}`
  - `GET /api/v1/albuns/por-artista/{artistaId}`
  - `GET /api/v1/albuns/por-artista?nome=...`
  - `GET /api/v1/albuns/por-tipo?tipo=BANDA|SOLO`
  - `GET /api/v1/albuns/filtro?titulo=...&artista=...&tipo=BANDA|SOLO` 
  - `POST /api/v1/albuns/upload` (multipart, cria álbum + capas no MinIO)
  - `PUT /api/v1/albuns/{id}`
  - `DELETE /api/v1/albuns/{id}` (inativa)

- **Capas**: 
  - `/api/v1/albuns/{albumId}/capas`
  - `GET /api/v1/albuns/{albumId}/capas`
  - `POST /api/v1/albuns/{albumId}/capas/upload`
  - `PUT /api/v1/albuns/{albumId}/capas/{capaId}/principal`

- **Regionais**:
  - `/api/v1/regionais`
  - `GET /api/v1/regionais`
  - `POST /api/v1/regionais/sync`

## WebSocket
- Endpoint STOMP: `/ws`
- Tópico: `/topic/albuns` (notifica a criação de álbum)

## Health Checks
- Actuator: `GET /actuator/health`

## Segurança (CORS)
- CORS limitado a `http://localhost:*` e `http://127.0.0.1:*`.

## Estrutura de dados (tabelas)
### artista
- `id BIGSERIAL PK`
- `nome VARCHAR(255) NOT NULL`
- `tipo VARCHAR(20) NOT NULL` (BANDA/SOLO)
- `ativo BOOLEAN NOT NULL`
- `created_at TIMESTAMP NOT NULL`
- `updated_at TIMESTAMP NOT NULL`

### album
- `id BIGSERIAL PK`
- `titulo VARCHAR(255) NOT NULL`
- `ano_lancamento INT`
- `created_at TIMESTAMP NOT NULL`
- `updated_at TIMESTAMP NOT NULL`
- `ativo BOOLEAN NOT NULL`

### artista_album (N:N)
- `artista_id BIGINT PK/FK`
- `album_id BIGINT PK/FK`

### album_capa
- `id BIGSERIAL PK`
- `album_id BIGINT NOT NULL`
- `object_key VARCHAR(255) NOT NULL`
- `principal BOOLEAN NOT NULL`
- `created_at TIMESTAMP NOT NULL`

### regional
- `id BIGSERIAL PK`
- `id_regional_externo INTEGER NOT NULL`
- `nome VARCHAR(200) NOT NULL`
- `ativo BOOLEAN NOT NULL`

### segurança
- `roles (id, authority)`
- `users (id, username, password)`
- `user_roles (user_id, role_id)`
- `tokens (id, token_session, deleted_at, user_id)`

## Carga inicial
Flyway popula artistas, álbuns e relacionamento N:N com exemplos, além de usuário `admin`.

## Requisitos do edital atendidos
- API REST Java (Spring Boot)
- JWT com expiração e refresh
- POST, PUT, GET
- Paginação de álbuns
- Consultas por artista (id/nome) e tipo (banda/solo)
- Upload de capas e armazenamento no MinIO
- Links pré-assinados (30 min)
- Versionamento de endpoints
- Flyway migrations
- Swagger/OpenAPI
- Health checks (actuator)
- Testes unitários
- WebSocket para novo álbum
- Endpoint de regionais com sincronização
- Docker e docker-compose (API + MinIO + DB)
- Rate limit (10 request/min por usuário).
