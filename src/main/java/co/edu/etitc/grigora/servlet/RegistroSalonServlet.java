package co.edu.etitc.grigora.servlet;

import co.edu.etitc.grigora.modelo.Salon;
import co.edu.etitc.grigora.modelo.SalonRepositorio;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Semana 8 (servlets). Recibe el formulario de registro de index.html (método POST),
 * valida los datos, registra el salón y responde con una página dinámica.
 * Ruta registrada con @WebServlet.
 */
@WebServlet(name = "RegistroSalonServlet", urlPatterns = "/registrar-salon")
public class RegistroSalonServlet extends HttpServlet {

    private static final Pattern CODIGO = Pattern.compile("^[A-Za-z]\\d{3}$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Entrar por URL directa lleva al formulario
        response.sendRedirect(request.getContextPath() + "/index.html#registro");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8"); // tildes y ñ del formulario
        response.setContentType("text/html;charset=UTF-8");
        String ctx = request.getContextPath();

        String codigo = limpiar(request.getParameter("codigo"));
        String nombre = limpiar(request.getParameter("nombre"));
        String bloque = limpiar(request.getParameter("bloque"));
        String pisoTxt = limpiar(request.getParameter("piso"));
        String minutosTxt = limpiar(request.getParameter("minutos"));

        // ----- Validaciones básicas -----
        List<String> errores = new ArrayList<>();

        if (codigo.isEmpty()) {
            errores.add("El código del salón es obligatorio.");
        } else if (!CODIGO.matcher(codigo).matches()) {
            errores.add("El código debe tener una letra y tres números, por ejemplo A105.");
        }
        if (nombre.isEmpty()) {
            errores.add("El nombre del salón es obligatorio.");
        } else if (nombre.length() > 60) {
            errores.add("El nombre no puede superar 60 caracteres.");
        }
        if (bloque.isEmpty()) {
            errores.add("Debes elegir un bloque.");
        } else if (!SalonRepositorio.BLOQUES.contains(bloque)) {
            errores.add("El bloque elegido no existe en el campus.");
        }
        int piso = entero(pisoTxt, "piso", 1, 10, errores);
        int minutos = entero(minutosTxt, "minutos caminando", 1, 60, errores);

        if (!errores.isEmpty()) {
            responderErrores(response, ctx, errores, HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        codigo = codigo.toUpperCase();
        int[] pos = SalonRepositorio.posicionEnBloque(bloque);
        boolean agregado = SalonRepositorio.getInstancia()
                .agregar(new Salon(codigo, nombre, bloque, piso, minutos, pos[0], pos[1]));

        if (!agregado) {
            errores.add("Ya existe un salón registrado con el código " + codigo + ".");
            responderErrores(response, ctx, errores, HttpServletResponse.SC_CONFLICT);
            return;
        }

        // ----- Respuesta dinámica con response.getWriter() -----
        String cuerpo = "<article class=\"card destacado\"><h2>¡Salón registrado!</h2>"
                + "<p><strong>" + Pagina.escapar(codigo) + "</strong> · " + Pagina.escapar(nombre) + "</p>"
                + "<p>" + Pagina.escapar(bloque) + ", piso " + piso + " · " + minutos + " min caminando desde la entrada.</p>"
                + "</article>"
                + "<article class=\"card\"><p><a href=\"" + ctx + "/consulta?codigo=" + Pagina.escapar(codigo)
                + "\">Ver el salón registrado</a></p><p><a href=\"" + ctx + "/consulta\">Ver todos los salones</a></p>"
                + "<p><a href=\"" + ctx + "/index.html#registro\">Registrar otro salón</a></p>"
                + "<p><a href=\"" + ctx + "/index.html#mapa\">Verlo en el mapa</a></p></article>";
        try (PrintWriter out = response.getWriter()) {
            out.print(Pagina.envolver(ctx, "Salón registrado", cuerpo));
        }
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    /** Convierte a entero validando que no esté vacío, sea numérico y esté en rango. */
    private static int entero(String texto, String campo, int min, int max, List<String> errores) {
        if (texto.isEmpty()) {
            errores.add("El campo " + campo + " es obligatorio.");
            return 0;
        }
        try {
            int n = Integer.parseInt(texto);
            if (n < min || n > max) {
                errores.add("El campo " + campo + " debe estar entre " + min + " y " + max + ".");
            }
            return n;
        } catch (NumberFormatException e) {
            errores.add("El campo " + campo + " debe ser un número entero.");
            return 0;
        }
    }

    private void responderErrores(HttpServletResponse response, String ctx, List<String> errores, int estado)
            throws IOException {
        response.setStatus(estado);
        StringBuilder lista = new StringBuilder("<ul>");
        for (String e : errores) {
            lista.append("<li>").append(Pagina.escapar(e)).append("</li>");
        }
        lista.append("</ul>");
        String cuerpo = "<article class=\"card destacado\"><h2>No se pudo registrar el salón</h2>" + lista
                + "<p><a href=\"" + ctx + "/index.html#registro\">← Volver al formulario</a></p></article>";
        try (PrintWriter out = response.getWriter()) {
            out.print(Pagina.envolver(ctx, "Error en el registro", cuerpo));
        }
    }
}
