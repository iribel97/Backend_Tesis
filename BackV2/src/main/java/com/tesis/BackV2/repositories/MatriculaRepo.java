package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.CicloAcademico;
import com.tesis.BackV2.entities.Curso;
import com.tesis.BackV2.entities.Inscripcion;
import com.tesis.BackV2.entities.Matricula;
import com.tesis.BackV2.enums.EstadoMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatriculaRepo extends JpaRepository<Matricula, Long> {

    // ultimo registro de una inscripcion
    Matricula findTopByInscripcionCedulaOrderByIdDesc(String cedula);

    // Traer la última amtricula de un estudiante
    Matricula findTopByEstudianteIdOrderByIdDesc(long id);

    boolean existsByInscripcionAndCiclo(Inscripcion inscripcion, CicloAcademico topByOrderByIdDesc);

    boolean existsByInscripcionAndCicloAndIdNot(Inscripcion inscripcion, CicloAcademico topByOrderByIdDesc, Long id);

    // Listar por estado
    List<Matricula> findByEstado(EstadoMatricula estado);

    List<Matricula> findByInscripcion_Representante_Usuario_Cedula(String inscripcionRepresentanteUsuarioCedula);

    // Listar estudiantes por el último ciclo académico y por curso
    List<Matricula> findByCicloAndCurso_Id(CicloAcademico ciclo, Long idCurso);

    // Listar estudiantes matriculados y del ciclo academico activo
    List<Matricula> findByCicloAndEstado(CicloAcademico ciclo, EstadoMatricula estado);

    // Listar matrículas por ciclo académico y curso
    List<Matricula> findByCicloAndCurso(CicloAcademico ciclo, Curso curso);

    Matricula findTopByEstudianteUsuarioCedulaOrderByIdDesc(String cedulaEst);
}
