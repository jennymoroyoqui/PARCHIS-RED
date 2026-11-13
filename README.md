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

### a) Con el mock (una sola computadora, Hito 2A)

1. En `Fabrica/.../FabricaFachadas.java` poner `USAR_MOCK = true`.
2. Clean and Build y correr **Presentacion**.
3. El mock ya tiene a **Sofia (rojo, lista)**, así que:
   * escribe `Sofia` → falla A2 (nombre en uso);
   * elige el color rojo → falla A3 (color ocupado);
   * entra con otro nombre y color → ESTOY LISTO → empieza la partida.

### b) En red (dos computadoras, Hito 2B)

**Computadora 1** (la que tiene MySQL):

1. En MySQL Workbench correr `Persistencia/sql/parchis.sql`.
2. Copiar `ServidorSala/bd.properties.ejemplo` como `ServidorSala/bd.properties` y escribir la contraseña. **Ese archivo no se sube a GitHub.**
3. Correr **Broker** (`MainBroker`). Imprime las IPs de la computadora; anoten la de la red WiFi.
   Si Windows pregunta por el firewall → **Permitir acceso** a Java en redes privadas.
4. Correr **ServidorSala** (`MainServidorSala`).
5. Correr **Presentacion** (jugador 1).

**Computadora 2**:

1. En `Presentacion/parchis.properties` cambiar `broker.host=localhost` por la IP de la computadora 1.
2. Correr **Presentacion** (jugador 2).

> Se pueden abrir varias Presentacion en la misma computadora para simular 3 o 4 jugadores.

### c) Prototipo del estilo (dos computadoras se mandan un mensaje)

Computadora 1: correr `MainBroker` y luego `Broker/.../prototipo/PrototipoMensaje.java` (clic derecho → Run File).
Computadora 2: correr `PrototipoMensaje` y escribir la IP de la computadora 1. Lo que escriban en una aparece en la otra, **pasando por el broker**.

## 5. Documentos

* [docs/01-estilo-message-broker.md](docs/01-estilo-message-broker.md): justificación del estilo con los escenarios de calidad.
* [docs/02-diseno.md](docs/02-diseno.md): CU-01, capas, componentes, DTOs, pantallas y qué dibujar en StarUML.
* [docs/03-casos-de-prueba.md](docs/03-casos-de-prueba.md): un caso de prueba por flujo, para pasar a Word con capturas.
