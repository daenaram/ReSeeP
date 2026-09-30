# ReSeeP backend

A small JAX-RS + JPA backend for the ReSeeP recipe/pantry tracker, deployed
as a WAR on Payara. It's built to be called by the `index.html` frontend
without any changes on that side, once you point `API_BASE` at this app.

## Software required

- JDK 21 (or 25)
- Apache Maven 3.9.x
- Payara Server 6.2025.11 (JDK 21) or 7.2026.2 (JDK 25)
- MySQL Connector/J 9.7, placed in `<Payara>/glassfish/domains/domain1/lib`

## 1. Point the persistence unit at your JDBC resource

Open `src/main/resources/META-INF/persistence.xml` and confirm:

```xml
<jta-data-source>jdbc/ReSeePPool</jta-data-source>
```
There is no separate SQL script to run by hand — the three tables
(`ingredient`, `recipe`, `recipe_ingredient`) and the sample data are created
automatically the first time the app deploys, by `DbInitializer.java` (see
below). If your database already has empty tables from an earlier attempt,
that's fine — `DbInitializer` only inserts sample data if the `ingredient`
table is currently empty, so it won't duplicate anything.

## 2. Build

```
mvn clean package
```

This produces `target/reseep.war`.

## 3. Deploy

Deploy `reseep.war` to Payara — either drag it into the admin console
(Applications > Deploy) or:

```
asadmin deploy --force=true target/reseep.war
```

The app is served at `http://localhost:8080/reseep`, so the API root is:

```
http://localhost:8080/reseep/api
```

Watch `server.log` for the line `[DbInitializer] Schema check complete.` to
confirm the tables were created (or already existed) without errors.

## 4. Point the frontend at it

In `index.html`, set:

```js
const API_BASE = "http://localhost:8080/reseep/api";
```

Reload the page — the status line under the tabs should switch from
"showing sample data" to "Connected to …", and the same sample recipes and
pantry items the frontend uses offline should now be coming from the
database instead.

## Resetting the data

Visiting `http://localhost:8080/reseep/reset` in a browser clears every row
from all three tables (see `ResetServlet.java`). Useful before a clean demo
run. If you redeploy afterwards, `DbInitializer` will reseed the sample data
automatically, since it'll find the `ingredient` table empty again.

## Endpoints

| Method | Path                | Description                                   |
|--------|---------------------|------------------------------------------------|
| GET    | `/api/recipes`      | List all recipes with their ingredient lines   |
| GET    | `/api/recipes/{id}` | Get one recipe                                 |
| POST   | `/api/recipes`      | Create a recipe                                |
| PUT    | `/api/recipes/{id}` | Replace a recipe's fields and ingredient list  |
| DELETE | `/api/recipes/{id}` | Delete a recipe                                |
| GET    | `/api/pantry`       | List all pantry ingredients                    |
| POST   | `/api/pantry`       | Add a new pantry ingredient                    |
| PUT    | `/api/pantry/{id}`  | Update a pantry ingredient's quantity on hand  |

All request/response bodies are JSON. Validation is intentionally simple:
a blank recipe/ingredient name, a negative quantity, or an unknown
`ingredientId` on a recipe line all return `400 Bad Request` with a short
JSON error message. Requesting a recipe/ingredient id that doesn't exist
returns `404 Not Found`.

## Known limitations

- No authentication — anyone who can reach the API can read/write everything.
- `PUT /recipes/{id}` replaces the whole ingredient list rather than
  patching individual lines; simplest correct behaviour for this scope.
- CORS is wide open (`Access-Control-Allow-Origin: *`) in `CorsFilter.java`,
  which is convenient for local development/marking but not something
  you'd ship as-is.
- Schema creation and seeding happen in application code (`DbInitializer`)
  rather than a proper migration tool — fine for coursework, but a real
  deployment would use something like Flyway instead.
- `/reset` has no confirmation step and no access control — anyone who
  knows the URL can wipe the data instantly. Acceptable for a local dev
  convenience, not something to expose publicly.