package org.mapper;

import org.mapper.parser.*;

import java.io.*;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Class of realising of interface Mapper.
 */
public class Mapper implements ru.hse.homework4.Mapper {
    protected boolean keepIdentity = false;

    // To support KeepIdentity when parsing strings and files.
    private Map<Integer, Object> idMap;

    // To detect dependency cycles.
    // If an object with the same hash has already been mentioned here, it means an exception.
    private Stack<Integer> parsingStack;

    // To support KeepIdentity when writing to strings.
    private IdentityHashMap<Object, Integer> clonesMap;

    /**
     * Constructor eith paramets of keepIdentity.
     *
     * @param keepIdentity if identity is keeping.
     */
    public Mapper(boolean keepIdentity) {
        this.keepIdentity = keepIdentity;
        idMap = new HashMap<>();
        clonesMap = new IdentityHashMap<>();
    }

    /**
     * Method for deserialisation.
     *
     * @param clazz   - class.
     * @param element - element.
     * @param format  - string.
     * @param <T>     - type.
     * @return T.
     */
    private <T> T unserialize(Class<T> clazz, DataElement element, String format) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        if (clazz == LocalDate.class) {
            return clazz.cast(LocalDate.parse(((StringElement) element).get(), dateTimeFormatter));
        }
        if (clazz == LocalTime.class) {
            return clazz.cast(LocalTime.parse(((StringElement) element).get(), dateTimeFormatter));
        }
        if (clazz == LocalDateTime.class) {
            return clazz.cast(LocalDateTime.parse(((StringElement) element).get(), dateTimeFormatter));
        }
        return null;
    }

    /**
     * Method for deserialisation eith two parametrs.
     *
     * @param clazz   - class.
     * @param element - element.
     * @param <T>     - type.
     * @return - T.
     */
    private <T> T unserialize(Class<T> clazz, DataElement element) {

        if (element == null || (element.getType() == ElementType.STRING && element.getClass().equals("0")))
            return null;
        if (clazz == String.class) {
            return clazz.cast(((StringElement) element).get());
        }
        if (clazz == Character.class || clazz.getTypeName().equals("char")) {
            return clazz.cast(((StringElement) element).get().charAt(0));
        }
        if (clazz == Boolean.class) {
            return clazz.cast(Boolean.parseBoolean(((StringElement) element).get()));
        }
        if (clazz == Byte.class) {
            return clazz.cast(Byte.parseByte(((StringElement) element).get()));
        }
        if (clazz == Short.class) {
            return clazz.cast(Short.parseShort(((StringElement) element).get()));
        }
        if (clazz == Integer.class) {
            return clazz.cast(Integer.parseInt(((StringElement) element).get()));
        }
        if (clazz == Long.class) {
            return clazz.cast(Long.parseLong(((StringElement) element).get()));
        }
        if (clazz == Float.class) {
            return clazz.cast(Float.parseFloat(((StringElement) element).get()));
        }
        if (clazz == Double.class) {
            return clazz.cast(Double.parseDouble(((StringElement) element).get()));
        }

        if (clazz == LocalDate.class) {
            return clazz.cast(LocalDate.parse(((StringElement) element).get()));
        }
        if (clazz == LocalTime.class) {
            return clazz.cast(LocalTime.parse(((StringElement) element).get()));
        }
        if (clazz == LocalDateTime.class) {
            return clazz.cast(LocalDateTime.parse(((StringElement) element).get()));
        }


        Set<Class> ints = Set.of(clazz.getInterfaces());
        if (ints.contains(List.class)) {
            if (element.getType() != ElementType.ARRAY) return null;
            try {
                Constructor<T> c = clazz.getConstructor();
                List lst = (List) c.newInstance();
                for (DataElement it : ((ArrayElement) element).get()) {
                    lst.add(unserialize(Object.class, it));
                }
                return clazz.cast(lst);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } else if (ints.contains(Set.class)) {
            if (element.getType() != ElementType.ARRAY) return null;
            try {
                ParameterizedType superclass = (ParameterizedType) ((ParameterizedType)
                        clazz.getGenericSuperclass());
                Constructor<T> c = clazz.getConstructor();
                Set st = (Set) c.newInstance();
                for (DataElement it : ((ArrayElement) element).get()) {
                    st.add(unserialize(Object.class, it));
                }
                return clazz.cast(st);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } else if (clazz.isRecord()) {
            RecordComponent recordComponents[] = clazz.getRecordComponents();

            ObjectElement objectElement = (ObjectElement) element;
            int n = recordComponents.length;
            Class argTypes[] = new Class[n];
            for (int i = 0; i < n; i++) {
                argTypes[i] = recordComponents[i].getType();
            }
            Constructor constructor = null;
            try {
                constructor = clazz.getConstructor(argTypes);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            Object argz[] = new Object[n];
            for (int i = 0; i < n; i++) {
                String name = recordComponents[i].getName();
                if (recordComponents[i].isAnnotationPresent(Ignored.class)) {
                    continue;
                }
                if (recordComponents[i].isAnnotationPresent(PropertyName.class)) {
                    name = recordComponents[i].getAnnotation(PropertyName.class).value();
                }
                if (recordComponents[i].isAnnotationPresent(DateFormat.class)) {
                    argz[i] = unserialize(recordComponents[i].getType(), objectElement.getVal(name),
                            recordComponents[i].getAnnotation(DateFormat.class).value());
                } else {
                    argz[i] = unserialize(recordComponents[i].getType(), objectElement.getVal(name));
                }
            }
            try {
                return clazz.cast(constructor.newInstance(argz));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else if (clazz == Object.class || clazz.isAnnotationPresent(Exported.class)) {
            Object obj = null;
            if (element.getType() == ElementType.STRING) {
                // If the type of data that was written as a string is unknown.
                // Then it is either one of the primitive types or a string or Localdatetime.
                StringElement sel = (StringElement) element;
                if (sel.getClassName() == null || sel.getClassName().isEmpty() ||
                        sel.getClassName().length() == 0 || sel.getClassName().charAt(0) == '\0') {
                    return clazz.cast(sel.get());
                }
                // Checking first char for type.
                switch (sel.getClassName().charAt(0)) {
                    case 'B':
                        return clazz.cast(Boolean.parseBoolean(sel.get()));
                    case 'c':
                        return clazz.cast(sel.get().charAt(0));
                    case 'b':
                        return clazz.cast(Byte.parseByte(sel.get()));
                    case 's':
                        return clazz.cast(Short.parseShort(sel.get()));
                    case 'i':
                        return clazz.cast(Integer.parseInt(sel.get()));
                    case 'l':
                        return clazz.cast(Long.parseLong(sel.get()));
                    case 'f':
                        return clazz.cast(Float.parseFloat(sel.get()));
                    case 'd':
                        return clazz.cast(Double.parseDouble(sel.get()));
                    case 't':
                        return clazz.cast(LocalTime.parse(sel.get()));
                    case 'D':
                        return clazz.cast(LocalDate.parse(sel.get()));
                    case 'T':
                        return clazz.cast(LocalDateTime.parse(sel.get()));
                    default:
                        return null;
                }

            } else if (element.getType() == ElementType.ARRAY) {
                // Unknown object collection type.
                return null;
            } // else:
            ObjectElement objectElement = (ObjectElement) element;
            Class aClass = null;
            try {
                aClass = Class.forName(objectElement.getClassName());
                if (!aClass.isAnnotationPresent(Exported.class))
                    throw new Exception("class is not supported");
                obj = aClass.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (keepIdentity && objectElement.getVal("this") != null) {
                int key = 0;
                try {
                    key = unserialize(Integer.class, objectElement.getVal("this"));
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                // Here idMap is a regular Map and the key is compared in value.
                if (idMap.containsKey(key)) {
                    return clazz.cast(idMap.get(key));
                } else {
                    // Here obj is still empty.
                    idMap.put(key, obj);
                    // So we continue to parse.
                }
            } // else
            for (Field field : aClass.getFields()) {
                if (Modifier.isPublic(field.getModifiers())) {
                    try {
                        if (field.isAnnotationPresent(Ignored.class) || field.isSynthetic()) {
                            continue;
                        }
                        DataElement dataElement = null;
                        if (field.isAnnotationPresent(PropertyName.class)) {
                            dataElement = objectElement.getVal(field.getAnnotation(PropertyName.class).value());
                        } else {
                            dataElement = objectElement.getVal(field.getName());
                        }
                        try {
                            if (dataElement == null || (dataElement.getType() == ElementType.STRING && dataElement.getClassName().equals("0"))) {
                                field.set(obj, null);
                                continue;
                            }
                        } catch (Exception e) {
                            System.out.println("Нельзя null присвоить сюда");
                            e.printStackTrace();
                            return null;
                        }
                        if (field.isAnnotationPresent(DateFormat.class) &&
                                (field.getType() == LocalDate.class ||
                                        field.getType() == LocalTime.class ||
                                        field.getType() == LocalDateTime.class)) {
                            field.set(obj, unserialize(field.getType(), dataElement, field.getAnnotation(DateFormat.class).value()));
                        } else {
                            if (field.getType().getTypeName() == "char") {
                                char c = unserialize(Character.class, dataElement);
                                field.setChar(obj, c);
                            } else if (field.getType().getTypeName() == "byte") {
                                byte c = unserialize(Byte.class, dataElement);
                                field.setByte(obj, c);
                                ;
                            } else if (field.getType().getTypeName() == "short") {
                                short c = unserialize(Short.class, dataElement);
                                field.setShort(obj, c);
                                ;
                            } else if (field.getType().getTypeName() == "int") {
                                int c = unserialize(Integer.class, dataElement);
                                field.setInt(obj, c);
                            } else if (field.getType().getTypeName() == "long") {
                                long c = unserialize(Long.class, dataElement);
                                field.setLong(obj, c);
                            } else if (field.getType().getTypeName() == "float") {
                                float c = unserialize(Float.class, dataElement);
                                field.setFloat(obj, c);
                            } else if (field.getType().getTypeName() == "double") {
                                double c = unserialize(Double.class, dataElement);
                                field.setDouble(obj, c);
                            } else if (field.getType().getTypeName() == "boolean") {
                                boolean c = unserialize(Boolean.class, dataElement);
                                field.setBoolean(obj, c);
                            } else {
                                field.set(obj, unserialize(field.getType(), dataElement));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
            return clazz.cast(obj);

        }
        return null;
    }

    /**
     * Reading from string method.
     *
     * @param clazz a class whose saved instance is located in {@code input}
     * @param input string representation of a saved instance of the {@code class
     *              clazz}
     * @param <T>   - type.
     * @return T.
     */
    @Override
    public <T> T readFromString(Class<T> clazz, String input) {
        Parser parser = new Parser();
        DataElement element = null;
        try {
            element = parser.read(input);
        } catch (Exception e) {
            idMap.clear();
            e.printStackTrace();
        }
        if (keepIdentity) idMap.clear();
        T res = unserialize(clazz, element);
        if (keepIdentity) idMap.clear();
        return res;
    }

    /**
     * Reading from stream method.
     *
     * @param clazz       a class whose saved instance is located in {@code
     *                    inputStream}
     * @param inputStream an input stream containing a string in {@link
     *                    java.nio.charset.StandardCharsets#UTF_8} encoding
     *                    5
     * @param <T>         type.
     * @return - T.
     * @throws IOException - if fail to read.
     */
    @Override
    public <T> T read(Class<T> clazz, InputStream inputStream) throws IOException {
        Parser parser = new Parser();
        DataElement element = null;
        try {
            element = parser.read(inputStream);
        } catch (Exception e) {
            idMap.clear();
            e.printStackTrace();
        }
        if (keepIdentity) idMap.clear();
        T res = unserialize(clazz, element);
        if (keepIdentity) idMap.clear();
        return res;
    }

    /**
     * Reading from file.
     *
     * @param clazz a class whose saved instance is located in {@code
     *              inputStream}
     * @param file  an input file containing a string in {@link
     *              java.nio.charset.StandardCharsets#UTF_8} encoding
     *              5
     * @param <T>   type.
     * @return - T.
     * @throws IOException - if fail to read.
     */
    @Override
    public <T> T read(Class<T> clazz, File file) throws IOException {
        Parser parser = new Parser();
        DataElement element = null;
        try {
            element = parser.read(file);
        } catch (Exception e) {
            idMap.clear();
            e.printStackTrace();
        }
        if (keepIdentity) idMap.clear();
        T res = unserialize(clazz, element);
        if (keepIdentity) idMap.clear();
        return res;

    }

    /**
     * Method for serialisation.
     *
     * @param obj    - object to serialise.
     * @param format - format.
     * @return element.
     * @throws IOException - if fail to serialise.
     */
    private DataElement serialize(Object obj, String format) throws IOException {
        if (obj == null) {
            return new StringElement("0", "");
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(format);
        if (obj instanceof LocalDate) {
            return new StringElement(((LocalDate) obj).format(fmt));
        }
        if (obj instanceof LocalTime) {
            return new StringElement(((LocalTime) obj).format(fmt));
        }
        if (obj instanceof LocalDateTime) {
            return new StringElement(((LocalDateTime) obj).format(fmt));
        }
        return null;
    }

    /**
     * Serialisation of object.
     *
     * @param obj - object.
     * @return - element.
     * @throws IOException - if fail to serialise.
     */
    private DataElement serialize(Object obj) throws IOException {
        if (obj == null) {
            return new StringElement("0", "");
        }

        Class<?> clazz = obj.getClass();

        if (obj instanceof String ||
                obj instanceof Character ||
                obj instanceof Double ||
                obj instanceof Float ||
                obj instanceof Integer ||
                obj instanceof Long ||
                obj instanceof Byte ||
                obj instanceof Short ||
                obj instanceof Boolean ||
                obj instanceof LocalDate ||
                obj instanceof LocalTime ||
                obj instanceof LocalDateTime) {
            String type = "";
            if (obj instanceof Character) {
                type = "c";
            }
            if (obj instanceof Double) {
                type = "d";
            }
            if (obj instanceof Float) {
                type = "f";
            }
            if (obj instanceof Integer) {
                type = "i";
            }
            if (obj instanceof Long) {
                type = "l";
            }
            if (obj instanceof Byte) {
                type = "b";
            }
            if (obj instanceof Short) {
                type = "s";
            }
            if (obj instanceof Boolean) {
                type = "B";
            }
            if (obj instanceof LocalDate) {
                type = "D";
            }
            if (obj instanceof LocalTime) {
                type = "t";
            }
            if (obj instanceof LocalDateTime) {
                type = "T";
            }
            StringElement el = new StringElement(type, obj.toString());
            return el;
        }
        if (obj instanceof List<?>) {
            DataElement element = new ArrayElement();
            for (Object object : (List<?>) obj) {
                element.add(serialize(object));
            }
            return element;
        }
        if (obj instanceof Set<?>) {
            DataElement element = new ArrayElement();
            for (Object objEl : (Set<?>) obj) {
                element.add(serialize(objEl));
            }
            return element;
        }
        if (clazz.isRecord()) {
            ObjectElement element = new ObjectElement(clazz.getTypeName());
            for (RecordComponent recordComponent : clazz.getRecordComponents()) {
                String name = recordComponent.getName();
                try {
                    Method m = clazz.getMethod(name, null);
                    if (recordComponent.isAnnotationPresent(Ignored.class)) {
                        continue;
                    }
                    if (recordComponent.isAnnotationPresent(PropertyName.class)) {
                        element.addKey(recordComponent.getAnnotation(PropertyName.class).value());
                    } else {
                        element.addKey(recordComponent.getName());
                    }
                    if (recordComponent.isAnnotationPresent(DateFormat.class)) {
                        element.add(serialize(m.invoke(obj, null), recordComponent.getAnnotation(DateFormat.class).value()));
                    } else {
                        element.add(serialize(m.invoke(obj, null)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return element;
        }
        if (clazz.isAnnotationPresent(Exported.class)) {
            ObjectElement el = new ObjectElement(clazz.getTypeName());
            if (keepIdentity) {
                int key = 0;
                if (clonesMap.containsKey(obj)) {
                    key = clonesMap.get(obj);
                } else {
                    key = System.identityHashCode(obj);
                    clonesMap.put(obj, key);
                }
                assert (key != 0);
                el.addKey("this");
                el.add(new StringElement(Integer.toString(key)));
            }
            for (Field f : clazz.getFields()) {
                String fName = f.getName();
                String propname = fName;
                if (f.isAnnotationPresent(Ignored.class) || f.isSynthetic()) {
                    continue;
                }
                if (f.isAnnotationPresent(PropertyName.class)) {
                    propname = f.getAnnotation(PropertyName.class).value();
                }
                if (Modifier.isPublic(f.getModifiers())) {
                    try {
                        DataElement val = null;
                        if (f.isAnnotationPresent(DateFormat.class) &&
                                (f.getType() == LocalDate.class ||
                                        f.getType() == LocalTime.class ||
                                        f.getType() == LocalDateTime.class)) {
                            val = serialize(f.get(obj), f.getAnnotation(DateFormat.class).value());
                        } else {
                            val = serialize(f.get(obj));
                        }
                        el.addKey(propname);
                        el.add(val);
                    } catch (Exception e) {
                        throw new IOException("fail to parse", e);
                    }
                }
            }
            return el;
        } else {
            String msg = "Failed to serialize obj of class ";
            msg += clazz.getName();
            throw new IOException(msg);
        }


    }

    /**
     * Writing to string object.
     *
     * @param object object to save
     * @return - string.
     * @throws IOException - if fail to write.
     */
    @Override
    public String writeToString(Object object) throws IOException {
        if (keepIdentity) {
            clonesMap.clear();
        }
        DataElement el = serialize(object);
        if (keepIdentity) {
            clonesMap.clear();
        }
        String s = el.toString();
        return s;
    }

    /**
     * Write to stream object.
     *
     * @param object       object to save.
     * @param outputStream - stream.
     * @throws IOException - if fail to write.
     */
    @Override
    public void write(Object object, OutputStream outputStream) throws IOException {
        String str = writeToString(object);
        if (str == null) {
            throw new IOException("Object unparsible");
        }
        outputStream.write(str.getBytes(java.nio.charset.Charset.forName("UTF-8")));

    }

    /**
     * Write object to file.
     *
     * @param object object to save.
     * @param file   - file.
     * @throws IOException - fail to write.
     */
    @Override
    public void write(Object object, File file) throws IOException {
        OutputStream out = new FileOutputStream(file, true);
        write(object, out);
    }
}
