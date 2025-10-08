# Serverless Workflow Orchestrator

A lightweight analog of Temporal.io / AWS Step Functions built with Spring Boot that executes YAML-described workflow processes and manages their state in PostgreSQL.

## 🎯 Project Overview

This project demonstrates expertise in asynchronous flows, DSL interpretation, and state management by providing a reactive workflow orchestration engine that can execute complex multi-step processes defined in YAML format.

## 🏗️ Architecture

The project is structured as a multi-module Maven application:

- **workflow-storage** - Data access layer with PostgreSQL entities and repositories
- **workflow-engine** - Core workflow engine with YAML parsing and step execution
- **workflow-api** - REST API with WebSocket support for real-time monitoring
- **workflow-ui** - React-based web interface for workflow monitoring and management

## 🚀 Features

- ✅ YAML-based workflow definition
- ✅ Reactive step execution with backpressure handling
- ✅ State persistence and recovery
- ✅ Real-time monitoring via WebSocket
- ✅ Modern React web interface for workflow management
- ✅ Extensible step executor framework
- ✅ Built-in step types: HTTP, Delay, Condition, Log
- ✅ Expression evaluation with Spring SpEL
- ✅ Comprehensive error handling and retry logic
- ✅ Interactive dashboard with workflow statistics
- ✅ Live workflow execution tracking

## 🛠️ Technology Stack

- **Java 21** - Latest LTS with modern language features
- **Spring Boot 3** - Reactive web framework
- **Spring WebFlux** - Non-blocking I/O
- **Project Reactor** - Reactive programming
- **PostgreSQL + R2DBC** - Reactive database access
- **SnakeYAML** - YAML parsing
- **WebSocket** - Real-time updates
- **React 18** - Modern frontend framework
- **Tailwind CSS** - Utility-first CSS framework
- **Docker Compose** - Local development environment

## 📋 Quick Start

### Prerequisites
- Java 21+
- Docker & Docker Compose
- Maven 3.8+
- Node.js 18+ (for UI development)

### 1. Clone and Build
```bash
git clone <repository-url>
cd Serverless-Workflow-Orchestrator-PoC
mvn clean install
```

### 2. Start Infrastructure
```bash
docker-compose up -d postgres
```

### 3. Run Application
```bash
cd workflow-api
mvn spring-boot:run
```

### 4. Access Web Interface
Open your browser and navigate to: **http://localhost:8080**

### 5. Test Workflow Execution

#### Via Web Interface:
1. Open http://localhost:8080 in your browser
2. Click "Start Workflow" button
3. Select a workflow definition (sample-workflow or data-processing-workflow)
4. Provide input JSON
5. Monitor execution in real-time

#### Via API:
```bash
# Start a workflow
curl -X POST http://localhost:8080/api/workflows/start/sample-workflow \
  -H "Content-Type: application/json" \
  -d '{"input": "{\"userId\": 123}"}'

# Check workflow status
curl http://localhost:8080/api/workflows/{workflow-instance-id}

# View workflow steps
curl http://localhost:8080/api/workflows/{workflow-instance-id}/steps
```

## 📄 Workflow Definition Format

Workflows are defined in YAML format with the following structure:

```yaml
name: example-workflow
version: 1.0.0
variables:
  timeout: 30s
  maxRetries: 3

steps:
  - id: fetch-data
    type: http
    method: GET
    url: https://api.example.com/data
    next: validate-response

  - id: validate-response
    type: condition
    expression: "#result['status'] == 200"
    onTrue: process-data
    onFalse: handle-error

  - id: process-data
    type: delay
    duration: 2s
    next: save-result

  - id: save-result
    type: http
    method: POST
    url: https://api.example.com/save
    body:
      data: "#result"
      timestamp: "${System.currentTimeMillis()}"

  - id: handle-error
    type: log
    message: "API call failed: ${result}"
    level: error
```

## 🔧 Supported Step Types

### HTTP Step
```yaml
- id: api-call
  type: http
  method: GET|POST|PUT|DELETE
  url: https://example.com/api
  body: { "key": "value" }  # For POST/PUT requests
```

### Delay Step
```yaml
- id: wait
  type: delay
  duration: 5s  # Supports s, m, h units
```

### Condition Step
```yaml
- id: check-result
  type: condition
  expression: "#result['status'] == 200"
  onTrue: success-step
  onFalse: error-step
```

