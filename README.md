# True Presence

**True Presence** is a smart and secure attendance management system that combines **Geofencing** and **Facial Recognition** to ensure accurate, tamper-proof, and location-bound attendance verification. Designed for modern enterprises, educational institutions, and hybrid work environments, True Presence provides a dual-authentication mechanism to verify both the identity and location of the user before recording attendance.

## 📌 Features

- ✅ **Geofencing Authentication**: Attendance can only be marked within a defined GPS boundary.
- 🧠 **AI-Based Facial Recognition**: Verifies identity using deep learning models for accurate face matching.
- 🔐 **Secure Data Storage**: Encrypted attendance records to ensure data privacy and integrity.
- 📊 **Real-Time Reports**: Admin dashboard to view and analyze attendance statistics.
- 🌐 **Multi-Platform Support**: Compatible with Android-based mobile devices and scalable for web dashboards.
- 🏢 **Flexible for Multiple Sectors**: Suitable for offices, universities, hospitals, construction sites, and remote workers.

## 🛠️ Technologies Used

- **Frontend**: Java (Android)
- **Backend**: Firebase
- **Database**: Firebase Firestore 
- **Facial Recognition**: ML Kit 
- **Geofencing**: Android Location Services (FusedLocationProviderClient), Open Street Map

## 🔍 How It Works

1. **Geofence Setup**: Admin defines the geographic boundary where attendance is allowed.
2. **User Check-In**: App checks if the user is within the geofence.
3. **Face Scan**: Facial recognition verifies the user's identity.
4. **Attendance Recorded**: Upon successful validation, attendance is logged and stored securely.
5. **Admin Panel**: Provides analytics and reporting tools to monitor attendance.

## 📱 Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/true-presence.git
   cd true-presence
