package com.spring.app.proyecto_test.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.app.proyecto_test.model.Cuenta;
import com.spring.app.proyecto_test.model.TransaccionDto;



@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CuentaControllerTestRestTemplate {

    @Autowired
    private TestRestTemplate client;
    @Autowired
    private ObjectMapper oMapper;

    @BeforeEach
    void setUp(){
        oMapper= new ObjectMapper();
    }

    @Test
    @Order(2)
    void testDetalle() {

        ResponseEntity<Cuenta> response= client.getForEntity("/api/cuentas/1",Cuenta.class);
        Cuenta cuenta= response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        assertNotNull(cuenta);
        assertEquals("Nahuel", cuenta.getPersona());
        assertEquals("900.00", cuenta.getSaldo().toPlainString());
        assertEquals(new Cuenta(1L, "Nahuel",new BigDecimal("900.00")), cuenta);

    }

    @Test
    @Order(5)
    void testEliminar() {

        ResponseEntity<Cuenta[]> response= client.getForEntity("/api/cuentas", Cuenta[].class);
        List<Cuenta> cuenta= java.util.Arrays.asList(response.getBody());
        assertEquals(3, cuenta.size());

        client.delete("/api/cuentas/1");

        response= client.getForEntity("/api/cuentas", Cuenta[].class);
        cuenta= java.util.Arrays.asList(response.getBody());
        assertEquals(2, cuenta.size());

        ResponseEntity<Cuenta> responseDetalle= client.getForEntity("/api/cuentas/1",Cuenta.class);
        assertEquals(HttpStatus.NOT_FOUND, responseDetalle.getStatusCode());
        assertFalse(responseDetalle.hasBody());

    }

    @Test
    @Order(4)
    void testGuardar() {
        Cuenta cuenta= new Cuenta(null, "Jesus",new BigDecimal("100.00"));
        ResponseEntity<Cuenta> response= client.postForEntity("/api/cuentas", cuenta, Cuenta.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        Cuenta cuentaCreada= response.getBody();
        assertEquals(3L, cuentaCreada.getId());
        assertEquals("Jesus", cuentaCreada.getPersona());
        assertEquals("100.00", cuentaCreada.getSaldo().toPlainString());

    }

    @Test
    @Order(3)
    void testListar() {
        ResponseEntity<Cuenta[]> response= client.getForEntity("/api/cuentas", Cuenta[].class);
        List<Cuenta> cuenta= java.util.Arrays.asList(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());


        assertEquals(1L, cuenta.get(0).getId());
        assertEquals("Nahuel", cuenta.get(0).getPersona());
        assertEquals("900.00", cuenta.get(0).getSaldo().toPlainString());
        assertEquals(2L, cuenta.get(1).getId());
        assertEquals("Rocio", cuenta.get(1).getPersona());
        assertEquals("2100.00", cuenta.get(1).getSaldo().toPlainString());
    }

    @Test
    @Order(1)
    void testTransferir() throws JsonMappingException, JsonProcessingException {
        TransaccionDto dto= new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1L);

        ResponseEntity<String> response= client
        .postForEntity("/api/cuentas/transferir", dto,String.class);
        String json= response.getBody();
        assertNotNull(json);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON , response.getHeaders().getContentType());
        assertTrue(json.contains("Transferencia con exito"));

        com.fasterxml.jackson.databind.JsonNode jsonNode= oMapper.readTree(json);
        assertEquals("Transferencia con exito", jsonNode.path("mensaje").asText());
        assertEquals(LocalDate.now().toString(),jsonNode.path("date").asText());
        assertEquals("100", jsonNode.path("transaccion").path("monto").asText());
        assertEquals(1L, jsonNode.path("transaccion").path("cuentaOrigenId").asLong());


        Map<String,Object> response2=new HashMap<>();
        response2.put("date", LocalDate.now().toString());
        response2.put("status", "OK");
        response2.put("mensaje", "Transferencia con exito");
        response2.put("transaccion", dto);
        assertEquals(oMapper.writeValueAsString(response2),json);

    }
}
