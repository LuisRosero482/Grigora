package co.edu.etitc.grigora.modelo;

/** Entidad principal de Grigora: un salón del campus (equivale a la clase Salon de app.js). */
public class Salon {
    private final String codigo;
    private final String nombre;
    private final String bloque;
    private final int piso;
    private final int minutos;
    private final int x; // posición en el mapa SVG (viewBox 0 0 600 400)
    private final int y;

    public Salon(String codigo, String nombre, String bloque, int piso, int minutos, int x, int y) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.bloque = bloque;
        this.piso = piso;
        this.minutos = minutos;
        this.x = x;
        this.y = y;
    }

    public String getCodigo()  { return codigo; }
    public String getNombre()  { return nombre; }
    public String getBloque()  { return bloque; }
    public int getPiso()       { return piso; }
    public int getMinutos()    { return minutos; }
    public int getX()          { return x; }
    public int getY()          { return y; }

    public boolean coincideConCodigo(String texto) {
        return texto != null && codigo.equalsIgnoreCase(texto.trim());
    }

    public String obtenerDescripcion() {
        return nombre + " · " + bloque + ", piso " + piso;
    }
}
