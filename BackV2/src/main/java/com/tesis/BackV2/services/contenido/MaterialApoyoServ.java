package com.tesis.BackV2.services.contenido;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.entities.contenido.MaterialApoyo;
import com.tesis.BackV2.entities.contenido.Tema;
import com.tesis.BackV2.entities.documentation.DocMaterialApoyo;
import com.tesis.BackV2.entities.documentation.Documento;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.contenido.MaterialApoyoRepo;
import com.tesis.BackV2.repositories.contenido.TemaRepo;
import com.tesis.BackV2.repositories.documentation.DocMaterialApoyoRepo;
import com.tesis.BackV2.request.contenido.MaterialApoyoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MaterialApoyoServ {

    private final MaterialApoyoRepo repo;
    private final DocMaterialApoyoRepo repoDoc;
    private final TemaRepo repoTema;

    // Crear material apoyo
    @Transactional
    public ApiResponse<String> crearMaterialApoyo(MaterialApoyoRequest request, MultipartFile documento) {

        // Validar si el tema existe
        Tema tema = repoTema.findById(request.getIdTema())
                .orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .codigo(400)
                        .mensaje("Solicitud incorrecta")
                        .detalles("El tema no ha sido encontrado")
                        .build()));

        MaterialApoyo material = MaterialApoyo.builder()
                .activo(request.isActivo())
                .link(request.getLink())
                .nombreLink("MaterialApoyo_"+tema.getTema())
                .tema(tema)
                .documento(guardarDoc(documento, tema.getTema(), tema))
                .build();

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Material de apoyo registrado")
                .detalles("El material de apoyo ha sido registrado exitosamente")
                .build();
    }

    /* ----- METODOS PROPIOS ---- */
    private DocMaterialApoyo crearNuevoDocumento(String titulo, DocMaterialApoyo documentoActual, MultipartFile nuevoArchivo) throws IOException {
        // Obtener la versión actual del documento
        long versionActual = Long.parseLong(documentoActual.getNombre().substring(documentoActual.getNombre().lastIndexOf("_v") + 2));

        // Obtener la fecha y hora actual
        String fechaTiempo = tiempo();

        // Construir el nuevo nombre del documento
        String nuevoNombre = documentoActual.getTipoDoc() + "_" + titulo + "_" + fechaTiempo + "_v" + (versionActual + 1);

        // Crear el nuevo documento con el nombre actualizado
        return repoDoc.save(DocMaterialApoyo.builder()
                .nombre(nuevoNombre)
                .contenido(nuevoArchivo.getBytes())
                .mime(nuevoArchivo.getContentType())
                .tipoDoc(documentoActual.getTipoDoc())
                .build());
    }

    private DocMaterialApoyo guardarDoc(MultipartFile file, String titulo, Tema tema) {
        try {
            String timestamp = tiempo();
            String nombre = String.format("%s_%s_%s_v%d", "Material Apoyo", titulo, timestamp, 1);

            DocMaterialApoyo documento = DocMaterialApoyo.builder()
                    .nombre(nombre)
                    .contenido(file.getBytes())
                    .mime(file.getContentType())
                    .tipoDoc("Material Apoyo")
                    .tema(tema)
                    .build();
            return repoDoc.save(documento);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo", e);
        }
    }

    private String tiempo(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
