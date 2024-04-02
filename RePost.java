package social.model;

import java.time.Instant;

/**
 *@author ISSA DIABIRA 12109303
 *je déclare qu'il s'agit de mon propre travail
*/

public class RePost extends SimplePost {
    private User subPostAuthor;
    private Post subPost;

    public RePost(String text, User subPostAuthor, Post subPost) {
        super(text);
        if(subPostAuthor == null || subPost == null){
        	throw new NullPointerException();
        }
        this.subPostAuthor = subPostAuthor;
        this.subPost = subPost;
    }

   
    public String getText() {
        String originalText = super.getText(); // Texte du nouveau Post
        String authorName = subPostAuthor.getName(); // Nom de l'auteur du Post partagé"
        String subPostText = subPost.getText(); // Texte du Post partagé

        return String.format("%s\nShared by: %s\nOriginal Post: %s", originalText, authorName, subPostText);
    }
}

