package co.edu.etitc.grigora.modelo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

/** Almacén en memoria de los salones (compartido por todos los servlets). */
public final class SalonRepositorio {

    public static final List<String> BLOQUES = Collections.unmodifiableList(
            Arrays.asList("Bloque A", "Bloque B", "Bloque C", "Bloque E"));

    private static final SalonRepositorio INSTANCIA = new SalonRepositorio();

    private final List<Salon> salones = new CopyOnWriteArrayList<>();

    private SalonRepositorio() {
        // Mismos datos de ejemplo que app.js
        salones.add(new Salon("A101", "Aula de sistemas", "Bloque A", 1, 2, 110, 90));
        salones.add(new Salon("B204", "Laboratorio de física", "Bloque B", 2, 4, 455, 85));
        salones.add(new Salon("C305", "Sala de dibujo técnico", "Bloque C", 3, 6, 135, 305));
        salones.add(new Salon("E102", "Auditorio principal", "Bloque E", 1, 3, 455, 305));
    }

    // Zonas del mapa (coordenadas del svg) por bloque: {minX, maxX, minY, maxY}
    private static final java.util.Map<String, int[]> ZONAS = new java.util.HashMap<>();
    static {
        ZONAS.put("Bloque A", new int[] {55, 165, 55, 130});
        ZONAS.put("Bloque B", new int[] {395, 520, 45, 130});
        ZONAS.put("Bloque C", new int[] {75, 200, 265, 345});
        ZONAS.put("Bloque E", new int[] {395, 515, 265, 345});
    }

    /** Posición {x, y} aleatoria dentro del bloque indicado (igual que hacía app.js). */
    public static int[] posicionEnBloque(String bloque) {
        int[] z = ZONAS.get(bloque);
        ThreadLocalRandom r = ThreadLocalRandom.current();
        return new int[] {r.nextInt(z[0], z[1] + 1), r.nextInt(z[2], z[3] + 1)};
    }

    public static SalonRepositorio getInstancia() {
        return INSTANCIA;
    }

    public List<Salon> listar() {
        return Collections.unmodifiableList(salones);
    }

    public Salon buscarPorCodigo(String codigo) {
        for (Salon s : salones) {
            if (s.coincideConCodigo(codigo)) {
                return s;
            }
        }
        return null;
    }

    /** Agrega el salón si no existe otro con el mismo código. Devuelve false si ya existía. */
    public synchronized boolean agregar(Salon nuevo) {
        if (buscarPorCodigo(nuevo.getCodigo()) != null) {
            return false;
        }
        salones.add(nuevo);
        return true;
    }
}
