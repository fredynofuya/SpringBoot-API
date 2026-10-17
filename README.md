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
### 3. Configurar variables de entorno

La aplicación usa `DB_URL` para la conexión JDBC (no `URL`, que puede estar sin definir o contener una URL que no sea JDBC). Para desarrollo local, la URL tiene como valor predeterminado `jdbc:mysql://localhost:3306/citaya` y el usuario predeterminado es `root`. Define `PASSWORD` con la contraseña de MySQL; si la cuenta no tiene contraseña, expórtala vacía (`export PASSWORD=`).

Configura el secreto JWT (de al menos 32 bytes) y, si corresponde, las credenciales de MySQL:

```sh
JWT_SECRET=tu_secreto_jwt_de_al_menos_32_bytes
USER_NAME=tu_usuario
PASSWORD=tu_contraseña
DB_URL='jdbc:mysql://localhost:3306/citaya?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true'
```

`PORT` es opcional y usa `8080` por defecto. `JWT_EXPIRATION_MS` también es opcional y usa `86400000` (24 horas). Las variables exportadas deben estar definidas en la misma terminal desde la que se inicia Maven.
---
### 4. Ejecutar proyecto
- mvn clean install
- mvn spring-boot:run
---
### 4. Endpoints principales
- /pacientes
- /medicos
- /citas
- /agenda
