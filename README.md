# Yesmin AI — Female AI Companion & JARVIS Android Assistant

**Yesmin AI** is a native Android application combining a warm, affectionate, playful female AI girlfriend companion with a powerful JARVIS-style phone automation system, continuous 2-way real-time voice calling, AI coding assistance, and persistent local memory.

---

## 🌟 Key Features

1. **AI Girlfriend & Assistant Personality**:
   - Affectionate, caring, humorous, and emotionally supportive.
   - Fluent in **Hinglish**, **Hindi**, and **English**.
   - Spoken responses using customized sweet female pitch and speed.
   - Transparent AI companion identity with customizable personalities in Settings.

2. **Google Gemini Brain**:
   - Supports `gemini-2.5-flash`, `gemini-2.5-pro`, `gemini-1.5-flash`, and `gemini-1.5-pro`.
   - In-app API key configuration with password masking, save, edit, remove, and live connection test.
   - Automatic injection of user preferences and memories into system prompts.

3. **Real-Time Voice Calling Mode**:
   - Hands-free continuous two-way voice call.
   - Live speech recognition and natural spoken AI replies.
   - **Barge-in / Interrupt**: Tap or speak to interrupt Yesmin and immediately redirect the conversation.
   - Live subtitles transcript, animated frequency visualizer, and call duration counter.
   - Mute microphone control and audio lifecycle management.

4. **JARVIS Phone Automation**:
   - Launch YouTube and search for songs / play music.
   - Set alarms and focus timers using Android Clock intents.
   - Open camera, maps, settings, browser, WhatsApp, and installed apps.
   - Dial phone numbers and draft SMS messages with user confirmation.
   - Strict adherence to Android permission boundaries.

5. **AI Coding & Termux Studio**:
   - Kotlin, Java, Python, JavaScript, HTML/CSS code generator and debugger.
   - Beginner explanations ("Explain like I'm 5").
   - Syntax-highlighted code blocks with 1-tap clipboard copying.
   - Complete step-by-step Termux guide to build APKs directly on an Android device.

6. **Persistent Local Memory (Room SQLite)**:
   - Saves preferences, personal goals, and conversation facts.
   - Memory viewer, add/edit/delete, and clear-all capabilities.
   - Sensitive data flags and zero unauthorized cloud leakage.

---

## 📱 How to Build APK using Termux on Android Phone

You can compile this exact project directly on an Android phone using **Termux** without a PC!

### Step 1: Install Required Packages in Termux
Open Termux (F-Droid version recommended) and run:
```bash
pkg update -y && pkg upgrade -y
pkg install -y openjdk-17 git wget unzip clang
```
Verify Java 17:
```bash
java -version
```

### Step 2: Grant Storage Access and Extract Project ZIP
```bash
termux-setup-storage
mkdir -p ~/projects
cd ~/projects
# Assuming you downloaded the ZIP from AI Studio into your phone's Downloads folder:
unzip ~/storage/downloads/YesminAI.zip -d yesmin_app
cd yesmin_app
chmod +x gradlew
```

### Step 3: Configure Android Command Line Tools & SDK (One-time)
```bash
mkdir -p $HOME/android-sdk/cmdline-tools
cd $HOME/android-sdk/cmdline-tools
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O cmdline-tools.zip
unzip cmdline-tools.zip
mv cmdline-tools latest
export ANDROID_HOME=$HOME/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Accept licenses and install platform & build-tools
yes | sdkmanager --licenses
sdkmanager "platforms;android-34" "build-tools;34.0.0"
```

### Step 4: Build the Debug APK
Navigate back to the project folder and run Gradle:
```bash
cd ~/projects/yesmin_app
./gradlew assembleDebug --no-daemon
```

### Step 5: Locate and Install the APK
```bash
cp app/build/outputs/apk/debug/app-debug.apk ~/storage/downloads/YesminAI-Debug.apk
```
Open your Android phone's **Files / Downloads** app, tap `YesminAI-Debug.apk`, and tap **Install**!

---

## 🛠️ Architecture & Tech Stack
- **Language**: Kotlin 2.2
- **UI Framework**: Jetpack Compose with Material 3 Design
- **Persistence**: Android Jetpack Room SQLite 2.7.0
- **Audio & Speech**: Android `SpeechRecognizer` + Android `TextToSpeech`
- **Network**: OkHttp 4.10 + Google Gemini REST API
- **Design Aesthetic**: Deep cyber background (`#090814`), neon purple, cyber cyan, and subtle magenta.
