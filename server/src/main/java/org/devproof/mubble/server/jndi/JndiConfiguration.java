/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.mubble.server.jndi;

import org.springframework.jndi.JndiTemplate;

import javax.naming.NamingException;

/**
 * Loads JNDI configuration from Tomcat
 *
 * @author Carsten Hufe
 */
public class JndiConfiguration {

    public static final String MONGODB_HOST = "java:comp/env/config/mubble/mongodb_host";
    public static final String MONGODB_PORT = "java:comp/env/config/mubble/mongodb_port";
    public static final String MONGODB_DBNAME = "java:comp/env/config/mubble/mongodb_dbname";
    public static final String MONGODB_DBUSER = "java:comp/env/config/mubble/mongodb_dbuser";
    public static final String MONGODB_DBPASSWORD = "java:comp/env/config/mubble/mongodb_dbpassword";

    public String resolveMongoDBHost() {
        return getJndiValue(MONGODB_HOST);
    }


    public Integer resolveMongoDBPort() {
        return getJndiIntValue(MONGODB_PORT);
    }

    public String resolveMongoDBName() {
        return getJndiValue(MONGODB_DBNAME);
    }


    public String resolveMongoDBUser() {
        return getJndiValue(MONGODB_DBUSER);
    }

    public String resolveMongoDBPassword() {
        return getJndiValue(MONGODB_DBPASSWORD);
    }

    private String getJndiValue(String jndiContext) {
        JndiTemplate jndi = new JndiTemplate();
        try {
            return (String) jndi.lookup(jndiContext);
        } catch (NamingException e) {
            return "empty";
        }
    }

    private Integer getJndiIntValue(String jndiContext) {
        JndiTemplate jndi = new JndiTemplate();
        try {
            return (Integer) jndi.lookup(jndiContext);
        } catch (NamingException e) {
            return -1;
        }
    }
}
