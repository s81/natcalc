# Install NatCalc on Your Android Phone — No PC Setup Required

## What happens
1. You upload this project to GitHub (free, browser-only)
2. GitHub automatically builds the APK in the cloud (~5 minutes)
3. You download the APK directly to your phone
4. Tap to install ✅

---

## Step 1 — Create a free GitHub account
Go to **https://github.com/signup** (if you don't have one already).

---

## Step 2 — Create a new repository

1. Go to **https://github.com/new**
2. Repository name: `natcalc`
3. Visibility: **Public** (required for free Actions minutes)
4. Click **Create repository**

---

## Step 3 — Upload the project files

On the empty repo page, click **"uploading an existing file"** link.

Drag-and-drop the entire `NatCalc` folder contents, **or** zip the folder and upload the zip.

> **Important:** GitHub's web uploader has a file limit. Use this trick instead:
> 1. Open your phone's browser → **https://github.com/YOUR_USERNAME/natcalc**
> 2. If on PC, drag all files into the upload area
> 3. Alternatively, install **GitHub Desktop** → drag folder → publish

**Commit message:** `Initial commit` → Click **Commit changes**

---

## Step 4 — Watch it build

1. Click the **Actions** tab in your repo
2. You'll see "Build NatCalc APK" running (green spinner)
3. Wait ~5 minutes for it to finish

---

## Step 5 — Download the APK to your phone

### Option A — From GitHub Releases (easiest on phone)
1. After the build succeeds, click the **Releases** section on the right sidebar
2. Find the latest release: **"NatCalc Build #1"**
3. Tap **app-debug.apk** to download it to your phone

### Option B — From Actions Artifacts
1. Click **Actions** → click the completed workflow run
2. Scroll down to **Artifacts** section
3. Download **NatCalc-debug**
4. Unzip it on your phone to get `app-debug.apk`

---

## Step 6 — Install on Android

1. Open **Files** app on your phone → find `app-debug.apk`
2. Tap it
3. If you see **"Install blocked"** → tap **Settings** → enable **"Install unknown apps"** for Files or your browser
4. Tap **Install** → **Open**

---

## Example inputs to try first

```
1 + 1
What is 20% of 500?
Convert 50 USD to EUR
Half of 200
معي 100 دينار كم نصها؟
احسب 20 بالمية من 5000
```

---

## Re-building after changes

Any time you push new code to GitHub, the workflow automatically re-runs and publishes a new Release with the updated APK.

To trigger a manual build without changing code:
**Actions** → **Build NatCalc APK** → **Run workflow** → **Run workflow** ✅
