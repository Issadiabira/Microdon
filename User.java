/**
 * 
 */
package social.model;

import java.time.Instant;
import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;


/**
 *@author ISSA DIABIRA 
 *je déclare qu'il s'agit de mon propre travail
 * Un utilisateur du réseau social Microdon. Chaque instance possède un nom
 * (getName()) unique, dans ce but chaque instance créée est mémorisée dans un
 * attribut static et le constructeur garantit qu'une nouvelle instance ne
 * peut-être créée avec un nom déjà porté par une instance pré-existante. En
 * plus de son nom, chaque User est caractérisé par son mot de passe, la liste
 * de ses messages, l'ensemble des User auxquels il est abonné et l'ensemble des
 * User qui sont abonnés à son compte (i.e. ses followers).
 * 
 * Un ensemble complet de méthodes permet d'utiliser un User comme un
 * ListIterator afin d'effectuer des itérations bidirectionnelles sur la liste
 * des Post de l'User: ce sont les méthodes: startIteration(), hasNext(),
 * nextIndex(), next(), getNext(), hasPrevious(), previousIndex(), previous(),
 * getPrevious() et lastIndex().
 * 
 * <pre>{@code
 * aUser.startIteration(); // Initialisation pour une nouvelle itération
 * // Affichage des Post du plus récent au plus ancien
 * while (aUser.hasNext()) {
 * 	System.out.println("Post suivant (plus ancien):" + aUser.next());
 * }
 * // Affichage des Post du plus ancien au plus récent
 * while (aUser.hasPrevious()) {
 * 	System.out.println("Post précédent (plus récent):" + aUser.previous());
 * }
 * }</pre>
 * 
 * @invariant User.getAllUser() != null;
 * @invariant User.getAllUser().contains(this);
 * @invariant User.getUser(this.getName()) == this;
 * @invariant User.hasUser(this.getName());
 * @invariant User.isValidUserName(this.getName());
 * @invariant User.isValidPassword(this.getPassword();
 * @invariant getRegistrationDate() != null;
 * @invariant getSubscriptions() != null && !getSubscriptions().contains(null);
 * @invariant getSubscriptions().size() < User.getAllUser().size();
 * @invariant getFollowers() != null && !getFollowers().contains(null);
 * @invariant getFollowers().size() < User.getAllUser().size();
 * @invariant getPosts() != null && !getPosts().contains(null);
 * @invariant newsFeed() != null;
 * @invariant iterator() != null;
 * @invariant previousIndex() >= -1 && previousIndex() < getPostNb();
 * @invariant nextIndex() >= 0 && nextIndex() <= getPostNb();
 * @invariant lastIndex() >= -1 && lastIndex() < getPostNb();
 * @invariant !hasPrevious() <==> previousIndex() == -1;
 * @invariant !hasNext() <==> nextIndex() == getPostNb();
 * 
 * @invariant nextIndex() == previousIndex() + 1;
 * @invariant lastIndex() == nextIndex() || lastIndex() == previousIndex();
 * 
 * @invariant ListIterObserver<Post> \model = new ListIterObserverAdapter<Post>(this);
 * @invariant \model.toList().equals(getPosts());
 * @invariant \model.isSorted(Comparator.reverseOrder());
 * 
 * @author Marc Champesme
 * @since 2/08/2023
 * @version 07/12/2023
 * 
 */
public class User implements Iterable<Post>, ExtendedListIterator<Post> {
	
	private String name;
	private String password;
	private Instant registrationDate;
	private Set <User> subscriptions;
	private Set <User> followers;
	private List <Post> userPosts;
	private int lastIndex;
	private int previousIndex;
	private int nextIndex;


	/**
	 * Renvoie une Collection non modifiable contenant toutes les instances créées
	 * de cette classe.
	 * 
	 * @return une Collection de toutes les instances créées
	 * 
	 * @ensures \result != null;
	 * 
	 * @pure
	 */
	private static List<User> allUsers = new ArrayList<>();
	public static Collection<User> getAllUser() {
		return List.copyOf(allUsers);
	}

