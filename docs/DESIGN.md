# ARC — Design & Project Reference

Last updated: 2026-08-22

This document is the living source of truth for ARC's concept, design system, screens, and technical decisions. Update it as decisions change — don't let it go stale.

---

## 1. What ARC is

An Android app for people with ADHD and autism, built around a learning approach that favors hard, sourced data over memorization/flashcards.

**Core mechanic:** the user sets a **start topic** and an **end topic**, then defines **chapters** (1–3) in between — either typed manually or via AI-suggested contextual chapters. ARC compiles a **lesson plan**: sequential chapters, each with fact-driven content and **photos pulled live from credited/factual sources only** (museums, .gov/.edu, science publications, clear-attribution sources — never stock sites with unclear licensing). A live research check screens out flawed, controversial, or prejudiced sources before they're used.

**Hard rule: NEVER generate AI images.** All photos must be sourced/credited real photographs, not generated.

---

## 2. Name & concept

**ARC = Adaptive Route Comprehension.**

Visual metaphor: a flight path arcing across a globe — except the globe is a brain. Each chapter is a waypoint along the route. The user "flies" from their start topic to their end topic.

---

## 3. Design system

### Typography
- Display/headings: **Space Grotesk**
- Body: **Inter**
- Utility (waypoint codes, labels, data): **IBM Plex Mono**
- No tagline under the ARC logo

### Color tokens

**Light mode:**
```
bg:         #FBF9F5
bgAlt:      #F3EFE8
ink:        #1E2023
inkSoft:    #5B5D63
accent:     #E8829E   (low-opacity pink)
accentSoft: rgba(232,130,158,0.14)
line:       rgba(30,32,35,0.10)
card:       #FFFFFF
```

**Dark mode:**
```
bg:         #1B1D22   (gunmetal)
bgAlt:      #23262B
ink:        #ECEDEE
inkSoft:    #9CA0A8
accent:     #6E8FC2   (low-opacity dark blue)
accentSoft: rgba(110,143,194,0.16)
line:       rgba(236,237,238,0.10)
card:       #23262B
```

### Signature element — the "brain-globe"
- Brain-shaped silhouette (scalloped two-hemisphere outline, small brainstem), radial-gradient fill tinted pink (light) / blue (dark)
- Gloss highlight (blurred white ellipse, top-left) for a glossy 3D feel
- Gyri/sulci fold lines across the surface
- A flight-arc path runs across it from a start point to an end point
- Waypoints = dots along the arc, one per chapter, labeled like flight codes (`WP-00`, `WP-01`, ...)
- **Draft state** (while building a route): arc appears faint/dashed, growing incrementally as each chapter is filled in — no glow, no plane
- **Confirmed state**: once the user taps "Confirm lesson plan," the arc solidifies, glows, and a plane (✈) animates once along the full path
- **Excluded deliberately:** floating stars/lightning-bolt sparkle decorations and the closed orbit-ring from the original reference image — flight arc only, no closed loop

> **3D status (open item):** the current brain-globe is hand-coded SVG (vector paths + gradients), explicitly a **placeholder for concept/wireframe purposes only**. The real target is a **true 3D-rendered brain** matching the glossy 3D look of the original reference image. Two paths forward, neither started:
> 1. **Pre-rendered 3D asset** (Blender / 3D asset marketplace / AI 3D generation tool) exported as a static image or sprite, layered under the existing arc/waypoint/plane SVG overlay. Simpler, but the brain itself won't be live-interactive.
> 2. **Real-time 3D** via a native Android 3D framework (Filament/SceneView) using an actual `.glb`/`.gltf` 3D model. Fully interactive (rotate/zoom), bigger scope, and needs a sourced or created 3D model file as an input — this can't be generated directly in chat.
>
> No 3D asset has been sourced yet.

---

## 4. Screens

All four screens were designed as a wireframe in a single React/JSX artifact file: **`arc-wireframe.jsx`** (not currently present in this repo — needs to be added under e.g. `docs/wireframe/` if we want it version-controlled here).

