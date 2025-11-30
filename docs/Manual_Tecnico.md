Informe Técnico: Sistema de Gestión de Emergencias Médicas

Proyecto: Simulación de Sistema de Despacho de Emergencias (Concurrencia en Java)

Fecha: Noviembre 2025

Versión: 1.0

1. Arquitectura y Diseño del Sistema

1.1 Descripción General

El sistema implementa una arquitectura basada en el patrón Productor-Consumidor con un mediador central (Singleton). Este diseño desacopla la generación de eventos (llamadas de emergencia) de su procesamiento (despacho de ambulancias), permitiendo manejar picos de demanda sin bloquear el sistema.

1.2 Diagrama de Clases (UML)

El siguiente diagrama ilustra la relación entre las entidades principales, el gestor central y las tareas concurrentes.

classDiagram
    %% Core System
    class SistemaGestion {
        -PriorityBlockingQueue~Emergencia~ colaEmergencias
        -List~Ambulancia~ flotaAmbulancias
        -ReentrantLock lockAmbulancias
        -Condition ambulanciaDisponible
        +getInstancia() SistemaGestion
        +reportarEmergencia(Emergencia e)
        +solicitarAmbulancia() Ambulancia
        +liberarAmbulancia(Ambulancia a)
    }
    
    %% Domain Entities
    class Emergencia {
        -int id
        -NivelGravedad gravedad
        -long tiempoCreacion
        +compareTo(Emergencia o) int
    }
    
    class Ambulancia {
        -int id
        -EstadoAmbulancia estado
        -Ubicacion ubicacionActual
        +asignarEmergencia()
        +liberar()
    }

    %% Concurrent Tasks
    class OperadorTask {
        <<Runnable>>
        +run()
    }
    
    class DespachadorTask {
        <<Runnable>>
        +run()
    }

    class AtencionMedicaTask {
        <<Runnable>>
        +run()
    }

    %% Relationships
    SistemaGestion "1" --* "*" Ambulancia : Contiene
    SistemaGestion "1" --o "*" Emergencia : Gestiona Cola
    OperadorTask ..> SistemaGestion : Produce Emergencias
    DespachadorTask ..> SistemaGestion : Consume Emergencias
    DespachadorTask ..> AtencionMedicaTask : Crea e Inicia
    AtencionMedicaTask ..> Ambulancia : Usa Recurso






1.3 Componentes Principales

SistemaGestion (Singleton): El "cerebro" del sistema. Mantiene el estado global, las colas y la sincronización de recursos. Es el único punto de acceso para solicitar o liberar ambulancias.

OperadorTask (Productor): Simula la entrada de llamadas. Genera emergencias con gravedad y ubicación aleatoria e intervalos estocásticos.

DespachadorTask (Consumidor): Monitorea la cola de emergencias. Su ciclo de vida es: Esperar Emergencia -> Solicitar Ambulancia -> Iniciar Atención.

PriorityBlockingQueue: Componente crítico que almacena las emergencias. No es una cola FIFO simple; ordena dinámicamente los elementos basándose en la gravedad de la emergencia.

2. Estrategias de Sincronización

La integridad de los datos en un entorno concurrente se garantiza mediante tres estrategias clave:

2.1 Sincronización de Cola (Estructuras Concurrentes)

En lugar de usar synchronized sobre una ArrayList (lo cual sería ineficiente), se utilizó java.util.concurrent.PriorityBlockingQueue.

Mecanismo: Internamente utiliza bloqueos (locks) finos para permitir inserciones y extracciones seguras.

Ventaja: Si la cola está vacía, el hilo del Despachador se bloquea (duerme) automáticamente en la operación take(), eliminando la necesidad de "busy waiting" (bucles infinitos de espera) y ahorrando CPU.

2.2 Gestión de Recursos Limitados (Lock & Condition)

El desafío más complejo es manejar un número finito de ambulancias (Recursos Compartidos) con múltiples despachadores compitiendo por ellas.

ReentrantLock: Se usa un cerrojo explícito para proteger el acceso a la lista flotaAmbulancias. Solo un hilo puede buscar o modificar el estado de una ambulancia a la vez.

Condition (ambulanciaDisponible): Implementa el patrón de Guarded Suspension.

Cuando un despachador pide una ambulancia y todas están ocupadas, no falla ni retorna null. Ejecuta ambulanciaDisponible.await(). Esto libera el lock y pone al hilo en espera.

Cuando una ambulancia termina su tarea, ejecuta ambulanciaDisponible.signalAll(), despertando a los despachadores para que intenten obtener el recurso nuevamente.

2.3 Atomicidad

Para la generación de IDs únicos de emergencias, se utilizó AtomicInteger. Esto asegura que dos hilos creando emergencias al mismo tiempo nunca obtengan el mismo ID, sin necesidad de bloquear todo el objeto.

3. Análisis de Rendimiento

Se realizaron pruebas simuladas bajo diferentes configuraciones de carga para evaluar la robustez del sistema.

Escenario A: Baja Carga (Normal)

Configuración: 2 Operadores, 5 Ambulancias.

Comportamiento: La cola de emergencias se mantiene casi siempre vacía. Las ambulancias pasan la mayor parte del tiempo en estado DISPONIBLE.

Tiempo de Espera: Despreciable. Las emergencias son atendidas casi instantáneamente.

Escenario B: Alta Carga (Saturación)

Configuración: 10 Operadores, 3 Ambulancias.

Comportamiento: La tasa de llegada de emergencias ($\lambda$) supera la tasa de servicio ($\mu$).

Observación:

La PriorityBlockingQueue comienza a crecer.

El algoritmo de priorización se vuelve evidente: las emergencias CRITICAS saltan al principio de la fila, mientras que las LEVES sufren "inanición" (starvation) temporal, esperando mucho más tiempo.

Todos los despachadores terminan bloqueados en await(), esperando que se libere una ambulancia.

Escenario C: Recuperación

Al detener los operadores pero mantener los despachadores activos, el sistema procesa el remanente de la cola eficientemente hasta vaciarla, demostrando que no hay Deadlocks (bloqueos mutuos) que impidan la finalización de tareas.

4. Conclusiones y Lecciones Aprendidas

4.1 Conclusiones

Eficacia de las Colas Bloqueantes: El uso de BlockingQueue simplifica enormemente el código al eliminar la necesidad de gestionar manualmente los wait() y notify() para la llegada de datos.

Priorización Real: El sistema cumple exitosamente con el requisito de atender primero lo urgente. En pruebas de estrés, se observó cómo incidentes críticos ingresados tardíamente fueron atendidos antes que incidentes leves antiguos.

Escalabilidad: El diseño permite aumentar el número de operadores o despachadores simplemente ajustando el tamaño del ThreadPool, sin cambiar la lógica del negocio.

4.2 Lecciones Aprendidas

Peligro de Deadlocks: Inicialmente, hubo riesgos de bloqueo si se intentaba sincronizar demasiados objetos anidados. La solución fue mantener los bloqueos lo más cortos posible (solo para buscar/asignar ambulancia) y realizar la tarea larga (simulación de atención médica) fuera del bloque sincronizado.

Importancia del finally: Al trabajar con Locks explícitos, es vital liberar el lock en un bloque finally. De no hacerlo ante una excepción, el sistema entero podría congelarse.

Monitoreo: En sistemas concurrentes, es difícil depurar paso a paso. La implementación de un hilo de monitoreo (MonitorTask) que imprime el estado cada pocos segundos fue indispensable para entender el comportamiento del sistema en tiempo real.