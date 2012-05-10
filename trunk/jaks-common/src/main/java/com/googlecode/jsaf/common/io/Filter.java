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
package com.googlecode.jsaf.common.io;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

public class Filter 
{
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
