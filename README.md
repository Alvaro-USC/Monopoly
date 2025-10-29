# MonopolyETSE

##  Descripción del Proyecto
Este proyecto implementa una versión completa del juego Monopoly en Java, con un fuerte enfoque en la Programación Orientada a Objetos. Se crea el tablero, una jerarquía de casillas (Solares, Servicios, Transporte, etc.), jugadores, avatares, y mecánicas de juego que incluyen movimiento, compra, alquileres, edificación, hipotecas, cartas (Suerte y Caja), y un sistema de estadísticas.

El juego se controla íntegramente mediante una interfaz de línea de comandos (CLI). El tablero se representa en modo texto ASCII con colores ANSI para los grupos de solares. Los avatares se muestran con "&" seguido de su ID en su casilla correspondiente.

El proyecto se divide en paquetes `monopoly` (clases principales del juego, tablero y casillas) y `partida` (clases relacionadas con jugadores, avatares y dados). Se soporta ejecución interactiva o desde un archivo de comandos.

## Cómo Ejecutar
- **Compilación**: Compila todos los archivos `.java` con `javac monopoly/*.java partida/*.java`.
- **Ejecución interactiva**: `java monopoly.MonopolyETSE`.
- **Ejecución con archivo de comandos**: `java monopoly.MonopolyETSE comandos.txt` (donde `comandos.txt` contiene comandos como "crear jugador mic Coche").

## Archivos y su Función
A continuación, se describe cada archivo del proyecto, su ubicación y qué hace:

---
### Paquete `monopoly` (Lógica del Juego y Tablero)
---
- **MonopolyETSE.java** (paquete: monopoly)
    - Clase principal para iniciar el juego.
    - Método `main`: Es el punto de entrada del programa. Instancia un nuevo `Menu`, pasándole `args[0]` (un archivo de comandos) si existe.
    - Uso: Punto de entrada de la aplicación.

- **Menu.java** (paquete: monopoly)
    - Clase principal que actúa como el "Controlador" del juego y la "Clase Dios".
    - Atributos: `jugadores`, `avatares`, `turno`, `tablero`, `dados`, `banca`, `mazoSuerte`, `mazoCaja`, `stats`, y banderas de estado (tirado, solvente, partidaIniciada).
    - Constructores: Uno para modo interactivo y otro que acepta un archivo de comandos.
    - Métodos: `iniciarBucleComandos` (lee la entrada del `Scanner`), `analizarComando` (un `if-else` gigante que procesa todos los comandos), `crearJugador`, `lanzarDados` (lógica central de turno, movimiento, evaluación de casilla y cartas), `acabarTurno`, `comprar`, `edificar`, `hipotecar`, `deshipotecar`, `venderEdificios`, `salirCarcel`, `descJugador`, `descAvatar`, `descCasilla`, `listarJugadores`, `listarAvatares`, `listarEnventa`, `listarEdificios`, `estadisticas`.
    - Uso: Gestiona el estado del juego, la interacción con el usuario y la ejecución de toda la lógica del juego.

- **Tablero.java** (paquete: monopoly)
    - Crea y almacena la estructura completa del tablero de 40 casillas.
    - Atributos: `posiciones` (ArrayList de 4 ArrayLists de Casilla, uno por lado), `grupos` (HashMap de `Grupo`), `banca`.
    - Constructores: Inicializa los atributos y llama a `generarCasillas`.
    - Métodos: `generarCasillas` (crea todas las instancias de `Solar`, `Transporte`, `Suerte`, etc., y las añade a los lados), `insertarLadoSur/Norte/Este/Oeste`, `encontrar_casilla` (busca una casilla por nombre), `toString` (dibuja el tablero en ASCII usando `representacionColoreada` de cada casilla).
    - Uso: Contiene todas las casillas y grupos; es consultado constantemente por `Menu` y `Avatar` para movimientos y descripciones.

