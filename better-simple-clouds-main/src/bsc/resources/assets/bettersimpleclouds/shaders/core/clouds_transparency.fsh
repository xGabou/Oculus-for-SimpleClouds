//https://jcgt.org/published/0002/02/09/paper.pdf and http://casual-effects.blogspot.com/2015/03/implemented-weighted-blended-order.html

// Better Simple Clouds: a copy of Simple Clouds' clouds_transparency.fsh with the marked blocks added - a tunable
// falloff for the weighted-blended OIT weight (MicWeightFlatten), and the same night grade / scene-match grade / soft
// terrain fade the opaque clouds.fsh applies. Better Simple Clouds registers this as
// bettersimpleclouds:clouds_transparency (reusing Simple Clouds' own vertex shader, so the SSBO cube expansion is
// unchanged) and swaps it in for the stock transparency shader (SimpleCloudsShadersMixin). All additions are inert at
// their defaults -> byte-for-byte Simple Clouds. The patch feeds the live values each frame
// (SimpleCloudsRendererTransparencyWeightMixin). If Simple Clouds updates clouds_transparency.fsh, re-copy it and
// re-apply the marked blocks.
//
// THE RULE FOR THIS FILE: every colour treatment in clouds.fsh must also be here, applied identically - same order,
// same expressions, same uniform values - and never scaled per-fragment (not by alpha, not by anything). This pass
// draws the translucent fringe that WRAPS the opaque body, so any treatment the two passes disagree on gets modulated
// by fringe alpha, and fringe alpha is the cloud noise field: the disagreement is then painted across every cloud face
// as noise mottling. Two separate bugs have already been exactly this. See CloudSceneGrade.

#version 430

uniform sampler2D BayerMatrixSampler;

uniform vec4 ColorModulator;
uniform float DitherScale;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

// === Better Simple Clouds additions ===
// 0 = stock Simple Clouds weighting; 1 = fully flat depth weighting. See below.
uniform float MicWeightFlatten = 0.0;
// Extent (blocks) the OIT weight's depth term is normalized over - the cloud field's real size, fed from the cloud fog
// end. Clamped to 1000 in main(), so at Simple Clouds' normal field size this reproduces its hardcoded 1000 exactly and
// only engages once a fog mod has shrunk the field. Default 1000 = stock. See the note at its use.
uniform float MicWeightScale = 1000.0;
// Scene-match grade - the SAME uniforms, fed the SAME values, as the opaque clouds.fsh (CloudSceneGrade feeds both).
// These are not optional here: a cloud's opaque body is seen THROUGH its translucent fringe shell, and the fringe's
// coverage is the cloud noise field (cube_mesh.comp emits a fringe cube exactly where the noise is in
// (-TransparencyFade, 0) and takes its alpha from that noise). So a grade applied to the body but not the fringe does
// not merely miss the fringe - it modulates the body by the fringe's alpha, stencilling the noise field onto every
// cloud face as mottling. See CloudSceneGrade for why Better Fog is what dragged that latent mismatch into view.
uniform vec3 MicSkyColor = vec3(0.0);
uniform float MicSkyTint = 0.0;
uniform float MicBrightness = 1.0;
uniform float MicSaturation = 1.0;
uniform float MicEdgeFadeStrength = 0.0;
uniform float MicEdgeFadeStart = 0.0;
uniform float MicEdgeFadeEnd = 0.0;
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
// Night legibility (same treatment as the opaque clouds.fsh, so a cloud's soft translucent fringes and its filled
// interior stay matched to the graded opaque body at night). MicNight = how "night" it is (0 by day -> inert),
// MicNightStrength = configured strength (0 = off). Fed each frame by CloudNightGrade.
uniform float MicNight = 0.0;
uniform float MicNightStrength = 0.0;
// Soft terrain intersection - identical to the opaque clouds.fsh, and needed here too: the in-cloud interior-fill haze
// and Simple Clouds' own soft fringe cubes are TRANSPARENT-pass geometry, so they still cut hard into terrain and
// z-fight against it (depthMask is off here, but the depth TEST is on) even once the opaque pass has its fade.
// near/far are fed explicitly - see CloudSoftFade for why they must NOT be read out of ProjMat (view bobbing).
uniform sampler2D MicSceneDepth;
uniform float MicSoftFade = 0.0;
uniform float MicNear = 0.0;
uniform float MicFar = 0.0;
// Smooth chunk fade-in - identical to the opaque clouds.fsh, and NOT optional here. Simple Clouds fades a newly
// generated chunk in by discarding pixels against a SCREEN-space Bayer matrix, so a mid-fade chunk wears a fixed
// cross-hatch of noise that does not move with the cloud. This pass is where it shows worst: the fringe is what you
// see another cloud through, and the two passes dither independently, so their patterns compound. Feeding this to the
// body but not the fringe would be the same body/fringe mismatch the note at the top of this file warns about, just
// expressed in coverage rather than colour. 0 = stock dithered fade. See CloudChunkFade.

