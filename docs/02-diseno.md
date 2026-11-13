# Diseño del CU-01 "Ingresar a la sala"

Este documento explica el diseño y dice **qué dibujar en StarUML** para que el modelo coincida con el código. Si un componente está en el diagrama pero no existe como módulo, o al revés, se pierden puntos.

---

## 1. Especificación del CU-01

**Actor:** Jugador. **Objetivo:** entrar a la sala de espera y empezar la partida.
**Precondición:** el broker y el ServidorSala están corriendo (en la versión mock no hace falta).

### Flujo básico (FB)

1. El sistema muestra **"Crea tu perfil de juego"**.
2. El jugador escribe su nombre, elige avatar y color y presiona **CONTINUAR**.
3. El sistema valida, **guarda el perfil** (MySQL) y mete al jugador en la sala.
4. El sistema muestra la **Sala de espera** con todos los jugadores (n/4), y los demás jugadores ven al nuevo **en tiempo real**.
5. El jugador presiona **ESTOY LISTO**; su tarjeta muestra la palomita en todas las pantallas.
6. Cuando hay al menos 2 jugadores y **todos están listos**, se muestra "La partida comenzará en 3...".
7. El sistema muestra el **tablero** con las fichas de cada jugador en su base. Fin del caso de uso.

### Flujos alternos

| Flujo | Cuándo pasa | Qué hace el sistema | Dónde se detecta |
|---|---|---|---|
| **A1** Datos incompletos | Paso 2: sin nombre, nombre de más de 15 letras, sin avatar o sin color | Muestra el error en rojo; no se manda nada | `ControlSala` |
| **A2** Nombre en uso | Paso 3: otro jugador de la sala ya usa ese nombre | "El nombre X ya lo usa otro jugador" | Fachada (mock y real) |
| **A3** Color ocupado | Paso 3: otro jugador ya eligió ese color (con 4 colores, también limita la sala a 4) | "El color X ya lo eligió otro jugador" | Fachada (mock y real) |
| **A4** Partida iniciada | Paso 3: la partida ya empezó | "La partida ya comenzó" | Fachada (mock y real) |
| **A5** Salir | Paso 4 o 5: el jugador presiona SALIR | Se libera su lugar y color; los demás lo ven; regresa a "Crea tu perfil" | Fachada |

El manual pide que el mock falle en **A2 y A3**: `FachadaSalaMock` lo hace porque ya trae a "Sofia" con el color rojo.

---

## 2. Capas

```
┌─────────────────── PRESENTACIÓN ───────────────────┐
│ PantallaPerfil  PantallaSalaEspera  PantallaTablero │
│                   ControlSala                       │  ← solo conoce IFachadaSala
└──────────────────────────┬──────────────────────────┘
                           │ FabricaFachadas (único que conoce las clases concretas)
┌──────────────────────── NEGOCIO ───────────────────────────┐
│ InterfacesSala: IFachadaSala, IObservadorSala, SalaException│
│ SalaMock: FachadaSalaMock          Sala: FachadaSala        │
│ ServidorSala: ServicioSala, SalaBO, PerfilBO                │
│ DTOs: PerfilDTO, JugadorDTO, SalaDTO, RespuestaDTO          │
└──────────────────────────┬─────────────────────────────────┘
┌───────── DATOS ─────────┐      ┌──── INFRAESTRUCTURA ────┐
│ IPerfilDAO              │      │ Broker: ServidorBroker, │
│ PerfilDAOMySQL, Perfil  │      │ ClienteBroker, Mensaje  │
└─────────────────────────┘      └─────────────────────────┘
```

Reglas que respetamos (son de la lista de "errores que cuestan puntos"):

* Las pantallas y `ControlSala` **nunca** nombran a `FachadaSalaMock`, `FachadaSala` ni a un DAO. Solo la fábrica (`FabricaFachadas`, `FabricaDAO`) los conoce.
* `PerfilBO` usa el DAO **por su interfaz** (`IPerfilDAO`). Todo el SQL está en `PerfilDAOMySQL`.
* La contraseña de MySQL está en `bd.properties`, que está en `.gitignore`.
* `IFachadaSala` no cambió entre el Hito 2A y el 2B.
* No hay funciones extra (ni chat, ni guardar partidas, ni ranking). El prototipo de mensajes es el que pide el manual en el punto 6 del Hito 2A, no una función del juego.

