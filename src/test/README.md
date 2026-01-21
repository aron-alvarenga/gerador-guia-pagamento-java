# Suíte de Testes - Código de Barras e PIX

> Testes automatizados para validação de algoritmos bancários brasileiros (DV FEBRABAN e CRC16 PIX)

## Visão Geral

Esta suíte de testes automatizados valida a geração e validação de algoritmos bancários brasileiros:

- **DV FEBRABAN**: Dígito verificador para códigos de barras (padrão FEBRABAN)
- **CRC16 PIX**: Checksum do payload EMV para QR Code PIX (padrão Banco Central)

## Estrutura de Testes

### Testes Unitários

#### CodigoBarrasServiceTest

Testes de validação do dígito verificador FEBRABAN:

- Geração de código de barras com DV válido
- Validação de diferentes cenários:
  - Valores mínimo e máximo
  - Casos normais de uso
  - Diferentes datas de vencimento
- Detecção de DV inválido
- Testes de compatibilidade (mesmo input = mesmo output)
- Validação do formato do código de barras (44 dígitos)

**Total**: 8 testes

#### PixServiceTest

Testes de validação do CRC16 do payload EMV:

- Geração de QR Code PIX com CRC16 válido
- Validação de diferentes tipos de chave PIX:
  - CPF
  - CNPJ
  - E-mail
  - Telefone
  - Chave aleatória (UUID)
- Detecção de CRC16 inválido
- Testes de compatibilidade (mesmo input = mesmo output)
- Validação da estrutura do payload EMV

**Total**: 9 testes

### Utilitários de Validação

**ValidacaoUtil** - Classe utilitária com métodos reutilizáveis:

| Método | Descrição |
|--------|-----------|
| `validarDVFEBRABAN(String codigoBarras)` | Valida o dígito verificador de um código de barras |
| `calcularDVFEBRABAN(String codigo)` | Calcula o DV FEBRABAN para um código sem DV |
| `validarCRC16EMV(String payload)` | Valida o CRC16 do payload PIX |
| `calcularCRC16(String payload)` | Calcula o CRC16-CCITT para um payload |

### Golden Files

Casos conhecidos documentados em JSON para garantir compatibilidade:

- `codigo-barras-casos-conhecidos.json` - Casos de teste para código de barras
- `pix-casos-conhecidos.json` - Casos de teste para QR Code PIX

## Executando os Testes

### Todos os Testes

```bash
mvn test
```

### Testes Específicos

```bash
# Apenas testes de código de barras
mvn test -Dtest=CodigoBarrasServiceTest

# Apenas testes de PIX
mvn test -Dtest=PixServiceTest
```

### Executar com Relatório

```bash
mvn test surefire-report:report
```

O relatório será gerado em `target/site/surefire-report.html`

## Cobertura de Testes

### Estatísticas

- **Total de testes**: 17
- **Testes de código de barras**: 8
- **Testes de PIX**: 9
- **Taxa de sucesso**: 100% (todos os testes passando)

### Garantias dos Testes

Os testes garantem:

- ✅ Validação correta do DV FEBRABAN
- ✅ Validação correta do CRC16 do payload EMV
- ✅ Detecção de valores inválidos
- ✅ Compatibilidade (testes de regressão)
- ✅ Diferentes cenários e tipos de dados
- ✅ Consistência (mesmo input sempre gera mesmo output)

## Detalhes Técnicos

### DV FEBRABAN

**Algoritmo**: Módulo 11 com multiplicadores de 2 a 9

**Processo de cálculo**:
1. Multiplica-se cada dígito do código (da direita para esquerda) por multiplicadores de 2 a 9 (ciclicamente)
2. Soma-se todos os resultados
3. Calcula-se o resto da divisão por 11
4. Subtrai-se 11 do resto
5. Se o resultado for 0, 10 ou 11, o DV é 1; caso contrário, é o próprio resultado

