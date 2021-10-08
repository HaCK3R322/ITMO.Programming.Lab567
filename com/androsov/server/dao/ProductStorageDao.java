package com.androsov.server.dao;

import com.androsov.general.User;
import com.androsov.server.pojo.*;
import com.androsov.server.products.ProductBuilder;
import com.androsov.server.products.exceptions.ContentException;
import com.androsov.server.products.exceptions.ManagementException;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Simple DAO that allows to {@code ADD, UPDATE, REMOVE} products.
 */
public class ProductStorageDao implements AutoCloseable, ProductDao<Product, String> {
    private static ProductStorageDao instance;

    private final Connection connection;
    private final List<Person> persons;
    private final List<Product> products;
    private final List<ProductChange> changes;
    private final List<User> users;
    private final Map<Product, String> productUserMap;

    /**
     * Connects to database and collects all products from it.
     * @param productBuilder {@link ProductBuilder}
     * @throws SQLException If there is some SQL problem occurred.
     * @throws ContentException If any of the products in the database did not pass validation when added to memory.
     */
    private ProductStorageDao(ProductBuilder productBuilder) throws SQLException, ContentException {
        String STORAGE_URL = "jdbc:postgresql://localhost:5432/?user=postgres&password=intmain321890";
        this.connection = DriverManager.getConnection(STORAGE_URL);
        this.persons = new ArrayList<>();
        this.products = new ArrayList<>();
        this.changes = new ArrayList<>();
        this.users = new ArrayList<>();
        this.productUserMap = new HashMap<>();

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM itmo.users")) {
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    users.add(new User(null, resultSet.getString("nickname"), resultSet.getString("password")));
                }
            }
        }

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM itmo.person")) {
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    persons.add(new Person(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getLong("height"),
                        Color.values()[resultSet.getInt("eye_color") - 1],
                        Color.values()[resultSet.getInt("hair_color") - 1],
                        Country.values()[resultSet.getInt("nationality") - 1]
                    ));
                }
            }
        }

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM itmo.product")) {
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    final int ownerId = resultSet.getInt("owner_id");
                    final Person owner = persons.stream()
                        .filter(x -> x.getId() == ownerId).findAny()
                        .orElseThrow(() -> new ContentException("Can't find owner of product"));

                    products.add(productBuilder.buildProduct(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        toCoordinates(resultSet.getString("coordinates")),
                        resultSet.getTimestamp("creation_time").toLocalDateTime(),
                        resultSet.getInt("price"),
                        resultSet.getString("part_number"),
                        productBuilder.usedPartNumbers,
                        resultSet.getFloat("manufacturer_cost"),
                        UnitOfMeasure.values()[resultSet.getInt("unit_of_measure") - 1],
                        owner
                    ));

                    productUserMap.put(products.get(products.size() - 1), resultSet.getString("user_nickname"));
                }
            }
        }

        final StringBuilder storageContent = new StringBuilder();
        storageContent.append("Storage got ");
        storageContent.append(persons.size());
        storageContent.append(" persons and ");
        storageContent.append(users.size());
        storageContent.append(" users and ");
        storageContent.append(products.size());
        storageContent.append(" products {\n\n");
        for (Product product : products) {
            storageContent.append("        product (id = ");
            storageContent.append(product.getId());
            storageContent.append("): ");
            storageContent.append(product.getName());
            storageContent.append("; user nickname: ");
            storageContent.append(productUserMap.get(product));
            storageContent.append(";\n");
        }
        storageContent.append("\n}");
        Logger.getLogger(("LOGGER")).info(storageContent.toString());
    }

    /**
     * Creates and returns {@code instance} object of {@link ProductStorageDao}, if wasn't created yet.
     * @param productBuilder {@link ProductBuilder} object, that will build projects from database in memory and destroy them if need it.
     * @return instance for {@link ProductStorageDao}
     * @throws ContentException - in case of wrong data format in database, {@link ProductBuilder} will throw this exception.
     */
    public static ProductStorageDao createInstance(ProductBuilder productBuilder) throws SQLException, ContentException {
        if (instance == null) {
            instance = new ProductStorageDao(productBuilder);
        }

        return instance;
    }

    /**
     * Returns {@code instance} object of {@link ProductStorageDao}.
     * <p>
     * <B>Note:</B> you'll get link to instance, but if instance wasn't created yet, it'll be {@code null}.
     * @return instance for {@link ProductStorageDao}
     */
    public static ProductStorageDao getInstance() {
        return instance;
    }

    /**
     * Returns {@code unmodifiable} {@link List}<{@link Product}> of products, contained in memory, but still not in database.
     * @return {@code unmodifiable} {@link List}<{@link Product}>
     */
    public List<Product> getList() {
        return Collections.unmodifiableList(products);
    }

    /**
     * Returns {@code unmodifiable} {@link Map}<{@link Product}, {@link String}> that represents
     * connectivity of {@link Product} in memory and user's {@code nickname}.
     * @return - {@code unmodifiable} {@link Map}<{@link Product}, {@link String}>
     */
    public Map<Product, String> getProductUserMap() {
        return Collections.unmodifiableMap(productUserMap);
    }

    //TODO изменить как то на что-то красивее, но в целом работает может лучше и не трогать) - лучше всего будет добавить работу с дао в продуктБилдер чтобы получать id

    /**
     * Creates new change of adding new {@link Product} to database and matches it with user, invoked change.
     * @param product {@link Product}
     * @param nickname nickname of user, that invoked {@link Product} adding.
     */
    public synchronized void add(Product product, String nickname) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT last_value FROM itmo.product_id_seq")) {
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    product.getOwner().setId(resultSet.getInt("last_value") + 1);
                    product.setId(resultSet.getInt("last_value") + 1);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        changes.add(new ProductChange(product, ProductChange.TYPE_ADDED, nickname));
    }

    /**
     * Creates new change of replacing existing {@link Product} with new {@link Product} but saves id.
     * @param product {@link Product}
     * @param nickname {@link String} nickname of user, that invoked {@link Product} updating.
     * @throws ManagementException If a user tries to update a {@link Product} that he did not create.
     */
    public synchronized void update(Product product, String nickname) throws ManagementException {
        if (!productUserMap.get(product).equals(nickname))
            throw new ManagementException("You don't have permission to modify this product");

        changes.add(new ProductChange(product, ProductChange.TYPE_UPDATED, nickname));
    }

    /**
     * Creates new change of removing some {@link Product}, if user has access to this Product;
     * @param product {@link Product}
     * @param nickname {@link String} nickname of user, that invoked {@link Product} updating
     * @throws ManagementException If a user tries to update a {@link Product} that he did not create.
     */
    public synchronized void remove(Product product, String nickname) throws ManagementException {
        if (!productUserMap.get(product).equals(nickname))
            throw new ManagementException("You don't have permission to modify this product");

        changes.add(new ProductChange(product, ProductChange.TYPE_REMOVED, nickname));
    }

    public synchronized void sort(Comparator<Product> comparator) {
        products.sort(comparator);
    }

    /**
     * Updates database corresponding to changes, made by users.
     * <p>
     * Inside Iterates through all {@link ProductChange}s and changes the database accordingly.
     */
    public void commit() {
        try {
            for (ProductChange change : changes) {
                final Product product = change.product;
                switch (change.type) {
                    case ProductChange.TYPE_ADDED -> {
                        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO itmo.person (name, height, eye_color, hair_color, nationality) VALUES (?,?,?,?,?)")) {
                            stmt.setString(1, product.getOwner().getName());
                            stmt.setLong(2, product.getOwner().getHeight());
                            stmt.setInt(3, product.getOwner().getEyeColor().ordinal() + 1);
                            stmt.setInt(4, product.getOwner().getHairColor().ordinal() + 1);
                            stmt.setInt(5, product.getOwner().getNationality().ordinal() + 1);
                            stmt.execute();
                        }
                        persons.add(product.getOwner());
                        System.out.println("owner id:  " + product.getOwner().getId());
                        System.out.println("product id: " + product.getId());


                        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO itmo.product (name, coordinates, price, part_number, manufacturer_cost, unit_of_measure, owner_id, user_nickname) VALUES (?,?,?,?,?,?,?,?)")) {
                            stmt.setString(1, product.getName());
                            stmt.setObject(2, toString(product.getCoordinates()), Types.OTHER);
                            stmt.setInt(3, product.getPrice());
                            stmt.setString(4, product.getPartNumber());
                            stmt.setFloat(5, product.getManufactureCost());
                            stmt.setInt(6, product.getUnitOfMeasure().ordinal() + 1);
                            stmt.setInt(7, product.getOwner().getId());
                            stmt.setString(8, change.changeInvokatorNickname);
                            stmt.execute();
                        }
                        products.add(product);
                        productUserMap.put(product, change.changeInvokatorNickname);
                        Logger.getLogger(("LOGGER")).info("added product " + change.product.getName() + " (id = " + change.product.getId() + ").");
                    }
                    case ProductChange.TYPE_UPDATED -> {
                        try (PreparedStatement stmt = connection.prepareStatement("UPDATE itmo.product SET name=?, coordinates=?, price=?, part_number=?, manufacturer_cost=?, unit_of_measure=?, owner_id=? WHERE id=?")) {
                            stmt.setString(1, product.getName());
                            stmt.setObject(2, "(0,0)", Types.OTHER);
                            stmt.setInt(3, product.getPrice());
                            stmt.setString(4, product.getPartNumber());
                            stmt.setFloat(5, product.getManufactureCost());
                            stmt.setInt(6, product.getUnitOfMeasure().ordinal() + 1);
                            stmt.setInt(7, product.getOwner().getId());
                            stmt.setLong(8, product.getId());
                            stmt.execute();
                        }
                        products.removeIf(x -> x.getId() == product.getId());
                        products.add(product);
                        Logger.getLogger(("LOGGER")).info("updated product " + change.product.getName() + " (id = " + change.product.getId() + ").");
                    }
                    case ProductChange.TYPE_REMOVED -> {
                        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM itmo.product WHERE id=?")) {
                            stmt.setLong(1, product.getId());
                            stmt.execute();
                        }
                        products.removeIf(x -> x.getId() == product.getId());
                        ProductBuilder.getInstance().destroy(product);

                        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM itmo.person WHERE id=?")) {
                            stmt.setLong(1, product.getOwner().getId());
                            stmt.execute();
                        }
                        persons.removeIf(x -> x.getId() == product.getOwner().getId());
                        Logger.getLogger("LOGGER").info("removed product " + change.product.getName() + " (id = " + change.product.getId() + ").");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        changes.clear();
    }

    /**
     * Closes connection to database.
     * @throws SQLException If there is some SQL problem occurred.
     */
    @Override
    public void close() throws SQLException {
        connection.close();
    }

    /**
     * Consists of the {@link Product}, {@link String} nickname of change invokator, and the type of change.
     */
    private record ProductChange(Product product, int type, String changeInvokatorNickname) {
        public static final int TYPE_ADDED = 1;
        public static final int TYPE_UPDATED = 2;
        public static final int TYPE_REMOVED = 3;
    }

    /**
     * Specific method that check if user with such nickname exist in database.
     * <p>
     * Inside collects all user nicknames from database, then searches match.
     * @param nickname {@link String} nickname of the user we want to find
     * @return {@code true} if found, {@code false} if not found. all ez.
     */
    public boolean hasUser(String nickname) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM itmo.users")) {
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    if (resultSet.getString("nickname").equals(nickname)) {
                        return true;
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if the entered password matches the nickname
     * @param nickname user
     * @param password his password
     * @return {@code true} if all is correct, {@code false} if password for that nickname is wrong.
     */
    public boolean passwordIsRight(String nickname, String password) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM itmo.users")) {
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    if (resultSet.getString("nickname").equals(nickname)) {
                        if (resultSet.getString("password").equals(password)) {
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    /**
     * Creates new user in database;
     * @param user Inserts new {@link User} in database.
     */
    public void createUser(User user) {
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO itmo.users (nickname, password) VALUES (?,?)")) {
            stmt.setString(1, user.getNickname());
            stmt.setString(2, user.getPassword());
            stmt.execute();
            users.add(user);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static Coordinates toCoordinates(String value) {
        final int sep = value.indexOf(',');
        return new Coordinates(
                Double.parseDouble(value.substring(1, sep)),
                Double.parseDouble(value.substring(sep + 1, value.length() - 1))
        );
    }

    private static String toString(Coordinates coordinates) {
        return "(" + coordinates.getX() + "," + coordinates.getY() + ")";
    }
}
