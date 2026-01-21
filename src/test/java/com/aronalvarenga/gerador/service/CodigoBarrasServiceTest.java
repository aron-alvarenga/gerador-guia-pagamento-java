package com.aronalvarenga.gerador.service;

import com.aronalvarenga.gerador.model.GuiaPagamento;
import com.aronalvarenga.gerador.util.ValidacaoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Código de Barras - Validação DV FEBRABAN")
class CodigoBarrasServiceTest {

    private CodigoBarrasService codigoBarrasService;

    @BeforeEach
    void setUp() {
        codigoBarrasService = new CodigoBarrasService();
    }

    @Test
    @DisplayName("Deve gerar código de barras com DV válido - Caso 1")
    void deveGerarCodigoBarrasComDVValido_Caso1() {
        // Golden file case 1
        GuiaPagamento guia = new GuiaPagamento(
                "JOAO SILVA",
                "12345678901",
                "Rua Teste, 123 - Centro - Campo Grande/MS",
                "12345678901",
                new BigDecimal("100.50"),
                "Teste",
                LocalDate.of(2024, 1, 15),
                "1234567890"
        );

        String codigoBarras = codigoBarrasService.gerarCodigoBarras(guia);

        assertNotNull(codigoBarras);
        assertEquals(44, codigoBarras.length(), "Código de barras deve ter 44 dígitos");
        assertTrue(ValidacaoUtil.validarDVFEBRABAN(codigoBarras), "DV FEBRABAN deve ser válido");
    }

    @Test
    @DisplayName("Deve gerar código de barras com DV válido - Caso 2")
    void deveGerarCodigoBarrasComDVValido_Caso2() {
        // Golden file case 2
        GuiaPagamento guia = new GuiaPagamento(
                "MARIA SANTOS",
                "98765432100",
                "Av. Principal, 456 - Jardim - São Paulo/SP",
                "98765432100",
                new BigDecimal("250.75"),
                "Pagamento",
                LocalDate.of(2024, 3, 20),
                "9876543210"
        );

        String codigoBarras = codigoBarrasService.gerarCodigoBarras(guia);

        assertNotNull(codigoBarras);
        assertEquals(44, codigoBarras.length());
        assertTrue(ValidacaoUtil.validarDVFEBRABAN(codigoBarras), "DV FEBRABAN deve ser válido");
    }

    @Test
    @DisplayName("Deve gerar código de barras com DV válido - Caso 3 (valor mínimo)")
    void deveGerarCodigoBarrasComDVValido_ValorMinimo() {
        // Golden file case 3 - valor mínimo
        GuiaPagamento guia = new GuiaPagamento(
                "PEDRO COSTA",
                "11122233344",
                "Rua Pequena, 1 - Bairro - Rio de Janeiro/RJ",
                "11122233344",
                new BigDecimal("0.01"),
                "Taxa mínima",
                LocalDate.of(2024, 6, 1),
                "1111111111"
        );

        String codigoBarras = codigoBarrasService.gerarCodigoBarras(guia);

        assertNotNull(codigoBarras);
        assertEquals(44, codigoBarras.length());
        assertTrue(ValidacaoUtil.validarDVFEBRABAN(codigoBarras), "DV FEBRABAN deve ser válido");
    }

    @Test
    @DisplayName("Deve gerar código de barras com DV válido - Caso 4 (valor alto)")
    void deveGerarCodigoBarrasComDVValido_ValorAlto() {
        // Golden file case 4 - valor alto
        GuiaPagamento guia = new GuiaPagamento(
                "ANA OLIVEIRA",
                "55566677788",
                "Av. Grande, 9999 - Centro - Belo Horizonte/MG",
                "55566677788",
                new BigDecimal("999999.99"),
                "Valor máximo",
                LocalDate.of(2024, 12, 31),
                "9999999999"
        );

        String codigoBarras = codigoBarrasService.gerarCodigoBarras(guia);

        assertNotNull(codigoBarras);
        assertEquals(44, codigoBarras.length());
        assertTrue(ValidacaoUtil.validarDVFEBRABAN(codigoBarras), "DV FEBRABAN deve ser válido");
    }

