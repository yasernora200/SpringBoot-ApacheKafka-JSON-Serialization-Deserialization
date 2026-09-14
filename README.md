# Kafka Producer–Consumer Demo (Spring Boot + JSON Object)

A minimal end-to-end messaging demo built with Apache Kafka (running in KRaft mode, no ZooKeeper) and Spring Boot. Unlike the plain-string version, this demo publishes and consumes a **JSON object** (`Customer`) instead of a raw string.

It consists of two independent Spring Boot applications communicating through a Kafka topic.

| App              | Role     | Description                                              |
|-------------------|----------|------------------------------------------------------------|
| `kafka_producer`  | Producer | Exposes a REST endpoint that publishes a `Customer` JSON object to Kafka |
| `kafka_consumer`  | Consumer | Subscribes to the topic and consumes incoming `Customer` objects |

## Table of Contents
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [1. Start Kafka (KRaft Mode)](#1-start-kafka-kraft-mode)
- [2. Run the Consumer](#2-run-the-consumer)
- [3. Run the Producer](#3-run-the-producer)
- [4. Send a Test Message](#4-send-a-test-message)
- [Serialization & Deserialization](#serialization--deserialization)
- [Notes](#notes)

## Architecture

```
        HTTP POST (JSON body)
Postman ────────► Producer App ────► Topic: "javatechie-demo" ────► Consumer App
                  (port 9191)          (Kafka Broker)
```

## Prerequisites
- Java 17+
- Apache Kafka distribution downloaded locally (no separate ZooKeeper needed — KRaft mode)
- Lombok (used for `Customer` DTO getters/setters via `@Data`)
- Postman or any HTTP client, for manual testing

## 1. Start Kafka (KRaft Mode)

From the Kafka installation directory:

```bash
# Generate a Cluster UUID
$KAFKA_CLUSTER_ID=(bin/kafka-storage.sh random-uuid)

# Format the log directories
bin/windows/kafka-storage.bat format --standalone -t $KAFKA_CLUSTER_ID -c config/server.properties

# Start the broker
bin/windows/kafka-server-start.bat config/server.properties
```

Keep this terminal open — it is running the Kafka broker itself.

## 2. Run the Consumer

In a new terminal, from the `kafka_consumer` project folder:

```bash
./mvnw spring-boot:run
```

The consumer joins its configured consumer group and starts listening on the `employees` topic, deserializing every incoming message into a `Customer` object.

## 3. Run the Producer

In another terminal, from the `kafka_producer` project folder:

```bash
./mvnw spring-boot:run
```

The producer app starts on `localhost:9191` by default (configured in `application.yml`).

## 4. Send a Test Message

With the broker, consumer, and producer all running, send a **POST** request with a JSON body:

```
POST http://localhost:9191/producer-app/publish
Content-Type: application/json
```

Body:
```json
{
  "id": 123,
  "name": "John",
  "email": "john@example.com",
  "contactNumber": "9876543210"
}
```

Expected response:
```
200 OK
```
