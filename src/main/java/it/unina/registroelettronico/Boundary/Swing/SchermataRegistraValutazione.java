package it.unina.registroelettronico.Boundary.Swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import it.unina.registroelettronico.Controller.ControllerClassi;
import it.unina.registroelettronico.Controller.ControllerRegistro;
import it.unina.registroelettronico.DTO.InfoClasseDTO;
import it.unina.registroelettronico.DTO.StudenteDTO;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SchermataRegistraValutazione {

    private InfoClasseDTO classe;
    private List<StudenteDTO> studenti;

    private JPanel contentPane;
    private JLabel lblHeader;
    private JLabel lblTipologia;
    private JComboBox<String> cmbTipologia;
    private JLabel lblVoto;
    private JComboBox<String> cmbVoto;
    private JLabel lblStudente;
    private JComboBox<String> cmbStudente;
    private JLabel lblData;
    private JTextField txtData;
    private JButton btnScegliData;
    private JLabel lblNota;
    private JTextArea txtNota;
    private JButton btnRegistraValutazione;

    private LocalDate dataSelezionata;

    private final DateTimeFormatter formatoData =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Color bluPrimario = new Color(47, 128, 237);
    private final Color bluHover = new Color(35, 111, 209);
    private final Color azzurroHover = new Color(248, 252, 255);
    private final Color bluScuro = new Color(25, 59, 99);
    private final Color bordoChiaro = new Color(218, 232, 244);

    public SchermataRegistraValutazione(InfoClasseDTO classe) {

        this.classe=classe;
        configuraNavigazioneIndietro();
        aggiornaIntestazioneClasse();

        cmbTipologia.setModel(new DefaultComboBoxModel<>(
                new String[]{"Seleziona tipologia"}
        ));

        cmbVoto.setModel(new DefaultComboBoxModel<>(creaVoti()));

        cmbStudente.setModel(new DefaultComboBoxModel<>(
                new String[]{"Seleziona studente"}
        ));

        preparaCampi();
        preparaPulsanti();

        caricaTipologieValutazione();
        caricaStudenti(classe.getCodice());

        btnScegliData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                apriCalendario();
            }
        });

        btnRegistraValutazione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registraValutazione();
            }
        });
    }

    private void configuraNavigazioneIndietro() {
        contentPane.remove(lblHeader);
        JPanel header = Main.creaIntestazioneConIndietro(lblHeader);
        contentPane.add(
                header,
                new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null, 0, false)
        );
    }

    private void caricaTipologieValutazione() {


        ControllerRegistro controllerRegistro =
                ControllerRegistro.getInstance();

        List<String> tipologie =
                controllerRegistro.recuperaTipologieValutazione();

        setTipologieValutazione(
                tipologie.toArray(new String[0])
        );

    }

    private void setTipologieValutazione(String[] tipologie) {
        DefaultComboBoxModel<String> model =
                new DefaultComboBoxModel<>();

        model.addElement("Seleziona tipologia");

        if (tipologie != null) {
            for (String tipologia : tipologie) {
                model.addElement(tipologia.replace("_"," "));
            }
        }

        cmbTipologia.setModel(model);
    }

    private void caricaStudenti(String codiceClasse) {
        ControllerClassi controllerClassi = ControllerClassi.getInstance();

        studenti =
                controllerClassi.recuperaStudenti(codiceClasse);

        //Convertiamo in lista di stringhe per visualizzazione grafica
        String[] studentiVisualizzati = new String[studenti.size()];

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

    private void registraValutazione() {

        String tipologia = getTipologiaSelezionata();
        String voto = getVotoSelezionato();
        int indiceStudente = cmbStudente.getSelectedIndex();
        LocalDate data = getDataSelezionata();
        String nota = getNota();

        if (cmbTipologia.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(
                    contentPane,
                    "Seleziona una tipologia."
            );
            return;
        }

        if (cmbVoto.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(
                    contentPane,
                    "Seleziona un voto."
            );
            return;
        }

        if (indiceStudente == 0) {
            JOptionPane.showMessageDialog(
                    contentPane,
                    "Seleziona uno studente."
            );
            return;
        }

        if (data == null) {
            JOptionPane.showMessageDialog(
                    contentPane,
                    "Seleziona una data."
            );
            return;
        }
        else if(data.isAfter(LocalDate.now())){
            JOptionPane.showMessageDialog(
                    contentPane,
                    "Impossibile selezionare data futura."
            );
            return;
        }
        else if(data.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
            JOptionPane.showMessageDialog(
                    contentPane,
                    "Non puoi registare una valutazione di Domenica."
            );
            return;
        }

        if(nota.length()>200){
            JOptionPane.showMessageDialog(
                    contentPane,
                    "Superato limite caratteri nota [200 caratteri]."
            );
            return;
        }

        float votoNumerico=convertiVoto(voto);

        StudenteDTO studente =
                studenti.get(indiceStudente - 1);

        String emailStudente =
                studente.getIndirizzoEmailIstituzionale();

        ControllerRegistro controllerRegistro =
                ControllerRegistro.getInstance();

        boolean esitoCasoDUso=controllerRegistro.registraValutazione(
                classe.getCodice(),
                emailStudente,
                tipologia.replace(" ","_"),
                votoNumerico,
                data,
                nota
        );

        if(esitoCasoDUso){
            JOptionPane.showMessageDialog(
                    contentPane,
                    "Registrazione valutazione completata con successo."
            );

            resetFormValutazione();

        }
        else{
            JOptionPane.showMessageDialog(
                    contentPane,
                    "Errore nella registrazione della valutazione."
            );

        }



    }

    private void aggiornaIntestazioneClasse() {
        lblHeader.setText(
                "<html><div style='text-align:center;'>"
                        + "<b><font size='+4' color='#193B63'>Registra valutazione</font></b>"
                        + "<br><font color='#667788'>Inserisci i dati della valutazione dello studente.</font>"
                        + "<br><b><font color='#2F80ED'>Classe: "
                        + classe.getNome()
                        + " &nbsp;&bull;&nbsp; "
                        + classe.getCodice()
                        + "</font></b>"
                        + "</div></html>"
        );
    }

    private void resetFormValutazione() {
        cmbTipologia.setSelectedIndex(0);
        cmbVoto.setSelectedIndex(0);
        cmbStudente.setSelectedIndex(0);

        dataSelezionata = null;
        txtData.setText("");
        txtNota.setText("");

        cmbTipologia.requestFocusInWindow();
    }

    private float convertiVoto(String voto) {

        if (voto.endsWith("+")) {
            float valore = Float.parseFloat(
                    voto.substring(0, voto.length() - 1)
            );

            return valore + 0.25f;
        }

        if (voto.endsWith("½")) {
            float valore = Float.parseFloat(
                    voto.substring(0, voto.length() - 1)
            );

            return valore + 0.5f;
        }

        if (voto.endsWith("-")) {
            float valore = Float.parseFloat(
                    voto.substring(0, voto.length() - 1)
            );

            return valore - 0.25f;
        }

        return Float.parseFloat(voto);
    }


    private void preparaCampi() {
        Border bordoCampo = new RoundedBorder(bordoChiaro, 2, 20);

        preparaCombo(cmbTipologia, bordoCampo);
        preparaCombo(cmbVoto, bordoCampo);
        preparaCombo(cmbStudente, bordoCampo);

        txtData.setUI(new RoundedTextFieldUI(Color.WHITE, 20));
        txtData.setBorder(bordoCampo);
        txtData.setOpaque(false);
        txtData.setBackground(Color.WHITE);
        txtData.setMargin(new Insets(0, 12, 0, 12));

        txtNota.setUI(new RoundedTextAreaUI(Color.WHITE, 22));
        txtNota.setBorder(new RoundedBorder(bordoChiaro, 2, 22));
        txtNota.setOpaque(false);
        txtNota.setBackground(Color.WHITE);
        txtNota.setLineWrap(true);
        txtNota.setWrapStyleWord(true);
        txtNota.setMargin(new Insets(12, 12, 12, 12));

        cmbTipologia.setMaximumRowCount(8);
        cmbVoto.setMaximumRowCount(10);
        cmbStudente.setMaximumRowCount(10);

        cmbTipologia.setToolTipText("Scegli la tipologia della valutazione");
        cmbVoto.setToolTipText("Scegli il voto");
        cmbStudente.setToolTipText("Scegli lo studente");
        txtNota.setToolTipText("Inserisci una breve nota descrittiva");
    }

    private void preparaCombo(JComboBox<String> combo, Border bordoCampo) {
        combo.setUI(new RoundedComboBoxUI(
                bluScuro,
                Color.WHITE,
                20
        ));
        combo.setOpaque(false);
        combo.setBackground(Color.WHITE);
        combo.setBorder(bordoCampo);
        combo.setFocusable(false);
    }

    private void preparaPulsanti() {
        btnScegliData.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        btnScegliData.setBorder(new RoundedBorder(bluPrimario, 2, 20));
        btnScegliData.setBorderPainted(true);
        btnScegliData.setContentAreaFilled(false);
        btnScegliData.setOpaque(false);

        btnScegliData.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnScegliData.setForeground(bluHover);
                btnScegliData.setBorder(new RoundedBorder(bluHover, 2, 20));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnScegliData.setForeground(bluPrimario);
                btnScegliData.setBorder(new RoundedBorder(bluPrimario, 2, 20));
            }
        });

        btnRegistraValutazione.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        btnRegistraValutazione.setForeground(Color.WHITE);
        btnRegistraValutazione.setBorderPainted(false);
        btnRegistraValutazione.setContentAreaFilled(false);
        btnRegistraValutazione.setOpaque(false);
        btnRegistraValutazione.setUI(
                new RoundedButtonUI(bluPrimario, bluHover, 26)
        );
    }

    private String[] creaVoti() {
        List<String> voti = new ArrayList<>();
        voti.add("Seleziona voto");

        for (int quarti = 4; quarti <= 40; quarti++) {
            int intero = quarti / 4;
            int resto = quarti % 4;

            if (resto == 0) {
                voti.add(String.valueOf(intero));
            } else if (resto == 1) {
                voti.add(intero + "+");
            } else if (resto == 2) {
                voti.add(intero + "½");
            } else {
                voti.add((intero + 1) + "-");
            }
        }

        return voti.toArray(new String[0]);
    }

    private void apriCalendario() {
        Window proprietario = SwingUtilities.getWindowAncestor(contentPane);

        JDialog dialog = new JDialog(
                proprietario,
                "Seleziona data",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JPanel principale = new JPanel(new BorderLayout(12, 12));
        principale.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        principale.setBackground(Color.WHITE);

        JPanel intestazione = new JPanel(new BorderLayout(8, 0));
        intestazione.setOpaque(false);

        JButton btnPrecedente = new JButton("‹");
        JButton btnSuccessivo = new JButton("›");
        JLabel lblMese = new JLabel("", SwingConstants.CENTER);

        lblMese.setFont(lblMese.getFont().deriveFont(Font.BOLD, 17f));
        lblMese.setForeground(bluScuro);

        preparaPulsanteCalendario(btnPrecedente);
        preparaPulsanteCalendario(btnSuccessivo);

        intestazione.add(btnPrecedente, BorderLayout.WEST);
        intestazione.add(lblMese, BorderLayout.CENTER);
        intestazione.add(btnSuccessivo, BorderLayout.EAST);

        JPanel calendario = new JPanel(new GridLayout(0, 7, 6, 6));
        calendario.setOpaque(false);

        principale.add(intestazione, BorderLayout.NORTH);
        principale.add(calendario, BorderLayout.CENTER);

        final YearMonth[] meseVisualizzato = {
                YearMonth.from(dataSelezionata != null
                        ? dataSelezionata
                        : LocalDate.now())
        };

        Runnable aggiornaCalendario = () -> {
            calendario.removeAll();

            String[] giorni = {"Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom"};

            for (String giorno : giorni) {
                JLabel lbl = new JLabel(giorno, SwingConstants.CENTER);
                lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 12f));
                lbl.setForeground(bluPrimario);
                calendario.add(lbl);
            }

            YearMonth mese = meseVisualizzato[0];

            String nomeMese = mese.getMonth()
                    .getDisplayName(TextStyle.FULL, Locale.ITALIAN);

            lblMese.setText(
                    Character.toUpperCase(nomeMese.charAt(0))
                            + nomeMese.substring(1)
                            + " "
                            + mese.getYear()
            );

            LocalDate primoGiorno = mese.atDay(1);
            int spaziVuoti =
                    primoGiorno.getDayOfWeek().getValue()
                            - DayOfWeek.MONDAY.getValue();

            for (int i = 0; i < spaziVuoti; i++) {
                calendario.add(new JLabel(""));
            }

            for (int giorno = 1; giorno <= mese.lengthOfMonth(); giorno++) {
                int giornoCorrente = giorno;
                LocalDate dataGiorno = mese.atDay(giorno);

                JButton btnGiorno = new JButton(String.valueOf(giorno));
                btnGiorno.setFocusPainted(false);
                btnGiorno.setCursor(
                        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                );
                btnGiorno.setBackground(Color.WHITE);
                btnGiorno.setForeground(bluScuro);
                btnGiorno.setBorder(
                        new RoundedBorder(bordoChiaro, 1, 14)
                );

                if (dataGiorno.equals(LocalDate.now())) {
                    btnGiorno.setBackground(new Color(238, 246, 255));
                    btnGiorno.setBorder(
                            new RoundedBorder(bluPrimario, 2, 14)
                    );
                }

                btnGiorno.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selezionaData(
                                dialog,
                                meseVisualizzato[0],
                                giornoCorrente
                        );
                    }
                });

                calendario.add(btnGiorno);
            }

            calendario.revalidate();
            calendario.repaint();
        };

        btnPrecedente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostraMesePrecedente(
                        meseVisualizzato,
                        aggiornaCalendario
                );
            }
        });

        btnSuccessivo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostraMeseSuccessivo(
                        meseVisualizzato,
                        aggiornaCalendario
                );
            }
        });

        aggiornaCalendario.run();

        dialog.setContentPane(principale);
        dialog.setSize(440, 390);
        dialog.setLocationRelativeTo(contentPane);
        dialog.setVisible(true);
    }

    private void selezionaData(
            JDialog dialog,
            YearMonth meseVisualizzato,
            int giorno
    ) {
        dataSelezionata = meseVisualizzato.atDay(giorno);

        txtData.setText(
                dataSelezionata.format(formatoData)
        );

        dialog.dispose();
    }

    private void mostraMesePrecedente(
            YearMonth[] meseVisualizzato,
            Runnable aggiornaCalendario
    ) {
        meseVisualizzato[0] =
                meseVisualizzato[0].minusMonths(1);

        aggiornaCalendario.run();
    }

    private void mostraMeseSuccessivo(
            YearMonth[] meseVisualizzato,
            Runnable aggiornaCalendario
    ) {
        meseVisualizzato[0] =
                meseVisualizzato[0].plusMonths(1);

        aggiornaCalendario.run();
    }

    private void preparaPulsanteCalendario(JButton button) {
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setForeground(bluPrimario);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 20f));
        button.setBorder(
                new RoundedBorder(bluPrimario, 2, 16)
        );
        button.setPreferredSize(new Dimension(44, 36));
    }

    public void setStudenti(String[] studenti) {
        DefaultComboBoxModel<String> model =
                new DefaultComboBoxModel<>();

        model.addElement("Seleziona studente");

        if (studenti != null) {
            for (String studente : studenti) {
                model.addElement(studente);
            }
        }

        cmbStudente.setModel(model);
    }

    public String getTipologiaSelezionata() {
        return (String) cmbTipologia.getSelectedItem();
    }

    public String getVotoSelezionato() {
        return (String) cmbVoto.getSelectedItem();
    }

    public LocalDate getDataSelezionata() {
        return dataSelezionata;
    }

    public String getNota() {
        return txtNota.getText().trim();
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
        contentPane.setLayout(new GridLayoutManager(7, 3, new Insets(28, 42, 30, 42), 16, 18));
        contentPane.setBackground(new Color(-1116417));
        contentPane.setOpaque(true);
        contentPane.setPreferredSize(new Dimension(900, 720));
        lblHeader = new JLabel();
        lblHeader.setBackground(new Color(-1));
        lblHeader.setHorizontalAlignment(0);
        lblHeader.setOpaque(true);
        lblHeader.setPreferredSize(new Dimension(800, 104));
        lblHeader.setText("<html><div style='text-align:center;'><b><font size='+4' color='#193B63'>Registra valutazione</font></b><br><br><font color='#667788'>Inserisci i dati della valutazione dello studente.</font></div></html>");
        lblHeader.setVerticalAlignment(0);
        contentPane.add(lblHeader, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblTipologia = new JLabel();
        Font lblTipologiaFont = this.$$$getFont$$$(null, Font.BOLD, 15, lblTipologia.getFont());
        if (lblTipologiaFont != null) lblTipologia.setFont(lblTipologiaFont);
        lblTipologia.setForeground(new Color(-15123613));
        lblTipologia.setText("Tipologia");
        contentPane.add(lblTipologia, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbTipologia = new JComboBox();
        cmbTipologia.setBackground(new Color(-1));
        Font cmbTipologiaFont = this.$$$getFont$$$(null, Font.PLAIN, 14, cmbTipologia.getFont());
        if (cmbTipologiaFont != null) cmbTipologia.setFont(cmbTipologiaFont);
        cmbTipologia.setForeground(new Color(-15123613));
        cmbTipologia.setPreferredSize(new Dimension(530, 48));
        contentPane.add(cmbTipologia, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblVoto = new JLabel();
        Font lblVotoFont = this.$$$getFont$$$(null, Font.BOLD, 15, lblVoto.getFont());
        if (lblVotoFont != null) lblVoto.setFont(lblVotoFont);
        lblVoto.setForeground(new Color(-15123613));
        lblVoto.setText("Voto");
        contentPane.add(lblVoto, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbVoto = new JComboBox();
        cmbVoto.setBackground(new Color(-1));
        Font cmbVotoFont = this.$$$getFont$$$(null, Font.PLAIN, 14, cmbVoto.getFont());
        if (cmbVotoFont != null) cmbVoto.setFont(cmbVotoFont);
        cmbVoto.setForeground(new Color(-15123613));
        cmbVoto.setPreferredSize(new Dimension(530, 48));
        contentPane.add(cmbVoto, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblStudente = new JLabel();
        Font lblStudenteFont = this.$$$getFont$$$(null, Font.BOLD, 15, lblStudente.getFont());
        if (lblStudenteFont != null) lblStudente.setFont(lblStudenteFont);
        lblStudente.setForeground(new Color(-15123613));
        lblStudente.setText("Studente");
        contentPane.add(lblStudente, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbStudente = new JComboBox();
        cmbStudente.setBackground(new Color(-1));
        Font cmbStudenteFont = this.$$$getFont$$$(null, Font.PLAIN, 14, cmbStudente.getFont());
        if (cmbStudenteFont != null) cmbStudente.setFont(cmbStudenteFont);
        cmbStudente.setForeground(new Color(-15123613));
        cmbStudente.setPreferredSize(new Dimension(530, 48));
        contentPane.add(cmbStudente, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblData = new JLabel();
        Font lblDataFont = this.$$$getFont$$$(null, Font.BOLD, 15, lblData.getFont());
        if (lblDataFont != null) lblData.setFont(lblDataFont);
        lblData.setForeground(new Color(-15123613));
        lblData.setText("Data");
        contentPane.add(lblData, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtData = new JTextField();
        txtData.setBackground(new Color(-1));
        txtData.setEditable(false);
        Font txtDataFont = this.$$$getFont$$$(null, Font.PLAIN, 14, txtData.getFont());
        if (txtDataFont != null) txtData.setFont(txtDataFont);
        txtData.setForeground(new Color(-15123613));
        txtData.setPreferredSize(new Dimension(385, 48));
        txtData.setText("");
        contentPane.add(txtData, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnScegliData = new JButton();
        btnScegliData.setBackground(new Color(-1));
        btnScegliData.setFocusPainted(false);
        Font btnScegliDataFont = this.$$$getFont$$$(null, Font.BOLD, 13, btnScegliData.getFont());
        if (btnScegliDataFont != null) btnScegliData.setFont(btnScegliDataFont);
        btnScegliData.setForeground(new Color(-13664019));
        btnScegliData.setPreferredSize(new Dimension(132, 48));
        btnScegliData.setText("Scegli data");
        contentPane.add(btnScegliData, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblNota = new JLabel();
        Font lblNotaFont = this.$$$getFont$$$(null, Font.BOLD, 15, lblNota.getFont());
        if (lblNotaFont != null) lblNota.setFont(lblNotaFont);
        lblNota.setForeground(new Color(-15123613));
        lblNota.setText("Nota");
        contentPane.add(lblNota, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtNota = new JTextArea();
        txtNota.setBackground(new Color(-1));
        Font txtNotaFont = this.$$$getFont$$$(null, Font.PLAIN, 14, txtNota.getFont());
        if (txtNotaFont != null) txtNota.setFont(txtNotaFont);
        txtNota.setForeground(new Color(-15123613));
        txtNota.setMinimumSize(new Dimension(530, 125));
        txtNota.setPreferredSize(new Dimension(530, 150));
        contentPane.add(txtNota, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        btnRegistraValutazione = new JButton();
        btnRegistraValutazione.setBackground(new Color(-13664019));
        btnRegistraValutazione.setFocusPainted(false);
        Font btnRegistraValutazioneFont = this.$$$getFont$$$(null, Font.BOLD, 15, btnRegistraValutazione.getFont());
        if (btnRegistraValutazioneFont != null) btnRegistraValutazione.setFont(btnRegistraValutazioneFont);
        btnRegistraValutazione.setForeground(new Color(-1));
        btnRegistraValutazione.setPreferredSize(new Dimension(280, 54));
        btnRegistraValutazione.setText("Registra valutazione");
        contentPane.add(btnRegistraValutazione, new GridConstraints(6, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(backgroundColor);
            g2.fillRoundRect(
                    1,
                    1,
                    getComponent().getWidth() - 2,
                    getComponent().getHeight() - 2,
                    arc,
                    arc
            );

            g2.dispose();

            super.paintSafely(g);
        }
    }

    private static class RoundedTextAreaUI extends BasicTextAreaUI {

        private final Color backgroundColor;
        private final int arc;

        public RoundedTextAreaUI(Color backgroundColor, int arc) {
            this.backgroundColor = backgroundColor;
            this.arc = arc;
        }

        @Override
        protected void paintSafely(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(backgroundColor);
            g2.fillRoundRect(
                    1,
                    1,
                    getComponent().getWidth() - 2,
                    getComponent().getHeight() - 2,
                    arc,
                    arc
            );

            g2.dispose();

            super.paintSafely(g);
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
                    g2.setStroke(new BasicStroke(
                            2.2f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    ));

                    g2.drawLine(cx - 5, cy - 2, cx, cy + 3);
                    g2.drawLine(cx, cy + 3, cx + 5, cy - 2);

                    g2.dispose();
                }
            };

            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setOpaque(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            return button;
        }

        @Override
        public void paintCurrentValueBackground(
                Graphics g,
                Rectangle bounds,
                boolean hasFocus
        ) {
            // Non disegna il classico rettangolo spigoloso dietro al valore.
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
            g2.setStroke(new BasicStroke(thickness));

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
            return new Insets(8, 13, 8, 13);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = 8;
            insets.left = 13;
            insets.bottom = 8;
            insets.right = 13;
            return insets;
        }
    }

    private static class RoundedButtonUI extends BasicButtonUI {

        private final Color normalColor;
        private final Color hoverColor;
        private final int arc;

        public RoundedButtonUI(
                Color normalColor,
                Color hoverColor,
                int arc
        ) {
            this.normalColor = normalColor;
            this.hoverColor = hoverColor;
            this.arc = arc;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton button = (AbstractButton) c;

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Color background =
                    button.getModel().isRollover()
                            ? hoverColor
                            : normalColor;

            if (button.getModel().isPressed()) {
                background = hoverColor.darker();
            }

            g2.setColor(background);
            g2.fillRoundRect(
                    0,
                    0,
                    c.getWidth(),
                    c.getHeight(),
                    arc,
                    arc
            );

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
