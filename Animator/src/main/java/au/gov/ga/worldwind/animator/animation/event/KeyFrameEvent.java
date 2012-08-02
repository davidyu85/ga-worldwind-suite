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
package au.gov.ga.worldwind.animator.animation.event;

import au.gov.ga.worldwind.animator.animation.KeyFrame;

/**
 * An {@link AnimationEvent} that is linked to a Key Frame.
 * <p/>
 * {@link KeyFrameEvent}s may be generated when key frames are added, removed, moved or changed.
 * 
 * @author James Navin (james.navin@ga.gov.au)
 *
 */
public interface KeyFrameEvent extends AnimationEvent
{
	
	KeyFrame getKeyFrame();
	
}