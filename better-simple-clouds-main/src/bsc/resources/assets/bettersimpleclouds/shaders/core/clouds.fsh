#version 430

// Better Simple Clouds: a copy of Simple Clouds' clouds.fsh with a small "sit nicely in a shader-lit scene" pass added
// at the end. Better Simple Clouds registers the cloud shader as bettersimpleclouds:clouds (reusing Simple Clouds' own
// vertex shader, so the SSBO geometry expansion is unchanged) so this fragment shader runs instead of the stock one.
// All additions are inert at their defaults (MicSkyTint = 0, MicBrightness = 1, MicSaturation = 1, MicNightStrength = 0)
// -> identical to Simple Clouds. The patch feeds the live values + the scene sky colour each frame (CloudShaderMatchMixin).
// If Simple Clouds updates clouds.fsh, re-copy it and re-apply the marked block.

uniform sampler2D BayerMatrixSampler;

uniform vec4 ColorModulator;
uniform float DitherScale;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

// === Better Simple Clouds additions ===
uniform vec3 MicSkyColor = vec3(0.0); // scene sky/atmosphere colour clouds are tinted toward
uniform float MicSkyTint = 0.0;       // 0 = off; how much distant-ish clouds pick up the sky colour
uniform float MicBrightness = 1.0;    // overall exposure multiplier (lower = less washed-out under shaders)
uniform float MicSaturation = 1.0;    // colour saturation (1 = unchanged)
// Soft far edge: additionally fade clouds into the sky colour as they approach the render horizon, so they dissolve
// into the sky instead of hard-cutting. Done here (fragment) rather than by thinning geometry, so it's smooth and
// works under shaders. Inert when MicEdgeFadeStrength = 0.
uniform float MicEdgeFadeStrength = 0.0;
uniform float MicEdgeFadeStart = 0.0;
uniform float MicEdgeFadeEnd = 0.0;
// EXPERIMENTAL far-cloud fog resistance: Simple Clouds fades distant clouds into the fog colour (washing them out near
// the horizon). This holds that back so far/edge clouds stay vivid. 0 = stock fade; 1 = clouds keep full colour to the
// edge (can look like they don't sit in the haze). Inert at 0.
uniform float MicFogResist = 0.0;

// Better Simple Clouds: cloud-relative fog range. FogStart/FogEnd carry the TERRAIN fog, which is tuned for a view a
// fraction of the distance clouds are drawn over - so every cloud past the terrain fog end clamps to fully fogged and
// the sky flattens into one flat colour. When MicCloudFogEnd > 0 these replace them with a range set as fixed
// percentages of the cloud field's own extent, so the clouds keep a real near->far gradient no matter what the terrain
// fog is doing. 0 = off, behave exactly as before.
uniform float MicCloudFogStart = 0.0;
uniform float MicCloudFogEnd = 0.0;
float micFogStart() { return MicCloudFogEnd > 0.0 ? MicCloudFogStart : FogStart; }
float micFogEnd()   { return MicCloudFogEnd > 0.0 ? MicCloudFogEnd   : FogEnd; }
// Night legibility: at night Simple Clouds' clouds collapse into flat dark grey blobs - the cloud colour is multiplied
// down to a dark grey and the per-cube brightness that gives a cloud its shape gets crushed into the bottom of the
// range where nothing reads. This opens that range back up (gamma), lifts exposure and adds a cool moonlit cast so the
// cloud's form is visible again. MicNight = how "night" it is (0 by day -> inert), MicNightStrength = configured
// strength (0 = off). Fed each frame by CloudNightGrade. Inert at defaults -> identical to Simple Clouds.
uniform float MicNight = 0.0;
uniform float MicNightStrength = 0.0;
// Soft terrain intersection ("soft particles"): where a cloud cube cuts into terrain, Simple Clouds draws a hard
// polygonal edge AND the two near-coincident surfaces z-fight (the intersection visibly flickers as the depth test
// flips between them). Fading the cloud out as it approaches the terrain behind it fixes both: the cut becomes a
// gradient, and the flicker stops because the cloud contributes ~nothing exactly where the surfaces coincide.
// MicSceneDepth is the scene (terrain) depth buffer; MicSoftFade is the fade distance in blocks (0 = off).
// ONLY valid on Simple Clouds' shader-support pipeline, where terrain depth is copied in before clouds draw - the
// patch feeds MicSoftFade = 0 on the default pipeline, where terrain hasn't rendered yet.
uniform sampler2D MicSceneDepth;
uniform float MicSoftFade = 0.0;
// Near/far are fed explicitly (CloudSoftFade) and must NOT be read back out of ProjMat: Minecraft post-multiplies the
// view-bob matrix INTO the projection (GameRenderer.renderLevel), so ProjMat is P*Bob and ProjMat[2][2] is c*cos(bob),
// not the perspective coefficient c. Simple Clouds also overrides getDepthFar globally to 10240, which makes the
// reconstruction so ill-conditioned that the tiny bob term dominates and flips the result's sign - that is what made
// this fade collapse to 0 and delete every cloud while WALKING (bob is only non-zero on the ground; flying was fine).
uniform float MicNear = 0.0;
uniform float MicFar = 0.0;

