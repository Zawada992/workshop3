package pl.coderslab.utils;



import pl.coderslab.BCrypt.BCrypt;
import java.sql.*;
import java.util.Arrays;

public class UserDao {

    private static final String FIND_ALL_USERS =
            "SELECT * FROM users";
    private static final String READ_USER =
            "SELECT * FROM users WHERE userId = ?";
    private static final String CREATE_USER =
            "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
    private static final String UPDATE_USER =
            "UPDATE users SET username = ?, email = ?, password = ? where userId = ?";
    private static final String DELETE_USER =
            "DELETE FROM users WHERE userId = ?";
    private User[] addToArray(User u, User[] users) {
        User[] tmpUsers = Arrays.copyOf(users, users.length + 1);
        tmpUsers[users.length] = u;
        return tmpUsers;
    }

    public User createUser(User user) {
        try (Connection connect = DbUtil.getConnection()) {
            PreparedStatement preparedStatement =
                    connect.prepareStatement(CREATE_USER, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, hashPassword(user.getPassword()));
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setUserId(resultSet.getInt(1));
                System.out.println("Is created");
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateUser(User user) {
        try (Connection connect = DbUtil.getConnection()) {
            PreparedStatement preparedStatement =
                    connect.prepareStatement(UPDATE_USER);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, hashPassword(user.getPassword()));
            preparedStatement.setLong(4, user.getUserId());
            preparedStatement.executeUpdate();
            System.out.println("Sucsses");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public void removeUser(long userId) {
        try (Connection connect = DbUtil.getConnection()) {
            PreparedStatement preparedStatement = connect.prepareStatement(DELETE_USER);
            preparedStatement.setLong(1, userId);
            preparedStatement.executeUpdate();
            System.out.println("Is delete");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public User readUser(long userId) {
        User user = null;
        try (Connection connect = DbUtil.getConnection()) {
            PreparedStatement preparedStatement = connect.prepareStatement(READ_USER);
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                user = new User(
                        resultSet.getLong("userId"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
    public User readOneUser(long userId) {
        User user = null;
        try (Connection connect = DbUtil.getConnection()) {
            PreparedStatement preparedStatement = connect.prepareStatement(READ_USER);
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                user = new User(
                        resultSet.getLong("userId"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }


    public User[] findAllUsers() {
        try (Connection connect = DbUtil.getConnection()) {
            User[] users = new User[0];
            PreparedStatement preparedStatement = connect.prepareStatement(FIND_ALL_USERS);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getLong("userId"));
                user.setUsername(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));

                users = addToArray(user, users);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }




    private String hashPassword(String password) {

        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println(hashed);
        return hashed;

    }


}