	/**
	 * Renvoie l'instance de User possédant le nom spécifié ou null si une telle
	 * instance n'existe pas.
	 * 
	 * @param userName le nom de l'User cherché
	 * 
	 * @return L'instance possédant le nom spécifié ou null si une telle instance
	 *         n'existe pas.
	 * 
	 * @ensures (userName == null || !isValidUserName(userName)) ==> (\result ==
	 *          null);
	 * @ensures (\result != null) <==> hasUser(userName);
	 * @ensures (\result != null) ==> \result.getName().equals(userName);
	 * @ensures (\result != null) <==> getAllUser().contains(\result);
	 * 
	 * @pure
	 */
	public static User getUser(String userName) {
		if (userName == null || !isValidUserName(userName)) {
        		return null;
    		}

    		for (User user : allUsers) {
        		if (user.getName().equals(userName)) {
            			return user;
        		}
    		}
    		return null;
	}

	/**
	 * Renvoie true si une instance d'User portant le nom spécifié a été créée.
	 * 
	 * @param userName le nom dont cherche à savoir s'il est porté par un User
	 *                 existant
	 * @return true si une instance d'User portant ce nom a été créée; false sinon
	 * 
	 * @ensures (userName == null || !isValidUserName(userName)) ==> !\result;
	 * @ensures \result <==> getUser(userName) != null;
	 * @ensures \result <==> (\exists User u; getAllUser().contains(u);
	 *          u.getName().equals(userName));
	 * 
	 * @pure
	 */
	public static boolean hasUser(String userName) {
		for (User user : allUsers) {
        		if (user.getName().equals(userName)) {
            			return true;
        		}
    		}
    		return false;
	}

	/**
	 * Renvoie true si la chaîne de caractères spécifiée peut être acceptée comme
	 * nom d'un User.
	 * 
	 * @param name la chaîne de caractères dont on veut tester la validité.
	 * 
	 * @return true si la chaîne de caractères spécifiée peut être acceptée comme
	 *         nom d'un User; false sinon.
	 * 
	 * @ensures \result <==> name != null && !name.isBlank();
	 * 
	 * @pure
	 */
	public static boolean isValidUserName(String name) {
		return name != null && !name.isBlank();
	}

	/**
	 * Renvoie true si la chaîne de caractères spécifiée peut être acceptée comme
	 * mot de passe d'un User.
	 * 
	 * @param pass la chaîne de caractères dont on veut tester la validité.
	 * 
	 * @return true si la chaîne de caractères spécifiée peut être acceptée comme
	 *         mot de passe d'un User; false sinon.
	 * 
	 * @ensures \result <==> pass != null && !pass.isBlank();
	 * 
	 * @pure
	 */
	public static boolean isValidPassword(String pass) {
		return pass != null && !pass.isBlank();
	}

