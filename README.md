# Gerador de Guia de Pagamento

> Sistema desktop Java para geração de guias de pagamento com código de barras FEBRABAN, QR Code PIX e exportação para PDF.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## Sobre o Projeto

O **Gerador de Guia de Pagamento** é uma aplicação desktop desenvolvida em Java que permite gerar guias de pagamento completas com código de barras válido (padrão FEBRABAN), QR Code PIX (padrão EMV) e exportação para PDF. O projeto demonstra implementação de algoritmos bancários brasileiros (DV FEBRABAN e CRC16 PIX) com validação completa através de testes automatizados.

### Objetivos

- Demonstrar implementação de padrões bancários brasileiros (FEBRABAN e PIX)
- Gerar códigos de barras e QR Codes PIX válidos e testáveis
- Aplicar boas práticas de desenvolvimento (testes, validações, documentação)
- Servir como referência para implementação de algoritmos de validação bancária

## Funcionalidades

### Geração de Código de Barras
- Implementação do padrão FEBRABAN de 44 dígitos
- Cálculo automático do dígito verificador (módulo 11)
- Validação completa do DV FEBRABAN
- Geração de imagem visual do código de barras (Code128)
- Formatação adequada para exibição

### Geração de QR Code PIX
- Implementação do padrão EMV para PIX
- Cálculo de CRC16-CCITT (polinômio 0x8408) para validação
- Validação completa do CRC16 do payload EMV
- Suporte a diferentes tipos de chave PIX:
  - E-mail
  - Telefone
  - CPF/CNPJ
  - Chave aleatória (UUID)

### Interface Gráfica
- Interface Swing com Look and Feel do sistema
- Formulário organizado e intuitivo
- Pré-visualização em tempo real
- Validações de entrada
- Scroll automático para guias grandes

### Exportação
- Geração de documentos PDF profissionais
- Layout otimizado para impressão A4
- Preserva todos os elementos (código de barras, QR Code, dados)

## Stack Tecnológica

### Core
- **Java 21 LTS** - Linguagem de programação
- **Maven** - Gerenciamento de dependências e build

### Bibliotecas
- **Barcode4J** - Geração de códigos de barras Code128
- **ZXing** - Geração de QR codes PIX
- **Apache Commons Lang** - Utilitários para formatação
- **iTextPDF** - Geração de documentos PDF

### Testes
- **JUnit 5** - Framework de testes
- **Maven Surefire Plugin** - Execução de testes

## Estrutura do Projeto

```
gerador-guia-pagamento-java/
├── src/
│   ├── main/
│   │   ├── java/com/aronalvarenga/gerador/
│   │   │   ├── GeradorGuiaPagamentoApp.java    # Aplicação principal
│   │   │   ├── model/
│   │   │   │   └── GuiaPagamento.java          # Modelo de dados
│   │   │   ├── service/
│   │   │   │   ├── CodigoBarrasService.java    # Geração código de barras
│   │   │   │   ├── PdfService.java             # Geração de PDF
│   │   │   │   └── PixService.java             # Geração QR Code PIX
│   │   │   ├── ui/
│   │   │   │   └── GuiaPagamentoPanel.java     # Interface gráfica
│   │   │   └── util/
│   │   │       └── BarcodeUtil.java            # Utilitário Barcode4J
│   │   └── resources/
│   │       └── images/                          # Screenshots e imagens
│   └── test/
│       ├── java/com/aronalvarenga/gerador/
│       │   ├── service/
│       │   │   ├── CodigoBarrasServiceTest.java # Testes DV FEBRABAN
│       │   │   └── PixServiceTest.java         # Testes CRC16 PIX
│       │   └── util/
│       │       └── ValidacaoUtil.java          # Utilitários de validação
│       └── resources/
│           └── golden-files/                    # Casos conhecidos
│               ├── codigo-barras-casos-conhecidos.json
│               └── pix-casos-conhecidos.json
├── pom.xml
└── README.md
```

## Como Executar

