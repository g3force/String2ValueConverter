/*
 * *********************************************************
 * Copyright (c) 2009 - 2015, DHBW Mannheim - Tigers Mannheim
 * Project: TIGERS - Sumatra
 * Date: Oct 27, 2015
 * Author(s): Nicolai Ommer <nicolai.ommer@gmail.com>
 * *********************************************************
 */
package com.github.g3force.s2vconverter;

import java.util.List;


/**
 * @author Nicolai Ommer <nicolai.ommer@gmail.com>
 */
public interface IString2ValueConverter {
    /**
     * @param clazz the implemenating class
     * @return true, if the class can be handled by this converter
     */
    boolean supportedClass(Class<?> clazz);


    /**
     * Parse given String-value according to implementation of this parameter
     *
     * @param impl  the implementing class
     * @param value the value encoded as String
     * @return an instance of type impl with the given value
     */
    Object parseString(final Class<?> impl, final String value);


    /**
     * Parse given String-value according to implementation of this parameter
     *
     * @param impl          the implementing class
     * @param value         the value encoded as String
     * @param genericsImpls possible generics implementations (e.g. for Lists)
     * @return an instance of type impl with the given value
     */
    default Object parseString(
            final Class<?> impl,
            final List<Class<?>> genericsImpls,
            final String value) {
        return null;
    }


    /**
     * Convert an object instance to a persistable and parsable String value
     *
     * @param impl  the implementing class
     * @param value the object instance to convert
     * @return a String representing the value
     */
    String toString(final Class<?> impl, final Object value);
}