float micViewDist(float winZ)
{
	float ndc = winZ * 2.0 - 1.0;
	return (2.0 * MicNear * MicFar) / (MicFar + MicNear - ndc * (MicFar - MicNear));
}

// 1.0 = untouched. Every unusable reading returns 1.0, so clouds stay solid rather than silently vanishing.
float micSoftAlpha()
{
	if (MicSoftFade <= 0.0 || MicNear <= 0.0 || MicFar <= MicNear)
		return 1.0;
	float rawScene = texelFetch(MicSceneDepth, ivec2(gl_FragCoord.xy), 0).r;
	if (!(rawScene > 0.0 && rawScene < 1.0))   // 1.0 = cleared depth / open sky, 0.0 = nothing bound
		return 1.0;
	float sceneDist = micViewDist(rawScene);
	float fragDist = micViewDist(gl_FragCoord.z);
	if (sceneDist <= 0.0 || fragDist <= 0.0)
		return 1.0;
	return clamp((sceneDist - fragDist) / MicSoftFade, 0.0, 1.0);
}

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
in float vertexDistance;

layout(location = 0) out vec4 accumColor;
layout(location = 1) out float revealage;

void main()
{
	float fade = ColorModulator.a;
	float r = texture(BayerMatrixSampler, gl_FragCoord.xy * DitherScale).r;
	if (fade < r)
		discard;

	vec4 color = vertexColor * vec4(ColorModulator.rgb, 1.0);

	// === Better Simple Clouds: night legibility - the same grade as the opaque clouds.fsh (open the crushed dark range
	// with a gamma < 1, lift exposure, cool moonlit cast), so translucent cloud stays matched to the graded opaque body
	// at night. No-op by day.
	//
	// The grade must be applied UNIFORMLY - identical curve for every fragment, exactly like the opaque pass, never
	// scaled by the fragment's own alpha. Every cloud face is seen through its translucent fringe shell, and a fringe
	// cube's alpha IS the cloud noise field (cube_mesh.comp derives it from the same noise). An earlier version weighted
	// the grade by alpha (to shield faint wisps from OIT-average error): that left half-transparent fringe cubes half-way
	// to their stock-dark colour while the opaque body behind them got the full moonlit lift, so the composite dimmed
	// each face by an amount that tracked the fringe cube's alpha - i.e. it painted the NOISE FIELD onto every face as
	// dark mottling ("the inside-of-a-cloud pattern", worst where alpha is near 0.5, gone at 0 or 1). Any per-fragment
	// scaling of the grade recreates that colour<->alpha correlation, which neither daytime (grade = no-op) nor stock
	// night ever has. Graded uniformly, night is just a dimmed, monotone-recoloured day: fragments keep the same relative
	// spread day has, so the OIT average can't show any mottle at night that day doesn't already show. ===
	if (MicNightStrength > 0.0 && MicNight > 0.0)
	{
		float s = min(MicNightStrength, 2.0);
		vec3 graded = pow(max(color.rgb, vec3(0.0)), vec3(mix(1.0, 0.72, s)));
		graded *= mix(1.0, 1.75, s);
		graded *= mix(vec3(1.0), vec3(0.80, 0.88, 1.05), clamp(MicNightStrength, 0.0, 1.0));
		color.rgb = mix(color.rgb, graded, clamp(MicNight, 0.0, 1.0));
	}

	// Better Simple Clouds: the moon's forward-scattered light. Added UNIFORMLY - it is a pure function of the view
	// ray, never of this fragment's colour or alpha - so it cannot recreate the colour<->alpha correlation that the
	// long note above warns about. Placed before the distance fog so a glowing far cloud still recedes into the haze.
	color.rgb += micMoonScatter();

	// === Better Simple Clouds: distance fog, with the opaque pass' optional fog resistance. The alpha lerp is split out
	// first because the two halves are not the same kind of thing: MicFogResist is a COLOUR choice ("hold far clouds back
	// from washing out"), while lerping alpha toward FogColor.a feeds the OIT coverage below and must keep its stock
	// behaviour regardless. At MicFogResist = 0 these two lines are exactly the stock mix(color, FogColor, fogT). ===
	float fogT = smoothstep(micFogStart(), micFogEnd(), fogDistance);
	color.a = mix(color.a, FogColor.a, fogT);
	color.rgb = mix(color.rgb, FogColor.rgb, fogT * (1.0 - clamp(MicFogResist, 0.0, 1.0)));

	// === Better Simple Clouds: the rest of the scene-match grade, applied exactly as clouds.fsh applies it to the opaque
	// body - same order, same expressions, same uniform values (CloudSceneGrade feeds both passes). Keeping the two in
	// lockstep is the whole point: the fringe wraps the body, so a difference between the passes does not read as a
	// slightly-off fringe, it reads modulated by fringe alpha = the cloud noise field - i.e. as noise mottling painted
	// across every cloud face. The aerial tint ramps over FogEnd, which is also where Simple Clouds ends the cloud field,
	// so a fog mod pulling FogEnd in (Better Fog does, at night) now just compresses both passes together. ===
	if (MicSkyTint > 0.0)
	{
		float aerial = smoothstep(0.0, micFogEnd(), fogDistance);
		color.rgb = mix(color.rgb, MicSkyColor, clamp(MicSkyTint * aerial, 0.0, 1.0));
	}
	if (MicSaturation != 1.0)
	{
		float grey = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
		color.rgb = mix(vec3(grey), color.rgb, MicSaturation);
	}
	color.rgb *= MicBrightness;
	if (MicEdgeFadeStrength > 0.0 && MicEdgeFadeEnd > MicEdgeFadeStart)
	{
		float edge = smoothstep(MicEdgeFadeStart, MicEdgeFadeEnd, fogDistance);
		color.rgb = mix(color.rgb, FogColor.rgb, clamp(edge * MicEdgeFadeStrength, 0.0, 1.0));
	}

	// === Better Simple Clouds: soft terrain intersection. Scaling color.a - not premul.rgb, and not the OIT weight - is
	// load-bearing: weighted-blended OIT coverage is 1 - prod(1 - a_i), a function of ALPHA alone, so revealage
	// (= premul.a) has to drop too or a faded fringe would still cut the background out of the composite and read as a
	// black silhouette instead of a fade. Placement is deliberate too: AFTER the fog mix (which lerps alpha, and would
	// partly undo an earlier fade) and BEFORE the premultiply. ===
	color.a *= micSoftAlpha();

	vec4 premul = vec4(color.r * color.a, color.g * color.a, color.b * color.a, color.a);

	// === Better Simple Clouds: normalize the OIT weight's depth term over the cloud field's ACTUAL extent instead of
	// Simple Clouds' hardcoded 1000. This is what stops a far cloud from showing through a nearer cloud's translucent
	// face.
	//
	// The depth term is the ONLY thing that makes this blend respect occlusion: it is what lets a near fringe outweigh a
	// far one, since the rest of the weight (premul.a) knows nothing about order. It only works if the fragments actually
	// spread out across z. Stock Simple Clouds spans clouds over ~2867 blocks, so z = vertexDistance/1000 spreads them
	// across the whole [0,1] range and pow(1 - z, e) separates near from far by orders of magnitude.
	//
	// Better Fog's Simple Clouds compat clamps the cloud fog end at night and Simple Clouds derives its mesh cull
	// distance from that same value, so the entire cloud field collapses to ~205 blocks (see CloudSceneGrade). Every
	// fragment then lands in z ~ [0.13, 0.25], where pow(1 - z, 3) only varies by ~1.5x - the depth term goes flat and
	// can no longer order anything. The blend degenerates into a plain alpha-weighted AVERAGE of whatever overlaps the
	// pixel, so a cloud behind another cloud's fringe contributes about as much as the fringe in front of it, and since
	// both alphas are the cloud noise field, the average swings between the two clouds' colours in a noise pattern. That
	// is "a cloud behind another cloud's transparent face" going mottled.
	//
	// Dividing by the field's real extent restores the spread (z ~ [0.62, 1.0] at 205 blocks -> a ~67x near/far ratio at
	// e = 3, i.e. real occlusion again). Clamped to 1000 so that whenever the field is at least Simple Clouds' normal
	// size this is bit-for-bit the stock expression - the fix only engages once a fog mod has actually shrunk the field. ===
	float z = min(vertexDistance / max(min(MicWeightScale, 1000.0), 1.0), 1.0);
	// === Better Simple Clouds: flatten the OIT weight's depth term. Stock Simple Clouds uses pow(1 - z, 3.0), which makes
	// a NEAR fringe cube's weight dwarf a FAR one's; across a big cloud's thick translucent fringe the order-independent
	// blend is then dominated per-patch and reads as faces darker/lighter in a noise pattern. Softening the exponent
	// (3.0 -> 0.5) makes near and far fringe contribute comparably so the blend is smooth. MicWeightFlatten = 0 keeps the
	// stock exponent exactly. Note this trades AGAINST the depth ordering above - it deliberately weakens the same term -
	// so it is a genuine trade-off, not a free win: turn it down if far clouds start reading through nearer ones. ===
	float micExp = mix(3.0, 0.5, clamp(MicWeightFlatten, 0.0, 1.0));
	float weight = max(premul.a * 3000.0 * pow(1.0 - z, micExp), 0.01);

    accumColor = premul * weight;
    revealage = premul.a;
}
