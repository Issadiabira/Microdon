/**
 *
 */
package social;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Comparator;
import java.util.ListIterator;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import social.model.SimplePost;
import social.model.User;
import social.view.FollowUserView;
import social.view.LoginPanel;
import social.view.MessageEditView;
import social.view.MessageView;

import social.model.test.DataProvider;

//import test.util.model.ListIterObserver;
//import test.util.model.ListIterObserverAdapter;

/**
 * Microdon est un réseau social très rudimentaire, permettant à un utilisateur
 * de se créer un compte avec mot de passe, de se connecter, de consulter son
 * fil d'actualité, de poster des messages et de s'abonner/désabonner aux comptes d'autres
 * utilisateurs. Le fil d'actualité de l'utilisateur est constitué de ses
 * propres messages ainsi que des messages des utilisateurs auxquel il est
 * abonné, ordonné du plus récent au plus ancien.
 * 
 * Cette classe permet de lancer l'application réseau social Microdon. Cette
 * classe possède par construction (à l'aide d'un constructeur private) une
 * unique instance.
 * 
 * @invariant isUserLogged() ==> getCurrentUser() != null;
 * @invariant getFrame() != null;
 * 
 * @author Marc Champesme
 * @since 2/08/2023
 * @version 13/11/2023
 */
public class Microdon implements Runnable {
	private final static Microdon theInstance = new Microdon();

	private JFrame myFrame;
	private boolean userIsLogged;
	private User currentUser;

	/**
	 * Renvoie l'unique instance de cette classe.
	 * 
	 * @return l'unique instance de cette classe.
	 */
	public static Microdon getTheInstance() {
		return theInstance;
	}

	/**
	 * Lance l'application Microdon.
	 * 
	 * @param args non utilisé
	 */
	public static void main(String[] args) {
		// Create test data:
		User u = DataProvider.userSupplier();

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(theInstance);
	}

	/**
	 * Initialise l'unique instance de cette classe.
	 * 
	 * @ensures getFrame() != null;
	 */
	private Microdon() {
		// Create and set up the window.
		myFrame = new JFrame("Microdon");
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Renvoie la fenêtre principale de l'application.
	 * 
	 * @return la fenêtre principale de l'application
	 * 
	 * @ensures \result != null;
	 * 
	 * @pure
	 */
	public JFrame getFrame() {
		return myFrame;
	}

	/**
	 * Renvoie l'utilisateur actuellement connecté, s'il y en a un.
	 * 
	 * @return l'utilisateur actuellement connecté, s'il y en a un
	 * 
	 * @ensures isUserLogged() ==> \result != null;
	 * 
	 * @pure
	 */
	public User getCurrentUser() {
		return currentUser;
	}

	/**
	 * Fait de l'utilisateur spécifié l'utilisateur courant.
	 * 
	 * @param u le nouvel utilisateur courant
	 * 
	 * @throws NullPointerException si l'utilisateur spécifié est null
	 * 
	 * @requires u != null;
	 * @ensures getCurrentUser().equals(u);
	 * @ensures isUserLogged();
	 */
	public void setCurrentUser(User u) {
		if (u == null) {
			throw new NullPointerException();
		}
		this.currentUser = u;
		this.userIsLogged = true;
	}

	/**
	 * Renvoie true si un utilisateur est actuellement connecté.
	 * 
	 * @return true si un utilisateur est actuellement connecté, false sinon
	 * 
	 * @pure
	 */
	public boolean isUserLogged() {
		return userIsLogged;
	}

	/**
	 * Déconnecte l'utilisateur courant.
	 * 
	 * @ensures !isUserLogged();
	 */
	public void logout() {
		this.userIsLogged = false;
	}

	/**
	 * Crée et affiche la fenêtre d'accueil permettant de se connecter ou de créer
	 * un compte utilisateur.
	 */
	public void createAndShowLoginPanel() {
		// Add the "Bienvenue…" label.
		myFrame.setPreferredSize(new Dimension(300, 180));

		JPanel p = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Bienvenue dans le réseau social Microdon");
		p.add(label, BorderLayout.NORTH);
		p.add(new LoginPanel(this), BorderLayout.CENTER);
		myFrame.setContentPane(p);

		// Display the window.
		myFrame.pack();
		myFrame.setVisible(true);
	}

	/**
	 * Crée et affiche la fenêtre de visualisation des messages du fil d'actualité
	 * de l'utilisateur actuellement connecté.
	 * 
	 * throws IllegalStateException si aucun utilisateur n'est actuellement connecté
	 * 
	 * @requires isUserLogged();
	 */
	public void createAndShowMessageListPanel() {
		if (!isUserLogged()) {
			throw new IllegalStateException(
					"Un utilisateur doit être connecté pour pouvoir afficher son fil d'actualités");
		}
		myFrame.setPreferredSize(new Dimension(700, 500));
		myFrame.setContentPane(new MessageView(currentUser));

		// Display the window.
		myFrame.pack();
		myFrame.setVisible(true);
	}

	/**
	 * Crée et affiche la fenêtre d'édition de message pour l'utilisateur connecté.
	 * 
	 * throws IllegalStateException si aucun utilisateur n'est actuellement connecté
	 * 
	 * @requires isUserLogged();
	 */
	public void createAndShowEditMessagePanel() {
		if (!isUserLogged()) {
			throw new IllegalStateException("Un utilisateur doit être connecté pour pouvoir poster des messages");
		}
		myFrame.setPreferredSize(new Dimension(500, 300));
		myFrame.setContentPane(new MessageEditView(currentUser));

		// Display the window.
		myFrame.pack();
		myFrame.setVisible(true);
	}

	/**
	 * Crée et affiche la fenêtre permettant de visualiser l'ensemble des
	 * utilisateurs et de gérer les abonnements de l'utilisateur courant.
	 * 
	 * throws IllegalStateException si aucun utilisateur n'est actuellement connecté
	 * 
	 * @requires isUserLogged();
	 */
	public void createAndShowFollowUserPanel() {
		if (!isUserLogged()) {
			throw new IllegalStateException("Un utilisateur doit être connecté pour pouvoir gérer les abonnements");
		}
		myFrame.setPreferredSize(new Dimension(400, 400));
		myFrame.setContentPane(new FollowUserView(currentUser));

		// Display the window.
		myFrame.pack();
		myFrame.setVisible(true);
	}

	/**
	 * Crée et affiche la fenêtre d'accueil de l'application permettant de se
	 * connecter ou de créer un compte utilisateur.
	 */
	@Override
	public void run() {
		createAndShowLoginPanel();
	}

}
