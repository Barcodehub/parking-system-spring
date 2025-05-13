# parking-system-spring
prueba tecnica parking system con spring boot


### AUTHENTICACIÓN


```http
POST /auth/login
Descripción: Iniciar sesión 
Body: {"email": "string", "password": "string"}
Permisos: Público
```

```http
POST /auth/socio
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
Descripción: Lista TODOS los parqueaderos
Headers: Authorization: Bearer {token}
Permisos: Admin
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
Asegurate de introducir un SocioID valido
```

```http
DELETE /parkings/{id}
Descripción: Elimina un parqueadero
Headers: Authorization: Bearer {token}
Permisos: Admin
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
