package com.totsp.keying.dao;

import com.google.common.base.Objects;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Unindex;

/**
 * Created with IntelliJ IDEA.
 * User: Robert
 * Date: 12/20/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Unindex
public class DeterministicEntity {
    @Id
    private String id;
    private String firstName;
    private String lastName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeterministicEntity that = (DeterministicEntity) o;

        return Objects.equal(this.id, that.id) &&
                Objects.equal(this.firstName, that.firstName) &&
                Objects.equal(this.lastName, that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, firstName, lastName);
    }
}
