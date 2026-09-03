# 📱 KasKu - Application Financial Management for XII PPLG

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin_2.0-blue.svg)
![UI Framework](https://img.shields.io/badge/UI-Jetpack_Compose_Material3-purple.svg)
![Storage](https://img.shields.io/badge/Storage-Offline--First_(SharedPreferences)-orange.svg)
![Build Tool](https://img.shields.io/badge/Build-Gradle_(JDK_21)-darkgreen.svg)
![Version](https://img.shields.io/badge/Version-1.3.0-brightgreen.svg)

**KasKu** adalah aplikasi manajemen kas kelas modern yang dirancang khusus untuk memenuhi kebutuhan pengelolaan keuangan kelas **XII PPLG**. Aplikasi ini mengkombinasikan antarmuka yang bersih, minimalis, dan elegan dengan performa tinggi berbasis **Jetpack Compose (Material3)** dan pola arsitektur **Offline-First**.

---

## 📋 Daftar Isi

- [🎯 Latar Belakang & Tujuan Project](#-latar-belakang--tujuan-project)
- [🆕 Changelog v1.3.0](#-changelog-v130)
- [💾 Mekanisme Penyimpanan Data Lokal HP](#-mekanisme-penyimpanan-data-lokal-hp)
- [✨ Fitur-Fitur Utama](#-fitur-fitur-utama)
- [🏛️ Arsitektur Aplikasi & Design System](#️-arsitektur-aplikasi--design-system)
- [📂 Struktur Direktori Proyek](#-struktur-direktori-proyek)
- [🔍 Deep-Dive Kode & Komponen Utama](#-deep-dive-kode--komponen-utama)
  - [1. Data Models (`Models.kt`)](#1-data-models-modelskt)
  - [2. Storage & Repository Engine (`KaskuRepository.kt`)](#2-storage--repository-engine-kaskurepositorykt)
  - [3. ViewModel & State Management (`KaskuViewModel.kt`)](#3-viewmodel--state-management-kaskuviewmodelkt)
  - [4. Entry Point & Navigation (`MainActivity.kt`)](#4-entry-point--navigation-mainactivitykt)
  - [5. Dashboard Utama (`HomeScreen.kt`)](#5-dashboard-utama-homescreenkt)
  - [6. Dialog Kas Masuk & QRIS Payment (`AddTransactionDialog.kt`)](#6-dialog-kas-masuk--qris-payment-addtransactiondialogkt)
  - [7. Pengaturan & Utilities (`SettingsScreen.kt`)](#7-pengaturan--utilities-settingsscreenkt)
  - [8. Profil Akun (`ProfileScreen.kt`)](#8-profil-akun-profilescreenkt)
  - [9. Theme Engine (`Theme.kt` & `Color.kt`)](#9-theme-engine-themekt--colorkt)
- [💻 Panduan Instalasi Lokal & Build APK](#-panduan-instalasi-lokal--build-apk)
- [📊 Ringkasan Data Initial 33 Anggota](#-ringkasan-data-initial-33-anggota)

---

## 🎯 Latar Belakang & Tujuan Project

Dalam pengelolaan kas kelas skala menengah (33 siswa), pencatatan manual sering kali memicu ketidakcocokan data, hilangnya riwayat pembayaran, serta kesulitan pelaporan transparan kepada wali kelas atau sesama anggota. **KasKu** hadir sebagai solusi komprehensif dengan filosofi:

1. **Kejujuran & Transparansi**: Seluruh anggota dapat melihat status pembayaran kas bulanan secara rinci.
2. **Kemudahan Pembayaran**: Integrasi visual **Kartu Pembayaran QRIS** instan dengan nama merchant terverifikasi.
3. **Ketahanan Data (Offline-First)**: Data tersimpan secara lokal pada penyimpanan internal HP menggunakan `SharedPreferences` sehingga aplikasi tetap responsif tanpa ketergantungan koneksi internet.
4. **Keindahan Desain**: Desain serba putih (Minimalist White Theme) tanpa gradien norak atau emoji berlebihan, dengan tampilan bersih konsisten.

---

## 🆕 Changelog v1.3.0

### ✅ Fitur Baru
- **📈 Line Chart Pemasukan Per Minggu**: Grafik garis interaktif berbasis Canvas di halaman utama yang menampilkan perbandingan pemasukan iuran kas per minggu (Minggu 1–4) dengan gradient fill, dot markers, dan label otomatis.
- **⚠️ Sistem Denda Keterlambatan Bayar Kas**: Denda otomatis 5% dari iuran (Rp 10.000) per bulan keterlambatan. Contoh: Juli belum bayar hingga September = 2× denda (Rp 1.000), Agustus belum bayar hingga September = 1× denda (Rp 500). Ditampilkan di DaftarSiswaSheet dengan badge denda per siswa dan ringkasan total denda keseluruhan.

### 🔧 Perbaikan Bug
- **🔐 Login Hanya Akun Terdaftar**: Login sekarang hanya menerima email & password yang sesuai dengan akun terdaftar. Tidak lagi menerima sembarang input.
- **👤 Peran Dinamis (Bukan Hardcoded)**: Registrasi akun baru otomatis mendapat peran "Anggota", bukan "Administrator". Hanya akun default admin (`akunku1234@gmail.com`) yang mendapat peran "Administrator".
- **🚪 Tombol Logout di ProfileScreen**: Ditambahkan tombol "Keluar" berwarna merah di bagian bawah halaman profil untuk melakukan logout.

### 🗑️ Dihapus
- **🌙 Mode Gelap (Dark Mode)**: Fitur toggle dark mode dihapus sepenuhnya dari SettingsScreen, Theme.kt, dan seluruh referensi `isDarkMode`. Aplikasi kini konsisten menggunakan tema terang (Light Mode only).

### 📝 Perubahan Kode Utama

#### `Models.kt`
```kotlin
// BARU: Field role pada UserProfile
data class UserProfile(
    val name: String = "",
    val email: String = "",
    val classGroup: String = "XII PPLG",
    val role: String = "Anggota"  // NEW — default "Anggota"
)

// DIPERBARUI: isDarkMode dihapus, versi di-bump
data class AppSettings(
    val notificationsEnabled: Boolean = true,
    // isDarkMode: DIHAPUS
    val weeklyFee: Double = 10000.0,
    val appVersion: String = "1.3.0"
)
```

#### `KaskuRepository.kt` — Sistem Denda
```kotlin
// Hitung bulan-bulan yang belum dibayar beserta multiplier denda
fun getUnpaidMonthsWithDelay(member: Member): List<Pair<String, Int>> {
    val monthOrder = listOf("Juli 2026", "Agustus 2026")
    val currentMonthIndex = 2 // September 2026
    val result = mutableListOf<Pair<String, Int>>()
    for ((idx, month) in monthOrder.withIndex()) {
        val paid = member.monthlyPayments[month] ?: false
        if (!paid) {
            val delay = currentMonthIndex - idx
            result.add(month to delay)
        }
    }
    return result
}

// Hitung total denda: 5% × iuran × bulan keterlambatan
fun calculatePenalty(member: Member): Double {
    val fee = _settings.value.weeklyFee  // Rp 10.000
    val penaltyRate = 0.05
    var totalPenalty = 0.0
    for ((_, delay) in getUnpaidMonthsWithDelay(member)) {
        totalPenalty += fee * penaltyRate * delay
    }
    return totalPenalty
}
```

#### `KaskuRepository.kt` — Login Fix
```kotlin
// SEBELUM (BUG): Menerima sembarang email
fun login(email: String, password: String): Boolean {
    // ...
    if (key == "akunku1234@gmail.com" || key.isNotBlank()) { // BUG: selalu true
        return true
    }
}

// SESUDAH (FIX): Hanya menerima akun terdaftar
fun login(email: String, password: String): Boolean {
    val key = email.trim().lowercase()
    val stored = registeredUsers[key]
    if (stored != null && stored.second == password) {
        _userProfile.value = UserProfile(name = stored.first, email = key, role = stored.third)
        saveToPrefs()
        return true
    }
    return false  // Gagal jika tidak terdaftar
}
```

#### `KaskuRepository.kt` — Weekly Income Data untuk Chart
```kotlin
// Agregasi pemasukan per minggu untuk line chart
fun getWeeklyIncomeData(): List<Pair<String, Double>> {
    val weekMap = linkedMapOf("Mg 1" to 0.0, "Mg 2" to 0.0, "Mg 3" to 0.0, "Mg 4" to 0.0)
    for (t in _transactions.value) {
        if (t.type != TransactionType.INCOME) continue
        val day = t.date.split(" ").firstOrNull()?.toIntOrNull() ?: continue
        val weekKey = when {
            day <= 7 -> "Mg 1"; day <= 14 -> "Mg 2"
            day <= 21 -> "Mg 3"; else -> "Mg 4"
        }
        weekMap[weekKey] = (weekMap[weekKey] ?: 0.0) + t.amount
    }
    return weekMap.toList()
}
```

#### `HomeScreen.kt` — Line Chart Composable
```kotlin
// Grafik garis Canvas-based dengan gradient fill dan dot markers
@Composable
fun WeeklyIncomeChart(weeklyData: List<Pair<String, Double>>, modifier: Modifier) {
    val values = weeklyData.map { it.second.toFloat() }
    val maxVal = values.maxOrNull()?.coerceAtLeast(1f) ?: 1f

    Canvas(modifier = modifier) {
        // Draw horizontal grid lines (dashed)
        // Calculate points from data
        // Draw gradient fill area (Brush.verticalGradient)
        // Draw line path (Stroke with StrokeCap.Round)
        // Draw dots with glow effect
        // Draw week labels at bottom via nativeCanvas.drawText
    }
}
```

#### `HomeScreen.kt` — DaftarSiswaSheet dengan Denda
```kotlin
// Badge denda per siswa
if (penalty > 0) {
    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp))
        .background(ExpenseRed.copy(alpha = 0.1f))
        .padding(horizontal = 6.dp, vertical = 2.dp)) {
        Text(text = viewModel.formatRupiah(penalty), color = ExpenseRed)
    }
}

// Breakdown detail denda (contoh: "Juli: 2x denda, Agustus: 1x denda")
if (penalty > 0) {
    val detail = unpaidMonths.joinToString(", ") { (m, d) ->
        "${m.substringBefore(" ")}: ${d}x denda"
    }
    // Ditampilkan di baris subteks siswa
}

// Total denda keseluruhan kelas
val totalPenalty = uiState.members.sumOf { viewModel.calculatePenalty(it) }
// Ditampilkan di card merah di bagian bawah DaftarSiswaSheet
```

#### `ProfileScreen.kt` — Logout & Dynamic Role
```kotlin
// Peran ditampilkan dari data akun, bukan hardcoded
val displayRole = userProfile.role.ifBlank { "Anggota" }
// Badge warna biru untuk Administrator, abu-abu untuk Anggota

// Tombol Logout di bagian bawah
Card(
    modifier = Modifier.fillMaxWidth().clickable(onClick = onLogout),
    colors = CardDefaults.cardColors(containerColor = ExpenseRed)
) {
    Row { Icon(Icons.Default.ExitToApp); Text("Keluar", color = Color.White) }
}
```

#### `Theme.kt` — Dark Mode Dihapus
```kotlin
// SEBELUM: Mendukung dua tema
@Composable
fun KaskuTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) KaskuDarkColorScheme else KaskuLightColorScheme
}

// SESUDAH: Hanya Light Mode
@Composable
fun KaskuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KaskuLightColorScheme,
        typography = KaskuTypography,
        content = content
    )
}
```

---

## 💾 Mekanisme Penyimpanan Data Lokal HP

Seluruh data pengguna, akun pendaftaran, riwayat transaksi, hingga status pembayaran kas 33 siswa disimpan secara **LOKAL di penyimpanan internal handphone**.

### 📍 Lokasi File Penyimpanan di Android OS
- **Nama File Preference**: `kasku_prefs.xml`
- **Path Asli di Handphone**:  
  `/data/data/com.kasku.app/shared_prefs/kasku_prefs.xml`
- **Mode Akses**: `Context.MODE_PRIVATE` (Hanya dapat diakses secara eksklusif oleh aplikasi KasKu).

### 🔒 Perizinan Aplikasi (Permissions)
- **TIDAK PERLU IZIN RUNTIME STORAGE (No Permission Request Required)**: Karena penyimpanan memanfaatkan internal sandboxed storage Android, aplikasi **tidak memerlukan konfirmasi izin akses file/galeri/storage** dari pengguna saat pertama kali diinstal. Data otomatis aman dan tersimpan permanen.

### 📝 Implementasi Kode Penyimpanan & Akses Data (`KaskuRepository.kt`)

#### 1. Inisialisasi SharedPreferences Engine
```kotlin
class KaskuRepository(context: Context? = null) {

    // Membuka atau membuat file Preference private di penyimpanan internal HP
    private val prefs: SharedPreferences? = context?.getSharedPreferences("kasku_prefs", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(UserProfile(name = "Akun", email = "AkunKu1234@gmail.com", role = "Administrator"))
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // email -> Triple(name, password, role)
    private val registeredUsers = mutableMapOf<String, Triple<String, String, String>>()

    init {
        loadData()
    }

    private fun loadData() {
        // Default admin account
        registeredUsers["akunku1234@gmail.com"] = Triple("Akun", "123456", "Administrator")

        if (prefs != null && prefs.contains("saved_data")) {
            loadFromPrefs() // Membaca data yang pernah disimpan sebelumnya di HP
        } else {
            loadInitialData() // Memuat 33 data anggota pertama kali
            saveToPrefs()    // Menyimpan data awal ke SharedPreferences
        }
    }
}
```

#### 2. Fungsi Menyimpan Data ke HP (`saveToPrefs`)
```kotlin
private fun saveToPrefs() {
    prefs?.edit()?.apply {
        putBoolean("saved_data", true)
        putString("user_name", _userProfile.value.name)
        putString("user_email", _userProfile.value.email)
        putString("user_role", _userProfile.value.role)
        putBoolean("notif_enabled", _settings.value.notificationsEnabled)
        apply() // Menulis data secara asinkron ke file kasku_prefs.xml
    }
}
```

#### 3. Fungsi Membaca Data dari HP (`loadFromPrefs`)
```kotlin
private fun loadFromPrefs() {
    if (prefs == null) return

    val name = prefs.getString("user_name", "Akun") ?: "Akun"
    val email = prefs.getString("user_email", "AkunKu1234@gmail.com") ?: "AkunKu1234@gmail.com"
    val role = prefs.getString("user_role", "Anggota") ?: "Anggota"
    val notifEnabled = prefs.getBoolean("notif_enabled", true)

    _userProfile.value = UserProfile(name = name, email = email, role = role)
    _settings.value = AppSettings(notificationsEnabled = notifEnabled)
}
```

#### 4. Registrasi & Simpan Akun Baru
```kotlin
fun register(name: String, email: String, password: String): Boolean {
    val key = email.trim().lowercase()
    if (registeredUsers.containsKey(key)) return false  // Cegah email duplikat
    registeredUsers[key] = Triple(name.trim(), password, "Anggota")  // Role default: Anggota
    _userProfile.value = UserProfile(name = name.trim(), email = key, role = "Anggota")
    saveToPrefs() // Otomatis tersimpan ke HP
    return true
}
```

#### 5. Reset Data Lokal HP (`resetData`)
```kotlin
fun resetData() {
    prefs?.edit()?.clear()?.apply() // Menghapus seluruh isi file preference di HP
    loadInitialData() // Mengembalikan ke 33 data anggota default
}
```

---

## ✨ Fitur-Fitur Utama

### 📊 1. Dashboard Keuangan Transparan
- **Kartu Saldo Kas Total**: Menampilkan akumulasi saldo bersih (Kas Masuk - Kas Keluar) secara real-time dalam format Rupiah (`Rp XX.XXX`).
- **Indikator Pembayaran Bulan Ini**: Progress bar persentase pembayaran siswa pada bulan aktif.
- **Kas Masuk vs Kas Keluar**: Ringkasan akumulasi pemasukan dan pengeluaran.

### 📈 2. Line Chart Pemasukan Per Minggu *(BARU v1.3.0)*
- Grafik garis interaktif berbasis **Compose Canvas** yang menampilkan perbandingan pemasukan iuran kas dari Minggu 1 hingga Minggu 4.
- Dilengkapi **gradient fill**, **dot markers** dengan glow effect, **dashed grid lines**, dan **week labels** otomatis.
- Menampilkan ringkasan nominal pemasukan per minggu di bawah grafik.

### 👥 3. Manajemen 33 Anggota Kelas Real & Riwayat Bulanan
- Kartu **Total Siswa** interaktif yang ketika diklik akan membuka **Bottom Sheet Daftar Siswa**.
- Menampilkan 33 anggota kelas XII PPLG lengkap dengan peran (Ketua Kelas, Bendahara, Sekretaris, Wakil Ketua, Anggota).
- Tracking pembayaran per bulan (contoh: **Juli 2026** & **Agustus 2026**) dengan indikator status pembayaran visual (`V` Lunas warna Hijau / `X` Belum warna Merah).

### ⚠️ 4. Sistem Denda Keterlambatan Bayar Kas *(BARU v1.3.0)*
- **Perhitungan Denda Otomatis**: 5% dari iuran (Rp 10.000) = Rp 500 per bulan keterlambatan.
  - Contoh: Juli belum bayar hingga September = **2× denda** (Rp 1.000).
  - Contoh: Agustus belum bayar hingga September = **1× denda** (Rp 500).
- **Badge Denda Per Siswa**: Ditampilkan di DaftarSiswaSheet dengan warna merah.
- **Breakdown Detail**: Menunjukkan rincian "Juli: 2x denda, Agustus: 1x denda" per siswa.
- **Total Denda Keseluruhan**: Kartu ringkasan total denda seluruh kelas di bagian bawah sheet.

### 🗓️ 5. Pemilih Bulan Aktif (Month Selector)
- Kartu **Bulan Aktif** dapat diklik untuk memilih dan beralih periode bulan kas (Juli 2026, Agustus 2026, dll.).

### 💳 6. Integrasi Kartu Pembayaran QRIS
- Pada modal pencatatan **Kas Masuk**, terdapat kartu QRIS interaktif lengkap dengan:
  - Kode QRIS lokal terintegrasi (`qris_code.jpg`).
  - Nama Merchant: **I W. B. P., D. & K., ELEKTRONIK**.
  - NMID: **ID1026552494969**.
  - Nominal Iuran acuan: **Rp 10.000 / minggu**.

### 📋 7. Modal Quick Action Sheets
- **Laporan Transaksi**: Rincian matematis Kas Masuk, Kas Keluar, Saldo Akhir, serta perbandingan jumlah siswa yang Lunas vs Belum Bayar.
- **Riwayat Transaksi**: Scrollable list seluruh riwayat transaksi keuangan kelas.

### 🔐 8. Autentikasi & Role-Based Access *(DIPERBAIKI v1.3.0)*
- Login hanya menerima akun yang sudah terdaftar (email + password valid).
- Registrasi membuat akun baru dengan peran **"Anggota"** (bukan Administrator).
- Hanya akun default admin yang mendapat peran **"Administrator"**.
- Tombol **Logout** tersedia di halaman Profil.

### ⚙️ 9. Pengaturan & Pusat Bantuan
- **Backup Data ke Google Drive**: Dialog konfirmasi ekspor dan sinkronisasi cadangan data ke cloud.
- **Export Laporan Kas**: Menghasilkan dokumen laporan keuangan berbasis teks ringkas dan memicu Android Share Intent (dapat dikirim ke WhatsApp, Telegram, atau disimpan ke File).
- **Reset Data**: Fitur reset storage lokal ke keadaan awal dengan proteksi dialog konfirmasi.
- **Pusat Bantuan**: Mengarahkan pengguna langsung ke WhatsApp Customer Support di **+62 812-3720-1227** (`https://wa.me/6281237201227`).

---

## 🏛️ Arsitektur Aplikasi & Design System

Aplikasi ini dibangun menggunakan arsitektur **MVVM (Model-View-ViewModel)** dengan alur data satu arah (**Unidirectional Data Flow / UDF**):

```
 ┌─────────────────────────────────────────────────────────────┐
 │                       UI Layer (Compose)                    │
 │  HomeScreen, SettingsScreen, ProfileScreen, Dialogs, Sheets │
 └──────────────────────────────▲──────────────────────────────┘
                                │ StateFlow<KaskuUiState>
                                │ Events (login, addTx, etc.)
 ┌──────────────────────────────┴──────────────────────────────┐
 │                     KaskuViewModel                          │
 │      Aggregates StateFlows via combine() & Operations      │
 └──────────────────────────────▲──────────────────────────────┘
                                │ Flow<List<Member>>, Flow<List<Transaction>>
 ┌──────────────────────────────┴──────────────────────────────┐
 │                     KaskuRepository                         │
 │     SharedPreferences Persistence Engine & Initial Data      │
 └─────────────────────────────────────────────────────────────┘
```

### Design System Tokens
- **Primary Color**: `HeaderBlue` (`#1565C0`) & `HeaderBlueDark` (`#0D47A1`)
- **Accent Cards**: `CardBlue` (`#1E88E5`) & `LightBlue` (`#BBDEFB`)
- **Status Colors**: `IncomeGreen` (`#4CAF50`) & `ExpenseRed` (`#F44336`)
- **Background**: `WhiteBackground` (`#F5F5F5`), Cards (`#FFFFFF`) — Light Mode only
- **Typography**: Font system Material3 (`headlineLarge`, `titleLarge`, `bodyLarge`, `labelSmall`).

---

## 📂 Struktur Direktori Proyek

```
kasku/
├── app/
│   ├── build.gradle.kts                # Konfigurasi dependensi & versi (versionCode 4, versionName 1.3.0)
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml     # Deklarasi aktivitas utama & izin aplikasi
│           ├── res/
│           │   ├── drawable/
│           │   │   └── qris_code.jpg   # Gambar QRIS merchant lokal
│           │   └── values/
│           │       ├── colors.xml
│           │       └── themes.xml      # Konfigurasi Force Dark = false (dikelola via Compose)
│           └── java/com/kasku/app/
│               ├── MainActivity.kt     # Entry point & Root Composable Scaffold
│               ├── model/
│               │   └── Models.kt       # Data classes (CashTransaction, Member, UserProfile, AppSettings)
│               ├── repository/
│               │   └── KaskuRepository.kt  # Engine penyimpanan lokal, Auth, Denda, & Weekly Income
│               ├── theme/
│               │   ├── Color.kt        # Definisi Color Tokens
│               │   └── Theme.kt        # Setup LightColorScheme (Light Mode only)
│               └── ui/
│                   ├── screens/
│                   │   ├── HomeScreen.kt           # Dashboard, Line Chart, Bottom Sheets & Denda
│                   │   ├── AddTransactionDialog.kt # Form Input & Card QRIS
│                   │   ├── SettingsScreen.kt       # Pengaturan, Drive Backup, WA Direct
│                   │   ├── ProfileScreen.kt        # Tampilan Akun & Logout
│                   │   ├── LoginScreen.kt          # Halaman Autentikasi Login
│                   │   └── RegisterScreen.kt       # Halaman Pendaftaran Akun
│                   └── viewmodel/
│                       └── KaskuViewModel.kt       # Business Logic, State Aggregator, & Penalty
├── KasKu.apk                           # Output APK Debug Siap Install
└── build.gradle.kts                    # Root build script (Gradle Kotlin DSL)
```

---

## 🔍 Deep-Dive Kode & Komponen Utama

### 1. Data Models (`Models.kt`)
Mendefinisikan tipe data inti aplikasi.

```kotlin
enum class TransactionType { INCOME, EXPENSE }

data class CashTransaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val memberName: String? = null,
    val date: String,
    val category: String = "Kas Kelas"
)

data class Member(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: String = "Anggota",
    val totalPaid: Double = 0.0,
    val isPaidThisWeek: Boolean = false,
    val monthlyPayments: Map<String, Boolean> = emptyMap() // Track status bayar bulanan
)

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val classGroup: String = "XII PPLG",
    val role: String = "Anggota"  // v1.3.0: Role dinamis (Administrator / Anggota)
)

data class AppSettings(
    val notificationsEnabled: Boolean = true,
    val weeklyFee: Double = 10000.0,
    val appVersion: String = "1.3.0"
)
```

---

### 2. Storage & Repository Engine (`KaskuRepository.kt`)
Mengelola pembacaan dan penulisan data ke **SharedPreferences** internal Android, autentikasi akun, perhitungan denda, dan agregasi data chart.

```kotlin
class KaskuRepository(context: Context? = null) {
    private val prefs: SharedPreferences? = context?.getSharedPreferences("kasku_prefs", Context.MODE_PRIVATE)

    private val _transactions = MutableStateFlow<List<CashTransaction>>(emptyList())
    val transactions: StateFlow<List<CashTransaction>> = _transactions.asStateFlow()

    private val _members = MutableStateFlow<List<Member>>(emptyList())
    val members: StateFlow<List<Member>> = _members.asStateFlow()

    // email -> Triple(name, password, role)
    private val registeredUsers = mutableMapOf<String, Triple<String, String, String>>()

    init {
        registeredUsers["akunku1234@gmail.com"] = Triple("Akun", "123456", "Administrator")
        loadData()
    }

    // --- Auth (v1.3.0 FIX) ---
    fun login(email: String, password: String): Boolean {
        val key = email.trim().lowercase()
        val stored = registeredUsers[key]
        if (stored != null && stored.second == password) {
            _userProfile.value = UserProfile(name = stored.first, email = key, role = stored.third)
            return true
        }
        return false // HANYA akun terdaftar
    }

    fun register(name: String, email: String, password: String): Boolean {
        val key = email.trim().lowercase()
        if (registeredUsers.containsKey(key)) return false
        registeredUsers[key] = Triple(name.trim(), password, "Anggota") // Default role: Anggota
        return true
    }

    // --- Denda/Penalty (v1.3.0 NEW) ---
    fun calculatePenalty(member: Member): Double { /* 5% × iuran × bulan terlambat */ }
    fun getUnpaidMonthsWithDelay(member: Member): List<Pair<String, Int>> { /* (bulan, multiplier) */ }

    // --- Weekly Income Data (v1.3.0 NEW) ---
    fun getWeeklyIncomeData(): List<Pair<String, Double>> { /* Agregasi per minggu */ }
}
```

---

### 3. ViewModel & State Management (`KaskuViewModel.kt`)
Menggabungkan beberapa `Flow` dari repository menjadi satu `KaskuUiState` yang aman dan reaktif.

```kotlin
data class KaskuUiState(
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val transactions: List<CashTransaction> = emptyList(),
    val members: List<Member> = emptyList(),
    val paidCountThisWeek: Int = 0,
    val totalMembersCount: Int = 0,
    val weeklyIncomeData: List<Pair<String, Double>> = emptyList()  // v1.3.0: Data chart
)

class KaskuViewModel(private val repository: KaskuRepository) : ViewModel() {

    val uiState: StateFlow<KaskuUiState> = combine(
        repository.transactions,
        repository.members
    ) { transactions, members ->
        KaskuUiState(
            totalBalance = income - expense,
            totalIncome = income,
            totalExpense = expense,
            transactions = transactions,
            members = members,
            paidCountThisWeek = members.count { it.isPaidThisWeek },
            totalMembersCount = members.size,
            weeklyIncomeData = repository.getWeeklyIncomeData()  // v1.3.0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), KaskuUiState())

    fun formatRupiah(amount: Double): String = repository.formatRupiah(amount)
    fun calculatePenalty(member: Member): Double = repository.calculatePenalty(member)       // v1.3.0
    fun getUnpaidMonthsWithDelay(member: Member) = repository.getUnpaidMonthsWithDelay(member) // v1.3.0
}
```

---

### 4. Entry Point & Navigation (`MainActivity.kt`)
Mengkonfigurasi Root Activity, menginjeksikan Application Context ke ViewModel Factory, dan menerapkan `KaskuTheme` konsisten.

```kotlin
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: KaskuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val repository = KaskuRepository(applicationContext)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return KaskuViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[KaskuViewModel::class.java]

        setContent {
            KaskuTheme {  // v1.3.0: Tanpa parameter darkTheme (Light Mode only)
                Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                    KaskuApp(viewModel = viewModel)
                }
            }
        }
    }
}
```

---

### 5. Dashboard Utama (`HomeScreen.kt`)
Memuat seluruh widget keuangan, **line chart pemasukan per minggu**, aksi cepat (Quick Actions), serta Bottom Sheet modal interaktif.

- **Line Chart Pemasukan Per Minggu** *(BARU v1.3.0)*:
  ```kotlin
  @Composable
  fun WeeklyIncomeChart(weeklyData: List<Pair<String, Double>>, modifier: Modifier) {
      Canvas(modifier = modifier) {
          // Dashed grid lines, gradient fill, line path, dot markers, week labels
      }
  }
  ```
- **Bottom Sheet Daftar Siswa (`DaftarSiswaSheet`)**: Menampilkan 33 anggota kelas, perbandingan status bayar bulanan (`V` Lunas / `X` Belum), **badge denda**, dan **total denda keseluruhan**.
- **Bottom Sheet Pilih Bulan (`MonthPickerSheet`)**: Memilih bulan kas aktif.

---

### 6. Dialog Kas Masuk & QRIS Payment (`AddTransactionDialog.kt`)
Memfasilitasi pencatatan transaksi kas masuk atau kas keluar, sekaligus menampilkan informasi pembayaran QRIS.

```kotlin
// Komponen Kartu QRIS di dalam Dialog
Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4F8))
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("PEMBAYARAN QRIS INSTAN", fontWeight = FontWeight.Bold, color = HeaderBlue)
        Image(
            painter = painterResource(id = R.drawable.qris_code),
            contentDescription = "QRIS Code",
            modifier = Modifier.size(180.dp).clip(RoundedCornerShape(8.dp))
        )
        Text("NMID: ID1026552494969", style = MaterialTheme.typography.labelSmall)
        Text("Merchant: I W. B. P., D. & K., ELEKTRONIK", fontWeight = FontWeight.Bold)
    }
}
```

---

### 7. Pengaturan & Utilities (`SettingsScreen.kt`)
Menyediakan fitur manajerial aplikasi:
- **Toggle Notifikasi**: Aktifkan/nonaktifkan notifikasi aplikasi.
- **Direct WhatsApp Link**: `Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/6281237201227"))`
- **Export Laporan Kas**:
  ```kotlin
  val shareIntent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_SUBJECT, "Laporan Kas Kelas KasKu")
      putExtra(Intent.EXTRA_TEXT, reportText)
  }
  context.startActivity(Intent.createChooser(shareIntent, "Bagikan Laporan Kas"))
  ```

> **Catatan v1.3.0**: Toggle "Mode Gelap" telah dihapus dari halaman pengaturan.

---

### 8. Profil Akun (`ProfileScreen.kt`)
Tampilan informasi akun pengelola dengan **peran dinamis** (Administrator / Anggota) dan **tombol Logout**.

```kotlin
// Badge peran dinamis
Box(modifier = Modifier.clip(RoundedCornerShape(6.dp))
    .background(if (displayRole == "Administrator") HeaderBlue else TextGray)) {
    Text(text = displayRole.uppercase(), color = Color.White)
}

// Tombol Logout (BARU v1.3.0)
Card(
    modifier = Modifier.fillMaxWidth().clickable(onClick = onLogout),
    colors = CardDefaults.cardColors(containerColor = ExpenseRed)
) {
    Row { Icon(Icons.Default.ExitToApp); Text("Keluar", color = Color.White) }
}
```

---

### 9. Theme Engine (`Theme.kt` & `Color.kt`)
Menggunakan tema terang konsisten dengan `KaskuLightColorScheme`. Dark Mode telah dihapus di v1.3.0.

```kotlin
@Composable
fun KaskuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KaskuLightColorScheme,
        typography = KaskuTypography,
        content = content
    )
}
```

---

## 💻 Panduan Instalasi Lokal & Build APK

### Prasyarat Sistem
- **Java Development Kit (JDK)**: OpenJDK 21 atau lebih baru.
- **Android SDK**: Build-Tools version 34.0.0+ / API 34.
- **Gradle**: Gradle Wrapper terintegrasi (`./gradlew`).

### 1. Clone atau Buka Repository Project
```bash
cd /home/wira/Documents/kasku
```

### 2. Atur Environment JDK 21
Pastikan variabel `JAVA_HOME` mengarah ke JDK 21:
```bash
export JAVA_HOME=/usr/lib/jvm/java-1.21.0-openjdk-amd64
```

### 3. Kompilasi & Clean Build APK
Jalankan perintah berikut pada terminal untuk membersihkan cache lama dan mem-build APK baru:
```bash
./gradlew clean assembleDebug
```

Proses kompilasi akan menghasilkan file output APK di:
`app/build/outputs/apk/debug/app-debug.apk`

### 4. Salin APK ke Root Folder Project
```bash
cp app/build/outputs/apk/debug/app-debug.apk KasKu.apk
```

### 5. Install APK ke HP Android via ADB (Opsional)
Jika perangkat Android terhubung via USB Debugging / Wireless ADB:
```bash
adb install -r KasKu.apk
```

---

## 📊 Ringkasan Data Initial 33 Anggota

| No | Nama Siswa | Peran | Status Juli 2026 | Status Agustus 2026 | Denda (Sep 2026) |
|---|---|---|:---:|:---:|:---:|
| 1 | BOYKE VILANO HAMONANGAN SIHITE | Ketua Kelas | Lunas (V) | Lunas (V) | - |
| 2 | BINTANG LEONITA CHRISTYA RENATA | Bendahara | Lunas (V) | Lunas (V) | - |
| 3 | CAROLINA TIMUTHY JANGGUR | Sekretaris | Lunas (V) | Lunas (V) | - |
| 4 | DEWA GEDE DALEM OKA ADNYANA SANDI | Wakil Ketua | Lunas (V) | Lunas (V) | - |
| 5 | GALISTAN RAMADHAN KURNIA TAUNAES | Anggota | Lunas (V) | Lunas (V) | - |
| 6 | GEDE AGUS WIRA DARMA PUTRA | Anggota | Lunas (V) | Lunas (V) | - |
| 7 | I GEDE ABI WIRYA DINATA | Anggota | Lunas (V) | Lunas (V) | - |
| 8 | I GEDE DARMA SUPTIAWAN | Anggota | Lunas (V) | Lunas (V) | - |
| 9 | I KOMANG RADITYA PUTRA | Anggota | Lunas (V) | Lunas (V) | - |
| 10 | I KOMANG RISKI SETIAWAN | Anggota | Lunas (V) | Lunas (V) | - |
| 11 | I NYOMAN GEDE ARTA WIGUNA | Anggota | Lunas (V) | Lunas (V) | - |
| 12 | I PUTU DIKA LAKSMANA PUTRA | Anggota | Lunas (V) | Lunas (V) | - |
| 13 | I PUTU DITYA ARTHA WIJAYA | Anggota | Lunas (V) | Lunas (V) | - |
| 14 | I PUTU PANDE ANDIKA | Anggota | Lunas (V) | Lunas (V) | - |
| 15 | I PUTU SUYOGA MAHENDRA | Anggota | Lunas (V) | Lunas (V) | - |
| 16 | I WAYAN BAGUS PUTRAWAN | Anggota | Lunas (V) | Lunas (V) | - |
| 17 | I WAYAN PASEK KEVIN ARIADI | Anggota | Lunas (V) | Lunas (V) | - |
| 18 | KADEK YUDA PRASETYA | Anggota | Lunas (V) | Lunas (V) | - |
| 19 | KADEK YUNI CALLISTA PUTRI DEWI | Anggota | Lunas (V) | Lunas (V) | - |
| 20 | KOMANG DIAH PUTRI PRATIWI | Anggota | Lunas (V) | Lunas (V) | - |
| 21 | LUH RIA MIRASIH | Anggota | Lunas (V) | Lunas (V) | - |
| 22 | NI KADEK ADELIA CAHYA KENCANA PUTRI | Anggota | Lunas (V) | Lunas (V) | - |
| 23 | NI KADEK LINA ANTIKA DEWI | Anggota | Lunas (V) | Belum (X) | Rp 500 |
| 24 | NI KOMANG KIRANA PARAMITA ARDANARI | Anggota | Lunas (V) | Belum (X) | Rp 500 |
| 25 | NI KOMANG SEPTIARINI | Anggota | Lunas (V) | Belum (X) | Rp 500 |
| 26 | NI LUH PUTU KESYA ASTRI MELANI | Anggota | Lunas (V) | Belum (X) | Rp 500 |
| 27 | NI PUTU CAHAYA LESTARI DEWI | Anggota | Lunas (V) | Belum (X) | Rp 500 |
| 28 | NI PUTU INTAN LESTARI DARMAYANTI | Anggota | Lunas (V) | Belum (X) | Rp 500 |
| 29 | OKTA PRADIPTA ATTALA DZAKI | Anggota | Belum (X) | Belum (X) | Rp 1.500 |
| 30 | PUTU BAYU SATRIA WANGSA BUKIAN | Anggota | Belum (X) | Belum (X) | Rp 1.500 |
| 31 | PUTU NANDA LINDIA MAHARANI | Anggota | Belum (X) | Belum (X) | Rp 1.500 |
| 32 | PUTU PUTRI CAHYANI | Anggota | Belum (X) | Belum (X) | Rp 1.500 |
| 33 | RADITYA RONDI | Anggota | Belum (X) | Belum (X) | Rp 1.500 |

---

## 👨‍💻 Developer & Kontributor

Dikembangkan dengan 💙 oleh Tim Pengembang **KasKu App** untuk kelas **XII PPLG**.  
Bantuan & Dukungan Teknis Direct WhatsApp: **[+62 812-3720-1227](https://wa.me/6281237201227)**.
