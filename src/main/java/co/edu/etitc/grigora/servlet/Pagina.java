package co.edu.etitc.grigora.servlet;

/** Utilidades para generar las respuestas HTML de los servlets. */
final class Pagina {

    private Pagina() { }

    /** Escapa texto del usuario para evitar inyección de HTML (XSS). */
    static String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                    .replace("\"", "&quot;").replace("'", "&#39;");
    }

    /** Envuelve un contenido en la página con el estilo de Grigora. */
    static String envolver(String contextPath, String titulo, String cuerpo) {
        return "<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n<meta charset=\"UTF-8\">\n"
             + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
             + "<title>" + escapar(titulo) + " · Grigora</title>\n"
             + "<link rel=\"stylesheet\" href=\"" + contextPath + "/styles.css\">\n</head>\n<body>\n"
             + "<header class=\"hero\"><nav class=\"menu\"><strong>Grigora</strong>"
             + "<a href=\"" + contextPath + "/index.html\">Inicio</a>"
             + "<a href=\"" + contextPath + "/index.html#registro\">Registrar salón</a>"
             + "<a href=\"" + contextPath + "/consulta\">Ver salones</a></nav>"
             + "<h1>" + escapar(titulo) + "</h1></header>\n"
             + "<main class=\"grid\">\n" + cuerpo + "\n</main>\n"
             + "<footer>Instituto Técnico Central · Proyecto Grigora</footer>\n</body>\n</html>";
    }
}
