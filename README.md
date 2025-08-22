
# Digital Data Distribution Display Platform

A Spring Boot microservices-based backend system for DDDD Platform.  
基于Spring Boot微服务的数字数据分发显示平台后端系统。

---

## 📦 Project Structure | 项目结构


Campus-Digital-Signage-Platform/
├── auth-service         # User authentication and authorization | 用户认证与权限管理
├── content-service      # Media upload and metadata management | 内容上传与元数据管理
├── device-service       # Device registration and control | 设备注册与控制
├── schedule-service     # Time-based content scheduling | 播放调度与排程
└── pom.xml              # Parent project configuration | 父项目配置文件

````

Each module is an independent Spring Boot application.  
每个模块都是独立的 Spring Boot 应用程序。

---

## 🚀 Tech Stack | 技术栈

- Java 21  
- Spring Boot 3.1.9  
- Maven  
- MySQL 8.x  
- Spring Security, Spring Data JPA  
- SonarQube（可选代码质量检查）  
- JUnit 5（单元测试）

---

## ⚙️ Getting Started | 快速开始

### ✅ Prerequisites | 前置条件

- Java 21  
- Maven 3.8+  
- MySQL 数据库（本地或云端）  
- 推荐使用 IntelliJ IDEA

### 📥 Clone the Repository | 克隆项目代码

```bash
git clone https://github.com/jyc0421/Campus-Digital-Signage-Platform.git
cd Campus-Digital-Signage-Platform
````

### 🛠️ Configure the Database | 配置数据库连接

Each module contains its own `application.yml`. Example:
每个模块都有独立的配置文件 `application.yml`，示例：

```yaml
spring:
  datasource:
    url: jdbc:mysql://<your-host>:3306/dddd_platform
    username: <your-username>
    password: <your-password>
    driver-class-name: com.mysql.cj.jdbc.Driver
```

> 默认配置已上传仓库，仅当本地运行时需手动修改连接信息。

---

## ▶️ Run the Services | 启动服务

All services can be started independently.
所有服务均可独立运行，无需顺序依赖。

**Option 1: IntelliJ IDEA**
在 IDEA 中运行模块下的 `XXXApplication.java` 主类。

**Option 2: Command Line | 命令行方式**

```bash
cd auth-service
mvn spring-boot:run
```

Repeat for each module | 对每个模块重复上述操作。

---

## ✅ Running Tests | 执行测试与生成覆盖率报告

Execute the following in any module directory:
在任意模块目录下运行：

```bash
mvn clean verify
```

Coverage report will be generated at:
测试覆盖率报告路径：

```
target/site/index.html
```

Open in browser to view. | 用浏览器打开查看即可。

---

```

如果你还需要我协助生成 `README.md` 文件下载版本、转为 PDF、或为每个模块单独写一份说明文档，也可以继续告诉我。
```
