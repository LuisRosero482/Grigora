[README.md](https://github.com/user-attachments/files/33188953/README.md)
# Grigora – Despliegue y Servlets (Semanas 7 y 8)

Navegación interactiva para llegar a los salones del campus (ETITC).

## Estructura Maven
```
pom.xml                                  packaging war -> target/grigora.war
src/main/java/.../modelo/                Salon, SalonRepositorio
src/main/java/.../servlet/               ConsultaSalonServlet (web.xml), RegistroSalonServlet (@WebServlet)
src/main/webapp/WEB-INF/web.xml          descriptor de despliegue
src/main/webapp/                         index.html, styles.css, app.js (tu interfaz, conectada a los servlets)
```

## Semana 7 – Despliegue
- `web.xml`: `<servlet>` + `<servlet-mapping>` de `ConsultaSalonServlet` en `/consulta`.
- Empaquetar: `mvn clean package` -> `target/grigora.war`
- Desplegar: copiar el `.war` a `webapps/` de **Tomcat 9** y abrir
  - http://localhost:8080/grigora/consulta  (todos los salones)
  - http://localhost:8080/grigora/consulta?codigo=B204

## Semana 8 – Servlets
- El formulario "¿No aparece tu salón? Regístralo" de `index.html` envía por POST a `/registrar-salon`.
- `RegistroSalonServlet` (`@WebServlet("/registrar-salon")`) usa `request.getParameter()`,
  valida (obligatorios, vacíos, formato A105, rangos, bloque válido, duplicados) y responde con `response.getWriter()`.
- `app.js` pide `consulta?formato=json` al cargar, así que los salones registrados aparecen en el listado y en el mapa.
  Si se abre `index.html` sin servidor, usa los datos de ejemplo.

## Nota Tomcat 10+
Este proyecto usa `javax.servlet` (Tomcat 9). Para Tomcat 10+ cambiar a `jakarta.servlet-api` 5.0/6.0
y reemplazar los imports `javax.servlet` por `jakarta.servlet`.

## Commits
```
git add .
git commit -m "semana07-despliegue-web"
git push
# después de probar el formulario:
git commit --allow-empty -m "semana08-servlets"   # o haz el commit con los cambios de la semana 8 por separado
git push
```

  
   ## Entrega semana 7
   Despliegue web: pom.xml (war), WEB-INF/web.xml y ConsultaSalonServlet en /consulta.

   
   ## Entrega semana 8
   Servlets: formulario de registro en index.html conectado a RegistroSalonServlet (/registrar-salon) con validaciones.