	/**
	 * Initialise une nouvelle instance ayant les nom et mot de passe spécifiés. Le
	 * nom spécifié ne doit pas déjà être le nom d'une instance existante. La
	 * nouvelle instance est mémorisée dans une varaible static de cette classe. La
	 * date d'inscription du nouvel utilisateur est la date au moment de l'exécution
	 * de ce constructeur.
	 * 
	 * @param userName nom de la nouvelle instance de User
	 * @param password mot de passe de la nouvelle instance de User
	 * 
	 * @throws NullPointerException     si le nom ou le mot de passe spécifié est
	 *                                  null
	 * @throws IllegalArgumentException si le nom ou le mot de passe spécifié n'est
	 *                                  pas valide ou si le nom spécifié a déjà été
	 *                                  donné à une autre instance
	 * 
	 * @old oldDate = Instant.now();
	 * @requires userName != null && password != null;
	 * @requires User.isValidUserName(userName) && User.isValidPassword(password);
	 * @requires !User.hasUser(userName);
	 * @ensures getName().equals(userName);
	 * @ensures getPassword().equals(password);
	 * @ensures getSubscriptionDate() != null;
	 * @ensures oldDate.compareTo(getSubscriptionDate()) <= 0;
	 * @ensures getSubscriptionDate().compareTo(Instant.now()) <= 0;
	 * @ensures getSubscriptions().isEmpty();
	 * @ensures getPosts() != null;
	 * @ensures getPosts().isEmpty();
	 * @ensures getFollowers() != null;
	 * @ensures getFollowers().isEmpty();
	 * @ensures User.getAllUser().contains(this);
	 * @ensures User.getUser(userName) == this;
	 * @ensures User.hasUser(userName);
	 */
	public User(String userName, String password) {
		if(userName == null || password == null){
        		throw new NullPointerException();
        	}
		if (!isValidUserName(userName) || !isValidPassword(password) ||hasUser(userName)) {
            		throw new IllegalArgumentException("Invalid username or password");
        	}
        	this.name = userName;
        	this.password = password;
        	this.registrationDate = Instant.now();
        	this.subscriptions = new HashSet<>();
        	this.followers = new HashSet<>();
        	this.userPosts = new ArrayList<>();
        	allUsers.add(this);
        	
	}

	/**
	 * Renvoie le nom de cet utilisateur.
	 * 
	 * @return le nom de cet utilisateur
	 * 
	 * @pure
	 */
	public String getName() {
		return name;
	}

	/**
	 * Renvoie le mot de passe de cet utilisateur.
	 * 
	 * @return le mot de passe de cet utilisateur
	 * 
	 * @pure
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Renvoie une instance de Instant représentant la date d'inscription de cet
	 * utilisateur.
	 * 
	 * @return la date d'inscription de cet utilisateur
	 * 
	 * @pure
	 */
	public Instant getRegistrationDate() {
		return registrationDate;
	}

	/**
	 * Renvoie une vue non modifiable de l'ensemble des utilisateurs auxquels cet
	 * utilisateur s'est abonné.
	 * 
	 * @return l'ensemble des utilisateurs auxquels cet utilisateur s'est abonné
	 * 
	 * @ensures \result != null;
	 * @ensures User.getAllUser().containsAll(\result);
	 * @ensures (\forall User u; \result.contains(u); hasSubscritionTo(u));
	 * @ensures (\forall User u; \result.contains(u); u.hasFollower(this));
	 * 
	 * @pure
	 */
	public Set<User> getSubscriptions() {
		return Collections.unmodifiableSet(new HashSet<>(subscriptions));
	}

	/**
	 * Renvoie une vue non modifiable de l'ensemble des utilisateurs abonnés à cet
	 * utilisateur (ses "followers").
	 * 
	 * @return l'ensemble des utilisateurs abonnés à cet utilisateur
	 * 
	 * @ensures \result != null;
	 * @ensures User.getAllUser().containsAll(\result);
	 * @ensures (\forall User u; \result.contains(u); this.hasFollower(u));
	 * @ensures (\forall User u; \result.contains(u); u.hasSubscriptionTo(this));
	 * 
	 * @pure
	 */
	public Set<User> getFollowers() {
		return Collections.unmodifiableSet(followers);
	}

