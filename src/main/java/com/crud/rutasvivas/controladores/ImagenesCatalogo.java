package com.crud.rutasvivas.controladores;

import java.text.Normalizer;
import java.util.Locale;

import com.crud.rutasvivas.entidades.Atractivo;
import com.crud.rutasvivas.entidades.Experiencia;

/** Asigna las fotografías incluidas en static/img cuando el registro no tiene imagen propia. */
public final class ImagenesCatalogo {

    private ImagenesCatalogo() {
    }

    public static void completar(Atractivo atractivo) {
        if (atractivo == null) return;
        String nombre = normalizar(atractivo.getNombre());
        String archivo = null;

        if (nombre.contains("chicamocha")) archivo = "parque-chicamocha.jpg";
        else if (nombre.contains("barichara")) archivo = "barichara.jpg";
        else if (nombre.contains("cartagena") || nombre.contains("amurallada")) archivo = "cartagena-amurallada.jpg";
        else if (nombre.contains("cocora")) archivo = "valle-cocora.jpg";
        else if (nombre.contains("cano cristales")) archivo = "cano-cristales.jpg";
        else if (nombre.contains("ciudad perdida")) archivo = "ciudad-perdida.jpg";
        else if (nombre.contains("catedral de sal")) archivo = "catedral-sal-zipaquira.jpg";
        else if (nombre.contains("penol") || nombre.contains("guatape")) archivo = "piedra-penol-guatape.jpg";
        else if (nombre.contains("lajas")) archivo = "santuario-las-lajas.jpg";
        else if (nombre.contains("tatacoa")) archivo = "desierto-tatacoa.jpg";
        else if (nombre.contains("san agustin")) archivo = "parque-san-agustin.jpg";
        else if (nombre.contains("mompox")) archivo = "mompox-historico.jpg";
        else if (nombre.contains("tayrona")) archivo = "parque-tayrona.jpg";
        else if (nombre.contains("museo del oro")) archivo = "museo-del-oro.jpg";
        else if (nombre.contains("termales")) archivo = "termales-santa-rosa.jpg";
        else if (nombre.contains("puente de boyaca")) archivo = "puente-boyaca.jpg";

        if (archivo != null) atractivo.setImagenUrl("/img/atractivos/" + archivo);
    }

    public static void completar(Experiencia experiencia) {
        if (experiencia == null) return;
        String nombre = normalizar(experiencia.getNombre());
        String archivo = null;

        if (nombre.contains("artesania") && nombre.contains("barichara")) archivo = "artesania-barichara.jpg";
        else if (nombre.contains("gastronomico") || nombre.contains("gastronomico") || nombre.contains("cartagena")) archivo = "gastronomia-cartagena.jpg";
        else if (nombre.contains("cafetera") && nombre.contains("quindio")) archivo = "experiencia-cafetera-quindio.jpg";
        else if (nombre.contains("musica llanera")) archivo = "musica-llanera.jpg";
        else if (nombre.contains("cata") && nombre.contains("cafe")) archivo = "cata-cafe-origen.jpg";
        else if (nombre.contains("cocina") && nombre.contains("santandereana")) archivo = "cocina-santandereana.jpg";
        else if (nombre.contains("wayuu") || nombre.contains("tejido")) archivo = "tejido-wayuu.jpg";
        else if (nombre.contains("aves")) archivo = "avistamiento-aves-andes.jpg";
        else if (nombre.contains("tamboras") || nombre.contains("caribe")) archivo = "tamboras-caribe.jpg";
        else if (nombre.contains("cacao")) archivo = "cacao-colombiano.jpg";

        if (archivo != null) experiencia.setImagenUrl("/img/experiencias/" + archivo);
    }

    private static String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
