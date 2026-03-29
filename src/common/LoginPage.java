package common;

import java.awt.*;

import javax.swing.*;

import common.components.app.IconUtils;
import common.components.app.UIStyle;
import dao.UserDAO;
import gui.backoffice.Main;
import model.User;
import service.AppSession;

public class LoginPage extends JFrame {

    private JTextField idField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private JProgressBar loader;
    private JButton loginButton;

    public LoginPage() {

        setTitle("Connexion");
        setSize(600, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();

        idField.requestFocus();
        getRootPane().setDefaultButton(loginButton);
        setVisible(true);
    }
    private void initUI() {

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(236, 240, 241));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        card.setPreferredSize(new Dimension(420, 480));

        // ==========================
        // LOGO
        // ==========================
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);

        JLabel logo = new JLabel();
        logo.setIcon(IconUtils.load("/icons/logo.png", 180, 50));

        logoPanel.add(logo);
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // ==========================
        // TITRE
        // ==========================
        JLabel title = new JLabel("Connexion");
        UIStyle.styleHeaderLabel(title);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Accédez à votre espace");
        UIStyle.styleSmallLabel(subtitle);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ==========================
        // FORM PANEL (PROPRE)
        // ==========================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        // IDENTIFIANT
        gbc.gridy = 0;
        JLabel idLabel = new JLabel("Identifiant");
        UIStyle.styleSubHeaderLabel(idLabel);
        formPanel.add(idLabel, gbc);

        gbc.gridy++;
        idField = new JTextField();
        UIStyle.styleTextField(idField, 35);
        idField.setPreferredSize(new Dimension(0, 35));
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        formPanel.add(idField, gbc);

        // PASSWORD
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Mot de passe");
        UIStyle.styleSubHeaderLabel(passwordLabel);
        formPanel.add(passwordLabel, gbc);

        gbc.gridy++;
        passwordField = new JPasswordField();
        UIStyle.styleTextField(passwordField, 35);
        passwordField.setPreferredSize(new Dimension(0, 35));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        formPanel.add(passwordField, gbc);

        // ERROR
        gbc.gridy++;
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(231, 76, 60));
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(errorLabel, gbc);

        // ==========================
        // LOADER
        // ==========================
        loader = new JProgressBar();
        loader.setIndeterminate(true);
        loader.setVisible(false);
        loader.setAlignmentX(Component.CENTER_ALIGNMENT);
        loader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));

        // ==========================
        // BUTTON
        // ==========================
        loginButton = new JButton("Se connecter");
        UIStyle.stylePrimaryButton(loginButton);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginButton.addActionListener(e -> handleLogin());

        // ==========================
        // AJOUT
        // ==========================
        card.add(logoPanel);
        card.add(Box.createVerticalStrut(15));

        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(25));

        card.add(formPanel);
        card.add(Box.createVerticalStrut(15));

        card.add(loader);
        card.add(Box.createVerticalStrut(15));

        card.add(loginButton);

        root.add(card);
        setContentPane(root);
    }
    private void handleLogin() {

        errorLabel.setText(" ");

        String email = idField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        // UI loading
        loginButton.setEnabled(false);
        loginButton.setText("Connexion...");
        loader.setVisible(true);

        UserDAO userDAO = new UserDAO();

        User user = userDAO.login(email, password);

        if(user != null) {
            JOptionPane.showMessageDialog(this, "Connexion réussie");
            dispose();
            if(AppSession.getInstance().getIsAdmin()) {
                    new Main(); // ouvrir le backoffice
                    return;
            }
        }else {
            errorLabel.setText("Identifiants incorrects");
        }

        // reset UI
        loginButton.setEnabled(true);
        loginButton.setText("Se connecter");
        loader.setVisible(false);
        // // Simule appel API (thread pour éviter freeze UI)
        // new Thread(() -> {
        //     try {
        //         Thread.sleep(1500); // simulation

        //         SwingUtilities.invokeLater(() -> {

        //             if (email.equals("admin@ent.com") && password.equals("admin")) {

        //                 JOptionPane.showMessageDialog(this, "Connexion réussie");
        //                 dispose();

        //                 new Main(); // ouvrir le backoffice

        //             } else {
        //                 errorLabel.setText("Identifiants incorrects");
        //             }

        //             // reset UI
        //             loginButton.setEnabled(true);
        //             loginButton.setText("Se connecter");
        //             loader.setVisible(false);
        //         });

        //     } catch (InterruptedException e) {
        //         e.printStackTrace();
        //     }
        // }).start();
    }
    // ==========================
    // MAIN TEST
    // ==========================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}