package it.unina.registroelettronico.Boundary.Swing;


import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.font.TextAttribute;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static final Deque<JPanel> cronologiaSchermate = new ArrayDeque<>();

    private static final Color BLU_PRIMARIO = new Color(47, 128, 237);
    private static final Color BLU_HOVER = new Color(35, 111, 209);
    private static final Color BORDO_CHIARO = new Color(214, 228, 244);

    // Famiglia tipografica uniforme: usa Inter quando disponibile, con fallback sicuri.
    private static final String FONT_UI_FAMILY = scegliFamigliaFontUI();

    public static void main(String[] args) {

        // Migliora il rendering del testo a livello globale senza modificare i form generati.
        System.setProperty("awt.useSystemAAFontSettings", "lcd_hrgb");
        System.setProperty("swing.aatext", "true");

        UIManager.put(
                "defaultFont",
                creaFontUI(Font.PLAIN, 14f)
        );

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Area Docente");

            SchermataIniziale schermataIniziale = new SchermataIniziale();
            JPanel iniziale = schermataIniziale.getContentPane();
            applicaRenderingTesto(iniziale);

            frame.setContentPane(iniziale);

            frame.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );

            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /*
     * Navigazione centralizzata senza introdurre nuove classi/file.
     * Conserva il pannello corrente e mostra quello successivo.
     */
    public static void apriSchermata(JFrame frame, JPanel schermataCorrente, JPanel schermataSuccessiva) {
        if (frame == null || schermataSuccessiva == null) {
            return;
        }

        if (schermataCorrente != null) {
            cronologiaSchermate.push(schermataCorrente);
        }

        applicaRenderingTesto(schermataSuccessiva);
        frame.setContentPane(schermataSuccessiva);
        frame.revalidate();
        frame.repaint();
    }

    public static void tornaIndietro(JFrame frame) {
        if (frame == null || cronologiaSchermate.isEmpty()) {
            return;
        }

        JPanel schermataPrecedente = cronologiaSchermate.pop();
        frame.setContentPane(schermataPrecedente);
        frame.revalidate();
        frame.repaint();
    }

    /*
     * Pulsante di navigazione volutamente compatto: solo icona, con un contenitore
     * quadrato arrotondato. In questo modo resta riconoscibile come azione di sistema
     * senza competere visivamente con il titolo della schermata.
     */
    public static JButton creaPulsanteIndietro() {

        JButton button = new JButton(creaIconaFrecciaIndietro()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                ButtonModel model = getModel();

                Color sfondo;
                Color bordo;

                if (model.isPressed()) {
                    sfondo = new Color(224, 238, 255);
                    bordo = new Color(157, 194, 238);
                } else if (model.isRollover()) {
                    sfondo = new Color(239, 247, 255);
                    bordo = new Color(184, 211, 243);
                } else {
                    sfondo = new Color(255, 255, 255, 235);
                    bordo = new Color(215, 229, 245);
                }

                // Ombra molto leggera, coerente con le card dell'interfaccia.
                g2.setColor(new Color(25, 59, 99, model.isRollover() ? 18 : 10));
                g2.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 4, 14, 14);

                g2.setColor(sfondo);
                g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);

                g2.setColor(bordo);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        button.setPreferredSize(new Dimension(40, 40));
        button.setMinimumSize(new Dimension(40, 40));
        button.setMaximumSize(new Dimension(40, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setUI(new BasicButtonUI());
        button.setToolTipText("Torna indietro");
        button.getAccessibleContext().setAccessibleName("Indietro");

        button.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(button);
            if (window instanceof JFrame frame) {
                tornaIndietro(frame);
            }
        });

        return button;
    }

    /*
     * Costruisce una riga di intestazione con il pulsante Indietro a sinistra.
     * A destra viene riservata la stessa larghezza del pulsante: il titolo rimane
     * centrato, ma l'header risulta meno pesante rispetto alla precedente versione.
     */
    public static JPanel creaIntestazioneConIndietro(JLabel intestazione) {
        /*
         * L'intera intestazione è bianca: in questo modo il pulsante Indietro
         * fa parte visivamente dello stesso pannello del titolo e non resta
         * appoggiato sullo sfondo azzurro della schermata.
         */
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(true);
        header.setBackground(Color.WHITE);

        /*
         * GridBagLayout centra il pulsante sia orizzontalmente sia verticalmente
         * nell'altezza dell'header. Il pannello laterale mantiene una larghezza
         * fissa, senza alterare il centraggio del titolo.
         */
        JPanel sinistra = new JPanel(new GridBagLayout());
        sinistra.setOpaque(true);
        sinistra.setBackground(Color.WHITE);
        // Nelle schermate che usano questa intestazione il pulsante viene
        // spostato leggermente verso destra, mantenendo il titolo centrato.
        sinistra.setPreferredSize(new Dimension(64, 40));
        sinistra.add(creaPulsanteIndietro());

        JPanel destra = new JPanel();
        destra.setOpaque(true);
        destra.setBackground(Color.WHITE);
        destra.setPreferredSize(new Dimension(64, 40));

        // Anche la label centrale mantiene lo stesso sfondo dell'header.
        intestazione.setBackground(Color.WHITE);
        intestazione.setOpaque(true);

        header.add(sinistra, BorderLayout.WEST);
        header.add(intestazione, BorderLayout.CENTER);
        header.add(destra, BorderLayout.EAST);

        Dimension preferita = intestazione.getPreferredSize();
        if (preferita != null) {
            header.setPreferredSize(preferita);
        }

        applicaRenderingTesto(header);
        return header;
    }

    private static Icon creaIconaFrecciaIndietro() {
        return new FlatSVGIcon("icons/indietro.svg", 20, 20);
    }

    /*
     * Il pulsante di copia è icon-only: vicino al codice risulta più pulito e non
     * aumenta inutilmente la larghezza delle card. Il significato completo resta
     * disponibile tramite tooltip e descrizione accessibile.
     */
    public static JButton creaPulsanteCopiaCodice(String codiceClasse) {

        JButton button = new JButton(creaIconaCopia()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                ButtonModel model = getModel();
                boolean copiato = Boolean.TRUE.equals(getClientProperty("codiceCopiato"));

                Color sfondo;
                Color bordo;

                if (copiato) {
                    // Feedback più netto dopo il click: verde ben visibile.
                    sfondo = new Color(220, 246, 229);
                    bordo = new Color(46, 160, 87);
                } else if (model.isPressed()) {
                    sfondo = new Color(224, 238, 255);
                    bordo = new Color(157, 194, 238);
                } else if (model.isRollover()) {
                    sfondo = new Color(239, 247, 255);
                    bordo = new Color(184, 211, 243);
                } else {
                    sfondo = new Color(255, 255, 255, 220);
                    bordo = new Color(215, 229, 245);
                }

                g2.setColor(sfondo);
                g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 11, 11);

                g2.setColor(bordo);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 11, 11);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        button.setPreferredSize(new Dimension(32, 32));
        button.setMinimumSize(new Dimension(32, 32));
        button.setMaximumSize(new Dimension(32, 32));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setUI(new BasicButtonUI());
        button.setToolTipText("Copia codice negli appunti");
        button.getAccessibleContext().setAccessibleName("Copia codice negli appunti");

        button.addActionListener(e -> {
            try {
                Toolkit.getDefaultToolkit()
                        .getSystemClipboard()
                        .setContents(new StringSelection(codiceClasse), null);

                button.putClientProperty("codiceCopiato", Boolean.TRUE);
                button.setToolTipText("Codice copiato!");
                button.repaint();

                // Mostra anche un piccolo messaggio temporaneo vicino al pulsante,
                // senza modificare il layout della schermata.
                mostraFeedbackCopia(button);

            } catch (IllegalStateException | HeadlessException ex) {
                JOptionPane.showMessageDialog(
                        button,
                        "Impossibile accedere agli appunti di sistema.",
                        "Copia codice",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        return button;
    }

    private static Icon creaIconaCopia() {
        return new FlatSVGIcon("icons/copia.svg", 17, 17);
    }

    private static void mostraFeedbackCopia(JButton button) {
        JToolTip feedback = button.createToolTip();
        feedback.setTipText("✓  Codice copiato!");
        feedback.setBackground(new Color(236, 250, 241));
        feedback.setForeground(new Color(25, 117, 62));
        feedback.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(145, 207, 169), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        applicaRenderingTesto(feedback);

        Popup popup = null;
        try {
            Point posizione = button.getLocationOnScreen();
            Dimension dimensione = feedback.getPreferredSize();
            int x = posizione.x + (button.getWidth() - dimensione.width) / 2;
            int y = posizione.y - dimensione.height - 7;

            popup = PopupFactory.getSharedInstance().getPopup(
                    button, feedback, x, y
            );
            popup.show();
        } catch (IllegalComponentStateException ignored) {
            // Il cambio di colore del pulsante resta comunque come feedback.
        }

        Popup popupDaChiudere = popup;
        Timer timer = new Timer(1500, evento -> {
            if (popupDaChiudere != null) {
                popupDaChiudere.hide();
            }
            button.putClientProperty("codiceCopiato", Boolean.FALSE);
            button.setToolTipText("Copia codice negli appunti");
            button.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    /*
     * Uniforma davvero la tipografia dei componenti già creati dai form.
     * La crenatura viene affidata alle coppie di glifi del font (KERNING_ON):
     * non viene più aggiunto tracking artificiale, che allargava uniformemente
     * tutte le lettere e poteva peggiorare la resa di titoli e testi brevi.
     */
    public static void applicaRenderingTesto(Component component) {
        if (component == null) {
            return;
        }

        Font font = component.getFont();
        if (font != null) {
            Font base = creaFontUI(font.getStyle(), font.getSize2D());
            Map<TextAttribute, Object> attributi = new HashMap<>(base.getAttributes());
            attributi.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
            attributi.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);
            attributi.put(TextAttribute.TRACKING, 0f);

            component.setFont(base.deriveFont(attributi));
        }

        if (component instanceof JComponent swingComponent) {
            swingComponent.putClientProperty("html.disable", Boolean.FALSE);
        }

        if (component instanceof Container container) {
            for (Component figlio : container.getComponents()) {
                applicaRenderingTesto(figlio);
            }
        }
    }

    private static Font creaFontUI(int stile, float dimensione) {
        return new Font(FONT_UI_FAMILY, stile, 1).deriveFont(dimensione);
    }

    private static String scegliFamigliaFontUI() {
        String[] preferenze = {"Inter", "Segoe UI", "Arial", "SansSerif"};
        String[] disponibili = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();

        for (String preferita : preferenze) {
            for (String disponibile : disponibili) {
                if (preferita.equalsIgnoreCase(disponibile)) {
                    return disponibile;
                }
            }
        }

        return Font.SANS_SERIF;
    }
}
