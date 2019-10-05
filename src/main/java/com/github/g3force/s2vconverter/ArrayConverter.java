/*
 * *********************************************************
 * Copyright (c) 2009 - 2015, DHBW Mannheim - Tigers Mannheim
 * Project: TIGERS - Sumatra
 * Date: Oct 27, 2015
 * Author(s): Nicolai Ommer <nicolai.ommer@gmail.com>
 * *********************************************************
 */
package com.github.g3force.s2vconverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;


/**
 * Convert arrays.
 */
public class ArrayConverter implements IString2ValueConverter {
    private static final Logger log = LogManager.getLogger(ArrayConverter.class.getName());

    private final IString2ValueConverter parent;


    /**
     * Default no-arg constructor for service loader
     */
    public ArrayConverter() {
        parent = String2ValueConverter.getDefault();
    }

    /**
     * @param parent the parent converter for converting array values
     */
    public ArrayConverter(final IString2ValueConverter parent) {
        this.parent = parent;
    }


    @Override
    public boolean supportedClass(final Class<?> clazz) {
        return clazz.isArray();
    }


    @Override
    public Object parseString(final Class<?> impl, final String value) {
        String[] split = value.split(";");
        Object[] arr;
        try {
            arr = (Object[]) Array.newInstance(impl.getComponentType(),
                    split.length);

        } catch (ClassCastException err) {
            log.error(
                    "Could not create array of type "
                            + impl.getComponentType(),
                    err);
            return new Object[0];
        }
        for (int i = 0; i < split.length; i++) {
            arr[i] = parent.parseString(impl.getComponentType(), split[i]);
        }
        return arr;
    }


    @Override
    public String toString(final Class<?> impl, final Object value) {
        Object[] arr = convertPrimitiveArrays(value);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object v : arr) {
            if (!first) {
                sb.append(';');
            }
            first = false;
            sb.append(parent.toString(impl.getComponentType(), v));
        }
        return sb.toString();
    }


    private Object[] convertPrimitiveArrays(final Object arr) {
        if (arr instanceof Object[]) {
            return (Object[]) arr;
        }
        Object[] ret;
        if (arr instanceof int[]) {
            int[] pArr = (int[]) arr;
            ret = new Integer[pArr.length];
            for (int i = 0; i < pArr.length; i++) {
                ret[i] = pArr[i];
            }
        } else if (arr instanceof long[]) {
            long[] pArr = (long[]) arr;
            ret = new Long[pArr.length];
            for (int i = 0; i < pArr.length; i++) {
                ret[i] = pArr[i];
            }
        } else if (arr instanceof float[]) {
            float[] pArr = (float[]) arr;
            ret = new Float[pArr.length];
            for (int i = 0; i < pArr.length; i++) {
                ret[i] = pArr[i];
            }
        } else if (arr instanceof double[]) {
            double[] pArr = (double[]) arr;
            ret = new Double[pArr.length];
            for (int i = 0; i < pArr.length; i++) {
                ret[i] = pArr[i];
            }
        } else if (arr instanceof char[]) {
            char[] pArr = (char[]) arr;
            ret = new Character[pArr.length];
            for (int i = 0; i < pArr.length; i++) {
                ret[i] = pArr[i];
            }
        } else {
            throw new IllegalArgumentException("Unknown array implementation: "
                    + arr);
        }
        return ret;
    }
}
