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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Static file utility methods.
 * @author Jason Smith
 */
public class FileUtil 
{
	/**
	 * Return the set of files and folders contained directly under the parent {@code folder}.
	 * If the parent folder does not exist or is empty, return an empty list.
	 * @param folder The parent folder.
	 * @return The set of files and folders contained directly under the parent {@code folder}.
	 */
	public static List<File> listFilesFlat(final File folder)
	{
		return listFilesFlat(folder, Filter.all());
	}
	
	/**
	 * Return the set of files and folders contained directly under the parent {@code folder}.
	 * If the parent folder does not exist or is empty, return an empty list.
	 * Setting a filter to {@code null} is the same as {@link Filter#all()}.
	 * @param folder The parent folder.
	 * @param filter A file filter.
	 * @return The set of files and folders contained directly under the parent {@code folder}.
	 * @see Filter
	 */
	public static List<File> listFilesFlat(final File folder, final FileFilter filter)
	{
		if(!folder.isDirectory())
		{
			return new ArrayList<File>();
		}
		else
		{
			return new ArrayList<File>(Arrays.asList(filter==null?folder.listFiles():folder.listFiles(filter)));
		}
	}
	
	/**
	 * Return the set of files and folders under the parent {@code file} recursively (deep).
	 * Setting a filter to {@code null} is the same as {@link Filter#all()}.
	 * @param file Either a file or a folder.
	 * @param filter A file filter. This is applied after the walking the entire tree to include
	 *             or exclude files on an individual basis.
	 * @param dirFilter A file filter. This is applied to directories as the tree is 
	 *             being walked to determine if they are recursed or ignored. If this filter does
	 *             not accept a directory, it and all of its children will not be considered further.
	 * @return The set of files and folder under the parent {@code file} recursively (deep).
	 * @see Filter
	 */
	public static List<File> listFilesDeep(final File file, final FileFilter filter, final FileFilter dirFilter)
	{
		final List<File> results = new ArrayList<File>();
		if(file.isFile())
		{
			if(filter == null || filter.accept(file))
			{
				results.add(file);
			}
			return results;
		}
		else if(file.isDirectory())
		{
			if(dirFilter == null || dirFilter.accept(file))
			{
				if(filter == null || filter.accept(file))
				{
					results.add(file);
				}
				for(final File child : file.listFiles())
				{
					results.addAll(listFilesDeep(child, filter, dirFilter));
				}
			}
		}
		return results;
	}
}
