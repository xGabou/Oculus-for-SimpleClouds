# Install Oculus For SimpleClouds

## Prerequisites
- Minecraft 1.20.1
- Forge 47.x
- Java 17
- Simple Clouds installed
- Oculus installed

## Officially Supported Shader Setup
README says the official shader integration target is Atmospheric Shaders.
If you want the documented shader behavior, use Atmospheric Shaders.

## Install Order
Based on the repository README:
1. Install Forge for Minecraft 1.20.1.
2. Install Oculus.
3. Install Simple Clouds.
4. Install Oculus For SimpleClouds.
5. Install Atmospheric Shaders if you want the officially supported shader workflow.

## Basic Verification
After launching the game:
- The game should start without missing-dependency errors.
- Simple Clouds should render with Oculus enabled.
- If you select a shader pack that is not Atmospheric Shaders, OFSC should show an unsupported shader warning.
- If you use the cloud dimension whitelist, the file should exist at `config/oculus_for_simpleclouds/cloud_dimension_whitelist.json`.

## If Installation Fails
Check:
- Minecraft version is 1.20.1.
- Loader is Forge, not Fabric or NeoForge.
- Oculus and Simple Clouds are both present.
- You are not treating unsupported shader packs as officially supported.
