package com.spring.app.proyecto_test.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.app.proyecto_test.model.Banco;

public interface BancoRepository extends JpaRepository<Banco,Long>{

    
}
