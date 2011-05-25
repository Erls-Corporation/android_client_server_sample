package org.devproof.mubble.server.test;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;

/**
 * @author Carsten Hufe
 */
public class Testen {

    public static void main(String args[]) {
        System.out.println(StringEscapeUtils.escapeSql("Hallo * = \" welt"));
    }
}
