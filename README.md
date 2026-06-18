# Nuts - Aplikasi Manajemen & Klasifikasi Kacang

Aplikasi Android berbasis **Jetpack Compose** yang dirancang untuk mengelola dan mengklasifikasikan data gambar berbagai jenis kacang (Almond, Chestnut, Hazelnut). Proyek ini merupakan bagian dari Tugas Akhir.

## 🚀 Fitur Utama
- **Autentikasi User**: Login dan Logout yang terintegrasi dengan Supabase.
- **Manajemen Gambar**: Menghitung secara otomatis jumlah gambar kacang yang tersimpan di memori perangkat berdasarkan folder user.
- **Status Premium & Admin**: Manajemen hak akses user (Premium/Admin) yang tersimpan secara lokal dan sinkron dengan server.
- **Sinkronisasi Realtime**: Menggunakan database realtime untuk pembaruan data secara instan.

## 🛠️ Tech Stack
- **Bahasa**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Arsitektur**: MVVM (Model-View-ViewModel)
- **Local Database**: Room Persistence
- **Backend/API**: Supabase (Postgrest & Realtime)
- **Dependency Injection**: Manual DI dengan `ViewModelFactory`
- **Asynchronous**: Kotlin Coroutines & StateFlow

## 🏗️ Penjelasan Arsitektur

### 1. ViewModel & Lifecycle
Aplikasi ini menggunakan **ViewModel** untuk memisahkan logika bisnis dari tampilan (UI). ViewModel memastikan data tetap aman saat terjadi perubahan konfigurasi (seperti rotasi layar). Penggunaan `viewModelScope` memastikan semua proses latar belakang berhenti secara otomatis mengikuti siklus hidup (lifecycle) ViewModel untuk menghindari kebocoran memori.

### 2. State Management
Pengelolaan status aplikasi menggunakan **StateFlow**. Dengan pola ini, UI akan otomatis memperbarui tampilannya segera setelah data di dalam ViewModel berubah. Contohnya pada penghitungan jumlah gambar kacang yang diperbarui secara reaktif.

### 3. Dependency Injection (DI)
Proyek ini menerapkan **Manual Dependency Injection**. Objek seperti `AuthRepository` dan `Classifier` disuntikkan ke dalam ViewModel melalui `ViewModelFactory`. Hal ini membuat kode lebih modular, mudah diuji (testable), dan efisien dalam penggunaan memori (singleton pattern).

### 4. Data Entity
`UserEntity` digunakan sebagai model data tunggal yang berfungsi ganda:
- Sebagai **Tabel Database** (Room) untuk penyimpanan offline.
- Sebagai **Data Mapping** (Serialization) untuk komunikasi dengan server Supabase.

## 📁 Struktur Folder Utama
- `screens/`: Berisi UI Compose dan ViewModel untuk setiap halaman (Home, History, dll).
- `data/`: Berisi Repository dan Entity (Sumber data).
- `ui/theme/`: Pengaturan tema warna dan tipografi Material 3.

---
*Proyek ini dikembangkan sebagai syarat Tugas Akhir.*
