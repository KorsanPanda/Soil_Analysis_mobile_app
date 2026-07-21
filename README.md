# 🌾 Soil Analysis & Agricultural Field Tracking Mobile App

![Platform](https://img.shields.io/badge/Platform-Android-green.svg)
![Language](https://img.shields.io/badge/Language-Java-orange.svg)
![Backend](https://img.shields.io/badge/Backend-Firebase_Firestore_%26_Auth-red.svg)
![API](https://img.shields.io/badge/API-Google_Maps_SDK-blue.svg)

An Android application designed to facilitate soil analysis data entry, crop yield estimation, and geographic field mapping for farmers and agricultural engineers (Ziraat Mühendisi).

---

## 📌 Project Overview

This application bridges the gap between field management and laboratory analysis by organizing data into a role-based agricultural workflow:
* **Role-Based Portals:** Distinct execution paths and user permissions for **Farmers** (`Ciftci`) and **Agricultural Engineers** (`Ziraatci`).
* **Geographic Field Mapping:** Interactive satellite visualization using **Google Maps SDK** to mark, view, and inspect land parcels (`Tarla`).
* **Soil Quality & Crop Yield Analytics:** Input soil chemical parameters (pH, organic matter %, phosphorus, potassium) and calculate crop yield predictions (`Mahsul`).
* **Cloud Database Synchronization:** Full integration with **Firebase Authentication** and **Firebase Firestore** for real-time document-based storage.

---

## 🏗️ System Architecture & Workflow

```text
+-------------------------------------------------------------------------------+
|                            Role Selection (MainActivity)                     |
+---------------------------------------++--------------------------------------+
                                        ||
                  +---------------------+---------------------+
                  |                                           |
                  v                                           v
    +---------------------------+               +---------------------------+
    | Farmer Portal             |               | Engineer Portal           |
    | (HaritaActivity)          |               | (LaboratuvarActivity)     |
    +-------------+-------------+               +-------------+-------------+
                  |                                           |
                  v                                           v
    +---------------------------+               +---------------------------+
    | Interactive Satellite Map |               | Soil Analysis Data Entry  |
    |  - Pin Land Parcels       |               |  - pH, Organic Matter     |
    |  - View Parcel Details    |               |  - Phosphorus & Potassium |
    |  - Add New Fields         |               |  - Crop Recommendations   |
    +---------------------------+               +---------------------------+
```

---

## 📱 Key Features & Modules

### 1. User Authentication & Authorization (`GirisActivity`)
* Email/Password authentication powered by Firebase Auth.
* Role-based navigation routing: Engineers are directed to the **Laboratory Portal**, while Farmers access the **Field Map Portal**.

### 2. Interactive Field Mapping (`HaritaActivity` & `YeniTarlaActivity`)
* Rendered in `MAP_TYPE_SATELLITE` mode via Google Maps API.
* Automatically fetches user-owned land parcels and drops dynamic location markers (`MarkerOptions`).
* Supports adding new parcels with auto-generated unique 8-character Parcel IDs (`UUID`).

### 3. Soil Analysis & Crop Yield (`LaboratuvarActivity` & `TarlaDetayActivity`)
* Records chemical soil metrics ($pH$, Organic Matter %, Phosphorus $\text{kg/da}$, Potassium $\text{kg/da}$).
* Links crop recommendation models with estimated yield percentages.

### 4. Consolidated Records Management (`TumKayitlarActivity` & `KayitAdapter`)
* Displays analysis records in a 2-column `GridLayoutManager` `RecyclerView`.
* Performs relational document fetching from Firestore to dynamically resolve land ownership information.

---

## 🚀 Getting Started

### Prerequisites
* **Android Studio** (Jellyfish / Ladybug or newer recommended)
* **JDK 17** or **JDK 21**
* **Android SDK** (API Level 24+)
* A valid **Google Maps API Key**

### Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/KorsanPanda/soil_analysis_mobile_app.git](https://github.com/KorsanPanda/soil_analysis_mobile_app.git)
   cd soil_analysis_mobile_app
   ```

2. **Configure Google Maps API:**
   * Open `src/main/AndroidManifest.xml`.
   * Replace `YourAPI_key` with your valid Google Maps API Key:
     ```xml
     <meta-data
         android:name="com.google.android.geo.API_KEY"
         android:value="YOUR_ACTUAL_GOOGLE_MAPS_API_KEY" />
     ```

3. **Configure Firebase:**
   * Place your `google-services.json` file inside the `app/` root directory.

4. **Build and Run:**
   * Open the project in **Android Studio**.
   * Sync Gradle files and launch on an Android Emulator or physical device.

---

## 📁 Directory Structure

```text
korsanpanda-soil_analysis_mobile_app/
├── src/main/
│   ├── java/com/example/odev/
│   │   ├── model/
│   │   │   ├── Kullanici.java       # User authentication data model
│   │   │   ├── Tarla.java           # Land parcel coordinate model
│   │   │   ├── LabSonucu.java       # Soil chemical analysis model
│   │   │   └── Mahsul.java          # Crop recommendation model
│   │   └── view/
│   │       ├── MainActivity.java    # Role selection entry screen
│   │       ├── GirisActivity.java   # Firebase Auth login/register controller
│   │       ├── HaritaActivity.java  # Google Maps satellite field viewer
│   │       ├── YeniTarlaActivity.java # Field registration activity
│   │       ├── LaboratuvarActivity.java # Soil test data entry screen
│   │       ├── TarlaDetayActivity.java  # Detailed parcel view & deletion logic
│   │       ├── TumKayitlarActivity.java # Grid list of all laboratory records
│   │       └── KayitAdapter.java        # RecyclerView adapter for soil tests
│   ├── res/
│   │   ├── layout/                  # Activity XML layouts
│   │   └── drawable/                # Custom UI vectors & backgrounds
│   └── AndroidManifest.xml          # Application manifest & API keys
├── build.gradle.kts                 # Project Gradle build configuration
└── README.md                        # Project documentation
```