	/**
	 * Ajoute l'utilisateur spécifié à l'ensemble des souscriptions de cet
	 * utilisateur. Un utilisateur ne peut souscrire à lui-même. Renvoie true si
	 * l'utilisateur spécifié ne faisait pas déjà partie des souscriptions de cet
	 * utilisateur. Cet utilisateur est ajouté à l'ensemble des followers de
	 * l'utilisateur spécifié.
	 * 
	 * @param u l'utilisateur auquel cet utilisateur veut s'abonner
	 * @return true si l'utilisateur spécifié ne faisait pas déjà partie des
	 *         abonnements de cet utilisateur; false sinon
	 * 
	 * @throws NullPointerException     si l'argument spécifié est null
	 * @throws IllegalArgumentException si l'argument spécifié est cet utilisateur
	 * 
	 * @requires u != null;
	 * @requires !this.equals(u);
	 * @ensures hasSubscriptionTo(u);
	 * @ensures u.hasFollower(this);
	 * @ensures \result <==> !\old(hasSubscriptionTo(u));
	 * @ensures \result <==> (getSubscriptionNb() == \old(getSubscriptionNb() + 1));
	 * @ensures \result <==> (u.getFollowerNb() == \old(u.getFollowerNb() + 1));
	 * @ensures !\result <==> (getSubscriptionNb() == \old(getSubscriptionNb()));
	 * @ensures !\result <==> (u.getFollowerNb() == \old(u.getFollowerNb()));
	 * @ensures !\result <==> (getSubscriptions().equals(\old(getSubscriptions()));
	 * @ensures !\result <==> (u.getFollowers().equals(\old(u.getFollowers()));
	 */
	public boolean addSubscriptionTo(User u) {
		if (u == null) {
        		throw new NullPointerException("L'utilisateur spécifié est null.");
    		}
    
    		if (this.equals(u)) {
        		throw new IllegalArgumentException("Impossible de s'abonner à soi-même.");
    		}

    		boolean added = subscriptions.add(u);
    		boolean dejaSuivi = u.followers.contains(this);

    		if (!dejaSuivi) {
        		u.followers.add(this);
    		}

    		return added;
	}

	/**
	 * Retire l'utilisateur spécifié de l'ensemble des abonnements de cet
	 * utilisateur. Retire également cet utilisateur des followers de l'utilisateur
	 * spécifié. Renvoie true si l'utilisateur spécifié était précédemment un
	 * abonnement de cet utilisateur.
	 * 
	 * @param u l'utilisateur à retirer de l'ensemble des abonnements de cet
	 *          utilisateur.
	 * 
	 * @return true si l'utilisateur spécifié était précédemment un abonnement de
	 *         cet utilisateur; false sinon
	 * 
	 * @ensures !hasSubscriptionTo(u);
	 * @ensures u == null || !u.hasFollower(this);
	 * @ensures \result <==> \old(hasSubscriptionTo(u));
	 * @ensures \result <==> u != null && \old(u.hasFollower(this));
	 * @ensures \result <==> (getSubscriptionNb() == \old(getSubscriptionNb() - 1));
	 * @ensures \result <==> u != null && (u.getFollowerNb() ==
	 *          \old(u.getFollowerNb()) - 1);
	 * @ensures !\result <==> (getSubscriptionNb() == \old(getSubscriptionNb()));
	 * @ensures !\result <==> u == null || (u.getFollowerNb() ==
	 *          \old(u.getFollowerNb()));
	 * @ensures !\result <==> (getSubscriptions().equals(\old(getSubscriptions()));
	 * @ensures !\result <==> u == null ||
	 *          (u.getFollowers().equals(\old(u.getFollowers()));
	 */
	public boolean removeSubscriptionTo(User u) {
		if (u == null || !hasSubscriptionTo(u)) {
        		return false;
    		}
    
    		boolean removed = getSubscriptions().remove(u);
    		if (removed) {
       			u.getFollowers().remove(this);
    		}
    
    		return removed;
	}

	/**
	 * Renvoie true si l'utilisateur spécifié fait partie des abonnements de cet
	 * utilisateur.
	 * 
	 * @param u l'utilisateur dont on cherche à savoir s'il fait partie des
	 *          abonnements de cet utilisateur
	 * 
	 * @return true si l'utilisateur spécifié fait partie des abonnements de cet
	 *         utilisateur; false sinon
	 * 
	 * @ensures \result <==> getSubscriptions().contains(u);
	 * @ensures \result <==> u != null && u.hasFollower(this);
	 * 
	 * @pure
	 */
	public boolean hasSubscriptionTo(User u) {
		if (u == null) {
        		return false;
    		}
    		return subscriptions.contains(u);
	}