### Pré-requisitos

- **Java 21** ou superior
- **Maven 3.6** ou superior

### Executando Localmente

1. **Clone o repositório**
   ```bash
   git clone <repository-url>
   cd gerador-guia-pagamento-java
   ```

2. **Compile o projeto**
   ```bash
   mvn clean compile
   ```

3. **Execute a aplicação**
   ```bash
   mvn exec:java -Dexec.mainClass="com.aronalvarenga.gerador.GeradorGuiaPagamentoApp"
   ```

   Ou gere um JAR executável:
   ```bash
   mvn clean package
   java -jar target/gerador-guia-pagamento-1.0-SNAPSHOT.jar
   ```

### Executando Testes

```bash
# Todos os testes
mvn test

# Testes específicos
mvn test -Dtest=CodigoBarrasServiceTest
mvn test -Dtest=PixServiceTest
```

## Como Usar

1. **Iniciar a aplicação**: Execute a classe principal `GeradorGuiaPagamentoApp`

2. **Preencher dados**: O sistema vem com dados fictícios pré-preenchidos para facilitar os testes:
   - Nome do Proprietário: João da Silva Santos
   - CPF: 123.456.789-01
   - Endereço: Rua das Flores, 123 - Centro - Campo Grande/MS
   - Chave PIX: joao.silva@email.com
   - Valor: R$ 150,50
   - Descrição: Pagamento de Taxa de Condomínio - Janeiro 2025
   - Vencimento: 28/02/2025
   - Nº da Guia: 2025010001

3. **Gerar guia**: Clique no botão "Gerar Guia de Pagamento"

4. **Visualizar resultado**: A guia será exibida com:
   - Cabeçalho com número e data de vencimento
   - Dados do proprietário
   - Dados do pagamento
   - Código de barras válido (padrão FEBRABAN)
   - QR Code PIX com instruções

5. **Exportar PDF**: Use a funcionalidade de exportação para gerar o documento PDF

## Testes Automatizados

O projeto inclui uma suíte completa de testes automatizados para garantir a qualidade e confiabilidade das implementações de algoritmos bancários.

### Cobertura de Testes

- **17 testes automatizados** cobrindo:
  - Validação do **DV FEBRABAN** para códigos de barras
  - Validação do **CRC16** do payload EMV para QR Code PIX
  - Diferentes cenários (valores mínimo/máximo, diferentes tipos de chave PIX)
  - Detecção de valores inválidos
  - Testes de compatibilidade/regressão

### Estratégia de Testes

- **Testes Unitários**: Validação de algoritmos (DV FEBRABAN, CRC16)
- **Golden Files**: Casos conhecidos documentados para garantir compatibilidade
- **Testes de Regressão**: Garantir que mudanças não quebrem funcionalidades existentes

### Validações Implementadas

- **DV FEBRABAN**: Validação do dígito verificador usando módulo 11
  - Algoritmo: Módulo 11 com multiplicadores de 2 a 9
  - Posição no código: Dígito 4 (após banco + moeda)
  - Valores especiais: Se DV = 0, 10 ou 11, então DV = 1

- **CRC16 PIX**: Validação do CRC16-CCITT do payload EMV
  - Algoritmo: CRC16-CCITT (polinômio 0x8408)
  - Posição no payload: Últimos 4 caracteres após "6304"
  - Formato: Hexadecimal em maiúsculas (4 dígitos)

Para mais detalhes, consulte [src/test/README.md](src/test/README.md).

## Dependências

### Dependências de Produção

| Biblioteca | Versão | Propósito |
|------------|--------|-----------|
| Barcode4J | 2.1 | Geração de códigos de barras Code128 |
| ZXing Core | 3.4.1 | Geração de QR codes |
| ZXing JavaSE | 3.4.1 | Suporte JavaSE para QR codes |
| Apache Commons Lang | 3.12.0 | Utilitários para formatação |
| iTextPDF | 5.5.13.3 | Geração de documentos PDF |

### Dependências de Teste

