package it.polimi.tiw.plain_html.dao;

import it.polimi.tiw.plain_html.beans.ProductBean;
import it.polimi.tiw.plain_html.exceptions.ProductException;

import java.io.InputStream;
import java.sql.*;

public class ProductDao {
    private final Connection connection;

    public ProductDao(Connection connection) {
        this.connection = connection;
    }

    public Integer insertProduct(String productName, String productDescription, InputStream image) throws ProductException, SQLException {
        int productID = -1;
        String queryInsProduct = "INSERT INTO PRODUCT (name,description,image) VALUES (?,?,?)";
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(queryInsProduct, Statement.RETURN_GENERATED_KEYS);
            pstatement.setString(1, productName);
            pstatement.setString(2, productDescription);
            pstatement.setBlob(3, image);

            if(pstatement.executeUpdate()==0){
                throw new ProductException("Cannot insert new product.");
            }
            try (ResultSet generatedKeys = pstatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    productID = (int)generatedKeys.getLong(1);
                }
                else {
                    throw new ProductException("Cannot insert new product.");
                }
            }
        }catch (SQLException sqlException){
            throw new SQLException("Database error");
        }
        return productID;
    }
}