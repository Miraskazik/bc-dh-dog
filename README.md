# bc-dh-dog

Business služba **evidence psů** (a jejich profilů) systému **Dog Hotel**.
Čisté CRUD se soft delete, žádná Kafka. Implementuje server stub vygenerovaný
z kontraktu [bc-dh-dog-api](https://github.com/Miraskazik/bc-dh-dog-api).

Stack: Java 25 · Spring Boot 4.1.0 · PostgreSQL (Flyway) · Spring Security 7
(OAuth2 resource server, Keycloak JWT) · springdoc · MapStruct + Lombok.

## Architektura (vrstvená)

```
controller/  DogApiDelegateImpl (impl generovaného delegate) + GlobalExceptionHandler
service/     DogService (business logika, @Transactional)
repository/  DogRepository (Spring Data JPA)
domain/      Dog (JPA entita, soft delete přes @SQLRestriction)
mapper/      DogApiMapper (entita → generované DTO, MapStruct)
config/      SecurityConfig, KeycloakRealmRoleConverter, OpenApiConfig
```

Generovaný `DogsApiController` (z artefaktu api) je v podbalíčku
`cz.doghotel.dog.api.server`, takže ho pokryje component scan a deleguje
na `DogApiDelegateImpl`.

## Contract-first

API se **nemění zde** — úprava endpointů/DTO jde PR do
[bc-dh-dog-api](https://github.com/Miraskazik/bc-dh-dog-api).
Tady se jen implementuje vygenerované rozhraní.

## Endpointy v1 (`/api/dogs`)

CRUD + stránkovaný seznam s hledáním dle jména/plemene (`page`, `size`, `search`)
a filtrem dle majitele (`customerId`), `DELETE` = soft delete. Pes drží jen
`customerId` (id zákazníka), ne kopii dat majitele — detail skládá BFF.
Chyby: RFC 9457 ProblemDetail. Auth: Keycloak JWT, role `ADMIN`.
Swagger UI: `/swagger-ui.html`.

## Build

```bash
mvn verify
```

Build potřebuje server stub `cz.doghotel:bc-dh-dog-api:0.1.0`:

- **lokálně:** v repu `bc-dh-dog-api` spusť `mvn install` (dá artefakt do `~/.m2`),
  nebo měj v `~/.m2/settings.xml` server `github` s tokenem (scope `read:packages`);
- **v CI:** `GITHUB_TOKEN` + `setup-java` (server-id `github`) — viz `.github/workflows/build.yml`.

## Konfigurace (env, dev defaulty)

| Proměnná | Default | Popis |
|---|---|---|
| `SERVER_PORT` | `8082` | HTTP port |
| `DOG_DB_URL` | `jdbc:postgresql://localhost:5434/dog` | PostgreSQL |
| `DOG_DB_USER` / `DOG_DB_PASSWORD` | `dog` / `dog` | DB přihlášení |
| `KEYCLOAK_ISSUER_URI` | `http://keycloak:8090/realms/dog-hotel` | JWT issuer |

Lokální běh vyžaduje PostgreSQL a Keycloak — spouští je docker-compose v
[dh-infra](https://github.com/Miraskazik/dh-infra).

## Docker

Multi-stage build; build fáze tahá api artefakt z GitHub Packages přes BuildKit secret:

```bash
docker build --secret id=m2_settings,src=$HOME/.m2/settings.xml -t bc-dh-dog .
```

## CI

- **PR / push do `main`:** `mvn verify` (`.github/workflows/build.yml`).
- **PR review:** `claude.yml` (`@claude` / automaticky) — vyžaduje secret `ANTHROPIC_API_KEY`.
- Každý PR musí projít `mvn verify` před mergem.

## Testy

Jen unit (JUnit5 + Mockito), bez Spring contextu a DB: `DogServiceTest`,
`DogApiDelegateImplTest`, `DogApiMapperTest`.
