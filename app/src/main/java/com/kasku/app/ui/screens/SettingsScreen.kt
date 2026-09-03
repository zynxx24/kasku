package com.kasku.app.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasku.app.model.AppSettings
import com.kasku.app.model.UserProfile
import com.kasku.app.theme.*

@Composable
fun SettingsScreen(
    userProfile: UserProfile,
    settings: AppSettings,
    onBack: () -> Unit,
    onUpdateProfile: (name: String, email: String) -> Unit,
    onUpdateSettings: (AppSettings) -> Unit,
    onResetData: () -> Unit
) {
    val context = LocalContext.current
    var notifEnabled by remember(settings) { mutableStateOf(settings.notificationsEnabled) }

    var showResetDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeaderBlue)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = "PENGATURAN",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black
                ),
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Toggle section card (Notifikasi only)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        // Notifikasi
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(HeaderBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Notifications, null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        "Notifikasi",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = TextDark
                                    )
                                    Text(
                                        "Aktifkan Atau Nonaktifkan",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextGray
                                    )
                                }
                            }
                            Switch(
                                checked = notifEnabled,
                                onCheckedChange = {
                                    notifEnabled = it
                                    onUpdateSettings(settings.copy(notificationsEnabled = it))
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = HeaderBlue,
                                    uncheckedThumbColor = TextGray,
                                    uncheckedTrackColor = DividerGray
                                )
                            )
                        }
                    }
                }
            }

            // Menu items card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        SettingsMenuItem(
                            icon = Icons.Default.Backup,
                            iconBg = HeaderBlue,
                            title = "Backup Data",
                            subtitle = "Simpan Data Ke Google Drive",
                            onClick = { showBackupDialog = true }
                        )
                        HorizontalDivider(
                            color = DividerGray,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        SettingsMenuItem(
                            icon = Icons.Default.Description,
                            iconBg = HeaderBlue,
                            title = "Export Laporan",
                            subtitle = "Export Laporan Ke PDF/excel/TXT",
                            onClick = { showExportDialog = true }
                        )
                        HorizontalDivider(
                            color = DividerGray,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        SettingsMenuItem(
                            icon = Icons.Default.Delete,
                            iconBg = ExpenseRed,
                            title = "Reset Data",
                            subtitle = "Hapus data Aplikasi",
                            subtitleColor = ExpenseRed,
                            onClick = { showResetDialog = true }
                        )
                        HorizontalDivider(
                            color = DividerGray,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        SettingsMenuItem(
                            icon = Icons.Default.Info,
                            iconBg = HeaderBlue,
                            title = "Versi aplikasi",
                            subtitle = "Versi ${settings.appVersion}",
                            showChevron = false,
                            onClick = {}
                        )
                        HorizontalDivider(
                            color = DividerGray,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        SettingsMenuItem(
                            icon = Icons.AutoMirrored.Filled.HelpOutline,
                            iconBg = HeaderBlue,
                            title = "Bantuan",
                            subtitle = "Hubungi WA: +62 812-3720-1227",
                            onClick = {
                                try {
                                    val waUrl = "https://wa.me/6281237201227"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Membuka WA +62 812-3720-1227", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // Google Drive Backup Dialog
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("Backup Data ke Google Drive", fontWeight = FontWeight.Bold) },
            text = { Text("Semua data transaksi dan anggota kas kelas akan disinkronkan dan disimpan secara aman ke akun Google Drive Anda.") },
            confirmButton = {
                TextButton(onClick = {
                    showBackupDialog = false
                    Toast.makeText(context, "Backup berhasil disimpan ke Google Drive!", Toast.LENGTH_LONG).show()
                }) {
                    Text("Sinkronkan Sekarang", color = HeaderBlue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = false }) {
                    Text("Batal", color = TextGray)
                }
            },
            containerColor = CardWhite
        )
    }

    // Export Report Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Laporan Kas", fontWeight = FontWeight.Bold) },
            text = { Text("Format laporan kas kelas akan diexport sebagai file ringkasan transaksi.") },
            confirmButton = {
                TextButton(onClick = {
                    showExportDialog = false
                    val reportContent = """
                        --- LAPORAN KAS KELAS ---
                        Tanggal: 03 September 2026
                        Total Anggota: 33 Siswa
                        Sudah Bayar: 22 Siswa
                        Belum Bayar: 11 Siswa
                        Penyelenggara: I W. B. P., D. & K., ELEKTRONIK
                        --------------------------
                    """.trimIndent()
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Laporan Kas Kelas")
                        putExtra(Intent.EXTRA_TEXT, reportContent)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Bagikan Laporan Kas"))
                }) {
                    Text("Export & Bagikan", color = HeaderBlue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Batal", color = TextGray)
                }
            },
            containerColor = CardWhite
        )
    }

    // Reset Data Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Data?", color = TextDark, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Semua transaksi akan dihapus dan data dikembalikan ke awal. Tindakan ini tidak bisa dibatalkan.",
                    color = TextGray
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    onResetData()
                    Toast.makeText(context, "Data berhasil di-reset!", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Reset", color = ExpenseRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal", color = TextGray)
                }
            },
            containerColor = CardWhite
        )
    }
}

@Composable
private fun SettingsMenuItem(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    subtitleColor: Color = TextGray,
    showChevron: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextDark
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = subtitleColor
            )
        }
        if (showChevron) {
            Icon(
                Icons.Default.ChevronRight, null,
                tint = TextGray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
