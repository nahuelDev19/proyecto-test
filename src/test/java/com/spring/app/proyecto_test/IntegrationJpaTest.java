package com.spring.app.proyecto_test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.spring.app.proyecto_test.model.Cuenta;
import com.spring.app.proyecto_test.repositories.CuentaRepository;

@DataJpaTest
public class IntegrationJpaTest {

    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void testFindById(){
        Optional<Cuenta> cuenta= cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Nahuel", cuenta.orElseThrow().getPersona());
    }

    @Test
    void testFindByPersona(){
        Optional<Cuenta> cuenta= cuentaRepository.findByPersona("Nahuel");
        assertTrue(cuenta.isPresent());
        assertEquals("Nahuel", cuenta.orElseThrow().getPersona());
        assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
    }

    @Test
    void testFindByPersonaThrowException(){
        Optional<Cuenta> cuenta= cuentaRepository.findByPersona("Nacho");
        assertThrows(NoSuchElementException.class,()->{
            cuenta.orElseThrow();
        });
        assertFalse(cuenta.isPresent());
    }

    @Test
    void testFindAll(){
        List<Cuenta> cuentas= cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
    }

    @Test
    void testSave(){
        Cuenta cuentaNueva= new Cuenta(null,"Pepe",new BigDecimal("3000"));
    
        Cuenta buscaCuenta= cuentaRepository.save(cuentaNueva);
        assertEquals("Pepe", buscaCuenta.getPersona());
        assertEquals("3000", buscaCuenta.getSaldo().toPlainString());
    }

    @Test
    void testUpdate(){
        Cuenta cuentaNueva= new Cuenta(null,"Pepe",new BigDecimal("3000"));
    
        Cuenta buscaCuenta= cuentaRepository.save(cuentaNueva);
        assertEquals("Pepe", buscaCuenta.getPersona());
        assertEquals("3000", buscaCuenta.getSaldo().toPlainString());
        buscaCuenta.setSaldo(new BigDecimal("4500"));
        Cuenta cuentaAcualizada=cuentaRepository.save(buscaCuenta);
        assertEquals("Pepe", cuentaAcualizada.getPersona());
        assertEquals("4500", cuentaAcualizada.getSaldo().toPlainString());
    }

    @Test
    void testDelete(){
        Cuenta cuenta= cuentaRepository.findById(1L).orElseThrow();
        assertEquals("Nahuel", cuenta.getPersona());
        cuentaRepository.delete(cuenta);
        assertThrows(NoSuchElementException.class,()->cuentaRepository.findById(1L).orElseThrow());
    }
}
