/*
 * *********************************************************
 * Copyright (c) 2009 - 2014, DHBW Mannheim - Tigers Mannheim
 * Project: TIGERS - Sumatra
 * Date: Mar 17, 2014
 * Author(s): Nicolai Ommer <nicolai.ommer@gmail.com>
 * *********************************************************
 */
package com.github.g3force.s2vconverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;


/**
 * Convert values of certain important classes from string to value and vice
 * versa.
 * Override this class to add support for your own classes.
 */
public class String2ValueConverter implements IString2ValueConverter
{
    private static final Logger log = LogManager.getLogger(String2ValueConverter.class.getName());

    private final List<IString2ValueConverter> converters = new ArrayList<>();

    private static final String2ValueConverter INST = new String2ValueConverter();

    static
    {
        ServiceLoader<IString2ValueConverter> serviceLoader = ServiceLoader.load(IString2ValueConverter.class);
        serviceLoader.forEach(INST::addConverter);
    }


    /**
     * @return the default (singleton) instance with all known converters
     */
    public static String2ValueConverter getDefault()
    {
        return INST;
    }


    /**
     * @param converter the converter to add
     */
    public void addConverter(final IString2ValueConverter converter)
    {
        for (IString2ValueConverter existingConverter : converters)
        {
            if (existingConverter.getClass().equals(converter.getClass()))
            {
                // converter exists
                return;
            }
        }
        log.debug("Adding converter: {}", converter);
        converters.add(converter);
    }


    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object parseString(final Class<?> impl, final String value)
    {
        for (IString2ValueConverter c : converters)
        {
            if (c.supportedClass(impl))
            {
                return c.parseString(impl, value);
            }
        }

        if (impl.equals(Integer.class) || impl.equals(Integer.TYPE))
        {
            try
            {
                return Integer.valueOf(value);
            } catch (NumberFormatException err)
            {
                return 0;
            }
        } else if (impl.equals(Long.class) || impl.equals(Long.TYPE))
        {
            try
            {
                return Long.valueOf(value);
            } catch (NumberFormatException err)
            {
                log.error("Could not parse value.", err);
                return 0L;
            }
        } else if (impl.equals(Float.class) || impl.equals(Float.TYPE))
        {
            try
            {
                return Float.valueOf(value);
            } catch (NumberFormatException err)
            {
                log.error("Could not parse value.", err);
                return 0.0f;
            }
        } else if (impl.equals(Double.class) || impl.equals(Double.TYPE))
        {
            try
            {
                return Double.valueOf(value);
            } catch (NumberFormatException err)
            {
                log.error("Could not parse value.", err);
                return 0.0;
            }
        } else if (impl.equals(Boolean.class) || impl.equals(Boolean.TYPE))
        {
            return Boolean.valueOf(value);
        } else if (impl.equals(String.class))
        {
            return value;
        } else if (impl.isEnum())
        {
            return Enum.valueOf(((Class<Enum>) impl), value);
        } else
        {
            throw new IllegalStateException("Unknown implementation: " + impl
                + ". Can not parse string.");
        }
    }


    @Override
    public Object parseString(final Class<?> impl,
        final List<Class<?>> genericsImpls, final String value)
    {
        for (IString2ValueConverter c : converters)
        {
            if (c.supportedClass(impl))
            {
                return c.parseString(impl, genericsImpls, value);
            }
        }

        if (impl.equals(List.class))
        {
            String[] values = value.split(";");
            List<Object> list = new ArrayList<>(values.length);
            for (String val : values)
            {
                list.add(parseString(genericsImpls.get(0), val));
            }
            return list;
        }
        throw new IllegalStateException("Unknown implementation: " + impl
            + ". Can not parse string.");
    }


    @Override
    public String toString(final Class<?> impl, final Object value)
    {
        for (IString2ValueConverter c : converters)
        {
            if (c.supportedClass(impl))
            {
                return c.toString(impl, value);
            }
        }

        if (impl.equals(Integer.class) || impl.equals(Integer.TYPE))
        {
            return Integer.toString((Integer) value);
        } else if (impl.equals(Long.class) || impl.equals(Long.TYPE))
        {
            return Long.toString((Long) value);
        } else if (impl.equals(Float.class) || impl.equals(Float.TYPE))
        {
            return Float.toString((Float) value);
        } else if (impl.equals(Double.class) || impl.equals(Double.TYPE))
        {
            return Double.toString((Double) value);
        } else if (impl.equals(Boolean.class) || impl.equals(Boolean.TYPE))
        {
            return Boolean.toString((Boolean) value);
        } else if (impl.isEnum())
        {
            return ((Enum<?>) value).name();
        } else if (impl.equals(String.class))
        {
            return value.toString();
        } else
        {
            throw new IllegalStateException("Unknown implementation: " + impl
                + ". Can not parse string.");
        }
    }


    /**
     * @param value either a Class or a String
     * @return the implementing class
     */
    public static Class<?> getClassFromValue(final Object value)
    {
        if (value.getClass() == Class.class)
        {
            return (Class<?>) value;
        }
        String clazz = (String) value;
        if (clazz.equals("int"))
        {
            return Integer.TYPE;
        }
        if (clazz.equals("long"))
        {
            return Long.TYPE;
        }
        if (clazz.equals("float"))
        {
            return Float.TYPE;
        }
        if (clazz.equals("double"))
        {
            return Double.TYPE;
        }
        if (clazz.equals("boolean"))
        {
            return Boolean.TYPE;
        }
        try
        {
            return Class.forName(clazz);
        } catch (ClassNotFoundException err)
        {
            return String.class;
        }
    }


    @Override
    public boolean supportedClass(final Class<?> clazz)
    {
        return false;
    }
}
