# Justificación del estilo: Message Broker (Equipo 5)
## 1. ¿Qué es un Message Broker?

Es un estilo donde los componentes no se comunican directamente entre sí. Todos se conectan a un intermediario, el broker, y le mandan mensajes. El broker los reenvía a quien esté interesado.

* Un componente se suscribe a un canal ("quiero recibir lo que llegue a sala.eventos").
* Otro componente publica un mensaje en ese canal.
* El broker entrega el mensaje a todos los suscritos.

Así nadie necesita saber la IP ni la existencia de los demás: solo conoce al broker.

## 2. Cómo lo aplicamos en el Parchís

| Pieza | Dónde corre | Qué hace |
|---|---|---|
| `Broker` (`ServidorBroker`) | Computadora 1 | Recibe mensajes y los reenvía por canal. **No sabe nada del juego**: el contenido le llega como bytes. |
| `ServidorSala` | Computadora 1 | Se suscribe a `sala.peticiones`. Aplica las reglas y responde. |
| `FachadaSala` (en cada jugador) | Cada computadora | Publica peticiones y se suscribe a `sala.eventos` y a su canal privado `cliente.<id>`. |

Canales que usamos:

```
sala.peticiones      jugadores ──▶ ServidorSala        (ENTRAR, LISTO, SALIR)
cliente.<id>         ServidorSala ──▶ un jugador        (RESPUESTA: ok o error)
sala.eventos         ServidorSala ──▶ todos             (SALA_ACTUALIZADA)
broker.desconexiones broker ──▶ ServidorSala           (DESCONECTADO: alguien cerró su programa)
```

Ejemplo, cuando Fernando entra a la sala:

1. La `FachadaSala` de Fernando publica `ENTRAR` en `sala.peticiones`.
2. El broker se lo pasa al `ServidorSala`.
3. `SalaBO` valida (nombre y color libres), guarda el perfil en MySQL y lo agrega.
4. El servidor publica la `RESPUESTA` en `cliente.<id de Fernando>` y la `SALA_ACTUALIZADA` en `sala.eventos`.
5. El broker entrega la sala nueva a **todas** las computadoras y todas redibujan su sala de espera.

## 3. Escenarios de calidad

| ID | Atributo | Fuente | Estímulo | Artefacto | Ambiente | Respuesta | Medida |
|---|---|---|---|---|---|---|---|
| ESC-01 | Rendimiento (sincronización) | Un jugador | Entra a la sala o presiona "Estoy listo" | Sala de espera | Operación normal, red local, 2 a 4 jugadores | Todos los jugadores ven el cambio en su pantalla | En menos de 1 segundo |
| ESC-02 | Interoperabilidad / distribución | Jugadores en distintas computadoras | Se conectan para jugar | Sistema completo | Red local, cada jugador en su equipo | Se comunican sin conocer la IP de los demás | Solo se configura **una** IP (la del broker) |
| ESC-03 | Modificabilidad | Equipo de desarrollo | Agregar un nuevo servicio (por ejemplo la partida, CU Ejecutar turno) | Comunicación | Tiempo de desarrollo | Se agrega sin cambiar el broker ni la sala | 0 líneas cambiadas en `Broker` y en `ServidorSala` |
| ESC-04 | Disponibilidad | Un jugador | Cierra su programa sin presionar Salir | Sala de espera | Operación normal | Se le saca de la sala, se libera su color y los demás siguen | En menos de 1 segundo, sin reiniciar nada |

## 4. Por qué el Message Broker atiende cada escenario

| Escenario | Cómo lo atiende el estilo |
|---|---|
| ESC-01 | El servidor publica **un** mensaje en `sala.eventos` y el broker lo reparte a todos al mismo tiempo (publicar/suscribir). Cada cliente redibuja con el patrón Observador (`IObservadorSala`). |
| ESC-02 | Todos se conectan solo al broker. Las computadoras de los jugadores únicamente tienen la IP del broker en `parchis.properties`. |
| ESC-03 | Un servicio nuevo solo necesita **sus propios canales** (ej. `partida.peticiones`). El broker reenvía cualquier mensaje sin entender su contenido, así que no se modifica. |
| ESC-04 | El broker detecta cuando un socket se cierra y publica `DESCONECTADO` en `broker.desconexiones`. El `ServidorSala` está suscrito, saca al jugador y publica la sala actualizada. |

## 5. Riesgo principal del estilo y cómo lo resolvimos

**Riesgo:** el broker es un **punto único de falla** y un posible **cuello de botella**. Si el broker se cae, nadie se puede comunicar, y todos los mensajes pasan por él.

**Cómo lo atendimos:**

1. **Broker muy simple:** no tiene reglas del juego, solo reenvía bytes. Menos código significa menos fallas. Cada cliente se atiende en su propio hilo, para que uno lento no detenga a los demás.
2. **Mensajes pequeños:** se envían DTOs, no pantallas ni objetos grandes. En 2 a 4 jugadores la carga es mínima.
3. **Falla detectada, no silenciosa:**
   * Si el broker se cae, `ClienteBroker` lo detecta y la pantalla muestra **"Se perdió la conexión con el servidor"**.
   * Si el servidor no responde, la petición espera máximo **5 segundos** y muestra un error (la aplicación no se congela para siempre).
   * Si el broker no está corriendo, al presionar Continuar se ve: *"No se pudo conectar con el broker... revisa la IP"*.
4. **Recuperación sencilla:** se vuelve a correr `MainBroker` y `MainServidorSala`, y los jugadores vuelven a entrar.
