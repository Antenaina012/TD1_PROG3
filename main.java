import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DBConnection dbConnection = new DBConnection();
        DataRetriever dataRetriever = new DataRetriever(dbConnection);

        
        System.out.println("============== 7.a : getAllCategories() ==============");
        List<Category> categories = dataRetriever.getAllCategories();
        categories.forEach(System.out::println);
        System.out.println("Total Catégories: " + categories.size());

        
        System.out.println("\n============== 7.b : getProductList(page, size) ==============");
        int[][] paginationTests = {
                {1, 10},
                {1, 5},
                {1, 3},
                {2, 2}
        };

        for (int[] test : paginationTests) {
            int page = test[0];
            int size = test[1];
            System.out.println("\n--- Test Pagination: Page=" + page + ", Size=" + size + " ---");
            List<Product> products = dataRetriever.getProductList(page, size);
            products.forEach(System.out::println);
            System.out.println("Résultats obtenus: " + products.size());
        }

        
        System.out.println("\n============== 7.c : getProductsByCriteria (Non paginé) ==============");
        
        Object[][] criteriaTests = {
            {"Dell", null, null, null},
            {null, "info", null, null},
            {"iPhone", "mobile", null, null},
            {null, null, parseInstant("2024-02-01"), parseInstant("2024-03-01")},
            {"Samsung", "bureau", null, null},
            {"Sony", "informatique", null, null},
            {null, "audio", parseInstant("2024-01-01"), parseInstant("2024-12-01")},
            {null, null, null, null}
        };

        for (Object[] test : criteriaTests) {
            String pName = (String) test[0];
            String cName = (String) test[1];
            Instant cMin = (Instant) test[2];
            Instant cMax = (Instant) test[3];

            System.out.println("\n--- Test Critères: pName='" + pName + "', cName='" + cName + "', cMin=" + cMin + ", cMax=" + cMax + " ---");
            List<Product> products = dataRetriever.getProductsByCriteria(pName, cName, cMin, cMax);
            products.forEach(System.out::println);
            System.out.println("Résultats obtenus: " + products.size());
        }


        
        System.out.println("\n============== 7.d : getProductsByCriteria (Paginé) ==============");
        
        Object[][] criteriaPaginatedTests = {
            {null, null, null, null, 1, 10},
            {"Dell", null, null, null, 1, 5},
            {null, "informatique", null, null, 1, 10}
        };

        for (Object[] test : criteriaPaginatedTests) {
            String pName = (String) test[0];
            String cName = (String) test[1];
            Instant cMin = (Instant) test[2];
            Instant cMax = (Instant) test[3];
            int page = (Integer) test[4];
            int size = (Integer) test[5];
            
            System.out.println("\n--- Test Critères & Pagination: pName='" + pName + "', cName='" + cName + "', page=" + page + ", size=" + size + " ---");
            List<Product> products = dataRetriever.getProductsByCriteria(pName, cName, cMin, cMax, page, size);
            products.forEach(System.out::println);
            System.out.println("Résultats obtenus: " + products.size());
        }
    }
    
    private static Instant parseInstant(String dateString) {
        if (dateString == null) return null;
        try {
            return Instant.parse(dateString);
        } catch (DateTimeParseException e) {
            return Instant.parse(dateString + "T00:00:00Z");
        }
    }
}