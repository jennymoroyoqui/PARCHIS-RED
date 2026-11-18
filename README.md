Parchís en red

Arquitectura de Software 

Integrantes: Ricardo Félix Valenzuela, Jenny Alejandra Moroyoqui Molina y Juan Diego Peña Valenzuela.

Este proyecto implementa el **CU-01 "Ingresar a la sala"**: el jugador crea su perfil (nombre, avatar y color), entra a la sala de espera y confirma que está listo. Cuando todos están listos (mínimo 2) empieza la partida y se ve el tablero. Funciona **entre varias computadoras** usando el estilo **Message Broker**.

---

## 1. La idea en un dibujo

```
  Computadora 2                  Computadora 1
 ┌──────────────┐        ┌────────────────────────────────┐
 │ Presentacion │        │  Presentacion (otro jugador)   │
 │ ControlSala  │        │                                │
 │ FachadaSala ─┼──┐     │       ┌──────────┐             │
 └──────────────┘  └─────┼──────▶│  BROKER  │◀── FachadaSala
                         │       └────┬─────┘             │
                         │            │                   │
                         │      ServidorSala ── MySQL     │
                         └────────────────────────────────┘
```

* Nadie habla directo con nadie: **todos le mandan mensajes al broker** y el broker se los reenvía a quien esté suscrito a ese canal.
* El **ServidorSala** tiene la sala "oficial" y aplica las reglas (nombre repetido, color ocupado, etc.).
* Cada computadora solo necesita saber **la IP del broker**.

## 2. Módulos (= componentes del diagrama)

| Módulo | Capa | Qué tiene |
|---|---|---|
| `DTOs` | Negocio (DTO) | `PerfilDTO`, `JugadorDTO`, `SalaDTO`, `RespuestaDTO`, `ColorFicha`, `ProtocoloSala` |
| `InterfacesSala` | Negocio (interfaces) | `IFachadaSala`, `IObservadorSala`, `SalaException` |
| `SalaMock` | Negocio (mock) | `FachadaSalaMock`: guarda todo en memoria (Hito 2A) |
| `Sala` | Negocio (subsistema real) | `FachadaSala`: habla con el servidor por el broker (Hito 2B) |
| `Fabrica` | — | `FabricaFachadas`: el **único** que conoce el mock y la fachada real |
| `Presentacion` | Presentación | Pantallas Swing + `ControlSala` + `MainCliente` |
| `Broker` | Infraestructura | `ServidorBroker`, `ClienteBroker`, `Mensaje` y el prototipo |
| `ServidorSala` | Negocio (servidor) | `ServicioSala`, `SalaBO`, `PerfilBO`, `MainServidorSala` |
| `Persistencia` | Datos | `IPerfilDAO`, `PerfilDAOMySQL` (todo el SQL), `FabricaDAO` |

Más detalle en [docs/02-diseno.md](docs/02-diseno.md).

## 3. Cómo abrirlo

1. NetBeans → **File → Open Project** → seleccionar la carpeta `parchis-red`.
2. Clic derecho al proyecto **Parchis en red** → **Clean and Build**. Debe decir `BUILD SUCCESS`.
3. Para correr un módulo: en el nodo **Modules**, doble clic al módulo (se abre como proyecto) → clic derecho → **Run**.

Necesitan JDK 17 o más nuevo. MySQL solo hace falta en la computadora 1.

## 4. Cómo correrlo

El sistema son **3 programas** que corren al mismo tiempo: **Broker**, **ServidorSala** y **Presentacion** (una ventana por jugador). "Parchis en red" solo sirve para compilar (**Clean and Build**); no se corre.

Para correr un módulo: en **Parchis en red → Modules**, doble clic al módulo (aparece como proyecto en la lista) → clic derecho → **Run**. El orden siempre es **Broker → ServidorSala → Presentacion**.

### a) Con el mock (una sola computadora, Hito 2A)

1. En `Fabrica/.../FabricaFachadas.java` poner `USAR_MOCK = true`.
2. Clean and Build y correr **Presentacion** (con el mock no hacen falta Broker ni ServidorSala).
3. El mock ya tiene a **Sofia (rojo, lista)**, así que:
   * escribe `Sofia` → falla A2 (nombre en uso);
   * elige el color rojo → falla A3 (color ocupado);
   * entra con otro nombre y color → ESTOY LISTO → empieza la partida (tú y Sofia).

> Con el mock cada ventana tiene su propia sala en memoria: si abren dos Presentacion **no se ven entre ellas**. Para varios jugadores de verdad usen la opción b).

