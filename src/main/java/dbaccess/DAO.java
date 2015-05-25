package dbaccess;

import messaging.Message;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by denis on 29.3.15.
 * Data access object
 */

public class DAO {

    private static final String ENTITY_MANAGER_FACTORY_NAME =
            "persistenceUnit2";
    private EntityManagerFactory factory;

    /**
     * DAO constructor
     */
    public DAO() {
        factory = Persistence.createEntityManagerFactory(ENTITY_MANAGER_FACTORY_NAME);

        //factory = new org.hibernate.ejb.HibernatePersistence().createEntityManagerFactory(ENTITY_MANAGER_FACTORY_NAME, null);
    }

    public boolean addUser(int id, String name){
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        boolean result = false;
        try {
            entityManager = factory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            UsersEntity usersEntity = new UsersEntity();
            usersEntity.setId(id);
            usersEntity.setName(name);
            entityManager.merge(usersEntity);
            transaction.commit();
            result = true;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            return result;
        }
    }

    public boolean addMessage(Message message) {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        boolean result = false;
        try {
            entityManager = factory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            String[] params = new String[1];
            params[0] = message.getUser();
            UsersEntity user = (UsersEntity)get("usersByName", params).get(0);
            MessagesEntity messageEntity = new MessagesEntity();
            messageEntity.setTextdate(message.getMessageText());
            messageEntity.setUsersByUserId(user);
            entityManager.merge(messageEntity);
            transaction.commit();
            result = true;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            return result;
        }
    }

    public List<Object> get(String type, Object [] params)  {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        List<Object> resultArray = null;
        try {
            entityManager = factory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query query = entityManager.createNamedQuery(type);
            if (params != null)
                for (int i = 1; i <= params.length; i++){
                    query.setParameter(i, params[i-1]);
                }
            resultArray = (List<Object>)query.getResultList();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            return resultArray;
        }
    }

    public List<String> getMessagesByUserName(String name){
        String[] params = new String[1];
        params[0] = name;
        UsersEntity user = (UsersEntity)get("usersByName", params).get(0);
        return getMessagesByUserId(user.getId());
    }

    public List<String> getMessagesByUserId(Integer id){
        List<String> list = new ArrayList<String>();
        Integer [] params_ = new Integer[1];
        params_[0] = id;
        for (Object messagesEntity: get("messagesByUserId", params_)){
            list.add(((MessagesEntity)messagesEntity).getTextdate());
        }
        return list;
    }

    public List<String> getMessagesAll(){
        List<String> list = new ArrayList<String>();
        for (Object messagesEntity: get("messages",null)){
            list.add(((MessagesEntity)messagesEntity).getTextdate());
        }
        return list;
    }


}

