# Kafka Producer–Consumer Demo (Spring Boot + object or List of JSON Objects)

A minimal end-to-end messaging demo built with Apache Kafka (running in KRaft mode, no ZooKeeper) and Spring Boot. This variant publishes and consumes a **`List<Customer>`** (a batch of objects in a single message) instead of a single object or a plain string.

It consists of two independent Spring Boot applications communicating through a Kafka topic.

| App              | Role     | Description                                                        |
|-------------------|----------|----------------------------------------------------------------------|
| `kafka_producer`  | Producer | Exposes a REST endpoint that publishes a `List<Customer>` as one Kafka message |
| `kafka_consumer`  | Consumer | Subscribes to the topic and consumes the incoming list of `Customer` objects |

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
        HTTP POST (JSON array body)
Postman ────────► Producer App ────► Topic: "javatechie-demo" ────► Consumer App
                  (port 9191)          (Kafka Broker)
```

Each HTTP request publishes **one Kafka message whose value is a JSON array** of customers — not one message per customer.

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

The consumer joins its configured consumer group and starts listening on the `students` topic, deserializing every incoming message into a `List<Customer>`.

## 3. Run the Producer

In another terminal, from the `kafka_producer` project folder:

```bash
./mvnw spring-boot:run
```

The producer app starts on `localhost:9191` by default (configured in `application.yml`).

## 4. Send a Test Message

With the broker, consumer, and producer all running, send a **POST** request with a JSON **array** body:

```
POST http://localhost:9191/producer-app/publish
Content-Type: application/json
```

Body:
```json
[
  {
    "id": 1,
    "name": "John",
    "email": "john@example.com",
    "contactNumber": "9876543210"
  },
  {
    "id": 2,
    "name": "Sarah",
    "email": "sarah@example.com",
    "contactNumber": "9123456780"
  }
]
```

Expected response:
```
200 OK
```

The entire list should immediately appear — deserialized as `List<Customer>` — in the consumer's terminal log in a single log statement, not one log line per customer.

Instead of `application.yml`, the same serializer/deserializer setup can be defined in code via `@Configuration` classes (`ProducerConfig`/`ConsumerConfig` beans) — required for advanced cases like SSL/certificate setup on a secured cluster or setting consumer concurrency levels.

## Notes
- Topic name: `students`
- DTO class: `Customer` (`id`, `name`, `email`, `contactNumber`) — annotated with Lombok's `@Data`
- The same `Customer` class must exist in **both** producer and consumer projects (or be extracted into a shared/common module)
- The entire list is sent and read as **one Kafka message** — Kafka has no built-in awareness that the payload contains multiple customers, it's just one JSON array as far as the broker is concerned
- The publish endpoint uses `POST` with a JSON array request body (`@RequestBody List<Customer>`)
- Startup order matters: Kafka broker → Consumer → Producer, so the consumer is already subscribed and ready before any message is sent