### b) En red en UNA sola computadora (2 a 4 jugadores)

Sirve para probar todo sin otra computadora.

1. Revisa que `FabricaFachadas.java` tenga `USAR_MOCK = false` y que `Presentacion/parchis.properties` diga `broker.host=localhost` (así vienen en GitHub).
2. *(Opcional)* MySQL: correr `Persistencia/sql/parchis.sql` y crear `ServidorSala/bd.properties` (pasos 1 y 2 de la opción c). Sin MySQL también funciona: los jugadores entran, pero el perfil no se guarda y ServidorSala muestra `ERROR de base de datos`.
3. **Parchis en red** → clic derecho → **Clean and Build** → `BUILD SUCCESS`.
4. Run **Broker** → esperar `[Broker] Broker escuchando en el puerto 5000`.
5. Run **ServidorSala** → esperar `[ServidorSala] Conectado al broker en localhost:5000. Esperando jugadores...`.
6. Run **Presentacion** → ventana del jugador 1.
7. Run **Presentacion** otra vez → ventana del jugador 2 (NetBeans abre otra pestaña de salida). Repetir para el 3 y el 4.
8. En cada ventana: nombre, avatar y un color **distinto** → CONTINUAR. Todas las ventanas se ven en la sala de espera.
9. Todos presionan **ESTOY LISTO** (mínimo 2) → cuenta regresiva → tablero.

Para volver a empezar: detener todo con el **cuadro rojo (Stop)** de cada pestaña de salida y repetir desde el paso 4.

### c) En red (dos computadoras, Hito 2B)

**Computadora 1** (la que tiene MySQL):

1. En MySQL Workbench correr `Persistencia/sql/parchis.sql`.
2. Copiar `ServidorSala/bd.properties.ejemplo` como `ServidorSala/bd.properties` y escribir la contraseña. **Ese archivo no se sube a GitHub.**
3. Correr **Broker** (`MainBroker`). Imprime las IPs de la computadora; anoten la de la red WiFi. Si Windows pregunta por el firewall → **Permitir acceso** a Java en redes privadas.
4. Correr **ServidorSala** (`MainServidorSala`).
5. Correr **Presentacion** (jugador 1).

**Computadora 2** (no necesita MySQL, Broker ni ServidorSala):

1. En `Presentacion/parchis.properties` cambiar `broker.host=localhost` por la IP de la computadora 1. Ese cambio no se sube a GitHub.
2. Correr **Presentacion** (jugador 2).

Las dos computadoras deben estar en la **misma red WiFi**.

### d) Prototipo del estilo (dos computadoras se mandan un mensaje)

Computadora 1: correr `MainBroker` y luego `Broker/.../prototipo/PrototipoMensaje.java` (clic derecho → Run File).
Computadora 2: correr `PrototipoMensaje` y escribir la IP de la computadora 1. Lo que escriban en una aparece en la otra, **pasando por el broker**.

### e) Si algo falla

| Lo que sale | Por qué | Qué hacer |
|---|---|---|
| `No se pudo conectar con el broker` (en Presentacion o ServidorSala) | El Broker no está corriendo | Correr primero **Broker** y esperar `escuchando en el puerto` |
| El Broker se cierra con `BindException` o `Address already in use` | Otro programa ya usa el puerto 5000 (pasa en algunas Windows y en Mac por AirPlay) | Cambiar el puerto (abajo) |
| `El servidor de la sala no respondió` | ServidorSala no está corriendo | Correr **ServidorSala** |
| `[PerfilBO] ERROR de base de datos` | No hay MySQL, no se corrió el script o la contraseña está mal | Revisar los pasos 1 y 2 de la opción c). El juego sigue funcionando |
| La computadora 2 no conecta | IP equivocada, otra red o el firewall | Misma WiFi, la IP que imprimió el Broker y **Permitir acceso** en el firewall |

**Cambiar el puerto:** poner el mismo número (por ejemplo `6060`) en `Broker/.../ServidorBroker.java` (`PUERTO_POR_DEFECTO`) y en `Presentacion/parchis.properties` (`broker.puerto`), y hacer **Clean and Build** de **Parchis en red**. En la prueba con dos computadoras, las dos deben usar el mismo puerto.

## 5. Documentos

* [docs/01-estilo-message-broker.md](docs/01-estilo-message-broker.md): justificación del estilo con los escenarios de calidad.
* [docs/02-diseno.md](docs/02-diseno.md): CU-01, capas, componentes, DTOs, pantallas y qué dibujar en StarUML.
