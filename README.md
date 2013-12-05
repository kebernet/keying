Keying
======

This is a library for implemented some common Datastore keying strategies on Google App Engine/Google Compute Engine.
More specifically, this is designed to help with several different keying strategies for indexing and natural order
manipulation with Objectfy.

Purpose
-------

If you have been working with GAE for a while, you learn a few tricks about ids. Since custom composite indexes are
limited, if you need to create a join-table type entity, it makes a lot of sense to use the two side of the join as
the id, like you might do with a composite key in an RDBMS. For other things, you want unique keys, but you also want
the natural order of you entities to come in a certain for -- say, reverse chronological for your blog. Keying is an
API that makes this easier.

Maven
-----

    <repository>
        <id>kebernet</id>
        <name>bintray</name>
        <url>http://dl.bintray.com/kebernet/maven</url>
    </repository>

    ...

    <dependency>
        <groupId>com.totsp</groupId>
        <artifactId>keying</artifactId>
        <version>1.0.6</version>
    </dependency>

Usage
-----

Keying allows you to annotate your id field or property with a description of a key strategy to create a String entity
id. Let's look at a simple example:


    @Entity
    public class Pet {
        @Id
        @KeyStrategy(KeySegment.UUID)
        public String getId(){ ... };
        public String getName();

    }

If you want to save this to your datastore, you can call:

    KeyGenerator.key(myPet);

If you are using Objectify, you can extends the base DAO implementation:

    public class PetDao extends AbstractStringKeyedDao<Pet> {
        public PetDao(){
            super(Pet.class);
        }
    }

and to save a pet, you can call:

    myPetDao.save(myPet);

which will assign it a new key if the id is null.

Another common usage is for "Joiners" -- that is, entities that are meant to be used like join tables in a database.
Suppose you have Users and Groups with a many to many relationship. You might have:

    @Entity
    public class Membership {

        @Id
        @KeyStrategy(value={KeySegment.PROPERTY, KeySegment.PROPERTY},
                    properties={"groupId", "userId"})
        public String getId(){ ... }
        public String getGroupId() { ... }
        public String getUserId() { ... }

    }

In this example, there are two parts to the KeyStrategy value, both are PROPERTY segments. The "properties" array
then determines the values and orders in which they will be populated. So if I do:

    Membership membership = new Membership("Administrators", "Bob");
    KeyGenerator.key(membership);

the Id property on the Membership instance will be set to "Administrators:Bob". (You can also declare a "separator" value
on the key strategy that will override the ":" default.)

This example is a "Deterministic Key": that means given the same object with the same values, you get the same results.
Sometimes you might want to what the key would be without having a complete entity. For example, if you are synchronizing
this membership from an external source, you don't want to simply write to the datastore all the Membership objects
if they change rarely. In this case you can create a set of Ids using:

    String possibleId = KeyGenerator.compute(membership);

Another example of a non-deterministic strategy is time. For example, you might want to (almost always) find blogs posts
in reverse chronlogical order. In this case you could have:

    @Entity
    public class Post {
        @Id
        @KeyStrategy(value={KeySegment.INVERSE_TIME, KeySegment.UUID})
        public String getId(){ ... }
        public String getTitle() { ... }
        public String getContent() { ... }
    }

This will give you a key in the form of: hex value of (Long.MAX_VALUE - current epoch time) ":" random uuid. You can use
KeySegment.TIME for oldest first or KeySegment.INVERSE_TIME for newest first. However, because you might (MIGHT!) insert
two entities with the same timestamp, temporal key segments *must* be combined with another segment.

Google Web Toolkit
------------------

If you are using GWT, you can get the annotations (but *not* the KeyGenerator) for compilation using:

    <inherits name="com.totsp.Keying"/>
