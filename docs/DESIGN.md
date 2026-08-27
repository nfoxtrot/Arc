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
3. **Builder ("Plan a route")** — a "✨ Suggest chapters for me" option (optional AI suggestions) and a **"Confirm lesson plan"** button that locks the fields and triggers the plane-flight animation. "Edit route" unlocks it again. Start topic, chapters (1–3), and End topic: the wireframe spec called for separate Start/End fields above a vertical chapters list with drag-and-drop reordering (⠿ handle); as of 2026-08-26 all three render as **one horizontal, snap-scrolling waypoint timeline** — Start and End as fixed endpoint cards (labeled START/END) framing the reorderable chapter waypoints (WP-code labels, dot-and-line connectors, left/right chevrons) in between. Both changes were requested directly (design audit + explicit ask), not spec-driven. Reordering is still buttons, not real drag gestures (see ยง6 open items).
4. **Lesson View** — brain-globe + Save / Export as PDF / Export as text buttons; continuous scrolling chapter content (not an accordion); a waypoint scrollspy nav (dots, right-side on wide layouts / horizontal strip on narrow layouts) that highlights the active chapter via `IntersectionObserver` and lets the user jump to a section by tapping a dot. Each chapter shows a placeholder photo block (`[credited photo — source cited below]`) and a `Source: example.edu` line.
5. **Profile/Account** — added 2026-08-26, not part of the original wireframe (this section originally covered 4 screens; there are 5 now). No spec existed for this one, so content was decided directly rather than wireframed first: avatar initial, editable preferred name + email, sign out (signed-in state); a distinct signed-out state with a prompt and a button that reopens the signup sheet. Lives as a 4th bottom tab (a deliberate, explicit change to the original 3-tab layout — see ยง9 changelog).

