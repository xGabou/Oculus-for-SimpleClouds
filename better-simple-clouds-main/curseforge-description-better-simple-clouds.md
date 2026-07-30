# Better Simple Clouds

**Short summary (for the CurseForge "Summary" field):**
> Improvements and fixes for Simple Clouds: feel-inside-the-cloud immersion, far-cloud LOD taming, storm-fog fixes, a transparent-edge fix, a cloud-type spawn blacklist, plus a ReTerraForged worldgen crash-guard. Video options integrate with Sodium / Reese's Sodium Options.

---

## Simple Clouds' volumetric skies, polished.

**Better Simple Clouds** is an add-on for
[Simple Clouds](https://www.curseforge.com/minecraft/mc-mods/simple-clouds) (required) that polishes how its
clouds look and behave — especially at long render distances and under Iris shaderpacks.

### ☁️ In-cloud immersion
Flying into a cloud actually feels like being inside one: cloud faces stay **solid** (no seeing straight through),
the interior **fills with haze** ramping to an opaque shell at a configurable view distance, translucent **motes
drift** through the interior (scaled by storminess), and optional cloud-coloured fog caps the view without shaders.

### 🏔️ Far-cloud detail
Stop distant clouds from degrading into giant single cubes: **cap the LOD coarseness** (far rings rebuilt from finer
cubes), **soften or cull far small clouds** to declutter the horizon while the dramatic storm masses stay, and
**remove stray floating cubes** without ever eroding a real cloud.

### 🌧️ Storm fog (distant rain)
The far rain wall can stay **visible behind clouds** (instead of being cut off by the cloud layer's depth), and its
whole-screen **blur can be disabled** so it stops smearing over near rain (e.g. Pretty Rain). Shader render path
supported.

### ✨ Transparency fix
Clears the mottled "inside and outside coincide" noise where a cloud's translucent edges are seen through another
cloud, by flattening Simple Clouds' OIT depth weighting (strength slider).

### 🚫 Cloud-type spawn blacklist
Block any cloud type from **ever spawning** — natural spawns re-roll to another type (cloud count unchanged), adds
through the Simple Clouds API by other mods are refused, and existing ones are swept away. By default only the tiny
`itty_bitty` type is blocked. Every known cloud type gets a Blocked/Allowed toggle in the options screen, and custom
ids (datapacks, other mods) can be added.

### 🌍 ReTerraForged worldgen crash-guard
Bundled bonus: stops a server-start crash where a mod samples biomes before
[ReTerraForged](https://www.curseforge.com/minecraft/mc-mods/reterraforged)'s worldgen context is ready (e.g.
ProjectAtmosphere's server-start weather forecast) — those premature samples return a neutral value instead of
throwing, and terrain resolves normally once worldgen is initialized. Only active when ReTerraForged is installed;
toggle it under *Mods → Better Simple Clouds → Config*.

### ⚙️ Options, done right
- **Video/visual options** appear in **Sodium's video settings** — fully compatible with **Reese's Sodium
  Options** — under "Better Simple Clouds", with the mod's icon, grouped into pages.
- **Behaviour options** (the blacklist, debug) live in a **searchable options screen** (*Mods → Better Simple
  Clouds → Config*) with per-option reset and tooltips.
- Everything applies **live** — no restarts.

## 📦 Requirements

- Minecraft **1.21.1** · **NeoForge 21.1.x**
- **Simple Clouds** (required)
- **Sodium** optional (for the video-settings integration) · **Iris** optional (shader render path)
- **ReTerraForged** optional (only then is the worldgen crash-guard active)

## 🙏 Credits

An **independent, unofficial** add-on — not affiliated with or endorsed by Simple Clouds. All credit for the
underlying clouds goes to **nonamecrackers2** (Simple Clouds). From the author of **Make it Compatible**.
