package au.gov.ga.worldwind.common.layers.shapefile.point;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.Logging;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.w3c.dom.Element;

import au.gov.ga.worldwind.common.layers.Bounded;
import au.gov.ga.worldwind.common.util.AVKeyMore;

public abstract class PointLayer extends AbstractLayer implements Bounded
{
	public final static String POINT_STYLES = PointLayer.class.getName() + ".PointStyles";
	public final static String POINT_ATTRIBUTES = PointLayer.class.getName() + ".PointAttributes";

	protected String url;
	protected String dataCacheName;
	protected URL context;

	protected Style[] styles;
	protected Map<String, Style> styleMap = new HashMap<String, Style>();
	protected Style defaultStyle;
	protected Attribute[] attributes;

	public PointLayer(AVList params)
	{
		String s = params.getStringValue(AVKey.DISPLAY_NAME);
		if (s != null)
			setName(s);

		s = params.getStringValue(AVKey.URL);
		if (s != null)
			setUrl(s);

		s = params.getStringValue(AVKey.DATA_CACHE_NAME);
		if (s != null)
			setDataCacheName(s);

		Long l = (Long) params.getValue(AVKey.EXPIRY_TIME);
		if (l != null)
			setExpiryTime(l);

		Object o = params.getValue(POINT_STYLES);
		if (o != null)
			setStyles((Style[]) o);

		o = params.getValue(POINT_ATTRIBUTES);
		if (o != null)
			setAttributes((Attribute[]) o);

		context = (URL) params.getValue(AVKeyMore.CONTEXT_URL);
	}

	public PointLayer(Element domElement, AVList params)
	{
		this(getParamsFromDocument(domElement, params));
	}

	protected static AVList getParamsFromDocument(Element domElement, AVList params)
	{
		if (params == null)
			params = new AVListImpl();

		AbstractLayer.getLayerConfigParams(domElement, params);
		PointLayerUtils.getParamsFromDocument(domElement, params);

		return params;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getDataCacheName()
	{
		return dataCacheName;
	}

	public void setDataCacheName(String dataCacheName)
	{
		this.dataCacheName = dataCacheName;
	}

	public Style[] getStyles()
	{
		return styles;
	}

	public void setStyles(Style[] styles)
	{
		this.styles = styles;

		styleMap.clear();
		defaultStyle = null;

		for (Style style : styles)
		{
			if (style.isDefault() && defaultStyle == null)
				defaultStyle = style;
			styleMap.put(style.getName(), style);
		}
		
		if(defaultStyle == null)
		{
			defaultStyle = new Style(null, true);
		}
	}

	public Attribute[] getAttributes()
	{
		return attributes;
	}

	public void setAttributes(Attribute[] attributes)
	{
		this.attributes = attributes;
	}

	@Override
	public void render(DrawContext dc)
	{
		if (isEnabled() && !isLoaded())
		{
			//TODO move loading to new thread (ensure only single concurrent load() running)
			try
			{
				URL url = new URL(context, getUrl());
				load(url);
			}
			catch (MalformedURLException e)
			{
				Logging.logger().log(Level.SEVERE, "Error loading points", e);
			}
		}

		super.render(dc);
	}

	protected void addPoint(Position position, AVList attrib)
	{
		String link = null;
		String text = null;

		Style style = null;
		for (Attribute attribute : attributes)
		{
			if (style == null)
			{
				String styleName = attribute.getMatchingStyle(attrib);
				if (styleMap.containsKey(styleName))
				{
					style = styleMap.get(styleName);
				}
			}

			String t = attribute.getText(attrib);
			if (t != null)
				text = text == null ? t : text + t;

			link = link != null ? link : attribute.getLink(attrib);
		}

		if (style == null)
			style = defaultStyle;

		addPoint(position, attrib, style, text, link);
	}

	protected abstract void addPoint(Position position, AVList attrib, Style style, String text,
			String link);

	protected abstract boolean isLoaded();

	protected abstract void load(URL url);
}