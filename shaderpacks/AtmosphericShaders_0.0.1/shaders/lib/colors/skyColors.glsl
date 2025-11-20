#ifndef INCLUDE_SKY_COLORS
#define INCLUDE_SKY_COLORS

#include "/lib/util/sc_bridge.glsl"

#ifdef OVERWORLD

    vec3 skyColorSqrt = sqrt(skyColor);
    float invRainStrength2 = (1.0 - rainStrength) * (1.0 - rainStrength);
    vec3 skyColorM = mix(max(skyColorSqrt, vec3(0.63, 0.67, 0.73)), skyColorSqrt, invRainStrength2);
    vec3 skyColorM2 = mix(max(skyColor, sunFactor * vec3(0.265, 0.295, 0.35)), skyColor, invRainStrength2);



    #if USE_SC
        float scThickness = Get_SC_ThicknessRaw();

        float stormDarkSky      = mix(1.0, 0.28, scStormDark);
        float stormDarkHorizon  = mix(1.0, 0.42, scStormDark * 1.15);
        float stormCoverage     = clamp(scStormDark * scThickness, 0.0, 1.0);
        float stormSunFade      = smoothstep(0.08, 0.6, stormCoverage);
        float stormSunOcclusion = mix(1.0, 0.04, stormSunFade);

        vec3 scStormTint = mix(vec3(1.0), vec3(0.6, 0.64, 0.72), scStormDark);
        float scStormBrightness = mix(1.0, 0.55, scStormDark);
        float scThicknessBlend = mix(0.95, 1.05, scThickness);
    #else
        float scThickness = 0.0;
        float stormDarkSky = 1.0;
        float stormDarkHorizon = 1.0;
        float stormCoverage = 0.0;
        float stormSunFade = 0.0;
        float stormSunOcclusion = 1.0;
        vec3 scStormTint = vec3(1.0);
        float scStormBrightness = 1.0;
        float scThicknessBlend = 1.0;
    #endif

    #ifdef SPECIAL_BIOME_WEATHER
        vec3 nmscSnowM = inSnowy * vec3(-0.3, 0.05, 0.2);
        vec3 nmscDryM = inDry * vec3(-0.3);
        vec3 ndscSnowM = inSnowy * vec3(-0.25, -0.01, 0.25);
        vec3 ndscDryM = inDry * vec3(-0.05, -0.09, -0.1);
    #else
        vec3 nmscSnowM = vec3(0.0), nmscDryM = vec3(0.0), ndscSnowM = vec3(0.0), ndscDryM = vec3(0.0);
    #endif

    #if RAIN_STYLE == 2
        vec3 nmscRainMP = vec3(-0.15, 0.025, 0.1);
        vec3 ndscRainMP = vec3(-0.125, -0.005, 0.125);
        #ifdef SPECIAL_BIOME_WEATHER
            vec3 nmscRainM = inRainy * ndscRainMP;
            vec3 ndscRainM = inRainy * ndscRainMP;
        #else
            vec3 nmscRainM = ndscRainMP;
            vec3 ndscRainM = ndscRainMP;
        #endif
    #else
        vec3 nmscRainM = vec3(0.0), ndscRainM = vec3(0.0);
    #endif

    vec3 nmscWeatherM = vec3(-0.1, -0.4, -0.6) + vec3(0.0, 0.06, 0.12) * noonFactor;
    vec3 ndscWeatherM = vec3(-0.15, -0.3, -0.42) + vec3(0.0, 0.02, 0.08) * noonFactor;


    //-------------------------------------------------
    // RAW BASE COLORS (constants)
    //-------------------------------------------------

    vec3 _noonUpSkyColor     = pow(skyColorM, vec3(2.9)) * scStormTint * scStormBrightness;
    vec3 _noonMiddleSkyColor = (skyColorM * (vec3(1.15) + rainFactor * (nmscWeatherM + nmscRainM + nmscSnowM + nmscDryM))
                                   + _noonUpSkyColor * 0.6) * scStormTint * scThicknessBlend;
    vec3 _noonDownSkyColor   = (skyColorM * (vec3(0.9) + rainFactor * (ndscWeatherM + ndscRainM + ndscSnowM + ndscDryM))
                                   + _noonUpSkyColor * 0.25) * mix(vec3(1.0), scStormTint, 0.5);

    vec3 _sunsetUpSkyColor     = skyColorM2 * (vec3(0.8, 0.58, 0.58) + vec3(0.1, 0.2, 0.35) * rainFactor2);
    vec3 _sunsetMiddleSkyColor = skyColorM2 * (vec3(1.8, 1.3, 1.2) + vec3(0.15, 0.25, -0.05) * rainFactor2);
    vec3 _sunsetDownSkyColorP  = vec3(1.45, 0.86, 0.5) - vec3(0.8, 0.3, 0.0) * rainFactor;

    vec3 sunsetDownSkyColorP   = _sunsetDownSkyColorP;

    vec3 _sunsetDownSkyColor   = (_sunsetDownSkyColorP * 0.5 + 0.25 * _sunsetMiddleSkyColor) * stormDarkHorizon;


    vec3 _dayUpSkyColor     = mix(_noonUpSkyColor, _sunsetUpSkyColor, invNoonFactor2);
    vec3 _dayMiddleSkyColor = mix(_noonMiddleSkyColor, _sunsetMiddleSkyColor, invNoonFactor2);
    vec3 _dayDownSkyColor   = mix(_noonDownSkyColor, _sunsetDownSkyColor, invNoonFactor2);

    vec3 _nightColFactor      = vec3(0.07, 0.14, 0.24) * (1.0 - 0.5 * rainFactor) + skyColor;
    vec3 _nightUpSkyColor     = pow(_nightColFactor, vec3(0.90)) * 0.4;
    vec3 _nightMiddleSkyColor = sqrt(_nightUpSkyColor) * 0.68;
    vec3 _nightDownSkyColor   = _nightMiddleSkyColor * vec3(0.82, 0.82, 0.88);


    //-------------------------------------------------
    // FINAL VALUES
    //-------------------------------------------------

    vec3 noonUpSkyColor       = _noonUpSkyColor * stormSunOcclusion;
    vec3 noonMiddleSkyColor   = _noonMiddleSkyColor;
    vec3 noonDownSkyColor     = _noonDownSkyColor;

    vec3 sunsetUpSkyColor     = _sunsetUpSkyColor * stormSunOcclusion;
    vec3 sunsetMiddleSkyColor = _sunsetMiddleSkyColor;
    vec3 sunsetDownSkyColor   = _sunsetDownSkyColor;

    vec3 dayUpSkyColor        = _dayUpSkyColor * stormDarkSky;
    vec3 dayMiddleSkyColor    = _dayMiddleSkyColor * stormDarkSky;
    vec3 dayDownSkyColor      = _dayDownSkyColor * stormDarkHorizon;

    vec3 nightUpSkyColor      = _nightUpSkyColor * stormDarkSky;
    vec3 nightMiddleSkyColor  = _nightMiddleSkyColor * stormDarkSky;
    vec3 nightDownSkyColor    = _nightDownSkyColor * stormDarkHorizon;

#endif

#endif // INCLUDE_SKY_COLORS