	/**
	 * Renvoie true si l'utilisateur spécifié fait partie des followers de cet
	 * utilisateur.
	 * 
	 * @param u l'utilisateur dont on cherche à savoir s'il fait partie des
	 *          followers de cet utilisateur
	 * 
	 * @return true si l'utilisateur spécifié fait partie des followers de cet
	 *         utilisateur; false sinon
	 * 
	 * @ensures \result <==> getFollowers().contains(u);
	 * @ensures \result <==> u != null && u.hasSubscriptionTo(this);
	 * 
	 * @pure
	 */
	public boolean hasFollower(User u) {
		if (u == null) {
        		return false;
    		}
    		return followers.contains(u);
	}

	/**
	 * Renvoie le nombre d'utilisateurs auxquels cet utilisateur est abonné.
	 * 
	 * @return le nombre d'utilisateurs auxquels cet utilisateur est abonné
	 * 
	 * @ensures \result == getSubscriptions().size();
	 * 
	 * @pure
	 */
	public int getSubscriptionNb() {
		return subscriptions.size();
	}

	/**
	 * Renvoie le nombre d'utilisateurs abonnés à cet utilisateur.
	 * 
	 * @return le nombre d'utilisateurs abonnés à cet utilisateur
	 * 
	 * @ensures \result == getFollowers().size();
	 * 
	 * @pure
	 */
	public int getFollowerNb() {
		return followers.size();
	}

	/**
	 * Renvoie le ième plus récent Post de ce User.
	 * 
	 * @param i index du Post cherché
	 * 
	 * @return le ième plus récent Post de ce User
	 * 
	 * @throws IndexOutOfBoundsException si l'index spécifié est < 0 ou >=
	 *                                   getPostNb()
	 * 
	 * @requires i >= 0 && i < getPostNb();
	 * @ensures \result.equals(getPosts().get(i));
	 * 
	 * @pure
	 */
	public Post getPost(int i) {
		if (i < 0 || i >= getPostNb()) {
        		throw new IndexOutOfBoundsException("L'index spécifié est invalide");
    		}
    
    		return getPosts().get(i);
	}

	/**
	 * Renvoie une vue non modifiable de la liste des posts de cet utilisateur. La
	 * liste renvoyée est triée selon leurs dates, les messages les plus récents
	 * étant en tête de liste.
	 * 
	 * @return une vue non modifiable de la liste des posts de cet utilisateur
	 * 
	 * @ensures \result != null;
	 * @ensures (\forall int i, j; i >= 0 && i < j && j < \result.size();
	 *          \result.get(i).isAfter(\result.get(j)));
	 * 
	 * @pure
	 */
	public List<Post> getPosts() {
		List<Post> sortedPosts = new ArrayList<>(userPosts);
        	sortedPosts.sort((p1, p2) -> p2.getDate().compareTo(p1.getDate()));
        	return Collections.unmodifiableList(sortedPosts);
	}