---

## 3. Vista de componentes (un módulo de Maven por componente)

| Componente | Depende de | Interfaz que ofrece |
|---|---|---|
| DTOs | — | (clases de datos) |
| InterfacesSala | DTOs | `IFachadaSala`, `IObservadorSala` |
| SalaMock | InterfacesSala | implementa `IFachadaSala` |
| Sala | InterfacesSala, Broker | implementa `IFachadaSala` |
| Fabrica | InterfacesSala, SalaMock, Sala | `FabricaFachadas.crearFachadaSala()` |
| Presentacion | DTOs, InterfacesSala, Fabrica | (pantallas) |
| Broker | — | `ClienteBroker` (publicar / suscribir) |
| ServidorSala | InterfacesSala, Broker, Persistencia | (programa servidor) |
| Persistencia | driver MySQL | `IPerfilDAO` |

En StarUML (**Component View**): un componente por renglón, con el mismo nombre que el módulo, y flechas de dependencia como en la columna "Depende de". Usen la notación de interfaz ofrecida/requerida (bolita y media luna) para `IFachadaSala`, `IPerfilDAO` y el `ClienteBroker`.

---

## 4. DTOs (paquete `Negocios/DTO` del modelo)

| DTO | Atributos | Para qué |
|---|---|---|
| `PerfilDTO` | nombre, avatar, color | Lo que captura el jugador (pantalla → fachada → servidor) |
| `JugadorDTO` | nombre, avatar, color, listo | Una tarjeta de la sala de espera |
| `SalaDTO` | jugadores: List<JugadorDTO>, partidaIniciada | La sala completa, se reparte a todos |
| `RespuestaDTO` | exito, error, sala | Respuesta del servidor a una petición |
| `ColorFicha` (enum) | AMARILLO, ROJO, VERDE, AZUL | Color del jugador |

**Objetos de negocio (paquete `Negocios/objetosNegocio`):** `SalaBO` (reglas de la sala) y `PerfilBO` (guarda el perfil usando `IPerfilDAO`).
**Entidad (capa de datos):** `Perfil` (renglón de la tabla `perfiles`).

---

## 5. Pantallas (iguales al storyboard)

| Pantalla | Storyboard | Llama a ControlSala |
|---|---|---|
| `PantallaPerfil` | "Crea tu perfil de juego" | `continuar(nombre, avatar, color)` |
| `PantallaSalaEspera` | "Sala de espera" (2/4) y "Todos listos, comenzará en 3..." | `confirmarListo()`, `salir()`, `cerrarAplicacion()` |
| `PantallaTablero` | "La partida ha comenzado" | — |

---

## 6. Interior del subsistema Sala (Hito 2B)

### Diagrama de clases

```
            «interface»                       «interface»
           IFachadaSala ◁- - - - - - FachadaSala - - - - -▷ IReceptorMensajes
                △                         │ 1                       △
                ┆                         ▼                         ┆
         FachadaSalaMock            ClienteBroker ──────▶ Mensaje   ┆
                                          ▲                         ┆
                                          │ 1                       ┆
 MainServidorSala ──▶ ServicioSala - - - - - - - - - - - - - - - - -┘
                          │ 1
                          ▼
                       SalaBO ──1──▶ PerfilBO ──▶ «interface» IPerfilDAO ◁- - PerfilDAOMySQL
                          │                                                         │
                          ▼ *                                                       ▼
                     JugadorDTO                                                 ConexionBD
```

### Un diagrama de secuencia por cada operación de la fachada

**entrarASala(perfil)**
1. `ControlSala` → `FachadaSala.entrarASala(perfil)`
2. `FachadaSala` → `ClienteBroker.publicar("sala.peticiones", "ENTRAR", perfil)`
3. `ClienteBroker` → *Broker* → `ServicioSala.mensajeRecibido(m)`
4. `ServicioSala` → `SalaBO.entrar(perfil, idCliente)`
   * `alt` nombre repetido → `SalaException` (A2)
   * `alt` color ocupado → `SalaException` (A3)
   * `alt` partida iniciada → `SalaException` (A4)
   * si no hay error: `SalaBO` → `PerfilBO.guardarPerfil(perfil)` → `IPerfilDAO.buscarPorNombre` / `agregar` o `actualizar`
