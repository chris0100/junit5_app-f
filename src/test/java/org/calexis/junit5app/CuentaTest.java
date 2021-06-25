package org.calexis.junit5app;


import org.calexis.junit5app.exceptions.DineroInsuficienteException;
import org.calexis.junit5app.models.Cuenta;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {


    Cuenta cuenta;
    Cuenta cuenta2;

    @BeforeEach
    void initMetodoTest(){
        this.cuenta = Cuenta.builder()
                .persona("christian")
                .saldo(new BigDecimal("1000.25"))
                .build();


        this.cuenta2 = Cuenta.builder()
                .persona("marx")
                .saldo(new BigDecimal("1500.89"))
                .build();

        System.out.println("se inicia antes de cada metodo");
    }


    @AfterEach
    void finalizaCadametodo(){
        System.out.println("finalizando despues de cada metodo");
    }


    @BeforeAll
    static void beforeAll(){
        System.out.println("inicializando el test");
    }


    @AfterAll
    static void afterAll(){
        System.out.println("finalizando el test");
    }


    @Tag("cuenta")
    @Nested
    @DisplayName("Test atributos de cuenta")
    class CuentaTestNombreSaldo {

        @Test
        @DisplayName("Probando nombre de la cuenta")
        void testNombreCuenta() {
            String esperado = "christian";
            String real = cuenta.getPersona();

            assertNotNull(real, () -> "la cuenta no puede ser nula");
            assertEquals(esperado, real, () -> "el nombre de la cuenta no es el que se esperaba");
            assertTrue(real.equals("christian"), () -> "nombre cuenta esperada debe ser igual a la real");
        }


        @Test
        @DisplayName("Probando el saldo de la cuenta")
        void testSaldoCuenta() {
            assertNotNull(cuenta.getSaldo());
            assertEquals("1000.25", cuenta.getSaldo().toPlainString());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @Tag("error")
    @Test
    @DisplayName("Probando la excepcion")
    void testDineroInsuficienteException(){
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("1500"));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, actual);
    }


    @DisplayName("Probando el credito")
    @Test
    void testCreditoCuenta() {
        cuenta.credito(new BigDecimal("100"));

        assertEquals(1100, cuenta.getSaldo().intValue());
        assertNotNull(cuenta.getSaldo());
        assertEquals("1100.25", cuenta.getSaldo().toPlainString());
    }
}






















