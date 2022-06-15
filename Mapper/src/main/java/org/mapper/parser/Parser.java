package org.mapper.parser;

import java.io.*;
import java.util.Stack;

public class Parser {
    public Parser() {
    }

    public DataElement read(File file) throws IOException {
        FileReader in = new FileReader(file);
        return read(in);
    }

    /**
     * Reading element from stream.
     *
     * @param in_ - stream.
     * @return reading other part of element.
     * @throws IOException if fail to read.
     */
    public DataElement read(InputStream in_) throws IOException {
        /**
         * * in the process of parsing: parsing elements recursively
         * st[0] - root
         */
        InputStreamReader in = new InputStreamReader(in_, "UTF-8");
        return read(in);
    }

    /**
     * Reading the 1st object from 'in'.
     *
     * @param in stream.
     * @return readed string.
     * @throws IOException if fail to read.
     */
    public DataElement read(InputStreamReader in) throws IOException {
        Stack<DataElement> stack = new Stack<>();

        DataElement root = null;
        DataElement element;
        String className;
        // String value type (1 letter).
        char type = '\0';
        // Current character from the stream.
        char current = (char) in.read();
        //Checking if file is empty.
        if (current == (char) (-1)) {
            throw new IOException("File empty");
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            while (current != (char) (-1) && (Character.isSpaceChar(current) || current == '\n')) {
                current = (char) in.read();
            }
            // End of string.
            if (current == (char) (-1)) {
                break;
            }
            if (current == '0') {
                // Null element.
                stack.push(new StringElement("0", ""));
                current = (char) in.read();
                continue;
            }
            //The letter before the quotation marks is the name of the type.
            if (Character.isAlphabetic(current)) {
                type = current;
                current = (char) in.read();
            }

            switch (current) {
                //The line started.
                case '"':
                    // Go ahead processing '\'.
                    // Before the closing quotation mark.
                    current = (char) in.read();
                    stringBuilder = new StringBuilder();
                    while (current != -1 && current != '"') {
                        if (current == '\\') {
                            stringBuilder.append(current);
                            current = (char) in.read();
                            stringBuilder.append(current);
                            current = (char) in.read();
                        } else {
                            stringBuilder.append(current);
                            current = (char) in.read();
                        }
                    }
                    if (current == '"') {
                        element = new StringElement("" + type, unescape(stringBuilder.toString()));
                        stack.push(element);
                    } else {
                        throw new IOException("unexpected end of file");

                    }
                    type = '\0';
                    break;
                // The object has started.
                case '{':
                    do {
                        current = (char) in.read();
                    } while (current != -1 && (Character.isSpaceChar(current) || current == '\n'));
                    // End of string.
                    if (current == -1) {
                        throw new IOException("unexpected end of file");
                    }
                    // Reading the class name.
                    stringBuilder = new StringBuilder();
                    while (current != -1 && (
                            !Character.isSpaceChar(current) && current != '"' && current != '\n')) {
                        stringBuilder.append(current);
                        current = (char) in.read();
                    }
                    className = stringBuilder.toString();
                    stack.push(new ObjectElement(className));
                    continue;
                    // The list or set has started.
                case '[':
                    do {
                        current = (char) in.read();
                    } while (current != -1 && (Character.isSpaceChar(current) || current == '\n'));
                    // End of string.
                    if (current == -1) {
                        throw new IOException("unexpected end of file");
                    }
                    stack.push(new ArrayElement());
                    continue;
                    // The previous element is necessarily a string!
                case '=':
                    element = stack.pop();
                    if (element.getType() != ElementType.STRING) {
                        throw new IOException("string expected as property name");
                    }
                    StringElement strel = (StringElement) element;
                    element = stack.peek();
                    if (element.getType() != ElementType.OBJECT) {
                        throw new IOException("= not in object");
                    }
                    element.addKey(strel.get());
                    current = (char) in.read();
                    continue;
                    // An item of an object or list has ended.
                case ',':
                    element = stack.pop();
                    stack.peek().add(element);
                    current = (char) in.read();
                    // To the beginning of the cycle.
                    continue;
                    // The list has ended.
                case ']':
                    // The object has ended.
                case '}':
                    element = stack.pop();
                    stack.peek().add(element);
                    // Exit case.
                    break;
                default:
                    throw new IOException("Parsing error: unexpected symbol " + current);

            }
            // If there is one element in the stack, then that's it.
            if (stack.size() == 1) {
                root = stack.pop();
                // Read 1 whole record (string array or object).
                break;
            }
            // We are inside some object or list - we need to read the next character.
            current = (char) in.read();
        }
        return root;

    }

