package org.calexis.junit5app;

import org.calexis.junit5app.exceptions.DineroInsuficienteException;
import org.calexis.junit5app.models.Banco;
import org.calexis.junit5app.models.Cuenta;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS) crea una instancia para quitar statica beforeAll, afterAll
class CuentaTest {

    Cuenta cuenta;
    private TestInfo testInfo;
    private TestReporter testReporter;

    //**************************************************
    // Se ejecutan antes y despues de cada test de metodo
    //**************************************************
    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
        this.cuenta = Cuenta.builder()
                .persona("Christian")
                .saldo(new BigDecimal("1000.25"))
                .build();

        this.testInfo = testInfo;
        this.testReporter = testReporter;

        System.out.println("se inicia antes de cada metodo");
        testReporter.publishEntry("ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().get().getName() +
                " con las etiquetas " + testInfo.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("finalizando despues del metodo");
    }
    //**************************************************
    // fin
    //**************************************************


    //**************************************************
    // Se ejecutan al inicio y fin de la ejecucion
    //**************************************************
    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("finalizando el test");
    }
    //**************************************************
    // fin
    //**************************************************




    @Tag("cuenta")
    @Nested
    @DisplayName("Test atributos de cuenta")
    class CuentaTestNombreSaldo{


        @Test
        @DisplayName("Probando nombre de la cuenta!")
        void testNombreCuenta() {
            System.out.println(testInfo.getTags());
            if (testInfo.getTags().contains("cuenta")){
                System.out.println("hacer algo con la etiqueta cuenta");
            }

            String esperado = "Christian";
            String real = cuenta.getPersona();

            assertNotNull(real, () -> "la cuenta no puede ser nula");
            assertEquals(esperado, real, () -> "el nombre de la cuenta no es el que se esperaba");
            assertTrue(real.equals("Christian"), () -> "nombre cuenta esperada debe ser igual a la real");
        }

        @Test
        @DisplayName("Probando el saldo de la cuenta")
        void testSaldoCuenta() {
            Cuenta cuenta = Cuenta.builder()
                    .persona("Christian")
                    .saldo(new BigDecimal("1000.25"))
                    .build();

            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.25, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Probando las cuentas")
        void testReferenciaCuenta() {
            Cuenta cuenta = Cuenta.builder()
                    .persona("john doe")
                    .saldo(new BigDecimal("8900.25"))
                    .build();

            Cuenta cuenta2 = Cuenta.builder()
                    .persona("john doe")
                    .saldo(new BigDecimal("8900.25"))
                    .build();

            assertEquals(cuenta2, cuenta);
        }
    }



    @Nested
    class CuentaOperacionesTest{
        @Tag("cuenta")
        @Test
        @DisplayName("Probando el debito")
        void testDebitoCuenta() {
            Cuenta cuenta = Cuenta.builder()
                    .persona("Alexis")
                    .saldo(new BigDecimal("1000.25"))
                    .build();

            cuenta.debito(new BigDecimal(100));

            assertEquals(900, cuenta.getSaldo().intValue());
            assertNotNull(cuenta.getSaldo());
            assertEquals("900.25", cuenta.getSaldo().toPlainString());
        }


        @Tag("cuenta")
        @Test
        @DisplayName("Probando el credito")
        void testCreditoCuenta() {
            Cuenta cuenta = Cuenta.builder()
                    .persona("Alexis")
                    .saldo(new BigDecimal("1000.25"))
                    .build();

            cuenta.credito(new BigDecimal(100));

            assertEquals(1100, cuenta.getSaldo().intValue());
            assertNotNull(cuenta.getSaldo());
            assertEquals("1100.25", cuenta.getSaldo().toPlainString());
        }


        @Tag("cuenta")
        @Tag("banco")
        @Test
        @DisplayName("Probando transferencia de dinero")
        void testTransferirDineroCuentas() {
            Cuenta cuenta1 = Cuenta.builder()
                    .persona("ander")
                    .saldo(new BigDecimal("2500"))
                    .build();

            Cuenta cuenta2 = Cuenta.builder()
                    .persona("marx")
                    .saldo(new BigDecimal("1500.89"))
                    .build();

            Banco banco = Banco.builder()
                    .nombre("Banco Falabella")
                    .build();

            banco.transferir(cuenta1, cuenta2, new BigDecimal("500"));
            assertEquals("2000", cuenta1.getSaldo().toPlainString());
            assertEquals("2000.89", cuenta2.getSaldo().toPlainString());
        }
    }



    @Tag("cuenta")
    @Tag("error")
    @Test
    @DisplayName("Probando la excepcion")
    void testDineroInsuficienteException() {
        Cuenta cuenta = Cuenta.builder()
                .persona("Christian")
                .saldo(new BigDecimal("1000.123"))
                .build();

        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("1500"));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, actual);
    }





    @Tag("cuenta")
    @Tag("banco")
    @Test
    @DisplayName("Probando relacion entre cuentas de banco")
    @Disabled
        //deshabilita un test
    void testRelacionBancoCuentas() {
        fail();

        Banco banco = Banco.builder()
                .nombre("Banco Falabella")
                .cuentas(new ArrayList<>())
                .build();

        Cuenta cuenta1 = Cuenta.builder()
                .persona("ander")
                .saldo(new BigDecimal("2500"))
                .build();

        Cuenta cuenta2 = Cuenta.builder()
                .persona("marx")
                .saldo(new BigDecimal("1500.89"))
                .build();


        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.transferir(cuenta1, cuenta2, new BigDecimal("500"));

        assertAll(() -> assertEquals("2000", cuenta1.getSaldo().toPlainString(), () -> "El valor del saldo no es el esperado"),
                () -> assertEquals("2000.89", cuenta2.getSaldo().toPlainString(), () -> "El valor del saldo no es el esperado"),
                () -> assertEquals(2, banco.getCuentas().size(), () -> "El banco no tiene las cuentas esperadas"),
                () -> assertEquals("Banco Falabella", cuenta1.getBanco().getNombre(), () -> "la cuenta no esta asociado al banco esperado"),
                () -> assertEquals("ander", banco.getCuentas()
                        .stream()
                        .filter(obj -> obj.getPersona().equals("ander"))
                        .findFirst()
                        .get()
                        .getPersona(), () -> "El nombre no esta asociado a ningun banco"),
                () -> assertTrue(banco.getCuentas()
                        .stream()
                        .anyMatch(obj -> obj.getPersona().equals("ander")), () -> "El nombre no esta asociado a ningun banco"));
    }





    @Nested
    class SistemaOperativoTest {

        //**************************************************
        // Se manejan para sistemas operativos
        //**************************************************
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {

        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSoloMacLinux() {

        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {

        }
        //**************************************************
        // fin
        //**************************************************
    }





    @Nested
    class JavaSystemTest {
        //**************************************************
        // Se manejan para versiones de java
        //**************************************************
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJDK8() {
        }

        @Test
        @DisabledOnJre(JRE.JAVA_8)
        void testNoJDK8() {
        }
        //**************************************************
        // fin
        //**************************************************


        //**************************************************
        //system test
        //**************************************************
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k,v) -> System.out.println(k + ":" + v));
        }

        @Test
        void imprimirVariablesAmbiente() {
            final Map<String, String> getenv = System.getenv();
            getenv.forEach((k,v) -> System.out.println(k + " = " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = "C:\\Program Files\\Java\\jdk1.8.0_192")
        @Disabled
        void testJavaHome() {
        }

        //***************************************************
        //fin
        //***************************************************

        @Test
        @DisplayName("Probando el saldo de la cuenta DEV")
        void testSaldoCuentaDev() {

            boolean esDev = "dev".equals(System.getProperty("ENV"));

            Cuenta cuenta = Cuenta.builder()
                    .persona("Christian")
                    .saldo(new BigDecimal("1000.25"))
                    .build();

            assumingThat(esDev, () -> {
                assertNotNull(cuenta.getSaldo());
            });

            assumeTrue(esDev);

            assertEquals(1000.25, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }





    //***************************************************
    //Repeticion de test
    //***************************************************
    @RepeatedTest(value = 5, name = "Repeticion numero {currentRepetition} de {totalRepetition}")
    void testRepetirFive(RepetitionInfo info) {
        if (info.getCurrentRepetition() == 3){
            System.out.println("estamos en la repeticion " + info.getCurrentRepetition());
        }

        Cuenta cuenta = Cuenta.builder()
                .persona("Alexis")
                .saldo(new BigDecimal("1000.25"))
                .build();

        cuenta.debito(new BigDecimal(100));

        assertEquals(900, cuenta.getSaldo().intValue());
        assertNotNull(cuenta.getSaldo());
        assertEquals("900.25", cuenta.getSaldo().toPlainString());
    }
    //***************************************************
    //fin
    //***************************************************



    @Nested
    @Tag("params") // se agrega etiqueta
    class PruebasParametrizadasTest{
        //***************************************************
        //Test Parametrizados
        //***************************************************
        @ParameterizedTest(name = "numero {index} ejecutando con valor {argumentsWithNames}")
        @ValueSource(strings = {"100", "200","300","500","700","1000"})
        @DisplayName("Probando el debito")
        void testDebitoCuentaValueSource(String monto) {
            cuenta.debito(new BigDecimal(monto));

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
            assertNotNull(cuenta.getSaldo());
        }
        //***************************************************
        //fin
        //***************************************************



        //***************************************************
        //Test Parametrizados  2
        //***************************************************
        @ParameterizedTest(name = "numero {index} ejecutando con valor {argumentsWithNames}")
        @CsvSource({"1,300","2,500","3,700","4,1000"})
        @DisplayName("Probando el debito")
        void testDebitoCuentaCsvValueSource(String index, String monto) {
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
            assertNotNull(cuenta.getSaldo());
        }


        @ParameterizedTest(name = "numero {index} ejecutando con valor {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        @DisplayName("Probando el debito")
        void testDebitoCuentaCsvFileValueSource(String monto) {
            cuenta.debito(new BigDecimal(monto));

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
            assertNotNull(cuenta.getSaldo());
        }


        @ParameterizedTest(name = "numero {index} ejecutando con valor {argumentsWithNames}")
        @CsvSource({"200,100","250,200","301,300","550,500"})
        @DisplayName("Probando el debito")
        void testDebitoCuentaCsvValueSource2(String saldo, String monto) {
            System.out.println(saldo + " -> " + monto);

            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
            assertNotNull(cuenta.getSaldo());
        }
        //***************************************************
        //fin
        //***************************************************
    }



    @Tag("params")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {argumentsWithNames}")
    @MethodSource("montoList")
    @DisplayName("Probando el debito")
    void testDebitoCuentaMethodSource(String monto) {
        this.cuenta.debito(new BigDecimal(monto));

        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(cuenta.getSaldo());
    }

    static List<String> montoList(){
        return Arrays.asList("100", "200","300","500","700","1000");
    }


    @Nested
    @Tag("timeout")
    class EjemploTimeoutTest{
        //solo da espera de 5 segundos, luego lanza error
        @Test
        @Timeout(5)
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.SECONDS.sleep(4); // se envia sobrecarga para 6 segundos
        }

        @Test
        @Timeout(value=50000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeout2() throws InterruptedException {
            TimeUnit.SECONDS.sleep(6); // se envia sobrecarga para 6 segundos
        }

        @Test
        void testTimeOutAssertions() {
            assertTimeout(Duration.ofSeconds(5),() -> {
                TimeUnit.MILLISECONDS.sleep(1100);
            });
        }
    }

}






















