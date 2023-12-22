package com.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


public class Tests {
    public static int totalClients = 0;
    private static Hashtable<Integer, Accounts> accountHashtable = new Hashtable<>();
    public static void main(String[] args) {
        Clients[] clients = loadClients(3); 
        Accounts[] xmlAccounts = loadXmlFile("./accounts.xml");
        Accounts[] accounts = loadAccounts(clients, xmlAccounts);
        Hashtable<Integer, Accounts> accountHashtable = createAccountHashtable(accounts);
        Flow[] jsonFlows = loadJsonFile("./flows.json");
        Flow[] flows = loadFlows(accounts, jsonFlows);
        

        updateAccountBalances(flows, accountHashtable);


        displayClients(clients);
        System.out.println("---------------------------------------");
        displayAccounts(accounts);
        System.out.println("---------------------------------------");
        displayAccountHashtable(accountHashtable);
        System.out.println("---------------------------------------");
        displayFlow(flows);
    }

    // Method to load clients/accounts into an array
    private static Clients[] loadClients(int numberOfClients) {
        Clients[] clients = new Clients[numberOfClients];
        for (int i = 0; i < numberOfClients; i++) {
            clients[i] = generateClient("name" + (i + 1), "firstname" + (i + 1));
        }
        return clients;
    }

    private static Accounts[] loadAccounts(Clients[] clients, Accounts[] newAccount) {
        Accounts[] accounts = new Accounts[clients.length * 2]; 

        for (int i = 0; i < clients.length; i++) {
            accounts[i] = new SavingsAccount("Savings", clients[i]);
            accounts[i + clients.length] = new CurrentAccount("Current", clients[i]);
        }

        int originalLength = accounts.length;
            accounts = Arrays.copyOf(accounts, originalLength + newAccount.length);

            System.arraycopy(newAccount, 0, accounts, originalLength, newAccount.length);

        return accounts;
    }

    private static Hashtable<Integer, Accounts> createAccountHashtable(Accounts[] accounts) {
        Hashtable<Integer, Accounts> accountHashtable = new Hashtable<>();
        for (Accounts account : accounts) {
            accountHashtable.put(account.getAccountNumber(), account);
        }
        return accountHashtable;
    }

    private static Flow[] loadFlows(Accounts[] accounts, Flow[] newFlows) {
        LocalDate currentDate = LocalDate.now();
        LocalDate twoDaysLater = currentDate.plusDays(2);

        Flow[] flows = {
                new Debit("Debit from account 1", 1, 50, 1, true, Date.from(twoDaysLater.atStartOfDay(ZoneId.systemDefault()).toInstant())),
                new Credit("Credit on all current accounts", 2, 100.50, -1, true, Date.from(twoDaysLater.atStartOfDay(ZoneId.systemDefault()).toInstant())),
                new Credit("Credit on all savings accounts", 3, 1500, -1, true, Date.from(twoDaysLater.atStartOfDay(ZoneId.systemDefault()).toInstant())),
                new Transfer("Transfer from account 1 to account 2", 4, 50, 2, true, 1, Date.from(twoDaysLater.atStartOfDay(ZoneId.systemDefault()).toInstant()))
        };

        int originalLength = flows.length;
            flows = Arrays.copyOf(flows, originalLength + newFlows.length);

            // Copy elements from 'newFlows' to the extended 'flows' array
            System.arraycopy(newFlows, 0, flows, originalLength, newFlows.length);


        Arrays.stream(flows).forEach(flow -> {
            String[] type;
            type = flow.getComment().split(" "); 
            if(flow.getTargetAccountNumber() >= 0){
                accounts[flow.getTargetAccountNumber()-1].setBalance(flow.getAmount(), type[0]);
            }
        });

        return flows;
    }

    // Method to generate a client with the given name and firstname
    private static Clients generateClient(String name, String firstName) {
        totalClients++;
        return new Clients(name, firstName, totalClients);
    }

    // Method to display the contents of the client/accounts array using a stream
    private static void displayClients(Clients[] clients) {
        Arrays.stream(clients).forEach(client -> System.out.println(client.toString()));
    }

    private static void displayAccounts(Accounts[] accounts) {
        Arrays.stream(accounts).forEach(account -> System.out.println(account.toString()));
    }



    // Method to display the contents of the Hashtable in ascending order according to balance
    private static void displayAccountHashtable(Hashtable<Integer, Accounts> accountHashtable) {
        // Use TreeMap to sort the Hashtable by balance
        Map<Integer, Accounts> sortedMap = new TreeMap<>(accountHashtable);

        // Display the sorted Hashtable using a stream
        sortedMap.forEach((accountNumber, account) -> System.out.println(account.toString()));
    }

    private static void displayFlow(Flow[] flows) {
        Arrays.stream(flows).forEach(flow -> System.out.println(flow.toString()));
    }


// Method to update the balances of the accounts and check for negative balances
    private static void updateAccountBalances(Flow[] flows, Hashtable<Integer, Accounts> accountHashtable) {
        Arrays.stream(flows).forEach(flow -> {
            // Update the balances based on the type of flow
            Optional.ofNullable(accountHashtable.get(flow.getTargetAccountNumber()))
                    .ifPresent(account -> account.setBalance(flow.getAmount(), flow.getClass().getSimpleName()));

            if (flow instanceof Transfer) {
                Transfer transfer = (Transfer) flow;
                Optional.ofNullable(accountHashtable.get(transfer.getIssuingAccountNumber()))
                        .ifPresent(account -> account.setBalance(-flow.getAmount(), flow.getClass().getSimpleName()));
            }
        });

       
        boolean hasNegativeBalance = accountHashtable.values().stream()
                .anyMatch(account -> account.getBalance() < 0);

        
        Optional.of(hasNegativeBalance)
                .filter(b -> b)
                .ifPresent(b -> System.out.println("Warning: There is an account with a negative balance."));

    }

    // Method to load data from a JSON file and return a List of Flows
    private static Flow[] loadJsonFile(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Flow[] newFlows = objectMapper.readValue(filePath, Flow[].class);
            return newFlows;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Method to load data from an XML file and return a List of Accounts
    private static Accounts[] loadXmlFile(String filePath) {
        try {
            File xmlFile = new File(filePath);
            JAXBContext context = JAXBContext.newInstance(Accounts.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Accounts[] xmlAccounts = (Accounts[]) unmarshaller.unmarshal(xmlFile);
            
            return xmlAccounts;

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return null;
    }
}