    public DataElement read(String str) throws IOException {
        /**
         * * in the process of parsing: parsing elements recursively
         * st[0] - root
         */
        Stack<DataElement> elementStack = new Stack<>();
        // Index in the row.
        int index = 0;
        DataElement root = null;
        DataElement element;
        String className;
        // Type of string value (1 letter).
        char type = '\0';
        int ind1;
        while (index < str.length()) {
            while (index < str.length() && (
                    Character.isSpaceChar(str.charAt(index)) || str.charAt(index) == '\n')) {
                index++;
            }
            // End of string.
            if (index == str.length()) {
                break;
            }
            if (str.charAt(index) == '0') {
                elementStack.push(new StringElement("0", ""));
                index++;
                continue;
            }
            if (Character.isAlphabetic(str.charAt(index))) {
                type = str.charAt(index);
                index++;
            }

            switch (str.charAt(index)) {
                case '"':
                    // Construction has begun.
                    // Go ahead processing '\'.
                    // Before the closing quotation mark.
                    // the character after ".
                    index++;
                    ind1 = index;
                    while (true) {
                        if (str.charAt(ind1) == '\\') {
                            ind1 += 2;
                        } else if (str.charAt(ind1) == '"') {
                            // End of line found.
                            element = new StringElement("" + type, unescape(str.substring(index, ind1)));
                            elementStack.push(element);
                            break;
                        } else {
                            ind1++;
                        }
                    }
                    // "" processed.
                    type = '\0';
                    index = ind1 + 1;
                    break;
                // The object has started.
                case '{':
                    index++;
                    while (index < str.length() && (
                            Character.isSpaceChar(str.charAt(index)) || str.charAt(index) == '\n')) {
                        index++;
                    }
                    // Reading the class name.
                    ind1 = index;
                    while (ind1 < str.length() && (
                            !Character.isSpaceChar(str.charAt(ind1)) && str.charAt(ind1) != '"' && str.charAt(ind1) != '\n')) {
                        ind1++;
                    }
                    className = str.substring(index, ind1);
                    elementStack.push(new ObjectElement(className));
                    index = ind1;
                    continue;
                    // The list or set has started.
                case '[':
                    index++;
                    while (index < str.length() && (
                            Character.isSpaceChar(str.charAt(index)) || str.charAt(index) == '\n')) {
                        index++;
                    }
                    elementStack.push(new ArrayElement());
                    continue;
                    // The previous element is necessarily a string!
                case '=':
                    element = elementStack.pop();
                    if (element.getType() != ElementType.STRING) {
                        throw new IOException("string expected as property name");
                    }
                    StringElement strel = (StringElement) element;
                    element = elementStack.peek();
                    if (element.getType() != ElementType.OBJECT) {
                        throw new IOException("= not in object");
                    }
                    element.addKey(strel.get());
                    index++;
                    continue;
                    // An item of an object or list has ended.
                case ',':
                    element = elementStack.pop();
                    elementStack.peek().add(element);
                    index++;
                    // To the beginning of the cycle.
                    continue;
                    // The list has ended.
                case ']':
                    // The object has ended.
                case '}':
                    element = elementStack.pop();
                    elementStack.peek().add(element);
                    index++;
                    // Exit case.
                    break;
                default:
                    System.out.println("Parsing error: unexpected symbol");
                    return null;
            }
            // If there is one element in the stack, then that's it.
            if (elementStack.size() == 1) {
                root = elementStack.pop();
                break;
            }
        }
        return root;
    }

    /**
     * Process \-sequences "\\", "\'","\ n","\t","\""
     *
     * @param s
     * @return
     */
    String unescape(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;
        while (index < s.length()) {
            if (s.charAt(index) == '\\') {
                index++;
                switch (s.charAt(index)) {
                    case 'n':
                        stringBuilder.append('\n');
                        break;
                    case 't':
                        stringBuilder.append('\t');
                        break;
                    case '\\':
                        stringBuilder.append('\\');
                        break;
                    case '\"':
                        stringBuilder.append('\"');
                        break;
                    case '\'':
                        stringBuilder.append('\'');
                        break;
                }
            } else {
                stringBuilder.append(s.charAt(index));
            }
            index++;
        }
        return stringBuilder.toString();
    }
}
