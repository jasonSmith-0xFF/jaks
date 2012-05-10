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
package com.googlecode.jsaf.system;

import java.util.ArrayList;
import java.util.List;

/**
 * Thrown on a non-zero exit-code.
 * @author Jason Smith
 */
public class SubprocessException extends Exception
{
    private static final long serialVersionUID = 2125387689173962196L;

    /** The command that was the initial cause of this exception. */
    public final List<String> command;
    
    /** The numeric exit code of the process. */
    public final int exitCode;
    
    /** The working-folder path. */
    public final String path;
    
    SubprocessException(final List<String> command, final int exitCode, final String path, final String message)
    {
        super(message);
        
        this.command = new ArrayList<String>(command);
        this.exitCode = exitCode;
        this.path = path;
    }
    
    SubprocessException(final List<String> command, final int exitCode, final String path)
    {
        this(command, exitCode, path, "");
    }
    
    @Override
    public String toString()
    {
        return getClass().getName() + " [" + exitCode + "] " + path + "$ " + command + "\n" + getMessage();
    }
}

