# 🏦 Personal Banking Assistant – Backend

This is the backend of a personal banking assistant built using **Spring Boot**, integrating with **OpenAI** for natural language conversation. It serves Slovak-speaking clients and communicates over a simple REST API.

---

## 🚀 Features

- 🧠 AI assistant powered by OpenAI
- 💬 REST API endpoint for chat
- 📅 Transaction and client database model
- 🔐 Supports conversation history per client
- 🗣️ Slovak language assistant prompts

---

## 🛠️ Technologies

- Java 17+
- Spring Boot 3.x
- Maven
- Hibernate / Spring Data JPA
- PostgreSQL
- OpenAI GPT-4 via [`openai-gpt3-java`](https://github.com/TheoKanning/openai-java)

---

## 📦 Getting Started

### Prerequisites

- Java 17+
- PostgreSQL running
- OpenAI API Key
- Maven 3+

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/personal-banker-backend.git
cd personal-banker-backend
```

### 2. Set Up Environment Variables

Create a file called `.env` or configure your `application.yml`:

```yaml
openai:
  api-key: sk-xxx

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/finance_db
    username: your_user
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### 3. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

App will be running at: `http://localhost:8080`

---

## 📡 API Reference

### 🔄 POST `/api/assistant/chat`

Send a message to the assistant and get a reply.

#### Request

```json
{
  "clientId": 1,
  "message": "Zobraz moje posledné transakcie."
}
```

#### Response

```json
{
  "role": "assistant",
  "message": "Tu sú vaše posledné transakcie za mesiac jún..."
}
```

---

## 🧪 Development Notes

- Chat messages are processed with context, including client name and past conversation.
- Data model includes:
  - `Client` (id, name, email, joinedDate)
  - `Transaction` (id, amount, category, date, description, clientId)
- You can customize assistant behavior in `ChatService::buildSystemMessage`.

---

## 🧠 OpenAI Integration

OpenAI client is configured using:

```java
OpenAiService openAiService = new OpenAiService(apiKey, Duration.ofSeconds(60));
```

Chat prompts are composed with both system and user messages to maintain context.

---

## 🤝 Contributions

Feel free to open issues or PRs. Help make this assistant smarter and more helpful for Slovak-speaking clients!

---

## 📄 License

MIT – Do whatever you want, but attribution is appreciated.