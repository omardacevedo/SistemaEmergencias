
Sistema de Gestión de Emergencias Médicas (Simulación)

Este proyecto implementa una simulación concurrente en Java para coordinar la asignación de recursos limitados (ambulancias) a incidentes de emergencia en tiempo real. Fue desarrollado como parte del Caso de Estudio para la materia de Desarrollo de Software Seguro en la IUDigital de Antioquia.

Descripción del Proyecto

El sistema simula un entorno de despacho de emergencias donde múltiples actores interactúan simultáneamente:

Operadores (Productores): Reciben llamadas y generan emergencias con diferentes niveles de gravedad y ubicación.

Sistema Central (Gestor): Prioriza las emergencias utilizando una cola de prioridad concurrente.

Despachadores (Consumidores): Asignan ambulancias disponibles a las emergencias más críticas.

Ambulancias (Recursos Compartidos): Viajan al lugar del incidente, atienden al paciente y regresan a la base.

Objetivos Técnicos Cumplidos

Concurrencia: Manejo eficiente de múltiples hilos (Threads) para operadores y despachadores.

Sincronización: Uso de ReentrantLock y Condition para evitar condiciones de carrera (Race Conditions) en el acceso a las ambulancias.

Thread-Safety: Implementación de estructuras de datos seguras como PriorityBlockingQueue.

Priorización: Algoritmo que pondera la gravedad del incidente (Crítica > Leve) y el tiempo de espera.

Comenzando

Sigue estas instrucciones para obtener una copia del proyecto y ejecutarlo en tu máquina local.

Prerrequisitos

Java JDK 17 o superior.

Maven 3.6+ (Gestor de dependencias y construcción).

IDE (NetBeans, IntelliJ, Eclipse o VS Code).

Instalación

Clona el repositorio:

git clone [https://github.com/omardacevedo/SistemaEmergencias.git](https://github.com/omardacevedo/SistemaEmergencias.git)


Navega al directorio del proyecto:

cd SistemaEmergencias



Compilación y Ejecución

Este proyecto utiliza Maven para facilitar la compilación.

Desde la Terminal

Compilar el proyecto:

mvn clean compile



Ejecutar la simulación:

mvn exec:java



Desde NetBeans / IDE

Abre el proyecto como "Maven Project".

Haz clic derecho sobre el proyecto -> Run.

 Estructura del Proyecto

El código está organizado en paquetes para asegurar la modularidad y limpieza:

com.iudigital.emergencias
├── core       # Lógica central (Singleton SistemaGestion)
├── modelo     # Entidades (Emergencia, Ambulancia, Ubicacion)
├── tareas     # Hilos de ejecución (Operador, Despachador, Monitor)
└── Main.java  # Punto de entrada



 Configuración de la Simulación

Puedes ajustar los parámetros de la simulación editando las constantes en la clase SistemaGestion.java y Main.java:

NUM_AMBULANCIAS: Cantidad de recursos disponibles.

TIEMPO_SIMULACION: Duración de la ejecución automática.

 Documentación

La documentación técnica detallada, incluyendo diagramas de clases y análisis de sincronización, se encuentra en la carpeta /docs.

Manual Técnico

 Autores

Omar Danilo Acevedo Rojas- Desarrollo e Implementación - IUDigital

Este proyecto es de carácter académico para la asignatura de Desarrollo de Software Seguro.