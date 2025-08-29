package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.*;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

    private SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        properties.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/first_db");
        properties.setProperty("hibernate.connection.username", "postgres");
        properties.setProperty("hibernate.connection.password", "postgres");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");


        sessionFactory = new Configuration()
                .addProperties(properties)
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        List<Player> players;
        List<Player> playersResult = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            players = session.createNativeQuery("SELECT * FROM rpg.player", Player.class).list();
            session.getTransaction().commit();
        }
            int start = pageNumber * pageSize;
            int end = start + pageSize;
            for (int i = 0; i < pageSize; i++) {
                playersResult.add(players.get(start + i));
            }
        return players.subList(start, end);

    }

    @Override
    public int getAllCount() {
        try (Session session = sessionFactory.openSession()) {
            Query<Integer> result = session.createNamedQuery("Player.getAllCount", int.class);
            return result.getSingleResult();
        }
    }

    @Override
    public Player save(Player player) {
        return null;
    }

    @Override
    public Player update(Player player) {
        return null;
    }

    @Override
    public Optional<Player> findById(long id) {
        return Optional.empty();
    }

    @Override
    public void delete(Player player) {

    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}