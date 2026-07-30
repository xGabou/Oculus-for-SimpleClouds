# Better Simple Clouds

**Simple Clouds' volumetric skies, polished.** *(requires Simple Clouds — NeoForge 1.21.1)*

An add-on that improves how Simple Clouds looks and behaves, especially at long render distances and
under Iris. You get real fog when you're inside a cloud, cleaner far clouds and edges, storm-fog
fixes, and a per-type cloud spawn blacklist — plus a crash-guard for ReTerraForged worldgen. Video
options integrate with Sodium / Reese's Sodium Options.

## Building

Requires JDK 21 (Gradle auto-provisions the toolchain).

```bash
./gradlew bscJar     # builds the jar -> build/libs/better-simple-clouds-<version>.jar
./gradlew runClient    # launches a dev client (put bridged mods in run/mods/)
```

The mods this bridges are compile-only and **not** bundled — they're provided at runtime by your modpack.
To build, drop the matching jars into `libs/` (see [libs/README.md](libs/README.md)); they are gitignored
and never redistributed here.

## Compatibility model

Every patch **self-gates**: it activates only when the mods it bridges are installed, and stays dormant
(and harmless) otherwise. Safe to keep loaded with any subset of the target mods.

## License

[MIT](LICENSE) © leon.raineri
