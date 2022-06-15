package org.mapper.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class for arrays.
 */
public class ArrayElement implements DataElement {
    ArrayList<DataElement> array;

    String className;

    /**
     * Constructor with no parametrs.
     */
    public ArrayElement() {
        this.className = "";
        array = new ArrayList<>();
    }

    /**
     * Constructor with parametr of class name.
     *
     * @param className
     */
    public ArrayElement(String className) {
        this.className = className;
        array = new ArrayList<>();
    }

    /**
     * Getting type of array element.
     *
     * @return enum
     */
    @Override
    public ElementType getType() {
        return ElementType.ARRAY;
    }

    /**
     * Adding element to array.
     *
     * @param val adding to array.
     */
    @Override
    public void add(DataElement val) {
        array.add(val);
    }

    /**
     * Adding key to array.
     *
     * @param key
     */
    @Override
    public void addKey(String key) {
    }

    /**
     * Getting name of class.
     *
     * @return
     */
    @Override
    public String getClassName() {
        return className;
    }

    /**
     * String format of array.
     *
     * @return
     */
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[");
        Iterator<DataElement> iterator = array.iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next());
            if (iterator.hasNext()) stringBuilder.append(",");
        }
        stringBuilder.append("\n]\n");
        return stringBuilder.toString();
    }

    /**
     * Getter of array.
     *
     * @return
     */
    public List<DataElement> get() {
        return array;
    }
}