### Log Step
```yaml
- id: log-info
  type: log
  message: "Processing complete: ${result}"
  level: info  # debug, info, warn, error
```

## 🌐 API Endpoints

### Web Interface
- `GET /` - Main dashboard interface
- `GET /workflow/{id}` - Detailed workflow view

### Workflow Management
- `POST /api/workflows/start/{workflowName}` - Start workflow execution
- `GET /api/workflows/{instanceId}` - Get workflow instance status
- `GET /api/workflows` - List all workflow instances
- `GET /api/workflows/{instanceId}/steps` - Get workflow step history

### Workflow Definitions
- `POST /api/definitions` - Create workflow definition
- `GET /api/definitions` - List all workflow definitions

### Real-time Updates
- `WebSocket /ws/workflow-updates` - Real-time workflow status updates

## 🧪 Testing

### Backend Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Frontend Development
```bash
cd workflow-ui/src/main/webapp
npm install
npm start  # Runs on http://localhost:3000 with proxy to backend
```

### Manual Testing with Sample Workflows
The application includes pre-loaded sample workflows:
- `sample-workflow` - Basic HTTP → Delay → Condition flow
- `data-processing-workflow` - Data fetching and processing example

## 🐳 Docker Deployment

### Build and Run with Docker Compose
```bash
docker-compose up --build
```

This will start:
- PostgreSQL database (port 5432)
- Workflow API with embedded React UI (port 8080)

## 🔍 Monitoring

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Web Dashboard
Visit http://localhost:8080 for comprehensive monitoring including:
- Real-time workflow statistics
- Execution timelines
- Error tracking
- Performance metrics

### WebSocket Connection (JavaScript)
```javascript
const ws = new WebSocket('ws://localhost:8080/ws/workflow-updates');
ws.onmessage = (event) => {
  const update = JSON.parse(event.data);
  console.log('Workflow update:', update);
};
```

## 🏗️ Extending the Framework

### Adding Custom Step Types

1. Implement `StepExecutor` interface:
```java
@Component
public class CustomStepExecutor implements StepExecutor {
    @Override
    public String getStepType() {
        return "custom";
    }
    
    @Override
    public Mono<Object> execute(StepDefinition step, ExecutionContext context) {
        // Custom logic here
        return Mono.just(result);
    }
}
```

2. The executor will be automatically registered via Spring's component scanning.

## 📊 Performance Characteristics

- **Reactive Architecture**: Non-blocking I/O throughout the stack
- **Backpressure Handling**: Built-in flow control with Project Reactor
- **State Persistence**: Workflow state saved after each step
- **Fault Tolerance**: Automatic retry logic and error handling
- **Scalability**: Horizontally scalable with proper database configuration
- **Modern UI**: Efficient React components with optimized rendering
- **Real-time Updates**: WebSocket-based live data streaming

## 🔐 Security Considerations

- Input validation on all API endpoints
- SQL injection protection via R2DBC parameterized queries
- Expression evaluation sandboxing with Spring SpEL
- WebSocket connection management and rate limiting
- XSS protection in React components
- CSRF protection for state-changing operations

## 📚 Further Development

Potential enhancements:
- [ ] Workflow versioning and migration
- [ ] Parallel step execution
- [ ] Workflow scheduling (cron-like)
- [ ] Metrics and observability integration
- [ ] GraphQL API
- [x] Web UI for workflow visualization ✅ **COMPLETED**
- [ ] Workflow templates and libraries
- [ ] Role-based access control
- [ ] Workflow export/import functionality
- [ ] Advanced analytics and reporting
- [ ] Mobile app for workflow monitoring

## 🎯 Project Structure

```
serverless-workflow-orchestrator/
├── workflow-storage/          # Data persistence layer
│   ├── entities/             # PostgreSQL entities
│   └── repositories/         # R2DBC reactive repositories
├── workflow-engine/          # Core workflow execution engine  
│   ├── interpreters/         # YAML parsing
│   ├── executors/           # Step execution (HTTP, Delay, Condition, Log)
│   └── models/              # Workflow definition models
├── workflow-api/            # REST API & WebSocket endpoints
│   ├── controllers/         # HTTP endpoints
│   ├── services/           # Business logic
│   └── websocket/          # Real-time updates
├── workflow-ui/             # React web interface
│   └── src/main/webapp/     # React application
│       ├── src/components/  # React components
│       ├── public/         # Static assets
│       └── package.json    # NPM dependencies
└── docker-compose.yml      # Local development setup
```
