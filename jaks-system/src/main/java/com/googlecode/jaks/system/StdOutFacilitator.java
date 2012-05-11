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
package com.googlecode.jaks.system;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>Thread-worker that pumps an input stream <i>completely</i>
 * to an output stream.</p>  
 * @author Jason Smith
 */
class StdOutFacilitator extends AbstractFacilitator
{
    /**
     * Constructor. 
     * @param baseThreadName Base name of the thread.
     * @param in The input stream; either "stdout" or "stderr" of the process.
     * @param out The destination output.
     */
    public StdOutFacilitator(String baseThreadName, InputStream in, OutputStream out)
    {
        super(baseThreadName, in, out);
    }
    
    /*
     * @see java.lang.Thread#run()
     */
    public void run()
    {
        try
        {
                 byte[] b = new byte[4096];
             while(true)
             {
                 int len = in.read(b);
                 if(len == -1) break;
                 out.write(b, 0, len);
             }
             out.flush();
        }
        catch(Throwable t)
        {
            error = t;
        }
    }
}

