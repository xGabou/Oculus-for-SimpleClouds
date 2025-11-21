## Explanation in normal words

### Part one  
Distant Horizons forces its own cloud render pipeline every time.  
It registers an event handler that always overrides the pipeline with DhSupportPipeline. Because this override is unconditional, the Simple Clouds renderer never gets the chance to choose the shader compatible pipeline when Oculus or Iris shaders are active. That is why when Distant Horizons and Oculus are loaded together, the shader aware path is never selected and the clouds disappear.

### Part two  
When Distant Horizons is active, clouds ignore the depth of blocks that are near the player. This happens because the depth buffer used by Simple Clouds is replaced entirely by the depth that comes from the Distant Horizons frame buffer. The pipeline copies depth from the Distant Horizons frame buffer into the cloud frame buffers, but it never merges this value with the regular Minecraft depth. As a result, the clouds do not know that there are blocks in front of them. This causes clouds to render through walls, caves and terrain.

### Part three  
The beforeDistantHorizonsApplyShader method clears the Simple Clouds frame buffers, binds the Distant Horizons frame buffer for reading and blits its depth into the cloud targets. This is how depth from the distant terrain is injected. However the vanilla depth is missing at this point. Only the distant depth is present.

### Part four  
The afterDistantHorizonsRender method performs all the real cloud rendering. It renders opaque clouds, then transparent clouds, then cloud shadows, then the final composite pass. During the composite, it uses the cloud target depth as the current depth. Later, for lightning rendering, the method temporarily attaches the cloud depth texture to the main frame buffer. This makes the main scene use the Distant Horizons depth instead of the real Minecraft depth. This again means that only distant depth affects visibility. Normal block depth is not considered.

### Summary of the issue  
Distant Horizons depth replaces the entire depth buffer used for cloud rendering. The vanilla depth that contains nearby blocks never gets merged. Because of this the Simple Clouds pipeline cannot know that geometry is close and the clouds draw over everything.

### Summary of the first required fix  
Allow Simple Clouds to choose its shader compatible pipeline when Oculus is running and only allow Distant Horizons to override it when it is truly necessary. The current Distant Horizons override happens always and blocks the shader aware path.

### Summary of the second required fix  
Merge the Minecraft vanilla depth and the Distant Horizons depth into a single depth texture before the cloud compositing step. This merged result should then be used during cloud rendering and during the final composite. This is the only way to ensure that clouds respect terrain that is near the camera and also respect the distant terrain provided by Distant Horizons.