1. **Landing/Home** — hero copy, brain-globe visual, CTA buttons ("Plan my first route," "See a sample route")
2. **Signup** — NOT a permanent screen/tab. A **one-time bottom-sheet modal** that pops up automatically the first time the app opens while signed out. Fields: preferred name, email, password, confirm password (with inline mismatch validation), "Continue with Google" OAuth button, "Skip for now" to dismiss. Submit button reads **"Let's get started, {name}"**.
3. **Builder ("Plan a route")** — start topic input, end topic input, 1–3 chapter inputs with drag-and-drop reordering (⠿ handle), a "✨ Suggest chapters for me" option (optional AI suggestions), and a **"Confirm lesson plan"** button that locks the fields and triggers the plane-flight animation. "Edit route" unlocks it again.
4. **Lesson View** — brain-globe + Save / Export as PDF / Export as text buttons; continuous scrolling chapter content (not an accordion); a waypoint scrollspy nav (dots, right-side on wide layouts / horizontal strip on narrow layouts) that highlights the active chapter via `IntersectionObserver` and lets the user jump to a section by tapping a dot. Each chapter shows a placeholder photo block (`[credited photo — source cited below]`) and a `Source: example.edu` line.

### Android app shell
- **Bottom tab bar**, not top tabs: Home / Plan / Lessons (⌂ ✎ ▤)
- Top bar: back arrow (appears only when there's screen history), ARC logo, light/dark toggle
- **Back-stack navigation** simulated via a history array (push/pop) with slide transitions (forward slides in from the right, back slides in from the left — Material-style motion)
- Wrapped in a phone-frame mockup (status bar, rounded device chrome) with device-width presets: standard phone (390px), Pixel 10 Pro Fold cover (380px) and unfolded (700px), Galaxy Fold cover (344px) and unfolded (745px)
- **Narrow breakpoint at 500px:** below it, all screens reflow to single-column stacked layouts (globe moves above content); at/above it, two/three-column desktop-style layouts are used. **Both layout modes must be kept available** — don't drop the wider grid option even though phone-first is the priority.
- ⚠️ **Known limitation:** a browser/web preview cannot detect a real foldable's hinge or posture — that requires native Jetpack WindowManager APIs in the compiled Android app. The presets above are for manual preview only; real fold-state-aware behavior needs to be built natively.
- **Gap not yet addressed:** no Profile/Account tab or screen yet, despite accounts being a confirmed requirement.

---

## 5. Technical / build decisions

- **Platform:** native Android app (pivoted from an earlier "website" direction). Android-first; an iOS version will be retrofitted later.
- **Backend:** full-stack build with real user accounts (email/password + Google OAuth), and save/export of lessons (PDF and plain text). Preferred name captured at signup.
- **Deployment/build tool:** **Manus** — confirmed capable of generating native Android (and iOS) apps, packaging Android output as an AAB for Google Play submission, and handling full-stack backend needs (auth, database, deployment) in one pipeline.
- **minSdkVersion: 31 (Android 12).** Chosen so Material You dynamic color is guaranteed available on every installable device — no static-theme fallback path needed. Tradeoff acknowledged: excludes a meaningful share of older Android devices industry-wide; acceptable since the primary test device (Pixel 10 Pro Fold) is current and this is a personal/niche-scale app for now.
- **targetSdkVersion: 37** — updated from the previously documented 36 (confirmed 2026-08-22; the gradle file was found to already be on 37, and the user confirmed 37 is the correct/current target rather than the doc).
- **JDK: 17 minimum, 21 preferred** — required by the modern Android Gradle Plugin (AGP 8.x), not by Material You itself. Android Studio bundles its own JDK (JetBrains Runtime).
- **IDE:** Android Studio, using the official Anthropic Claude Code plugin (not the unofficial "IntelliClaude"). Requires the Claude Code CLI installed and authenticated separately, then the plugin from JetBrains Marketplace, then restart. Connect via running `claude` in the integrated terminal, or `/ide` from an external terminal.
- **Suggested but not yet explicitly locked:** Kotlin + Jetpack Compose as the app framework (recommended for first-class Material3/Material You support). Treat as still slightly open until explicitly confirmed.
- **Primary test device:** Google Pixel 10 Pro Fold (cover/outer screen + inner/unfolded screen support). Also wants scaling support for the Galaxy Fold and "every phone size" generally.

---

## 6. Open items

| Item | Status |
|---|---|
| `[PACKAGE_NAME]` for the Android Studio project (e.g. `com.yourname.arc`) | Not yet provided |
| `CLAUDE.md` summarizing project context for this repo | Offered, not yet drafted |
| Profile/Account screen and tab | Not yet designed |
| 3D brain asset (pre-rendered vs. real-time) | Not yet sourced or created |
| Kotlin + Jetpack Compose as confirmed framework | Suggested, not explicitly locked |
| `arc-wireframe.jsx` wireframe file | Not yet added to this repo |

---

## 7. How Claude should work on this project

1. Never assume, guess, recall, or rely on common sense — especially as more variables get added. Confirm source data; treat new information with abductive reasoning.
2. If information needed to complete a task is missing, say so explicitly and explain the possible outcomes of proceeding without it.
3. Any time seed data is used, flag it immediately so the user can provide/confirm it.
4. Static data that's confirmed factually correct is **not** seed data and should not be flagged as such.
5. Never resolve ambiguity silently — lay out the relevant information and variables instead of guessing.
6. Group all assumptions in a task into a single, numbered, upfront list (not scattered through the response) so the user can address each one directly — do this every time.
7. Any seed data that needs the user's input should be presented as distinct, clearly labeled input fields (not just mentioned in prose).

---

## 8. Known quirks

- Earlier in this project, a stale/cached render of the wireframe artifact didn't reflect several rounds of already-made updates to the underlying file. If a change is reported as "didn't take," check whether the file itself is current before assuming a code bug — it may just be a stale preview needing a refresh/reopen.

---

## 9. Changelog

- **2026-08-22:** Repo initialized (`git init`), initial commit made with the Android Studio scaffold + this design doc. Git identity configured globally (Nick Foskette / nfoskette94@gmail.com). GitHub remote `origin` added (`https://github.com/nfoxtrot/Arc.git`), local branch renamed `master` → `main`. Claude Code Remote Control connected for mobile/phone access to this session.
- **2026-08-22:** Package renamed `project.arc` → `com.nfoskette.arc` (namespace + applicationId + all source file packages). `targetSdk`/`compileSdk` corrected to 37 in this doc (previously incorrectly documented as 36; the gradle file was already correct).
- **2026-08-22:** Android emulator set up and verified end-to-end: SDK command-line tools installed, Android 37.0 Google Play x86_64 system image installed, `Pixel_10_Pro_Fold_API_37` AVD created (matches primary test device) and booted successfully. `com.nfoskette.arc` built, installed, and launched on it — confirmed running via `adb` and a screenshot. A physical Pixel 9 Pro was also found already connected via `adb` on this machine, available for real-device testing.
- **2026-08-22:** First real progress on the 3D brain-globe asset (open item since project start). Used the user's Meshy AI account (API + web dashboard) to generate/process candidate models:
  - Brain: started from a Meshy "part-segmentation" export (10 flat-colored mesh parts, no real materials), ran it through the Meshy Retexture API (10 credits) as one experiment, and separately processed a web-dashboard-generated "Pink Cartoon Brain" model in Blender — applied a glossy material (low roughness, Coat layer) and a Fresnel-driven radial gradient tint in the design system's pink, then **baked** that procedural result into a real texture and exported a portable `.glb` (`docs/assets/3d-models/brain_v1_pink.glb`, not yet committed — see size note below). Confirmed via render: gloss highlight ✅, gradient tint ✅, brainstem + two-hemisphere split ✅ (visible from an underside camera angle).
  - Plane: processed the "Blue Sky Voyager" model (cute rounded toy-plane style, approved as the design direction) — recolored only the blue paint (by saturation-masked hue shift, base color texture only) to exactly match the dark-mode accent token `#6E8FC2`, keeping the white body untouched, then re-exported (`docs/assets/3d-models/plane_v1_blue.glb`, also not yet committed).
  - Blender 5.2 LTS was installed on this machine (via winget's normal `.msi` path, run interactively by the user — automated installs via winget/choco both failed: winget hit Cloudflare bot protection on download.blender.org, choco hit an elevation/permissions error) to do this rendering/material/baking work headlessly.
  - **Known unresolved problem:** both exported `.glb` files are still huge (brain: 97.8MB, plane: 91.5MB) — baking only fixed the texture; the real weight is Meshy's high-poly mesh geometry, untouched so far. Both files sit close to GitHub's 100MB hard file-size limit and have **not been committed to git** (copied into `docs/assets/3d-models/` locally only) pending a decision: decimate/optimize the geometry first, or set up Git LFS before committing large binaries. Not yet resolved — next session should pick this up before treating either asset as final.