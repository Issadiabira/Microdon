Ce projet consiste à améliorer une version du réseau social Microdon en implémentant des fonctionnalités supplémentaires par rapport à la version précédente. Les interfaces Post et ExtendedListIterator sont fournies et ne doivent pas être modifiées.

Travail demandé :

   ->Implémenter les classes SimplePost et RePost, toutes deux héritant de l'interface Post, selon les spécifications suivantes :
        ->SimplePost possède un constructeur unique prenant en paramètre une chaîne de caractères représentant le texte du post.
        
        ->RePost, sous-classe de SimplePost, permet de partager des posts. Son constructeur prend en paramètre le texte du nouveau post, l'auteur du post partagé et le post partagé lui-même. La méthode getText() de RePost renvoie une chaîne de caractères contenant le texte du nouveau post, le nom de l'auteur du post partagé et le texte du post partagé.
    ->Compléter les classes User et FusionSortedIterator, sans ajouter de nouvelles méthodes ou constructeurs, ni modifier les en-têtes existants.

Fichiers à déposer :

    1.SimplePost.java
    2.RePost.java
    3.Un rapport de test nommé TEST-junit-jupiterPost.xml généré avec Junit à l'aide de la classe de test PostTest.
    4.User.java et un rapport de test nommé TEST-junit-jupiterUser.xml généré avec Junit à l'aide de la classe de test PostUser.
    5.FusionSortedIterator.java et un rapport de test nommé TEST-junit-jupiterFusionSortedIterator.xml généré avec Junit à l'aide de la classe de test PostTest.
    6.Tout fichier source supplémentaire nécessaire pour implémenter les classes, uniquement si jugé nécessaire.
