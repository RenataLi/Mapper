package org.mapper.parser;

import java.util.Iterator;

/**
 * Class for element iterator.
 */
public class ObjectIterator implements Iterator<DataElement> {
    /**
     * Checking for iterator if it is has next.
     *
     * @return boolean.
     */
    @Override
    public boolean hasNext() {
        return false;
    }

    /**
     * Getting next element.
     *
     * @return next element.
     */
    @Override
    public DataElement next() {
        return null;
    }
}
