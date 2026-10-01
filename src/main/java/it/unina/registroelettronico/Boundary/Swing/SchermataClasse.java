package it.unina.registroelettronico.Boundary.Swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import it.unina.registroelettronico.Boundary.Swing.SchermataRegistro;
import it.unina.registroelettronico.Controller.ControllerClassi;
import it.unina.registroelettronico.DTO.InfoClasseDTO;
import it.unina.registroelettronico.DTO.StudenteDTO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;

public class SchermataClasse {

    private InfoClasseDTO classe;

    private JPanel contentPane;
    private JLabel lblNomeClasse;
    private JLabel lblCodiceClasse;
    private JLabel lblSottotitolo;
    private JButton btnApriRegistro;
    private JLabel lblStudentiTitolo;
    private JButton btnAggiungiStudente;
    private JLabel lblStudentiHint;
    private JScrollPane scrollStudenti;
    private JList lstStudenti;

    public SchermataClasse(InfoClasseDTO classe) {
        this.classe = classe;
        lblNomeClasse.setText(classe.getNome());
        lblCodiceClasse.setText("Codice classe: " + classe.getCodice());

        configuraIntestazioneClasse();
        preparaElencoStudenti();

        caricaStudenti(classe.getCodice());

        btnApriRegistro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                apriSchermataRegistro(classe);
            }
        });
    }

    private void apriSchermataRegistro(InfoClasseDTO classe) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(contentPane);

        SchermataRegistro schermataRegistro = new SchermataRegistro(classe);

        Main.apriSchermata(
                frame,
                getContentPane(),
                schermataRegistro.getContentPane()
        );
    }

    private void caricaStudenti(String codice) {

        ControllerClassi controllerClassi=ControllerClassi.getInstance();

        List<StudenteDTO> studenti = controllerClassi.recuperaStudenti(codice);


        aggiornaElencoStudenti(studenti);
    }

    private void configuraIntestazioneClasse() {
        Container parent = lblNomeClasse.getParent();
        if (!(parent instanceof JPanel pannelloHeader)) {
            return;
        }

        pannelloHeader.remove(lblNomeClasse);
        pannelloHeader.remove(lblCodiceClasse);
        pannelloHeader.remove(lblSottotitolo);

        // Nessun margine iniziale: il codice resta allineato verticalmente
        // con il nome della classe e con il sottotitolo.
        JPanel rigaCodice = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rigaCodice.setOpaque(false);
        rigaCodice.setAlignmentX(Component.LEFT_ALIGNMENT);
        rigaCodice.add(lblCodiceClasse);
        rigaCodice.add(Box.createHorizontalStrut(10));
        rigaCodice.add(Main.creaPulsanteCopiaCodice(classe.getCodice()));

        JPanel testi = new JPanel();
        testi.setOpaque(false);
        testi.setLayout(new BoxLayout(testi, BoxLayout.Y_AXIS));
        lblNomeClasse.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSottotitolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        testi.add(lblNomeClasse);
        testi.add(Box.createRigidArea(new Dimension(0, 5)));
        testi.add(rigaCodice);
        testi.add(Box.createRigidArea(new Dimension(0, 5)));
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
                        0, 0, 3, 1,
                        GridConstraints.ANCHOR_WEST,
                        GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null, 0, false
                )
        );

        pannelloHeader.revalidate();
        pannelloHeader.repaint();
    }

    private void preparaElencoStudenti() {
        Color sfondoElenco = new Color(248, 251, 255);
        Color bordo = new Color(218, 232, 244);
        Color blu = new Color(47, 128, 237);
        Color testo = new Color(25, 59, 99);
        Color secondario = new Color(102, 119, 136);

        scrollStudenti.setBorder(BorderFactory.createEmptyBorder());
        scrollStudenti.getViewport().setBackground(sfondoElenco);
        scrollStudenti.getVerticalScrollBar().setUnitIncrement(16);

        lstStudenti.setOpaque(false);
        lstStudenti.setBackground(sfondoElenco);
        lstStudenti.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        lstStudenti.setFixedCellHeight(72);
        lstStudenti.setFocusable(false);
        lstStudenti.setRequestFocusEnabled(false);

        // L'elenco è solo informativo: nessuna selezione o interazione al click.
        lstStudenti.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                // Nessuna selezione.
            }

            @Override
            public void addSelectionInterval(int index0, int index1) {
                // Nessuna selezione.
            }
        });

        lstStudenti.setCellRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus
            ) {
                StudenteDTO studente = (StudenteDTO) value;

                JPanel card = new JPanel(new BorderLayout(12, 0)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // Aspetto volutamente statico: niente hover e niente stato selezionato.
                        g2.setColor(Color.WHITE);
                        g2.fillRoundRect(1, 3, getWidth() - 3, getHeight() - 7, 18, 18);
                        g2.setColor(bordo);
                        g2.setStroke(new BasicStroke(1.2f));
                        g2.drawRoundRect(1, 3, getWidth() - 3, getHeight() - 7, 18, 18);
                        g2.dispose();
                    }
                };

                card.setOpaque(false);
                card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

                JLabel indicatore = new JLabel("●", SwingConstants.CENTER);
                indicatore.setForeground(blu);
                indicatore.setFont(indicatore.getFont().deriveFont(Font.BOLD, 16f));
                indicatore.setPreferredSize(new Dimension(18, 18));

                JPanel testi = new JPanel();
                testi.setOpaque(false);
                testi.setLayout(new BoxLayout(testi, BoxLayout.Y_AXIS));

                JLabel nome = new JLabel(studente.getNome() + " " + studente.getCognome());
                nome.setForeground(testo);
                nome.setFont(nome.getFont().deriveFont(Font.BOLD, 14f));

                JLabel email = new JLabel(studente.getIndirizzoEmailIstituzionale());
                email.setForeground(secondario);
                email.setFont(email.getFont().deriveFont(Font.PLAIN, 12f));

                testi.add(nome);
                testi.add(Box.createRigidArea(new Dimension(0, 4)));
                testi.add(email);

                card.add(indicatore, BorderLayout.WEST);
                card.add(testi, BorderLayout.CENTER);

                Main.applicaRenderingTesto(card);
                return card;
            }
        });
    }

    private void aggiornaElencoStudenti(List<StudenteDTO> studenti) {
        DefaultListModel<StudenteDTO> model = new DefaultListModel<>();

        for (StudenteDTO studente : studenti) {
            model.addElement(studente);
        }

        lstStudenti.setModel(model);
        Main.applicaRenderingTesto(lstStudenti);
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
        Font contentPaneFont = this.$$$getFont$$$("Arial", -1, -1, contentPane.getFont());
        if (contentPaneFont != null) contentPane.setFont(contentPaneFont);
        contentPane.setOpaque(true);
        contentPane.setPreferredSize(new Dimension(820, 650));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 2, new Insets(18, 22, 18, 22), 14, 5));
        panel1.setBackground(new Color(-1));
        panel1.setOpaque(true);
        contentPane.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        lblNomeClasse = new JLabel();
        Font lblNomeClasseFont = this.$$$getFont$$$(null, Font.BOLD, 27, lblNomeClasse.getFont());
        if (lblNomeClasseFont != null) lblNomeClasse.setFont(lblNomeClasseFont);
        lblNomeClasse.setForeground(new Color(-14730405));
        lblNomeClasse.setText("Nome classe");
        panel1.add(lblNomeClasse, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblCodiceClasse = new JLabel();
        Font lblCodiceClasseFont = this.$$$getFont$$$(null, Font.BOLD, 14, lblCodiceClasse.getFont());
        if (lblCodiceClasseFont != null) lblCodiceClasse.setFont(lblCodiceClasseFont);
        lblCodiceClasse.setForeground(new Color(-12877066));
        lblCodiceClasse.setText("Codice classe: CLASSE-XXXX");
        panel1.add(lblCodiceClasse, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblSottotitolo = new JLabel();
        Font lblSottotitoloFont = this.$$$getFont$$$(null, Font.PLAIN, 13, lblSottotitolo.getFont());
        if (lblSottotitoloFont != null) lblSottotitolo.setFont(lblSottotitoloFont);
        lblSottotitolo.setForeground(new Color(-10785926));
        lblSottotitolo.setText("Gestisci gli studenti della classe e accedi al registro.");
        panel1.add(lblSottotitolo, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnApriRegistro = new JButton();
        btnApriRegistro.setBackground(new Color(-12877066));
        btnApriRegistro.setFocusPainted(false);
        Font btnApriRegistroFont = this.$$$getFont$$$(null, Font.BOLD, 14, btnApriRegistro.getFont());
        if (btnApriRegistroFont != null) btnApriRegistro.setFont(btnApriRegistroFont);
        btnApriRegistro.setForeground(new Color(-1));
        btnApriRegistro.setOpaque(true);
        btnApriRegistro.setPreferredSize(new Dimension(165, 42));
        btnApriRegistro.setText("Apri registro");
        panel1.add(btnApriRegistro, new GridConstraints(0, 1, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 2, new Insets(18, 22, 20, 22), 12, 10));
        panel2.setBackground(new Color(-1));
        panel2.setOpaque(true);
        contentPane.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        lblStudentiTitolo = new JLabel();
        Font lblStudentiTitoloFont = this.$$$getFont$$$(null, Font.BOLD, 19, lblStudentiTitolo.getFont());
        if (lblStudentiTitoloFont != null) lblStudentiTitolo.setFont(lblStudentiTitoloFont);
        lblStudentiTitolo.setForeground(new Color(-14730405));
        lblStudentiTitolo.setText("Studenti iscritti");
        panel2.add(lblStudentiTitolo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnAggiungiStudente = new JButton();
        btnAggiungiStudente.setBackground(new Color(-525569));
        btnAggiungiStudente.setFocusPainted(false);
        Font btnAggiungiStudenteFont = this.$$$getFont$$$(null, Font.BOLD, 13, btnAggiungiStudente.getFont());
        if (btnAggiungiStudenteFont != null) btnAggiungiStudente.setFont(btnAggiungiStudenteFont);
        btnAggiungiStudente.setForeground(new Color(-12877066));
        btnAggiungiStudente.setOpaque(true);
        btnAggiungiStudente.setPreferredSize(new Dimension(175, 38));
        btnAggiungiStudente.setText("+  Aggiungi studente");
        panel2.add(btnAggiungiStudente, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblStudentiHint = new JLabel();
        Font lblStudentiHintFont = this.$$$getFont$$$(null, Font.PLAIN, 12, lblStudentiHint.getFont());
        if (lblStudentiHintFont != null) lblStudentiHint.setFont(lblStudentiHintFont);
        lblStudentiHint.setForeground(new Color(-10785926));
        lblStudentiHint.setText("Elenco degli studenti attualmente associati alla classe.");
        panel2.add(lblStudentiHint, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrollStudenti = new JScrollPane();
        scrollStudenti.setBackground(new Color(-525569));
        scrollStudenti.setOpaque(true);
        scrollStudenti.setPreferredSize(new Dimension(720, 340));
        panel2.add(scrollStudenti, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollStudenti.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        lstStudenti = new JList();
        lstStudenti.setBackground(new Color(-525569));
        Font lstStudentiFont = this.$$$getFont$$$(null, Font.PLAIN, 14, lstStudenti.getFont());
        if (lstStudentiFont != null) lstStudenti.setFont(lstStudentiFont);
        lstStudenti.setForeground(new Color(-14730405));
        lstStudenti.setSelectionMode(0);
        lstStudenti.setVisibleRowCount(10);
        scrollStudenti.setViewportView(lstStudenti);
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

}
