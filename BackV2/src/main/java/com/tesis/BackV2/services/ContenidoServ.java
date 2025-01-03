package com.tesis.BackV2.services;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.contenido.*;
import com.tesis.BackV2.dto.doc.DocumentoDTO;
import com.tesis.BackV2.dto.horarioConfig.AgendaDTO;
import com.tesis.BackV2.entities.Distributivo;
import com.tesis.BackV2.entities.Horario;
import com.tesis.BackV2.entities.contenido.Asignacion;
import com.tesis.BackV2.entities.contenido.MaterialApoyo;
import com.tesis.BackV2.entities.contenido.Tema;
import com.tesis.BackV2.entities.contenido.Unidad;
import com.tesis.BackV2.entities.documentation.DocContMateria;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.DistributivoRepo;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.repositories.HorarioRepo;
import com.tesis.BackV2.repositories.SistCalifRepo;
import com.tesis.BackV2.repositories.contenido.*;
import com.tesis.BackV2.repositories.documentation.DocEntregaRepo;
import com.tesis.BackV2.repositories.documentation.DocMaterialApoyoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContenidoServ {

    private final UnidadRepo repo;
    private final DistributivoRepo repoDis;
    private final HorarioRepo repoHor;
    private final TemaRepo repoTem;
    private final MaterialApoyoRepo repoMatAp;
    private final AsignacionRepo repoAsig;
    private final DocMaterialApoyoRepo repoDoc;


    public ContenidoDTO contenidoMateria (Long idDistributivo) {
        // Validar que el id exista
        Distributivo distributivo = repoDis.findById(idDistributivo).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud incorrecta")
                .codigo(400)
                .detalles("Distributivo no encontrado")
                .build()
        ));

        ContenidoDTO contenido = new ContenidoDTO();

        contenido.setIdDistributivo(distributivo.getId());
        contenido.setNombreMateria(distributivo.getMateria().getNombre());
        contenido.setDocenteNombres(distributivo.getDocente().getUsuario().getNombres());
        contenido.setDocenteApellidos(distributivo.getDocente().getUsuario().getApellidos());
        contenido.setHorarios(getAgendaByDocente(distributivo.getId()));
        contenido.setUnidades(getUnidadesByMateriaActiva(distributivo.getId()));

        return contenido;
    }


    // Agenda DTO
    private List<AgendaDTO> getAgendaByDocente(long idDis) {
        List<Horario> horarios = repoHor.findByDistributivo_Id(idDis);
        List<AgendaDTO> agendas = new ArrayList<>();

        for (Horario horario : horarios) {
            // Busca si ya existe un registro para el día
            AgendaDTO agendaExistente = agendas.stream()
                    .filter(agenda -> agenda.getDia().equals(horario.getDiaSemana()))
                    .findFirst()
                    .orElse(null);

            if (agendaExistente != null) {
                // Si ya existe, actualiza la hora fin
                agendaExistente.setHoraFin(String.valueOf(horario.getHorario().getHoraFin()));
            } else {
                // Si no existe, agrega un nuevo registro
                agendas.add(AgendaDTO.builder()
                        .dia(horario.getDiaSemana())
                        .horaInicio(String.valueOf(horario.getHorario().getHoraInicio()))
                        .horaFin(String.valueOf(horario.getHorario().getHoraFin()))
                        .build());
            }
        }

        return agendas;
    }


    // Unidades de una materia
    private List<UnidadesDTO> getUnidadesByMateriaActiva(long idDis) {
        List<Unidad> unidades = repo.findByDistributivo_IdAndActivo(idDis, true);

        List<UnidadesDTO> uniDTO = new ArrayList<>();
        for (Unidad unidad : unidades) {
            uniDTO.add(UnidadesDTO.builder()
                    .idUnidad(unidad.getId())
                    .nombre(unidad.getTitulo())
                    .activo(unidad.isActivo())
                    .contenido(getTemasByUnidadActivo(unidad.getId()))
                    .build());
        }

        return uniDTO;
    }

    // Temas de una unidad
    private List<TemaDTO> getTemasByUnidadActivo(long idUnidad) {
        List<Tema> temas = repoTem.findByUnidadIdAndActivo(idUnidad, true);

        List<TemaDTO> temaDTO = new ArrayList<>();
        for (Tema tema : temas) {
            temaDTO.add(TemaDTO.builder()
                    .idTema(tema.getId())
                    .activo(tema.isActivo())
                    .nombreTema(tema.getTema())
                    .descripcion(tema.getDetalle())
                    .materiales(getMaterialByTemaActivo(tema.getId()))
                    .asignaciones(getAsignacionesByTemaActivo(tema.getId()))
                    .build());
        }

        return temaDTO;
    }

    // Materiales de apoyo de un tema
    private List<MaterialApoyoDTO> getMaterialByTemaActivo(long idTema) {
        List<MaterialApoyo> materiales = repoMatAp.findByTema_IdAndActivo(idTema, true);

        List<MaterialApoyoDTO> matDTO = new ArrayList<>();
        for (MaterialApoyo material : materiales) {
            matDTO.add(MaterialApoyoDTO.builder()
                    .idMaterial(material.getId())
                    .activo(material.isActivo())
                    .link(material.getLink())
                    .nombreLink(material.getNombreLink())
                    .documento(material.getDocumento() != null ? convertirDoc(material.getDocumento()) : null)
                    .build());
        }

        return matDTO;
    }

    private DocumentoDTO convertirDoc (DocContMateria doc) {
        return DocumentoDTO.builder()
                .id(doc.getId())
                .nombre(doc.getNombre())
                .base64(Base64.getEncoder().encodeToString(doc.getContenido()))
                .mime(doc.getMime())
                .build();
    }

    // Asignaciones de un tema
    private List<AsignacionesDTO> getAsignacionesByTemaActivo(long idTema) {
        List<Asignacion> asignaciones = repoAsig.findByTema_IdAndActivo(idTema, true);

        List<AsignacionesDTO> asigDTO = new ArrayList<>();
        for (Asignacion asignacion : asignaciones) {
            asigDTO.add(AsignacionesDTO.builder()
                    .idAsignacion(asignacion.getId())
                    .activo(asignacion.isActivo())
                    .nombre(asignacion.getNombre())
                    .descripcion(asignacion.getDescripcion())
                    .fechaFin(String.valueOf(asignacion.getFechaFin()))
                    .horaFin(String.valueOf(asignacion.getHoraFin()))
                    .documentos(repoDoc.findByAsignacion_Id(asignacion.getId()).stream()
                            .map(this::convertirDoc)
                            .toList())
                    .build());
        }

        return asigDTO;
    }
}