package au.gov.ga.worldwind.common.layers.curtain;

import gov.nasa.worldwind.cache.Cacheable;

public class Segment implements Cacheable
{
	//percentages, between 0.0 and 1.0
	private final double start, end;
	private final double top, bottom;

	public Segment(double start, double end, double top, double bottom)
	{
		this.start = start;
		this.end = end;
		this.top = top;
		this.bottom = bottom;
	}

	public double getStart()
	{
		return start;
	}

	public double getCenter()
	{
		return 0.5 * (start + end);
	}

	public double getEnd()
	{
		return end;
	}

	public double getTop()
	{
		return top;
	}

	public double getBottom()
	{
		return bottom;
	}

	@Override
	public long getSizeInBytes()
	{
		return 4 * Double.SIZE;
	}

	@Override
	public String toString()
	{
		return "(" + start + "," + top + " to " + end + "," + bottom + ")";
	}
}
