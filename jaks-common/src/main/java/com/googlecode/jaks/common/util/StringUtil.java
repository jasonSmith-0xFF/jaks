package com.googlecode.jaks.common.util;

import static java.lang.Character.isWhitespace;

public final class StringUtil 
{
	/**
	 * Private constructor.
	 */
	private StringUtil()
	{
	}

	/** 
	 * Return {@code true} if string is {@code null} or empty (0 length or
	 * composed entirely of whitespace characters).
	 * @param string The string to check.
	 * @return {@code true} if the string is {@code null} or empty.
	 */
	public static boolean isEmpty(final String string)
	{
		if(string == null)
		{
			return true;
		}
		else
		{
			final int len = string.length();
			for(int i=0; i<len; i++)
			{
				if(!isWhitespace(string.charAt(i)))
				{
					return false;
				}
			}
			return true;
		}
	}
}
