# Casos de prueba del CU-01 "Ingresar a la sala"

Un caso de prueba por flujo. Pásenlo a Word y peguen **capturas de la aplicación corriendo** en cada paso marcado con 📷.

* **Hito 2A:** con el mock (`USAR_MOCK = true`), una computadora.
* **Hito 2B:** en red (`USAR_MOCK = false`), dos computadoras. Agreguen captura de `SELECT * FROM perfiles;` en MySQL.

---

## CP-01 · Flujo básico: entrar a la sala y empezar la partida

| | Mock (2A) | Red (2B) |
|---|---|---|
| Precondición | Correr Presentacion | Broker y ServidorSala corriendo; Presentacion en la computadora 1 y en la 2 |
| Datos | Fernando · Ninja · Azul | Comp. 1: Fernando · Ninja · Azul. Comp. 2: Sofia · Pirata · Rojo |

| Paso | Acción | Resultado esperado | 📷 |
|---|---|---|---|
| 1 | Abrir la aplicación | Se ve "Crea tu perfil de juego" | 📷 |
| 2 | Escribir nombre, elegir avatar y color, presionar CONTINUAR | Se abre la Sala de espera. Mock: 2/4 con Sofia lista. Red: 1/4 | 📷 |
| 3 | (Red) En la comp. 2 entrar como Sofia | **En las dos** pantallas se ve 2/4 con Fernando y Sofia | 📷 📷 |
| 4 | Presionar ESTOY LISTO (en red, en las dos) | Aparece la palomita en todas las pantallas; el botón se deshabilita | 📷 |
| 5 | Cuando todos están listos | "¡TODOS LOS JUGADORES LISTOS! La partida comenzará en 3..." | 📷 |
| 6 | Esperar 3 segundos | Se ve el tablero con las fichas 1 a 4 de cada jugador en su base y su nombre | 📷 |
| 7 | (Red) MySQL: `SELECT * FROM perfiles;` | Aparecen Fernando y Sofia con su avatar y color | 📷 |

---

## CP-02 · A1: datos incompletos

| Paso | Acción | Resultado esperado | 📷 |
|---|---|---|---|
| 1 | Presionar CONTINUAR sin escribir nada | "Escribe tu nombre de usuario." en rojo; no cambia de pantalla | 📷 |
| 2 | Escribir nombre y presionar CONTINUAR sin avatar | "Elige un avatar." | 📷 |
| 3 | Elegir avatar y presionar CONTINUAR sin color | "Selecciona el color de tus fichas." | 📷 |
| 4 | Escribir un nombre de más de 15 letras | "El nombre debe tener máximo 15 letras." | 📷 |

---

## CP-03 · A2: el nombre ya está en uso

| | Mock (2A) | Red (2B) |
|---|---|---|
| Precondición | (Sofia ya está en el mock) | Fernando ya entró desde la computadora 1 |
| Datos | Sofia · Hechicera · Verde | Fernando · Vaquero · Verde (desde la comp. 2) |

| Paso | Acción | Resultado esperado | 📷 |
|---|---|---|---|
| 1 | Llenar los datos y presionar CONTINUAR | "El nombre "…" ya lo usa otro jugador. Escribe otro." y se queda en la pantalla de perfil | 📷 |
| 2 | Cambiar el nombre y presionar CONTINUAR | Entra a la sala normalmente | 📷 |

---

## CP-04 · A3: el color ya está ocupado

| | Mock (2A) | Red (2B) |
|---|---|---|
| Precondición | (Sofia tiene el rojo) | Fernando ya entró con azul |
| Datos | Fernando · Ninja · **Rojo** | Sofia · Pirata · **Azul** |

| Paso | Acción | Resultado esperado | 📷 |
|---|---|---|---|
| 1 | Llenar los datos y presionar CONTINUAR | "El color … ya lo eligió otro jugador. Elige otro color." | 📷 |
| 2 | Elegir otro color y presionar CONTINUAR | Entra a la sala normalmente | 📷 |

---

## CP-05 · A4: la partida ya comenzó (solo en red)

| Paso | Acción | Resultado esperado | 📷 |
|---|---|---|---|
| 1 | Hacer CP-01 hasta que empiece la partida | Las dos computadoras ven el tablero | |
| 2 | Abrir otra Presentacion e intentar entrar como Joaquin · Verde | "La partida ya comenzó, ya no puedes entrar a la sala." | 📷 |

> Con el mock no se puede reproducir desde la pantalla, porque al empezar la partida ya no se regresa al perfil.

---

## CP-06 · A5: salir de la sala de espera

| Paso | Acción | Resultado esperado | 📷 |
|---|---|---|---|
| 1 | Entrar a la sala (en red: con 2 jugadores) | Sala con 2/4 | 📷 |
| 2 | Un jugador presiona SALIR | Regresa a "Crea tu perfil". En la otra computadora la sala baja a 1/4 | 📷 📷 |
| 3 | Volver a entrar con el mismo color | Se permite, porque el color quedó libre | 📷 |
| 4 | (Red) Cerrar la ventana con la X en vez de SALIR | En la otra computadora el jugador desaparece de la sala igual | 📷 |
