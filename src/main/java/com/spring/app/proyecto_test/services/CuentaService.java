package com.spring.app.proyecto_test.services;

import java.math.BigDecimal;
import java.util.List;

import com.spring.app.proyecto_test.model.Cuenta;

public interface CuentaService {


    List<Cuenta> findAll();
    Cuenta save(Cuenta cuenta);
    Cuenta findById(Long id);
    void deleteById(Long id);
    int revisarTotalTransferencias(Long bancoId);
    BigDecimal revisarSaldo(Long Cuenta);
    void transferir(Long idCuentaOrigen,Long idCuentaDestino,BigDecimal monto,Long bancoId);

}
