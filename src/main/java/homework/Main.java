package homework;

import io.github.cdimascio.dotenv.Dotenv;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Main {

    private static boolean isRunningInDocker() {
        String env = System.getenv("DOCKER_ENV");
        return "true".equalsIgnoreCase(env);
    }
    public static void main(String[] args) {
        String host, port, database, username, password;
        if (isRunningInDocker()) {
            host = System.getenv("HOST");
            port = System.getenv("PORT");
            database = System.getenv("DATABASE");
            username = System.getenv("USERNAME");
            password = System.getenv("PASSWORD");
        } else {
            Dotenv dotenv = Dotenv.load();
            host = dotenv.get("HOST", "localhost");
            port = dotenv.get("PORT", "3306");
            database = dotenv.get("DATABASE", "classes_db");
            username = dotenv.get("USERNAME", "root");
            password = dotenv.get("PASSWORD", "root");
        }

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;

        System.out.printf("URL: %s, user: %s, pass: %s\n", url, username, password);

        try (SessionFactory sessionFactory = new Configuration()
                .setProperty("hibernate.connection.url", url)
                .setProperty("hibernate.connection.password", password)
                .setProperty("hibernate.connection.username", username)
                .configure().buildSessionFactory()) {
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                int result = session.createMutationQuery("delete from MyClasses").executeUpdate();
                System.out.println("Deleted " + result + " rows");
                session.getTransaction().commit();

                session.beginTransaction();
                MyClasses classes = new MyClasses("Java", 40);
                session.persist(classes);
                MyClasses classes2 = new MyClasses("Python", 30);
                session.persist(classes2);
                MyClasses classes3 = new MyClasses("C++", 50);
                session.persist(classes3);
                session.getTransaction().commit();

                session.beginTransaction();
                MyClasses python = session.get(MyClasses.class, classes2.getId());
                System.out.println("Result get python: " + python);
                python.setDuration(35);
                session.persist(python);
                session.getTransaction().commit();

                session.beginTransaction();
                MyClasses cpp = session.get(MyClasses.class, classes3.getId());
                System.out.println("Result cpp get: " + cpp);
                session.remove(cpp);
                session.getTransaction().commit();
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
}
