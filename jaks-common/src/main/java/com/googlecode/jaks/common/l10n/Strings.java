package com.googlecode.jaks.common.l10n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import org.mvel2.templates.TemplateRuntime;

import com.googlecode.jaks.common.SquashedException;
import com.googlecode.jaks.common.io.StreamUtil;

public class Strings 
{
	private static Map<Class<?>,Map<String,Map<String,String>>> cachedStrings = new HashMap<>();
	
	private static Map<String,Map<String,String>> cachedResults = new HashMap<>();
	
	/**
	 * Format using a template.
	 * @param object The object used as a reference when searching for localization data.
	 * <tt>object</tt> is the currently running command instance.
	 * Localization strings are available by name. Ex., <tt>@{MSG_COMMAND_NAME}</tt> will find
	 * the property value "MSG_COMMAND_NAME" from the localization data.
	 * @param template MVEL2 template or literal string.
	 * @param locale The locale.
	 * @param values Values passed in to be formatted.
	 * @return The formatted text.
	 * @throws IOException See {@link IOException}.
	 */
	public static String format(final Object object, final Locale locale, final String template, final Object... values) throws IOException
	{
		final Map<String,String> strings = Strings.getStrings(object.getClass(), locale);
		final Map<String,Object> vars = new HashMap<>();
		vars.putAll(strings);
		vars.put("object", object);
		return String.format(locale, Strings.evalTemplate(template, vars), values);
	}
	
	/**
	 * Format using a template.
	 * @param object The object used as a reference when searching for localization data.
	 * <tt>object</tt> is the currently running command instance.
	 * Localization strings are available by name. Ex., <tt>@{MSG_COMMAND_NAME}</tt> will find
	 * the property value "MSG_COMMAND_NAME" from the localization data.
	 * @param template MVEL2 template or literal string.
	 * @param values Values passed in to be formatted.
	 * @return The formatted text.
	 * @throws IOException See {@link IOException}.
	 */
	public static String format(final Object object, final String template, final Object... values) throws IOException
	{
		return format(object, Locale.getDefault(), template, values);
	}



	public synchronized static Map<String,String> getStrings(final Class<?> clazz, final Locale locale) throws IOException
	{
		String lang;
		try
		{
			lang = locale.getISO3Language();
		}
		catch(final MissingResourceException e)
		{
			lang = null;
		}
		
		String country;
		try
		{
			country = locale.getISO3Country();
		}
		catch(final MissingResourceException e)
		{
			country = null;
		}
		
		final String localeKey = lang + "_" + country;
		if(cachedResults.containsKey(localeKey))
		{
			return cachedResults.get(localeKey);
		}
		else
		{
			Map<String,String> results = new HashMap<String,String>();
			
			results.putAll(getStrings(clazz, "eng"));
			if(lang != null)
			{
				results.putAll(getStrings(clazz, lang));
			}
			if(lang != null && country != null)
			{
				results.putAll(getStrings(clazz, lang + "_" + country));
			}
			
			cachedResults.put(localeKey, Collections.unmodifiableMap(results));
			
			return cachedResults.get(localeKey);
		}
	}
	
	private static Map<String,String> getStrings(final Class<?> clazz, final String locale) throws IOException
	{
		if(clazz == null)
		{
			throw new IllegalArgumentException("Class not specified.");
		}
		
		if(locale == null)
		{
			throw new IllegalArgumentException("Locale not specified.");
		}
		
		if(cachedStrings.containsKey(clazz) && cachedStrings.get(clazz).containsKey(locale))
		{
			return cachedStrings.get(clazz).get(locale);
		}
		else
		{
			Map<String,String> results = new HashMap<String,String>();
			
			for(final Class<?> interfaze : clazz.getInterfaces())
			{
				results.putAll(getStrings(interfaze, locale));
			}
			
			for(final Class<?> interfaze : clazz.getInterfaces())
			{		
				final Properties properties = getProperties(interfaze, locale);
				for(final Object key : properties.keySet())
				{
					Map<String,Object> vars = new HashMap<String,Object>();
					vars.put("supers", getStrings(interfaze, locale));
					vars.put("values", Collections.unmodifiableMap(results));
					vars.put("super", getStrings(interfaze, locale).get((String)key));

					results.put(key==null?"":(String)key, evalTemplate(properties.getProperty((String)key), vars));
				}
			}
			
			final Map<String,String> classDefaults = 
					clazz.getSuperclass()==null
						?new HashMap<String,String>()
						:getStrings(clazz.getSuperclass(), locale);
			results.putAll(classDefaults);
			
			final Properties properties = getProperties(clazz, locale);
			for(final Object key : properties.keySet())
			{
				Map<String,Object> vars = new HashMap<String,Object>();
				vars.put("supers", classDefaults);
				vars.put("values", Collections.unmodifiableMap(results));
				vars.put("super", classDefaults.get((String)key));
				
				results.put(key==null?"":(String)key, evalTemplate(properties.getProperty((String)key), vars));
			}
			
			if(!cachedStrings.containsKey(clazz))
			{
				cachedStrings.put(clazz, new HashMap<String,Map<String,String>>());
			}
			cachedStrings.get(clazz).put(locale, Collections.unmodifiableMap(results));
			
			return cachedStrings.get(clazz).get(locale);
		}
	}
	
	private static Properties getProperties(final Class<?> clazz, final String locale) throws IOException
	{
		final String propPath = clazz.getName().replace(".", "/") + "." + locale + ".properties";
		final Properties properties = new Properties();
		final InputStream resourceStream = Strings.class.getClassLoader().getResourceAsStream(propPath);
		if(resourceStream != null)
		{
			try(final Reader reader = new InputStreamReader(Strings.class.getClassLoader().getResourceAsStream(propPath), StreamUtil.UTF8))
			{
				properties.load(reader);
			}
			catch(final UnsupportedEncodingException e)
			{
				return SquashedException.raise(e);
			}
		}
		return properties;
	}
	
	public static String evalTemplate(final String template, final Map<String,?> vars)
	{
		return (String)TemplateRuntime.eval(template, vars);
	}
}
