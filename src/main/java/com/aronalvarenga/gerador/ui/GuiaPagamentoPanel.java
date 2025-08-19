package com.aronalvarenga.gerador.ui;

import com.aronalvarenga.gerador.model.GuiaPagamento;
import com.aronalvarenga.gerador.service.CodigoBarrasService;
import com.aronalvarenga.gerador.util.BarcodeUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class GuiaPagamentoPanel extends JPanel {

    private GuiaPagamento guia;
    private final CodigoBarrasService codigoBarrasService;

    public GuiaPagamentoPanel() {
        this.codigoBarrasService = new CodigoBarrasService();
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
    }

    public void exibirGuia(GuiaPagamento guia) {
        this.guia = guia;
        repaint();
    }

    public void limpar() {
        this.guia = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (guia == null) {
            desenharMensagemVazia(g);
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        try {
            desenharGuiaPagamento(g2d);
        } catch (Exception e) {
            desenharErro(g2d, e.getMessage());
        } finally {
            g2d.dispose();
        }
    }

    private void desenharMensagemVazia(Graphics g) {
        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.ITALIC, 16));
        FontMetrics fm = g.getFontMetrics();
        String mensagem = "Preencha os campos e clique em 'Gerar Guia de Pagamento' para visualizar";
        int x = (getWidth() - fm.stringWidth(mensagem)) / 2;
        int y = getHeight() / 2;
        g.drawString(mensagem, x, y);
    }

    private void desenharErro(Graphics2D g2d, String mensagem) {
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth("Erro: " + mensagem)) / 2;
        int y = getHeight() / 2;
        g2d.drawString("Erro: " + mensagem, x, y);
    }

    private void desenharGuiaPagamento(Graphics2D g2d) throws WriterException {
        int margemX = 30;
        int y = 30;
        int larguraUtil = getWidth() - (2 * margemX);

        y = desenharCabecalho(g2d, margemX, y, larguraUtil);
        y += 20;

        y = desenharDadosProprietario(g2d, margemX, y, larguraUtil);
        y += 20;

        y = desenharDadosPagamento(g2d, margemX, y, larguraUtil);
        y += 30;

        y = desenharCodigoBarras(g2d, margemX, y, larguraUtil);
        y += 30;

        desenharQRCodePix(g2d, margemX, y, larguraUtil);
    }

    private int desenharCabecalho(Graphics2D g2d, int x, int y, int largura) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        String titulo = "GUIA DE PAGAMENTO";
        FontMetrics fm = g2d.getFontMetrics();
        int tituloX = x + (largura - fm.stringWidth(titulo)) / 2;
        g2d.drawString(titulo, tituloX, y);
        y += 30;

        g2d.setColor(Color.DARK_GRAY);
        g2d.drawLine(x, y, x + largura, y);
        y += 15;

        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Nº da Guia: " + guia.getNumeroGuia(), x, y);

        String vencimento = "Vencimento: " + guia.getVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        fm = g2d.getFontMetrics();
        int vencimentoX = x + largura - fm.stringWidth(vencimento);
        g2d.drawString(vencimento, vencimentoX, y);
        y += 20;

        return y;
    }

    private int desenharDadosProprietario(Graphics2D g2d, int x, int y, int largura) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("DADOS DO PROPRIETÁRIO", x, y);
        y += 20;

        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Nome: " + guia.getNomeProprietario(), x, y);
        y += 18;

        g2d.drawString("CPF: " + (guia.getCpf() != null ? guia.getCpf() : ""), x, y);
        y += 18;

        g2d.drawString("Endereço: " + (guia.getEndereco() != null ? guia.getEndereco() : ""), x, y);
        y += 18;

        return y;
    }

    private int desenharDadosPagamento(Graphics2D g2d, int x, int y, int largura) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("DADOS DO PAGAMENTO", x, y);
        y += 20;

        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Descrição: " + (guia.getDescricao() != null ? guia.getDescricao() : ""), x, y);
        y += 18;

        DecimalFormat df = new DecimalFormat("#,##0.00");
        String valorFormatado = "R$ " + df.format(guia.getValor());
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.BLUE);
        FontMetrics fm = g2d.getFontMetrics();
        int valorX = x + largura - fm.stringWidth(valorFormatado);
        g2d.drawString(valorFormatado, valorX, y);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("VALOR:", x, y);
        y += 25;

        return y;
    }

    private int desenharCodigoBarras(Graphics2D g2d, int x, int y, int largura) throws WriterException {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("CÓDIGO DE BARRAS", x, y);
        y += 20;

        try {
            int barcodeWidth = (int) (largura * 0.8);

            BufferedImage barcodeImage = BarcodeUtil.gerarCodigoBarrasImagem(
                    guia.getCodigoBarras(),
                    300,
                    12.0,
                    0.7
            );

            if (barcodeImage.getWidth() > barcodeWidth) {
                double scale = (double) barcodeWidth / barcodeImage.getWidth();
                int newWidth = barcodeWidth;
                int newHeight = (int) (barcodeImage.getHeight() * scale);

                BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D scaledG2d = scaledImage.createGraphics();
                scaledG2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                scaledG2d.drawImage(barcodeImage, 0, 0, newWidth, newHeight, null);
                scaledG2d.dispose();
                barcodeImage = scaledImage;
            }

            int barcodeX = x + (largura - barcodeImage.getWidth()) / 2;
            g2d.drawImage(barcodeImage, barcodeX, y, null);
            y += barcodeImage.getHeight() + 10;

        } catch (Exception e) {
            int errorHeight = 60;
            int errorWidth = largura - 100;

            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(x + 50, y, errorWidth, errorHeight);
            g2d.setColor(Color.RED);
            g2d.drawRect(x + 50, y, errorWidth, errorHeight);

            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            String errorMsg = "Erro na geração do código de barras";
            FontMetrics fm = g2d.getFontMetrics();
            int errorX = x + 50 + (errorWidth - fm.stringWidth(errorMsg)) / 2;
            g2d.drawString(errorMsg, errorX, y + 25);

            String codigoExibir = guia.getCodigoBarras();
            if (codigoExibir.length() > 50) {
                codigoExibir = codigoExibir.substring(0, 47) + "...";
            }
            g2d.drawString("Código: " + codigoExibir, x + 55, y + 45);
            y += errorHeight + 10;
        }

        String codigoFormatado = codigoBarrasService.formatarCodigoBarrasParaExibicao(guia.getCodigoBarras());
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Courier New", Font.PLAIN, 10));
        FontMetrics fm = g2d.getFontMetrics();

        if (fm.stringWidth(codigoFormatado) > largura) {
            int caracteresLinha = largura / fm.charWidth('0');

            for (int i = 0; i < codigoFormatado.length(); i += caracteresLinha) {
                int fim = Math.min(i + caracteresLinha, codigoFormatado.length());
                String linha = codigoFormatado.substring(i, fim);
                int codigoX = x + (largura - fm.stringWidth(linha)) / 2;
                g2d.drawString(linha, codigoX, y);
                y += 15;
            }
        } else {
            int codigoX = x + (largura - fm.stringWidth(codigoFormatado)) / 2;
            g2d.drawString(codigoFormatado, codigoX, y);
            y += 20;
        }

        return y;
    }

    private int desenharQRCodePix(Graphics2D g2d, int x, int y, int largura) throws WriterException {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("PIX - QR CODE", x, y);

        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        String chavePix = guia.getChavePix() != null ? guia.getChavePix() : "";
        g2d.drawString("Chave PIX: " + chavePix, x, y + 20);
        y += 40;

        int qrSize = Math.min(160, (getHeight() - y - 50) / 2);
        qrSize = Math.max(qrSize, 120);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(guia.getQrCodePix(), BarcodeFormat.QR_CODE, qrSize, qrSize);
        BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        int espacoInstrucoes = largura - qrSize - 40;

        boolean instrucaoAbaixo = espacoInstrucoes < 250;

        if (instrucaoAbaixo) {
            int qrX = x + (largura - qrSize) / 2;
            g2d.drawImage(qrCodeImage, qrX, y, null);
            y += qrSize + 20;

            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("Como pagar com PIX:", x, y);
            y += 20;

            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            String[] instrucoes = {
                    "1. Abra o aplicativo do seu banco",
                    "2. Selecione a opção PIX",
                    "3. Escolha 'Ler QR Code'",
                    "4. Aponte a câmera para o código acima",
                    "5. Confirme os dados e efetue o pagamento"
            };

            for (String instrucao : instrucoes) {
                g2d.drawString(instrucao, x, y);
                y += 15;
            }

            return y + 20;

        } else {
            g2d.drawImage(qrCodeImage, x, y, null);

            int instrucaoX = x + qrSize + 30;
            int instrucaoY = y + 20;

            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("Como pagar com PIX:", instrucaoX, instrucaoY);
            instrucaoY += 20;

            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            String[] instrucoes = {
                    "1. Abra o aplicativo do seu banco",
                    "2. Selecione a opção PIX",
                    "3. Escolha 'Ler QR Code'",
                    "4. Aponte a câmera para este código",
                    "5. Confirme os dados e efetue o pagamento"
            };

            for (String instrucao : instrucoes) {
                g2d.drawString(instrucao, instrucaoX, instrucaoY);
                instrucaoY += 15;
            }

            return y + qrSize + 20;
        }
    }

    public GuiaPagamento getGuia() {
        return this.guia;
    }
}