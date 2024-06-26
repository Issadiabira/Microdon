/**
*
*/
package social.model;



import java.util.Comparator;
import java.util.ListIterator;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import social.model.test.ListIterObserver;
import social.model.test.ListIterObserverAdapter;






/**
 *@author ISSA DIABIRA 12109303
 *je déclare qu'il s'agit de mon propre travail
 * Un ListIterator fusionnant plusieurs ExtendedListIterator en interdisant
 * toutes les opérations de modification (i.e. add, remove, set).
 *
 * Un FusionSortedIterator garantie que, si les ListIterator fusionnés sont
 * ordonnés, alors ce FusionSortedIterator sera également ordonné.
 *
 * Par défaut, l'ordre considéré est l'ordre naturel entre les éléments,
 * cependant un ordre alternatif peut-être spécifié à la création de l'instance.
 * 
 * @param <E> le type des éléments énumérés par cet itérateur
 * @param <I> le type des itérateurs fusionnés
 *
 * @model ListIterObserver<Post> iterModel = new
 *        ListIterObserverAdapter<Post>(self);
 * @invariant nextIndex() == previousIndex() + 1;
 * @invariant lastIndex() == nextIndex() || lastIndex() == previousIndex();
 * @invariant previousIndex() >= -1 && previousIndex() < iterModel.size());
 * @invariant nextIndex() >= 0 && nextIndex() <= iterModel.size());
 * @invariant lastIndex() >= -1 && lastIndex() < iterModel.size());
 * @invariant lastIndex() == -1 <==> lastIterator() == null;
 * @invariant !hasPrevious() <==> previousIndex() == -1;
 * @invariant !iterModel.contains(null);
 * @invariant iterModel.isSorted(comparator());
 * @invariant comparator() != null;
 * 
 * @author Marc Champesme
 * @since 2/08/2023
 * @version 8/12/2023
 *
 */
