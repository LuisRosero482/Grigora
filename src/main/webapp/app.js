// ===== Programación Orientada a Objetos =====
// La entidad principal del proyecto es el "Salón". Cada salón del campus
// se representa como una instancia de esta clase, con sus propiedades
// (código, nombre, bloque, piso, minutos caminando y posición en el mapa)
// y con comportamientos propios (métodos) en lugar de simples objetos sueltos.
class Salon {
  constructor(codigo, nombre, bloque, piso, minutos, x, y) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.bloque = bloque;
    this.piso = piso;
    this.minutos = minutos;
    this.x = x; // posición horizontal dentro del mapa SVG (viewBox 0 0 600 400)
    this.y = y; // posición vertical dentro del mapa SVG
  }

  // Método: compara el código del salón con el texto buscado, sin
  // importar mayúsculas/minúsculas ni espacios sobrantes.
  coincideConCodigo(texto) {
    return this.codigo.toLowerCase() === texto.trim().toLowerCase();
  }

  // Método: arma la descripción legible del salón para mostrarla en la interfaz.
  obtenerDescripcion() {
    return `${this.nombre} · ${this.bloque}, piso ${this.piso}`;
  }
}

// ===== Datos de ejemplo =====
// Datos de respaldo (por si se abre index.html sin servidor). Cuando la app
// corre en Tomcat, cargarSalonesDelServidor() los reemplaza con los salones
// que entrega el servlet /consulta (incluidos los registrados por el formulario).
// Cada salón se crea con "new Salon(...)", es decir, es un objeto creado
// a partir de la clase definida arriba.
const salones = [
  new Salon('A101', 'Aula de sistemas',      'Bloque A', 1, 2, 110, 90),
  new Salon('B204', 'Laboratorio de física', 'Bloque B', 2, 4, 455, 85),
  new Salon('C305', 'Sala de dibujo técnico','Bloque C', 3, 6, 135, 305),
  new Salon('E102', 'Auditorio principal',   'Bloque E', 1, 3, 455, 305),
];

// Punto fijo de partida en el mapa (la entrada principal)
const origen = { x: 300, y: 180 };

// ===== Funciones =====

// Busca un salón por su código (ignorando mayúsculas/minúsculas).
// Recorre el arreglo de objetos "salones" usando el método de la clase.
function buscarPorCodigo(codigo) {
  return salones.find(salon => salon.coincideConCodigo(codigo));
}

// Crea el HTML de una tarjeta de salón a partir de un objeto Salon
function crearTarjetaSalon(salon) {
  const tarjeta = document.createElement('article');
  tarjeta.className = 'card card--clic';
  tarjeta.innerHTML = `
    <h2>${salon.codigo}</h2>
    <p>${salon.obtenerDescripcion()}</p>
    <span>${salon.minutos} min caminando</span>
  `;
  // Al hacer clic en la tarjeta, se traza la ruta en el mapa hacia ese salón
  tarjeta.addEventListener('click', () => dibujarRuta(salon));
  return tarjeta;
}

// Dibuja una lista de salones dentro del contenedor
function mostrarSalones(lista) {
  const contenedor = document.getElementById('lista-salones');
  contenedor.innerHTML = '';
  lista.forEach(salon => {
    contenedor.appendChild(crearTarjetaSalon(salon));
  });
}

// Guarda el temporizador del efecto "marchando" para poder cancelarlo si se busca de nuevo
let temporizadorRuta = null;

// Dibuja la ruta en el mapa desde el origen hasta el salón indicado
function dibujarRuta(salon) {
  const ruta = document.getElementById('ruta');
  const pinDestino = document.getElementById('pin-destino');
  const pinPulso = document.getElementById('pin-destino-pulso');
  const pinLabel = document.getElementById('pin-destino-label');
  const info = document.getElementById('mapa-info');

  clearTimeout(temporizadorRuta);
  ruta.classList.remove('marchando');

  // Punto intermedio para curvar un poco la línea (no ir en línea recta perfecta)
  const puntoMedioX = (origen.x + salon.x) / 2;
  const puntoMedioY = origen.y - 20;

  const d = `M ${origen.x} ${origen.y} Q ${puntoMedioX} ${puntoMedioY}, ${salon.x} ${salon.y}`;
  ruta.setAttribute('d', d);

  // Anima el trazo de la ruta como si se fuera dibujando
  const largo = ruta.getTotalLength();
  ruta.style.strokeDasharray = largo;
  ruta.style.strokeDashoffset = largo;
  ruta.style.transition = 'none';
  // pequeño retraso para forzar el reinicio de la animación cada vez que se busca
  requestAnimationFrame(() => {
    ruta.style.transition = 'stroke-dashoffset 1s ease-out';
    ruta.style.strokeDashoffset = '0';
  });

  // Cuando termina de dibujarse, activa el efecto de "ruta en movimiento"
  temporizadorRuta = setTimeout(() => {
    ruta.style.strokeDasharray = '';
    ruta.style.strokeDashoffset = '';
    ruta.classList.add('marchando');
  }, 1050);

  // Ubica el pin de destino y su pulso sobre el salón encontrado
  pinDestino.setAttribute('cx', salon.x);
  pinDestino.setAttribute('cy', salon.y);
  pinDestino.setAttribute('r', 8);

  pinPulso.setAttribute('cx', salon.x);
  pinPulso.setAttribute('cy', salon.y);
  pinPulso.setAttribute('r', 8);
  pinPulso.classList.add('activo');

  pinLabel.setAttribute('x', salon.x);
  pinLabel.setAttribute('y', salon.y - 16);
  pinLabel.textContent = salon.codigo;

  info.textContent = `Ruta desde la entrada hasta ${salon.codigo} · ${salon.minutos} min caminando.`;
}

