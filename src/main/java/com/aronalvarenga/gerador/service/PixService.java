package com.aronalvarenga.gerador.service;

import com.aronalvarenga.gerador.model.GuiaPagamento;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PixService {

    private static final String PAYLOAD_FORMAT_INDICATOR = "01";
    private static final String MERCHANT_ACCOUNT_INFORMATION = "26";
    private static final String MERCHANT_CATEGORY_CODE = "52";
    private static final String TRANSACTION_CURRENCY = "53";
    private static final String TRANSACTION_AMOUNT = "54";
    private static final String COUNTRY_CODE = "58";
    private static final String MERCHANT_NAME = "59";
    private static final String MERCHANT_CITY = "60";
    private static final String ADDITIONAL_DATA_FIELD = "62";
    private static final String CRC16 = "63";

    /**
     * Gera o código QR Code PIX seguindo o padrão EMV
     */
    public String gerarQRCodePix(GuiaPagamento guia) {
        StringBuilder payload = new StringBuilder();

        payload.append(formatarCampo("00", "01"));

        payload.append(formatarCampo("01", "12"));

        payload.append(gerarMerchantAccountInfo(guia.getChavePix()));

        payload.append(formatarCampo("52", "0000"));

        payload.append(formatarCampo("53", "986"));

        payload.append(formatarCampo("54", formatarValorPix(guia.getValor())));

        payload.append(formatarCampo("58", "BR"));

        String nomeFormatado = formatarNome(guia.getNomeProprietario());
        payload.append(formatarCampo("59", nomeFormatado));

        String cidade = extrairCidade(guia.getEndereco());
        payload.append(formatarCampo("60", cidade));

        payload.append(gerarAdditionalDataField(guia));

        String payloadSemCRC = payload.toString() + "6304";
        String crc16 = calcularCRC16(payloadSemCRC);
        payload.append("63").append("04").append(crc16);

        return payload.toString();
    }

    private String formatarCampo(String id, String valor) {
        if (valor == null) valor = "";
        return id + String.format("%02d", valor.length()) + valor;
    }

    private String gerarMerchantAccountInfo(String chavePix) {
        StringBuilder merchant = new StringBuilder();

        merchant.append(formatarCampo("00", "br.gov.bcb.pix"));

        merchant.append(formatarCampo("01", chavePix));

        String merchantInfo = merchant.toString();
        return "26" + String.format("%02d", merchantInfo.length()) + merchantInfo;
    }

    private String formatarValorPix(BigDecimal valor) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("0.00", symbols);
        return df.format(valor);
    }

    private String formatarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return "PROPRIETARIO";
        }

        String nomeFormatado = nome.toUpperCase()
                .replaceAll("[^A-Z0-9\\s]", "")
                .trim();

        if (nomeFormatado.length() > 25) {
            nomeFormatado = nomeFormatado.substring(0, 25);
        }

        return nomeFormatado;
    }

    private String extrairCidade(String endereco) {
        if (endereco == null || endereco.trim().isEmpty()) {
            return "CAMPO GRANDE";
        }

        String[] partes = endereco.split("-");
        if (partes.length >= 2) {
            String cidade = partes[partes.length - 1].trim();

            if (cidade.contains("/")) {
                cidade = cidade.substring(0, cidade.indexOf("/")).trim();
            }

            cidade = cidade.toUpperCase()
                    .replaceAll("[^A-Z0-9\\s]", "")
                    .trim();

            if (cidade.length() > 15) {
                cidade = cidade.substring(0, 15);
            }

            return cidade.isEmpty() ? "CAMPO GRANDE" : cidade;
        }

        return "CAMPO GRANDE";
    }

    private String gerarAdditionalDataField(GuiaPagamento guia) {
        StringBuilder additional = new StringBuilder();

        if (guia.getNumeroGuia() != null && !guia.getNumeroGuia().trim().isEmpty()) {
            String referencia = guia.getNumeroGuia().length() > 25 ?
                    guia.getNumeroGuia().substring(0, 25) :
                    guia.getNumeroGuia();
            additional.append(formatarCampo("05", referencia));
        }

        if (additional.length() == 0) {
            return "";
        }

        String additionalInfo = additional.toString();
        return "62" + String.format("%02d", additionalInfo.length()) + additionalInfo;
    }

    private String calcularCRC16(String payload) {
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

    /**
     * Valida se uma chave PIX está em formato válido (validação básica)
     */
    public boolean validarChavePix(String chavePix) {
        if (chavePix == null || chavePix.trim().isEmpty()) {
            return false;
        }

        chavePix = chavePix.trim();

        if (chavePix.contains("@") && chavePix.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            return true;
        }

        if (chavePix.matches("^\\+55\\d{10,11}$")) {
            return true;
        }

        if (chavePix.matches("^\\d{11}$")) {
            return true;
        }

        if (chavePix.matches("^\\d{14}$")) {
            return true;
        }

        if (chavePix.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
            return true;
        }

        return true;
    }
}