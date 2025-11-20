#if USE_SC

    float scCloudThickness = Get_SC_ThicknessRaw();
    float scCloudStormDark = Get_SC_StormDarkness();
    float scThicknessAmbient = mix(0.85, 1.2, scCloudThickness);
    float scThicknessLight   = mix(0.9, 1.3, scCloudThickness);

    vec3 scCloudStormTint = mix(vec3(1.0), vec3(0.65, 0.7, 0.78), scCloudStormDark);
    float scStormDim      = mix(1.0, 0.55, scCloudStormDark);

    vec3 cloudRainColor = mix(nightMiddleSkyColor, dayMiddleSkyColor, sunFactor) * scCloudStormTint;

    vec3 cloudAmbientColor =
        mix(
            ambientColor * (sunVisibility2 * (0.55 + 0.1 * noonFactor) + 0.35),
            cloudRainColor * 0.5,
            rainFactor
        ) *
        (scThicknessAmbient * mix(1.0, 0.65, scCloudStormDark));

    vec3 cloudLightColor =
        mix(
            lightColor * (0.9 + 0.2 * noonFactor),
            cloudRainColor * 0.25,
            noonFactor * rainFactor
        ) *
        (scThicknessLight * scStormDim);

#else

    float scCloudThickness = 0.0;
    float scCloudStormDark = 0.0;
    float scThicknessAmbient = 1.0;
    float scThicknessLight   = 1.0;
    float scStormDim         = 1.0;

    vec3 cloudRainColor = mix(nightMiddleSkyColor, dayMiddleSkyColor, sunFactor);

    vec3 cloudAmbientColor = ambientColor;
    vec3 cloudLightColor    = lightColor;

#endif
