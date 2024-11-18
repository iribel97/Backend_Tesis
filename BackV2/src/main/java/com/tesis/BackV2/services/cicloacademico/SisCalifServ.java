package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.repositories.SistCalifRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SisCalifServ {

    @Autowired
    private SistCalifRepo repo;
}
