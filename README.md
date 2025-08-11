## Challenge FORO HUB - Alura

API REST desarrollada en java para Alura Latam en el marco del programa Oracle Next Education.

La aplicacion es capaz de registrar topicos en un foro, obtener todos los topicos, obtener topicos por id, editar topicos
y realizar un borrado lógico.

### Tecnologías Utilizadas
- Java 17.0
- Spring Boot 3.3.9
- Maven

#### Dependencias utilizadas 
- Spring Web: Para construir aplicaciones web RESTful
- Spring DevTools: Herramientas para desarrollo, como recarga automática
- Spring Security: Para asegurar la aplicación y manejar autenticación/autorización
- Lombok: Reducción de código repetitivo
- Spring Data JPA: Acceso a datos con JPA/Hibernate
- Validación con anotaciones
- Flyway: Migración de base de datos y su soporte para MySQL
- Driver MySQL para conectarse a la base de datos
- Auth0 Java JWT: Generación y validación de tokens JWT para autenticación
- Spring Test: Herramientas para pruebas unitarias e integración
- Spring Security Test: Pruebas específicas para seguridad

### Instalacion
    
1. Clonar el repositorio:


    git clone https://github.com/brendayw/ALURA-challenge-forohub
    cd ALURA-challenge-forohub

2. Configurar MySQL:


    Asegúrate de tener una instancia de MySQL en ejecución.
    Crea una base de datos llamada forohubapi.
    Verifica las credenciales en application.properties.

3. Compilar y Ejecutar:


    mvn spring-boot:run

### Estructura


    └───aluracursos
            └───forohub
            │        ├───controller
            │        │      ├─── AutenticacionController.java
            │        │      └─── TopicoController.java
            │        ├───infra
            │        │      ├───exceptions
            │        │      │       ├───
            │        │      └───security
            │        │              ├─── DatosTokenJWT
            │        │              ├─── SecurityConfigurations
            │        │              └─── SecurityFilter
            │        ├───model
            │        │       ├───topico
            │        │       │      ├─── DatosActualizarTopico
            │        │       │      ├─── DatosDetalleTopico
            │        │       │      ├─── DatosListadoTopicos
            │        │       │      ├─── DatosRegistroTopico
            │        │       │      ├─── Estado
            │        │       │      └─── Topico
            │        │       ├───usuario
            │        │       │      ├─── DatosAutenticacion
            │        │       │      └─── Usuario
            │        ├───repository
            │        │       ├─── TopicoRepository
            │        │       └─── UsuarioRepository
            │        └───service
            │        │       ├─── AutenticacionService
            │        │       └─── TokenService
            └───resources
                ├───db
                │  └───migration
                │        ├─── V1__create_tables_topicos
                │        ├─── V2__alter_table_topicos_add_column
                │        └─── V3__create_tables_usuarios


#### Estructura detallada

#### Controller

- **AutenticacionController:** gestiona el inicio de sesión de usuarios. Recibe las credenciales (login y contraseña), autentica al usuario utilizando Spring Security y devuelve un token JWT si las credenciales son válidas.
  - Endpoint: POST /login 
  - Responsabilidad: Autenticación de usuarios y generación de JWT. 
  - Devuelve: Un objeto con el token JWT para usar en futuras peticiones autenticadas.


- **TopicoController:** gestiona todas las operaciones relacionadas con los tópicos.
    - Endpoint base: /topicos 
    - Responsabilidades:
      - Crear un nuevo tópico (POST /topicos)
      - Listar tópicos paginados (GET /topicos)
      - Ver detalle de un tópico (GET /topicos/{id})
      - Actualizar información de un tópico (PUT /topicos)
      - Eliminar (lógicamente) un tópico (DELETE /topicos/{id})
  
#### Infra

##### *Exceptions*

- **GestorDeErrores:** centraliza el manejo de errores en la aplicación.
  - Responsabilidad: Intercepta excepciones comunes lanzadas por Spring y devuelve respuestas HTTP personalizadas.
  - Maneja: 
    - EntityNotFoundException: Devuelve un HTTP 404 Not Found
    - MethodArgumentNotValidException: Devuelve un HTTP 400 Bad Request con detalles de los errores de validación de campos.
  - Formato de error: Devuelve una lista de objetos DatosErrorValidacion con el nombre del campo y el mensaje de error.


- **TopicoNotFoundException:** representa un error cuando un tópico no se encuentra en la base de datos.
    - Uso: Es lanzada cuando se intenta acceder, actualizar o eliminar un tópico que no existe.
    - Hereda de: Throwable
    - Beneficio: Mejora la claridad del código al usar excepciones específicas del dominio de la aplicación.


##### *Security*

