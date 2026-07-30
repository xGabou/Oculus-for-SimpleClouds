# Drop the dependency jars here

`build.gradle` compiles against every `*.jar` in this folder as `compileOnly` (they're provided at runtime by
your modpack and are never bundled into this mod). Put these here:

| File | What it is | Where to get it |
|------|-----------|-----------------|
| `littletiles.jar`      | LittleTiles 1.6.0-pre222+ (NeoForge 1.21.1) | [Modrinth](https://modrinth.com/mod/littletiles) / CurseForge |
| `creativecore.jar`     | CreativeCore 2.13.41+ (LittleTiles' hard dep) | [Modrinth](https://modrinth.com/mod/creativecore) / CurseForge |
| `sable.jar`            | Sable 2.0.3 (the published NeoForge jar) | [Modrinth](https://modrinth.com/mod/sable) / CurseForge |
| `sable-companion.jar`  | Sable's API library | extract from `sable.jar` → `META-INF/jarjar/sable-companion-common-*.jar` |
| `sable-rapier.jar`     | Sable's Rapier physics pipeline (rigid-body colliders) | extract from `sable.jar` → `META-INF/jarjar/dev.ryanhcode.sable.sable-sable_rapier-*.jar` |
| `veil.jar`             | Veil (Sable's render library) | extract from `sable.jar` → `META-INF/jarjar/veil-neoforge-*.jar` |
| `sodium.jar`           | Sodium 0.8.12-beta.2 (the **inner** mod jar with the real classes) | extract from `sodium-0.8.12-beta.2.jar` → `META-INF/jarjar/net.caffeinemc.sodium-neoforge-*-mod.jar` |

> **Immersive Portals** (for the Immersive Portals × Sable patch) is *not* needed here — it resolves automatically
> from the Modrinth maven as `maven.modrinth:zf4Szzx2:6.0.7` (its NeoForge "-all" jar is flat, so the one
> coordinate covers the whole compile classpath, `q_misc_util` included). The same goes for **MixinSquared**
> (bawnorton maven), whose NeoForge half is JarJar'd into the distributed Sable-mod jar automatically.

The last three are needed only at **compile** time. Sable's public API types (`SableCompanion`,
`ClientSubLevelAccess`, `Pose3d`, the Veil render hooks) live in the bundled Sable libraries, and the Sable
render-distance patch compiles against Sodium 0.8's config-integration API
(`net.caffeinemc.mods.sodium.api.config.*`) for its video-settings slider. Sodium ships those classes in a
JarJar'd inner jar, so the outer `sodium-*.jar` won't work as a compile dep — you must extract the inner one.
Extract them once:

```bash
cd libs
unzip -j sable.jar 'META-INF/jarjar/sable-companion-common-*.jar' -d .
unzip -j sable.jar 'META-INF/jarjar/dev.ryanhcode.sable.sable-sable_rapier-*.jar' -d .
unzip -j sable.jar 'META-INF/jarjar/veil-neoforge-*.jar' -d .
unzip -j sodium-0.8.12-beta.2.jar 'META-INF/jarjar/net.caffeinemc.sodium-neoforge-*-mod.jar' -d .
# rename to sable-companion.jar / veil.jar / sodium.jar (any *.jar name works)
```

> These are not committed (`.gitignore` excludes `libs/*.jar`) — they're other authors' mods.
