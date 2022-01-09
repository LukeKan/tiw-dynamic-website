package it.polimi.tiw.plain_html.dao;

import it.polimi.tiw.plain_html.beans.UserBean;
import it.polimi.tiw.plain_html.exceptions.UserException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }




    //Returns the UserBean in case the authentication was successful, otherwise NULL
    public UserBean login(String email, String psw) throws UserException {
        String query = "SELECT * from user where email = ? and psw = ?";
        PreparedStatement pstatement = null;
        UserBean result = null;
        try {
            pstatement = connection.prepareStatement(query);

            pstatement.setString(1, email);
            pstatement.setString(2, psw);

            ResultSet res = pstatement.executeQuery();
            while (res.next()) {
                result = new UserBean(res.getInt("userID"),
                        res.getString("email"),
                        res.getString("name"),
                        res.getString("surname"),
                        res.getString("cf"));
            }
        } catch (SQLException e) {
            throw new UserException(e.getMessage());
        } finally {
            try {
                pstatement.close();
            } catch (Exception e1) {

            }
        }
        return result;
    }

    public UserBean register(String email, String psw, String name, String surname, String cf) throws UserException {
        String queryInsert ="INSERT INTO user(email,psw,name,surname,cf) VALUES( ?, ?, ?, ?, ?)";
        String querySelect ="SELECT * FROM user where email = ?";
        PreparedStatement pstatement = null;
        UserBean result = null;
        try {
            pstatement = connection.prepareStatement(queryInsert);

            pstatement.setString(1, email);
            pstatement.setString(2, psw);
            pstatement.setString(3, name);
            pstatement.setString(4, surname);
            pstatement.setString(5, cf);
            pstatement.execute();

            pstatement = connection.prepareStatement(querySelect);

            pstatement.setString(1, email);
            ResultSet res = pstatement.executeQuery();
            while (res.next()) {
                result = new UserBean(res.getInt("userID"),
                        res.getString("email"),
                        res.getString("name"),
                        res.getString("surname"),
                        res.getString("cf"));
            }
        } catch (SQLException e) {
            throw new UserException(e.getMessage());
        } finally {
            try {
                pstatement.close();
            } catch (Exception e1) {

            }
        }
        return result;
    }
}