- **Casilla.java** (paquete: monopoly)
    - Clase **padre** que representa una casilla genérica del tablero.
    - Atributos: `tipo`, `nombre`, `valor`, `posicion`, `duenho`, `grupo`, `impuesto`, `hipoteca`, `avatares` (lista de `Avatar` presentes), `tablero` (referencia).
    - Constructores: Varios constructores sobrecargados para distintos tipos de casillas (comprables, de impuestos, especiales).
    - Métodos: `anhadirAvatar`, `eliminarAvatar` (gestionan la lista `avatares`), `evaluarCasilla` (lógica genérica de pago de alquiler/impuestos), `comprarCasilla` (lógica de compra base), `infoCasilla` (genera descripción), `casEnVenta`, `representacionColoreada` (lógica base para colorear solares y mostrar avatares con "&").
    - Uso: Define la interfaz y el comportamiento común de todas las casillas del tablero mediante herencia.

- **Solar.java** (paquete: monopoly)
    - Subclase de `Casilla`. Representa una calle que se puede edificar.
    - Atributos: `edificios` (ArrayList de `Edificio`), `hipotecada` (boolean), `idSolar` (int para indexar en arrays de `Valor`).
    - Métodos: Sobrescribe `evaluarCasilla` (añade lógica de alquiler doblado si se posee el grupo). Sobrescribe `comprarCasilla` (registra la inversión en estadísticas). Sobrescribe `infoCasilla` (muy detallada, incluye edificios, precios y alquileres). Añade métodos para gestionar edificios e hipotecas (`addEdificio`, `eliminarEdificios`, `isHipotecada`, `setHipotecada`).
    - Uso: Es la principal casilla comprable y edificable del juego.

- **Transporte.java** (paquete: monopoly)
    - Subclase de `Casilla`. Representa una estación de transporte.
    - Atributos: Heredados de `Casilla`.
    - Métodos: Sobrescribe `evaluarCasilla` para implementar su lógica de alquiler. Incluye un "truco" para detectar si el pago es por una carta (`tirada == Valor.ALQUILER_TRANSP * 2`).
    - Uso: Propiedad comprable con lógica de alquiler especial.

- **Servicio.java** (paquete: monopoly)
    - Subclase de `Casilla`. Representa una compañía de servicios.
    - Atributos: Heredados de `Casilla`.
    - Métodos: Sobrescribe `evaluarCasilla` para implementar el alquiler basado en la tirada de dados (`4 * tirada * Valor.FACTOR_SERVICIO`).
    - Uso: Propiedad comprable con alquiler variable.

- **Impuesto.java** (paquete: monopoly)
    - Subclase de `Casilla`. Representa una casilla de impuestos.
    - Atributos: Heredados de `Casilla`.
    - Métodos: Sobrescribe `evaluarCasilla` para restar el `impuesto` al jugador y añadirlo al `Parking`.
    - Uso: Implementa la casilla de pago de impuestos.

- **Parking.java** (paquete: monopoly)
    - Subclase de `Casilla` (Especial). Representa el parking gratuito.
    - Atributos: Heredados. Usa `valor` para almacenar el "bote".
    - Métodos: Sobrescribe `evaluarCasilla` para entregar el bote (`getValor()`) al jugador y resetearlo a 0.
    - Uso: Casilla de "bote" donde se acumulan impuestos.

- **IrCarcel.java** (paquete: monopoly)
    - Subclase de `Casilla` (Especial).
    - Atributos: Heredados.
    - Métodos: Sobrescribe `evaluarCasilla` para mover el avatar del jugador a la casilla "Carcel" y establecer su estado a `enCarcel(true)`.
    - Uso: Implementa la casilla "Vaya a la Cárcel".

