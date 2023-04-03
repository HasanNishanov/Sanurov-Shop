package com.company.service;

import com.company.database.Database;
import com.company.model.ProductCategory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductCategoryService {
    public static ProductCategory getProductCategoryById(Integer id){

        loadCategoryList();

        for (ProductCategory category : Database.productCategoryList) {
            if(category.getId().equals(id)){
                return category;
            }
        }
        return null;
    }

    public static void loadCategoryList() {
        Connection connection = Database.getConnection();
        if(connection != null){

            try (Statement statement = connection.createStatement()) {

                Database.productCategoryList.clear();

                String query = " SELECT * FROM game_category; ";

                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String isDeleted = resultSet.getString("isDeleted");

                    ProductCategory category = new ProductCategory(id, name,isDeleted);

                    Database.productCategoryList.add(category);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    }
    public static void loadCategoryListGames() {
        Connection connection = Database.getConnection();
        if(connection != null){

            try (Statement statement = connection.createStatement()) {

                Database.productCategoryList.clear();

                String query = " SELECT * FROM game_category where product_category_id = 1; ";

                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String isDeleted = resultSet.getString("isDeleted");

                    ProductCategory category = new ProductCategory(id, name,isDeleted);

                    Database.productCategoryList.add(category);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    }
    public static void loadCategoryListApp() {
        Connection connection = Database.getConnection();
        if(connection != null){

            try (Statement statement = connection.createStatement()) {

                Database.productCategoryList.clear();

                String query = " SELECT * FROM game_category where product_category_id = 2; ";

                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String isDeleted = resultSet.getString("isDeleted");

                    ProductCategory category = new ProductCategory(id, name,isDeleted);

                    Database.productCategoryList.add(category);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    }
}
