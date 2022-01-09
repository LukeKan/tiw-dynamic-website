package it.polimi.tiw.plain_html.beans;

public class UserBean {
    private final Integer userID;
    private final String email;
    private String name;
    private String surname;
    private String cf;

    public UserBean(Integer userID, String email, String name, String surname, String cf) {
        this.userID = userID;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.cf = cf;
    }

    public Integer getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getCf() {
        return cf;
    }
}
