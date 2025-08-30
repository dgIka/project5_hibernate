package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(player);
            session.getTransaction().commit();
        }
        return player;
    }

    @Override
    public Player update(Player player) {
        Player player1 = null;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                player1 = session.get(Player.class, player.getId());
                if (player1 != null) {
                    player1.setName(player.getName());
                    player1.setBanned(player.getBanned());
                    player1.setProfession(player.getProfession());
                    player1.setRace(player.getRace());
                    player1.setTitle(player.getTitle());
                    player1.setBirthday(player.getBirthday());
                    player1.setLevel(player.getLevel());
                }
                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
                session.getTransaction().rollback();
            }
        }
        return player1;
    }

    @Override
    public Optional<Player> findById(long id) {
        Optional<Player> player = Optional.empty();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                Query<Player> query = session.createQuery("from Player where id = :id", Player.class);
                query.setParameter("id", id);
                player = Optional.of(query.getSingleResult());
            } catch (Exception e) {
                session.getTransaction().rollback();
                e.printStackTrace();
            }
            session.getTransaction().commit();
        }
        return player;
    }

    @Override
    public void delete(Player player) {
        Player player1 = null;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            player1 = session.get(Player.class, player.getId());
            try {
                if (player1 != null) {
                    session.delete(player1);
                }
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw new RuntimeException(e);
            }
            session.getTransaction().commit();
        }

    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}