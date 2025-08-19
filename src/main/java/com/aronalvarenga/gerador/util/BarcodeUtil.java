package com.aronalvarenga.gerador.util;

import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import java.awt.image.BufferedImage;

/**
 * Utilitário para geração de códigos de barras usando Barcode4J
 */
public class BarcodeUtil {

    /**
     * Gera uma imagem de código de barras Code128
     */
    public static BufferedImage gerarCodigoBarrasImagem(String codigo) {
        try {
            Code128Bean barcodeGenerator = new Code128Bean();

            barcodeGenerator.setModuleWidth(0.6);
            barcodeGenerator.setBarHeight(12.0);
            barcodeGenerator.setFontSize(2.0);
            barcodeGenerator.setQuietZone(2.0);
            barcodeGenerator.doQuietZone(true);

            BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                    200,
                    BufferedImage.TYPE_BYTE_GRAY,
                    false,
                    0
            );

            barcodeGenerator.generateBarcode(canvas, codigo);
            canvas.finish();

            return canvas.getBufferedImage();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar código de barras: " + e.getMessage(), e);
        }
    }

    /**
     * Gera uma imagem de código de barras com dimensões customizadas
     */
    public static BufferedImage gerarCodigoBarrasImagem(String codigo, int dpi, double altura, double larguraModulo) {
        try {
            Code128Bean barcodeGenerator = new Code128Bean();

            barcodeGenerator.setModuleWidth(larguraModulo);
            barcodeGenerator.setBarHeight(altura);
            barcodeGenerator.setFontSize(2.0);
            barcodeGenerator.setQuietZone(2.0);
            barcodeGenerator.doQuietZone(true);

            BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                    dpi,
                    BufferedImage.TYPE_BYTE_GRAY,
                    false,
                    0
            );

            barcodeGenerator.generateBarcode(canvas, codigo);
            canvas.finish();

            return canvas.getBufferedImage();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar código de barras customizado: " + e.getMessage(), e);
        }
    }
}