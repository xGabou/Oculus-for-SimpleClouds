# Troubleshooting

## Startup or Login Failure
Common causes:
- Wrong Minecraft version.
- Wrong loader.
- Missing Simple Clouds.
- Missing Oculus.
- Shader pack parse errors in the active shader pack.

Check:
- Minecraft 1.20.1
- Forge 47.x
- Simple Clouds installed
- Oculus installed

## Clouds Missing or Rendering Incorrectly With Shaders
README states only Atmospheric Shaders is officially supported.
If another shader pack is selected, OFSC-specific rendering should be treated as unsupported.

Workaround:
- Switch to Atmospheric Shaders.
- Re-test before filing support requests.

## Distant Horizons Problems
The repository documents Distant Horizons support as experimental.
Symptoms may include pipeline conflicts or depth problems.

Workarounds:
- Re-test without Distant Horizons.
- Re-test without shaders.
- Check the known issues list before reporting the problem as a new bug.

## Cloud Dimension Whitelist Problems
OFSC uses a file config at:
`config/oculus_for_simpleclouds/cloud_dimension_whitelist.json`

Common mistakes:
- Invalid JSON.
- Invalid dimension ids.
- Empty arrays where you expected custom dimension support.

Behavior from source:
- If a cloud type is missing or has no valid dimensions, it defaults to `minecraft:overworld`.

## Unsupported Shader Prompt
If you choose a shader pack that is not Atmospheric Shaders, OFSC shows a warning prompt.
That is expected behavior.

## When To Check Known Issues
Check known issues first if the problem involves:
- Distant Horizons
- Unsupported shader packs
- Shader-specific rendering behavior
