☁️ **Oculus for Simple Clouds update**

Client-only launcher enforcement is now in place for 1.21.1.

This mod no longer needs to be installed on a server. It will only run local launcher checks on the client and block the game if a suspicious launcher is detected.

## What changed

* Removed the server-side auth / challenge flow
* Removed the server install requirement from the mod metadata
* Added a client-side block screen for suspicious launcher detection

## Result

* Joining a server will not ask for this mod on the server anymore
* Singleplayer still gets the same local launcher check
* The mod is now client-side only in practice and in metadata

☁️ **Oculus for Simple Clouds update**


### Small fix was needed in the .toml file to remove the required dependency on Distant Horizons.

**DH COMPAT IS HERE FOR 1.21.1** 🔥🔥🔥

Distant Horizons compatibility is finally working on the NeoForge branch.

I updated the NeoForge branch to match the Forge branch, which means a lot of the latest rendering improvements are now available there too.

## What changed

* Distant Horizons compatibility is now working
* Better lighting
* Better shadows
* Improved cloud coloring
* Better rendering stability
* White fog and lightning should behave much better
* Bad weather sync resets should be fixed

## Important note

I do not want my mods to be used without a legitimate copy of Minecraft.

Because of that, the mod now includes a small check that can detect suspicious launcher setups. If you are using a legitimate launcher and a real copy of Minecraft, this should not affect you.

Thanks for the patience while I worked through this. This was one of the biggest blockers for proper DH support.