	/**
	 * Ajoute le Post spécifié en tête de la liste des posts de cet utilisateur.
	 * 
	 * @param p le nouveau post de cet utilisateur
	 * 
	 * @return le nouveau Post de cet utilisateur
	 * 
	 * @throws NullPointerException     si l'argument spécifié est null
	 * @throws IllegalArgumentException si le Post spécifié est antérieur à un des
	 *                                  posts de cet utilisateur
	 * 
	 * @requires p != null;
	 * @requires getPostNb() > 0 ==> p.isAfter(getPost(0));
	 * @ensures \result == p;
	 * @ensures getPost(0).equals(\result);
	 * @ensures getPostNb() == \old(getPostNb()) + 1;
	 * @ensures \old(lastIndex()) == -1 ==> nextIndex() == \old(nextIndex());
	 * @ensures \old(lastIndex()) == -1 ==> lastIndex() == -1;
	 * @ensures \old(lastIndex()) > -1 ==> nextIndex() == \old(nextIndex()) + 1;
	 * @ensures \old(lastIndex()) > -1 ==> lastIndex() == \old(lastIndex()) + 1;
	 */
	public Post addPost(Post p) {
		if (p == null) {
            		throw new NullPointerException("Le Post spécifié est null");
        	}

        	if (getPostNb() > 0 && !p.isAfter(getPost(0))) {
            		throw new IllegalArgumentException("Le Post spécifié est antérieur à un des posts de cet utilisateur");
        	}
		userPosts.add(0, p);
		if (lastIndex >= 0) {
			nextIndex++;
			lastIndex++;
    		}
    		return p;
	}

	/**
	 * Renvoie le nombre de Post de cet utilisateur.
	 * 
	 * @return le nombre de Post de cet utilisateur
	 * 
	 * @ensures \result == getPosts().size();
	 * 
	 * @pure
	 */
	public int getPostNb() {
		return userPosts.size();
	}

	/**
	 * Renvoie un NewsFeed de cet utilisateur. Ce NewsFeed interdit toute
	 * modification, il est obtenu en fusionnant les listes de Post de cet
	 * utilisateur et des utilisateurs auxquels il est abonné, il utilise les
	 * itérateurs natifs des User. L'appel de cette méthode réinitialise les
	 * itérateurs natifs de ce User ainsi que ceux des User auxquels il est abonné,
	 * comme le ferait un appel à startIteration(). Ce NewsFeed énumère les Post par
	 * ordre de date du plus récent au plus ancien.
	 * 
	 * @return un NewsFeed pour cet utilisateur
	 * 
	 * @ensures \result != null;
	 * @ensures lastIndex() == -1 && nextIndex() == 0 && previousIndex() == -1;
	 * @ensures (\forall User u; hasSubscriptionTo(u); u.lastIndex() == -1 &&
	 *          u.nextIndex() == 0 && u.previousIndex() == -1);
	 * @ensures \resmodel = new ListIterObserverAdapter(\result);
	 * @ensures !\resmodel.contains(null);
	 * @ensures \resmodel.isSorted(Comparator.reverseOrder());
	 * @ensures \resmodel.containsAll(getPosts());
	 * @ensures (\forall User u; hasSubscriptionTo(u);
	 *          \resmodel.containsAll(getPosts()));
	 * 
	 */
	public FusionSortedIterator<Post, User> newsFeed() {
    		Set<ExtendedListIterator<Post>> postIterators = new HashSet<>();
    
    		// Ajouter l'itérateur de la liste de Post de cet utilisateur
    		postIterators.add((ExtendedListIterator<Post>) iterator());
    
    		// Ajouter les itérateurs des utilisateurs auxquels cet utilisateur est abonné
    		for (User u : subscriptions) {
        		postIterators.add((ExtendedListIterator<Post>) u.iterator());
    		}
    
    		FusionSortedIterator<Post, ExtendedListIterator<Post>> fusionIterator = new FusionSortedIterator<>(postIterators);
    
    		// Réinitialiser les itérateurs natifs de cet utilisateur et de ceux auxquels il est abonné
    		startIteration();
    		for (User u : subscriptions) {
        		u.startIteration();
    		}
    
    		return (FusionSortedIterator<Post, User>) (FusionSortedIterator) fusionIterator;
	}



