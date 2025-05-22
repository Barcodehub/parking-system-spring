# parking-system-spring
prueba tecnica parking system con spring boot

## Índice
1. [Descripción](#descripción)
2. [Características principales](#características-principales)
3. [Roles y Permisos](#roles-y-permisos)
    - Admin
    - Socio
4. [Instalación](#instalación)
    - Requisitos previos
    - Configuración inicial
    - Configuración de entorno
    - Base de datos
    - Ejecución
    - Microservicio Email
5. [Estructura de directorios](#estructura-de-directorios)
6. [Endpoints del Sistema](#endpoints-del-sistema)
    - Autenticación
    - Analíticas
    - Vehículos
    - Parqueaderos
    - Email (Simulación)


## Descripción:
API para gestión de parqueaderos con autenticación de usuarios, registro de vehículos y análisis de datos. Desarrollada con Spring boot y PostgreSQL.

## Características principales:
- Control de vehículos en parqueaderos de múltiples socios
- Histórico completo de vehículos parqueados (VehicleHistory)
- Sistema de roles (ADMIN/SOCIO) con permisos diferenciados
- Microservicio de email simulado

## ROLES Y PERMISOS:

### Admin puede:

- [x] CRUD completo de parqueaderos.
- [x] Asignar parqueaderos a socios.
- [x] Ver listado/detalle de vehículos en cualquier parqueadero.
- [x] Simular envío de emails a socios.
- [x] Acceder a todos los indicadores.

### Socio puede:

- [x] Registrar entrada/salida de vehículos en sus parqueaderos.
- [x] Ver sus parqueaderos asociados.
- [x] Ver vehículos en sus parqueaderos.
- [x] Acceder a indicadores de sus parqueaderos.

## Instalación:

1. Requisitos previos:
    - Java 17+
    - PostgreSQL 14+ corriendo (puerto 5432 o ajustalo en properties)
    - Tener en cuenta usuario y password de la base de datos posgres en properties (postgres)

2. Crear una Base de datos llamada: parking_spring

3. Clonar Proyecto:
    - git clone [https://github.com/Barcodehub/parking-system-spring](https://github.com/Barcodehub/parking-system-spring)
    - cd parking-system-spring
    - `Run`

4. Documentación de la API con Swagger:
   - Esta aplicación incluye Swagger UI para explorar y probar los endpoints de la API de manera interactiva.
   - Accede a traves de: http://localhost:8080/swagger-ui/index.html
   - Asegurate de colocar el token de autenticacion correcta en el botón Authorize 🔒 

## Estructura de directorios:

  ```

  ├── email-service/        #microservice email-simulacion
  │ 
  │
  src/main/java/com.nelumbo.parqueadero_api/
  │
  ├── config/           # Configuraciones, permisos Auth 
  │
  ├── controllers/      # Lógica de endpoints HTTP
  │
  ├── exception/        #Excepciones globales - personalizadas
  │
  ├── dto/               # DTo's
  │
  ├── repositories/     # Acceso a datos 
  │
  ├── services/         # Lógica de negocio, manejo de errores
  │
  ├── models/            # Modelos BD
  │
  ├── ParqueaderoApiApplication            # # Inicio del servidor
  │
  ```

## ENDPOINTS DEL SISTEMA

Descarga la colección desde [postman_collection.json](Parking_Spring.postman_collection.json).

### AUTHENTICACIÓN

POST `http://localhost:8080/api/`

```http
POST /auth/login
Descripción: Iniciar sesión 
Body: {"email": "string", "password": "string"}
Permisos: Público
```

```http
POST /users
Descripción: Registra un nuevo socio (solo admin)
Headers: Authorization: Bearer {token}
Body: {"name": "Nombre", "email": "email@valido.com", "password": "contraseña"}
Permisos: Admin
```


### Parqueaderos

```http
POST /parkings
Descripción: Crea nuevo parqueadero
Headers: Authorization: Bearer {token}
Body: {
  "nombre": "Nombre",
  "capacidad": 50,
  "costoPorHora": 3.5,
  "socioId": 2
}
Permisos: Admin
```

```http
GET /parkings
Descripción: Lista TODOS los parqueaderos // Lista parqueaderos del socio
Headers: Authorization: Bearer {token}
Permisos: Admin o User
```

```http
GET /parkings/{id}
Descripción: Obtiene detalles de un parqueadero
Headers: Authorization: Bearer {token}
Permisos: Admin
```

```http
PUT /parkings/{id}
Descripción: Actualiza parqueadero
Headers: Authorization: Bearer {token}
Body: {
  "nombre": "Nombre Actualizado",
  "capacidad": 60,
  "costoPorHora": 4.0,
  "socioId": 2
}
Permisos: Admin
```

```http
DELETE /parkings/{id}
Descripción: Elimina un parqueadero
Headers: Authorization: Bearer {token}
Permisos: Admin
```

```http
GET /parkings/parkings/{ParkingID}/vehicles
Descripción: Lista vehículos en algun parqueadero / Vehículos en parqueadero que le pertenezca
Headers: Authorization: Bearer {token}
Permisos: Admin o User
```


### VEHÍCULOS

```http
POST /vehicles/entry
Descripción: Registrar entrada de vehículo
Headers: Authorization: Bearer {token}
Body: {"placa": "ABC123", "parqueaderoId": 1}
Permisos: Socio
```

```http
POST /vehicles/exit
Descripción: Registrar salida de vehículo
Headers: Authorization: Bearer {token}
Body: {"placa": "ABC123", "parqueaderoId": 1}
Permisos: Socio
```


### Analitycs
```http
GET /analityc/vehicles/top-global
Descripción: Top 10 vehículos más registrados en todos los parqueaderos
Headers: Authorization: Bearer {token}
Permisos: Admin/Socio
```

```http
GET /analityc/socios/top-earnings
Descripción: Top 3 socios con más ingresos esta semana
Headers: Authorization: Bearer {token}
Permisos: Admin
```

```http
GET /analityc/parkings/{parkingId}/earnings
Descripción: Ganancias (hoy/semana/mes/año) de un parqueadero
Headers: Authorization: Bearer {token}
Permisos: Socio
```

```http
GET /analityc/parkings/{parkingId}/vehicles/top
Descripción: Top 10 vehículos más registrados en un parqueadero específico
Headers: Authorization: Bearer {token}
Permisos: Admin/Socio
```

```http
GET /analityc/parkings/{parkingId}/vehicles/first-time
Descripción: Vehículos registrados por primera vez en el parqueadero
Headers: Authorization: Bearer {token}
Permisos: Admin/Socio
```

```http
GET /analityc/parkings/top-earnings
Descripción: Top 3 parqueaderos con mayor ganancia semanal
Headers: Authorization: Bearer {token}
Permisos: Admin
```

### EMAIL (SIMULACIÓN prueba directa)

POST `http://localhost:8081/api/`

```http
POST /notifications/send-email
Descripción: Simula envío de notificación por email
Body: {
  "email": "destino@mail.com",
  "placa": "ABC123",
  "mensaje": "Su vehículo ha ingresado",
  "parqueaderoId": "1"
}
```