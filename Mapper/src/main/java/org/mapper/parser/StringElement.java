package org.mapper.parser;

/**
 * Class for string elements.
 */
public class StringElement implements DataElement {

    /**
     * "" or null or "String" - String
     * c char
     * b byte
     * s short
     * i int
     * l long
     * f float
     * d double
     * B boolean
     * t LocalTime
     * D LocalDate
     * T LocalDateTime
     * 0 null (without quotes)
     */
    String className;
    String str;

    /**
     * Constructor of string element.
     *
     * @param s
     */
    public StringElement(String s) {
        this.className = "";
        str = s;
    }

    /**
     * Constructor of string element.
     *
     * @param className - name of class.
     * @param string    - string.
     */
    public StringElement(String className, String string) {
        this.className = className;
        str = string;
    }

    /**
     * Getting value of class.
     *
     * @param clazz - class.
     * @param <T>   - type.
     * @return value.
     */
    public <T> T getValue(Class<T> clazz) {
        return null;
    }

    /**
     * Getting type of element.
     *
     * @return enum type.
     */
    @Override
    public ElementType getType() {
        return ElementType.STRING;
    }

    /**
     * Getting element.
     *
     * @return string.
     */
    public String get() {
        return str;
    }

    /**
     * Adding element.
     *
     * @param val adding to element.
     */
    @Override
    public void add(DataElement val) {
    }

    /**
     * Adding key to element.
     *
     * @param key
     */
    @Override
    public void addKey(String key) {
    }

    /**
     * String format of element.
     *
     * @return
     */
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (className != null && className.equals("0")) {
            return "0";
        }
        if (!(className == null || className.isEmpty() || className.isBlank())) {
            stringBuilder.append(className);
        }
        stringBuilder.append("\"");
        int index = 0;
        while (index < str.length()) {
            switch (str.charAt(index)) {
                case '\n':
                    stringBuilder.append("\\n");
                    break;
                case '\t':
                    stringBuilder.append("\\t");
                    break;
                case '\\':
                    stringBuilder.append("\\\\");
                    break;
                case '\"':
                    stringBuilder.append("\\\"");
                default:
                    stringBuilder.append(str.charAt(index));
            }
            index++;
        }
        stringBuilder.append("\"");
        return stringBuilder.toString();
    }

    /**
     * Getting class name of element.
     *
     * @return
     */
    public String getClassName() {
        return className;
    }
}
