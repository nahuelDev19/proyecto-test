package com.spring.app.proyecto_test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.spring.app.proyecto_test.datos_test.Datos;
import com.spring.app.proyecto_test.exceptios.DineroInsuficienteException;
import com.spring.app.proyecto_test.model.Banco;
import com.spring.app.proyecto_test.model.Cuenta;
import com.spring.app.proyecto_test.repositories.BancoRepository;
import com.spring.app.proyecto_test.repositories.CuentaRepository;
import com.spring.app.proyecto_test.services.CuentaServiceImp;

@SpringBootTest
class ProyectoTestApplicationTests {

	@MockBean
	CuentaRepository cuentaRepository;
	@MockBean
	BancoRepository bancoRepository;
	@Autowired
	CuentaServiceImp cuentaService;

	@Test
	void contextLoads() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.CUENTA_001());
		when(cuentaRepository.findById(2L)).thenReturn(Datos.CUENTA_002());
		when(bancoRepository.findById(1L)).thenReturn(Datos.BANCO());

		
		BigDecimal saldoOrigen= cuentaService.revisarSaldo(1L);
		BigDecimal saldoDestino= cuentaService.revisarSaldo(2L);
		
		assertEquals("1000", saldoOrigen.toString());
		assertEquals("2000", saldoDestino.toString());

		cuentaService.transferir(1L,2L, new BigDecimal("100"),1L);
		
		saldoOrigen= cuentaService.revisarSaldo(1L);
		saldoDestino= cuentaService.revisarSaldo(2L);

		assertEquals("900", saldoOrigen.toString());
		assertEquals("2100", saldoDestino.toString());

		int total= cuentaService.revisarTotalTransferencias(1L);
		assertEquals(1,total);
 
		verify(cuentaRepository,times(3)).findById(1L);
		verify(cuentaRepository,times(3)).findById(2L);
		verify(cuentaRepository,times(2)).save(any(Cuenta.class));

		verify(bancoRepository,times(2)).findById(1L);
		verify(bancoRepository).save(any(Banco.class));

		verify(cuentaRepository,times(6)).findById(anyLong());
		verify(cuentaRepository,never()).findAll();

	}

	@Test
	void contextLoads2() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.CUENTA_001());
		when(cuentaRepository.findById(2L)).thenReturn(Datos.CUENTA_002());
		when(bancoRepository.findById(1L)).thenReturn(Datos.BANCO());

		
		BigDecimal saldoOrigen= cuentaService.revisarSaldo(1L);
		BigDecimal saldoDestino= cuentaService.revisarSaldo(2L);
		
		assertEquals("1000", saldoOrigen.toString());
		assertEquals("2000", saldoDestino.toString());

		assertThrows(DineroInsuficienteException.class,()-> {
			cuentaService.transferir(1L,2L, new BigDecimal("1200"),1L);

		});
		
		saldoOrigen= cuentaService.revisarSaldo(1L);
		saldoDestino= cuentaService.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toString());
		assertEquals("2000", saldoDestino.toString());

		int total= cuentaService.revisarTotalTransferencias(1L);
		assertEquals(0,total);
 
		verify(cuentaRepository,times(3)).findById(1L);
		verify(cuentaRepository,times(2)).findById(2L);
		verify(cuentaRepository,never()).save(any(Cuenta.class));

		verify(bancoRepository,times(1)).findById(1L);
		verify(bancoRepository,never()).save(any(Banco.class));

		verify(cuentaRepository,times(5)).findById(anyLong());


	}

	@Test
	void contestLoads3() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.CUENTA_001());

		Cuenta cuenta= cuentaService.findById(1L);
		Cuenta cuenta2= cuentaService.findById(1L);

		assertSame("Nahuel",cuenta.getPersona());
		assertEquals("Nahuel", cuenta2.getPersona());
		verify(cuentaRepository,times(2)).findById(anyLong());
	}


	@Test
	void findAll(){
		List<Cuenta> datos= Arrays.asList(Datos.CUENTA_001().get(),Datos.CUENTA_002().get());
		when(cuentaRepository.findAll()).thenReturn(datos);

		List<Cuenta> cuentas= cuentaService.findAll();

		assertEquals("Nahuel", cuentas.get(0).getPersona());
		assertFalse(cuentas.isEmpty());
		assertEquals(2, cuentas.size());
		assertTrue(cuentas.contains(Datos.CUENTA_002().get()));
		verify(cuentaRepository).findAll();
	}

	@Test
	void testName() {
	Cuenta cuentaPepe= Datos.CUENTA_001().get();
	when(cuentaRepository.save(any())).thenReturn(cuentaPepe);

	Cuenta cuenta= cuentaService.save(cuentaPepe);
	assertEquals("Nahuel", cuenta.getPersona());


	verify(cuentaRepository).save(any());

}
}