// === Better Simple Clouds: moonlight scattering THROUGH the cloud (the silver lining). ===
// Cloud droplets scatter light overwhelmingly FORWARDS, which is why a cloud drifting across the moon lights up
// along its edge instead of just blocking it. Simple Clouds has no notion of the moon, so a cloud crossing a full
// moon simply goes flat grey. MicMoonDir is the moon's direction in WORLD space (transformed here by the shader's
// own ModelViewMat, so it can never drift out of sync with the geometry); MicMoonGlow is the master intensity,
// already scaled by phase, altitude, rain and time of day on the CPU. Inert at MicMoonGlow = 0.
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 MicMoonDir = vec3(0.0);
uniform vec3 MicMoonColor = vec3(1.0);
uniform float MicMoonGlow = 0.0;
uniform float MicMoonSharpness = 0.76;
uniform vec2 MicScreenSize = vec2(1920.0, 1080.0);

// Henyey-Greenstein forward-scattering lobe, normalised to peak at exactly 1.0 looking straight at the moon.
// g ~ 0.76 is a realistic cloud-droplet asymmetry: sharply peaked forward, which is what makes the lining a thin
// bright rim rather than an even wash over the whole sky.
vec3 micMoonScatter()
{
	if (MicMoonGlow <= 0.0)
		return vec3(0.0);
	// View ray for this fragment, straight from the projection matrix - no varying needed, so Simple Clouds' own
	// vertex shader stays untouched.
	vec2 ndc = (gl_FragCoord.xy / MicScreenSize) * 2.0 - 1.0;
	vec3 ray = normalize(vec3(ndc.x / ProjMat[0][0], ndc.y / ProjMat[1][1], -1.0));
	vec3 moonView = normalize((ModelViewMat * vec4(MicMoonDir, 0.0)).xyz);
	float ct = max(dot(ray, moonView), 0.0);
	float g = clamp(MicMoonSharpness, 0.0, 0.95);
	float gg = g * g;
	float denom = max(1.0 + gg - 2.0 * g * ct, 1.0e-4);
	float hg = (1.0 - gg) / pow(denom, 1.5);
	float norm = (1.0 - g) * (1.0 - g) / (1.0 + g);   // hg at ct = 1, inverted
	return MicMoonColor * (MicMoonGlow * hg * norm);
}

in vec4 vertexColor;
in float fogDistance;

out vec4 fragColor;

// Window depth [0,1] -> positive view distance in blocks. Standard perspective linearization with EXPLICIT near/far,
// which stays exact under view bobbing: for M = P*Bob the bob only rotates/translates the eye, so clip.w is still -z'
// and this recovers z' - the one space the scene and cloud fragments at a given pixel genuinely share.
float micViewDist(float winZ)
{
	float ndc = winZ * 2.0 - 1.0;
	return (2.0 * MicNear * MicFar) / (MicFar + MicNear - ndc * (MicFar - MicNear));
}