- **Carcel.java** (paquete: monopoly)
    - Subclase de `Casilla` (Especial). Representa la casilla de la cárcel.
    - Atributos: Heredados.
    - Métodos: `evaluarCasilla` no hace nada (es pasiva). `infoCasilla` muestra los jugadores encarcelados y sus tiradas.
    - Uso: Es la casilla donde los jugadores están de visita o encarcelados.

- **Salida.java** (paquete: monopoly)
    - Subclase de `Casilla` (Especial).
    - Atributos: Heredados.
    - Métodos: `evaluarCasilla` no hace nada (la lógica de *pasar* por salida está en `Avatar.moverAvatar`).
    - Uso: Es la casilla inicial y la que otorga dinero al ser pasada.

- **Suerte.java** (paquete: monopoly)
    - Subclase de `Casilla`.
    - Atributos: Heredados.
    - Métodos: `evaluarCasilla` no hace nada. La lógica real está en `Menu.lanzarDados`, que comprueba `instanceof Suerte` y saca una carta.
    - Uso: Casilla "marcador" que activa el mazo de Suerte.

- **CajaComunidad.java** (paquete: monopoly)
    - Subclase de `Casilla`.
    - Atributos: Heredados.
    - Métodos: `evaluarCasilla` no hace nada. La lógica real está en `Menu.lanzarDados`, que comprueba `instanceof CajaComunidad` y saca una carta.
    - Uso: Casilla "marcador" que activa el mazo de Caja de Comunidad.

- **MazoCartas.java** (paquete: monopoly)
    - Representa un mazo de cartas (Suerte o Caja) que se saca de forma circular.
    - Atributos: `cartas` (ArrayList de `Carta`), `siguiente` (índice), `suerte` (boolean).
    - Constructores: Crea un mazo de Suerte o Caja llamando a `crearMazoSuerte()` o `crearMazoCaja()`.
    - Métodos: `crearMazo...` (define las cartas y sus acciones), `sacarCarta` (obtiene la siguiente y avanza el índice circularmente).
    - Uso: Proporciona a `Menu` la siguiente carta a ejecutar.

- **Carta.java** (paquete: monopoly)
    - Representa una carta de Suerte o Caja.
    - Atributos: `id`, `descripcion`, `accion` (un `enum TipoAccion`), `cantidad`, `destino`.
    - Enumeración `TipoAccion`: Define las acciones posibles (`MOVER_A`, `IR_A_CARCEL`, `COBRAR`, `PAGAR`, etc.).
    - Métodos: `ejecutar` (contiene un `switch` gigante que implementa la lógica para cada `TipoAccion`, moviendo al jugador, pagando, etc.).
    - Uso: Contiene y ejecuta la lógica de una carta específica.

- **Grupo.java** (paquete: monopoly)
    - Representa un grupo de Solares por color.
    - Atributos: `miembros` (lista de `Casilla`), `colorGrupo`, `numCasillas`.
    - Constructores: Para grupos de 2 o 3 miembros. Asigna este grupo a cada `Casilla` miembro.
    - Métodos: `esDuenhoGrupo` (comprueba si un jugador posee todas las casillas del grupo), `getAnsiColor` (traduce el `colorGrupo` string a un código ANSI de `Valor`).
    - Uso: Esencial para la lógica de doblar alquileres en `Solar.evaluarCasilla` y para colorear el tablero.

- **Edificio.java** (paquete: monopoly)
    - Representa un edificio (casa, hotel, piscina, pista) en un `Solar`.
    - Atributos: `id` (generado automáticamente), `tipo`, `propietario`, `casilla`, `grupo`, `coste`. Contadores estáticos para los IDs.
    - Constructores: Asigna atributos y llama a `generarId`.
    - Métodos: `generarId` (crea IDs únicos como `casa-1`, `hotel-1`).
    - Uso: Almacena la información de una edificación. Es contenido dentro de la lista `edificios` de un `Solar`.

