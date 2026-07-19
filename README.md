# 🚀 STRATA — Enterprise Object Storage Platform

> **A production-inspired S3-compatible object storage platform built with Spring Boot, MinIO, MongoDB, JWT Authentication, and React.**

STRATA demonstrates how modern cloud storage systems separate **object storage** from **metadata**, enforce **role-based security**, and provide **object lifecycle management** through a clean REST API and intuitive React dashboard.

<p align="center">

![Java](https://img.shields.io/badge/Java-17-red?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?logo=springsecurity&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-Database-47A248?logo=mongodb&logoColor=white)
![MinIO](https://img.shields.io/badge/MinIO-S3-Compatible-C72E49?logo=minio&logoColor=white)
![React](https://img.shields.io/badge/React-Vite-61DAFB?logo=react&logoColor=black)
![License](https://img.shields.io/badge/License-MIT-blue)

</p>

---

# 📖 Overview

STRATA is an enterprise-style media storage service inspired by cloud object storage platforms such as **Amazon S3**.

Instead of storing files directly on the filesystem, STRATA separates:

- **Object Storage** → MinIO (S3-compatible)
- **Metadata Storage** → MongoDB
- **Authentication & Authorization** → Spring Security + JWT
- **Frontend** → React + Vite

This architecture mirrors how real-world storage systems manage billions of objects while maintaining fast search capabilities and secure access control.

---

# 🏗️ System Architecture

```text
                        JWT Authenticated REST API
┌─────────────────────┐  ─────────────────────────────▶  ┌──────────────────────────┐
│                     │                                  │                          │
│   React Dashboard   │ ◀─────────────────────────────── │    Spring Boot API       │
│     (Vite)          │           JSON Responses         │ Spring Security + JWT    │
│                     │                                  │                          │
└─────────────────────┘                                  └──────────────┬───────────┘
                                                                        │
                                            ┌───────────────────────────┴──────────────────────────┐
                                            │                                                      │
                                            ▼                                                      ▼
                                  ┌──────────────────────┐                              ┌──────────────────────┐
                                  │      MongoDB        │                              │        MinIO         │
                                  │  File Metadata      │                              │   Object Storage     │
                                  │ Users, Checksums    │                              │    S3 Compatible     │
                                  └──────────────────────┘                              └──────────────────────┘
```

---

# ✨ Features

## 📦 Object Storage

- Create, list and delete buckets
- Upload & download files
- Rename objects
- Move objects between buckets
- Copy objects
- SHA-256 checksum generation
- ETag generation
- Object integrity verification

---

## 🔄 Object Lifecycle

Supports enterprise lifecycle states:

```
ACTIVE
    │
    ▼
ARCHIVED
    │
    ▼
SOFT DELETED
    │
    ▼
PERMANENTLY DELETED
```

---

## 🔐 Authentication & Security

- JWT Authentication
- BCrypt password hashing
- Stateless authentication
- Spring Security Filter Chain
- Role-Based Access Control (RBAC)

### Roles

| Role | Permissions |
|-------|------------|
| USER | Upload, Download, Search, Archive, Restore |
| ADMIN | Bucket Management, Statistics, Permanent Delete |

---

## 📊 Storage Analytics

Administrators can view:

- Total files
- Total storage usage
- Files by status
- Files by bucket
- Files by content type
- Trash management
- Storage overview dashboard

---

# 🛠 Tech Stack

| Layer | Technology |
|--------|------------|
| Backend | Java 17 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security + JWT |
| Database | MongoDB |
| Object Storage | MinIO |
| Frontend | React + Vite |
| API Docs | Swagger (OpenAPI) |
| Build Tool | Maven |

---

# 📁 Project Structure

```text
strata
│
├── enterprise-media-storage-service
│   ├── config
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── exception
│   ├── repository
│   ├── security
│   └── service
│
├── media-storage-frontend
│   ├── api
│   ├── components
│   ├── context
│   └── pages
│
└── docker-compose.yml
```

---

# 📡 REST API

Base URL

```
/api
```

| Module | Endpoint | Access |
|----------|---------|--------|
| Authentication | `/auth/register` | Public |
| Authentication | `/auth/login` | Public |
| Buckets | `GET /buckets` | Authenticated |
| Buckets | `POST /buckets` | ADMIN |
| Buckets | `DELETE /buckets/{name}` | ADMIN |
| Objects | `POST /objects/upload` | Authenticated |
| Objects | `GET /objects/download/{id}` | Authenticated |
| Objects | `DELETE /objects/{id}` | Authenticated |
| Metadata | `GET /metadata` | Authenticated |
| Lifecycle | `PUT /lifecycle/archive/{id}` | Authenticated |
| Lifecycle | `PUT /lifecycle/restore/{id}` | Authenticated |
| Lifecycle | `PUT /lifecycle/soft-delete/{id}` | Authenticated |
| Lifecycle | `DELETE /lifecycle/permanent-delete/{id}` | ADMIN |
| Statistics | `GET /statistics` | ADMIN |

---

# 🚀 Getting Started

## Prerequisites

- Java 17+
- Node.js 18+
- Docker Desktop
- Maven

---

## 1. Clone Repository

```bash
git clone https://github.com/karanjot140506/Strata-object-storage.git
cd Strata-object-storage
```

---

## 2. Start Infrastructure

```bash
docker compose up -d
```

Starts:

- MongoDB
- MinIO

---

## 3. Run Backend

```bash
cd enterprise-media-storage-service

mvn spring-boot:run
```

Swagger

```
http://localhost:8080/swagger-ui.html
```

---

## 4. Run Frontend

```bash
cd media-storage-frontend

npm install

npm run dev
```

Open

```
http://localhost:5173
```

---

# 👤 Default Behavior

The **first registered user** automatically becomes an **ADMIN**.

This simplifies local development and demonstrates role-based authorization without requiring a provisioning workflow.

---

# 💡 Design Decisions

### Why MinIO?

MinIO provides an S3-compatible API, making it easy to migrate to AWS S3 without changing application code.

---

### Why Separate Metadata?

Searching millions of files directly inside object storage is inefficient.

MongoDB stores:

- filenames
- owner
- checksums
- lifecycle state
- content type
- upload information

while MinIO stores only the file bytes.

This mirrors production cloud storage architecture.

---

### Why JWT?

JWT enables:

- Stateless authentication
- Horizontal scalability
- No server-side session storage
- Secure API access

---

# 📸 Screenshots

> Add screenshots of:

- Login Page
- Dashboard
- Upload Files
- Bucket Management
- Admin Statistics
- Swagger UI

---

# 🔮 Future Improvements

- Presigned URLs
- Multipart uploads
- Object versioning
- Redis caching
- Kubernetes deployment
- Audit logging
- File sharing links
- Email notifications
- Virus scanning
- Prometheus & Grafana monitoring

---

# 🤝 Contributing

Contributions are welcome!

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Open a Pull Request

---

# 📄 License

This project is licensed under the **MIT License**.

---

## ⭐ If you found this project helpful, consider giving it a star!
