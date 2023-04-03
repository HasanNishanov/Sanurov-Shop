package com.company.service;


import com.company.database.Database;
import com.company.model.Customer;
import lombok.NonNull;

import java.sql.*;

public class CustomerService {
private static String id;
    public static Customer getCustomerById( String id) {
        Connection connection = Database.getConnection();
            try (Statement statement = connection.createStatement()) {

                Database.customerList.clear();

                String query = " SELECT * FROM customer where chat_id ="+id;

                ResultSet resultSet = statement.executeQuery(query);
                String chat_id = resultSet.getString("chat_id");
                String username = resultSet.getString("username");
                String balance = resultSet.getString("balance");

                return new Customer(chat_id, username, balance);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("CONNECTION NOT WORKING");
                return null;
            }


    }




    public static void addCustomer(Customer customer) {
        Connection connection = Database.getConnection();
        if(connection != null){

            String query = " INSERT INTO customer(id, balance, username)" +
                    " VALUES(?, ?, ?); ";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, customer.getId());
                preparedStatement.setString(2, customer.getBalance());
                preparedStatement.setString(3, customer.getUsername());

                int executeUpdate = preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public static String loadAllId() {

        Connection connection = Database.getConnection();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                Database.previewList.clear();
                String query = " SELECT * FROM customer";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                     id = resultSet.getString("id");
                    String username = resultSet.getString("username");
                    String balance = resultSet.getString("balance");


                    Customer product = new Customer(id, username, balance);

                    Database.customerList.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return id;
    }
}
