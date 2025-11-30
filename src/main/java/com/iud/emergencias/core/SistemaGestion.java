package com.iud.emergencias.core;
import com.iud.emergencias.modelo.Ambulancia;
import com.iud.emergencias.modelo.Emergencia;
import com.iud.emergencias.modelo.EstadoAmbulancia;
import com.iud.emergencias.tareas.DespachadorTask;
import com.iud.emergencias.tareas.MonitorTask;
import com.iud.emergencias.tareas.OperadorTask;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Singleton que centraliza la gestión de recursos y colas.
 * Maneja la concurrencia y sincronización entre hilos.
 */
public class SistemaGestion {
    private static SistemaGestion instancia;

    // COLA CONCURRENTE: Ordena automáticamente por prioridad
    private final PriorityBlockingQueue<Emergencia> colaEmergencias;

    // RECURSOS COMPARTIDOS: Flota de ambulancias
    private final List<Ambulancia> flotaAmbulancias;

    // SINCRONIZACIÓN: Lock para controlar el acceso a la flota
    private final ReentrantLock lockAmbulancias = new ReentrantLock();
    private final Condition ambulanciaDisponible = lockAmbulancias.newCondition();

    // Pools de hilos
    private ExecutorService poolOperadores;
    private ExecutorService poolDespachadores;
    private ScheduledExecutorService monitorService;

    // Configuración
    public static final int NUM_AMBULANCIAS = 5;

    private SistemaGestion() {
        this.colaEmergencias = new PriorityBlockingQueue<>();
        this.flotaAmbulancias = new ArrayList<>();
        
        // Inicializar flota
        for (int i = 1; i <= NUM_AMBULANCIAS; i++) {
            flotaAmbulancias.add(new Ambulancia(i));
        }
    }

    public static synchronized SistemaGestion getInstancia() {
        if (instancia == null) {
            instancia = new SistemaGestion();
        }
        return instancia;
    }

    // --- MÉTODOS DE GESTIÓN DE THREADS ---

    public void iniciarComponentes(int numOperadores, int numDespachadores) {
        poolOperadores = Executors.newFixedThreadPool(numOperadores);
        poolDespachadores = Executors.newFixedThreadPool(numDespachadores);
        monitorService = Executors.newSingleThreadScheduledExecutor();

        // Iniciar Operadores
        for (int i = 0; i < numOperadores; i++) {
            poolOperadores.execute(new OperadorTask());
        }

        // Iniciar Despachadores
        for (int i = 0; i < numDespachadores; i++) {
            poolDespachadores.execute(new DespachadorTask());
        }

        // Iniciar Monitor (cada 2 segundos)
        monitorService.scheduleAtFixedRate(new MonitorTask(), 0, 2, TimeUnit.SECONDS);
    }

    public void detenerComponentes() {
        if (poolOperadores != null) poolOperadores.shutdownNow();
        if (poolDespachadores != null) poolDespachadores.shutdownNow();
        if (monitorService != null) monitorService.shutdownNow();
    }

    // --- MÉTODOS THREAD-SAFE DE LÓGICA DE NEGOCIO ---

    public void reportarEmergencia(Emergencia e) {
        System.out.println(" [ALERTA] Nueva emergencia reportada: " + e);
        colaEmergencias.put(e); // Thread-safe
    }

    public Emergencia tomarEmergencia() throws InterruptedException {
        return colaEmergencias.take(); // Bloquea si la cola está vacía
    }

    /**
     * Intenta obtener una ambulancia. Si no hay, el hilo se bloquea (espera)
     * hasta que una se libere.
     */
    public Ambulancia solicitarAmbulancia() throws InterruptedException {
        lockAmbulancias.lock();
        try {
            Ambulancia amb = buscarAmbulanciaLibre();
            while (amb == null) {
                // No hay recursos, esperamos la señal
                ambulanciaDisponible.await();
                amb = buscarAmbulanciaLibre();
            }
            amb.asignarEmergencia();
            return amb;
        } finally {
            lockAmbulancias.unlock();
        }
    }

    public void liberarAmbulancia(Ambulancia amb) {
        lockAmbulancias.lock();
        try {
            amb.liberar();
            // Notificamos a los despachadores que hay una libre
            ambulanciaDisponible.signalAll();
        } finally {
            lockAmbulancias.unlock();
        }
    }

    private Ambulancia buscarAmbulanciaLibre() {
        for (Ambulancia a : flotaAmbulancias) {
            if (a.getEstado() == EstadoAmbulancia.DISPONIBLE) {
                return a;
            }
        }
        return null;
    }

    // Getters para el Monitor
    public int getCantidadPendientes() { return colaEmergencias.size(); }
    public List<Ambulancia> getEstadoFlota() { return new ArrayList<>(flotaAmbulancias); }
}