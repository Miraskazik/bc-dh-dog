# CLAUDE.md — bc-dh-dog

## Role repa

Business služba (Typ B) systému **Dog Hotel** — evidence psů a jejich profilů,
čisté CRUD se soft delete, **žádná Kafka**. Implementuje server stub z kontraktu
`bc-dh-dog-api`.

Stack: Java 25 · Spring Boot 4.1.0 (Spring Framework 7, Jakarta EE 11, Jackson 3,
Spring Security 7) · Maven · PostgreSQL (Flyway) · Keycloak · MapStruct + Lombok.

## Související repa

- [bc-dh-dog-api](https://github.com/Miraskazik/bc-dh-dog-api) — kontrakt
  (openapi.yaml, generovaný klient + server stub). **Zdroj pravdy pro API.**
- [dh-infra](https://github.com/Miraskazik/dh-infra) — docker-compose (PostgreSQL, Keycloak), architektura
- [bc-dh-auth](https://github.com/Miraskazik/bc-dh-auth) — Keycloak realm (JWT issuer)

## Contract-first (tvrdé pravidlo)

Změna API (endpointy, DTO) se dělá **jen v `bc-dh-dog-api`** (PR úpravou
`openapi.yaml`), nikdy ne přímo zde. Po vydání nové verze api artefaktu se tady
zvedne `dog-api.version` v `pom.xml` a doimplementuje delegate.

## Architektura a konvence

- **Vrstvená:** `controller` (delegate impl + `@RestControllerAdvice`) / `service`
  (business logika, `@Transactional`) / `repository` (Spring Data JPA) / `domain`
  (JPA entity) / `mapper` (MapStruct entita→DTO) / `config`.
- **Entity:** `@Getter` + `@NoArgsConstructor(PROTECTED)`, žádné settery — změny
  přes behaviorální metody. `GenerationType.UUID`, `@Enumerated(STRING)`.
- **Soft delete:** `deletedAt` + `@SQLRestriction("deleted_at is null")` — běžné
  dotazy smazané filtrují; DELETE endpoint jen nastaví příznak.
- **Reference na majitele:** pes drží `customerId` (UUID), žádnou kopii dat
  zákazníka; jméno majitele skládá BFF.
- **Mapping:** MapStruct pro entita→DTO; request→entita přes factory/behavior metody
  na entitě (aby entita zůstala zapouzdřená). Lombok jen na boilerplate.
- **Chyby:** RFC 9457 ProblemDetail (`spring.mvc.problemdetails.enabled=true`).
- **Security:** OAuth2 resource server, Spring Security 7 **lambda DSL**. Keycloak
  role jsou pod `realm_access.roles` → `KeycloakRealmRoleConverter` na `ROLE_*`.
  Jediná role `ADMIN`.
- **Bez read modelů:** služba nedrží kopie cizích dat; agregaci dělá BFF.

## Pozor na Spring Boot 4 (ověřovat, nepsat z paměti)

Stack je novější než trénovací data. Ověřuj přes Context7 / docs.spring.io / Initializr.
Přejmenované artefakty oproti Boot 3: `spring-boot-starter-web` →
**`-webmvc`**; `spring-boot-starter-oauth2-resource-server` →
**`-security-oauth2-resource-server`**; Flyway přes **`spring-boot-starter-flyway`**
+ `flyway-database-postgresql`; test starter rozštěpen (`-webmvc-test`, `-data-jpa-test`…);
testy: `@MockitoBean` (ne `@MockBean`). Jackson 3 = `tools.jackson`, `JsonMapper` bean,
`@JacksonComponent`.

## Kafka

Tato služba **nepublikuje ani nekonzumuje** žádné eventy. (Konvence topiců systému:
`dh.<domena>.<udalost>`, zdroj eventů je `bc-dh-reservation`.)

## Příkazy

- `mvn verify` — kompilace (vč. MapStruct/Lombok processingu) + unit testy.
- Build potřebuje `bc-dh-dog-api` z GitHub Packages (lokálně `mvn install`
  api repa, v CI `GITHUB_TOKEN`).

## Git workflow

- default branch `main`; bootstrap = jeden initial commit; další práce ve feature
  branchích `feat/…`, `fix/…`, `chore/…` → PR → merge.
- **Conventional Commits**, ucelené smysluplné celky práce.
- **Každý PR musí projít `mvn verify` před mergem.** PR review přes `claude.yml`.

## Open-source / free (tvrdé pravidlo)

Jen OSI-approved licence: Apache-2.0, MIT, BSD, EPL, MPL, LGPL. Zakázané bez souhlasu:
placené služby, omezené free tiery, BSL/BUSL, SSPL, Elastic License, Confluent
Community License, „source available". U každé nové dependency uvádět licenci.
Když free varianta neexistuje nebo je licence nejasná — **zastavit, popsat, počkat**.
