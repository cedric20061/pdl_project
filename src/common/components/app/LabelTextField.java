package common.components.app;

import javax.swing.*;
import java.awt.*;

/**
 * Composant graphique personnalisé combinant un {@link JLabel} et un {@link JTextField}.
 * <p>
 * Ce composant est conçu pour faciliter la création de formulaires en regroupant
 * un label descriptif et un champ de saisie texte dans un seul composant réutilisable.
 * Le label est positionné au-dessus du champ texte grâce à un {@link BorderLayout}.
 * </p>
 *
 * <p>
 * Exemple d'utilisation :
 * </p>
 * <pre>
 *     LabelTextField nameField = new LabelTextField("Nom :", 20);
 *     String value = nameField.getText();
 * </pre>
 *
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 */
public class LabelTextField extends JPanel {

    private JLabel label;
    private JTextField textField;

    /**
     * Constructeur du composant LabelTextField.
     *
     * @param labelText le texte affiché dans le {@link JLabel}
     * @param fieldSize la taille du {@link JTextField} (nombre de colonnes)
     */
    public LabelTextField(String labelText, int fieldSize) {

        setLayout(new BorderLayout(5,5));

        label = new JLabel(labelText);
        textField = new JTextField(fieldSize);

        add(label, BorderLayout.NORTH);
        add(textField, BorderLayout.CENTER);
    }

    /**
     * Récupère le texte actuellement saisi dans le champ.
     *
     * @return le contenu du {@link JTextField}
     */
    public String getText() {
        return textField.getText();
    }

    /**
     * Définit le texte du champ de saisie.
     *
     * @param text le texte à afficher dans le {@link JTextField}
     */
    public void setText(String text) {
        textField.setText(text);
    }

    /**
     * Retourne le composant {@link JTextField}.
     * <p>
     * Permet d'accéder directement au champ pour des personnalisations avancées
     * (écouteurs, styles, validations, etc.).
     * </p>
     *
     * @return le {@link JTextField} associé
     */
    public JTextField getTextField() {
        return textField;
    }

    /**
     * Retourne le composant {@link JLabel}.
     *
     * @return le {@link JLabel} associé
     */
    public JLabel getLabel() {
        return label;
    }
}