**Estrutura do código de barras (44 dígitos)**:
- Posições 0-2: Código do banco (001)
- Posição 3: Código da moeda (9)
- Posição 4: **Dígito verificador**
- Posições 5-8: Fator de vencimento
- Posições 9-18: Valor (10 dígitos)
- Posições 19-43: Campo livre (25 dígitos)

**Valores especiais**: Se DV = 0, 10 ou 11, então DV = 1

### CRC16 PIX

**Algoritmo**: CRC16-CCITT (polinômio 0x8408)

**Processo de cálculo**:
1. Inicializa-se o CRC com 0xFFFF
2. Para cada byte do payload:
   - Faz-se XOR com o CRC
   - Para cada bit (8 iterações):
     - Se o bit menos significativo for 1, faz-se shift right e XOR com 0x8408
     - Caso contrário, apenas shift right
3. Inverte-se todos os bits do CRC
4. Retorna-se o resultado em hexadecimal (4 dígitos)

**Estrutura do payload EMV**:
- Campos obrigatórios: Payload Format Indicator, Merchant Account Information, Merchant Category Code, Transaction Currency, Transaction Amount, Country Code, Merchant Name, Merchant City
- Campo CRC16: Último campo (ID 63) com 4 dígitos hexadecimais

**Posição no payload**: Últimos 4 caracteres após "6304"

**Formato**: Hexadecimal em maiúsculas (4 dígitos)

## Estratégia de Testes

### Testes Unitários

Focam na validação dos algoritmos de cálculo e verificação:

- Cálculo correto do DV FEBRABAN
- Cálculo correto do CRC16
- Validação de códigos gerados
- Detecção de valores inválidos

### Testes de Integração

Validam o fluxo completo de geração:

- Geração de código de barras completo
- Geração de QR Code PIX completo
- Validação end-to-end

### Testes de Regressão

Garantem compatibilidade:

- Mesmo input sempre gera mesmo output
- Mudanças não quebram funcionalidades existentes
- Golden files servem como referência

## Estrutura de Arquivos

```
src/test/
├── java/com/aronalvarenga/gerador/
│   ├── service/
│   │   ├── CodigoBarrasServiceTest.java
│   │   └── PixServiceTest.java
│   └── util/
│       └── ValidacaoUtil.java
└── resources/
    └── golden-files/
        ├── codigo-barras-casos-conhecidos.json
        └── pix-casos-conhecidos.json
```

## Boas Práticas

### Nomenclatura

- Nomes de testes descritivos usando `@DisplayName`
- Padrão: `deve[Comportamento]_[Condicao]`

### Organização

- Um teste por comportamento
- Testes independentes (sem dependências entre testes)
- Setup e teardown quando necessário

### Assertions

- Mensagens descritivas em todas as assertions
- Validações específicas (não apenas `assertTrue`)
- Testes de casos negativos (valores inválidos)

## Troubleshooting

### Testes Falhando

1. Verifique se todas as dependências estão instaladas:
   ```bash
   mvn dependency:resolve
   ```

2. Execute com verbose para ver detalhes:
   ```bash
   mvn test -X
   ```

3. Verifique os relatórios em `target/surefire-reports/`

### Problemas Comuns

- **DV inválido**: Verifique se o código tem exatamente 44 dígitos
- **CRC16 inválido**: Verifique se o payload está completo (incluindo campo "6304")
- **Testes intermitentes**: Verifique se não há dependências de estado compartilhado

## Contribuindo

Ao adicionar novos testes:

1. Siga o padrão de nomenclatura existente
2. Adicione casos ao golden files quando apropriado
3. Mantenha os testes independentes
4. Documente casos especiais ou complexos

## Referências

- [Padrão FEBRABAN](https://www.febraban.org.br/)
- [Padrão PIX - Banco Central](https://www.bcb.gov.br/estabilidadefinanceira/pix)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
