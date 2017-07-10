package com.vivantor.mediaplayer;

/**
 * Created by AhmedNTS on 2016-06-19.
 */
public class MediaFile
{
	public String filePath;

	public long totalDuration = 0;
	public long currentDuration = 0;

	public MediaFile(String filePath)
	{
		this.filePath = filePath;
	}
}
