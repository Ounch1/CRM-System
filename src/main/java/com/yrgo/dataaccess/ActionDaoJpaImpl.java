package com.yrgo.dataaccess;

import com.yrgo.domain.Action;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ActionDaoJpaImpl implements ActionDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public void create(Action newAction) {
        em.persist(newAction);
    }

    @Override
    public List<Action> getIncompleteActions(String userId) {
        return em.createQuery("SELECT action FROM Action as action where action.owningUser=:userId and action.complete=:false", Action.class).getResultList();
    }

    @Override
    public void update(Action actionToUpdate) throws RecordNotFoundException {
        Action action = em.find(Action.class, actionToUpdate.getActionId());
        if (action == null) {throw new RecordNotFoundException();}
        em.merge(actionToUpdate);
    }

    @Override
    public void delete(Action oldAction) throws RecordNotFoundException {
        Action action = em.find(Action.class, oldAction.getActionId());
        em.remove(action);
    }
}
