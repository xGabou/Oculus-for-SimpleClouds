# **Oculus For Simple Clouds**

A compatibility addon that allows **SimpleClouds** to work correctly with **Oculus or Iris** on Minecraft Forge.  
It exposes custom uniforms, fixes pipeline conflicts and ensures that cloud rendering stays consistent when shaders are enabled.

This addon is designed for packs that want **shader based cloud rendering**, especially when using **Project Atmosphere** and **Atmospheric Shaders**.

---

## **Oculus For Simple Clouds FAQ**

### ❓ Does this work with Project Atmosphere  
Yes. Full support.

### ❓ Can you request support for a specific shader  
Possibly, but two conditions must be met  
• The shader source must be public  
• I must have the legal right to republish a modified version  
This means shaders like **SEUS** cannot be supported.

### ❓ Does this modify Oculus code  
Yes and no.  
The mod uses Mixins at runtime to add the compatibility layer, but it does **not** bundle or redistribute any Oculus code.  
You must install Oculus separately.

### ❓ Can I use any shader with this mod  
No. Only **Atmospheric Shaders** is supported right now.  
Any other shader will work, but it will behave exactly as if the mod was not installed.

### ❓ Will clouds cast real shadows  
Not for now.  
SimpleClouds clouds are not world blocks. They are a screen space layer, so raycasting light onto them is not possible.  
Currently I use storminess and fade values to darken the world and fake the effect.  
Real cloud shadows remain a long term goal.


### ❓ I have an issue but the Issues tab isn’t there

Yes, that’s intended. I don’t read issues here, so the GitHub issues tab has been disabled.  
It’s simply easier for me to track everything in **one place** for all my mods.

It’s not that I dislike GitHub issues, and honestly, if I had the choice, I would’ve kept them enabled.  
But not everyone is comfortable with how GitHub works, so to make support more accessible for everyone, I use **Discord instead**: [https://discord.gg/2jRhTJgYz4](https://discord.gg/2jRhTJgYz4)



---

## **What this addon does**

### Shader aware cloud pipeline  
SimpleClouds normally uses two pipelines  
• a default pipeline  
• a shader aware pipeline  
When Iris or Oculus are active, the shader aware pipeline must be used.  
This addon ensures that the correct pipeline is chosen even when other mods try to override it.

### Depth behaviour fixes  
Some mods replace the cloud render pipeline.  
This addon restores correct behaviour by ensuring  
• clouds respect depth  
• clouds do not render through blocks  
• shaders receive the correct cloud uniforms

### Uniform exposure  
Atmospheric Shaders receives  
• cloud density  
• cloud type  
• storminess  
• height fade  
• cloud color data  
These values allow the shader pack to shade SimpleClouds as if it were fully integrated.

---

## **Current limitations**

### Only Atmospheric Shaders is fully supported  
Cloud lighting, tint, density and storm visuals are only implemented for Atmospheric Shaders.  
Other shaders will not crash, but they will ignore the SimpleClouds uniforms.

### No real volumetric shadows  
SimpleClouds is not volumetric in the world.  
Only fake shadows are possible for now.

### Distant Horizons compatibility is experimental  
Distant Horizons replaces the cloud pipeline.  
Work is ongoing to  
• allow shader compatible pipeline selection  
• merge DH depth with vanilla depth so clouds do not appear through blocks

---

## **How to install**

1. Install Forge  
2. Install Oculus for Forge  
3. Install SimpleClouds  
4. Install this mod  
5. Install Atmospheric Shaders for Simple Clouds Support

The addon only activates when both Oculus and SimpleClouds with compatible shaders are present.

---

## **Recommended setup**

### For best results  
Use  
**Atmospheric Shaders**  
SimpleClouds 3.0 or later  
Oculus 1.8 or later  
Project Atmosphere 0.3 or later  
All of these support consistent cloud data, fog layers, and weather uniforms.

---

## **Support**

If you have issues  
• include your log  
• mention your shader pack  
• mention your SimpleClouds version  
• mention whether Distant Horizons is installed

This helps reproduce the behaviour.

---

## **License**

This mod does not redistribute any part of Iris or Oculus.  
It injects compatibility layers at runtime using Mixins.  
Follow the licenses of all dependencies when modifying or distributing the project.
