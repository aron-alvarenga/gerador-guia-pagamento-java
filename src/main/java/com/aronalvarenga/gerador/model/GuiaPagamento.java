package com.aronalvarenga.gerador.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GuiaPagamento {

    private String nomeProprietario;
    private String cpf;
    private String endereco;
    private String chavePix;
    private BigDecimal valor;
    private String descricao;
    private LocalDate vencimento;
    private String numeroGuia;
    private String codigoBarras;
    private String qrCodePix;

    public GuiaPagamento() {
    }

    public GuiaPagamento(String nomeProprietario, String cpf, String endereco,
                         String chavePix, BigDecimal valor, String descricao,
                         LocalDate vencimento, String numeroGuia) {
        this.nomeProprietario = nomeProprietario;
        this.cpf = cpf;
        this.endereco = endereco;
        this.chavePix = chavePix;
        this.valor = valor;
        this.descricao = descricao;
        this.vencimento = vencimento;
        this.numeroGuia = numeroGuia;
    }

    public String getNomeProprietario() {
        return nomeProprietario;
    }

    public void setNomeProprietario(String nomeProprietario) {
        this.nomeProprietario = nomeProprietario;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getChavePix() {
        return chavePix;
    }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }

    public String getNumeroGuia() {
        return numeroGuia;
    }

    public void setNumeroGuia(String numeroGuia) {
        this.numeroGuia = numeroGuia;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getQrCodePix() {
        return qrCodePix;
    }

    public void setQrCodePix(String qrCodePix) {
        this.qrCodePix = qrCodePix;
    }

    @Override
    public String toString() {
        return "GuiaPagamento{" +
                "nomeProprietario='" + nomeProprietario + '\'' +
                ", cpf='" + cpf + '\'' +
                ", endereco='" + endereco + '\'' +
                ", chavePix='" + chavePix + '\'' +
                ", valor=" + valor +
                ", descricao='" + descricao + '\'' +
                ", vencimento=" + vencimento +
                ", numeroGuia='" + numeroGuia + '\'' +
                '}';
    }
}