# SpringBoot-API
Disposición de un servicio API con Java y SpringBoot

# 🏥 API Gestión de Citas Médicas - Backend

Este proyecto corresponde al backend de una aplicación para la gestión de citas médicas, desarrollado con Spring Boot.

## 🚀 Tecnologías utilizadas
- Java
- Spring Boot
- Spring Data JPA
- MySQL
- Maven

## ⚙️ Configuración del proyecto

### 1. Clonar repositorio
- git clone https://github.com/fredynofuya/SpringBoot-API.git
- cd SpringBoot-API
---
### 2. Configurar base de datos
- Crear base de datos en MySQL:
- CREATE DATABASE citaya
---
### 3. Configurar base de datos

- spring.datasource.url=jdbc:mysql://localhost:3306/citaya
- spring.datasource.username=root
- spring.datasource.password=
- spring.jpa.hibernate.ddl-auto=update
- spring.jpa.show-sql=true
---
### 4. Ejecutar proyecto
- mvn clean install
- mvn spring-boot:run
---
### 4. Endpoints principales
- /pacientes
- /medicos
- /citas
