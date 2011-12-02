package au.gov.ga.worldwind.animator.application.effects;

import gov.nasa.worldwind.render.DrawContext;

import java.awt.Dimension;

import au.gov.ga.worldwind.animator.animation.Animatable;
import au.gov.ga.worldwind.animator.animation.Animation;
import au.gov.ga.worldwind.animator.animation.io.AnimationIOConstants;
import au.gov.ga.worldwind.animator.application.render.FrameBuffer;

/**
 * Interface that defines an {@link Animatable} full-screen effect.
 * {@link Effect}s are bound by the scene controller, and are rendered in screen
 * space. Each {@link Effect} implementation should contain a
 * {@link FrameBuffer} that has a depth texture, and the effect shaders should
 * write to the gl_FragDepth variable so that the fragment depth is passed
 * through the effect chain.
 * 
 * @author Michael de Hoog (michael.dehoog@ga.gov.au)
 */
public interface Effect extends Animatable
{
	/**
	 * Add the provided parameter to this effect
	 * 
	 * @param parameter
	 *            The parameter to add to the effect
	 */
	void addParameter(EffectParameter parameter);

	/**
	 * Called before the scene is rendered. The effect should setup it's
	 * framebuffer here.
	 * 
	 * @param dc
	 *            Draw context
	 * @param dimensions
	 *            Dimensions of the viewport (includes render scale during
	 *            rendering)
	 */
	void bindFrameBuffer(DrawContext dc, Dimension dimensions);

	/**
	 * Called after the scene is rendered. The effect should unbind it's
	 * framebuffer here.
	 * 
	 * @param dc
	 *            Draw context
	 * @param dimensions
	 *            Dimensions of the viewport (includes render scale during
	 *            rendering)
	 */
	void unbindFrameBuffer(DrawContext dc, Dimension dimensions);

	/**
	 * Called after the scene is rendered, and possibly after another
	 * {@link Effect}'s frame buffer has been bound. The effect should render
	 * it's framebuffer using it's effect shader here.
	 * 
	 * @param dc
	 *            Draw context
	 * @param dimensions
	 *            Dimensions of the viewport (includes render scale during
	 *            rendering)
	 */
	void drawFrameBufferWithEffect(DrawContext dc, Dimension dimensions);

	/**
	 * Release any resources ({@link FrameBuffer}s, {@link Shader}s) associated
	 * with this effect. This is called every frame if the effect is disabled.
	 * 
	 * @param dc
	 *            Draw context
	 */
	void releaseResources(DrawContext dc);

	/**
	 * Create a new instance of this {@link Effect}, associated with the
	 * provided {@link Animation}.
	 * 
	 * @param animation
	 *            Animation to associated new Effect with
	 * @return New instance of this Effect
	 */
	Effect createWithAnimation(Animation animation);

	/**
	 * @return The default name for this effect.
	 */
	String getDefaultName();

	/**
	 * @param constants
	 * @return The effect's XML element name.
	 */
	String getXmlElementName(AnimationIOConstants constants);
}