### Android app shell
- **Bottom tab bar**, not top tabs: Home / Plan / Lessons / **Profile** (added 2026-08-26; original spec had only the first three). Icons are real Material icons as of 2026-08-26, replacing the original ⌂ ✎ ▤ unicode placeholders.
- Top bar: back arrow (appears only when there's screen history), ARC logo, light/dark toggle
- **Back-stack navigation** simulated via a history array (push/pop) with slide transitions (forward slides in from the right, back slides in from the left — Material-style motion)
- Wrapped in a phone-frame mockup (status bar, rounded device chrome) with device-width presets: standard phone (390px), Pixel 10 Pro Fold cover (380px) and unfolded (700px), Galaxy Fold cover (344px) and unfolded (745px)
- **Narrow breakpoint at 500px:** below it, all screens reflow to single-column stacked layouts (globe moves above content); at/above it, two/three-column desktop-style layouts are used. **Both layout modes must be kept available** — don't drop the wider grid option even though phone-first is the priority.
- ⚠️ **Known limitation:** a browser/web preview cannot detect a real foldable's hinge or posture — that requires native Jetpack WindowManager APIs in the compiled Android app. The presets above are for manual preview only; real fold-state-aware behavior needs to be built natively.
- ~~**Gap not yet addressed:** no Profile/Account tab or screen yet, despite accounts being a confirmed requirement.~~ Closed 2026-08-26 — see screen #5 above.

---

## 5. Technical / build decisions

- **Platform:** native Android app (pivoted from an earlier "website" direction). Android-first; an iOS version will be retrofitted later.
- **Backend:** full-stack build with real user accounts (email/password + Google OAuth), and save/export of lessons (PDF and plain text). Preferred name captured at signup.
- **Deployment/build tool:** **Manus** — confirmed capable of generating native Android (and iOS) apps, packaging Android output as an AAB for Google Play submission, and handling full-stack backend needs (auth, database, deployment) in one pipeline.
- **minSdkVersion: 31 (Android 12).** Chosen so Material You dynamic color is guaranteed available on every installable device — no static-theme fallback path needed. Tradeoff acknowledged: excludes a meaningful share of older Android devices industry-wide; acceptable since the primary test device (Pixel 10 Pro Fold) is current and this is a personal/niche-scale app for now.
- **targetSdkVersion: 37** — updated from the previously documented 36 (confirmed 2026-08-22; the gradle file was found to already be on 37, and the user confirmed 37 is the correct/current target rather than the doc).
- **JDK: 17 minimum, 21 preferred** — required by the modern Android Gradle Plugin (AGP 8.x), not by Material You itself. Android Studio bundles its own JDK (JetBrains Runtime).
- **IDE:** Android Studio, using the official Anthropic Claude Code plugin (not the unofficial "IntelliClaude"). Requires the Claude Code CLI installed and authenticated separately, then the plugin from JetBrains Marketplace, then restart. Connect via running `claude` in the integrated terminal, or `/ide` from an external terminal.
- **Kotlin + Jetpack Compose** as the app framework — effectively locked in by implementation as of 2026-08-25 (real screens built on it, see ยง8 changelog), though never explicitly confirmed in so many words. Flagging that distinction rather than silently treating it as formally decided.
- **Material 3 Expressive components (2026-08-26), explicitly NOT real dynamic color.** `androidx.compose.material3:material3` is pinned to `1.5.0-alpha27` (above the stable version the Compose BOM otherwise provides) specifically for `ButtonGroup` and other Expressive components. This is a real tradeoff — an alpha library dependency, not a stable one — confirmed deliberate: when asked for "the most up-to-date Material You experience," the two different things both called "Material You" were surfaced explicitly (Expressive components vs. actual wallpaper-derived dynamic color, which is what `minSdk 31` above was actually chosen for) and Expressive-components-only was the confirmed choice. ARC's fixed brand palette is unchanged. Revisit the pin once material3 1.5.0 reaches stable and the BOM catches up.
- **Primary test device:** Google Pixel 10 Pro Fold (cover/outer screen + inner/unfolded screen support). Also wants scaling support for the Galaxy Fold and "every phone size" generally.

---

## 6. Open items

| Item | Status |
|---|---|
| `CLAUDE.md` summarizing project context for this repo | Offered, not yet drafted |
| Real-time 3D (Filament/SceneView) integration | Not started — Home/Lesson screens use a static placeholder image of the brain for now, by explicit choice (2026-08-23) |
| Drag-and-drop chapter reordering (Builder) | Simplified to left/right chevron buttons on the waypoint timeline for now, real drag gesture not implemented |
| Scrollspy nav (Lesson View) | Simplified to `firstVisibleItemIndex`, not true IntersectionObserver-equivalent behavior |
| Backend: auth, save/export, AI chapter suggestions | All stubbed/no-op in the UI — no backend wired up; Profile's data is real but local-only (not persisted across restarts) |
| Builder audit findings (2026-08-26) not yet acted on | Disabled "Confirm lesson plan" gives no reason why; "Suggest chapters for me" is enabled before Start/End exist; per-card reorder chevrons could be confused with row-scroll controls now that Start/End share the row |
| Full-app audit findings (2026-08-26) not yet acted on | Signup: no password-visibility toggle on Password/Confirm password; disabled primary "Let's get started" reads less prominent than the fully-colored "Continue with Google" tonal button below it before the form is valid; "Skip for now" uses the brand accent pink for a low-emphasis dismissive action, diluting what that color signals elsewhere |
| Profile screen polish (photo upload, password change, account deletion) | Deliberately out of scope for the first pass — only what signup already collects is shown/editable |
| `arc-wireframe.jsx` wireframe file | Not yet added to this repo |
| `material3` alpha pin (`1.5.0-alpha27`) | Needs revisiting once material3 1.5.0 reaches stable and the Compose BOM includes it — the pin is a deliberate but real tradeoff, not meant to be permanent |
| `rememberModalBottomSheetState` deprecation (SignupSheet) | Deprecated in the pinned material3 version in favor of `rememberBottomSheetState`, not yet migrated — didn't want to guess the new API's parameters mid-task |
| Only 4 of 5 button types used | `ElevatedButton`/`SplitButton`/toggle button families weren't needed for anything on screen yet, not "rejected" |
| Bottom nav selected-tab color | Currently Material3's default-derived color, not explicitly matched to the ARC accent token — minor polish item |

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
  - **Known unresolved problem:** both exported `.glb` files are still huge (brain: 97.8MB, plane: 91.5MB) — baking only fixed the texture; the real weight is Meshy's high-poly mesh geometry, untouched so far. This still needs a decimation/optimization pass before either asset should be considered final or integrated into the actual app.
- **2026-08-23:** Set up **Git LFS** for `*.glb` (`.gitattributes` added) and committed both 3D assets through it — confirmed the git history only stores small LFS pointer files (~130 bytes each), with the real ~189MB in LFS storage, not bloating the repo. Note for later: GitHub's free tier includes only 1GB LFS storage + 1GB bandwidth/month; these two assets already use ~189MB of that, and every future re-committed version adds more. Not a problem yet, but worth tracking as more/updated 3D assets are added.
- **2026-08-25:** Optimized both 3D assets: decimated to 150,000 triangles (from ~3.14M each — a naive 20k-triangle first attempt caused visible faceting on the glossy surfaces, so 150k was the quality floor found by testing) plus Draco mesh compression on export. Brain: 97.8MB → 9.1MB (91% smaller). Plane: 91.5MB → 3.1MB (97% smaller). Re-verified visually with no quality loss, re-committed through Git LFS (only ~12MB re-uploaded).
- **2026-08-25:** Built the first real app screens in Kotlin/Compose, replacing the default template — Home, Signup (bottom sheet), Builder, and Lesson View, plus the navigation shell (bottom tabs, top bar with conditional back arrow + dark/light toggle, slide transitions). Design tokens and typography (Space Grotesk/Inter/IBM Plex Mono, fetched from Google's font repo and bundled locally) are wired up for real, using the ARC brand palette rather than Material You dynamic color (a deliberate choice — see ยง5's `minSdk 31` note for why that tension exists). The optimized pink brain asset was rendered to a static transparent PNG as a placeholder hero image (real-time 3D via Filament/SceneView is still a separate, not-yet-started task). Verified end-to-end on the `Pixel_10_Pro_Fold_API_37` emulator: all three tabs and the signup sheet work correctly. No backend, auth, or AI features are wired up — every non-navigation interactive element (Save/Export, Google sign-in, chapter suggestions) is a stub. Several spec details were simplified for this pass and flagged in ยง6's open items (chapter drag-and-drop, scrollspy behavior) rather than silently treated as done.
  - Along the way, the Android emulator from the previous session was found to be in a wedged state after ~2 days idle (adb connected at the transport level but the shell was unresponsive, and the adb server itself needed a forceful restart) — fixed by killing and starting a fresh emulator instance rather than continuing to fight the stale one.
- **2026-08-25:** Added the dark-mode blue brain-globe, closing that open item. Recolored the already-optimized pink brain's baked texture (hue-shift, same technique as the plane) to the `#6E8FC2` dark-mode accent token, preserving all gloss/shading — needed the same "darken beyond the literal token value" fix the pink brain required, since the render lighting washes out colors picked at face value. `HomeScreen`/`LessonScreen` now switch between the light/dark hero image based on the app's own `isDarkTheme` state (not system night-mode resource qualifiers, since the toggle is in-app, not tied to system dark mode). Verified on the emulator: toggling dark mode correctly swaps the brain image, accent color, and background across both screens.
- **2026-08-26:** Added the Profile screen as a 4th bottom tab (placement decided explicitly — see ยง4 screen #5). Added `UserState` so signup data (previously captured then discarded on dismiss) actually persists in-memory and shows up somewhere; `ProfileScreen` has distinct signed-in (avatar initial, editable name/email, sign out) and signed-out (prompt + reopen-signup button) states. Also replaced all 4 tabs' placeholder unicode glyphs with real Material icons and gave the bottom nav's selected state the actual ARC accent color (was defaulting to Material3's generic secondary color) — both were flagged polish items, now closed.
  - The emulator hit a **crash-consent dialog deadlock** on this session's first boot attempt (likely triggered by last session's forceful `taskkill` leaving stale crash-report state) — the emulator process was alive but stuck waiting on a native Windows dialog with no way to interact with it headlessly. Fixed by killing the process, deleting the stale crash database (`%TEMP%\AndroidEmulator\emu-crash-*.db`), and cold-booting (`--cold`, skipping any saved snapshot).
  - Testing the signup form via `adb shell input tap`/`input text` uncovered a real gotcha worth remembering: a `ModalBottomSheet`'s on-screen layout shifts significantly once the keyboard opens (fields move up ~400px), so coordinates captured before the keyboard appears become wrong for every field after the first. Worse, a lower field can end up positioned behind the IME's suggestion strip, silently swallowing taps meant for the app. The reliable fix was to stop tapping fields directly after the first one and instead send `KEYCODE_TAB` (`input keyevent 61`) to move focus between fields — immune to both problems.
- **2026-08-26:** Reworked the Builder screen's chapter list from a vertical list into a **horizontal, snap-scrolling waypoint timeline** (requested directly, not spec-driven) — WP-code labels, dot-and-line connectors, and mono typography reused from Lesson View, so the chapter-building step now visually reads as laying waypoints along the flight path rather than filling out a form. Reorder buttons became left/right chevrons (was up/down) to match the new orientation. Caught and fixed one bug before shipping: the trailing "Add chapter" card's connector dot was rendered in the same color as its own line, making it invisible — changed to a hollow ring for that "not yet placed" slot. Verified add/remove/reorder update state and WP-numbering correctly across 1–3 chapters.
- **2026-08-26:** Ran a quick design audit on the Builder screen against modern practice, per request, and acted on the top finding: Start/End topic were plain fields separated from the chapters row, which broke the "one continuous journey" mental model the flight-path metaphor is built on. Merged them into the **same horizontal timeline** as fixed endpoint cards (labeled START/END, no reorder/remove controls since they aren't list items) framing the chapter waypoints. This incidentally also resolved the previous entry's open question — scrolling the now-longer row was watched actually overflowing and snapping correctly (Start peeks off the left edge, End slides into view), so that's no longer just reasoned about. Three more audit findings were surfaced and intentionally not acted on yet (listed in ยง6): no inline reason shown for why "Confirm lesson plan" is disabled, "Suggest chapters for me" enabled before Start/End are filled in, and per-card reorder chevrons potentially reading as row-scroll controls now that Start/End share the row. (A fifth finding — horizontal scroll discoverability — turned out to resolve itself once Start/End made the row long enough to actually overflow, giving a natural partial-card peek.)
- **2026-08-26:** Asked for "the most up-to-date Material You experience... Android 12+" for buttons. Surfaced explicitly that this phrase covers two different things — Material 3 Expressive components (style-only, any Android version) vs. real wallpaper-derived dynamic color (the actual Android-12-gated feature, which `minSdk 31` was chosen for) — since implementing the wrong one would reverse a documented decision. Confirmed: Expressive components only, brand colors unchanged. Researched the real current API by reading the actual `androidx/androidx` library source on GitHub (the official docs site is too JS-rendered to scrape reliably) rather than going from training-data memory, which is stale for anything this new — found `ButtonGroup`/`clickableItem` need no experimental opt-in as of `material3:1.5.0-alpha27`, confirmed the exact function signatures before writing any code. Pinned that alpha version above the BOM's stable one (a real, flagged tradeoff — see ยง6). Applied `FilledTonalButton` to every medium-emphasis button across Home/Builder/Signup/Profile, and replaced Lesson View's 3 separate Save/Export buttons with one `ButtonGroup` cluster. Caught a real bug before calling it done: tonal buttons initially rendered a generic lavender-purple instead of ARC pink, because the theme never defined `secondaryContainer` (what `FilledTonalButton` actually reads its color from) — only found this by looking at the rendered screenshot, not by reading the color token list. Fixed by mirroring `primaryContainer`'s value onto `secondaryContainer` in both light and dark schemes.
- **2026-08-26:** Lifted the Builder screen's minimum-1-chapter requirement (requested directly): `RouteState.canConfirm` no longer requires `chapters.isNotEmpty()` (an empty list vacuously satisfies `chapters.all { it.title.isNotBlank() }`, so this now only blocks confirm when a chapter exists but is left blank), and `removeChapter` dropped its `chapters.size > 1` guard so the last chapter can be removed too. A route is now valid with just Start/End topic and zero chapters. Verified live on the emulator, not just from source: removed the default chapter down to zero, filled Start ("Photosynthesis") and End ("Cellular_Respiration") topics, and confirmed "Confirm lesson plan" actually turns from disabled/gray to enabled/solid-pink with no chapters present (screenshotted before and after).
- **2026-08-26:** Fixed a dark-mode status bar/nav bar contrast bug the user caught directly ("no contrast in the notification tray during dark mode"). Root cause: `MainActivity.enableEdgeToEdge()` was called with no arguments, so its default `SystemBarStyle.auto(...)` reads the *device's* system dark-mode setting to color system-bar icons - not ARC's own in-app dark toggle, which is deliberately independent of system theme (§5). Toggling ARC to dark while the phone itself was in light mode left status bar icons dark-on-dark, invisible. Fixed by driving `WindowInsetsControllerCompat.isAppearanceLightStatusBars`/`isAppearanceLightNavigationBars` directly off the app's own `isDarkTheme` state in a `SideEffect`. Verified both directions on the emulator.
- **2026-08-26:** Ran a full-app design audit at the user's request ("audit all pages, every single element"), screenshotting every screen (Home, Signup, Builder empty/filled/locked, Lessons, Profile signed-out/signed-in) in both light and dark mode. Found and fixed the most significant gap: **Lesson View never displayed the route's Start/End topics at all**, chapters or not - the "lesson" gave no indication of the actual journey it represented. Most visible with zero chapters (confirmable since the min-1-chapter fix above): the screen showed only the brain hero and Save/Export buttons, nothing else. Added a `RouteSummaryHeader` (Start/End topics either side of an arrow, in a tonal container matching the app's other cards) shown unconditionally above the chapter list, not just as a zero-chapter fallback. Verified in both themes and both zero-chapter and with-chapter states. Three smaller findings from the same audit were surfaced and intentionally not acted on yet (listed in §6): no password-visibility toggle on signup, an inverted-looking button hierarchy on the signup sheet before the form is valid, and "Skip for now" using the brand accent color for a low-emphasis action.
  - Process note: during the audit, repeatedly eyeballed `adb shell input tap` coordinates from screenshots instead of dumping the UI tree first, wasting a turn on a misleading result. Corrected mid-task and switched to `uiautomator dump` for exact bounds before every tap for the rest of the session, per explicit user feedback that guessing coordinates is not a lower-stakes exception to this project's "never guess" rule (§7).
- **2026-08-26:** Asked for "more of a Material You feel" on buttons/components, researched as options first rather than guessed. Surfaced 4 real options (real wallpaper-driven dynamic color; seeding the Material You HCT tonal algorithm with ARC's own brand color via the third-party `material-kolor` library; the same real dynamic color but as a user opt-in toggle; dynamic color on buttons only). Initial pick was real dynamic color (`dynamicLightColorScheme`/`dynamicDarkColorScheme`) — implemented and was mid-way through proving it live (button color visibly changed from fixed ARC pink to a wallpaper-derived navy blue with zero color-token edits) when the request was corrected: the actual want was Material You's *structural* language (tonal containers, filled/expressive component shapes) with ARC's exact colors kept fixed for consistency, not wallpaper reactivity. Reverted the dynamic-color change and the `material-kolor` dependency it would have needed, then implemented the corrected request instead:
  - Filled in `surfaceContainerLowest/Low/surfaceContainer/High/Highest` on both color schemes in `Theme.kt`, previously left undefined and silently falling back to Material3's generic neutral-derived tones — the same class of bug the `secondaryContainer` fix caught for tonal buttons, this time for the tonal-container roles Material You's layered "objects and containers" look actually depends on (e.g. `Card`'s default container color reads `surfaceContainerHighest`, confirmed from the real M3 token source, not assumed). No new colors invented — stepped between the existing `LightBg`/`LightBgAlt`/`LightCard` (and dark equivalents) tokens.
  - Replaced every `OutlinedTextField` with filled `TextField` across Builder, Signup, and Profile — the bordered-box look is the outlined M3 style, not the filled/tonal-container one Material You actually uses.
  - Bumped Card/container corner radius from the M3 default 12.dp to 20.dp on Builder's waypoint/endpoint cards and add-chapter slot, and on Lesson View's placeholder image card, for the chunkier rounding real Material You containers use.
  - Verified visually on the emulator in both light and dark mode after reverting: ARC's exact pink/blue accent colors are unchanged (confirms the wallpaper-reactive detour was fully undone), while cards and text fields now read as distinct layered/filled container surfaces instead of flat outlined boxes.