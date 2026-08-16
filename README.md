# BÀI 4: THỰC HÀNH CẤU HÌNH — CÀI ĐẶT HYBRID AI RUNTIME

## 1. NỘI DUNG FILE BUILD.GRADLE

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.3.5'
    id 'io.spring.dependency-management' version '1.1.6'
}

group = 'com.rikkei'
version = '0.0.1-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven { url 'https://repo.spring.io/milestone' }
}

ext {
    set('springAiVersion', "1.0.0-M5")
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    
    // Spring AI Ollama Starter (Local AI Runtime)
    implementation 'org.springframework.ai:spring-ai-ollama-spring-boot-starter'
    
    // Spring AI OpenAI Starter (Dung de ket noi OpenRouter / Gemini Cloud qua OpenAI-compatible API)
    implementation 'org.springframework.ai:spring-ai-openai-spring-boot-starter'
    
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.ai:spring-ai-bom:${springAiVersion}"
    }
}

tasks.named('test') {
    useJUnitPlatform()
}
```

---

## 2. NỘI DUNG CÁC FILE CẤU HÌNH PROPERTIES

### a. File `application.properties` (Cấu hình chung & Active Profile mặc định)
Đường dẫn: `src/main/resources/application.properties`

```properties
spring.application.name=hybrid-ai-runtime
spring.profiles.active=local

# Tat cac modality khong dung den de tranh conflict/missing keys
spring.ai.openai.embedding.enabled=false
spring.ai.openai.image.enabled=false
spring.ai.openai.audio.transcription.enabled=false
spring.ai.openai.audio.speech.enabled=false
spring.ai.openai.moderation.enabled=false
spring.ai.ollama.embedding.enabled=false
```

### b. File `application-local.properties` (Môi trường Local - Ollama)
Đường dẫn: `src/main/resources/application-local.properties`

```properties
# Local Profile: Ollama
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=qwen2.5-coder:7b

# Tat OpenAI khi chay local
spring.ai.openai.chat.enabled=false
spring.ai.openai.embedding.enabled=false
spring.ai.openai.api-key=local-dummy-key
```

### c. File `application-cloud.properties` (Môi trường Cloud - Gemini / OpenRouter)
Đường dẫn: `src/main/resources/application-cloud.properties`

```properties
# Cloud Profile: Gemini / OpenRouter qua OpenAI-compatible API
spring.ai.openai.enabled=true
spring.ai.openai.chat.enabled=true
spring.ai.openai.api-key=${GEMINI_API_KEY:${OPENROUTER_API_KEY}}
spring.ai.openai.base-url=${AI_BASE_URL:https://generativelanguage.googleapis.com/v1beta/openai/}
spring.ai.openai.chat.options.model=${AI_MODEL:gemini-2.5-flash}

# Tat Ollama khi chay cloud
spring.ai.ollama.chat.enabled=false
spring.ai.ollama.embedding.enabled=false
```

---

## 3. HƯỚNG DẪN CHẠY ỨNG DỤNG Ở PROFILE CLOUD TỪ DÒNG LỆNH (CLI)

### Chạy trực tiếp qua Gradle BootRun:

- Trên Windows (PowerShell):
```powershell
$env:GEMINI_API_KEY="your-gemini-api-key"
./gradlew bootRun --args='--spring.profiles.active=cloud'
```

- Hoặc nếu dùng OpenRouter Key:
```powershell
$env:OPENROUTER_API_KEY="sk-or-v1-your-openrouter-key"
$env:AI_BASE_URL="https://openrouter.ai/api/v1"
$env:AI_MODEL="google/gemini-2.5-flash"
./gradlew bootRun --args='--spring.profiles.active=cloud'
```

---

## 4. HƯỚNG DẪN KIỂM THỬ ENDPOINT

1. Khởi động ứng dụng (Local hoặc Cloud).
2. Mở trình duyệt hoặc PowerShell gọi API:
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/chat?prompt=Xin chao"
```
Response JSON:
```json
{
  "status": "success",
  "prompt": "Xin chao",
  "response": "Xin chào! Tôi có thể giúp gì cho bạn hôm nay?"
}
```
