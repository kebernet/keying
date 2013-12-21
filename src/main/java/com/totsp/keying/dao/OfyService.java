/**
 *    Copyright 2013 Robert Cooper
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.totsp.keying.dao;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
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
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), Charset.forName("utf-8"))) ){
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
