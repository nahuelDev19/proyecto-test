package com.spring.app.proyecto_test.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.app.proyecto_test.model.Cuenta;

public interface CuentaRepository extends JpaRepository<Cuenta,Long>{

    Optional<Cuenta> findByPersona(String persona);
    

}
