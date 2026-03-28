package dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Classe d'acces a la base de donnees
 * 
 * @author ESIGELEC - TIC Department
 * @version 2.0
 * */
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
	 * Constructor
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