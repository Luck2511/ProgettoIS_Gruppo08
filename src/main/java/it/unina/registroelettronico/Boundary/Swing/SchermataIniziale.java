package it.unina.registroelettronico.Boundary.Swing;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import it.unina.registroelettronico.Controller.ControllerUtenti;
import it.unina.registroelettronico.DTO.DocenteDTO;
import it.unina.registroelettronico.DTO.StudenteDTO;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;


public class SchermataIniziale {

    private JPanel contentPane;
    private JLabel lblTitolo;
    private JLabel lblSottotitolo;
    private JPanel pnlScelte;
    private JButton btnDocente;
    private JButton btnStudente;

    public SchermataIniziale() {
        pnlScelte.setOpaque(true);

        preparaRiquadro(
                btnDocente,
                "Docente",
                "Gestisci classi, registro e valutazioni",
                "icons/docente.svg"
        );

        preparaRiquadro(
                btnStudente,
                "Studente",
                "Consulta informazioni, voti e attività",
                "icons/studente.svg"
        );
        btnDocente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                apriSchermataDocente();
            }
        });
        btnStudente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                apriSchermataStudente();
            }
        });
    }

    private void apriSchermataDocente() {

        JFrame frame =
                (JFrame) SwingUtilities.getWindowAncestor(contentPane);

        /*
        *
        * In caso di autenticazione avremmo avuto un oggetto DocenteDTO associato ai dati inseriti nel form di
        * autenticazione; non sviluppando quel caso d'uso
        * simuliamo questo comportamento creando noi un DocenteDTO fittizio e lavorando con esso
        *
        */

        DocenteDTO docente=new DocenteDTO("Anna Rita","Fasolino","annarita.fasolino@unina.it");

        //Vogliamo rendere persistente questo docente nel database
        ControllerUtenti controllerUtenti=ControllerUtenti.getInstance();

        boolean esitoRegistrazione=controllerUtenti.registraDocente(docente);

        if(!esitoRegistrazione){
            System.out.println("Errore nella registrazione fittizia");
        }
        else{
            SchermataDocente schermataDocente =
                    new SchermataDocente(docente);

            frame.setContentPane(
                    schermataDocente.getContentPane()
            );

            frame.revalidate();
            frame.repaint();
        }


    }


    private void apriSchermataStudente() {

        JFrame frame =
                (JFrame) SwingUtilities.getWindowAncestor(contentPane);

        /*
         *
         * In caso di autenticazione avremmo avuto un oggetto StudenteDTO associato ai dati inseriti nel form di
         * autenticazione; non sviluppando quel caso d'uso
         * simuliamo questo comportamento creando noi uno StudenteDTO fittizio e lavorando con esso
         *
         */

        StudenteDTO studente=new StudenteDTO("Matteo","Cefalo","matteo.cefalo@studenti.unina.it");

        //Vogliamo rendere persistente questo studente nel database
        ControllerUtenti controllerUtenti=ControllerUtenti.getInstance();

        boolean esitoRegistrazione=controllerUtenti.registraStudente(studente);

        if(!esitoRegistrazione){
            System.out.println("Errore nella registrazione fittizia");
        }
        else{
            SchermataStudente schermataStudente =
                    new SchermataStudente(studente);

            frame.setContentPane(
                    schermataStudente.getContentPane()
            );


            frame.revalidate();
            frame.repaint();
        }

    }

    private void preparaRiquadro(
            JButton button,
            String titolo,
            String descrizione,
            String pathIcona
    ) {
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setIconTextGap(16);
        button.setForeground(new Color(25, 59, 99));
        button.setIcon(new FlatSVGIcon(pathIcona, 150, 150));
        button.setText(
                "<html><div style='text-align:center;'>"
                        + "<span style='font-size:20px; font-weight:700; color:#193B63;'>" + titolo + "</span>"
                        + "<br><span style='font-size:11px; color:#6D7E91;'>" + descrizione + "</span>"
                        + "</div></html>"
        );
        button.setUI(
                new CardButtonUI(
                        Color.WHITE,
                        new Color(245, 250, 255),
                        new Color(214, 228, 244),
                        new Color(47, 128, 237),
                        30
                )
        );
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
        contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(28, 30, 28, 30), 0, 16));
        contentPane.setBackground(new Color(-1116929));
        contentPane.setOpaque(true);
        contentPane.setPreferredSize(new Dimension(900, 620));
        lblTitolo = new JLabel();
        lblTitolo.setBackground(new Color(-1));
        Font lblTitoloFont = this.$$$getFont$$$("Arial", Font.BOLD, 28, lblTitolo.getFont());
        if (lblTitoloFont != null) lblTitolo.setFont(lblTitoloFont);
        lblTitolo.setForeground(new Color(-15123613));
        lblTitolo.setHorizontalAlignment(0);
        lblTitolo.setOpaque(true);
        lblTitolo.setPreferredSize(new Dimension(820, 92));
        lblTitolo.setText("Seleziona il tuo ruolo");
        lblTitolo.setVerticalAlignment(0);
        contentPane.add(lblTitolo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblSottotitolo = new JLabel();
        Font lblSottotitoloFont = this.$$$getFont$$$(null, Font.PLAIN, 14, lblSottotitolo.getFont());
        if (lblSottotitoloFont != null) lblSottotitolo.setFont(lblSottotitoloFont);
        lblSottotitolo.setForeground(new Color(-10063271));
        lblSottotitolo.setHorizontalAlignment(0);
        lblSottotitolo.setText("Scegli se entrare come docente oppure come studente.");
        contentPane.add(lblSottotitolo, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlScelte = new JPanel();
        pnlScelte.setLayout(new GridLayoutManager(1, 2, new Insets(6, 6, 6, 6), 22, 0));
        pnlScelte.setBackground(new Color(-1116929));
        pnlScelte.setOpaque(true);
        contentPane.add(pnlScelte, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        btnDocente = new JButton();
        btnDocente.setBackground(new Color(-1));
        btnDocente.setFocusPainted(false);
        Font btnDocenteFont = this.$$$getFont$$$("Arial", Font.BOLD, 18, btnDocente.getFont());
        if (btnDocenteFont != null) btnDocente.setFont(btnDocenteFont);
        btnDocente.setForeground(new Color(-15123613));
        btnDocente.setHorizontalTextPosition(0);
        btnDocente.setIconTextGap(14);
        btnDocente.setOpaque(false);
        btnDocente.setPreferredSize(new Dimension(380, 380));
        btnDocente.setText("Docente");
        btnDocente.setVerticalTextPosition(3);
        pnlScelte.add(btnDocente, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnStudente = new JButton();
        btnStudente.setBackground(new Color(-1));
        btnStudente.setFocusPainted(false);
        Font btnStudenteFont = this.$$$getFont$$$("Arial", Font.BOLD, 18, btnStudente.getFont());
        if (btnStudenteFont != null) btnStudente.setFont(btnStudenteFont);
        btnStudente.setForeground(new Color(-15123613));
        btnStudente.setHorizontalTextPosition(0);
        btnStudente.setIconTextGap(14);
        btnStudente.setOpaque(false);
        btnStudente.setPreferredSize(new Dimension(380, 380));
        btnStudente.setText("Studente");
        btnStudente.setVerticalTextPosition(3);
        pnlScelte.add(btnStudente, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
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
            return new Insets(12, 12, 12, 12);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = 12;
            insets.left = 12;
            insets.bottom = 12;
            insets.right = 12;
            return insets;
        }
    }

    private static class CardButtonUI extends BasicButtonUI {
        private final Color normalBackground;
        private final Color hoverBackground;
        private final Color normalBorder;
        private final Color hoverBorder;
        private final int arc;

        public CardButtonUI(
                Color normalBackground,
                Color hoverBackground,
                Color normalBorder,
                Color hoverBorder,
                int arc
        ) {
            this.normalBackground = normalBackground;
            this.hoverBackground = hoverBackground;
            this.normalBorder = normalBorder;
            this.hoverBorder = hoverBorder;
            this.arc = arc;
        }

        @Override
        protected void installDefaults(AbstractButton b) {
            super.installDefaults(b);
            b.setOpaque(false);
            b.setRolloverEnabled(true);
            b.setBorder(new RoundedBorder(normalBorder, 2, arc));
            b.setMargin(new Insets(18, 18, 22, 18));
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton button = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            boolean hover = button.getModel().isRollover();
            boolean pressed = button.getModel().isPressed();

            Color fill = hover ? hoverBackground : normalBackground;
            Color border = hover ? hoverBorder : normalBorder;

            if (pressed) {
                fill = hoverBackground.darker();
            }

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), arc, arc);

            g2.setColor(border);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, c.getWidth() - 3, c.getHeight() - 3, arc, arc);

            g2.dispose();
            super.paint(g, c);
        }
    }
}
