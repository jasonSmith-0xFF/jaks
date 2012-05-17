package com.googlecode.jaks.examples;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import com.googlecode.jaks.cli.AbstractJaksCommand;
import com.googlecode.jaks.cli.JaksNonOption;
import com.googlecode.jaks.system.Subprocess;

/**
 * <a href="http://programmingishard.blogspot.com/2012/04/shrinking-decades-worth-of-video-clips.html">ProgrammingIsHard:
 * Shrinking Decade's Worth of Video Clips</a>.
 * @author Jason Smith
 */
public class Transcode extends AbstractJaksCommand
{
	@JaksNonOption(required=true)
	public List<File> vids;

	@Override
	public void execute() throws Exception 
	{
		for(final File vid : vids)
		{
			if(!asList("avi", "mod", "mpeg").contains(getExtension(vid.getName())))
			{
				throw new IllegalArgumentException(vid.getPath() + " is not a convertable file type.");
			}
			
			if(!vid.isFile())
			{
				throw new FileNotFoundException(vid.getPath());
			}
			
			new Subprocess("ffmpeg", 
					"-threads", "0",
					"-i", vid.getCanonicalPath(),
					"-b", "1500K",
					"-vcodec", "libx264",
					"-vpre", "slow",
					"-y",
					new File(vid.getParentFile(), getBaseName(vid.getName()) + ".mp4").getPath()
				).call();
		}
	}
}
