package com.tesis.BackV2.services.contenido;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.contenido.MaterialApoyoDTO;
import com.tesis.BackV2.dto.doc.DocumentoDTO;
import com.tesis.BackV2.entities.contenido.MaterialApoyo;
import com.tesis.BackV2.entities.contenido.Tema;
import com.tesis.BackV2.entities.documentation.DocContMateria;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.contenido.MaterialApoyoRepo;
import com.tesis.BackV2.repositories.contenido.TemaRepo;
import com.tesis.BackV2.repositories.documentation.DocMaterialApoyoRepo;
import com.tesis.BackV2.request.contenido.MaterialApoyoRequest;
import com.tesis.BackV2.request.documentation.DocumentoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialApoyoServ {

    private final MaterialApoyoRepo repo;
    private final DocMaterialApoyoRepo repoDoc;
    private final TemaRepo repoTema;

    // Crear material apoyo
    @Transactional
    public ApiResponse<String> crearMaterialApoyo(MaterialApoyoRequest request) {

        // Validar si el tema existe
        Tema tema = repoTema.findById(request.getIdTema())
                .orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .codigo(400)
                        .mensaje("Solicitud incorrecta")
                        .detalles("El tema no ha sido encontrado")
                        .build()));

        long part = repo.countByTema_Id(tema.getId()) + 1;
        String nombre = "MaterialApoyo_"+tema.getTema()+"_pt"+part;

        MaterialApoyo material = MaterialApoyo.builder()
                .activo(request.isActivo())
                .link(!request.getLink().isEmpty() ? request.getLink() : null)
                .nombreLink(!request.getLink().isEmpty() ? nombre : null)
                .tema(tema)
                .documento( request.getDocumento() != null ? guardarDoc(request.getDocumento(), nombre, tema) : null)
                .build();

        repo.save(material);

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Material de apoyo registrado")
                .detalles("El material de apoyo ha sido registrado exitosamente")
                .build();
    }

    // Editar
    public ApiResponse<String> editarMaterialApoyo (MaterialApoyoRequest request) {
        MaterialApoyo material = repo.findById(request.getId())
                .orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .codigo(400)
                        .mensaje("Solicitud incorrecta")
                        .detalles("El material de apoyo no ha sido encontrado")
                        .build()));

        material.setActivo(request.isActivo());
        material.setLink(request.getLink());
        repo.save(material);

        if (request.getDocumento() != null) {
            DocContMateria doc = material.getDocumento();
            doc.setContenido(Base64.getDecoder().decode(request.getDocumento().getBase64()));
            doc.setMime(request.getDocumento().getMime());
            repoDoc.save(doc);
        }
        return ApiResponse.<String> builder()
                .error(false)
                .codigo(200)
                .mensaje("Edición completa")
                .detalles("Se ha editado correctamente elmaterial de apoyo")
                .build();
    }

    // Eliminar material de apoyo
    public ApiResponse<String> eliminarMaterialApoyo(long id) {
        MaterialApoyo material = repo.findById(id)
                .orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .codigo(400)
                        .mensaje("Solicitud incorrecta")
                        .detalles("El material de apoyo no ha sido encontrado")
                        .build()));

        if (material.getDocumento() != null) {
            repoDoc.delete(material.getDocumento());
        }
        repo.delete(material);

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Material de apoyo eliminado")
                .detalles("El material de apoyo ha sido eliminado exitosamente")
                .build();
    }

    // traer por tema
    public List<MaterialApoyoDTO> obtenerPorTema(long idTema) {
        Tema tema = repoTema.findById(idTema)
                .orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .codigo(400)
                        .mensaje("Solicitud incorrecta")
                        .detalles("El tema no ha sido encontrado")
                        .build()));

        return repo.findByTema_Id(tema.getId()).stream()
                .map(this::convertirDTO)
                .toList();
    }

    // traer por tema y activo
    public List<MaterialApoyoDTO> obtenerPorTemaActivo(long idTema, boolean activo) {
        Tema tema = repoTema.findById(idTema)
                .orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .codigo(400)
                        .mensaje("Solicitud incorrecta")
                        .detalles("El tema no ha sido encontrado")
                        .build()));

        return repo.findByTema_IdAndActivo(tema.getId(), activo).stream()
                .map(this::convertirDTO)
                .toList();
    }

    /* ----- METODOS PROPIOS ---- */
    private DocContMateria guardarDoc(DocumentoRequest file, String titulo, Tema tema) {
        try {
            DocContMateria documento = DocContMateria.builder()
                    .nombre(titulo)
                    .contenido(Base64.getDecoder().decode(file.getBase64()))
                    .mime(file.getMime())
                    .tipoDoc("Material Apoyo")
                    .build();
            return repoDoc.save(documento);
        } catch (RuntimeException e) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .codigo(500)
                    .mensaje("Error interno")
                    .detalles("Ha ocurrido un error al guardar el documento")
                    .build());
        }
    }

    private MaterialApoyoDTO convertirDTO (MaterialApoyo material) {
        return MaterialApoyoDTO.builder()
                .id(material.getId())
                .activo(material.isActivo())
                .link(material.getLink())
                .nombreLink(material.getNombreLink())
                .documento(material.getDocumento() != null ? convertirDoc(material.getDocumento()) : null)
                .build();
    }

    private DocumentoDTO convertirDoc (DocContMateria doc) {
        return DocumentoDTO.builder()
                .id(doc.getId())
                .nombre(doc.getNombre())
                .base64(Base64.getEncoder().encodeToString(doc.getContenido()))
                .mime(doc.getMime())
                .build();
    }
}