	/**
	 * Renvoie un Iterator sur les Post de cet utilisateur. Cet Iterator interdit
	 * toute modification et permet d'effectuer une itération indépendament de
	 * l'itérateur intégré. L'usage de l'itérateur renvoyé ne mofifie pas l'état de
	 * cette instance en tant qu'itérateur tel qu'il peut être observé à l'aide des
	 * méthodes de cette classe (notamment hasPrevious(), hasNext(), nextIndex(),
	 * ...).
	 * 
	 * @return un Iterator sur les Post de cet utilisateur
	 * 
	 * @ensures \result != null;
	 * @ensures \resmodel = new ListIterObserverAdapter(\result);
	 * @ensures !\resmodel.contains(null);
	 * @ensures \resmodel.isSorted(Comparator.reverseOrder());
	 * @ensures \resmodel.toList().equals(getPosts());
	 * 
	 * @pure
	 */
	
	public ListIterator<Post> iterator() {
		return Collections.unmodifiableList(userPosts).listIterator();
	}

	/**
	 * Initialise ce User pour le démarrage d'une nouvelle itération sur les Post de
	 * ce User. Cette itération s'effectue à partir du Post le plus récent, de sorte
	 * que chaque appel à next() renvoie un Post plus ancien.
	 * 
	 * @ensures !hasPrevious();
	 * @ensures previousIndex() == -1;
	 * @ensures nextIndex() == 0;
	 * @ensures lastIndex() == -1;
	 */

	public void startIteration() {
		previousIndex = -1;
		nextIndex = 0;
		lastIndex = -1;
	}

	/**
	 * Renvoie true si ce User possède un Post plus ancien pour l'itération en
	 * cours.
	 * 
	 * @return true si ce User possède un Post plus ancien pour l'itération en cours
	 * 
	 * @ensures \result <==> nextIndex() < getPostNb();
	 * 
	 * @pure
	 */
	public boolean hasNext() {
		return nextIndex() < getPostNb();
	}

	/**
	 * Renvoie le Post suivant (plus ancien) dans l'itération en cours et avance
	 * d'un élément dans l'itération.
	 * 
	 * @return le Post suivant (plus ancien) dans l'itération en cours
	 * 
	 * @throws NoSuchElementException si hasNext() est false
	 * 
	 * @requires hasNext();
	 * @ensures \result.equals(\old(getNext()));
	 * @ensures nextIndex() == \old(nextIndex()) + 1;
	 * @ensures previousIndex() == \old(previousIndex()) + 1;
	 * @ensures previousIndex() == \old(nextIndex());
	 * @ensures lastIndex() == \old(nextIndex());
	 * @ensures lastIndex() == previousIndex();
	 */
	public Post next() {
		if (!hasNext()) {
        		throw new NoSuchElementException("Il n'y a pas de Post suivant.");
    		}
    		Post pos = getPost(nextIndex);
		lastIndex = nextIndex;
		previousIndex++;
		nextIndex++;
		return pos;
	}

	/**
	 * Renvoie le Post suivant (plus ancien) dans l'itération en cours sans modifié
	 * l'état de ce User.
	 * 
	 * @return le Post suivant (plus ancien) dans l'itération en cours
	 * 
	 * @throws NoSuchElementException si hasNext() est false
	 * 
	 * @requires hasNext();
	 * @ensures \result != null;
	 * @ensures \result.equals(getPost(nextIndex()));
	 * @ensures hasPrevious() ==> getPrevious().isAfter(\result);
	 * 
	 * @pure
	 */
	public Post getNext() {
		if (!hasNext()) {
        		throw new NoSuchElementException("Il n'y a pas de Post suivant.");
    		}
    		return getPost(nextIndex);
	}

	/**
	 * Renvoie true si ce User possède un Post plus récent pour l'itération en
	 * cours.
	 * 
	 * @return true si ce User possède un Post plus récent pour l'itération en cours
	 * 
	 * @ensures \result <==> previousIndex() >= 0;
	 * @ensures !\result <==> previousIndex() == -1;
	 * 
	 * @pure
	 */
	
	public boolean hasPrevious() {
		return previousIndex() >= 0;
	}

