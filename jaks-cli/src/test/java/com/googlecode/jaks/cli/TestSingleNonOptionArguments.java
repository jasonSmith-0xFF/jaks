package com.googlecode.jaks.cli;

import joptsimple.OptionException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link OptionProcessor} having to do with a single (non-collection or array) 
 * non-option argument.
 * @author Jason Smith
 */
public class TestSingleNonOptionArguments extends Assert
{
	/** Option processor. */
	protected final OptionProcessor proc = new OptionProcessor();

	/**
	 * This command demonstrates a single non-option argument without a defined default value.
	 */
	public static class Required
	{
		/** Expects a required single non-option argument, an integer. */
		@JaksNonOption(required=true)
		public Integer value;
	}
	
	/**
	 * If value is specified, it parses correctly.
	 * @throws Exception See {@link Exception}.
	 */
	@Test 
	public void testRequired() throws Exception
	{
		assertEquals("Value did not parse as expected.",
				Integer.valueOf(42),
				proc.process(new Required(), "42").value);
	}
	
	/**
	 * If marked required, processing should fail if a single non-option argument is not specified.
	 * @throws Exception See {@link Exception}.
	 */
	@Test(expected=OptionException.class)
	public void testRequiredFailsIfNoValue() throws Exception
	{
		proc.process(new Required());
	}
	
	/**
	 * If more than one value, attempt to parse single value should fail.
	 * @throws Exception See {@link Exception}.
	 */
	@Test(expected=OptionException.class)
	public void testRequiredFailsOnMultipleValues() throws Exception
	{
		proc.process(new Required(), "42", "43");
	}
}
