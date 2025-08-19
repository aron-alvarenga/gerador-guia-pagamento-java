package com.aronalvarenga.gerador;

import com.aronalvarenga.gerador.model.GuiaPagamento;
import com.aronalvarenga.gerador.service.CodigoBarrasService;
import com.aronalvarenga.gerador.service.PdfService;
import com.aronalvarenga.gerador.service.PixService;
import com.aronalvarenga.gerador.ui.GuiaPagamentoPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class GeradorGuiaPagamentoApp extends JFrame {

    private JTextField nomeProprietarioField;
    private JTextField cpfField;
    private JTextField enderecoField;
    private JTextField chavePIXField;
    private JTextField valorField;
    private JTextField descricaoField;
    private JTextField vencimentoField;
    private JTextField numeroGuiaField;

    private GuiaPagamentoPanel guiaPanel;

    public GeradorGuiaPagamentoApp() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeComponents() {
        setTitle("Gerador de Guia de Pagamento - Aron Alvarenga");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        nomeProprietarioField = new JTextField(20);
        cpfField = new JTextField(15);
        enderecoField = new JTextField(30);
        chavePIXField = new JTextField(25);
        valorField = new JTextField(10);
        descricaoField = new JTextField(30);
        vencimentoField = new JTextField(10);
        numeroGuiaField = new JTextField(15);

        vencimentoField.setToolTipText("Formato: dd/MM/yyyy (ex: 28/02/2025)");
        valorField.setToolTipText("Formato: 150,50 ou 150.50");
        chavePIXField.setToolTipText("E-mail, telefone, CPF, CNPJ ou chave aleatória");

        nomeProprietarioField.setText("João da Silva Santos");
        cpfField.setText("123.456.789-01");
        enderecoField.setText("Rua das Flores, 123 - Centro - Campo Grande/MS");
        chavePIXField.setText("joao.silva@email.com");
        valorField.setText("150,50");
        descricaoField.setText("Pagamento de Taxa de Condomínio - Janeiro 2025");
        vencimentoField.setText("28/02/2025");
        numeroGuiaField.setText("2025010001");

        guiaPanel = new GuiaPagamentoPanel();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(guiaPanel);
        scrollPane.setPreferredSize(new Dimension(850, 400));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Dados da Guia de Pagamento"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nome do Proprietário:"), gbc);
        gbc.gridx = 1;
        panel.add(nomeProprietarioField, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 3;
        panel.add(cpfField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Endereço:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        panel.add(enderecoField, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Chave PIX:"), gbc);
        gbc.gridx = 1;
        panel.add(chavePIXField, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Valor (R$):"), gbc);
        gbc.gridx = 3;
        panel.add(valorField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        panel.add(descricaoField, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Vencimento:"), gbc);
        gbc.gridx = 1;
        panel.add(vencimentoField, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Nº da Guia:"), gbc);
        gbc.gridx = 3;
        panel.add(numeroGuiaField, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton gerarButton = new JButton("Gerar Guia de Pagamento");
        gerarButton.setPreferredSize(new Dimension(200, 35));
        gerarButton.addActionListener(new GerarGuiaListener());

        JButton limparButton = new JButton("Limpar");
        limparButton.addActionListener(e -> limparCampos());

        JButton baixarPdfButton = new JButton("Baixar PDF");
        baixarPdfButton.addActionListener(new BaixarPdfListener());

        panel.add(gerarButton);
        panel.add(limparButton);
        panel.add(baixarPdfButton);

        return panel;
    }

    private void setupEventListeners() {}

    private void limparCampos() {
        nomeProprietarioField.setText("");
        cpfField.setText("");
        enderecoField.setText("");
        chavePIXField.setText("");
        valorField.setText("");
        descricaoField.setText("");
        vencimentoField.setText("");
        numeroGuiaField.setText("");
        guiaPanel.limpar();
    }

    private class GerarGuiaListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                GuiaPagamento guia = criarGuiaPagamento();
                guiaPanel.exibirGuia(guia);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        GeradorGuiaPagamentoApp.this,
                        "Erro ao gerar guia: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private GuiaPagamento criarGuiaPagamento() throws Exception {
        if (nomeProprietarioField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do proprietário é obrigatório");
        }

        if (valorField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Valor é obrigatório");
        }

        BigDecimal valor;
        try {
            String valorTexto = valorField.getText().replace(",", ".");
            valor = new BigDecimal(valorTexto);
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Valor deve ser maior que zero");
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Valor inválido");
        }

        LocalDate vencimento;
        try {
            vencimento = LocalDate.parse(vencimentoField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Data de vencimento inválida. Use o formato dd/MM/yyyy");
        }

        GuiaPagamento guia = new GuiaPagamento();
        guia.setNomeProprietario(nomeProprietarioField.getText().trim());
        guia.setCpf(cpfField.getText().trim());
        guia.setEndereco(enderecoField.getText().trim());
        guia.setChavePix(chavePIXField.getText().trim());
        guia.setValor(valor);
        guia.setDescricao(descricaoField.getText().trim());
        guia.setVencimento(vencimento);
        guia.setNumeroGuia(numeroGuiaField.getText().trim());

        CodigoBarrasService codigoBarrasService = new CodigoBarrasService();
        PixService pixService = new PixService();

        String codigoBarras = codigoBarrasService.gerarCodigoBarras(guia);
        String qrCodePix = pixService.gerarQRCodePix(guia);

        guia.setCodigoBarras(codigoBarras);
        guia.setQrCodePix(qrCodePix);

        return guia;
    }

    private class BaixarPdfListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (guiaPanel.getGuia() == null) {
                JOptionPane.showMessageDialog(
                        GeradorGuiaPagamentoApp.this,
                        "Gere uma guia antes de baixar o PDF",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvar Guia de Pagamento");
            fileChooser.setSelectedFile(new File("GuiaPagamento.pdf"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Documento PDF (*.pdf)", "pdf"));

            int userSelection = fileChooser.showSaveDialog(GeradorGuiaPagamentoApp.this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                try {
                    PdfService pdfService = new PdfService();
                    pdfService.gerarPdf(guiaPanel.getGuia(), filePath);

                    JOptionPane.showMessageDialog(
                            GeradorGuiaPagamentoApp.this,
                            "Guia salva com sucesso em:\n" + filePath,
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            GeradorGuiaPagamentoApp.this,
                            "Erro ao gerar PDF: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ex) {
                System.err.println("Usando Look and Feel padrão");
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GeradorGuiaPagamentoApp().setVisible(true);
            }
        });
    }
}