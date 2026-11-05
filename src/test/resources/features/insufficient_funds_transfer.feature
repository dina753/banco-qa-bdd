# language: es
Característica: Transferencias con fondos insuficientes
  Para proteger el saldo disponible de los clientes
  Como cliente del banco
  Quiero que se rechacen las transferencias que superen el saldo de mi cuenta

  Escenario: Rechazar transferencia superior al saldo disponible
    Dado que creo una cuenta de origen con saldo inicial de 500
    Y creo una cuenta de destino con saldo inicial de 1000
    Cuando intento transferir 800 de la cuenta de origen a la cuenta de destino
    Entonces la API rechaza la transferencia con HTTP 400 y error "Saldo insuficiente"
    Y el saldo de la cuenta de origen permanece en 500
    Y el saldo de la cuenta de destino permanece en 1000
