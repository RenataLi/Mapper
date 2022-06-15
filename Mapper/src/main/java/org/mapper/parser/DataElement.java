package org.mapper.parser;

/**
 * Interface of DataElement.
 */
public interface DataElement {
    /**
     * Getting type of  element.
     *
     * @return
     */
    ElementType getType();

    /**
     * Adding element to array if element is array.
     *
     * @param val adding to array.
     */
    void add(DataElement val);

    /**
     * Adding key to array.
     *
     * @param key
     */
    void addKey(String key);

    /**
     * String format of element.
     *
     * @return
     */
    String toString();

    /**
     * Getter of element.
     *
     * @return
     */
    public String getClassName();

}
