package org.devproof.mubble.server.repository;

import com.google.code.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Enforce constraints on container startup
 *
 * @author Carsten Hufe
 */
@Component
public class EnsureMongoConstraints {
    @Autowired
    private Datastore datastore;

    @PostConstruct
    public void ensureIndexesAndCaps() {
        datastore.ensureIndexes();
        datastore.ensureCaps();
    }
}
