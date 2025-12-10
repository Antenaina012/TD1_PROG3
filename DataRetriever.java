import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private final DBConnection dbConnection;

    public DataRetriever(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
    
    private Product createProductFromResultSet(ResultSet rs, Connection connection) throws SQLException {
        int productId = rs.getInt("id");
        String name = rs.getString("name");
        double price = rs.getDouble("price");
        
        Timestamp ts = rs.getTimestamp("creation_datetime");
        Instant creationDatetime = ts.toInstant();
        
        List<Category> categories = getCategoriesForProduct(productId, connection);

        return new Product(productId, name, price, creationDatetime, categories);
    }

    private List<Category> getCategoriesForProduct(int productId, Connection connection) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id as category_id, name as category_name FROM Product_category WHERE product_id = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, productId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    categories.add(new Category(rs.getInt("category_id"), rs.getString("category_name")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des catégories pour le produit ID " + productId + ": " + e.getMessage());
        }
        return categories;
    }
    
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT ON (name) id, name FROM Product_category ORDER BY name, id";

        try (Connection connection = dbConnection.getDBConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de toutes les catégories: " + e.getMessage());
        }
        return categories;
    }

    public List<Product> getProductList(int page, int size) {
        List<Product> products = new ArrayList<>();
        if (page <= 0 || size <= 0) {
            return products;
        }

        int offset = (page - 1) * size;

        String sql = "SELECT id, name, price, creation_datetime FROM Product ORDER BY id LIMIT ? OFFSET ?";

        try (Connection connection = dbConnection.getDBConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, size);
            preparedStatement.setInt(2, offset);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    products.add(createProductFromResultSet(rs, connection));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits paginés: " + e.getMessage());
        }
        return products;
    }

    public List<Product> getProductsByCriteria(
            String productName, String categoryName, Instant creationMin, Instant creationMax) {
        return getProductsByCriteria(productName, categoryName, creationMin, creationMax, 0, 0);
    }
    
    public List<Product> getProductsByCriteria(
            String productName, String categoryName, Instant creationMin, Instant creationMax, int page, int size) {
        
        List<Product> products = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();
        
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1");
        
        if (productName != null && !productName.isEmpty()) {
            whereClause.append(" AND P.name ILIKE ?");
            parameters.add("%" + productName + "%");
        }

        if (categoryName != null && !categoryName.isEmpty()) {
            whereClause.append(" AND P.id IN (SELECT product_id FROM Product_category WHERE name ILIKE ?)");
            parameters.add("%" + categoryName + "%");
        }
        
        if (creationMin != null) {
            whereClause.append(" AND P.creation_datetime >= ?");
            parameters.add(Timestamp.from(creationMin));
        }

        if (creationMax != null) {
            whereClause.append(" AND P.creation_datetime <= ?");
            parameters.add(Timestamp.from(creationMax));
        }

        String baseSql = "SELECT P.id, P.name, P.price, P.creation_datetime FROM Product P";
        
        StringBuilder fullSql = new StringBuilder(baseSql)
                .append(whereClause)
                .append(" ORDER BY P.id");
        
        boolean isPaginated = page > 0 && size > 0;
        if (isPaginated) {
            int offset = (page - 1) * size;
            fullSql.append(" LIMIT ? OFFSET ?");
            parameters.add(size);
            parameters.add(offset);
        }

        try (Connection connection = dbConnection.getDBConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(fullSql.toString())) {

            int index = 1;
            for (Object param : parameters) {
                preparedStatement.setObject(index++, param);
            }

            System.out.println("\nExecuting SQL: " + preparedStatement.toString().replace("P.", ""));

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    products.add(createProductFromResultSet(rs, connection));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits par critères: " + e.getMessage());
        }
        return products;
    }
}