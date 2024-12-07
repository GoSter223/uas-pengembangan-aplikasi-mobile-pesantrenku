package com.example.pesanternku;

public class ItemComment {
    String username;
    String comment;
    String idComment;
    String emailUser;
    String emailComment;

    public ItemComment(String idComment, String username, String comment, String emailUser, String emailComment) {
        this.idComment = idComment;
        this.emailUser = emailUser;
        this.emailComment = emailComment;
        this.username = username;
        this.comment = comment;
    }
    public String getEmailComment() {
        return emailComment;
    }

    public void setEmailComment(String emailComment) {
        this.emailComment = emailComment;
    }
    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }
    public String getIdComment() {
        return idComment;
    }

    public void setIdComment(String idComment) {
        this.idComment = idComment;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String nama) {
        this.username = nama;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
