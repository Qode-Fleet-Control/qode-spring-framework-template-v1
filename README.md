# Spring Framework template

Provisioned from [`Qode-Fleet-Control/fleet-template-v1`](https://github.com/Qode-Fleet-Control/fleet-template-v1) — the fleet
lifecycle contract (`bin/`, `fleet.conf`, deploy workflows) with a
plain Spring Framework (Spring MVC, no Spring Boot) starter laid on top.

Spring Framework 7.0 / Spring MVC on an embedded Tomcat 11, no Spring Boot, Java 21, Maven build. `Application.main` starts Tomcat and registers Spring MVC's `DispatcherServlet` programmatically (no `web.xml`); `GET /` and `GET /health` are a `@RestController`, JSON via Jackson 3.

## Origin

Hand-written — Spring Framework has no project generator without Boot (start.spring.io only makes Boot projects).
Laid out the way the Spring Framework reference teaches it: a `@Configuration @EnableWebMvc` class
(Web MVC config, "Enable MVC Configuration"), a `DispatcherServlet` around an
`AnnotationConfigWebApplicationContext` registered in code ("DispatcherServlet", Java configuration),
on Tomcat's embedded API. Versions are the ones Spring Boot 4.1.1 manages (Spring 7.0.9,
Tomcat 11.0.24, Jackson 3.1.5, JUnit Jupiter 6.0.3), so the set is known to work together.

## Verified

On 2026-10-05, on docker 29.8.2 (`maven:3.9-eclipse-temurin-21` build, `eclipse-temurin:21-jre` runtime):

- The fleet's docker runtime end to end with the migrate-docker-runtime skill's `verify.sh`
  (`bin/run` → probe `/health` → `bin/restart` → probe → `bin/stop`):
  `qode-spring-framework-template-v1 run=200 restart=200 containers_after_stop=0`
- `migrate.py audit` on the repo: `READY`.
- `mvn package` with the test suite: passed (2 MockMvc tests against the real WebConfig).

## Run it

**On the fleet** — nothing to do: the fleet clones the repo, injects `PORT` (plus
`DATABASE_URL` and the workspace's other services) and calls `bin/run`, which runs
fleet.conf's `DOCKER_BUILD_CMD` (`docker compose build`) then `DOCKER_START_CMD`
(`docker compose up`, in the foreground). The health check hits `/health`.

**With docker**, locally:

    PORT=8080 bin/run                  # the fleet's docker runtime
    docker compose up --build            # or plain compose; serves on ${PORT:-8080}
    curl http://localhost:8080/health

**Without docker** — a JDK 21 and Maven 3.9 (`mvn`) on `PATH`:

    FLEET_RUNTIME=process PORT=8080 bin/run

| step | command |
|---|---|
| install | `mvn -B -q dependency:go-offline` |
| build | `mvn -B -q package -DskipTests` |
| start | `env PORT="$PORT" java -jar target/app.jar` |

    ./bin/run       # install, build, start in the foreground
    ./bin/start     # start from existing build artifacts
    ./bin/restart   # rebuild and restart
    ./bin/stop      # stop whatever holds the port

## Serving

Listens on `0.0.0.0:$PORT` (default `8080`), read from the environment at run
time. The app is served at the root (`/`) of its own hostname
(`https://<hash>.<FLEET_APP_DOMAIN>/`), so every route, redirect and asset URL is
a plain root path. `/health` answers 200 for the fleet's health check.

## Layout

- `src/main/java/world/qode/app/Application.java` — embedded Tomcat + `DispatcherServlet`, port from `$PORT`.
- `src/main/java/world/qode/app/WebConfig.java` — `@EnableWebMvc` + component scan.
- `src/main/java/world/qode/app/HomeController.java` — `GET /` and `GET /health`.
- `src/test/java/...` — MockMvc tests against the real `WebConfig` (`@SpringJUnitWebConfig`).
- `pom.xml` — builds `target/app.jar` with its runtime jars copied to `target/lib` (the manifest names them).

## What differs from stock output

- No generator exists for plain Spring Framework; everything above is hand-written (see Origin).
- Added the fleet harness: `bin/`, `fleet.conf`, `Dockerfile`, `compose.yaml`, `.dockerignore`, `.gitignore`, `.github/workflows/`, `docs/fleet-lifecycle.md`.
