package com.aronalvarenga.gerador.service;

import com.aronalvarenga.gerador.model.GuiaPagamento;
import com.aronalvarenga.gerador.util.BarcodeUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class PdfService {

    public void gerarPdf(GuiaPagamento guia, String caminhoArquivo) throws DocumentException, IOException, WriterException {
        Document document = new Document(PageSize.A4, 30, 30, 30, 30);
        PdfWriter.getInstance(document, new FileOutputStream(caminhoArquivo));
        document.open();

        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph titulo = new Paragraph("GUIA DE PAGAMENTO", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        PdfPTable cabecalho = new PdfPTable(2);
        cabecalho.setWidthPercentage(100);
        cabecalho.setWidths(new float[]{1, 1});

        PdfPCell cellNumero = new PdfPCell(new Phrase("Nº da Guia: " + guia.getNumeroGuia()));
        cellNumero.setBorder(Rectangle.NO_BORDER);
        cabecalho.addCell(cellNumero);

        PdfPCell cellVencimento = new PdfPCell(new Phrase("Vencimento: " +
                guia.getVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        cellVencimento.setBorder(Rectangle.NO_BORDER);
        cellVencimento.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cabecalho.addCell(cellVencimento);

        document.add(cabecalho);
        document.add(new Paragraph(" "));

        document.add(new Paragraph("DADOS DO PROPRIETÁRIO", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        document.add(new Paragraph("Nome: " + guia.getNomeProprietario()));
        document.add(new Paragraph("CPF: " + guia.getCpf()));
        document.add(new Paragraph("Endereço: " + guia.getEndereco()));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("DADOS DO PAGAMENTO", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        document.add(new Paragraph("Descrição: " + guia.getDescricao()));

        DecimalFormat df = new DecimalFormat("#,##0.00");
        document.add(new Paragraph("Valor: R$ " + df.format(guia.getValor()),
                new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("CÓDIGO DE BARRAS", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

        BufferedImage barcodeImage = BarcodeUtil.gerarCodigoBarrasImagem(guia.getCodigoBarras(), 200, 15.0, 0.8);
        Image barcodePdf = Image.getInstance(barcodeImage, null);

        float scaler = (document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - 50) / barcodePdf.getWidth();
        barcodePdf.scalePercent(scaler * 100);
        barcodePdf.setAlignment(Image.ALIGN_CENTER);
        document.add(barcodePdf);

        document.add(new Paragraph(guia.getCodigoBarras(),
                new Font(Font.FontFamily.COURIER, 10, Font.NORMAL)));
        document.add(new Paragraph(" "));

        document.add(new Paragraph(guia.getCodigoBarras(),
                new Font(Font.FontFamily.COURIER, 10, Font.NORMAL)));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("PIX - QR CODE", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        document.add(new Paragraph("Chave PIX: " + guia.getChavePix()));

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(guia.getQrCodePix(), BarcodeFormat.QR_CODE, 150, 150);
        BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        Image qrCodePdf = Image.getInstance(qrCodeImage, null);

        qrCodePdf.scaleAbsolute(150, 150);
        qrCodePdf.setAlignment(Image.ALIGN_CENTER);
        document.add(qrCodePdf);

        document.close();
    }
}