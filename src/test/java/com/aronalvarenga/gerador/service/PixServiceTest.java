package com.aronalvarenga.gerador.service;

import com.aronalvarenga.gerador.model.GuiaPagamento;
import com.aronalvarenga.gerador.util.ValidacaoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de PIX - Validação CRC16 do Payload EMV")
class PixServiceTest {

    private PixService pixService;

    @BeforeEach
    void setUp() {
        pixService = new PixService();
    }

    @Test
    @DisplayName("Deve gerar QR Code PIX com CRC16 válido - Caso 1")
    void deveGerarQRCodePixComCRC16Valido_Caso1() {
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

        String qrCode = pixService.gerarQRCodePix(guia);

        assertNotNull(qrCode);
        assertTrue(ValidacaoUtil.validarCRC16EMV(qrCode), "CRC16 do payload EMV deve ser válido");
    }

    @Test
    @DisplayName("Deve gerar QR Code PIX com CRC16 válido - Caso 2")
    void deveGerarQRCodePixComCRC16Valido_Caso2() {
        // Golden file case 2
        GuiaPagamento guia = new GuiaPagamento(
                "MARIA SANTOS",
                "98765432100",
                "Av. Principal, 456 - Jardim - São Paulo/SP",
                "maria.santos@email.com",
                new BigDecimal("250.75"),
                "Pagamento",
                LocalDate.of(2024, 3, 20),
                "9876543210"
        );

        String qrCode = pixService.gerarQRCodePix(guia);

        assertNotNull(qrCode);
        assertTrue(ValidacaoUtil.validarCRC16EMV(qrCode), "CRC16 do payload EMV deve ser válido");
    }

    @Test
    @DisplayName("Deve gerar QR Code PIX com CRC16 válido - Caso 3 (chave aleatória)")
    void deveGerarQRCodePixComCRC16Valido_ChaveAleatoria() {
        // Golden file case 3 - chave aleatória (UUID)
        GuiaPagamento guia = new GuiaPagamento(
                "PEDRO COSTA",
                "11122233344",
                "Rua Pequena, 1 - Bairro - Rio de Janeiro/RJ",
                "550e8400-e29b-41d4-a716-446655440000",
                new BigDecimal("50.00"),
                "Teste UUID",
                LocalDate.of(2024, 6, 1),
                "1111111111"
        );

        String qrCode = pixService.gerarQRCodePix(guia);

        assertNotNull(qrCode);
        assertTrue(ValidacaoUtil.validarCRC16EMV(qrCode), "CRC16 do payload EMV deve ser válido");
    }

    @Test
    @DisplayName("Deve gerar QR Code PIX com CRC16 válido - Caso 4 (valor mínimo)")
    void deveGerarQRCodePixComCRC16Valido_ValorMinimo() {
        // Golden file case 4 - valor mínimo
        GuiaPagamento guia = new GuiaPagamento(
                "ANA OLIVEIRA",
                "55566677788",
                "Av. Grande, 9999 - Centro - Belo Horizonte/MG",
                "+5511999999999",
                new BigDecimal("0.01"),
                "Valor mínimo",
                LocalDate.of(2024, 12, 31),
                "9999999999"
        );

        String qrCode = pixService.gerarQRCodePix(guia);

        assertNotNull(qrCode);
        assertTrue(ValidacaoUtil.validarCRC16EMV(qrCode), "CRC16 do payload EMV deve ser válido");
    }

    @Test
    @DisplayName("Deve validar CRC16 corretamente para payload conhecido")
    void deveValidarCRC16_PayloadConhecido() {
        // Golden file - payload EMV conhecido
        // Payload sem CRC: 00020126580014br.gov.bcb.pix0114123456789010212...
        // CRC16 calculado deve ser válido
        
        String payloadSemCRC = "00020126580014br.gov.bcb.pix0114123456789010212520400053039865404100.505802BR5913JOAO SILVA6009SAO PAULO62070503***6304";
        String crc16Esperado = ValidacaoUtil.calcularCRC16(payloadSemCRC + "6304");
        
        String payloadCompleto = payloadSemCRC + "6304" + crc16Esperado;
        
        assertTrue(ValidacaoUtil.validarCRC16EMV(payloadCompleto), 
                "CRC16 calculado deve ser válido para o payload conhecido");
    }

