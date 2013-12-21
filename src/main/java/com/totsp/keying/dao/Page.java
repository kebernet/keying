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

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
     * @return the list of result entities.
     */
    public List<T> getResults() {
        return results;
    }

    /**
     * The cursor to the next page of results, or null if this is the
     * final page.
     * @return the cursor to the next batch or null.
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

