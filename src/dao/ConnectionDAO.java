package dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Classe de base pour l'accès à la base de données Oracle.
 * Gère les paramètres de connexion (URL, LOGIN, PASS) chargés depuis config.properties.
 * 
 * Responsabilités :
 * - Charger les paramètres de connexion depuis le fichier de configuration
 * - Initialiser le driver Oracle JDBC
 * - Fournir une base commune pour tous les DAO
 * 
 * Configuration requise dans config.properties :
 * - db.url : URL de connexion Oracle (ex: jdbc:oracle:thin:@host:1521:sid)
 * - db.login : Identifiant de connexion (ex: C##BDD1_1)
 * - db.pass : Mot de passe de connexion
 * 
 * @author ESIGELEC - TIC Department
 * @version 2.0
 * @since 1.0
 */
public class ConnectionDAO {
	/**
	 * Parametres de connexion a la base de donnees oracle
	 * URL, LOGIN et PASS sont des constantes
	 */
	// � utiliser si vous �tes sur une machine personnelle :
	protected static String URL   = "jdbc:oracle:thin:@oracle.esigelec.fr:1521:orcl";
	
	// � utiliser si vous �tes sur une machine de l'�cole :
	// final static String URL   = "jdbc:oracle:thin:@//srvoracledb.intranet.int:1521/orcl.intranet.int";

	protected static String LOGIN = "";   // remplacer les ********. Exemple C##BDD1_1
	protected static String PASS  = "";   // remplacer les ********. Exemple BDD11
	
	/**
	 * Constructeur.
	 * Charge les paramètres de connexion depuis config.properties et initialise le driver Oracle.
	 * 
	 * Charge les propriétés suivantes :
	 * - db.url : URL de la base de données
	 * - db.login : Identifiant de connexion
	 * - db.pass : Mot de passe de connexion
	 * 
	 */
	public ConnectionDAO() {
		// chargement du pilote de bases de donnees
		try {
			Properties props = new Properties();
			props.load(new FileInputStream("config.properties"));
			URL = props.getProperty("db.url");
			LOGIN = props.getProperty("db.login");
			PASS = props.getProperty("db.pass");
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("Impossible de charger le pilote de BDD, ne pas oublier d'importer le fichier .jar dans le projet");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}