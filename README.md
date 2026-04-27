# AulaClick - Backend

El backend de **AulaClick** es el núcleo del sistema de gestión y reservas de recursos. Está construido sobre **Spring Boot** (Java) y provee una API RESTful robusta y segura que da servicio a la aplicación cliente Android y futuros clientes web.

## Tecnologías Utilizadas

- **Java 17+**
- **Spring Boot 3.x**
  - Spring Web (REST API)
  - Spring Data JPA (Capa de persistencia)
  - Spring Security (Autenticación y Autorización)
- **Base de Datos:** Relacional (MySQL)
- **Maven** (Gestor de dependencias y ciclo de vida)

## Arquitectura

El proyecto sigue una arquitectura clásica de capas para asegurar el principio de responsabilidad única (SRP):

- `controller`: Define los endpoints REST, maneja las peticiones HTTP y mapea las respuestas.
- `service`: Contiene la lógica de negocio. Valida reglas complejas de las reservas (ej. fines de semana, horario).
- `repository`: Interfaces de Spring Data JPA para el acceso a datos.
- `entity`: Modelos de base de datos mapeados mediante JPA/Hibernate.
- `dto`: Objetos de Transferencia de Datos (`Data Transfer Objects`) como `ReservaCrearDTO` y `ReservaDTO` para optimizar payloads y proteger las entidades.
- `security`: Configuración de JWT, filtros de autenticación y manejo de CORS.
- `config`: Configuraciones generales como el mapeo global de fechas (`@JsonFormat`).

## Guía de Instalación y Configuración

1. **Clonar el repositorio y acceder a la carpeta backend:**
   ```bash
   cd aulaclick-backend
   ```

2. **Configurar la Base de Datos:**
   Edita el archivo `src/main/resources/application.properties` (o `.yml`) para establecer tu conexión:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/aulaclick_db
   spring.datasource.username=root
   spring.datasource.password=root
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **Construir el Proyecto con Maven:**
   Puedes usar el wrapper incluido para evitar instalar Maven globalmente:
   - En Windows: `mvnw.cmd clean install`
   - En Linux/Mac: `./mvnw clean install`

4. **Ejecutar la Aplicación:**
   ```bash
   ./mvnw spring-boot:run
   ```
   El servidor arrancará por defecto en `http://localhost:8080`.

## Documentación de Código

El código base incluye comentarios JavaDoc sobre el propósito de controladores clave y la lógica fundamental de los servicios (por ejemplo, validación de fechas y horarios de recursos). 

Se recomienda generar la documentación JavaDoc completa con el comando:
```bash
./mvnw javadoc:javadoc
```
Los reportes se generarán en la carpeta `target/site/apidocs`.
