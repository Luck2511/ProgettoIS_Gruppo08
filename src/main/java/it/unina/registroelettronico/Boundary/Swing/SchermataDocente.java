package it.unina.registroelettronico.Boundary.Swing;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import it.unina.registroelettronico.Controller.ControllerClassi;
import it.unina.registroelettronico.Controller.Exceptions.ClasseEsistenteException;
import it.unina.registroelettronico.DTO.DocenteDTO;
import it.unina.registroelettronico.DTO.InfoClasseDTO;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SchermataDocente {

    private DocenteDTO docente;

    private JPanel contentPane;
    private JLabel lblTitolo;
    private JLabel lblSottotitolo;
    private JButton btnCreaClasse;
    private JLabel lblClassi;
    private JLabel lblHint;
    private JScrollPane scrollClassi;
    private JPanel pnlClassi;

    private final List<InfoClasseDTO> classi = new ArrayList<>();

    private final Color bluPrimario = new Color(47, 128, 237);
    private final Color bluHover = new Color(35, 111, 209);
    private final Color bluScuro = new Color(25, 59, 99);
    private final Color bordoChiaro = new Color(214, 228, 244);
    private final Color sfondoAzzurro = new Color(238, 246, 255);

    private final Color[][] paletteCard = new Color[][]{
            {new Color(242, 248, 255), new Color(47, 128, 237), new Color(224, 239, 255)},
            {new Color(248, 245, 255), new Color(111, 78, 225), new Color(235, 228, 255)},
            {new Color(241, 251, 247), new Color(32, 174, 139), new Color(221, 246, 237)},
            {new Color(255, 248, 239), new Color(238, 157, 57), new Color(255, 235, 208)}
    };

    public SchermataDocente(DocenteDTO docente) {

        this.docente=docente;
        configuraIntestazioneConIndietro();

        scrollClassi.setBorder(null);
        scrollClassi.getVerticalScrollBar().setUnitIncrement(18);
        scrollClassi.getViewport().setOpaque(true);
        scrollClassi.getViewport().setBackground(sfondoAzzurro);
        scrollClassi.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // La griglia ricalcola il numero di colonne in base alla larghezza realmente disponibile.
        scrollClassi.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                aggiornaGrigliaClassi(false);
            }
        });

        btnCreaClasse.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCreaClasse.setFocusPainted(false);
        btnCreaClasse.setBorderPainted(false);
        btnCreaClasse.setContentAreaFilled(false);
        btnCreaClasse.setOpaque(false);
        btnCreaClasse.setUI(new RoundedFillButtonUI(bluPrimario, bluHover, 22));

        //btnCreaClasse.addActionListener(this::apriDialogCreazioneClasse);

        caricaClassi();
        btnCreaClasse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                apriDialogCreazioneClasse(e);
            }
        });
    }


    private void configuraIntestazioneConIndietro() {
        Container parent = lblTitolo.getParent();
        if (!(parent instanceof JPanel pannelloHeader)) {
            return;
        }

        pannelloHeader.remove(lblTitolo);
        pannelloHeader.remove(lblSottotitolo);

        JPanel testi = new JPanel();
        testi.setOpaque(false);
        testi.setLayout(new BoxLayout(testi, BoxLayout.Y_AXIS));
        lblTitolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSottotitolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        testi.add(lblTitolo);
        testi.add(Box.createRigidArea(new Dimension(0, 4)));
        testi.add(lblSottotitolo);

        // Il pulsante resta dentro l'header bianco ed è centrato verticalmente.
        JPanel sinistra = new JPanel(new GridBagLayout());
        sinistra.setOpaque(false);
        sinistra.setPreferredSize(new Dimension(40, 40));
        sinistra.add(Main.creaPulsanteIndietro());

        JPanel blocco = new JPanel(new BorderLayout(14, 0));
        blocco.setOpaque(false);
        blocco.add(sinistra, BorderLayout.WEST);
        blocco.add(testi, BorderLayout.CENTER);

        pannelloHeader.add(
                blocco,
                new GridConstraints(
                        0, 0, 2, 1,
                        GridConstraints.ANCHOR_WEST,
                        GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null, 0, false
                )
        );
    }

    private void caricaClassi() {

        ControllerClassi controllerClassi =
                ControllerClassi.getInstance();

        List<InfoClasseDTO> classiRecuperate =
                controllerClassi.recuperaClassiDocente(
                        docente.getIndirizzoEmailIstituzionale()
                );

        classi.clear();
        classi.addAll(classiRecuperate);

        aggiornaGrigliaClassi();
    }



    private void apriDialogCreazioneClasse(ActionEvent e) {
        Window owner = SwingUtilities.getWindowAncestor(contentPane);

        JDialog dialog = new JDialog(
                owner,
                "Crea classe",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        RoundedPanel wrapper = new RoundedPanel(Color.WHITE, 30);
        wrapper.setPreferredSize(new Dimension(460, 325));
        wrapper.setLayout(new BorderLayout());
        wrapper.setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundedBorder(bordoChiaro, 2, 30),
                        BorderFactory.createEmptyBorder(24, 26, 24, 26)
                )
        );

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblTitoloDialog = new JLabel("Crea una nuova classe");
        lblTitoloDialog.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitoloDialog.setForeground(bluScuro);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(lblTitoloDialog, gbc);

        JLabel lblDescrizione = new JLabel("Inserisci il nome della classe e conferma la creazione.");
        lblDescrizione.setFont(new Font("Arial", Font.PLAIN, 13));
        lblDescrizione.setForeground(new Color(105, 120, 140));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(lblDescrizione, gbc);

        JLabel lblNome = new JLabel("Nome classe");
        lblNome.setFont(new Font("Arial", Font.BOLD, 14));
        lblNome.setForeground(bluScuro);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(lblNome, gbc);

        JTextField txtNomeClasse = new JTextField();
        txtNomeClasse.setPreferredSize(new Dimension(350, 48));
        txtNomeClasse.setBorder(new RoundedBorder(bordoChiaro, 2, 20));
        txtNomeClasse.setOpaque(false);
        txtNomeClasse.setBackground(Color.WHITE);
        txtNomeClasse.setUI(new RoundedTextFieldUI(Color.WHITE, 20));
        txtNomeClasse.setMargin(new Insets(0, 12, 0, 12));
        txtNomeClasse.setFont(new Font("Arial", Font.PLAIN, 14));
        txtNomeClasse.setForeground(bluScuro);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(txtNomeClasse, gbc);

        JLabel lblErrore = new JLabel(" ");
        lblErrore.setForeground(new Color(200, 60, 60));
        lblErrore.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 4;
        gbc.insets = new Insets(2, 2, 16, 0);
        panel.add(lblErrore, gbc);

        JPanel pulsanti = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        pulsanti.setOpaque(false);

        JButton btnAnnulla = new JButton("Annulla");
        preparaPulsanteSecondario(btnAnnulla);

        JButton btnConferma = new JButton("Crea classe");
        preparaPulsantePrimario(btnConferma);

        pulsanti.add(btnAnnulla);
        pulsanti.add(btnConferma);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(pulsanti, gbc);

        wrapper.add(panel, BorderLayout.CENTER);
        dialog.setContentPane(wrapper);

        btnAnnulla.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annullaCreazioneClasse(dialog);
            }
        });

        btnConferma.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confermaCreazioneClasse(dialog, txtNomeClasse, lblErrore);
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(contentPane);
        dialog.setVisible(true);
    }

    private void annullaCreazioneClasse(JDialog dialog) {

        dialog.dispose();
    }

    private void confermaCreazioneClasse(JDialog dialog, JTextField txtNomeClasse, JLabel lblErrore) {
        String nomeClasse = txtNomeClasse.getText() == null
                ? ""
                : txtNomeClasse.getText().trim();

        if (nomeClasse.isEmpty()) {
            lblErrore.setText("Inserisci il nome della classe.");
            return;
        }

        // QUI VA RECUPERATO ControllerClassi TRAMITE getInstance() E CHIAMATO IL METODO PER CREARE LA CLASSE VIRTUALE.
        ControllerClassi controllerClassi=ControllerClassi.getInstance();

        InfoClasseDTO classeCreata=new InfoClasseDTO("","");
        boolean esitoCasoDUso=true;
        try{
            esitoCasoDUso=controllerClassi.creaClasse(nomeClasse,classeCreata,docente);
        }
        catch(ClasseEsistenteException e){
            lblErrore.setText("Nome già in uso per un'altra classe.");
            return;
        }

        if(!esitoCasoDUso){
            lblErrore.setText("Errore nella creazione della classe");
            return;
        }

        caricaClassi();
        dialog.dispose();
    }

    private void aggiornaGrigliaClassi() {
        aggiornaGrigliaClassi(true);
    }

    private void aggiornaGrigliaClassi(boolean riportaInAlto) {
        pnlClassi.removeAll();

        int larghezzaViewport = scrollClassi.getViewport().getExtentSize().width;
        if (larghezzaViewport <= 0) {
            larghezzaViewport = 690;
        }

        if (classi.isEmpty()) {
            pnlClassi.setLayout(new BorderLayout());
            pnlClassi.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            pnlClassi.setPreferredSize(new Dimension(larghezzaViewport, 300));
            pnlClassi.add(creaStatoVuoto(), BorderLayout.CENTER);
        } else {
            final int larghezzaMinimaCard = 300;
            final int altezzaCard = 166;
            final int spazio = 16;
            final int margineLaterale = 8;

            int larghezzaDisponibile = Math.max(1, larghezzaViewport - (margineLaterale * 2));
            int colonne = Math.max(1, (larghezzaDisponibile + spazio) / (larghezzaMinimaCard + spazio));
            colonne = Math.min(colonne, classi.size());

            // Le card si dividono tutta la larghezza disponibile: in questo modo
            // non rimane la fascia vuota a destra del riquadro delle classi.
            int larghezzaCard =
                    (larghezzaDisponibile - Math.max(0, colonne - 1) * spazio) / colonne;

            pnlClassi.setLayout(new GridBagLayout());
            pnlClassi.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            for (int i = 0; i < classi.size(); i++) {
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = i % colonne;
                gbc.gridy = i / colonne;
                gbc.anchor = GridBagConstraints.NORTHWEST;
                gbc.fill = GridBagConstraints.NONE;
                gbc.weightx = 0.0;
                gbc.weighty = 0.0;
                gbc.insets = new Insets(0, 0, spazio, gbc.gridx == colonne - 1 ? 0 : spazio);
                pnlClassi.add(creaCardClasse(classi.get(i), i, larghezzaCard), gbc);
            }

            int righe = (classi.size() + colonne - 1) / colonne;

            GridBagConstraints filler = new GridBagConstraints();
            filler.gridx = 0;
            filler.gridy = righe;
            filler.gridwidth = colonne;
            filler.weightx = 1.0;
            filler.weighty = 1.0;
            filler.fill = GridBagConstraints.BOTH;

            JPanel spacer = new JPanel();
            spacer.setOpaque(false);
            pnlClassi.add(spacer, filler);

            int altezza = (righe * altezzaCard) + Math.max(0, (righe - 1) * spazio) + 16;
            pnlClassi.setPreferredSize(new Dimension(larghezzaViewport, altezza));
        }

        Main.applicaRenderingTesto(pnlClassi);
        pnlClassi.revalidate();
        pnlClassi.repaint();

        if (riportaInAlto) {
            SwingUtilities.invokeLater(() ->
                    scrollClassi.getVerticalScrollBar().setValue(0)
            );
        }
    }

    private JComponent creaStatoVuoto() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        JLabel lbl = new JLabel(
                "<html><div style='text-align:center;'>"
                        + "<b><font size='+1' color='#193B63'>Nessuna classe creata</font></b>"
                        + "<br><br><font color='#667788'>"
                        + "Premi “Crea classe” per aggiungere la tua prima classe."
                        + "</font></div></html>",
                SwingConstants.CENTER
        );

        panel.add(lbl);
        return panel;
    }

    private JComponent creaCardClasse(InfoClasseDTO classe, int indice, int larghezzaCard) {
        Color[] palette = paletteCard[indice % paletteCard.length];
        Color sfondo = palette[0];
        Color accento = palette[1];
        Color sfondoPill = palette[2];

        JButton card = new JButton();
        card.setLayout(new BorderLayout(0, 10));
        card.setPreferredSize(new Dimension(larghezzaCard, 166));
        card.setMinimumSize(new Dimension(larghezzaCard, 166));
        card.setMaximumSize(new Dimension(larghezzaCard, 166));
        card.setForeground(bluScuro);
        card.setOpaque(false);
        card.setContentAreaFilled(false);
        card.setBorderPainted(false);
        card.setFocusPainted(false);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setUI(new ClassCardUI(sfondo, schiarisci(sfondo), accento, 26));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);

        JLabel lblNomeClasse = new JLabel(classe.getNome());
        lblNomeClasse.setFont(new Font("Arial", Font.BOLD, 18));
        lblNomeClasse.setForeground(bluScuro);

        JLabel lblIcona = new JLabel(new FlatSVGIcon("icons/classe.svg", 58, 58));
        lblIcona.setHorizontalAlignment(SwingConstants.RIGHT);

        top.add(lblNomeClasse, BorderLayout.CENTER);
        top.add(lblIcona, BorderLayout.EAST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel lblCodiceTitle = new JLabel("Codice classe");
        lblCodiceTitle.setFont(new Font("Arial", Font.BOLD, 12));
        lblCodiceTitle.setForeground(new Color(104, 120, 140));
        lblCodiceTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        PillLabel lblCodice = new PillLabel(classe.getCodice(), sfondoPill, accento);
        lblCodice.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel rigaCodice = new JPanel();
        rigaCodice.setOpaque(false);
        rigaCodice.setLayout(new BoxLayout(rigaCodice, BoxLayout.X_AXIS));
        rigaCodice.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnCopiaCodice = Main.creaPulsanteCopiaCodice(classe.getCodice());
        rigaCodice.add(lblCodice);
        rigaCodice.add(Box.createRigidArea(new Dimension(8, 0)));
        rigaCodice.add(btnCopiaCodice);
        rigaCodice.add(Box.createHorizontalGlue());

        info.add(lblCodiceTitle);
        info.add(Box.createRigidArea(new Dimension(0, 7)));
        info.add(rigaCodice);

        card.add(top, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);

        card.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                apriSchermataClasse(classe);
            }
        });

        return card;
    }

    private void apriSchermataClasse(InfoClasseDTO classe) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(contentPane);

        SchermataClasse schermataClasse = new SchermataClasse(classe);
        Main.apriSchermata(
                frame,
                getContentPane(),
                schermataClasse.getContentPane()
        );
    }

    private Color schiarisci(Color color) {
        int r = Math.min(255, color.getRed() + 5);
        int g = Math.min(255, color.getGreen() + 5);
        int b = Math.min(255, color.getBlue() + 5);
        return new Color(r, g, b);
    }

    private void preparaPulsanteSecondario(JButton button) {
        button.setPreferredSize(new Dimension(120, 44));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setForeground(bluPrimario);
        button.setBorder(new RoundedBorder(bluPrimario, 2, 20));
        button.setBorderPainted(true);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setUI(new RoundedFillButtonUI(Color.WHITE, new Color(244, 249, 255), 20));
    }

    private void preparaPulsantePrimario(JButton button) {
        button.setPreferredSize(new Dimension(145, 44));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setForeground(Color.WHITE);
        button.setUI(new RoundedFillButtonUI(bluPrimario, bluHover, 20));
    }


    public JPanel getContentPane() {
        Main.applicaRenderingTesto(contentPane);
        return contentPane;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(24, 28, 24, 28), 0, 18));
        contentPane.setBackground(new Color(-1116929));
        contentPane.setOpaque(true);
        contentPane.setPreferredSize(new Dimension(820, 610));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 2, new Insets(18, 22, 18, 22), 12, 6));
        panel1.setBackground(new Color(-1));
        panel1.setOpaque(true);
        contentPane.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        lblTitolo = new JLabel();
        Font lblTitoloFont = this.$$$getFont$$$("Arial", Font.BOLD, 28, lblTitolo.getFont());
        if (lblTitoloFont != null) lblTitolo.setFont(lblTitoloFont);
        lblTitolo.setForeground(new Color(-15123613));
        lblTitolo.setText("Area Docente");
        panel1.add(lblTitolo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblSottotitolo = new JLabel();
        Font lblSottotitoloFont = this.$$$getFont$$$(null, Font.PLAIN, 13, lblSottotitolo.getFont());
        if (lblSottotitoloFont != null) lblSottotitolo.setFont(lblSottotitoloFont);
        lblSottotitolo.setForeground(new Color(-10063271));
        lblSottotitolo.setText("Gestisci le tue classi e crea un nuovo spazio didattico.");
        panel1.add(lblSottotitolo, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnCreaClasse = new JButton();
        btnCreaClasse.setBackground(new Color(-13664019));
        btnCreaClasse.setFocusPainted(false);
        Font btnCreaClasseFont = this.$$$getFont$$$(null, Font.BOLD, 14, btnCreaClasse.getFont());
        if (btnCreaClasseFont != null) btnCreaClasse.setFont(btnCreaClasseFont);
        btnCreaClasse.setForeground(new Color(-1));
        btnCreaClasse.setPreferredSize(new Dimension(170, 44));
        btnCreaClasse.setText("+ Crea classe");
        panel1.add(btnCreaClasse, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 1, new Insets(18, 22, 18, 22), 0, 10));
        panel2.setBackground(new Color(-1));
        panel2.setOpaque(true);
        contentPane.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        lblClassi = new JLabel();
        Font lblClassiFont = this.$$$getFont$$$(null, Font.BOLD, 20, lblClassi.getFont());
        if (lblClassiFont != null) lblClassi.setFont(lblClassiFont);
        lblClassi.setForeground(new Color(-15123613));
        lblClassi.setText("Le tue classi");
        panel2.add(lblClassi, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblHint = new JLabel();
        Font lblHintFont = this.$$$getFont$$$(null, Font.PLAIN, 12, lblHint.getFont());
        if (lblHintFont != null) lblHint.setFont(lblHintFont);
        lblHint.setForeground(new Color(-10063271));
        lblHint.setText("Clicca su una classe per aprirla. Le nuove classi compariranno qui in due colonne.");
        panel2.add(lblHint, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrollClassi = new JScrollPane();
        scrollClassi.setBackground(new Color(-1116929));
        scrollClassi.setOpaque(true);
        scrollClassi.setPreferredSize(new Dimension(720, 360));
        panel2.add(scrollClassi, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollClassi.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        pnlClassi = new JPanel();
        pnlClassi.setLayout(new GridLayoutManager(1, 1, new Insets(6, 6, 6, 6), -1, -1));
        pnlClassi.setBackground(new Color(-1116929));
        pnlClassi.setOpaque(true);
        scrollClassi.setViewportView(pnlClassi);
        pnlClassi.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    /*
    private static class ClassData {
        private final String nomeClasse;
        private final String codiceClasse;

        private ClassData(String nomeClasse, String codiceClasse) {
            this.nomeClasse = nomeClasse;
            this.codiceClasse = codiceClasse;
        }
    }
    */


    private static class PillLabel extends JLabel {
        private final Color backgroundColor;
        private final Color foregroundColor;

        public PillLabel(String text, Color backgroundColor, Color foregroundColor) {
            super(text);
            this.backgroundColor = backgroundColor;
            this.foregroundColor = foregroundColor;
            setOpaque(false);
            setFont(new Font("Arial", Font.BOLD, 14));
            setForeground(foregroundColor);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedPanel extends JPanel {
        private final Color backgroundColor;
        private final int arc;

        public RoundedPanel(Color backgroundColor, int arc) {
            this.backgroundColor = backgroundColor;
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedTextFieldUI extends BasicTextFieldUI {
        private final Color backgroundColor;
        private final int arc;

        public RoundedTextFieldUI(Color backgroundColor, int arc) {
            this.backgroundColor = backgroundColor;
            this.arc = arc;
        }

        @Override
        protected void paintSafely(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(1, 1, getComponent().getWidth() - 2, getComponent().getHeight() - 2, arc, arc);
            g2.dispose();
            super.paintSafely(g);
        }
    }

    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int arc;

        public RoundedBorder(Color color, int thickness, int arc) {
            this.color = color;
            this.thickness = thickness;
            this.arc = arc;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            int offset = thickness / 2;
            g2.drawRoundRect(x + offset, y + offset, width - thickness, height - thickness, arc, arc);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 12);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = 8;
            insets.left = 12;
            insets.bottom = 8;
            insets.right = 12;
            return insets;
        }
    }

    private static class RoundedFillButtonUI extends BasicButtonUI {
        private final Color normalColor;
        private final Color hoverColor;
        private final int arc;

        public RoundedFillButtonUI(Color normalColor, Color hoverColor, int arc) {
            this.normalColor = normalColor;
            this.hoverColor = hoverColor;
            this.arc = arc;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton button = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color background = button.getModel().isRollover() ? hoverColor : normalColor;
            if (button.getModel().isPressed()) {
                background = hoverColor.darker();
            }

            g2.setColor(background);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), arc, arc);
            g2.dispose();

            super.paint(g, c);
        }

        @Override
        protected void installDefaults(AbstractButton b) {
            super.installDefaults(b);
            b.setOpaque(false);
            b.setRolloverEnabled(true);
        }
    }

    private static class ClassCardUI extends BasicButtonUI {
        private final Color normalColor;
        private final Color hoverColor;
        private final Color accentColor;
        private final int arc;

        public ClassCardUI(Color normalColor, Color hoverColor, Color accentColor, int arc) {
            this.normalColor = normalColor;
            this.hoverColor = hoverColor;
            this.accentColor = accentColor;
            this.arc = arc;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton button = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color background = button.getModel().isRollover() ? hoverColor : normalColor;

            g2.setColor(new Color(40, 80, 120, 18));
            g2.fillRoundRect(2, 4, c.getWidth() - 4, c.getHeight() - 6, arc, arc);

            g2.setColor(background);
            g2.fillRoundRect(0, 0, c.getWidth() - 2, c.getHeight() - 4, arc, arc);

            g2.setColor(accentColor);
            g2.fillRoundRect(0, 0, 7, c.getHeight() - 4, 7, 7);

            g2.dispose();
            super.paint(g, c);
        }

        @Override
        protected void installDefaults(AbstractButton b) {
            super.installDefaults(b);
            b.setOpaque(false);
            b.setRolloverEnabled(true);
        }
    }
}