// Borra la ruta del mapa (cuando no hay búsqueda o no se encontró el salón)
function limpiarRuta(mensaje) {
  clearTimeout(temporizadorRuta);
  const ruta = document.getElementById('ruta');
  ruta.classList.remove('marchando');
  ruta.setAttribute('d', '');
  document.getElementById('pin-destino').setAttribute('r', 0);
  document.getElementById('pin-destino-pulso').classList.remove('activo');
  document.getElementById('pin-destino-pulso').setAttribute('r', 0);
  document.getElementById('pin-destino-label').textContent = '';
  document.getElementById('mapa-info').textContent = mensaje;
}

// Maneja el clic en el botón "Buscar"
function manejarBusqueda() {
  const input = document.getElementById('input-salon');
  const resultado = document.getElementById('resultado');
  const codigo = input.value.trim();

  if (codigo === '') {
    resultado.textContent = 'Escribe un código de salón, por ejemplo B204.';
    mostrarSalones(salones);
    limpiarRuta('Busca un salón para ver la ruta trazada en el mapa.');
    return;
  }

  const salon = buscarPorCodigo(codigo);

  if (salon) {
    resultado.textContent =
      `${salon.codigo} · ${salon.nombre} está en ${salon.bloque}, piso ${salon.piso} (≈ ${salon.minutos} min caminando).`;
    mostrarSalones([salon]);
    dibujarRuta(salon);
  } else {
    resultado.textContent = `No encontré ningún salón con el código "${codigo}".`;
    mostrarSalones([]);
    limpiarRuta(`No encontré "${codigo}" para trazar la ruta.`);
  }
}

// Semana 8: el formulario se envía por POST al servlet /registrar-salon.
// Aquí solo se hace una validación rápida en el navegador; la validación
// definitiva la hace el servidor (RegistroSalonServlet).
function validarFormularioRegistro(evento) {
  const codigo = document.getElementById('nuevo-codigo').value.trim();
  const nombre = document.getElementById('nuevo-nombre').value.trim();
  const bloque = document.getElementById('nuevo-bloque').value;
  const piso = Number(document.getElementById('nuevo-piso').value);
  const minutos = Number(document.getElementById('nuevo-minutos').value);
  const mensaje = document.getElementById('mensaje-nuevo-salon');

  if (!codigo || !nombre || !bloque || !piso || !minutos) {
    evento.preventDefault();
    mensaje.textContent = 'Completa todos los campos para registrar el salón.';
    return;
  }

  if (!/^[A-Za-z]\d{3}$/.test(codigo)) {
    evento.preventDefault();
    mensaje.textContent = 'El código debe tener una letra y tres números, por ejemplo A105.';
    return;
  }

  if (buscarPorCodigo(codigo)) {
    evento.preventDefault();
    mensaje.textContent = `Ya existe un salón registrado con el código "${codigo}".`;
    return;
  }
  // Si todo está bien, el formulario se envía normalmente al servlet.
}

// Pide al servlet /consulta (formato JSON) la lista de salones y actualiza la interfaz.
// Si el servidor no responde (por ejemplo, abriendo el archivo directamente), se
// siguen usando los datos de ejemplo.
async function cargarSalonesDelServidor() {
  try {
    const respuesta = await fetch('consulta?formato=json');
    if (!respuesta.ok) return;
    const datos = await respuesta.json();
    salones.length = 0;
    datos.forEach(d => salones.push(
      new Salon(d.codigo, d.nombre, d.bloque, d.piso, d.minutos, d.x, d.y)
    ));
    mostrarSalones(salones);
  } catch (error) {
    console.warn('No se pudo consultar el servidor, se usan los datos de ejemplo.', error);
  }
}

// ===== Inicio =====
document.getElementById('btn-buscar').addEventListener('click', manejarBusqueda);

document.getElementById('form-nuevo-salon').addEventListener('submit', validarFormularioRegistro);

// Permite buscar también con la tecla Enter
document.getElementById('input-salon').addEventListener('keydown', (evento) => {
  if (evento.key === 'Enter') {
    manejarBusqueda();
  }
});

// Muestra todos los salones al cargar la página (primero los de respaldo, luego los del servidor)
mostrarSalones(salones);
cargarSalonesDelServidor();

// ===== Animación al hacer scroll =====
// Cada elemento con la clase "reveal" aparece con una animación
// la primera vez que entra en la pantalla.
const observador = new IntersectionObserver((entradas) => {
  entradas.forEach(entrada => {
    if (entrada.isIntersecting) {
      entrada.target.classList.add('visible');
      observador.unobserve(entrada.target);
    }
  });
}, { threshold: 0.15 });

document.querySelectorAll('.reveal').forEach(elemento => observador.observe(elemento));

console.log({ salones });