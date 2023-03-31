package org.orm.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public @Data class Userr {
    private String cin;
    private String cne;
    private String name;
    private String email;
    private String lastName;
    private Integer age;

    private Filiere filiere;

    // many to many with filiere for test
    private List<Filiere> particapationFilieres;

    private List<Message> sentMessages;
    private List<Message> receivedMessages;
    private List<Notification> notifications;
    private List<Post> posts;
    private List<Commentaire> commentaires;
    private List<SubCommentaire> subCommentaires;
    private List<LignePostReaction> lignePostReactions;



    public Userr(String cin, String cne, String name, String email, String lastName, Integer age) {
        this.cin = cin;
        this.cne = cne;
        this.name = name;
        this.email = email;
        this.lastName = lastName;
        this.age = age;
    }

    public Userr(String cin, String cne, String name, String email, String lastName, Integer age, Filiere filiere) {
        this.cin = cin;
        this.cne = cne;
        this.name = name;
        this.email = email;
        this.lastName = lastName;
        this.age = age;

        this.filiere = filiere;
    }

    public Userr(String cin, String cne, String name, String email, String lastName, Integer age, Filiere filiere, List<Filiere> particapationFilieres) {
        this.cin = cin;
        this.cne = cne;
        this.name = name;
        this.email = email;
        this.lastName = lastName;
        this.age = age;

        this.filiere = filiere;
        this.posts = posts;
        this.particapationFilieres = particapationFilieres;
    }


}
