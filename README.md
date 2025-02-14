```markdown:README.md
# API REST para Gestión de Sensores

API Spring Boot para gestión de sensores y mediciones con base de datos H2.

## Configuración Técnica
- **Java 17** - Spring Boot 3.4.2
- **Base de datos**: H2 en memoria
- **Documentación**: OpenAPI 3 (Swagger UI)
- **Persistencia**: Spring Data JPA
- **Mapeo DTO**: Patrón Data Transfer Object

Configuración BD (application.properties):
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
spring.h2.console.path=/h2-ui
```

## Endpoints Principales

| Método | Ruta                                | Descripción                                  |
|--------|-------------------------------------|----------------------------------------------|
| POST   | /sensores                           | Registrar nuevo sensor                       |
| DELETE | /sensores/{idSensor}                | Eliminar sensor por ID                       |
| GET    | /sensores                           | Listar todos los sensores                    |
| GET    | /sensores/{idSensor}                | Obtener valor actual del sensor              |
| GET    | /sensores/{idSensor}/media/{fechas} | Calcular media de mediciones en rango fechas  |
| GET    | /sensores/{idSensor}/historico      | Histórico paginado de mediciones             |

## Características Clave
- Tipos de sensor únicos (Temperatura, Humedad, Presión, Luz)
- Generación automática de valores coherentes con la magnitud
- Validación de formatos de fecha (ISO 8601)
- Paginación en histórico de mediciones
- Control de errores personalizado

## Uso Recomendado
1. Acceder a Swagger UI: `http://localhost:8080/swagger-ui.html`
2. Consola H2 Database: `http://localhost:8080/h2-ui`
   - JDBC URL: `jdbc:h2:mem:testdb`
   - User: `sa`
   
## Ejemplo de Creación de Sensor
```json
POST /sensores
{
  "tipo": "Temperatura",
  "magnitud": "temperatura"
}
```

Los sensores disponibles son: Temperatura, Humedad, Presión y Luz. Cada tipo solo puede registrarse una vez.