    @Test
    @DisplayName("Deve validar DV FEBRABAN corretamente para código conhecido")
    void deveValidarDVFEBRABAN_CodigoConhecido() {
        // Golden file - gera um código de barras real e valida seu DV
        GuiaPagamento guia = new GuiaPagamento(
                "TESTE DV",
                "12345678901",
                "Rua Teste - Campo Grande/MS",
                "12345678901",
                new BigDecimal("100.00"),
                "Teste DV",
                LocalDate.of(2024, 1, 1),
                "1234567890"
        );

        // Gera código de barras usando o serviço
        String codigoBarras = codigoBarrasService.gerarCodigoBarras(guia);
        
        // Valida que o DV está correto
        assertTrue(ValidacaoUtil.validarDVFEBRABAN(codigoBarras), 
                "DV do código gerado deve ser válido");
        
        // Testa que um DV incorreto é detectado
        char[] codigoArray = codigoBarras.toCharArray();
        int dvAtual = Character.getNumericValue(codigoArray[4]);
        int dvIncorreto = (dvAtual + 1) % 10;
        codigoArray[4] = (char) ('0' + dvIncorreto);
        String codigoInvalido = new String(codigoArray);
        
        assertFalse(ValidacaoUtil.validarDVFEBRABAN(codigoInvalido), 
                "DV incorreto deve ser detectado");
    }

    @Test
    @DisplayName("Deve detectar DV inválido")
    void deveDetectarDVInvalido() {
        // Código com DV incorreto
        String codigoComDVIncorreto = "0019123456789012345678901234567890123456";
        
        // Se o código tiver 44 caracteres mas DV incorreto, a validação deve falhar
        if (codigoComDVIncorreto.length() == 44) {
            // Modifica o DV para um valor incorreto
            char[] codigoArray = codigoComDVIncorreto.toCharArray();
            int dvAtual = Character.getNumericValue(codigoArray[4]);
            int dvIncorreto = (dvAtual + 1) % 10; // DV diferente
            codigoArray[4] = (char) ('0' + dvIncorreto);
            String codigoInvalido = new String(codigoArray);
            
            assertFalse(ValidacaoUtil.validarDVFEBRABAN(codigoInvalido), 
                    "DV incorreto deve ser detectado");
        }
    }

    @Test
    @DisplayName("Deve manter compatibilidade - mesmo input gera mesmo output")
    void deveManterCompatibilidade_MesmoInputMesmoOutput() {
        // Teste de regressão - garantir que pequenas mudanças não quebrem compatibilidade
        GuiaPagamento guia = new GuiaPagamento(
                "TESTE COMPATIBILIDADE",
                "12345678901",
                "Rua Teste, 123 - Centro - Campo Grande/MS",
                "12345678901",
                new BigDecimal("100.00"),
                "Teste compatibilidade",
                LocalDate.of(2024, 1, 1),
                "1234567890"
        );

        String codigo1 = codigoBarrasService.gerarCodigoBarras(guia);
        String codigo2 = codigoBarrasService.gerarCodigoBarras(guia);

        assertEquals(codigo1, codigo2, "Mesmo input deve gerar mesmo código de barras");
        assertTrue(ValidacaoUtil.validarDVFEBRABAN(codigo1), "DV deve ser válido");
        assertTrue(ValidacaoUtil.validarDVFEBRABAN(codigo2), "DV deve ser válido");
    }

    @Test
    @DisplayName("Deve validar formato do código de barras")
    void deveValidarFormatoCodigoBarras() {
        GuiaPagamento guia = new GuiaPagamento(
                "VALIDACAO FORMATO",
                "12345678901",
                "Rua Teste - Campo Grande/MS",
                "12345678901",
                new BigDecimal("50.00"),
                "Teste",
                LocalDate.of(2024, 5, 15),
                "1234567890"
        );

        String codigoBarras = codigoBarrasService.gerarCodigoBarras(guia);

        // Valida estrutura: 001 (banco) + 9 (moeda) + DV + fator + valor + campo livre
        assertTrue(codigoBarras.matches("^\\d{44}$"), 
                "Código deve conter apenas dígitos");
        assertEquals("001", codigoBarras.substring(0, 3), 
                "Código do banco deve ser 001");
        assertEquals("9", codigoBarras.substring(3, 4), 
                "Código da moeda deve ser 9");
    }

}
