const API = "http://127.0.0.1:8000/api";
const $ = (id) => document.getElementById(id);
let token = localStorage.getItem("token");
let mensajeTimeout;

function mostrarMensaje(texto, tipo = "ok") {
  const mensaje = $("mensaje");
  mensaje.textContent = texto;
  mensaje.className = tipo;
  mensaje.hidden = false;
  clearTimeout(mensajeTimeout);
  mensajeTimeout = setTimeout(() => {
    mensaje.hidden = true;
  }, 5000);
}

async function request(path, { method = "GET", body } = {}) {
  const headers = { "Content-Type": "application/json" };
  if (token) headers.Authorization = `Token ${token}`;

  let response;
  try {
    response = await fetch(`${API}${path}`, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
    });
  } catch {
    throw { networkError: true };
  }

  if (response.status === 204) return null;
  let data;
  try {
    data = await response.json();
  } catch {
    throw {
      status: response.status,
      data: { detail: "El servidor devolvió una respuesta no válida." },
    };
  }
  if (!response.ok) throw { status: response.status, data };
  return data;
}

function formatearErrores(error) {
  if (error.networkError) {
    return "Error de red: ¿está el servidor encendido?";
  }
  if (error.status === 401) {
    return "Debes iniciar sesión para esta operación.";
  }
  if (error.data?.detail) return error.data.detail;
  if (!error.data || typeof error.data !== "object") {
    return "Ocurrió un error al procesar la solicitud.";
  }
  return Object.entries(error.data)
    .map(([campo, mensajes]) => {
      const texto = Array.isArray(mensajes) ? mensajes.join(" ") : mensajes;
      return `${campo}: ${texto}`;
    })
    .join(" | ");
}

function actualizarAuthUI() {
  $("auth-status").textContent = token ? "Sesión activa" : "Sin sesión";
  $("btn-login").hidden = Boolean(token);
  $("btn-logout").hidden = !token;
  $("login-user").hidden = Boolean(token);
  $("login-pass").hidden = Boolean(token);
}

$("btn-login").addEventListener("click", async () => {
  try {
    const data = await request("/login/", {
      method: "POST",
      body: {
        username: $("login-user").value,
        password: $("login-pass").value,
      },
    });
    token = data.token;
    localStorage.setItem("token", token);
    actualizarAuthUI();
    mostrarMensaje("Sesión iniciada");
    await refrescar();
  } catch (error) {
    mostrarMensaje(formatearErrores(error), "error");
  }
});

$("btn-logout").addEventListener("click", () => {
  token = null;
  localStorage.removeItem("token");
  actualizarAuthUI();
});

function crearTarjeta(titulo, lineas, onDelete) {
  const tarjeta = document.createElement("article");
  tarjeta.className = "card";

  const encabezado = document.createElement("h3");
  encabezado.textContent = titulo;
  tarjeta.appendChild(encabezado);

  lineas.forEach((texto) => {
    const parrafo = document.createElement("p");
    parrafo.textContent = texto;
    tarjeta.appendChild(parrafo);
  });

  const boton = document.createElement("button");
  boton.type = "button";
  boton.textContent = "Eliminar";
  boton.className = "danger";
  boton.addEventListener("click", onDelete);
  tarjeta.appendChild(boton);
  return tarjeta;
}

async function cargarEspacios() {
  const espacios = await request("/espacios/");
  const lista = $("lista-espacios");
  lista.replaceChildren();

  espacios.forEach((espacio) => {
    lista.appendChild(crearTarjeta(
      espacio.nombre,
      [
        `📍 ${espacio.ubicacion}`,
        `Aforo: ${espacio.aforo_maximo}`,
        `Estado: ${espacio.estado}`,
        `Recursos: ${espacio.total_recursos}`,
      ],
      () => eliminar(`/espacios/${espacio.id}/`, "Espacio eliminado"),
    ));
  });

  ["select-espacio", "filtro-espacio"].forEach((id) => {
    const select = $(id);
    const opcionInicial = id === "filtro-espacio"
      ? select.options[0].cloneNode(true)
      : null;
    select.replaceChildren();
    if (opcionInicial) select.appendChild(opcionInicial);
    espacios.forEach((espacio) => {
      select.appendChild(new Option(espacio.nombre, espacio.id));
    });
  });
}

async function cargarRecursos() {
  const params = new URLSearchParams();
  if ($("filtro-espacio").value) {
    params.set("espacio", $("filtro-espacio").value);
  }
  if ($("filtro-min").value) {
    params.set("costo_min", $("filtro-min").value);
  }
  if ($("filtro-max").value) {
    params.set("costo_max", $("filtro-max").value);
  }

  const query = params.toString();
  const recursos = await request(`/recursos/${query ? `?${query}` : ""}`);
  const lista = $("lista-recursos");
  lista.replaceChildren();

  recursos.forEach((recurso) => {
    lista.appendChild(crearTarjeta(
      recurso.nombre,
      [
        `Código: ${recurso.codigo_inventario}`,
        `Costo: $${recurso.costo}`,
        `Stock: ${recurso.stock}`,
        `Espacio: ${recurso.espacio_detalle.nombre}`,
      ],
      () => eliminar(`/recursos/${recurso.id}/`, "Recurso eliminado"),
    ));
  });
}

async function eliminar(path, mensajeOk) {
  try {
    await request(path, { method: "DELETE" });
    mostrarMensaje(mensajeOk);
    await refrescar();
  } catch (error) {
    mostrarMensaje(formatearErrores(error), "error");
  }
}

async function refrescar() {
  try {
    await cargarEspacios();
    await cargarRecursos();
  } catch (error) {
    mostrarMensaje(formatearErrores(error), "error");
  }
}

function manejarForm(formId, path, mensajeOk) {
  $(formId).addEventListener("submit", async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const datos = Object.fromEntries(new FormData(form));
    try {
      await request(path, { method: "POST", body: datos });
      form.reset();
      mostrarMensaje(mensajeOk);
      await refrescar();
    } catch (error) {
      mostrarMensaje(formatearErrores(error), "error");
    }
  });
}

async function filtrarRecursos() {
  try {
    await cargarRecursos();
  } catch (error) {
    mostrarMensaje(formatearErrores(error), "error");
  }
}

manejarForm("form-espacio", "/espacios/", "Espacio registrado correctamente");
manejarForm("form-recurso", "/recursos/", "Recurso registrado correctamente");
$("btn-filtrar").addEventListener("click", filtrarRecursos);

actualizarAuthUI();
refrescar();
