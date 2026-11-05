package com.banco.qa.steps;

import com.banco.qa.clients.CuentaClient;
import com.banco.qa.config.EnvironmentConfig;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TransferenciaSteps {
    private final CuentaClient cuentaClient = new CuentaClient(EnvironmentConfig.load());
    private String cuentaOrigen;
    private String cuentaDestino;
    private Response respuesta;

    @Given("a source account with a balance of {double}")
    public void aSourceAccountWithBalance(double saldoInicial) {
        respuesta = cuentaClient.crearCuenta("qa-origen", "CORRIENTE", saldoInicial);
        assertEquals("No se pudo crear la cuenta de origen", 201, respuesta.statusCode());
        cuentaOrigen = respuesta.jsonPath().getString("cuentaId");
    }

    @Given("a destination account with a balance of {double}")
    public void aDestinationAccountWithBalance(double saldoInicial) {
        respuesta = cuentaClient.crearCuenta("qa-destino", "AHORROS", saldoInicial);
        assertEquals("No se pudo crear la cuenta de destino", 201, respuesta.statusCode());
        cuentaDestino = respuesta.jsonPath().getString("cuentaId");
    }

    @Given("que creo una cuenta de origen con saldo inicial de {double}")
    public void creoUnaCuentaDeOrigenConSaldoInicial(double saldoInicial) {
        respuesta = cuentaClient.crearCuenta(UUID.randomUUID().toString(), "CORRIENTE", saldoInicial);
        assertEquals("No se pudo crear la cuenta de origen", 201, respuesta.statusCode());
        cuentaOrigen = respuesta.jsonPath().getString("cuentaId");
    }

    @Given("creo una cuenta de destino con saldo inicial de {double}")
    public void creoUnaCuentaDeDestinoConSaldoInicial(double saldoInicial) {
        respuesta = cuentaClient.crearCuenta(UUID.randomUUID().toString(), "AHORROS", saldoInicial);
        assertEquals("No se pudo crear la cuenta de destino", 201, respuesta.statusCode());
        cuentaDestino = respuesta.jsonPath().getString("cuentaId");
    }

    @Given("the source account is blocked")
    public void theSourceAccountIsBlocked() {
        respuesta = cuentaClient.bloquearCuenta(cuentaOrigen);
        assertEquals("No se pudo bloquear la cuenta de origen", 200, respuesta.statusCode());
    }

    @When("I transfer {double} from the source account to the destination account")
    public void iTransferAmount(double monto) {
        respuesta = cuentaClient.transferir(cuentaOrigen, cuentaDestino, monto);
    }

    @When("intento transferir {double} de la cuenta de origen a la cuenta de destino")
    public void intentoTransferirMonto(double monto) {
        respuesta = cuentaClient.transferir(cuentaOrigen, cuentaDestino, monto);
    }

    @Then("the transfer completes successfully")
    public void theTransferCompletesSuccessfully() {
        assertEquals(respuesta.asString(), 200, respuesta.statusCode());
    }

    @Then("la API rechaza la transferencia con HTTP {int} y error {string}")
    public void laApiRechazaTransferenciaConError(int statusEsperado, String errorEsperado) {
        assertEquals("Se esperaba HTTP " + statusEsperado + ". Respuesta: " + respuesta.asString(),
                statusEsperado, respuesta.statusCode());
        assertEquals(errorEsperado, respuesta.jsonPath().getString("error"));
    }

    @Then("the source account balance is {double}")
    public void theSourceAccountBalanceIs(double saldoEsperado) {
        Response consulta = cuentaClient.consultarCuenta(cuentaOrigen);
        assertEquals(200, consulta.statusCode());
        assertEquals(saldoEsperado, consulta.jsonPath().getDouble("saldo"), 0.001);
    }

    @Then("the destination account balance is {double}")
    public void theDestinationAccountBalanceIs(double saldoEsperado) {
        Response consulta = cuentaClient.consultarCuenta(cuentaDestino);
        assertEquals(200, consulta.statusCode());
        assertEquals(saldoEsperado, consulta.jsonPath().getDouble("saldo"), 0.001);
    }

    @Then("el saldo de la cuenta de origen permanece en {double}")
    public void elSaldoDeLaCuentaDeOrigenPermaneceEn(double saldoEsperado) {
        verificarSaldo(cuentaOrigen, saldoEsperado);
    }

    @Then("el saldo de la cuenta de destino permanece en {double}")
    public void elSaldoDeLaCuentaDeDestinoPermaneceEn(double saldoEsperado) {
        verificarSaldo(cuentaDestino, saldoEsperado);
    }

    @Then("the API responds with HTTP {int} and error {string}")
    public void theApiRespondsWithError(int statusEsperado, String errorEsperado) {
        assertEquals(respuesta.asString(), statusEsperado, respuesta.statusCode());
        assertEquals(errorEsperado, respuesta.jsonPath().getString("error"));
    }

    private void verificarSaldo(String cuentaId, double saldoEsperado) {
        Response consulta = cuentaClient.consultarCuenta(cuentaId);
        assertEquals("No se pudo consultar la cuenta " + cuentaId + ": " + consulta.asString(),
                200, consulta.statusCode());
        assertEquals(saldoEsperado, consulta.jsonPath().getDouble("saldo"), 0.001);
    }
}
