package com.royal.models.products;

public interface ElectronicProduct {
    String getId();
    String getModel();
    float getPrice();
    byte[] getPhoto();
    int getMemory();
    String getDescription();
    long getItemsInStock();

    void setModel(String model);
    void setPrice(float price);
    void setPhoto(byte[] photo);
    void setMemory(int memory);
    void setDescription(String description);
    void setItemsInStock(long items);
}
