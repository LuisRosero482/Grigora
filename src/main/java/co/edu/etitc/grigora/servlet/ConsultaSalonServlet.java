package co.edu.etitc.grigora.servlet;

import co.edu.etitc.grigora.modelo.Salon;
import co.edu.etitc.grigora.modelo.SalonRepositorio;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Semana 7 (despliegue). Registrado en WEB-INF/web.xml con <servlet> y <servlet-mapping> en /consulta.
 *
 *   GET /consulta              -> listado de todos los salones
 *   GET /consulta?codigo=B204  -> ubicación del salón B204
 *   GET /consulta?formato=json -> listado en JSON (lo usa app.js para llenar el mapa)
 */
public class ConsultaSalonServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if ("json".equals(request.getParameter("formato"))) {
            responderJson(response);
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        String ctx = request.getContextPath();
        String codigo = request.getParameter("codigo");
        SalonRepositorio repo = SalonRepositorio.getInstancia();
        StringBuilder html = new StringBuilder();

        if (codigo == null || codigo.trim().isEmpty()) {
            for (Salon s : repo.listar()) {
                html.append(tarjeta(s, ctx));
            }
            html.append(volver(ctx));
            escribir(response, ctx, "Salones del campus", html);
            return;
        }

        Salon salon = repo.buscarPorCodigo(codigo);
        if (salon == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            html.append("<article class=\"card destacado\"><h2>No encontrado</h2><p>No encontré ningún salón con el código \"")
                .append(Pagina.escapar(codigo.trim())).append("\".</p></article>")
                .append(volver(ctx));
            escribir(response, ctx, "Salón no encontrado", html);
            return;
        }

        html.append(tarjeta(salon, ctx)).append(volver(ctx));
        escribir(response, ctx, "Salón " + salon.getCodigo(), html);
    }

    private void responderJson(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        StringBuilder json = new StringBuilder("[");
        boolean primero = true;
        for (Salon s : SalonRepositorio.getInstancia().listar()) {
            if (!primero) {
                json.append(',');
            }
            primero = false;
            json.append("{\"codigo\":\"").append(js(s.getCodigo()))
                .append("\",\"nombre\":\"").append(js(s.getNombre()))
                .append("\",\"bloque\":\"").append(js(s.getBloque()))
                .append("\",\"piso\":").append(s.getPiso())
                .append(",\"minutos\":").append(s.getMinutos())
                .append(",\"x\":").append(s.getX())
                .append(",\"y\":").append(s.getY()).append('}');
        }
        json.append(']');
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
        }
    }

    private static String js(String t) {
        return t.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String tarjeta(Salon s, String ctx) {
        return "<article class=\"card\"><h2>" + Pagina.escapar(s.getCodigo()) + "</h2><p>"
             + Pagina.escapar(s.obtenerDescripcion()) + "</p><span>" + s.getMinutos()
             + " min caminando desde la entrada</span></article>\n";
    }

    private String volver(String ctx) {
        return "<article class=\"card\"><p><a href=\"" + ctx + "/index.html#buscador\">← Volver al mapa</a></p></article>";
    }

    private void escribir(HttpServletResponse response, String ctx, String titulo, StringBuilder cuerpo)
            throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print(Pagina.envolver(ctx, titulo, cuerpo.toString()));
        }
    }
}
