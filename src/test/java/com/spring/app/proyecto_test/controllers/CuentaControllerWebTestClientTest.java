package com.spring.app.proyecto_test.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.app.proyecto_test.model.Cuenta;
import com.spring.app.proyecto_test.model.TransaccionDto;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CuentaControllerWebTestClientTest {


    @Autowired
    private WebTestClient client;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        objectMapper= new ObjectMapper();
    }


    @Test
    @Order(2)
    void testDetalle() {

        Cuenta cuenta= new Cuenta(1L,"Nahuel",new BigDecimal("900"));
        try {
            client.get().uri("/api/cuentas/1")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.persona").isEqualTo("Nahuel")
            .jsonPath("$.saldo").isEqualTo("900.0")
            .json(objectMapper.writeValueAsString(cuenta));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        
        

    }
    @Test
    @Order(3)
    void testDetalle2() {
        client.get().uri("/api/cuentas/2")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(Cuenta.class)
        .consumeWith(response -> {
            Cuenta cuenta= response.getResponseBody();
            assertEquals("Rocio", cuenta.getPersona());
            assertEquals("2100.00", cuenta.getSaldo().toPlainString());
        });
    }
   

    @Test
    @Order(4)
    void testListar() {
        client.get().uri("/api/cuentas")
        .exchange().expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$[0].persona").isEqualTo("Nahuel")
        .jsonPath("$[0].saldo").isEqualTo("900.0")
        .jsonPath("$[0].id").isEqualTo(1)
        .jsonPath("$[1].persona").isEqualTo("Rocio")
        .jsonPath("$[1].saldo").isEqualTo("2100.0")
        .jsonPath("$[1].id").isEqualTo(2)
        .jsonPath("$").isArray();
        //.jsonPath("$").value((2));
    }

    @Test
    @Order(5)
    void testListarV2() {
        client.get().uri("/api/cuentas")
        .exchange().expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Cuenta.class)
        .consumeWith(response -> {
            List<Cuenta> cuentas= response.getResponseBody();
            assertNotNull(cuentas);
            assertEquals(2, cuentas.size());
            assertEquals(1L,cuentas.get(0).getId());
            assertEquals("Nahuel",cuentas.get(0).getPersona());
            assertEquals("900.00",cuentas.get(0).getSaldo().toPlainString());
            assertEquals(2L,cuentas.get(1).getId());
            assertEquals("Rocio",cuentas.get(1).getPersona());
            assertEquals("2100.00",cuentas.get(1).getSaldo().toPlainString());
        });
        
    }





    @Test
    @Order(1)
    void testTransferir()throws JsonProcessingException {

        TransaccionDto dto=new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1L);

        Map<String,Object> response=new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia con exito");
        response.put("transaccion", dto);
        
        client.post().uri("/api/cuentas/transferir")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(dto)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.mensaje").isEqualTo("Transferencia con exito")
        .jsonPath("$.status").isEqualTo("OK")
        .jsonPath("$.date").isNotEmpty() // Puedes agregar validación más detallada si es necesario
        .json(objectMapper.writeValueAsString(response)); // Verificación completa del JSON
    }


    @Test
    @Order(6)
    void testGuardar() {
        Cuenta cuenta= new Cuenta(null, "ramiro", new BigDecimal("1000"));
        client.post().uri("/api/cuentas")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(cuenta)
        .exchange()
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.persona").isEqualTo("ramiro")
        .jsonPath("$.saldo").isEqualTo(1000.00)
        .jsonPath("$.id").isEqualTo("3")
        .jsonPath("$.persona").isEqualTo("ramiro");
        
        

    } 
    
    
    @Test
    @Order(7)
    void testGuardar2() {
        Cuenta cuenta= new Cuenta(null, "ramiro", new BigDecimal("1000"));
        client.post().uri("/api/cuentas")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(cuenta)
        .exchange()
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(Cuenta.class)
        .consumeWith(response -> {
            Cuenta c = response.getResponseBody();
            assertNotNull(c);
            assertEquals(4, c.getId());
            assertEquals("ramiro", c.getPersona());
            assertEquals("1000", c.getSaldo().toPlainString());
        });
    }


    @Test
    @Order(8)
    void testEliminar(){
        client.get().uri("/api/cuentas").exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Cuenta.class)
        .hasSize(4);

        client.delete().uri("/api/cuentas/1").exchange()
        .expectStatus().isNoContent()
        .expectBody().isEmpty();

        client.get().uri("/api/cuentas").exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Cuenta.class)
        .hasSize(3);

        //busca el id que eliminamos y comprueba de que se haya eliminado
        /* client.get().uri("/api/cuentas/1").exchange()
        .expectStatus().is5xxServerError();
 */
        //segunda forma
        // en este caso aplicamos e
        client.get().uri("/api/cuentas/1").exchange()
        .expectStatus().isNotFound().expectBody().isEmpty();


    }

}
