package it.unina.registroelettronico.Boundary.Swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import it.unina.registroelettronico.Controller.ControllerClassi;
import it.unina.registroelettronico.Controller.ControllerRegistro;
import it.unina.registroelettronico.DTO.InfoClasseDTO;
import it.unina.registroelettronico.DTO.InfoValutazioneDTO;
import it.unina.registroelettronico.DTO.StudenteDTO;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;

public class SchermataValutazioniStudenti {

    private InfoClasseDTO classe;

    // CODICE AGGIUNTO: conserva gli studenti recuperati per la classe selezionata.
    private List<StudenteDTO> studenti;

    private JPanel contentPane;
    private JLabel lblHeader;
    private JLabel lblStudente;
    private JComboBox<String> cmbStudente;
    private JScrollPane scrollValutazioni;
    private JPanel pnlValutazioni;
    private JLabel lblInfo;

    private final Color bluScuro = new Color(25, 59, 99);
    private final Color testoSecondario = new Color(102, 119, 136);
    private final Color bordoChiaro = new Color(218, 232, 244);

    public SchermataValutazioniStudenti(InfoClasseDTO classe) {

        this.classe=classe;

        cmbStudente.setModel(new DefaultComboBoxModel<>(
                new String[]{"Seleziona studente"}
        ));

        cmbStudente.setUI(
                new RoundedComboBoxUI(bluScuro, Color.WHITE, 20)
        );
        cmbStudente.setOpaque(false);
        cmbStudente.setBackground(Color.WHITE);
        cmbStudente.setBorder(
                new RoundedBorder(bordoChiaro, 2, 20)
        );
        cmbStudente.setFocusable(false);
        cmbStudente.setMaximumRowCount(10);

        pnlValutazioni.setLayout(
                new BoxLayout(pnlValutazioni, BoxLayout.Y_AXIS)
        );

        scrollValutazioni.setBorder(null);
        scrollValutazioni.setOpaque(false);
        scrollValutazioni.getViewport().setOpaque(false);
        scrollValutazioni.getVerticalScrollBar().setUnitIncrement(18);

        // CODICE AGGIUNTO: recupera gli studenti della classe e popola la combo.
        caricaStudenti(classe.getCodice());

        mostraMessaggioIniziale();

        // CODICE AGGIUNTO: quando cambia lo studente selezionato,
        // avvia il recupero delle sue valutazioni.
        cmbStudente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                caricaValutazioniStudente();
            }
        });
    }

    //stessa logica usata in SchermataRegistraValutazione
    //per recuperare dal ControllerClassi gli studenti della classe.
    private void caricaStudenti(String codiceClasse) {

        ControllerClassi controllerClassi =
                ControllerClassi.getInstance();

        studenti =
                controllerClassi.recuperaStudenti(codiceClasse);

        String[] studentiVisualizzati =
                new String[studenti.size()];

        for (int i = 0; i < studenti.size(); i++) {

            StudenteDTO studente = studenti.get(i);

            studentiVisualizzati[i] =
                    studente.getNome() + " "
                            + studente.getCognome()
                            + " - "
                            + studente.getIndirizzoEmailIstituzionale();
        }

        setStudenti(studentiVisualizzati);
    }

    private void setStudenti(String[] studentiVisualizzati) {

        DefaultComboBoxModel<String> model =
                new DefaultComboBoxModel<>();

        model.addElement("Seleziona studente");

        if (studentiVisualizzati != null) {
            for (String studente : studentiVisualizzati) {
                model.addElement(studente);
            }
        }

        cmbStudente.setModel(model);
    }


    private void caricaValutazioniStudente() {

        int indiceStudente = cmbStudente.getSelectedIndex();

        if (indiceStudente == 0) {
            mostraMessaggioIniziale();
            return;
        }

        StudenteDTO studente =
                studenti.get(indiceStudente - 1);

        String emailStudente =
                studente.getIndirizzoEmailIstituzionale();



        ControllerRegistro controllerRegistro = ControllerRegistro.getInstance();
        List<InfoValutazioneDTO> valutazioni =
                 controllerRegistro.recuperaValutazioniStudente(
                         classe.getCodice(),
                         emailStudente
                );

        aggiornaElencoValutazioni(valutazioni);

    }

    private void mostraMessaggioIniziale() {

        pnlValutazioni.removeAll();

        JLabel messaggio = new JLabel(
                "<html><div style='text-align:center;'>"
                        + "<b><font color='#193B63' size='+1'>"
                        + "Nessuno studente selezionato"
                        + "</font></b>"
                        + "<br><br>"
                        + "<font color='#667788'>"
                        + "Scegli uno studente dal menu per visualizzare le sue valutazioni."
                        + "</font>"
                        + "</div></html>",
                SwingConstants.CENTER
        );

        messaggio.setAlignmentX(Component.CENTER_ALIGNMENT);
        messaggio.setBorder(
                BorderFactory.createEmptyBorder(90, 20, 90, 20)
        );

        pnlValutazioni.add(Box.createVerticalGlue());
        pnlValutazioni.add(messaggio);
        pnlValutazioni.add(Box.createVerticalGlue());

        pnlValutazioni.revalidate();
        pnlValutazioni.repaint();
    }

    // CODICE AGGIUNTO: riceve l'elenco completo delle valutazioni
    // restituito dal Controller e le visualizza una per una nel pannello.
    private void aggiornaElencoValutazioni(
            List<InfoValutazioneDTO> valutazioni
    ) {

        pnlValutazioni.removeAll();

        if (valutazioni == null || valutazioni.isEmpty()) {

            JLabel messaggio = new JLabel(
                    "Nessuna valutazione registrata per lo studente selezionato.",
                    SwingConstants.CENTER
            );

            messaggio.setForeground(testoSecondario);
            messaggio.setAlignmentX(Component.CENTER_ALIGNMENT);
            messaggio.setBorder(
                    BorderFactory.createEmptyBorder(90, 20, 90, 20)
            );

            pnlValutazioni.add(Box.createVerticalGlue());
            pnlValutazioni.add(messaggio);
            pnlValutazioni.add(Box.createVerticalGlue());

        } else {

            for (InfoValutazioneDTO valutazione : valutazioni) {

                JPanel card = creaCardValutazione(
                        String.valueOf(valutazione.getData()),
                        valutazione.getTipologia(),
                        valutazione.getNotaDescrittiva(),
                        String.valueOf(valutazione.getVoto())
                );

                pnlValutazioni.add(card);
                pnlValutazioni.add(
                        Box.createRigidArea(new Dimension(0, 14))
                );
            }
        }

        pnlValutazioni.revalidate();
        pnlValutazioni.repaint();
    }

    // METODO GIA' PRESENTE:
    // crea graficamente la card relativa a UNA singola valutazione.
    private JPanel creaCardValutazione(
            String data,
            String tipologia,
            String nota,
            String voto
    ) {

        RoundedPanel card = new RoundedPanel(
                Color.WHITE,
                24
        );

        card.setLayout(new BorderLayout(18, 0));
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundedBorder(bordoChiaro, 2, 24),
                        BorderFactory.createEmptyBorder(16, 18, 16, 18)
                )
        );

        card.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 122)
        );
        card.setPreferredSize(
                new Dimension(790, 122)
        );

        VoteBadge badge = new VoteBadge(voto);
        badge.setPreferredSize(
                new Dimension(112, 88)
        );

        JPanel informazioni = new JPanel();
        informazioni.setOpaque(false);
        informazioni.setLayout(
                new BoxLayout(informazioni, BoxLayout.Y_AXIS)
        );

        JLabel lblTitoloCard = new JLabel(
                "<html>"
                        + "<b><font color='#193B63' size='+1'>"
                        + tipologia
                        + "</font></b>"
                        + "<font color='#8796A5'>"
                        + " &nbsp;•&nbsp; "
                        + data
                        + "</font>"
                        + "</html>"
        );

        JLabel lblNotaCard = new JLabel(
                "<html><div style='width:560px;'>"
                        + "<font color='#667788'>"
                        + nota
                        + "</font>"
                        + "</div></html>"
        );

        lblTitoloCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblNotaCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        informazioni.add(Box.createVerticalGlue());
        informazioni.add(lblTitoloCard);
        informazioni.add(
                Box.createRigidArea(new Dimension(0, 12))
        );
        informazioni.add(lblNotaCard);
        informazioni.add(Box.createVerticalGlue());

        card.add(informazioni, BorderLayout.CENTER);
        card.add(badge, BorderLayout.EAST);

        return card;
    }

    public JPanel getContentPane() {
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
        contentPane.setLayout(new GridLayoutManager(4, 2, new Insets(28, 36, 28, 36), 14, 16));
        contentPane.setBackground(new Color(-1116417));
        contentPane.setOpaque(true);
        contentPane.setPreferredSize(new Dimension(940, 720));
        lblHeader = new JLabel();
        lblHeader.setBackground(new Color(-1));
        lblHeader.setHorizontalAlignment(0);
        lblHeader.setOpaque(true);
        lblHeader.setPreferredSize(new Dimension(850, 96));
        lblHeader.setText("<html><div style='text-align:center;'><b><font size='+4' color='#193B63'>Valutazioni studenti</font></b><br><br><font color='#667788'>Seleziona uno studente per visualizzare le valutazioni registrate nella classe.</font></div></html>");
        lblHeader.setVerticalAlignment(0);
        contentPane.add(lblHeader, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblStudente = new JLabel();
        Font lblStudenteFont = this.$$$getFont$$$(null, Font.BOLD, 15, lblStudente.getFont());
        if (lblStudenteFont != null) lblStudente.setFont(lblStudenteFont);
        lblStudente.setForeground(new Color(-15123613));
        lblStudente.setText("Studente");
        contentPane.add(lblStudente, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbStudente = new JComboBox();
        cmbStudente.setBackground(new Color(-1));
        Font cmbStudenteFont = this.$$$getFont$$$(null, Font.PLAIN, 14, cmbStudente.getFont());
        if (cmbStudenteFont != null) cmbStudente.setFont(cmbStudenteFont);
        cmbStudente.setForeground(new Color(-15123613));
        cmbStudente.setPreferredSize(new Dimension(650, 48));
        contentPane.add(cmbStudente, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrollValutazioni = new JScrollPane();
        scrollValutazioni.setBackground(new Color(-1116417));
        scrollValutazioni.setHorizontalScrollBarPolicy(31);
        scrollValutazioni.setOpaque(false);
        scrollValutazioni.setPreferredSize(new Dimension(850, 475));
        scrollValutazioni.setVerticalScrollBarPolicy(20);
        contentPane.add(scrollValutazioni, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        pnlValutazioni = new JPanel();
        pnlValutazioni.setLayout(new GridLayoutManager(1, 1, new Insets(6, 6, 6, 6), -1, -1));
        pnlValutazioni.setBackground(new Color(-1116417));
        pnlValutazioni.setOpaque(true);
        scrollValutazioni.setViewportView(pnlValutazioni);
        lblInfo = new JLabel();
        Font lblInfoFont = this.$$$getFont$$$(null, Font.PLAIN, 13, lblInfo.getFont());
        if (lblInfoFont != null) lblInfo.setFont(lblInfoFont);
        lblInfo.setForeground(new Color(-10066330));
        lblInfo.setHorizontalAlignment(0);
        lblInfo.setText("Seleziona uno studente per visualizzare i suoi voti.");
        contentPane.add(lblInfo, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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

    private static class RoundedPanel extends JPanel {

        private final Color backgroundColor;
        private final int arc;

        public RoundedPanel(
                Color backgroundColor,
                int arc
        ) {
            this.backgroundColor = backgroundColor;
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(backgroundColor);
            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    arc,
                    arc
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class VoteBadge extends JComponent {

        private final String voto;

        public VoteBadge(String voto) {
            this.voto = voto == null ? "" : voto;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int w = getWidth();
            int h = getHeight();

            GradientPaint gradient = new GradientPaint(
                    0,
                    0,
                    new Color(47, 128, 237),
                    w,
                    h,
                    new Color(111, 78, 225)
            );

            g2.setPaint(gradient);

            g2.fillRoundRect(
                    0,
                    0,
                    w,
                    h,
                    26,
                    26
            );

            g2.setColor(Color.WHITE);
            g2.setFont(
                    getFont().deriveFont(Font.BOLD, 30f)
            );

            FontMetrics fm = g2.getFontMetrics();

            int x = (w - fm.stringWidth(voto)) / 2;
            int y = (h - fm.getHeight()) / 2 + fm.getAscent();

            g2.drawString(voto, x, y);

            g2.dispose();
        }
    }

    private static class RoundedComboBoxUI extends BasicComboBoxUI {

        private final Color arrowColor;
        private final Color backgroundColor;
        private final int arc;

        public RoundedComboBoxUI(
                Color arrowColor,
                Color backgroundColor,
                int arc
        ) {
            this.arrowColor = arrowColor;
            this.backgroundColor = backgroundColor;
            this.arc = arc;
        }

        @Override
        public void paint(Graphics g, JComponent c) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(backgroundColor);

            g2.fillRoundRect(
                    1,
                    1,
                    c.getWidth() - 2,
                    c.getHeight() - 2,
                    arc,
                    arc
            );

            g2.dispose();

            super.paint(g, c);
        }

        @Override
        protected JButton createArrowButton() {

            JButton button = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {

                    Graphics2D g2 = (Graphics2D) g.create();

                    g2.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON
                    );

                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;

                    g2.setColor(arrowColor);

                    g2.setStroke(
                            new BasicStroke(
                                    2.2f,
                                    BasicStroke.CAP_ROUND,
                                    BasicStroke.JOIN_ROUND
                            )
                    );

                    g2.drawLine(
                            cx - 5,
                            cy - 2,
                            cx,
                            cy + 3
                    );

                    g2.drawLine(
                            cx,
                            cy + 3,
                            cx + 5,
                            cy - 2
                    );

                    g2.dispose();
                }
            };

            button.setBorder(
                    BorderFactory.createEmptyBorder()
            );
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setOpaque(false);

            button.setCursor(
                    Cursor.getPredefinedCursor(
                            Cursor.HAND_CURSOR
                    )
            );

            return button;
        }

        @Override
        public void paintCurrentValueBackground(
                Graphics g,
                Rectangle bounds,
                boolean hasFocus
        ) {
            // Nessun rettangolo spigoloso dietro al valore selezionato.
        }
    }

    private static class RoundedBorder extends AbstractBorder {

        private final Color color;
        private final int thickness;
        private final int arc;

        public RoundedBorder(
                Color color,
                int thickness,
                int arc
        ) {
            this.color = color;
            this.thickness = thickness;
            this.arc = arc;
        }

        @Override
        public void paintBorder(
                Component c,
                Graphics g,
                int x,
                int y,
                int width,
                int height
        ) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(color);
            g2.setStroke(
                    new BasicStroke(thickness)
            );

            int offset = thickness / 2;

            g2.drawRoundRect(
                    x + offset,
                    y + offset,
                    width - thickness,
                    height - thickness,
                    arc,
                    arc
            );

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(
                    8,
                    13,
                    8,
                    13
            );
        }

        @Override
        public Insets getBorderInsets(
                Component c,
                Insets insets
        ) {
            insets.top = 8;
            insets.left = 13;
            insets.bottom = 8;
            insets.right = 13;

            return insets;
        }
    }
}
