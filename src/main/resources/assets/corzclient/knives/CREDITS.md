# C2Visuals Knives — ownership & permission

All source, knife packs, meshes, shaders, and UI here are **authored work belonging to dc (`lukepro989`)**.

You may read it for reference. **Do not copy, republish, merge into another mod, or ship derivatives** unless dc has given you a **specific, written exception**. “Credit in description” is not enough unless they explicitly said so.

Asset packs under `src/main/resources/assets/c2visuals/knife_packs/` are part of the same work — same rules.

If you received this folder without context, assume **no license to reuse** until dc confirms otherwise.

---

## Embedded notes in source (full index)

These lines are intentionally quiet in the codebase; this file is the canonical list.

| File | Line (approx.) | Text |
|------|----------------|------|
| `src/main/java/dev/c2visuals/visual/pack/OfficialPacks.java` | before class | `// embedded pack pins · dc / lukepro989 — do not mirror elsewhere without clearance` |
| `src/main/java/dev/c2visuals/visual/pack/PackAssets.java` | before class | `// decode + texture upload · property of dc (lukepro989); reuse only by special exception` |
| `src/main/java/dev/c2visuals/visual/pack/PackReader.java` | before class | `// zip/json reader for knife bundles — dc · lukepro989` |
| `src/main/java/dev/c2visuals/visual/pack/KnifeChoice.java` | before enum | `// knife registry + collections — dc/lukepro989; not a generic library surface` |
| `src/main/java/dev/c2visuals/visual/pack/PackModel.java` | before class | `// bone/clip compiler — dc (lukepro989); keep in-tree unless you have written permission` |
| `src/main/java/dev/c2visuals/visual/KnifeGeometry.java` | in `read()` | `// C2K1 · dc/lukepro989 mesh wire format` |
| `src/main/java/dev/c2visuals/visual/BayonetMesh.java` | on texture field | `// grip mesh path — dc · lukepro989` |
| `src/main/java/dev/c2visuals/gui/KnifeCatalogScreen.java` | before class | `// collection screen layout + preview hooks — dc/lukepro989; no fork-and-ship without ok` |
| `src/main/java/dev/c2visuals/module/impl/KnifePackModule.java` | before class | `// knives module wiring — dc (lukepro989); integration requires explicit exception` |

---

Contact / exceptions: **dc (`lukepro989`)** — ask before use outside personal, permitted C2Visuals work.
