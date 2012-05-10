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
package com.googlecode.jsaf.archive;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.jsaf.archive.resource.AbstractNode;
import com.googlecode.jsaf.archive.resource.FileResource;
import com.googlecode.jsaf.archive.resource.Folder;

public final class ArchiveUtil 
{
	private ArchiveUtil()
	{
	}
	
	public static List<AbstractNode> walkFolder(final File folder) throws FileNotFoundException
	{
		return recursiveWalkFolder(folder, "");
	}
	
	private static List<AbstractNode> recursiveWalkFolder(final File folder, final String path) throws FileNotFoundException
	{
		final List<AbstractNode> results = new ArrayList<AbstractNode>();
		for(final File file : folder.listFiles())
		{
			final String nodePath = path + (path.isEmpty()?"":"/") + file.getName();
			if(file.isFile())
			{
				results.add(new FileResource(nodePath, file));
			}
			else if(file.isDirectory())
			{
				results.add(new Folder(nodePath));
				results.addAll(recursiveWalkFolder(file, nodePath));
			}
		}
		return results;
	}
}
