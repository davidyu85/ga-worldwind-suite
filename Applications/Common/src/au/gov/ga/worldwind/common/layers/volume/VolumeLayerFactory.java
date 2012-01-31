package au.gov.ga.worldwind.common.layers.volume;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.util.WWXML;

import javax.xml.xpath.XPath;

import org.w3c.dom.Element;

import au.gov.ga.worldwind.common.layers.data.DataLayerFactory;
import au.gov.ga.worldwind.common.layers.model.ModelLayer;
import au.gov.ga.worldwind.common.layers.model.ModelProvider;
import au.gov.ga.worldwind.common.util.AVKeyMore;
import au.gov.ga.worldwind.common.util.ColorMap;
import au.gov.ga.worldwind.common.util.XMLUtil;

/**
 * Factory used for creating {@link ModelLayer} instances an XML definition.
 * 
 * @author Michael de Hoog (michael.dehoog@ga.gov.au)
 */
public class VolumeLayerFactory
{
	/**
	 * Create a new {@link ModelLayer} from an XML definition.
	 * 
	 * @return New {@link ModelLayer}.
	 */
	public static VolumeLayer createVolumeLayer(Element domElement, AVList params)
	{
		params = AbstractLayer.getLayerConfigParams(domElement, params);
		params = getParamsFromDocument(domElement, params);

		VolumeLayer layer = new BasicVolumeLayer(params);
		DataLayerFactory.setLayerParams(layer, params);
		return layer;
	}

	/**
	 * Fill the params with the values in the {@link ModelLayer} specific XML
	 * elements.
	 */
	public static AVList getParamsFromDocument(Element domElement, AVList params)
	{
		if (params == null)
			params = new AVListImpl();

		XPath xpath = WWXML.makeXPath();

		WWXML.checkAndSetStringParam(domElement, params, AVKey.URL, "URL", xpath);
		WWXML.checkAndSetLongParam(domElement, params, AVKey.EXPIRY_TIME, "ExpiryTime", xpath);
		WWXML.checkAndSetDateTimeParam(domElement, params, AVKey.EXPIRY_TIME, "LastUpdate",
				DataLayerFactory.DATE_TIME_PATTERN, xpath);
		WWXML.checkAndSetStringParam(domElement, params, AVKey.DATA_CACHE_NAME, "DataCacheName", xpath);

		WWXML.checkAndSetDoubleParam(domElement, params, AVKeyMore.MAX_VARIANCE, "MaxVariance", xpath);
		WWXML.checkAndSetDoubleParam(domElement, params, AVKeyMore.MINIMUM_DISTANCE, "MinimumDistance", xpath);
		WWXML.checkAndSetStringParam(domElement, params, AVKey.COORDINATE_SYSTEM, "CoordinateSystem", xpath);
		
		WWXML.checkAndSetColorParam(domElement, params, AVKeyMore.NO_DATA_COLOR, "NoDataColor", xpath);

		ColorMap colorMap = XMLUtil.getColorMap(domElement, xpath);
		params.setValue(AVKeyMore.COLOR_MAP, colorMap);

		setupVolumeDataProvider(domElement, xpath, params);

		return params;
	}

	/**
	 * Adds a {@link ModelProvider} to params matching the 'DataFormat' XML
	 * element.
	 */
	protected static void setupVolumeDataProvider(Element domElement, XPath xpath, AVList params)
	{
		String format = WWXML.getText(domElement, "DataFormat", xpath);

		if ("GOCAD SGrid".equalsIgnoreCase(format))
		{
			params.setValue(AVKeyMore.DATA_LAYER_PROVIDER, new SGridVolumeDataProvider());
		}
		else
		{
			throw new IllegalArgumentException("Could not find volume data provider for DataFormat: " + format);
		}
	}
}
