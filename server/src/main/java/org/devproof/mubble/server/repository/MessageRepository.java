package org.devproof.mubble.server.repository;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;
import org.bson.types.ObjectId;
import org.devproof.mubble.server.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Provide the generic DAO as spring bean
 *
 * @author Carsten Hufe
 */
@Repository
public class MessageRepository extends BasicDAO<Message, ObjectId> {

    @Autowired
    public MessageRepository(Datastore ds) {
        super(ds);
    }
}