5. `ServicioSala` → `ClienteBroker.publicar("cliente.<id>", "RESPUESTA", respuesta)`
6. `ServicioSala` → `ClienteBroker.publicar("sala.eventos", "SALA_ACTUALIZADA", sala)` (solo si hubo éxito)
7. *Broker* → `FachadaSala.mensajeRecibido(RESPUESTA)` → regresa `SalaDTO` o lanza `SalaException`
8. *Broker* → `FachadaSala.mensajeRecibido(SALA_ACTUALIZADA)` → `IObservadorSala.salaActualizada(sala)` en **todas** las computadoras

**confirmarListo(nombre)**: igual que el anterior con `"LISTO"`; `SalaBO.confirmarListo` marca al jugador y, si hay 2 o más y todos están listos, pone `partidaIniciada = true`.

**salirDeSala(nombre)**: igual con `"SALIR"`; `SalaBO.salir` quita al jugador y libera su color.

**agregarObservador(obs)**: `ControlSala` → `FachadaSala.agregarObservador(this)` → se agrega a la lista `observadores` (no hay red).

> En el Hito 2A (mock) las mismas cuatro operaciones son más cortas: `ControlSala` → `FachadaSalaMock` → valida en su lista en memoria → `notificarObservadores()`.

---

## 7. Vista de despliegue

```
«device» Computadora 1                       «device» Computadora 2
 ├─ «executable» MainBroker (puerto 5000)     └─ «executable» MainCliente (Presentacion)
 ├─ «executable» MainServidorSala                   │ TCP/IP (socket) → IP de la computadora 1 :5000
 ├─ «executable» MainCliente (Presentacion)
 └─ «database» MySQL (base "parchis")
```

---

## 8. Dónde está cada cosa en el modelo de StarUML

El modelo actualizado es `parchis-modelo4+1vistas-unidad2.mdj` (el mismo modelo de la Unidad I con lo nuevo agregado). Todo lo de la Unidad I se conservó; solo se borraron los subsistemas de ejemplo vacíos de la plantilla (`subsistema_1` y `subsistema_N`).

| Vista | Paquete en el modelo | Diagramas |
|---|---|---|
| Casos de uso | `Use Case Model` | **CU-01 Ingresar a la sala** (la especificación completa está en la *Documentation* del caso de uso) |
| Lógica · Análisis | `Logical View / Analisis / CU01_IngresarALaSala` | Clases de análisis CU-01 · DS Análisis CU-01 Flujo básico · DS Análisis CU-01 Flujos alternos |
| Lógica · Diseño | `Logical View / Diseño` | Vista lógica de diseño: capas y paquetes (Unidad II) |
| Presentación | `Diseño / Presentacion` | Presentacion: diagrama de clases |
| DTOs | `Diseño / Negocios / DTO` | DTOs: diagrama de clases |
| Interfaces | `Diseño / Negocios / interfaces` | Interfaces del subsistema Sala |
| Fábrica | `Diseño / Negocios / fabrica` | Fabrica: diagrama de clases |
| Objetos de negocio | `Diseño / Negocios / objetosNegocio` | Objetos de negocio: diagrama de clases |
| Subsistema mock | `Diseño / Negocios / SalaMock` | SalaMock: diagrama de clases · secuencia entrarASala (mock) |
| **Subsistema Sala** | `Diseño / Negocios / Sala` | Sala: diagrama de clases (interior) · secuencias **entrarASala**, **confirmarListo**, **salirDeSala** y **agregarObservador** (una por operación de la fachada) |
| Servidor | `Diseño / Negocios / ServidorSala` | (sus clases salen en el diagrama del subsistema Sala) |
| Datos | `Diseño / Datos / Persistencia` | Persistencia: diagrama de clases |
| Infraestructura | `Diseño / Infraestructura / Broker` | Broker: diagrama de clases |
| Componentes | `Component View` | Vista de componentes (Unidad II): los 9 módulos y las interfaces `IFachadaSala` e `IPerfilDAO` |
| Despliegue | `Deployment View` | Vista de despliegue (Unidad II) |

Si cambian el código, actualicen también el modelo: **todo lo del modelo debe existir en el código con el mismo nombre**, y al revés.
