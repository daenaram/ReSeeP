# ReSeeP backend

A small JAX-RS + JPA backend for the ReSeeP recipe/pantry tracker, deployed
as a WAR on Payara. It's built to be called by the `index.html` frontend
without any changes on that side, once you point `API_BASE` at this app.

## Software required

- JDK 21 (or 25)
- Apache Maven 3.9.x
- Payara Server 6.2025.11 (JDK 21) or 7.2026.2 (JDK 25)
- MySQL Connector/J 9.7, placed in `<Payara>/glassfish/domains/domain1/lib`
- An AWS MySQL database (`comp713_w3`) with a JDBC connection pool and JDBC
  resource already created in Payara (see the lab setup notes)

## 1. Create the database tables

Run `db/schema.sql` against `comp713_w3`. It creates three tables
(`ingredient`, `recipe`, `recipe_ingredient`) and inserts the same sample
data the frontend uses as its offline demo, so the two line up once
connected.

```
mysql -h <your-aws-host> -u comp713_student -p comp713_w3 < db/schema.sql
```

## 2. Point the persistence unit at your JDBC resource

Open `src/main/resources/META-INF/persistence.xml` and change:

```xml
<jta-data-source>jdbc/ReSeePPool</jta-data-source>
```

to the JNDI name of the JDBC Resource you created in Payara (Resources >
JDBC > JDBC Resources) for the `comp713_w3` connection pool.

## 3. Build

```
mvn clean package
```

This produces `target/reseep.war`.

## 4. Deploy

Deploy `reseep.war` to Payara — either drag it into the admin console
(Applications > Deploy) or:

```
asadmin deploy target/reseep.war
```

With the default `finalName` of `reseep`, the app is served at
`http://localhost:8080/reseep`, so the API root is:

```
http://localhost:8080/reseep/api
```

## 5. Point the frontend at it

In `index.html`, set:

```js
const API_BASE = "http://localhost:8080/reseep/api";
```

Reload the page — the status line under the tabs should switch from
"showing sample data" to "Connected to …", and the same recipes/pantry
items from `schema.sql` should appear (matching the offline demo data).

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