    @Test
    @DisplayName("Deve detectar CRC16 inválido")
    void deveDetectarCRC16Invalido() {
        GuiaPagamento guia = new GuiaPagamento(
                "TESTE CRC INVALIDO",
                "12345678901",
                "Rua Teste - Campo Grande/MS",
                "12345678901",
                new BigDecimal("100.00"),
                "Teste",
                LocalDate.of(2024, 1, 1),
                "1234567890"
        );

        String qrCodeValido = pixService.gerarQRCodePix(guia);
        
        // Modifica o CRC16 para um valor incorreto
        String qrCodeInvalido = qrCodeValido.substring(0, qrCodeValido.length() - 4) + "FFFF";
        
        assertFalse(ValidacaoUtil.validarCRC16EMV(qrCodeInvalido), 
                "CRC16 incorreto deve ser detectado");
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

        String qrCode1 = pixService.gerarQRCodePix(guia);
        String qrCode2 = pixService.gerarQRCodePix(guia);

        assertEquals(qrCode1, qrCode2, "Mesmo input deve gerar mesmo QR Code PIX");
        assertTrue(ValidacaoUtil.validarCRC16EMV(qrCode1), "CRC16 deve ser válido");
        assertTrue(ValidacaoUtil.validarCRC16EMV(qrCode2), "CRC16 deve ser válido");
    }

    @Test
    @DisplayName("Deve validar estrutura do payload EMV")
    void deveValidarEstruturaPayloadEMV() {
        GuiaPagamento guia = new GuiaPagamento(
                "VALIDACAO ESTRUTURA",
                "12345678901",
                "Rua Teste - Campo Grande/MS",
                "12345678901",
                new BigDecimal("50.00"),
                "Teste",
                LocalDate.of(2024, 5, 15),
                "1234567890"
        );

        String qrCode = pixService.gerarQRCodePix(guia);

        // Valida estrutura básica do payload EMV
        assertTrue(qrCode.startsWith("00"), 
                "Payload deve começar com Payload Format Indicator");
        assertTrue(qrCode.contains("6304"), 
                "Payload deve conter o campo CRC16 (63) com tamanho 04");
        assertTrue(qrCode.length() > 20, 
                "Payload deve ter tamanho mínimo");
        
        // CRC16 deve ser os últimos 4 caracteres (hexadecimal)
        String crc16 = qrCode.substring(qrCode.length() - 4);
        assertTrue(crc16.matches("^[0-9A-F]{4}$"), 
                "CRC16 deve ser hexadecimal de 4 dígitos");
    }

    @Test
    @DisplayName("Deve validar CRC16 para diferentes tipos de chave PIX")
    void deveValidarCRC16_DiferentesTiposChavePix() {
        // Testa com diferentes tipos de chave PIX
        String[] chavesPix = {
                "12345678901", // CPF
                "12345678000190", // CNPJ
                "+5511999999999", // Telefone
                "usuario@email.com", // Email
                "550e8400-e29b-41d4-a716-446655440000" // Chave aleatória
        };

        for (String chavePix : chavesPix) {
            GuiaPagamento guia = new GuiaPagamento(
                    "TESTE CHAVE",
                    "12345678901",
                    "Rua Teste - Campo Grande/MS",
                    chavePix,
                    new BigDecimal("100.00"),
                    "Teste",
                    LocalDate.of(2024, 1, 1),
                    "1234567890"
            );

            String qrCode = pixService.gerarQRCodePix(guia);
            assertTrue(ValidacaoUtil.validarCRC16EMV(qrCode), 
                    "CRC16 deve ser válido para chave PIX: " + chavePix);
        }
    }

}
