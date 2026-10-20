package com.crud.rutasvivas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.crud.rutasvivas.entidades.Atractivo;
import com.crud.rutasvivas.entidades.Experiencia;
import com.crud.rutasvivas.entidades.Ruta;
import com.crud.rutasvivas.repositorios.AtractivoRepositorio;
import com.crud.rutasvivas.repositorios.ExperienciaRepositorio;
import com.crud.rutasvivas.repositorios.RutaRepositorio;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final AtractivoRepositorio atractivoRepositorio;
    private final RutaRepositorio rutaRepositorio;
    private final ExperienciaRepositorio experienciaRepositorio;

    public DatosIniciales(
            AtractivoRepositorio atractivoRepositorio,
            RutaRepositorio rutaRepositorio,
            ExperienciaRepositorio experienciaRepositorio) {
        this.atractivoRepositorio = atractivoRepositorio;
        this.rutaRepositorio = rutaRepositorio;
        this.experienciaRepositorio = experienciaRepositorio;
    }

    @Override
    public void run(String... args) {

        List<Atractivo> atractivos = new ArrayList<>();

        {
            atractivos.add(crearAtractivo(
                    "Parque Nacional del Chicamocha",
                    "Parque natural y turístico reconocido por el paisaje del cañón del Chicamocha.",
                    "Santander", "Aratoca", "Naturaleza",
                    "Cañón del Chicamocha, vía Bucaramanga-San Gil"));

            atractivos.add(crearAtractivo(
                    "Barichara",
                    "Pueblo patrimonial conocido por sus calles empedradas y arquitectura tradicional.",
                    "Santander", "Barichara", "Patrimonio",
                    "Centro histórico de Barichara"));

            atractivos.add(crearAtractivo(
                    "Ciudad Amurallada de Cartagena",
                    "Centro histórico con murallas, plazas y arquitectura colonial.",
                    "Bolívar", "Cartagena", "Patrimonio",
                    "Centro histórico de Cartagena de Indias"));

            atractivos.add(crearAtractivo(
                    "Valle de Cocora",
                    "Paisaje montañoso conocido por sus palmas de cera y senderos naturales.",
                    "Quindío", "Salento", "Naturaleza",
                    "Valle de Cocora, zona rural de Salento"));

            atractivos.add(crearAtractivo(
                    "Caño Cristales",
                    "Río de la Serranía de la Macarena conocido por sus colores estacionales.",
                    "Meta", "La Macarena", "Naturaleza",
                    "Parque Nacional Natural Sierra de La Macarena"));

            atractivos.add(crearAtractivo(
                    "Ciudad Perdida",
                    "Antiguo asentamiento arqueológico indígena en la Sierra Nevada de Santa Marta.",
                    "Magdalena", "Santa Marta", "Arqueología",
                    "Sierra Nevada de Santa Marta"));

            atractivos.add(crearAtractivo(
                    "Catedral de Sal de Zipaquirá",
                    "Complejo religioso subterráneo construido dentro de una mina de sal.",
                    "Cundinamarca", "Zipaquirá", "Cultura",
                    "Parque de la Sal, Zipaquirá"));

            atractivos.add(crearAtractivo(
                    "Piedra del Peñol y Guatapé",
                    "Destino reconocido por su monolito rocoso y el paisaje del embalse.",
                    "Antioquia", "Guatapé", "Naturaleza",
                    "Sector de la Piedra del Peñol, cerca de Guatapé"));

            atractivos.add(crearAtractivo("Santuario de Las Lajas",
                    "Santuario neogótico construido sobre el cañón del río Guáitara, referente religioso y arquitectónico del sur colombiano.",
                    "Nariño", "Ipiales", "Patrimonio", "Cañón del río Guáitara, Ipiales"));
            atractivos.add(crearAtractivo("Desierto de la Tatacoa",
                    "Paisaje semiárido con formaciones rojizas y grises, observación astronómica y senderos interpretativos.",
                    "Huila", "Villavieja", "Naturaleza", "Zona rural de Villavieja"));
            atractivos.add(crearAtractivo("Parque Arqueológico de San Agustín",
                    "Parque con esculturas y vestigios arqueológicos de culturas prehispánicas del Alto Magdalena.",
                    "Huila", "San Agustín", "Arqueología", "San Agustín, Huila"));
            atractivos.add(crearAtractivo("Mompox histórico",
                    "Centro histórico ribereño reconocido por su arquitectura colonial y tradición artesanal.",
                    "Bolívar", "Santa Cruz de Mompox", "Patrimonio", "Centro histórico de Mompox"));
            atractivos.add(crearAtractivo("Parque Nacional Natural Tayrona",
                    "Área protegida de playas, bosque tropical y senderos en el litoral del Caribe.",
                    "Magdalena", "Santa Marta", "Naturaleza", "Sector de ingreso al Parque Tayrona"));
            atractivos.add(crearAtractivo("Museo del Oro",
                    "Museo de Bogotá con colecciones que permiten conocer la orfebrería y culturas prehispánicas.",
                    "Bogotá D. C.", "Bogotá", "Cultura", "Parque Santander, centro de Bogotá"));
            atractivos.add(crearAtractivo("Termales de Santa Rosa de Cabal",
                    "Aguas termales y entorno de montaña característico del paisaje cafetero.",
                    "Risaralda", "Santa Rosa de Cabal", "Naturaleza", "Zona rural de Santa Rosa de Cabal"));
            atractivos.add(crearAtractivo("Puente de Boyacá",
                    "Lugar histórico asociado a la campaña de independencia y a la memoria nacional.",
                    "Boyacá", "Ventaquemada", "Historia", "Vía Tunja-Bogotá"));
            agregarAtractivoSiFalta(atractivos);
        }

        {
            List<Atractivo> disponibles =
                    atractivoRepositorio.findAll();

            Atractivo chicamocha = buscarPorNombre(
                    disponibles, "Parque Nacional del Chicamocha");
            Atractivo barichara = buscarPorNombre(
                    disponibles, "Barichara");
            Atractivo cartagena = buscarPorNombre(
                    disponibles, "Ciudad Amurallada de Cartagena");
            Atractivo cocora = buscarPorNombre(
                    disponibles, "Valle de Cocora");

            List<Ruta> rutas = new ArrayList<>();

            if (chicamocha != null && barichara != null) {
                rutas.add(crearRuta(
                        "Santander: cañón y pueblos patrimoniales",
                        "Recorrido por paisajes del cañón y calles históricas de Santander.",
                        "Aratoca y Barichara, Santander",
                        "2 días", "Guía turístico local",
                        Arrays.asList(chicamocha.getId(), barichara.getId())));
            }

            if (cartagena != null) {
                rutas.add(crearRuta(
                        "Historia del Caribe colombiano",
                        "Exploración del patrimonio arquitectónico de Cartagena.",
                        "Cartagena de Indias, Bolívar",
                        "1 día", "Guía cultural local",
                        Arrays.asList(cartagena.getId())));
            }

            if (cocora != null) {
                rutas.add(crearRuta(
                        "Paisajes del Eje Cafetero",
                        "Visita a paisajes de montaña y palmas de cera en el Quindío.",
                        "Salento, Quindío",
                        "1 día", "Guía de naturaleza",
                        Arrays.asList(cocora.getId())));
            }

            Atractivo lasLajas = buscarPorNombre(disponibles, "Santuario de Las Lajas");
            Atractivo tatacoa = buscarPorNombre(disponibles, "Desierto de la Tatacoa");
            Atractivo sanAgustin = buscarPorNombre(disponibles, "Parque Arqueológico de San Agustín");
            Atractivo tayrona = buscarPorNombre(disponibles, "Parque Nacional Natural Tayrona");
            Atractivo mompox = buscarPorNombre(disponibles, "Mompox histórico");
            Atractivo museoOro = buscarPorNombre(disponibles, "Museo del Oro");
            Atractivo termales = buscarPorNombre(disponibles, "Termales de Santa Rosa de Cabal");
            Atractivo puenteBoyaca = buscarPorNombre(disponibles, "Puente de Boyacá");
            if (lasLajas != null) rutas.add(crearRuta("Patrimonio del sur: Las Lajas",
                    "Recorrido para conocer el santuario y el paisaje del cañón del Guáitara.",
                    "Ipiales, Nariño", "1 día", "Guía cultural local", Arrays.asList(lasLajas.getId())));
            if (tatacoa != null && sanAgustin != null) rutas.add(crearRuta("Huila: desierto y arqueología",
                    "Ruta que combina paisajes semiáridos con el patrimonio arqueológico del Alto Magdalena.",
                    "Villavieja y San Agustín, Huila", "3 días", "Guía de naturaleza", Arrays.asList(tatacoa.getId(), sanAgustin.getId())));
            if (tayrona != null) rutas.add(crearRuta("Naturaleza del Caribe: Tayrona",
                    "Caminatas y reconocimiento del paisaje costero del Caribe colombiano.",
                    "Santa Marta, Magdalena", "1 día", "Guía de naturaleza", Arrays.asList(tayrona.getId())));
            if (mompox != null) rutas.add(crearRuta("Pueblos patrimoniales del río Magdalena",
                    "Exploración de arquitectura tradicional, historia y oficios artesanales.",
                    "Santa Cruz de Mompox, Bolívar", "2 días", "Guía cultural local", Arrays.asList(mompox.getId())));
            if (museoOro != null && puenteBoyaca != null) rutas.add(crearRuta("Historia y memoria de Colombia",
                    "Visita a espacios de memoria cultural e histórica en Bogotá y Boyacá.",
                    "Bogotá y Ventaquemada", "2 días", "Guía cultural local", Arrays.asList(museoOro.getId(), puenteBoyaca.getId())));
            if (termales != null) rutas.add(crearRuta("Bienestar y paisaje cafetero",
                    "Recorrido por el entorno montañoso y las aguas termales de Risaralda.",
                    "Santa Rosa de Cabal, Risaralda", "1 día", "Guía de naturaleza", Arrays.asList(termales.getId())));
            for (Ruta ruta : rutas) {
                if (!rutaRepositorio.existsByNombreIgnoreCase(ruta.getNombre())) {
                    rutaRepositorio.save(ruta);
                }
            }
        }

        {
            List<Experiencia> experiencias = new ArrayList<>();

            experiencias.add(crearExperiencia(
                    "Taller de artesanía en Barichara",
                    "Acercamiento a técnicas artesanales tradicionales de la región.",
                    "Barichara, Santander",
                    "Artesano local"));

            experiencias.add(crearExperiencia(
                    "Recorrido gastronómico en Cartagena",
                    "Exploración de sabores tradicionales de la cocina caribeña.",
                    "Cartagena, Bolívar",
                    "Anfitrión gastronómico"));

            experiencias.add(crearExperiencia(
                    "Experiencia cafetera en el Quindío",
                    "Conocimiento del cultivo, recolección y preparación del café.",
                    "Zona rural del Quindío",
                    "Productor cafetero"));

            experiencias.add(crearExperiencia(
                    "Noche cultural de música llanera",
                    "Encuentro cultural dedicado a la música y tradiciones de los Llanos.",
                    "Villavicencio, Meta",
                    "Gestor cultural"));

            experiencias.add(crearExperiencia("Cata guiada de café de origen",
                    "Actividad participativa para reconocer aromas, métodos de preparación y características del café colombiano.",
                    "Quindío, Eje Cafetero", "Productor cafetero"));
            experiencias.add(crearExperiencia("Cocina tradicional santandereana",
                    "Taller de cocina para conocer preparaciones regionales y su contexto cultural.",
                    "Bucaramanga y municipios cercanos, Santander", "Anfitrión gastronómico"));
            experiencias.add(crearExperiencia("Tejido y artesanía wayuu",
                    "Encuentro cultural sobre técnicas de tejido, símbolos y elaboración artesanal.",
                    "La Guajira", "Artesana o colectivo local"));
            experiencias.add(crearExperiencia("Avistamiento de aves en los Andes",
                    "Caminata interpretativa para observar aves y reconocer ecosistemas andinos con acompañamiento local.",
                    "Santander", "Guía de naturaleza"));
            experiencias.add(crearExperiencia("Taller de tamboras y música del Caribe",
                    "Actividad participativa para acercarse a ritmos tradicionales y sus contextos comunitarios.",
                    "Mompox, Bolívar", "Gestor cultural"));
            experiencias.add(crearExperiencia("Experiencia de cacao colombiano",
                    "Recorrido para conocer etapas del cultivo, fermentación y transformación del cacao.",
                    "Santander", "Productor de cacao"));
            for (Experiencia experiencia : experiencias) {
                if (!experienciaRepositorio.existsByNombreIgnoreCase(experiencia.getNombre())) {
                    experienciaRepositorio.save(experiencia);
                }
            }
        }
    }

    private void agregarAtractivoSiFalta(List<Atractivo> atractivos) {
        for (Atractivo atractivo : atractivos) {
            if (!atractivoRepositorio.existsByNombreIgnoreCase(atractivo.getNombre())) {
                atractivoRepositorio.save(atractivo);
            }
        }
    }

    private Atractivo crearAtractivo(
            String nombre,
            String descripcion,
            String departamento,
            String municipio,
            String categoria,
            String ubicacion) {

        Atractivo atractivo = new Atractivo();
        atractivo.setNombre(nombre);
        atractivo.setDescripcion(descripcion);
        atractivo.setDepartamento(departamento);
        atractivo.setMunicipio(municipio);
        atractivo.setCategoria(categoria);
        atractivo.setUbicacion(ubicacion);
        atractivo.setImagenUrl(null);
        atractivo.setPublicado(true);

        return atractivo;
    }

    private Ruta crearRuta(
            String nombre,
            String descripcion,
            String destino,
            String duracion,
            String guia,
            List<String> atractivosIds) {

        Ruta ruta = new Ruta();
        ruta.setNombre(nombre);
        ruta.setDescripcion(descripcion);
        ruta.setDestino(destino);
        ruta.setDuracion(duracion);
        ruta.setGuia(guia);
        ruta.setAtractivosIds(atractivosIds);

        return ruta;
    }

    private Experiencia crearExperiencia(
            String nombre,
            String descripcion,
            String ubicacion,
            String responsable) {

        Experiencia experiencia = new Experiencia();
        experiencia.setNombre(nombre);
        experiencia.setDescripcion(descripcion);
        experiencia.setUbicacion(ubicacion);
        experiencia.setResponsable(responsable);

        return experiencia;
    }

    private Atractivo buscarPorNombre(
            List<Atractivo> atractivos,
            String nombre) {

        return atractivos.stream()
                .filter(a -> nombre.equals(a.getNombre()))
                .findFirst()
                .orElse(null);
    }
}