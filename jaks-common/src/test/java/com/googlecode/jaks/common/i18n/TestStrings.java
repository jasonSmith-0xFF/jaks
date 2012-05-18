package com.googlecode.jaks.common.i18n;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class TestStrings extends Assert
{
	public interface iAA
	{
	}
	
	public interface iAB extends iAA
	{
	}
	
	public static class A implements iAB
	{
	}
	
	public interface iBA
	{
	}
	
	public interface iBB extends iBA
	{
	}
	
	public static class B extends A implements iBB
	{
	}
	
	@Test
	public void testFindsAllPropertiesResources() throws IOException
	{
		Map<String,String> strings = Strings.getStrings(B.class, Locale.US);
		assertEquals("Wrong value.", "Bravo!", strings.get("A_1"));
		assertEquals("Wrong value.", "Success!", strings.get("B_1"));
		assertEquals("Wrong value.", "Splendid!", strings.get("iAA_1"));
		assertEquals("Wrong value.", "Excellent!", strings.get("iBA_1"));
		assertEquals("Wrong value.", "Awesome!", strings.get("iBB_1"));
	}
	
	@Test
	public void testASimpleOverride() throws IOException
	{
		assertEquals("Override failed.", 
				"Hello.", 
				Strings.getStrings(B.class, Locale.US).get("A_SIMPLE_OVERRIDE"));
	}
	
	@Test
	public void testASimpleOverrideInFrench() throws IOException
	{
		assertEquals("Override failed.", 
				"Bonjour.", 
				Strings.getStrings(B.class, Locale.FRANCE).get("A_SIMPLE_OVERRIDE"));
	}

	
	@Test
	public void testAnOverrideUsingSupers() throws IOException
	{
		assertEquals("Override failed.", 
				"outer inner outer", 
				Strings.getStrings(B.class, Locale.US).get("AN_OVERRIDE_USING_SUPERS"));
	}
	
	@Test
	public void testAnOverrideUsingSuper() throws IOException
	{
		assertEquals("Override failed.", 
				"yxy", 
				Strings.getStrings(B.class, Locale.US).get("AN_OVERRIDE_USING_SUPER"));
	}

	
	@Test
	public void testAnOverrideUsingValues() throws IOException
	{
		assertEquals("Override failed.", 
				"Hello. World.", 
				Strings.getStrings(B.class, Locale.US).get("AN_OVERRIDE_USING_VALUES"));
	}
	
	@Test
	public void demoLocale()
	{
		for(final Locale locale : Arrays.asList(Locale.FRANCE))
		{
			System.out.println("-----------------------------------------------");
			System.out.println("Country:         " + locale.getCountry());
			System.out.println("Display Country: " + locale.getDisplayCountry());
			System.out.println("Language:        " + locale.getLanguage());
			System.out.println("Display Language:" + locale.getDisplayLanguage());
			System.out.println("Variant:         " + locale.getVariant());
			System.out.println("Display Name:    " + locale.getDisplayName());
			System.out.println("Display Script:  " + locale.getDisplayScript());
			System.out.println("ISO3 Language:   " + locale.getISO3Language());
			try
			{
				System.out.println("ISO3 Country:    " + locale.getISO3Country());
			}
			catch(final Exception e)
			{
				System.out.println("ISO3 Country:    ???????????");
			}
			System.out.println("Script:          " + locale.getScript());
		}
	}
}
