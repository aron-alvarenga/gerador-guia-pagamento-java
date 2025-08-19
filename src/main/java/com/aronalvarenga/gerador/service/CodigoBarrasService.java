package com.aronalvarenga.gerador.service;

import com.aronalvarenga.gerador.model.GuiaPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CodigoBarrasService {

    private static final String BANCO_CODIGO = "001";
    private static final String MOEDA_CODIGO = "9";
    private static final LocalDate DATA_BASE = LocalDate.of(1997, 10, 7);

    /**
     * Gera um código de barras válido baseado no padrão FEBRABAN
     * Para fins de demonstração, usa uma estrutura simplificada mas válida
     */
    public String gerarCodigoBarras(GuiaPagamento guia) {

        StringBuilder codigo = new StringBuilder();

        codigo.append(BANCO_CODIGO);

        codigo.append(MOEDA_CODIGO);

        String fatorVencimento = calcularFatorVencimento(guia.getVencimento());

        String valorFormatado = formatarValor(guia.getValor());

        String campoLivre = gerarCampoLivre(guia);

        String codigoSemDV = BANCO_CODIGO + MOEDA_CODIGO + fatorVencimento + valorFormatado + campoLivre;

        int digitoVerificador = calcularDigitoVerificador(codigoSemDV);

        codigo.append(BANCO_CODIGO)
                .append(MOEDA_CODIGO)
                .append(digitoVerificador)
                .append(fatorVencimento)
                .append(valorFormatado)
                .append(campoLivre);

        return codigo.toString();
    }

    private String calcularFatorVencimento(LocalDate vencimento) {
        long dias = ChronoUnit.DAYS.between(DATA_BASE, vencimento);
        int fator = (int) (dias % 10000);
        return String.format("%04d", fator);
    }

    private String formatarValor(BigDecimal valor) {
        long centavos = valor.multiply(new BigDecimal("100")).longValue();
        return String.format("%010d", centavos);
    }

    private String gerarCampoLivre(GuiaPagamento guia) {
        StringBuilder campo = new StringBuilder();

        String numeroGuia = guia.getNumeroGuia().replaceAll("\\D", "");
        if (numeroGuia.length() > 10) {
            numeroGuia = numeroGuia.substring(0, 10);
        } else {
            numeroGuia = String.format("%-10s", numeroGuia).replace(' ', '0');
        }
        campo.append(numeroGuia);

        campo.append("00001");

        String cpfNumeros = guia.getCpf().replaceAll("\\D", "");
        if (cpfNumeros.length() >= 10) {
            campo.append(cpfNumeros.substring(0, 10));
        } else {
            String sequencia = cpfNumeros + "0000000000";
            campo.append(sequencia.substring(0, 10));
        }

        return campo.toString();
    }

    private int calcularDigitoVerificador(String codigo) {
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
     * Formata o código de barras para exibição com espaçamentos
     */
    public String formatarCodigoBarrasParaExibicao(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.length() != 44) {
            return codigoBarras;
        }

        return codigoBarras.substring(0, 5) + "." +
                codigoBarras.substring(5, 10) + " " +
                codigoBarras.substring(10, 15) + "." +
                codigoBarras.substring(15, 21) + " " +
                codigoBarras.substring(21, 26) + "." +
                codigoBarras.substring(26, 32) + " " +
                codigoBarras.substring(32, 33) + " " +
                codigoBarras.substring(33);
    }
}