void main()
{
	float fade = ColorModulator.a;
	float r = texture(BayerMatrixSampler, gl_FragCoord.xy * DitherScale).r;
	if (fade < r)
		discard;

	vec4 color = vertexColor * vec4(ColorModulator.rgb, 1.0);

	// === Better Simple Clouds: night legibility (see uniform notes above). Grade the cloud's OWN colour here, before the
	// distance-fog blend below, so far night clouds still recede into the horizon. Open up the crushed dark range with a
	// gamma < 1 (which spreads out the per-cube brightness that shapes the cloud), lift exposure, and tint toward a cool
	// moonlit hue - instead of the flat dark grey. Blended in by MicNight so it fades on smoothly at dusk and is exactly a
	// no-op by day (MicNight = 0). MicNightStrength scales the whole thing (0 = off). ===
	if (MicNightStrength > 0.0 && MicNight > 0.0)
	{
		float s = min(MicNightStrength, 2.0);
		vec3 graded = pow(max(color.rgb, vec3(0.0)), vec3(mix(1.0, 0.72, s)));
		graded *= mix(1.0, 1.75, s);
		graded *= mix(vec3(1.0), vec3(0.80, 0.88, 1.05), clamp(MicNightStrength, 0.0, 1.0));
		color.rgb = mix(color.rgb, graded, clamp(MicNight, 0.0, 1.0));
	}

	// Better Simple Clouds: add the moon's forward-scattered light. Placed BEFORE the distance-fog blend so a glowing
	// far cloud still recedes into the horizon haze rather than burning through it.
	color.rgb += micMoonScatter();

	// Better Simple Clouds: optionally hold back Simple Clouds' distance-fog wash so far/edge clouds stay vivid.
	color = mix(color, FogColor, smoothstep(micFogStart(), micFogEnd(), fogDistance) * (1.0 - clamp(MicFogResist, 0.0, 1.0)));

	// === Better Simple Clouds: match the shader-lit scene a little better (inert at defaults). ===
	// Aerial perspective: tint toward the sky colour with DISTANCE only, so near clouds keep their true colour and
	// don't go patchy (an earlier luminance-weighted tint exaggerated per-face contrast into grey cubes).
	if (MicSkyTint > 0.0)
	{
		float aerial = smoothstep(0.0, FogEnd, fogDistance);
		color.rgb = mix(color.rgb, MicSkyColor, clamp(MicSkyTint * aerial, 0.0, 1.0));
	}
	if (MicSaturation != 1.0)
	{
		float grey = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
		color.rgb = mix(vec3(grey), color.rgb, MicSaturation);
	}
	color.rgb *= MicBrightness;

	// === Better Simple Clouds: soft far edge - fade clouds toward the SCENE FOG colour (the same colour Simple Clouds
	// itself fades distant clouds to, just above) over the outer render distance, so they melt smoothly into the
	// horizon haze. Uses FogColor, NOT the camera sky colour: it matches the real horizon so there are no flat grey
	// patches, and it's a plain colour blend so there's no dithered/pixelated look. ===
	if (MicEdgeFadeStrength > 0.0 && MicEdgeFadeEnd > MicEdgeFadeStart)
	{
		float edge = smoothstep(MicEdgeFadeStart, MicEdgeFadeEnd, fogDistance);
		color.rgb = mix(color.rgb, FogColor.rgb, clamp(edge * MicEdgeFadeStrength, 0.0, 1.0));
	}

	// === Better Simple Clouds: soft terrain intersection (see uniform notes above). The cloud target's alpha is NOT a
	// blend factor during this pass (blending is off) - it is what Simple Clouds' final composite blends with:
	//   finalCol = cloudCol.rgb * cloudCol.a + bg * (1.0 - cloudCol.a)
	// so writing a partial alpha here fades the cloud into the terrain behind it for free. Stock behaviour is alpha = 1
	// (a plain "cloud / no cloud" mask), which is exactly what MicSoftFade = 0 keeps. ===
	float softAlpha = 1.0;
	if (MicSoftFade > 0.0 && MicNear > 0.0 && MicFar > MicNear)
	{
		float rawScene = texelFetch(MicSceneDepth, ivec2(gl_FragCoord.xy), 0).r;
		// The depth buffer CLEARS TO 1.0, so rawScene >= 1.0 means "open sky, no terrain here" - the common case, and
		// also the worst-conditioned input to feed the linearization. rawScene <= 0.0 means no depth is bound at all.
		// Both must leave the cloud SOLID. (The old guard tested `> 0.0`, which every sky pixel passes - that is why
		// sky-backed clouds, i.e. essentially all of them, were the ones being deleted.)
		if (rawScene > 0.0 && rawScene < 1.0)
		{
			float sceneDist = micViewDist(rawScene);
			float fragDist = micViewDist(gl_FragCoord.z);
			// Fail-safe: only fade on a sane, positive pair of readings. Anything else leaves the cloud solid, so a bad
			// depth read can never silently erase the cloud field again.
			if (sceneDist > 0.0 && fragDist > 0.0)
				softAlpha = clamp((sceneDist - fragDist) / MicSoftFade, 0.0, 1.0);
		}
	}

	// A fully faded fragment must not stamp occluding depth into the cloud target: that depth is blitted into the
	// transparency target, where it would depth-reject the translucent fringe sitting behind an invisible body - leaving
	// only stray cloud outlines on screen (the "ghost occluder"). Alpha alone never suppresses a depth write.
	if (softAlpha <= 0.0)
		discard;

	fragColor = vec4(color.rgb, softAlpha);
}
