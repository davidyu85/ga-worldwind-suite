/*******************************************************************************
 * Copyright 2012 Geoscience Australia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package au.gov.ga.worldwind.common.render;

import gov.nasa.worldwind.AbstractSceneController;
import gov.nasa.worldwind.SceneController;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.terrain.SectorGeometryList;
import gov.nasa.worldwind.terrain.Tessellator;
import au.gov.ga.worldwind.common.util.SectorClipPlanes;
import au.gov.ga.worldwind.common.view.drawable.DrawableView;

/**
 * {@link SceneController} that uses a separate {@link Tessellator} to generate
 * a separate set of flat geometry, used by layers that are rendered onto a flat
 * surface.
 * 
 * @author Michael de Hoog (michael.dehoog@ga.gov.au)
 */
public class ExtendedSceneController extends AbstractSceneController implements DrawableSceneController
{
	private FlatRectangularTessellator flatTessellator = new FlatRectangularTessellator();
	protected final SectorClipPlanes sectorClipping = new SectorClipPlanes();

	public ExtendedSceneController()
	{
		dc.dispose();
		dc = new ExtendedDrawContext();
	}

	public void clipSector(Sector sector)
	{
		sectorClipping.clipSector(sector);
	}

	public void clearClipping()
	{
		sectorClipping.clear();
	}

	@Override
	protected void createTerrain(DrawContext dc)
	{
		super.createTerrain(dc);

		if (dc instanceof ExtendedDrawContext)
		{
			ExtendedDrawContext edc = (ExtendedDrawContext) dc;
			if (edc.getFlatSurfaceGeometry() == null)
			{
				if (dc.getModel() != null && dc.getModel().getGlobe() != null)
				{
					SectorGeometryList sgl = flatTessellator.tessellate(dc);
					edc.setFlatSurfaceGeometry(sgl);
				}
			}
		}
	}

	@Override
	protected void doRepaint(DrawContext dc)
	{
		this.initializeFrame(dc);
		try
		{
			this.applyView(dc);
			this.createPickFrustum(dc);
			this.createTerrain(dc);
			this.preRender(dc);
			this.clearFrame(dc);
			this.pick(dc);
			this.clearFrame(dc);
			try
			{
				sectorClipping.enableClipping(dc);
				if (view instanceof DrawableView)
				{
					((DrawableView) view).draw(dc, this);
				}
				else
				{
					this.draw(dc);
				}
			}
			finally
			{
				sectorClipping.disableClipping(dc);
			}
		}
		finally
		{
			this.finalizeFrame(dc);
		}
	}

	@Override
	public void draw(DrawContext dc)
	{
		super.draw(dc);
	}

	@Override
	public void clearFrame(DrawContext dc)
	{
		super.clearFrame(dc);
	}

	@Override
	public void applyView(DrawContext dc)
	{
		super.applyView(dc);
	}
}
