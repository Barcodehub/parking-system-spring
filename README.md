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