package social.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.List;
import social.model.test.ListIterObserverAdapter;

/**
 *@author ISSA DIABIRA 12109303
 *je déclare qu'il s'agit de mon propre travail
*/
public class SimplePost implements Post {
    private String text;
    private Instant date;
    private Set<User> likers;

    public SimplePost(String text) {
    	if(text == null){
    		throw new NullPointerException();
    	}
        this.text = text;
        this.date = Instant.now();
        likers = new HashSet<User>();
    }

   
    public Instant getDate() {
        return date;
    }

   
    public String getText() {
        return text;
    }

   
    public int getLikeNumber() {
        return likers.size();
    }

    
    public boolean hasLikeFrom(User u) {
        return likers.contains(u);
    }

   
    public boolean addLikeFrom(User u) {
        if (u == null) {
            throw new NullPointerException("User cannot be null");
        }
        if(!(hasLikeFrom(u))){
		likers.add(u);
		return true;
	}
        return false;
    }

    
    public Set<User> getLikers() {
        return Collections.unmodifiableSet(likers);
    }

    
    public ListIterator<User> iterator() {
        List<User> unmodifiableList = Collections.unmodifiableList(new ArrayList<>(likers));
        return unmodifiableList.listIterator();
    }

   
    public int compareTo(Post p) {
    	if(p == null){
    		throw new NullPointerException();
    	}
        return this.getDate().compareTo(p.getDate());
    }

    
    public boolean isBefore(Post p) {
    	if(p == null){
    		throw new NullPointerException();
    	}
        return this.getDate().isBefore(p.getDate());
    }

    
    public boolean isAfter(Post p) {
    	if(p == null){
    		throw new NullPointerException();
    	}
        return this.getDate().isAfter(p.getDate());
    }

   
}

