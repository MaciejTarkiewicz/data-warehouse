# Simple Data Warehouse

## Technologies
<ul>
<li>Kotlin</li>
<li>Micronaut</li>
<li>MongoDB</li>
<li>Spock</li>
<li>Gradle</li>
<li>Docker</li>
</ul>

## Installation

Set up a MongoDB instance with Docker
```bash
docker run --name local-mongo -p 27017:27017 -d mongo
 ```

## Run application and tests:

```bash
./gradlew run

./gradlew test
```

## First, we need to feed MongoDB with csv data

```bash
 curl --location --request POST 'http://localhost:8080/api/metrics/feed-data?filename=data-source' \
--data ''
```

## Example Query:

```bash
curl --location 'http://localhost:8080/api/metrics/query?metrics=clicks&campaign=Adventmarkt%20Touristik&groupedBy=campaign%2Cdatasource' \
--data ''
```



