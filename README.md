# ScrollBubble

Android 8+ experimental floating Instagram Reels / YouTube Shorts counter.

## Browser-only build

1. Create a **private** GitHub repository named `ScrollBubble` (company policies permitting).
2. Upload the **contents** of this project, including the hidden `.github` folder. Easiest: use GitHub's browser file uploader, or a browser Codespace. Do not upload the ZIP as a single file; GitHub Actions needs the extracted source files.
3. Open **Actions > Build Android APK > Run workflow** (or push to `main`).
4. After a successful run, open the run, scroll to **Artifacts**, and download `ScrollBubble-debug-apk`.
5. Extract the artifact ZIP and transfer `app-debug.apk` to your **personal Android phone**. Install it after approving Android's installation warning if you trust the APK.
6. Launch ScrollBubble and grant **Display over other apps** and **Accessibility** permissions. Open Instagram Reels or YouTube Shorts to test.

No software needs installing on the laptop. GitHub Actions runs Gradle and the Android SDK remotely. Your Android phone must install the app.

## Known limitations

- Detection is a heuristic, **not an accurate guaranteed count**. Accessibility events can be missing, duplicated, or emitted for unrelated scrolling.
- Current screen detection depends on English accessibility labels including `Reels` or `Shorts` and engagement controls. On some app versions the overlay may not appear at all; this is safer than counting every feed scroll.
- One video can emit multiple scroll events; the 900 ms suppression reduces but does not eliminate overcounts.
- First video on entry is not counted; counters represent estimated transitions, not unique videos.
- Overlay is draggable by touching the bubble; other areas pass touches to the underlying app.
- Android/app vendor updates can break detection. Test on a personal phone with actual Instagram and YouTube versions and refine the detector.
- Counts reset at local midnight. This prototype has no historical reports.
- Publishing to Google Play requires reviewing Accessibility API policies and applicable disclosures.

## Privacy

No INTERNET permission or analytics SDK. Accessibility screen text is processed in memory and is not stored; only aggregate daily counts are stored on device. The overlay and accessibility permissions are powerful; revoke them in Android Settings when not using the app.

## Developer notes

Kotlin 2.0.21, Android Gradle Plugin 8.7.3, Gradle 8.9, Java 17, compile SDK 35. This project uses Android platform views (not Compose) to minimize dependencies. Gradle wrapper is intentionally omitted; the GitHub workflow provisions Gradle remotely.
