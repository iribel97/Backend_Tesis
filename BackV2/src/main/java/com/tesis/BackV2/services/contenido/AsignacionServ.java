package com.tesis.BackV2.services.contenido;

import com.tesis.BackV2.repositories.contenido.AsignacionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsignacionServ {

    private final AsignacionRepo repo;

}
