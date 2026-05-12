# FAQ

## What does this mod do?
It is a compatibility addon for Simple Clouds and Oculus on Forge.
It keeps the shader-aware cloud path active and exposes cloud data to supported shader integrations.

## Does it work with any shader pack?
No.
README says only Atmospheric Shaders is officially supported.
Other shader packs may load, but OFSC-specific behavior is unsupported.

## Does it work with Project Atmosphere?
README says yes.
Project Atmosphere is explicitly described as supported.

## Does it bundle Oculus or Iris code?
README says no.
The project uses Mixins at runtime and does not redistribute Oculus code.

## Are there any commands?
No commands were found in the mod source.

## Does it have a config file?
Yes.
The repo contains a file-based config at:
`config/oculus_for_simpleclouds/cloud_dimension_whitelist.json`

## What does the config control?
It limits which dimensions each Simple Clouds cloud type can spawn in.
If a cloud type is missing or invalid, the default fallback is `minecraft:overworld`.

## Is Distant Horizons supported?
Only experimentally.
The README and repo notes do not present it as fully supported.

## Which Minecraft and loader versions are supported?
Repository metadata currently declares:
- Minecraft: 1.20.1
- Loader: Forge
