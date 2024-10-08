package com.spring.app.proyecto_test.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.proyecto_test.model.Banco;
import com.spring.app.proyecto_test.model.Cuenta;
import com.spring.app.proyecto_test.repositories.BancoRepository;
import com.spring.app.proyecto_test.repositories.CuentaRepository;
@Service
public class CuentaServiceImp implements CuentaService{


    private CuentaRepository cuentaRepository;
    private BancoRepository bancoRepository;

    
    public CuentaServiceImp(CuentaRepository cuentaRepository, BancoRepository bancoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.bancoRepository = bancoRepository;
    }
    @Transactional(readOnly = true)
    @Override
    public Cuenta findById(Long id) {
       return cuentaRepository.findById(id).orElseThrow();
    }
    @Transactional(readOnly = true)
    @Override
    public int revisarTotalTransferencias(Long bancoId) {
        Banco banco= bancoRepository.findById(bancoId).orElseThrow();
        return banco.getTotalTransferencias();
    }

    @Transactional(readOnly = true)
    @Override
    public BigDecimal revisarSaldo(Long Cuenta) {
        Cuenta cuenta= cuentaRepository.findById(Cuenta).orElseThrow();
        return cuenta.getSaldo();
    }

    
    /**
     * Realiza una transferencia de dinero entre dos cuentas bancarias y 
     * actualiza el total de transferencias del banco.
     *
     * @param idCuentaOrigen El ID de la cuenta de origen desde donde se debitará el monto.
     * @param idCuentaDestino El ID de la cuenta de destino a la cual se acreditará el monto.
     * @param monto El monto de dinero a transferir.
     */
    @Transactional
    @Override
    public void transferir(Long idCuentaOrigen, Long idCuentaDestino, BigDecimal monto,Long bancoId) {

        Cuenta cuentaOrigen= cuentaRepository.findById(idCuentaOrigen).orElseThrow();
        cuentaOrigen.debito(monto);
        cuentaRepository.save(cuentaOrigen);

        Cuenta cuentaDestino= cuentaRepository.findById(idCuentaDestino).orElseThrow();
        cuentaDestino.credito(monto);
        cuentaRepository.save(cuentaDestino);


        Banco banco= bancoRepository.findById(bancoId).orElseThrow();
        int totTranferencia= banco.getTotalTransferencias();
        banco.setTotalTransferencias(++totTranferencia);
        bancoRepository.save(banco);
        
    }
    @Override
    public List<Cuenta> findAll() {
        return cuentaRepository.findAll();
    }
    @Override
    public Cuenta save(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }
    @Override
    @Transactional
    public void deleteById(Long id) {
         cuentaRepository.deleteById(id);
    }

}
