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
    
    // Spring AI OpenAI Starter (Dung de ket noi OpenRouter Cloud)
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
```

### b. File `application-local.properties` (Môi trường Local - Ollama)
Đường dẫn: `src/main/resources/application-local.properties`

```properties
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=qwen2.5-coder:7b
```

### c. File `application-cloud.properties` (Môi trường Cloud - OpenRouter / Gemini)
Đường dẫn: `src/main/resources/application-cloud.properties`

```properties
spring.ai.openai.api-key=${OPENROUTER_API_KEY}
spring.ai.openai.base-url=https://openrouter.ai/api/v1
spring.ai.openai.chat.options.model=google/gemini-2.5-flash
```

---

## 3. HƯỚNG DẪN CHẠY ỨNG DỤNG Ở PROFILE CLOUD TỪ DÒNG LỆNH (CLI)

### Cách 1: Chạy trực tiếp qua Gradle BootRun (Khuyên dùng trong phát triển)

- Trên Windows (PowerShell):
```powershell
$env:OPENROUTER_API_KEY="sk-or-v1-your-actual-api-key"
gradle bootRun --args='--spring.profiles.active=cloud'
```

- Trên Windows (CMD):
```cmd
set OPENROUTER_API_KEY=sk-or-v1-your-actual-api-key
gradle bootRun --args="--spring.profiles.active=cloud"
```

- Trên Linux / macOS (Bash):
```bash
export OPENROUTER_API_KEY="sk-or-v1-your-actual-api-key"
./gradlew bootRun --args='--spring.profiles.active=cloud'
```

### Cách 2: Chạy file JAR đã đóng gói (Môi trường Staging / Production)

1. Đóng gói dự án thành file JAR:
```bash
gradle build -x test
```

2. Chạy ứng dụng với tham số kích hoạt profile cloud:
```bash
java -Dspring.profiles.active=cloud -jar build/libs/ss2-bt4-0.0.1-SNAPSHOT.jar
```
(Hoặc truyền qua System Property / Command Line Argument: `java -jar build/libs/ss2-bt4-0.0.1-SNAPSHOT.jar --spring.profiles.active=cloud`)

---

## 4. HƯỚNG DẪN CÁC BƯỚC THỰC HIỆN THỦ CÔNG DÀNH CHO HỌC VIÊN

Do máy của bạn mới cài đặt Ollama và chưa chạy service, dưới đây là các bước bạn cần thực hiện:

### Bước 1: Khởi động Ollama và tải Model Qwen (Dành cho Local)
1. Mở một cửa sổ Terminal / PowerShell mới và chạy lệnh khởi động Ollama service (nếu chưa chạy nền):
   ```bash
   ollama serve
   ```
2. Mở một cửa sổ Terminal khác và tải mô hình Qwen 2.5 Coder 7B:
   ```bash
   ollama pull qwen2.5-coder:7b
   ```
   *(Lưu ý: Mô hình 7B dung lượng khoảng 4.7 GB, cần có kết nối mạng ổn định để tải về).*

### Bước 2: Chạy ứng dụng ở chế độ Local (Mặc định)
```bash
cd ss2-bt4
gradle bootRun
```
- Khi ứng dụng khởi động thành công tại cổng 8080, mở trình duyệt hoặc Postman truy cập:
  `http://localhost:8080/api/chat?prompt=Xin chao`
- Ứng dụng sẽ gửi prompt đến Ollama chạy local tại cổng 11434.

### Bước 3: Chạy ứng dụng ở chế độ Cloud (OpenRouter / Gemini)
1. Đăng ký tài khoản và lấy API Key tại [OpenRouter](https://openrouter.ai/keys).
2. Thiết lập biến môi trường và chạy profile cloud:
   ```powershell
   $env:OPENROUTER_API_KEY="sk-or-v1-xxx..."
   gradle bootRun --args='--spring.profiles.active=cloud'
   ```
- Mở trình duyệt truy cập:
  `http://localhost:8080/api/chat?prompt=Xin chao`
- Lúc này ứng dụng sẽ tự động chuyển sang gọi model `google/gemini-2.5-flash` qua OpenRouter.
