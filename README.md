# Namma-Shasane 🇮🇳  
A smart Android application designed to preserve and promote cultural heritage through location-based storytelling, photo tagging, preservation alerts, and interactive maps.

---

## 📱 Project Overview
Namma-Shasane helps users explore historical places, upload and tag cultural photos, view stories related to heritage locations, and receive preservation alerts.  
The application aims to digitally protect and spread awareness about local heritage and culture.

---

## ✨ Features

- 🗺️ Interactive Google Maps integration
- 📍 Location-based heritage discovery
- 📸 Photo capture and tagging
- 📖 Story viewing for historical places
- 🚨 Preservation alerts and notifications
- 🔥 Firebase Authentication & Database support
- ☁️ Firebase Storage integration
- 🎨 Modern Material UI design
- ⚡ Smooth onboarding and splash screens

---

## 🛠️ Tech Stack

### Frontend
- Java
- XML Layouts
- Android ViewBinding

### Backend & Services
- Firebase Authentication
- Firebase Firestore
- Firebase Storage

### APIs & Libraries
- Google Maps API
- Google Location Services
- Glide (Image Loading)
- Gson (JSON Parsing)
- OkHttp (Networking)

---

## 📂 Project Structure

```bash
NammaShasane/
│
├── app/
│   ├── src/main/
│   │   ├── java/com/nammashasane/
│   │   │   ├── ui/
│   │   │   │   ├── splash/
│   │   │   │   ├── onboarding/
│   │   │   │   ├── home/
│   │   │   │   ├── map/
│   │   │   │   ├── photo/
│   │   │   │   └── alert/
│   │   ├── res/
│   │   └── AndroidManifest.xml
│
├── gradle/
├── build.gradle
└── settings.gradle
```

---

## ⚙️ Requirements

- Android Studio Hedgehog or above
- JDK 17
- Android SDK 34
- Firebase Project
- Google Maps API Key

---

## 🚀 Installation Steps

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/your-username/NammaShasane.git
```

### 2️⃣ Open in Android Studio

- Open Android Studio
- Click **Open Project**
- Select the `NammaShasane` folder

### 3️⃣ Configure Firebase

- Create a Firebase project
- Download `google-services.json`
- Place it inside:

```bash
app/google-services.json
```

### 4️⃣ Add Google Maps API Key

Open:

```xml
AndroidManifest.xml
```

Replace:

```xml
android:value="YOUR_MAPS_API_KEY"
```

with your actual API key.

---

## ▶️ Run the Application

- Connect an Android device or emulator
- Click **Run ▶** in Android Studio

---

## 🔐 Permissions Used

```xml
INTERNET
ACCESS_FINE_LOCATION
ACCESS_COARSE_LOCATION
CAMERA
READ_EXTERNAL_STORAGE
WRITE_EXTERNAL_STORAGE
```

---

## 📸 Screens Included

- Splash Screen
- Onboarding Screen
- Home Dashboard
- Map View
- Photo Tagging Screen
- Story Viewer
- Preservation Alert Screen

---

## 🎯 Future Enhancements

- AI-based heritage recognition
- Multi-language support
- Community contribution system
- Offline map support
- AR-based historical visualization

---

## 👩‍💻 Developed By

**Chithrasri MS**  
Engineering Student – Nagarjuna College of Engineering and Technology  
Internship Project at MindMatrix

---

## 📜 License

This project is developed for educational and internship purposes.
