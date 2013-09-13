package com.totsp.keying.dao;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class OfyService {
    static {
        try {
            Enumeration<URL> enu = OfyService.class.getClassLoader().getResources("/ofy-classes");
            while(enu.hasMoreElements()){
                URL url = enu.nextElement();
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream())) ){
                    for(String line = reader.readLine(); line != null ; line = reader.readLine() ){
                        line = line.trim();
                        try{
                            Class c = Class.forName(line);
                            ObjectifyService.factory().register(c);
                        } catch(ClassNotFoundException cne){
                            throw new Error("Objectify initialization couldn't find class "+line);
                        }
                    }
                }
            }

        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Failed to load ofyclasses files!");
        }
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
