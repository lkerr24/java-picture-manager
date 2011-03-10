package com.jiehoo.jpm.core;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

public class ImageInfo {
	private static Logger logger = Logger.getLogger(ImageInfo.class);
	private String date;
	private String ID;
	private HashSet<Integer> tags=new HashSet<Integer>();
	private int rank;
	private long size;
	private String camera;
	
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getCamera() {
		return camera;
	}
	public void setCamera(String camera) {
		this.camera = camera;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getID() {
		return ID;
	}
	public void setID(String id) {
		ID = id;
	}
	public HashSet<Integer> getTags() {
		return tags;
	}
	public void setTags(HashSet<Integer> tags) {
		this.tags = tags;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public void AddTag(int tag)
	{
		tags.add(tag);
	}
	
	public void RemoveTag(int tag)
	{
		tags.remove(tag);
	}
	
	public void ExtractImageInfo(String fullPath) throws IOException
	{
		logger.debug("Extract image info for:"+fullPath);
		File file = new File(fullPath);
		size=file.length();
		JpegImageMetadata metadata;
		try {
			metadata = (JpegImageMetadata)Sanselan.getMetadata(file);
			camera=(String)GetPropertyValue(metadata,TiffConstants.EXIF_TAG_MODEL,"Unknow");
			date=(String)GetPropertyValue(metadata,TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL,"1970:00:00 00:00:00");
			StringBuilder buffer=new StringBuilder();
			buffer.append(date);
			buffer.append("_").append(GetPropertyValue(metadata,TiffConstants.EXIF_TAG_INTEROP_OFFSET,"NA"));
			buffer.append("_").append(GetPropertyValue(metadata,TiffConstants.EXIF_TAG_EXPOSURE_TIME,"NA"));
			buffer.append("_").append(GetPropertyValue(metadata,TiffConstants.EXIF_TAG_MAX_APERTURE_VALUE,"NA"));
			ID=buffer.toString();
			if (ID.equals("1970:00:00 00:00:00_NA_NA_NA"))
			{
				ID=ID+"_"+size;
			}
		} catch (ImageReadException e) {
			logger.warn("Can't extract image information.",e);
		}
	}
	
	private Object GetPropertyValue(JpegImageMetadata metadata,TagInfo property,Object defaultValue) throws ImageReadException
	{
		if (metadata==null)
		{
			return defaultValue;
		}
		TiffField field=metadata.findEXIFValue(property);
		if (field!=null)
		{
			Object result = field.getValue();
			if (result!=null&&result instanceof String)
			{
				String s=(String)result;				
				result=s.trim();
			}
			return result;
		}
		else
		{
			return defaultValue;
		}
	}

}