- **SecurityConfigurations:** clase de configuración principal de Spring Security.
    - Define las reglas de seguridad HTTP, como:
      - Desactivar CSRF (ideal para APIs REST).
      - Usar sesiones sin estado (JWT).
      - Permitir acceso sin autenticación solo al endpoint /login.
    - Configura:
      - El filtro personalizado SecurityFilter.
      - El codificador de contraseñas (BCryptPasswordEncoder).
      - El AuthenticationManager para manejar la autenticación.


- **SecurityFilter:** filtro personalizado que intercepta cada solicitud HTTP para verificar si incluye un token JWT válido.
  - Extrae el token del encabezado Authorization.
  - Valida el token y recupera el usuario desde la base de datos.
  - Autentica al usuario en el contexto de seguridad de Spring.
  - Si el token no está presente o no es válido, la solicitud sigue sin autenticar.


- **DatosTokenJWT:** clase tipo record que encapsula el token JWT generado tras el login.
  - Se utiliza para devolver el token como respuesta al cliente.
  - Representa un objeto simple con solo un campo: token.

#### Model

#### *Topico*

- **Topico:** entidad principal que representa un tópico en el foro.
  - Mapeada a la tabla topicos. 
  - Contiene campos como: titulo, mensaje, fechaCreacion, status, autor, curso, y un flag activo para borrado lógico. 
  - Métodos importantes:
    - actualizarInformacion(...): Permite actualizar parcialmente el título y mensaje. 
    - eliminar(): Marca el tópico como inactivo (borrado lógico). 
  - El estado del tópico se gestiona mediante el enum Estado.


- **DatosRegistroTopico:** DTO usado para registrar un nuevo tópico.
  - Campos obligatorios: titulo, mensaje, autor, curso.
  - Usado en el endpoint POST /topicos.


- **DatosActualizarTopico:** DTO usado para actualizar un tópico existente.
  - Requiere el campo id. 
  - Los campos titulo y mensaje son opcionales y se actualizan solo si se envían.


- **DatosListadoTopicos:** DTO usado para mostrar una lista paginada de tópicos.
  - Incluye: id, titulo, mensaje, fechaCreacion, status, autor, curso.
  - Se construye a partir de una entidad Topico.


- **DatosDetalleTopico:** DTO usado para mostrar el detalle completo de un único tópico.
  - Similar a DatosListadoTopicos, pero diseñado para mostrar un solo recurso. 
  - Usado en GET /topicos/{id} y en la respuesta tras crear un nuevo tópico.


- **Estado:** enum que representa los estados posibles de un tópico:
  - ABIERTO: El tópico está activo y abierto para respuestas. 
  - CERRADO: El tópico ha sido cerrado manualmente. 
  - RESUELTO: El problema planteado fue resuelto.

#### *Usuario* 

- **Usuario:** entidad que representa a un usuario autenticado del sistema.
    - Mapeada a la tabla usuarios. 
    - Implementa UserDetails de Spring Security para integrarse con el sistema de autenticación. 
    - Campos: id, login, contrasena. 
    - Define un rol por defecto: ROLE_USER. 
    - Todos los métodos de la interfaz UserDetails retornan true (sin restricciones adicionales).


- **DatosAutenticacion:** DTO utilizado en el proceso de inicio de sesión.
    - Contiene: login y contrasena. 
    - Es enviado en el cuerpo de la petición POST /login.


#### Repository

- **TopicoRepository:** interface que extiende JpaRepository para acceder y gestionar los tópicos en la base de datos.
    - *findByIdAndActivoTrue(Long id)*: busca un tópico activo por ID.
    - *findAllByActivoTrue(Pageable paginacion)*: lista todos los tópicos activos de forma paginada.

- **UsuarioRepository:** interface que extiende JpaRepository para acceder a los usuarios del sistema.
  - findByLogin(String login): busca un usuario por su login. Devuelve un objeto UserDetails, compatible con Spring Security.


#### Service

- **AutenticacionService:** implementa UserDetailsService de Spring Security para autenticar usuarios por su nombre de usuario (login).
    - Usa UsuarioRepository para buscar el usuario en la base de datos. 
    - Spring la utiliza automáticamente al realizar el login (/login) gracias a la configuración de seguridad.


- **TokenService:** servicio responsable de generar y verificar tokens JWT.
  - *generarToken(Usuario usuario)*: 
    - Genera un token JWT firmado con HMAC256. 
    - Incluye como subject el login del usuario. 
    - Tiene una expiración de 2 horas.
  - *getSubject(String tokenJWT)*:
    - Verifica el token recibido y extrae el subject (login del usuario). 
    - Lanza una excepción si el token es inválido o ha expirado.

### Autor - Brenda Yañez

📧 Email: brendayw97@gmail.com

🔗 GitHub: @brendayw

🌐 LinkedIn: [Brenda Yañez](https://www.linkedin.com/in/brendayw/)