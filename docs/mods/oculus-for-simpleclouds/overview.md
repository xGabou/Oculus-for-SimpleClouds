# Oculus For SimpleClouds

## What It Does
Oculus For SimpleClouds is a Forge compatibility addon for Simple Clouds.
It keeps Simple Clouds rendering working when Oculus shaders are active and exposes Simple Clouds data to supported shader integrations.

## Main Features
- Forces the shader-aware Simple Clouds pipeline when shaders are active.
- Exposes cloud, weather, and storm-related uniforms to the shader pipeline.
- Registers the Simple Clouds render target with the Oculus sky bridge when available.
- Adds a file-based cloud dimension whitelist.
- Includes experimental shader-aware depth handling for Distant Horizons.

## Who It Is For
- Modpacks using Simple Clouds on Forge 1.20.1.
- Players using Oculus with Simple Clouds.
- Packs targeting Atmospheric Shaders support.

## Dependencies
High-level runtime dependencies based on the repository:
- Minecraft Forge 1.20.1
- Simple Clouds
- Oculus

Optional or scenario-specific components:
- Atmospheric Shaders: the only shader pack explicitly documented as supported.
- Project Atmosphere: README says full support.
- Distant Horizons: support exists but is documented as experimental.

## Important Compatibility Notes
- Loader support in this repository is Forge only.
- Minecraft support in this repository is 1.20.1 only.
- README says only Atmospheric Shaders is officially supported for shader-specific integration.
- README says other shader packs may load, but OFSC-specific behavior should be treated as unsupported.
- README and repo notes describe Distant Horizons support as experimental.
