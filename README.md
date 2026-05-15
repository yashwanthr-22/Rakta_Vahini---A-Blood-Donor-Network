# Rakta-Vahini 

**Rakta-Vahini** is a modern Android application designed to bridge the gap between blood donors and recipients. It facilitates quick access to eligible donors during emergencies, ensuring that life-saving blood is just a search away.

## 🚀 Features

- **User Authentication:** Secure registration and login for donors using Firebase Authentication.
- **Donor Profiles:** Comprehensive donor information including blood group, contact details, and location (Area, District, Pincode).
- **Emergency Search:** Advanced search functionality to find eligible donors of specific blood groups in localized areas.
- **Smart Eligibility:** Automatically tracks donation history and ensures a 90-day waiting period between donations for donor safety.
- **Real-time Updates:** Powered by Cloud Firestore for instant data synchronization across devices.
- **Modern UI:** Built entirely with Jetpack Compose and Material 3 for a fluid and accessible user experience.

## 🛠️ Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Backend:** [Firebase](https://firebase.google.com/)
    - **Authentication:** Email and Phone number based login.
    - **Cloud Firestore:** NoSQL database for donor records.
- **Navigation:** [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Asynchronous Programming:** Coroutines & Flow

## 📋 Prerequisites

- Android Studio Koala or newer.
- Android SDK 26 (Android 8.0) or higher.
- A Firebase project with Authentication and Firestore enabled.

## ⚙️ Setup & Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/Raktaa-Vahini.git
   ```

2. **Firebase Configuration:**
   - Create a new project in the [Firebase Console](https://console.firebase.google.com/).
   - Register the Android app with package name `com.example.raktaa_vahini`.
   - Download the `google-services.json` file and place it in the `app/` directory.
   - Enable **Email/Password** and **Phone** authentication in the Firebase Auth tab.
   - Create a **Cloud Firestore** database and set up appropriate security rules.

3. **Build and Run:**
   - Open the project in Android Studio.
   - Sync Project with Gradle Files.
   - Run the application on an emulator or a physical device.

## 📸 Screenshots

| Login | Search | Profile |
|-------|--------|---------|
| ![Login Screen](https://via.placeholder.com/200x400?text=Login+Screen) | ![Search Screen](https://via.placeholder.com/200x400?text=Search+Screen) | ![Profile Screen](https://via.placeholder.com/200x400?text=Profile+Screen) |

## 🤝 Contributing

Contributions are welcome! If you find a bug or have a feature suggestion, please open an issue or submit a pull request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
