package com.spring.app.proyecto_test.datos_test;

import java.math.BigDecimal;
import java.util.Optional;

import com.spring.app.proyecto_test.model.Banco;
import com.spring.app.proyecto_test.model.Cuenta;

public class Datos {

    public static Optional<Cuenta> CUENTA_001(){
        return Optional.of(new Cuenta(1L,"Nahuel",new BigDecimal("1000")));
    }
    public static Optional<Cuenta> CUENTA_002(){
        return Optional.of(new Cuenta(2L,"Rocio",new BigDecimal("2000")));
    }
    public static Optional<Banco> BANCO(){
        return Optional.of(new Banco(1L,"Banco Galicia",0));
    }
}
