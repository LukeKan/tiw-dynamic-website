package it.polimi.tiw.plain_html.beans;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class ProductBean {
    private final Integer productID;
    private final String name;
    private String description;
    private String image;

    public ProductBean(Integer productID, String name, String description, InputStream image) throws IOException {
        this.productID = productID;
        this.name = name;
        this.description = description;
        this.image = this.printImageToBase64(image);
    }


    public Integer getProductID() {
        return productID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(InputStream image) throws IOException {
        this.image = this.printImageToBase64(image);
    }

    public String printImageToBase64(InputStream inputStream) throws IOException {
        if(inputStream == null) return null;
        return Base64.getEncoder().encodeToString(inputStream.readAllBytes());
    }
}
