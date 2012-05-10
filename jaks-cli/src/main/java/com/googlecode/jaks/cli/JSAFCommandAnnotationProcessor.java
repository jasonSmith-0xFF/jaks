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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.StandardLocation;

/**
 * Catalogs classes that are annotated with {@link JSAFCommand} and 
 * puts this information into the JAR at <tt>META-INF/jsaf/command-classes.txt</tt>.
 * This supports automated discovery of command classes. 
 * @author Jason Smith
 */
@SupportedAnnotationTypes(value={"com.googlecode.jsaf.cli.JSAFCommand"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class JSAFCommandAnnotationProcessor extends AbstractProcessor
{
	private ProcessingEnvironment processingEnv = null;

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) 
	{
		super.init(processingEnv);
		this.processingEnv = processingEnv;
	}

	@Override
	public boolean process(
			final Set<? extends TypeElement> elements,
			final RoundEnvironment roundEnv) 
	{
		final List<String> annotatedClasses = new ArrayList<String>();
		
		for(final Element element : roundEnv.getElementsAnnotatedWith(JSAFCommand.class))
		{
			if(element.getKind() == ElementKind.CLASS && element.getAnnotation(JSAFCommand.class) != null)
			{
				TypeElement typeElement = (TypeElement)element;
				if(typeElement.getModifiers().contains(Modifier.PUBLIC)
						&& !typeElement.getModifiers().contains(Modifier.ABSTRACT))
				{
					annotatedClasses.add(getCanonicalClassName(element));
				}
			}
		}
		
		if(!annotatedClasses.isEmpty())
		{
			try(final PrintWriter printer = new PrintWriter(
					processingEnv.getFiler().createResource(
							StandardLocation.CLASS_OUTPUT, 
							"", 
							"META-INF/jsaf/command-classes.txt").openWriter()))
			{
				for(final String annotatedClass : annotatedClasses)
				{
					printer.println(annotatedClass);
				}
			} 
			catch(final IOException e) 
			{
				processingEnv.getMessager().printMessage(Kind.WARNING, e.toString());
			}
		}
		return false;
	}
	
	/**
	 * Inspect the element to put the class package/name in Java-canonical form. That is, package
	 * names are separated by a '.'; inner-classes are separated by a '$'.
	 * @param element The element.
	 * @return The class name in Java-canonical form.
	 */
	protected String getCanonicalClassName(final Element element)
	{
		if("PACKAGE".equals(element.getKind().name()))
		{
			return element.toString();
		}
		else if("CLASS".equals(element.getKind().name()))
		{
			return getCanonicalClassName(element.getEnclosingElement()) +
					("CLASS".equals(element.getEnclosingElement().getKind().name())?"$":".") +
					element.getSimpleName().toString();
		}
		else
		{
			throw new IllegalArgumentException(
					"Don't know what to do with a " + 
					element + 
					" (" + element.getKind().name() + ").");
		}
	}
}
