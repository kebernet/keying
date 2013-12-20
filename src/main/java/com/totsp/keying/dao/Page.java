package com.totsp.keying.dao;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A simple container for a page of datastore results.
 */
public class Page<T extends Serializable> implements Serializable{
    private final ArrayList<T> results;
    private final String cursor;

    public Page(ArrayList<T> results, String cursor) {
        this.results = results;
        this.cursor = cursor;
    }

    /** The results of the query.
     *
     * @return
     */
    public ArrayList<T> getResults() {
        return results;
    }

    /**
     * The cursor to the next page of results, or null if this is the
     * final page.
     * @return
     */
    public String getCursor() {
        return cursor;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Page that = (Page) o;

        return Objects.equal(this.results, that.results) &&
                Objects.equal(this.cursor, that.cursor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(results, cursor);
    }


    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("results", results)
                .add("cursor", cursor)
                .toString();
    }
}