	/**
	 * Renvoie le Post précedent (plus récent) dans l'itération en cours et recule
	 * d'un élément dans l'itération.
	 * 
	 * @return le Post précedent (plus récent) dans l'itération en cours
	 * 
	 * @throws NoSuchElementException si hasPrevious() est false
	 * 
	 * @requires hasPrevious();
	 * @ensures \result.equals(\old(getPrevious()));
	 * @ensures nextIndex() == \old(nextIndex()) - 1;
	 * @ensures previousIndex() == \old(previousIndex()) - 1;
	 * @ensures nextIndex() == \old(previousIndex());
	 * @ensures lastIndex() == \old(previousIndex());
	 * @ensures lastIndex() == nextIndex();
	 */

	public Post previous() {
		if (!hasPrevious()) {
        		throw new NoSuchElementException("Il n'y a pas de Post précédent.");
    		}
    		Post previousPost = getPost(previousIndex);
		lastIndex = previousIndex;
		nextIndex--;
		previousIndex--;
		return previousPost;
	}

	/**
	 * Renvoie le Post précédent (plus récent) dans l'itération en cours sans
	 * modifié l'état de ce User.
	 * 
	 * @return le Post précédent (plus récent) dans l'itération en cours
	 * 
	 * @throws NoSuchElementException si hasPrevious() est false
	 * 
	 * @requires hasPrevious();
	 * @ensures \result != null;
	 * @ensures \result.equals(getPost(previousIndex()));
	 * @ensures hasNext() ==> \result.isAfter(getNext());
	 * 
	 * @pure
	 */
	
	public Post getPrevious() {
		if (!hasPrevious()) {
        		throw new NoSuchElementException("Il n'y a pas de Post précédent.");
    		}

    		return getPost(previousIndex);
	}

	/**
	 * Renvoie l'index du Post qui sera renvoyé par le prochain appel à next(). Si
	 * l'itération est arrivée à la fin la valeur renvoyée est getPostNb().
	 * 
	 * @return l'index du Post qui sera renvoyé par le prochain appel à next(); ou
	 *         getPostNb()
	 * 
	 * @ensures \result == getPostNb() <==> !hasNext();
	 * @ensures hasNext() <==> \result >= 0 && \result < getPostNb();
	 * 
	 * @pure
	 */

	public int nextIndex() {
		return nextIndex;
	}

	/**
	 * Renvoie l'index du Post qui sera renvoyé par le prochain appel à previous().
	 * Si l'itération est arrivée au début la valeur renvoyée est -1.
	 * 
	 * @return l'index du Post qui sera renvoyé par le prochain appel à previous();
	 *         ou -1
	 * 
	 * @ensures \result == -1 <==> !hasPrevious();
	 * @ensures hasPrevious() <==> \result >= 0 && \result < getPostNb();
	 * 
	 * @pure
	 */

	public int previousIndex() {
		return previousIndex;
	}

	/**
	 * Renvoie l'index du Post renvoyé par le dernier appel à previous() ou next().
	 * Si previous() et next() n'ont pas été appelée depuis le dernier appel à
	 * StartIteration() (ou l'appel du constructeur), renvoie -1.
	 * 
	 * @return l'index du Post renvoyé par le dernier appel à previous() ou next()
	 * 
	 * @ensures \result == nextIndex() || \result == previousIndex();
	 * 
	 * @pure
	 */
	public int lastIndex() {
		return lastIndex;
	}

	/**
	 * Opération non supportée.
	 * 
	 * @throws UnsupportedOperationException toujours
	 */

	public void remove() {
		throw new UnsupportedOperationException("Opération non supportée.");
	}

	/**
	 * Opération non supportée.
	 * 
	 * @throws UnsupportedOperationException toujours
	 */

	public void set(Post e) {
		throw new UnsupportedOperationException("Opération non supportée.");
	}

	/**
	 * Opération non supportée.
	 * 
	 * @throws UnsupportedOperationException toujours
	 */
	
	public void add(Post e) {
		throw new UnsupportedOperationException("Opération non supportée.");
	}

}
