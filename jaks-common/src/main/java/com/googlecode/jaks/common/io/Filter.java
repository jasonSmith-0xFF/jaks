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
package com.googlecode.jaks.common.io;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

/**
 * A stock set of file filters.
 * @author Jason Smith
 */
public final class Filter 
{
	/**
	 * Private constructor.
	 */
	private Filter()
	{
	}
	
	/**
	 * Accept all files and directories.
	 * @return All files and directories.
	 */
	public static FileFilter all()
	{
		return 
			new FileFilter()
			{
				@Override
				public boolean accept(final File file) 
				{
					return true;
				}
			};
	}
	
	/**
	 * Accept files and directories with the given names.
	 * @param names The list of names.
	 * @return Files and directories with the given names.
	 */
	public static FileFilter name(final String... names)
	{
		return 
			new FileFilter()
			{
				@Override
				public boolean accept(final File file) 
				{
					return Arrays.asList(names).contains(file.getName());
				}
			};
	}
	
	/**
	 * Accept files, reject directories.
	 * @return Files, but not directories.
	 */
	public static FileFilter file()
	{
		return 
			new FileFilter()
			{
				@Override
				public boolean accept(final File file) 
				{
					return file.isFile();
				}
			};
	}
	
	/**
	 * Accept only hidden files.
	 * @return Hidden files, but not others.
	 */
	public static FileFilter hidden()
	{
		return 
			new FileFilter()
			{
				@Override
				public boolean accept(final File file) 
				{
					return file.isHidden();
				}
			};
	}
	
	/**
	 * Accept directories, reject files.
	 * @return Directories, but not files.
	 */
	public static FileFilter directory()
	{
		return 
			new FileFilter()
			{
				@Override
				public boolean accept(final File file) 
				{
					return file.isDirectory();
				}
			};
	}
	
	/**
	 * Reject directories <tt>.svn</tt> and <tt>_svn</tt>; accept anything else.
	 * @return All files and directories except directories <tt>.svn</tt> and <tt>_svn</tt>.
	 */
	public static FileFilter svnAware()
	{
		return 
			new FileFilter()
			{
				@Override
				public boolean accept(final File file) 
				{
					return !(file.isDirectory() && Arrays.asList(".svn", "_svn").contains(file.getName()));
				}
			};
	}
	
	/**
	 * Reject directory <tt>target</tt>; accept anything else.
	 * @return All files and directories except directory <tt>target</tt>.
	 */
	public static FileFilter mavenAware()
	{
		return 
			new FileFilter()
			{
				@Override
				public boolean accept(final File file) 
				{
					return !(file.isDirectory() && "target".equals(file.getName()));
				}
			};
	}
	
	/**
	 * Accept a file if any of the included filters accept it.
	 * @param filters The list of filters.
	 * @return Any file that is accepted by one or more of the filters.
	 */
	public static FileFilter or(final FileFilter... filters)
	{
		return 
			new FileFilter()
			{
				@Override
				public boolean accept(final File file) 
				{
					for(final FileFilter filter : filters)
					{
						if(filter.accept(file))
						{
							return true;
						}
					}
					return false;
				}
			};
	} 
	
	/**
	 * Accept a file if all of the included filters accept it.
	 * @param filters The list of filters.
	 * @return Any file that is accepted by all of the filters.
	 */
	public static FileFilter and(final FileFilter... filters)
	{
		return 
			new FileFilter()
			{
				@Override
				public boolean accept(final File file) 
				{
					for(final FileFilter filter : filters)
					{
						if(!filter.accept(file))
						{
							return false;
						}
					}
					return true;
				}
			};
	}
	
	/**
	 * Invert the output of the filter, turning accept into reject and vice versa.
	 * @param filter The filter.
	 * @return Any file that is rejected by the wrapped filter.
	 */
	public static FileFilter not(final FileFilter filter)
	{
		return 
			new FileFilter()
			{
				@Override
				public boolean accept(final File file) 
				{
					return !filter.accept(file);
				}
			};
	} 
}
