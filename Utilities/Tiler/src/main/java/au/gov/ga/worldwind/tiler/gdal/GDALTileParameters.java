package au.gov.ga.worldwind.tiler.gdal;

import java.awt.Dimension;
import java.awt.Rectangle;

import org.gdal.gdal.Dataset;

import au.gov.ga.worldwind.tiler.util.MinMaxArray;
import au.gov.ga.worldwind.tiler.util.NullableNumberArray;
import au.gov.ga.worldwind.tiler.util.Sector;


public class GDALTileParameters
{
	public GDALTileParameters(Dataset dataset, Dimension size, Sector sector)
	{
		this.dataset = dataset;
		this.size = size;
		this.sector = sector;
		this.sourceRectangle = null;
	}
	
	public GDALTileParameters(Dataset dataset, Dimension size, Rectangle sourceRectangle)
	{
		this.dataset = dataset;
		this.size = size;
		this.sector = null;
		this.sourceRectangle = sourceRectangle;
	}
	
	public final Dataset dataset;
	public final Dimension size;
	public final Sector sector;
	public final Rectangle sourceRectangle;
	
	public boolean addAlpha = false;
	public int selectedBand = -1;
	public boolean reprojectIfRequired = false;
	public boolean bilinearInterpolationIfRequired = true;
	public boolean ignoreBlank = true;

	//Use noData values for:
	//1. Pixels outside dataset extents
	//2. Ignoring pixels when calculating min/max
	//3. Ignoring pixels when bilinear magnifying
	public NullableNumberArray noData;
	
	//replacement variables
	public MinMaxArray[] minMaxs;
	public NullableNumberArray replacement;
	public NullableNumberArray otherwise;
}