| Biblioteca | Versão | Propósito |
|------------|--------|-----------|
| JUnit Jupiter | 5.10.1 | Framework de testes |

## Algoritmos Implementados

### DV FEBRABAN (Dígito Verificador)

O dígito verificador FEBRABAN é calculado usando o algoritmo módulo 11:

1. Multiplica-se cada dígito do código (da direita para esquerda) por multiplicadores de 2 a 9 (ciclicamente)
2. Soma-se todos os resultados
3. Calcula-se o resto da divisão por 11
4. Subtrai-se 11 do resto
5. Se o resultado for 0, 10 ou 11, o DV é 1; caso contrário, é o próprio resultado

**Estrutura do código de barras (44 dígitos)**:
- Posições 0-2: Código do banco (001)
- Posição 3: Código da moeda (9)
- Posição 4: Dígito verificador
- Posições 5-8: Fator de vencimento
- Posições 9-18: Valor (10 dígitos)
- Posições 19-43: Campo livre (25 dígitos)

### CRC16 PIX (Payload EMV)

O CRC16 do payload PIX é calculado usando o algoritmo CRC16-CCITT:

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

## Screenshots

<div align="center">

| Interface da Aplicação |
|:----------------------:|
| <img src="src/main/resources/images/imagem_app_desktop.PNG" alt="Interface da aplicação" width="600"/> |

| Validação do QR Code PIX |
|:------------------------:|
| <img src="src/main/resources/images/validacao_qrcode.PNG" alt="Validação do QR Code gerado" width="600"/> |
| *QR Code PIX em conformidade com o padrão EMV, contendo os campos obrigatórios como BR.GOV.BCB.PIX, identificação do beneficiário, valor e CRC para validação.* |

| PDF Gerado |
|:----------:|
| <img src="src/main/resources/images/pdf_gerado.PNG" alt="PDF gerado" width="600"/> |

</div>

## Implementações Recentes

### Testes Automatizados

- **Suíte completa de testes** com 17 casos de teste
- Validação de **DV FEBRABAN** para códigos de barras
- Validação de **CRC16** do payload EMV para PIX
- Golden files com casos conhecidos para garantir compatibilidade
- Detecção de valores inválidos
- Testes de regressão para evitar quebras

### Próximos Passos

- **Cobertura de código**: Aumentar cobertura de testes para outros componentes
- **Arquitetura de geração**: Separar "modelo → render → export"
- **Assinatura/segurança**: Implementar validação de integridade (mesmo que mock)
- **Empacotamento**: Gerar instalador ou JAR "one-click"

## Limitações e Considerações

Este sistema foi desenvolvido para **fins de teste técnico** com dados fictícios. Para uso em produção, seria necessário:

- Integração com APIs bancárias reais
- Implementação de segurança adequada
- Validações mais rigorosas (CPF, CNPJ, etc.)
- Persistência de dados
- Logs e auditoria
- Tratamento completo de erros
- Validação de chaves PIX reais

## Documentação Adicional

- [Documentação de Testes](src/test/README.md) - Detalhes sobre a suíte de testes
- [Golden Files](src/test/resources/golden-files/) - Casos conhecidos para validação

## Contribuindo

Este é um projeto de demonstração técnica. Contribuições são bem-vindas! Sinta-se à vontade para abrir issues e pull requests.

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## Autor

**Aron Alvarenga**

- GitHub: [@aron-alvarenga](https://github.com/aron-alvarenga)
- LinkedIn: [Aron Alvarenga](https://www.linkedin.com/in/aron-alvarenga)

## Agradecimentos

- FEBRABAN pelo padrão de código de barras
- Banco Central do Brasil pelo padrão PIX
- Todos os mantenedores das bibliotecas open-source utilizadas

---

**Nota**: Este projeto demonstra implementação de algoritmos bancários brasileiros com foco em qualidade, testes e validação. Os algoritmos implementados seguem os padrões oficiais FEBRABAN e EMV para PIX.
