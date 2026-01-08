# Iris/Oculus For Simple Clouds

### Added
- Inital Release
### Fixed
- Registered the cloud composite render hook on the NeoForge event bus during client setup instead of relying on removed `EventBusSubscriber` annotations.
- Updated shader-aware pipelines to the 1.21.1 SimpleClouds API (Matrix4f render callbacks, new `BufferBuilder`/`MeshData` flow, lightning rendering, depth merge hand-off).
- Aligned Simple Clouds compat checks with the new `CompatHelper.isIrisLoaded()` helper.

### Changed / Removed
- Pose stack handling now rebuilds from the incoming view matrix before custom draws to match the new render pipeline contract.