- **StatsTracker.java** (paquete: monopoly)
    - Implementa el **Patrón Singleton** para registrar estadísticas globales y por jugador.
    - Atributos: `instance` (static), y `Map` para `visits` (por casilla), `rentCollected` (por casilla y grupo), `laps`, y `byPlayer` (mapea nombre de jugador a `PlayerStats`).
    - Constructores: `private StatsTracker()`.
    - Métodos: `getInstance()` (acceso global), `asegurarJugador` (crea `PlayerStats` si no existe), `registrarVisita`, `registrarAlquiler`, `registrarPagoImpuesto`, `registrarPremioBote`, `registrarPasoSalida`, `registrarEncarcelamiento`, `reporteJugador`, `reporteGlobal` (calcula y formatea estadísticas).
    - Uso: Es llamado desde `Menu`, `Casilla`, `Avatar`, etc., para registrar eventos del juego.

- **Valor.java** (paquete: monopoly)
    - Implementa el patrón **Clase de Utilidad Estática**.
    - Atributos: Constantes `public static final` para todas las reglas del juego (fortunas, precios, alquileres, impuestos) y códigos de color ANSI. Contiene arrays gigantes de datos (ej. `PRECIO_EDIFICIOS`, `ALQUILER_BASE`).
    - Constructores: `private Valor()` para prevenir instanciación.
    - Métodos: `formatear(float)` (método estático para formatear moneda).
    - Uso: Proporciona constantes, utilidades y datos brutos a todo el proyecto.

---
### Paquete `partida` (Entidades del Juego)
---
- **Jugador.java** (paquete: partida)
    - Representa a un jugador (o la banca).
    - Atributos: `nombre`, `avatar` (objeto `Avatar`), `fortuna`, `enCarcel` (boolean), `tiradasCarcel`, `vueltas`, `propiedades` (lista de `Casilla`), y `estadisticas` (objeto `PlayerStats`).
    - Constructores: Vacío para "Banca". El principal crea y valida el `Avatar` asociado.
    - Métodos: `anhadirPropiedad`, `eliminarPropiedad`, `sumarFortuna`, `encarcelar` (mueve el avatar a la cárcel y actualiza el estado `enCarcel`).
    - Uso: Gestiona el estado completo de un jugador, su dinero, propiedades y estado.

- **Avatar.java** (paquete: partida)
    - Representa la pieza del jugador en el tablero.
    - Enumeración `TipoAvatar`: Define los tipos válidos (Coche, Esfinge, etc.).
    - Atributos: `id` (letra aleatoria), `tipo`, `jugador` (dueño), `lugar` (casilla actual).
    - Métodos: `moverAvatar` (movimiento **relativo** por dados; calcula la `newPos`, comprueba si pasa por "Salida" y da dinero). `setLugar` (movimiento **absoluto** o "teletransporte"; gestiona `eliminarAvatar` de la casilla antigua y `anhadirAvatar` a la nueva).
    - Uso: Es la clase responsable de la posición y el movimiento (relativo y absoluto) del jugador en el tablero.

- **Dado.java** (paquete: partida)
    - Representa un dado de 6 caras.
    - Atributos: `valor` (almacena el último resultado).
    - Método principal: `hacerTirada()` genera un valor aleatorio entre 1 y 6.
    - Uso: Simula lanzamientos de dados para movimientos y cálculos de alquiler en servicios.

- **PlayerStats.java** (paquete: partida)
    - Clase contenedora de datos (POJO) para las estadísticas de un solo jugador.
    - Atributos: `dineroInvertido`, `pagoTasasEImpuestos`, `pagoDeAlquileres`, `cobroDeAlquileres`, `pasarPorSalida`, `premiosBote`, `vecesEnLaCarcel`.
    - Métodos: Métodos `add...()` para incrementar valores (ej. `addDineroInvertido`) y `get...()` para leerlos.
    - Uso: Es usado por `StatsTracker` y `Jugador` para almacenar y acceder a las estadísticas individuales.