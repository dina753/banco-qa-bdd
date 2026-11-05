# banco-qa-bdd

Pruebas BDD con Cucumber y Java 17 para validar la API `mini-banco-api` mediante llamadas HTTP. Este proyecto es independiente de la API y del proyecto SauceDemo.

## Estructura

```text
banco-qa-bdd/
├── pom.xml
└── src/test/
    ├── java/com/banco/qa/
    │   ├── clients/       # Cliente HTTP REST Assured
    │   ├── config/        # Carga de configuración por ambiente
    │   ├── runners/       # Runner JUnit 4 + Cucumber
    │   └── steps/         # Hooks y definiciones de pasos
    └── resources/
        ├── config/        # qa.properties y uat.properties
        └── features/      # Escenarios Gherkin en español
```

## Ejecutar en QA local

Primero inicia la API en otra terminal:

```bash
cd ../mini-banco-api
mvn spring-boot:run
```

La API usa `http://127.0.0.1:8081` para evitar conflictos con servicios locales que ocupen el puerto 8080. Con la API escuchando, ejecuta desde `banco-qa-bdd`:

```bash
mvn test
```

QA reinicia los datos antes de cada escenario con `DELETE /api/test-data`. No ejecutes estas pruebas contra una base o servicio compartido.

Para validar que Cucumber encuentra los escenarios y enlaza todos los pasos sin llamar a la API:

```bash
mvn test -Dcucumber.execution.dry-run=true
```

## Ejecutar en UAT

Configura `api.base-url` en `src/test/resources/config/uat.properties` y ejecuta:

```bash
mvn test -Denv=uat
```

Los hooks no llaman al endpoint de reinicio en UAT para evitar borrar datos del ambiente. Los escenarios crean cuentas con IDs nuevos y deben ejecutarse contra una API accesible en la URL configurada.

También se puede sobrescribir la URL sin cambiar el archivo:

```bash
mvn test -Denv=qa -Dapi.base-url=http://127.0.0.1:8081
```

## Escenarios cubiertos

- Transferencia correcta y actualización de ambos saldos.
- Saldo insuficiente.
- Límite diario de transferencias.
- Cuenta de origen bloqueada.
- Monto cero.

## Principios SOLID

1. **S – Single Responsibility Principle:** Cada clase debe tener una única responsabilidad y una sola razón para cambiar.

2. **O – Open/Closed Principle:** El código debe permitir extender funcionalidades sin modificar el código existente.

3. **L – Liskov Substitution Principle:** Las clases hijas deben poder reemplazar a sus clases padre sin alterar el comportamiento esperado.

4. **I – Interface Segregation Principle:** Las interfaces deben ser pequeñas y específicas. No obligar a implementar métodos que no se necesitan.

5. **D – Dependency Inversion Principle:** Depender de abstracciones, no de implementaciones concretas. 