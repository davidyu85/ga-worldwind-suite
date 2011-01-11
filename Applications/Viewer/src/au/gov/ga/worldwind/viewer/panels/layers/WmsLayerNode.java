package au.gov.ga.worldwind.viewer.panels.layers;

import static au.gov.ga.worldwind.common.util.Util.isBlank;
import static au.gov.ga.worldwind.common.util.Util.isEmpty;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerInfoURL;
import gov.nasa.worldwind.ogc.wms.WMSLayerStyle;
import gov.nasa.worldwind.ogc.wms.WMSLogoURL;
import gov.nasa.worldwindow.core.WMSLayerInfo;

import java.net.URL;
import java.util.Collection;
import java.util.Set;

import au.gov.ga.worldwind.common.util.Icons;
import au.gov.ga.worldwind.common.util.URLUtil;
import au.gov.ga.worldwind.common.util.Util;

/**
 * A layer node that holds information about a WMS layer
 */
public class WmsLayerNode extends LayerNode
{
	private static final Long DEFAULT_EXPIRY_TIME = 86400000L; // 1 day
	private static final URL DEFAULT_ICON_URL = Icons.wmsbrowser.getURL();
	
	private static URL getInfoUrlForLayer(WMSLayerInfo layerInfo)
	{
		if (layerInfo == null || layerInfo.getCaps() == null)
		{
			return null;
		}
		
		WMSCapabilities caps = (WMSCapabilities)layerInfo.getCaps();
		if (caps.getServiceInformation() == null || 
				caps.getServiceInformation().getOnlineResource() == null || 
				isBlank(caps.getServiceInformation().getOnlineResource().getHref()))
		{
			return null;
		}
		
		return URLUtil.fromString(caps.getServiceInformation().getOnlineResource().getHref());
	}
	
	private static URL getLegendUrlForLayer(WMSLayerInfo layerInfo)
	{
		if (layerInfo == null || layerInfo.getCaps() == null)
		{
			return null;
		}
		
		WMSLayerCapabilities caps = ((WMSCapabilities)layerInfo.getCaps()).getLayerByName(layerInfo.getParams().getStringValue(AVKey.LAYER_NAMES));
		
		Collection<WMSLayerStyle> layerStyles = caps.getStyles();
		if (layerStyles == null || layerStyles.isEmpty())
		{
			return null;
		}
		
		// First legend with a URL wins...
		for (WMSLayerStyle style : layerStyles)
		{
			Set<WMSLogoURL> legendURLs = style.getLegendURLs();
			if (legendURLs == null || legendURLs.isEmpty())
			{
				continue;
			}
			for (WMSLogoURL legendURL : legendURLs)
			{
				String legendHref = legendURL.getOnlineResource().getHref();
				if (Util.isBlank(legendHref))
				{
					continue;
				}
				
				return URLUtil.fromString(legendHref);
			}
		}
		return null;
	}
	
	private static URL getLayerUrl(WMSLayerInfo layerInfo)
	{
		if (layerInfo == null || layerInfo.getCaps() == null)
		{
			return null;
		}
		WMSLayerCapabilities caps = ((WMSCapabilities)layerInfo.getCaps()).getLayerByName(layerInfo.getParams().getStringValue(AVKey.LAYER_NAMES));
		if (caps == null || isEmpty(caps.getDataURLs()))
		{
			return null;
		}
		
		// Use the first URL...
		for (WMSLayerInfoURL dataUrl : caps.getDataURLs())
		{
			if (dataUrl == null || dataUrl.getOnlineResource() == null || isBlank(dataUrl.getOnlineResource().getHref()))
			{
				continue;
			}
			URL result = URLUtil.fromString(dataUrl.getOnlineResource().getHref());
			if (result != null)
			{
				return result;
			}
		}
		return null;
	}
	
	private WMSLayerInfo layerInfo;
	
	public WmsLayerNode(WMSLayerInfo layerInfo, boolean enabled, double opacity)
	{
		super(layerInfo.getTitle(), getInfoUrlForLayer(layerInfo), DEFAULT_ICON_URL, false, getLayerUrl(layerInfo), enabled, opacity, DEFAULT_EXPIRY_TIME);
		setLegendURL(getLegendUrlForLayer(layerInfo));
		this.layerInfo = layerInfo;
	}
	
	public WMSLayerInfo getLayerInfo()
	{
		return layerInfo;
	}
	
	public WMSCapabilities getWmsCapabilities()
	{
		return (WMSCapabilities)layerInfo.getCaps();
	}
	
	public AVList getWmsParams()
	{
		return layerInfo.getParams();
	}
}
