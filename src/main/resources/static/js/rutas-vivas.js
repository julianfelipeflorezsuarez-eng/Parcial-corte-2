document.addEventListener("DOMContentLoaded", function () {
    const botones = document.querySelectorAll("[data-confirmar]");

    botones.forEach(function (boton) {
        boton.addEventListener("click", function (evento) {
            const mensaje = boton.getAttribute("data-confirmar");

            if (!window.confirm(mensaje)) {
                evento.preventDefault();
            }
        });
    });

    const imagenes = document.querySelectorAll(".tarjeta-imagen img, .detalle-imagen img");

    imagenes.forEach(function (imagen) {
        imagen.addEventListener("error", function () {
            imagen.style.display = "none";
        });
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const imagenes = document.querySelectorAll(".atractivo-imagen, .detalle-imagen");

    imagenes.forEach(function (imagen) {
        imagen.addEventListener("error", function () {
            imagen.style.display = "none";

            const aviso = document.createElement("div");
            aviso.className = "atractivo-sin-imagen";
            aviso.textContent = "Imagen no disponible";

            imagen.insertAdjacentElement("afterend", aviso);
        });
    });

    const formulariosEliminar = document.querySelectorAll("[data-confirmar]");

    formulariosEliminar.forEach(function (formulario) {
        formulario.addEventListener("submit", function (evento) {
            const mensaje = formulario.getAttribute("data-confirmar");

            if (!window.confirm(mensaje || "¿Confirmas esta operación?")) {
                evento.preventDefault();
            }
        });
    });

    const fechas = document.querySelectorAll('input[type="date"][data-fecha-futura]');

    fechas.forEach(function (campo) {
        const hoy = new Date();
        const anio = hoy.getFullYear();
        const mes = String(hoy.getMonth() + 1).padStart(2, "0");
        const dia = String(hoy.getDate()).padStart(2, "0");

        campo.min = `${anio}-${mes}-${dia}`;
    });
});
