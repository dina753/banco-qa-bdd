# language: en
Feature: Bank transfers
  As a bank customer
  I want to transfer money between accounts
  So that I can move my funds securely and safe

  Background:
    Given a source account with a balance of 10000
    And a destination account with a balance of 500

  Scenario: A successful transfer updates both balances
    When I transfer 1500 from the source account to the destination account
    Then the transfer completes successfully
    And the source account balance is 8500
    And the destination account balance is 2000

  Scenario: Cannot transfer more than the available balance
    When I transfer 10001 from the source account to the destination account
    Then the API responds with HTTP 400 and error "Saldo insuficiente"

  Scenario: Cannot exceed the daily limit for the source account
    When I transfer 4000 from the source account to the destination account
    And I transfer 1001 from the source account to the destination account
    Then the API responds with HTTP 400 and error "Límite diario excedido"

  Scenario: Cannot transfer from a blocked account
    Given the source account is blocked
    When I transfer 100 from the source account to the destination account
    Then the API responds with HTTP 403 and error "Cuenta bloqueada"

  Scenario: Cannot make a transfer with a zero amount
    When I transfer 0 from the source account to the destination account
    Then the API responds with HTTP 400 and error "Monto inválido"
