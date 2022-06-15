package org.mapper.parser;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Class for element of object.
 */
public class ObjectElement implements DataElement {
    HashMap<String, DataElement> props;
    String lastKey = null;
    String className;

    /**
     * Constructor of element of object.
     *
     * @param className - name of class.
     */
    public ObjectElement(String className) {
        this.className = className;
        props = new HashMap<>();
    }

    /**
     * Getting type of object element.
     *
     * @return
     */
    @Override
    public ElementType getType() {
        return ElementType.OBJECT;
    }

    /**
     * Adding value of element.
     *
     * @param val adding to array.
     */
    @Override
    public void add(DataElement val) {
        assert (lastKey != null);
        props.put(lastKey, val);
        lastKey = null;
    }

    /**
     * Adding key to element.
     *
     * @param key
     */
    @Override
    public void addKey(String key) {
        lastKey = key;
    }

    /**
     * Getting value of element(with checking property retainIdentify).
     *
     * @param key - using key for getting value.
     * @return
     */
    public DataElement getVal(String key) {
        if (props.containsKey(key)) {
            return props.get(key);
        } else {
            return null;
        }
    }

    /**
     * String format for object element.
     *
     * @return string.
     */
    public String toString() {
        StringBuilder s = new StringBuilder("{");
        s.append(className);
        s.append("\n");
        Iterator<String> it = props.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            DataElement value = props.get(key);
            s.append("\"").append(key).append("\" = ").append(value.toString());
            if (it.hasNext())
                s.append(",");
            s.append("\n");
        }
        s.append("}\n");
        return s.toString();
    }

    /**
     * Getting name of object element.
     *
     * @return
     */
    public String getClassName() {
        return className;
    }
}