public class FusionSortedIterator<E extends Comparable<? super E>, I extends ExtendedListIterator<? extends E>>
		implements ListIterator<E> {
	private int previousIndex;
	private int nextIndex;
	private int lastIndex;
	private User lastUser;
	private List<Post> content;
	private ListIterObserver<Post> iterModel;
	private Comparator<? super Post> comparator;

	/**
	 * Initialise une instance permettant d'itérer selon l'ordre "naturel" sur tous
	 * les éléments des ListIterator de l'ensemble spécifié. Il s'agit donc d'une
	 * fusion de tous les ListIterator contenus dans l'ensemble spécifié. Les
	 * ListIterator spécifiés sont supposés ordonnés selon l'ordre "naturel" de
	 * leurs éléments.
	 *
	 * @param iters ensemble des ListIterator à fusionner
	 *
	 * @requires iters != null && !iters.contains(null);
	 * @ensures (\forall ListIterator<Post> iter; postIterators.contains(iter);
	 *          iterModel.containsAll(toList(iter)));
	 * @ensures iterModel.size() == (\sum ListIterator<Post> iter;
	 *          postIterators.contains(iter); size(iter));
	 * @ensures (\forall Post p; iterModel.contains(p); (\exists ListIterator<Post>
	 *          iter; postIterators.contains(iter); contains(iter, p)));
	 * @ensures !hasPrevious();
	 * @ensures lastIndex() == -1;
	 * @ensures lastIterator() == null;
	 * @ensures (\forall I iter; iters.contains(iter); !iter.hasPrevious() &&
	 *          iter.lastIndex() == -1);
	 * @ensures comparator() != null;
	 *
	 * @throws NullPointerException si l'ensemble spécifié est null ou contient null
	 */
	public FusionSortedIterator(Set<? extends I> iters) {
		if (iters == null || iters.contains(null)) {
            		throw new NullPointerException("L'ensemble spécifié est null ou contient null");
        	}

        	// Initialisation des variables de classe et des invariants
        	previousIndex = -1;
        	nextIndex = 0;
        	lastIndex = -1;
        	iterModel = new ListIterObserverAdapter<Post>((ListIterObserver<Post>)this); // À adapter selon le modèle utilisé
        	comparator = Comparator.naturalOrder(); // Par défaut, ordre naturel

        	content = new ArrayList<>(); // Initialisation de la liste pour stocker les éléments fusionnés

        	// Ajout des itérateurs spécifiés à la fusion
        	for (I iter : iters) {
            		while (iter.hasNext()) {
                		// Ajout des éléments à la liste content (à adapter selon le modèle utilisé)
                		content.add((Post)iter.next());
            		}
        	}

        	// Tri de la liste content selon le comparateur spécifié
        	content.sort((Comparator<Post>)comparator);
	}

	/**
	 * Initialise une instance permettant d'itérer sur tous les éléments des
	 * ListIterator de l'ensemble spécifié selon l'ordre spécifié. Il s'agit donc
	 * d'une fusion de tous les ListIterator contenus dans l'ensemble spécifié. les
	 * ListIterator contenus dans l'ensemble spécifié sont supposés ordonnés selon
	 * l'ordre induit par le Comparator spécifié.
	 *
	 *
	 * @param iters      ensemble des ListIterator à fusionner
	 * @param comparator le comparateur à utiliser
	 *
	 * @requires iters != null && !iters.contains(null);
	 * @requires comparator != null;
	 * @ensures comparator() != null;
	 * @ensures !hasPrevious();
	 * @ensures lastIndex() == -1;
	 * @ensures lastIterator() == null;
	 * @ensures (\forall I iter; iters.contains(iter); !iter.hasPrevious() &&
	 *          iter.lastIndex() == -1);
	 *
	 * @throws NullPointerException si l'ensemble spécifié est null ou contient
	 *                              null, ou si le Comparator spécifié est null
	 */
	public FusionSortedIterator(Set<? extends I> iters, Comparator<? super E> comparator) {
		this(iters); // Appel du premier constructeur

    		if (comparator == null) {
        		throw new NullPointerException("Le comparateur spécifié est null");
    		}

    		this.comparator = (Comparator<? super Post>)comparator; // Utiliser le comparateur spécifié
    		content.sort((Comparator<Post>)comparator);
	}

	/**
	 * (Re)Initialise ce ListIterateur pour le démarrage d'une nouvelle itération
	 * sur ses éléments.
	 * 
	 * @ensures !hasPrevious();
	 * @ensures previousIndex() == -1;
	 * @ensures nextIndex() == 0;
	 * @ensures lastIndex() == -1;
	 * @ensures lastIterator() == null;
	 */
	public void startIteration() {
		previousIndex = -1;
        	nextIndex = 0;
        	lastIndex = -1;
        	lastUser = null;
	}

	/**
	 * Renvoie le comparateur selon lequel les éléments de cet itérateur sont
	 * ordonnés.
	 * 
	 * @return le comparateur selon lequel les éléments de cet itérateur sont
	 *         ordonnés
	 * 
	 * @ensures \result != null;
	 * 
	 * @pure
	 */
	public Comparator<? super E> comparator() {
		return (Comparator<? super E>)this.comparator;
	}

	/**
	 * Renvoie l'itérateur ayant produit l'élément lors du dernier appel à next() ou
	 * previous().
	 * 
	 * @return l'itérateur ayant produit l'élément lors du dernier appel à next() ou
	 *         previous()
	 * 
	 * @pure
	 */
	public I lastIterator() {
		if (lastUser != null) {
            		return (I) lastUser.iterator(); // Utilise la méthode iterator() de la classe User
        	}
        	return null;
	}

	/**
	 * Renvoie l'index pour cet itérateur du dernier élément retourné par next() ou
	 * previous().
	 * 
	 * @return l'index du dernier élément retourné par next() ou previous()
	 * 
	 * @pure
	 */
	public int lastIndex() {
		return lastIndex;
	}

	/**
	 * Renvoie true s'il reste un élément après dans l'itération.
	 * 
	 * @return true s'il reste un élément après dans l'itération; false sinon
	 * 
	 * @ensures !\result <==> nextIndex() == iterModel.size();
	 * 
	 * @pure
	 */
	@Override
	public boolean hasNext() {
		return nextIndex < content.size();
	}

	/**
	 * Renvoie l'élément suivant et avance le curseur.
	 *
	 * @return l'élément suivant
	 *
	 * @throws NoSuchElementException si l'itérateur n'a pas d'élément suivant
	 *
	 * @requires hasNext();
	 * @ensures \result != null;
	 * @ensures lastIterator() != null;
	 * @ensures \result.equals(lastIterator().getPrevious());
	 * @ensures \result.equals(iterModel.get(previousIndex()))
	 * @ensures \result.equals(\old(iterModel.get(nextIndex())));
	 * @ensures \old(hasPrevious()) ==> comparator().compare(iterModel.get(\old(previousIndex())), \result) <= 0;
	 * @ensures hasPrevious();
	 * @ensures previousIndex() == \old(nextIndex());
	 * @ensures nextIndex() == \old(nextIndex() + 1);
	 * @ensures lastIndex() == \old(nextIndex());
	 */
	@Override
	public E next() {
		if (!hasNext()) {
            		throw new NoSuchElementException("Pas d'élément suivant");
        	}
        	previousIndex = nextIndex;
        	nextIndex++;
        	lastIndex = previousIndex;
        	// Retourne l'élément suivant depuis la liste content (à adapter selon le modèle utilisé)
        	return (E) content.get(previousIndex);
	}

	/**
	 * Renvoie true s'il y a un élément précédent dans l'itération.
	 * 
	 * @return true s'il y a un élément précédent dans l'itération; false sinon
	 * 
	 * @ensures !\result <==> previousIndex() == -1;
	 *
	 * @pure
	 */
	@Override
	public boolean hasPrevious() {
		return  previousIndex >= 0;
	}

	/**
	 * Renvoie l'élément précédent et recule le curseur.
	 *
	 * @return l'élément précédent
	 *
	 * @throws NoSuchElementException si l'itérateur n'a pas d'élément précédent
	 *
	 * @requires hasPrevious();
	 * @ensures hasNext();
	 * @ensures \result != null;
	 * @ensures lastIterator() != null;
	 * @ensures \result.equals(lastIterator().getNext());
	 * @ensures \result.equals(\old(iterModel.get(previousIndex())));
	 * @ensures \result.equals(iterModel.get(nextIndex()));
	 * @ensures \old(hasNext()) ==> comparator().compare(\result, iterModel.get(\old(nextIndex())) <= 0;
	 * @ensures previousIndex() == \old(previousIndex()) - 1;
	 * @ensures nextIndex() == \old(nextIndex()) - 1;
	 * @ensures lastIndex() == \old(previousIndex());
	 */
	@Override
	public E previous() {
		if (!hasPrevious()) {
            		throw new NoSuchElementException("Pas d'élément précédent");
        	}
        	nextIndex = previousIndex;
        	previousIndex--;
        	lastIndex = previousIndex;
        	// Retourne l'élément précédent depuis la liste content (à adapter selon le modèle utilisé)
        	return (E) content.get(previousIndex);
	}

	/**
	 * Renvoie l'index de l'élément suivant dans l'itération. Renvoie le nombre
	 * total d'élément dans l'itération s'il n'y a pas d'élément suivant.
	 * 
	 * @return l'index de l'élément suivant dans l'itération
	 * 
	 * @ensures hasNext() <==> \result >= 0 && \result < iterModel.size();
	 * @ensures !hasNext() <==> \result == iterModel.size();
	 * 
	 * @pure
	 */
	@Override
	public int nextIndex() {
		return nextIndex;
	}

	/**
	 * Renvoie l'index de l'élément précédent dans l'itération. Renvoie -1 s'il n'y
	 * a pas d'élément précédent.
	 * 
	 * @return l'index de l'élément précédent dans l'itération
	 * 
	 * @ensures hasPrevious() ==> \result >= 0;
	 * @ensures !hasPrevious() <==> \result == -1;
	 *
	 * @pure
	 */
	@Override
	public int previousIndex() {
		return previousIndex;
	}

	/**
	 * Opération non supportée.
	 * 
	 * @throws UnsupportedOperationException toujours
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Opération non supportée");	
	}

	/**
	 * Opération non supportée.
	 * 
	 * @throws UnsupportedOperationException toujours
	 */
	@Override
	public void set(E e) {
		throw new UnsupportedOperationException("Opération non supportée");
	}

	/**
	 * Opération non supportée.
	 * 
	 * @throws UnsupportedOperationException toujours
	 */
	@Override
	public void add(E e) {
		throw new UnsupportedOperationException("Opération non supportée");
	}

}
