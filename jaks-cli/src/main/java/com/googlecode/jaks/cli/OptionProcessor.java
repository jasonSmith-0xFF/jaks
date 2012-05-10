/*
 * Copyright (C) 2012 by Jason Smith
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.jaks.cli;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

public class OptionProcessor 
{
	private static final List<Class<?>> BOOLEAN_CLASSES = Arrays.asList(new Class<?>[]{Boolean.class, boolean.class});
	
	/**
	 * Process a command instance, injecting the values from {@code args} into the annotated fields of the {@link command}.
	 * @param command The command instance.
	 * @param args The command-line args.
	 * @return The command instance.
	 * @throws Exception See {@link Exception}.
	 */
	public <T> T process(final T command, final String... args) throws Exception
	{
		initializeCommand(command, initializeOptionParser(command).parse(args));
		return command;
	}
	
	/**
	 * Initialize an option parser with the annotations from the {@code command} instance.
	 * @param command The command class.
	 * @return The option parser.
	 */
	public OptionParser initializeOptionParser(final Object command)
	{
		final OptionParser optionParser = new OptionParser();
		optionParser.posixlyCorrect(false);
		
		for(final Field field : command.getClass().getFields())
		{
			final JSAFOption option = field.getAnnotation(JSAFOption.class);
			if(option != null)
			{
				initializeAnOption(optionParser, field, option.name(), option.description(), option.separator(), option.required());
			}
		}
		
		return optionParser;
	}
	
	protected void initializeAnOption(
			final OptionParser optionParser, 
			final Field field, 
			final String[] optionName, 
			final String optionDescription,
			final char optionSeparator,
			final boolean optionIsRequired)
	{
		final OptionSpecBuilder builder = optionParser.acceptsAll(Arrays.asList(optionName), optionDescription);
		if(!BOOLEAN_CLASSES.contains(field.getType()))
		{
			Class<?> type = field.getType();
			if(field.getType().isArray())
			{
				type = field.getType().getComponentType();
			}
			else if(Collection.class.isAssignableFrom(field.getType()))
			{
				type = String.class;
				final ParameterizedType paramType = (ParameterizedType)field.getGenericType();
				if(paramType != null && paramType.getActualTypeArguments().length > 0)
				{
					type = (Class<?>)paramType.getActualTypeArguments()[0];
				}
			}
			
			final ArgumentAcceptingOptionSpec<?> optionSpec = builder.withRequiredArg().ofType(type);
			
			if('\u0000' != optionSeparator)
			{
				optionSpec.withValuesSeparatedBy(optionSeparator);
			}
			
			if(optionIsRequired)
			{
				optionSpec.required();
			}
		}
	}
	
	protected Class<?> getGenericType(final Field field)
	{
		final ParameterizedType paramType = (ParameterizedType)field.getGenericType();
		if(paramType != null && paramType.getActualTypeArguments().length > 0)
		{
			return (Class<?>)paramType.getActualTypeArguments()[0];
		}
		else
		{
			return null;
		}
	}

	/**
	 * Inject the values from the option set into the command instance.
	 * @param command The command class.
	 * @param optionSet The set of options created by parsing a set of command-line arguments.
	 * @throws Exception See {@link Exception}.
	 */
	public void initializeCommand(final Object command, final OptionSet optionSet) throws Exception
	{
		for(final Field field : command.getClass().getFields())
		{
			final JSAFOption option = field.getAnnotation(JSAFOption.class);
			if(option != null)
			{
				processOptionAnnotation(command, field, optionSet, option.name());
			}
			else
			{
				final JSAFNonOptionArguments nonOption = field.getAnnotation(JSAFNonOptionArguments.class);
				if(nonOption != null)
				{
					processNonOptionArgumentsAnnotation(command, field, optionSet);
				}
			}
		}
	}
	
	protected void processOptionAnnotation(final Object command, final Field field, final OptionSet optionSet, final String[] optionName) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		if(BOOLEAN_CLASSES.contains(field.getType()))
		{
			field.setAccessible(true);
			if(optionSet.has(optionName[0]))
			{
				field.set(command, true);
			}
			else
			{
				field.set(command, false);
			}
		}
		else
		{
			field.setAccessible(true);
			if(field.getType().isArray())
			{
				final List<?> values = optionSet.valuesOf(optionName[0]);
				final Object array = Array.newInstance(field.getType().getComponentType(), values.size());
				for(int i=0; i<values.size(); i++)
				{
					Array.set(array, i, values.get(i));
				}
				field.set(command, array);
			}
			else if(Collection.class.isAssignableFrom(field.getType()))
			{
				final List<?> values = optionSet.valuesOf(optionName[0]);
				if(field.getType().isInterface())
				{
					if(List.class.isAssignableFrom(field.getType()))
					{
						field.set(command, new ArrayList<Object>(values));
					}
					else if(Set.class.isAssignableFrom(field.getType()))
					{
						field.set(command, new LinkedHashSet<Object>(values));
					}
					else
					{
						field.set(command, new ArrayList<Object>(values));
					}
				}
				else
				{
					field.set(command, field.getType().getConstructor(Collection.class).newInstance(values));
				}
			}
			else
			{
				field.set(command, optionSet.valueOf(optionName[0]));
			}
		}
	}
	
	protected void processNonOptionArgumentsAnnotation(final Object command, final Field field, final OptionSet optionSet) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		/*
		 * JOpt-simple returns non-option arguments as a list of strings, always. 
		 * I wanted to be a little more sophisticated.
		 * So I create a fake options-set out of the non-option arguments, which
		 * allows me to process them the same way I process other options. That is,
		 * I can support any collection type and standard type conversions (File, int, etc.).
		 */
		final OptionParser fakeParser = new OptionParser();
		initializeAnOption(fakeParser, field, new String[]{"option"}, "This is a fake option", '\u0000', false);
		
		final List<String> fakeArgs = new ArrayList<String>();
		for(final String arg : optionSet.nonOptionArguments())
		{
			fakeArgs.add("--option");
			fakeArgs.add(arg);
		}
		
		final OptionSet fakeOptionSet = fakeParser.parse(fakeArgs.toArray(new String[fakeArgs.size()]));
		processOptionAnnotation(command, field, fakeOptionSet, new String[]{"option"});
	}
}
