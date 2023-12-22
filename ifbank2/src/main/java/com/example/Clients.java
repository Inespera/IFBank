package com.example;

public class Clients {
    // Attributes
    private String firstName;
    private String lastName;
    private int clientNumber;
    
    // Constructor
    public Clients(String firstName, String lastName, int counter) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.clientNumber = counter; 
    }

    // Setters and Getters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    // toString() method
    @Override
    public String toString() {
        return "Client{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", clientNumber=" + clientNumber +
                '}';
    }

    // // Private method to generate a unique client number
    // private static int generateClientNumber(int counter) {
    //     return counter;
    // }
}
