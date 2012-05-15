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
	
	/**
	 * Takes a single {@link int} as a non-option argument and returns it
	 * via stdout.
	 * @author Jason Smith
	 */
	public static class Primitive
	{
		@JaksNonOption
		public int value = 256;
	}
	
	/**
	 * Verify that when I pass in a single non-option argument, it is passed back correctly.
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testPrimitivePassOneValue() throws Exception
	{
		assertEquals("Returned the wrong value.", 
				42, 
				proc.process(new Primitive(), "42").value);
	}
	
	/**
	 * The command specifies a single value, not a collection or array; verify that when I pass more than one
	 * value, the command throws an exception.
	 * @throws Exception See {@link Exception}.
	 */
	@Test(expected=OptionException.class)
	public void testPrimitivePassTwoValuesNotAllowed() throws Exception
	{
		proc.process(new Primitive(), "42", "43");
	}
	
	/**
	 * Verify that we get the default value back if no explicit value is specified.
	 * Primitive types always have a default value, usually 0. In this case, I have set
	 * a non-standard default for testing.
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testPassZeroValues() throws Exception
	{
		assertEquals("Didn't return the expected default value.", 
				256, 
				proc.process(new Primitive()).value);
	}
	
	/**
	 * Verify that command throws exception if there is a conversion error.
	 * @throws Exception See {@link Exception}.
	 */
	@Test(expected=OptionException.class) 
	public void testBadConversion() throws Exception
	{
		proc.process(new Primitive(), "wont-convert-to-int");
	}
	
	
	/**
	 * Takes a single boolean parameter ({@code --test}); 
	 * writes "true" to stdout if present, "false" if not.
	 * @author Jason Smith
	 */
	public static class BooleanParam
	{
		/**
		 * The {@code --test} flag.
		 */
		@JaksOption(name="test", description="Test for a boolean.")
		public boolean test;
	}

	/**
	 * Setting the {@code --test} flag results in "true".
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testBooleanSet() throws Exception
	{
		assertEquals("Wrong value.", 
				true, 
				proc.process(new BooleanParam(), "--test").test);
	}
	
	/**
	 * Not setting the {@code --test} flag results in "false".
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testBooleanClear() throws Exception
	{
		assertEquals("Wrong value.", 
				false, 
				proc.process(new BooleanParam()).test);
	}
	
	/**
	 * Setting the {@code --test} flag is okay, and still results in "true".
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testSet2() throws Exception
	{
		assertEquals("Wrong value.", 
				true, 
				proc.process(new BooleanParam(), "--test", "--test").test);
	}
	
	/**
	 * Setting the {@code --illegal-option} flag results in an error.
	 * @throws Exception See {@link Exception}.
	 */
	@Test(expected=OptionException.class)
	public void testIllegalOption() throws Exception
	{
		proc.process(new BooleanParam(), "--illegal-option");
	}
}
