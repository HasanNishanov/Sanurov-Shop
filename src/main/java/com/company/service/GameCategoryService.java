package com.company.service;

import com.company.database.Database;
import com.company.model.GameCategory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GameCategoryService {
    public static GameCategory getGameCategoryById(Integer id){

        loadCategoryList();

        for (GameCategory category : Database.gameCategoryList) {
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

                Database.gameCategoryList.clear();

                String query = " SELECT * FROM game_category; ";

                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    Integer product_category_id = resultSet.getInt("product_category_id");
                    String isDeleted = resultSet.getString("isDeleted");

                    GameCategory category = new GameCategory(id, name, product_category_id,isDeleted);

                    Database.gameCategoryList.add(category);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    }
}
