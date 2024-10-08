package com.spring.app.proyecto_test.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.app.proyecto_test.datos_test.Datos;
import com.spring.app.proyecto_test.model.Cuenta;
import com.spring.app.proyecto_test.model.TransaccionDto;
import com.spring.app.proyecto_test.services.CuentaService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(CuentaController.class)
public class CuentaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CuentaService cuentaService;

    private ObjectMapper oMapper;

    @BeforeEach
    void setUp(){
        oMapper= new ObjectMapper();
    }

    @Test
void testDetalle() throws Exception {
    // Simulamos la respuesta del servicio
    when(cuentaService.findById(1L)).thenReturn(Datos.CUENTA_001().orElseThrow());

    // Realizamos la solicitud con MockMvc
    mvc.perform(get("/api/cuentas/1") // Agregar la barra inicial
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()) // Verificamos que el status es 200 OK
        .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Verificamos que el contenido es JSON
        .andExpect(jsonPath("$.persona").value("Nahuel")) // Verificamos que el campo "persona" sea "Nahuel"
        .andExpect(jsonPath("$.id").value(1)); // Verificación extra del ID (opcional)
}


    @Test
    void testTransferir() throws JsonProcessingException, Exception {
        TransaccionDto dto= new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("500"));
        dto.setBancoId(1L);

        Map<String,Object> response=new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia con exito");
        response.put("transaccion", dto);

        mvc.perform(post("/api/cuentas/transferir")
        .contentType(MediaType.APPLICATION_JSON).content(oMapper.writeValueAsString(dto)))
        
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
        .andExpect(content().json(oMapper.writeValueAsString(response)));
    }

    @Test
    void testName() throws JsonProcessingException, Exception {
    List<Cuenta> cuentas = Arrays.asList(new Cuenta(1L, "nahuel", new BigDecimal("1200"))
    ,new Cuenta(2L, "rocio", new BigDecimal("2200")));  

    when(cuentaService.findAll()).thenReturn(cuentas);

    mvc.perform(get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))
    .andExpect(jsonPath("$[0].persona").value("nahuel"))
    .andExpect(jsonPath("$[1].persona").value("rocio"))
    .andExpect(jsonPath("$[0].saldo").value("1200"))
    .andExpect(jsonPath("$[1].saldo").value("2200"))
    .andExpect(jsonPath("$", Matchers.hasSize(2)))
    .andExpect(content().json(oMapper.writeValueAsString(cuentas)));

    }

    @Test
    void testName2() throws JsonProcessingException, Exception {
    Cuenta cuenta= new Cuenta(null, "ricardo",new BigDecimal("300"));
    when(cuentaService.save(any())).thenAnswer(invocation ->{
        Cuenta c = invocation.getArgument(0, Cuenta.class);
        c.setId(3L);
        return c;
    });

    mvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
    .content(oMapper.writeValueAsString(cuenta)))

    .andExpect(status().isCreated())
    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    .andExpect(jsonPath("$.id",Matchers.is(3)))
    .andExpect(jsonPath("$.persona",Matchers.is("ricardo")))
    .andExpect(jsonPath("$.saldo",Matchers.is(300)));
    verify(cuentaService).save(any());
    
    }


    
    
}
