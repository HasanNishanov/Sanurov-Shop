package com.company.service;

import com.company.database.Database;
import com.company.model.GameCategory;
import com.company.model.Preview;


import java.sql.*;

public class PreviewService {

    public static void loadGameProductList() {
        Connection connection = Database.getConnection();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                Database.previewList.clear();
                String query = " SELECT * FROM ";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    int id = resultSet.getInt("");
                    int price = resultSet.getInt("");
                    String image = resultSet.getString("image");
                    String name = resultSet.getString("name");
                    String isDeleted = resultSet.getString("isDeleted");
                    Integer product_category_id = resultSet.getInt("product_category_id");
                    Integer game_category_id = resultSet.getInt("game_category_id");

                    Preview product = new Preview(id, price, image, name, isDeleted, product_category_id, game_category_id);

                    Database.previewList.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
    public static void loadFortniteHack() {
        Connection connection = Database.getConnection();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                Database.previewList.clear();
                String query = " SELECT * FROM preview WHERE game_category_id = 1";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int price = resultSet.getInt("price");
                    String image = resultSet.getString("image");
                    String name = resultSet.getString("name");
                    String isDeleted = resultSet.getString("isDeleted");
                    Integer product_category_id = resultSet.getInt("product_category_id");
                    Integer game_category_id = resultSet.getInt("game_category_id");

                    Preview product = new Preview(id, price, image, name, isDeleted, product_category_id, game_category_id);

                    Database.previewList.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
    public static void loadFortniteSkinchanger() {
        Connection connection = Database.getConnection();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                Database.previewList.clear();
                String query = " SELECT * FROM preview WHERE game_category_id = 2";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int price = resultSet.getInt("price");
                    String image = resultSet.getString("image");
                    String name = resultSet.getString("name");
                    String isDeleted = resultSet.getString("isDeleted");
                    Integer product_category_id = resultSet.getInt("product_category_id");
                    Integer game_category_id = resultSet.getInt("game_category_id");

                    Preview product = new Preview(id, price, image, name, isDeleted, product_category_id, game_category_id);

                    Database.previewList.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
    public static void loadGTA() {
        Connection connection = Database.getConnection();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                Database.previewList.clear();
                String query = " SELECT * FROM preview WHERE game_category_id = 3";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int price = resultSet.getInt("price");
                    String image = resultSet.getString("image");
                    String name = resultSet.getString("name");
                    String isDeleted = resultSet.getString("isDeleted");
                    Integer product_category_id = resultSet.getInt("product_category_id");
                    Integer game_category_id = resultSet.getInt("game_category_id");

                    Preview product = new Preview(id, price, image, name, isDeleted, product_category_id, game_category_id);

                    Database.previewList.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
    public static void loadFifa() {
        Connection connection = Database.getConnection();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                Database.previewList.clear();
                String query = " SELECT * FROM preview WHERE game_category_id = 4";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int price = resultSet.getInt("price");
                    String image = resultSet.getString("image");
                    String name = resultSet.getString("name");
                    String isDeleted = resultSet.getString("isDeleted");
                    Integer product_category_id = resultSet.getInt("product_category_id");
                    Integer game_category_id = resultSet.getInt("game_category_id");

                    Preview product = new Preview(id, price, image, name, isDeleted, product_category_id, game_category_id);

                    Database.previewList.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
    public static void loadNBA() {
        Connection connection = Database.getConnection();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                Database.previewList.clear();
                String query = " SELECT * FROM preview WHERE game_category_id = 5";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int price = resultSet.getInt("price");
                    String image = resultSet.getString("image");
                    String name = resultSet.getString("name");
                    String isDeleted = resultSet.getString("isDeleted");
                    Integer product_category_id = resultSet.getInt("product_category_id");
                    Integer game_category_id = resultSet.getInt("game_category_id");

                    Preview product = new Preview(id, price, image, name, isDeleted, product_category_id, game_category_id);

                    Database.previewList.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
    public static void loadVALORANT() {
        Connection connection = Database.getConnection();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                Database.previewList.clear();
                String query = " SELECT * FROM preview WHERE game_category_id = 6";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int price = resultSet.getInt("price");
                    String image = resultSet.getString("image");
                    String name = resultSet.getString("name");
                    String isDeleted = resultSet.getString("isDeleted");
                    Integer product_category_id = resultSet.getInt("product_category_id");
                    Integer game_category_id = resultSet.getInt("game_category_id");

                    Preview product = new Preview(id, price, image, name, isDeleted, product_category_id, game_category_id);

                    Database.previewList.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
    public static void loadGENSHIN() {
        Connection connection = Database.getConnection();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                Database.previewList.clear();
                String query = " SELECT * FROM preview WHERE game_category_id = 7";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int price = resultSet.getInt("price");
                    String image = resultSet.getString("image");
                    String name = resultSet.getString("name");
                    String isDeleted = resultSet.getString("isDeleted");
                    Integer product_category_id = resultSet.getInt("product_category_id");
                    Integer game_category_id = resultSet.getInt("game_category_id");

                    Preview product = new Preview(id, price, image, name, isDeleted, product_category_id, game_category_id);

                    Database.previewList.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
    public static void loadROBLOX() {
        Connection connection = Database.getConnection();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                Database.previewList.clear();
                String query = " SELECT * FROM preview WHERE game_category_id = 8";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int price = resultSet.getInt("price");
                    String image = resultSet.getString("image");
                    String name = resultSet.getString("name");
                    String isDeleted = resultSet.getString("isDeleted");
                    Integer product_category_id = resultSet.getInt("product_category_id");
                    Integer game_category_id = resultSet.getInt("game_category_id");

                    Preview product = new Preview(id, price, image, name, isDeleted, product_category_id, game_category_id);

                    Database.previewList.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
    public static void loadWARZONE() {
        Connection connection = Database.getConnection();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                Database.previewList.clear();
                String query = " SELECT * FROM preview WHERE game_category_id = 9";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int price = resultSet.getInt("price");
                    String image = resultSet.getString("image");
                    String name = resultSet.getString("name");
                    String isDeleted = resultSet.getString("isDeleted");
                    Integer product_category_id = resultSet.getInt("product_category_id");
                    Integer game_category_id = resultSet.getInt("game_category_id");

                    Preview product = new Preview(id, price, image, name, isDeleted, product_category_id, game_category_id);

                    Database.previewList.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
    public static void loadEFT() {
        Connection connection = Database.getConnection();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                Database.previewList.clear();
                String query = " SELECT * FROM preview WHERE game_category_id = 10";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int price = resultSet.getInt("price");
                    String image = resultSet.getString("image");
                    String name = resultSet.getString("name");
                    String isDeleted = resultSet.getString("isDeleted");
                    Integer product_category_id = resultSet.getInt("product_category_id");
                    Integer game_category_id = resultSet.getInt("game_category_id");

                    Preview product = new Preview(id, price, image, name, isDeleted, product_category_id, game_category_id);

                    Database.previewList.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public static void addPreview(Preview product) {
        Connection connection = Database.getConnection();
        if (connection != null) {

            String query = " INSERT INTO preview(price, name , image, isDeleted,product_category_id,game_category_id)" +
                    " VALUES(?, ?, ?, ?, 1,?); ";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, product.getPrice());
                preparedStatement.setString(2, product.getName());
                preparedStatement.setString(3, product.getImage());
                preparedStatement.setString(4, product.getIsDeleted());
                preparedStatement.setInt(5, product.getProduct_category_id());
//                preparedStatement.setInt(5, product.getGame_category_id());

                int executeUpdate = preparedStatement.executeUpdate();
                System.out.println(executeUpdate);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        loadGameProductList();
    }
    public static void deleteProduct(Integer id) {
        Connection connection = Database.getConnection();
        if (connection != null) {

            String query = " DELETE FROM preview WHERE id = ? ;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {


                preparedStatement.setInt(1, id);

                int executeUpdate = preparedStatement.executeUpdate();
                System.out.println("executeUpdate = " + executeUpdate);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }


    }
}
