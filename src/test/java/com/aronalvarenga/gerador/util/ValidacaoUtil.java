package com.aronalvarenga.gerador.util;

import java.nio.charset.StandardCharsets;

/**
 * Utilitário para validação de DV FEBRABAN e CRC16 do PIX
 * Reutilizável em testes e validações
 */
public class ValidacaoUtil {

    /**
     * Valida o dígito verificador FEBRABAN de um código de barras
     * @param codigoBarras Código de barras completo (44 dígitos)
     * @return true se o DV for válido
     */
    public static boolean validarDVFEBRABAN(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.length() != 44) {
            return false;
        }

        // Extrai o DV do código (posição 4)
        int dvCodigo = Character.getNumericValue(codigoBarras.charAt(4));

        // Reconstrói o código sem o DV para recalcular
        // Estrutura: 001 (banco) + 9 (moeda) + DV + resto
        String codigoSemDV = codigoBarras.substring(0, 4) + codigoBarras.substring(5);
        
        // Calcula o DV esperado
        int dvCalculado = calcularDVFEBRABAN(codigoSemDV);

        return dvCodigo == dvCalculado;
    }

    /**
     * Calcula o dígito verificador FEBRABAN
     * @param codigo Código sem o DV (43 dígitos)
     * @return Dígito verificador calculado
     */
    public static int calcularDVFEBRABAN(String codigo) {
        int soma = 0;
        int multiplicador = 2;

        for (int i = codigo.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(codigo.charAt(i));
            soma += digito * multiplicador;

            multiplicador++;
            if (multiplicador > 9) {
                multiplicador = 2;
            }
        }

        int resto = soma % 11;
        int dv = 11 - resto;

        if (dv == 0 || dv == 10 || dv == 11) {
            dv = 1;
        }

        return dv;
    }

    /**
     * Valida o CRC16 do payload EMV do PIX
     * @param payload Payload completo do QR Code PIX
     * @return true se o CRC16 for válido
     */
    public static boolean validarCRC16EMV(String payload) {
        if (payload == null || payload.length() < 8) {
            return false;
        }

        // Extrai o CRC16 do payload (últimos 4 caracteres após "6304")
        int indexCRC = payload.lastIndexOf("6304");
        if (indexCRC == -1 || indexCRC + 8 > payload.length()) {
            return false;
        }

        String crc16Codigo = payload.substring(indexCRC + 4, indexCRC + 8);
        
        // Reconstrói o payload sem o CRC16 para recalcular
        String payloadSemCRC = payload.substring(0, indexCRC + 4);
        
        // Calcula o CRC16 esperado
        String crc16Calculado = calcularCRC16(payloadSemCRC);

        return crc16Codigo.equalsIgnoreCase(crc16Calculado);
    }

    /**
     * Calcula o CRC16-CCITT (polinômio 0x8408) do payload
     * @param payload Payload sem o CRC16
     * @return CRC16 calculado em hexadecimal (4 dígitos)
     */
    public static String calcularCRC16(String payload) {
        int crc = 0xFFFF;
        byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);

        for (byte b : bytes) {
            crc ^= (b & 0xFF);
            for (int i = 0; i < 8; i++) {
                if ((crc & 1) != 0) {
                    crc = (crc >>> 1) ^ 0x8408;
                } else {
                    crc = crc >>> 1;
                }
            }
        }

        crc = (~crc) & 0xFFFF;
        return String.format("%04X", crc);
    }
}
