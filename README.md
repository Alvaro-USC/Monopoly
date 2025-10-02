# MonopolyETSE - Parte 1

## Descripción del Proyecto
Este proyecto implementa una versión básica del juego Monopoly en Java, enfocado en programación orientada a objetos. En esta primera parte, se crea el tablero, casillas, jugadores, avatares y mecánicas básicas como movimiento, compra y alquileres. El juego se controla mediante comandos en consola, y el tablero se representa en modo texto ASCII con colores ANSI para grupos de solares. Avatares se muestran con "&" seguido de su ID.

El proyecto se divide en paquetes `monopoly` (clases principales del juego) y `partida` (clases relacionadas con jugadores, avatares y dados). Se soporta ejecución interactiva o desde un archivo de comandos.

## Cómo Ejecutar
- **Compilación**: Compila todos los archivos `.java` con `javac monopoly/*.java partida/*.java`.
- **Ejecución interactiva**: `java monopoly.MonopolyETSE`.
- **Ejecución con archivo de comandos**: `java monopoly.MonopolyETSE comandos.txt` (donde `comandos.txt` contiene comandos como "crear jugador mic Coche").

## Archivos y su Función
A continuación, se describe cada archivo del proyecto, su ubicación y qué hace:

- **Dado.java** (paquete: partida)
  - Representa un dado de 6 caras.
  - Atributo principal: `valor` (entero para almacenar el resultado de la tirada).
  - Método principal: `hacerTirada()` genera un valor aleatorio entre 1 y 6 usando `Math.random()` y lo retorna.
  - Uso: Simula lanzamientos de dados para movimientos y cálculos de alquiler en servicios.

- **Jugador.java** (paquete: partida)
  - Representa a un jugador del juego, incluyendo la banca.
  - Atributos: `nombre`, `avatar`, `fortuna`, `gastos`, `enCarcel`, `tiradasCarcel`, `vueltas`, `propiedades` (lista de casillas).
  - Constructores: Vacío para banca; principal para jugadores normales, crea y valida el avatar.
  - Métodos: `anhadirPropiedad`, `eliminarPropiedad`, `sumarFortuna`, `sumarGastos`, `encarcelar` (mueve avatar a cárcel y establece estado encarcelado).
  - Getters/setters para todos los atributos.
  - Uso: Gestiona el estado de cada jugador, fortunas, propiedades y encarcelamiento.

- **Avatar.java** (paquete: partida)
  - Representa el avatar de un jugador en el tablero.
  - Enumeración `TipoAvatar`: Define tipos válidos (Coche, Esfinge, Sombrero, Pelota) con validación.
  - Atributos: `id` (letra aleatoria), `tipo`, `jugador`, `lugar` (casilla actual).
  - Constructores: Vacío; principal valida tipo, genera ID único y asocia jugador.
  - Métodos: `moverAvatar` (mueve a nueva posición, suma vuelta si pasa Salida); `generarId` (genera ID único).
  - Getters para todos los atributos.
  - Uso: Maneja el movimiento y posición del avatar en el tablero, asegurando IDs únicos y tipos válidos.

- **ComandoArchivo.java** (paquete: monopoly)
  - Clase para manejar la lectura y procesamiento de archivos de comandos.
  - Atributos: `menu` (referencia a Menu), `archivo` (ruta del archivo).
  - Constructor: Inicializa con Menu y archivo.
  - Método `procesarComandos()`: Verifica existencia del archivo, lee líneas con BufferedReader, ignora vacías e invoca `menu.analizarComando` para cada comando.
  - Uso: Permite ejecutar comandos desde un archivo de texto en lugar de entrada manual.

- **Casilla.java** (paquete: monopoly)
  - Representa una casilla del tablero.
  - Atributos: `tipo`, `nombre`, `valor`, `posicion`, `duenho`, `grupo`, `impuesto`, `hipoteca`, `avatares` (lista de avatares), `tablero`.
  - Constructores: Vacío; para solares/servicios/transportes; para impuestos; para otras (Suerte, Caja, especiales).
  - Métodos: `anhadirAvatar`, `eliminarAvatar`, `evaluarCasilla` (maneja alquileres, impuestos, parking, ir a cárcel); `comprarCasilla`, `sumarValor`, `setValor`, `infoCasilla`, `casEnVenta`, `representacionColoreada` (aplica color y muestra avatares con "&").
  - Getters/setters para todos los atributos.
  - Uso: Gestiona las casillas, evaluaciones al caer en ellas y representación visual en el tablero.

- **MonopolyETSE.java** (paquete: monopoly)
  - Clase principal para iniciar el juego.
  - Método `main`: Llama a `new Menu(args)` para manejar argumentos de línea de comandos (e.g., archivo de comandos).
  - Uso: Punto de entrada del programa.

- **Valor.java** (paquete: monopoly)
  - Clase de constantes para valores del juego y códigos ANSI de colores.
  - Constantes: Fortunas, sumas, impuestos, valores de casillas, factores, y colores (RESET, RED, etc.).
  - Uso: Proporciona valores fijos y códigos de color para el tablero y cálculos.

- **Grupo.java** (paquete: monopoly)
  - Representa un grupo de casillas solares con color.
  - Atributos: `miembros` (lista de casillas), `colorGrupo`, `numCasillas`.
  - Constructores: Vacío; para 2 o 3 casillas.
  - Métodos: `anhadirCasilla`, `esDuenhoGrupo` (verifica si un jugador posee el grupo completo), `getColorGrupo`, `getAnsiColor` (devuelve código ANSI basado en color).
  - Uso: Gestiona grupos para doblar alquileres y aplicar colores en el tablero.

- **Menu.java** (paquete: monopoly)
  - Clase principal para la interfaz de consola.
  - Atributos: Listas de jugadores/avatares, turno, lanzamientos, tablero, dados, banca, banderas (tirado, solvente, partidaIniciada).
  - Constructor: Inicializa, muestra bienvenida, procesa archivo de comandos si proporcionado, o inicia modo interactivo con Scanner.
  - Métodos: `iniciarPartida`, `analizarComando` (procesa comandos como crear jugador, lanzar dados, etc.), `descJugador`, `descAvatar`, `descCasilla`, `lanzarDados` (maneja tiradas, movimientos, evaluaciones), `comprar`, `salirCarcel`, `listarVenta`, `listarJugadores`, `listarAvatares`, `acabarTurno`.
  - Uso: Gestiona la interacción con el usuario, comandos y estado del juego.

### Notas finales
- Este README.md se basa en el código actual. Para más detalles, consulta el PDF del proyecto.
- Si necesitas agregar secciones como "Dependencias" o "Contribuciones", dímelo.
