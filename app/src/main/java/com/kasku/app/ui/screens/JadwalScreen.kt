package com.kasku.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kasku.app.theme.CardBlue
import com.kasku.app.theme.HeaderBlue

// ── Data ────────────────────────────────────────────────────────────────────

private data class SubjectSlot(
    val subject: String,
    val teacher: String,
    val spanCount: Int = 1,  // how many jam pelajaran this slot spans
    val color: Color = Color(0xFFB71C1C)
)

private data class DaySchedule(
    val day: String,
    val slots: List<SubjectSlot?> // null = break or empty
)

private val subjectColors = mapOf(
    "RPL" to Color(0xFF8B1A1A),
    "B.ING" to Color(0xFF1B5E20),
    "MAT" to Color(0xFF1565C0),
    "PABP" to Color(0xFFE65100),
    "KIK" to Color(0xFFF9A825),
    "BB" to Color(0xFF00838F),
    "PP" to Color(0xFF1565C0),
    "B.INDO" to Color(0xFF558B2F),
    "PDL RPL" to Color(0xFF80D8FF).copy(alpha = 0.8f),
    "PDL KL" to Color(0xFFFF00FF).copy(alpha = 0.7f)
)

private val subjectTextColors = mapOf(
    "KIK" to Color.Black,
    "PDL RPL" to Color.Black,
    "PDL KL" to Color.Black
)

// Jam ke- (1–10) for each day
// For Monday: JP 1-4 = PDL, JP 5 = RPL, JP 6 = break, JP 7-10 = B.ING? No, let me read the schedule properly.
// From image:
// Sen: JP1-4: (PDL RPL spans 1-2-3-4 & PDL KL spans 3-4), JP5: RPL, break, JP7-10: B.ING
// Sel: JP1-4: RPL, break(5 or not visible?), JP5-10: RPL (with Pak Rizky)
// Rab: JP1-4: RPL, break, JP5-6(?): RPL, JP 9-10: PABP
// Kam: JP1-4: MAT (1-2), RPL (3-4), break(?), JP 5-10: KIK
// Jum: JP1-2: BB, JP3-4: PP, break, JP5-7: B.INDO, JP8-10: PABP

// Time allocation (Senin, Selasa-Kamis, Jumat) from the schedule image
private data class TimeSlot(val period: String, val time: String, val isBreak: Boolean = false)

private val timeTableSenin = listOf(
    TimeSlot("0", "07.10 – 08.00", true),
    TimeSlot("1", "08.00 – 08.40"),
    TimeSlot("2", "08.40 – 09.20"),
    TimeSlot("3", "09.20 – 10.00"),
    TimeSlot("4", "10.00 – 10.40"),
    TimeSlot("BREAK", "10.40 – 11.10", true),
    TimeSlot("5", "11.10 – 11.50"),
    TimeSlot("6", "11.50 – 12.30"),
    TimeSlot("7", "12.30 – 13.10"),
    TimeSlot("8", "13.10 – 13.50"),
    TimeSlot("BREAK", "13.50 – 14.20", true),
    TimeSlot("9", "14.20 – 14.50"),
    TimeSlot("10", "14.50 – 15.20")
)

private val timeTableSelasaKamis = listOf(
    TimeSlot("0", "07.10 – 07.40", true),
    TimeSlot("1", "07.40 – 08.20"),
    TimeSlot("2", "08.20 – 09.00"),
    TimeSlot("3", "09.00 – 09.40"),
    TimeSlot("4", "09.40 – 10.20"),
    TimeSlot("BREAK", "10.20 – 10.50", true),
    TimeSlot("5", "10.50 – 11.30"),
    TimeSlot("6", "11.30 – 12.10"),
    TimeSlot("7", "12.10 – 12.50"),
    TimeSlot("8", "12.50 – 13.30"),
    TimeSlot("BREAK", "13.30 – 14.00", true),
    TimeSlot("9", "14.00 – 14.40"),
    TimeSlot("10", "14.40 – 15.20")
)

private val timeTableJumat = listOf(
    TimeSlot("0", "07.30 – 08.00", true),
    TimeSlot("1", "08.00 – 08.30"),
    TimeSlot("2", "08.30 – 09.00"),
    TimeSlot("3", "09.00 – 09.30"),
    TimeSlot("4", "09.30 – 10.00"),
    TimeSlot("BREAK", "10.00 – 10.30", true),
    TimeSlot("5", "10.30 – 11.00"),
    TimeSlot("6", "11.00 – 11.30"),
    TimeSlot("7", "11.30 – 12.00"),
    TimeSlot("8", "12.00 – 12.30"),
    TimeSlot("9", "12.30 – 13.00"),
    TimeSlot("10", "13.00 – 13.30")
)

// ── XII PPLG Daily Schedule ──────────────────────────────────────────────────
// Each item: JP number, subject, teacher
private data class JadwalItem(
    val jp: Int,
    val subject: String,
    val teacher: String,
    val isBreak: Boolean = false,
    val note: String = ""
)

private val jadwalSenin = listOf(
    JadwalItem(1, "PDL RPL", "Pak Surya"),
    JadwalItem(2, "PDL RPL", "Pak Surya"),
    JadwalItem(3, "PDL KL", "Ms. Dayu N"),
    JadwalItem(4, "PDL KL", "Ms. Dayu N"),
    JadwalItem(5, "RPL", "Pak Rizky"),
    JadwalItem(7, "B.ING", "Ms. Dian"),
    JadwalItem(8, "B.ING", "Ms. Dian"),
    JadwalItem(9, "B.ING", "Ms. Dian"),
    JadwalItem(10, "B.ING", "Ms. Dian"),
)

private val jadwalSelasa = listOf(
    JadwalItem(1, "RPL", "Pak Rizky"),
    JadwalItem(2, "RPL", "Pak Rizky"),
    JadwalItem(3, "RPL", "Pak Rizky"),
    JadwalItem(4, "RPL", "Pak Rizky"),
    JadwalItem(5, "RPL", "Pak Rizky"),
    JadwalItem(7, "RPL", "Pak Rizky"),
    JadwalItem(8, "RPL", "Pak Rizky"),
    JadwalItem(9, "RPL", "Pak Rizky"),
    JadwalItem(10, "RPL", "Pak Rizky"),
)

private val jadwalRabu = listOf(
    JadwalItem(1, "RPL", "Pak Rizky"),
    JadwalItem(2, "RPL", "Pak Rizky"),
    JadwalItem(3, "RPL", "Pak Rizky"),
    JadwalItem(4, "RPL", "Pak Rizky"),
    JadwalItem(5, "RPL", "Pak Rizky"),
    JadwalItem(7, "RPL", "Pak Rizky"),
    JadwalItem(8, "RPL", "Pak Rizky"),
    JadwalItem(9, "PABP", "Bu Azmi / Bu Happy / Bu Sani", note = "Agama Islam/Kristen/Katolik"),
    JadwalItem(10, "PABP", "Bu Azmi / Bu Happy / Bu Sani", note = "Agama Islam/Kristen/Katolik"),
)

private val jadwalKamis = listOf(
    JadwalItem(1, "MAT", "Pak Restu"),
    JadwalItem(2, "MAT", "Pak Restu"),
    JadwalItem(3, "RPL", "Pak Rizky"),
    JadwalItem(4, "RPL", "Pak Rizky"),
    JadwalItem(5, "KIK", "Bu Lulu"),
    JadwalItem(7, "KIK", "Bu Lulu"),
    JadwalItem(8, "KIK", "Bu Lulu"),
    JadwalItem(9, "KIK", "Bu Lulu"),
    JadwalItem(10, "KIK", "Bu Lulu"),
)

private val jadwalJumat = listOf(
    JadwalItem(1, "BB", "Bu Sinta"),
    JadwalItem(2, "BB", "Bu Sinta"),
    JadwalItem(3, "PP", "Bu Happy"),
    JadwalItem(4, "PP", "Bu Happy"),
    JadwalItem(5, "B.INDO", "Mr. Esa"),
    JadwalItem(6, "B.INDO", "Mr. Esa"),
    JadwalItem(7, "B.INDO", "Mr. Esa"),
    JadwalItem(9, "PABP", "Bu Chika"),
    JadwalItem(10, "PABP", "Bu Chika"),
)

private data class DayInfo(
    val name: String,
    val fullName: String,
    val schedule: List<JadwalItem>,
    val timeTable: List<TimeSlot>
)

private val allDays = listOf(
    DayInfo("Sen", "Senin", jadwalSenin, timeTableSenin),
    DayInfo("Sel", "Selasa", jadwalSelasa, timeTableSelasaKamis),
    DayInfo("Rab", "Rabu", jadwalRabu, timeTableSelasaKamis),
    DayInfo("Kam", "Kamis", jadwalKamis, timeTableSelasaKamis),
    DayInfo("Jum", "Jumat", jadwalJumat, timeTableJumat)
)

// ── Screen ──────────────────────────────────────────────────────────────────

@Composable
fun JadwalScreen() {
    val bgColor = MaterialTheme.colorScheme.background
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outlineVariant

    var selectedDayIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // ── Header ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeaderBlue)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.height(24.dp).width(24.dp)
                )
                Text(
                    text = "JADWAL PELAJARAN",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.0f), // invisible placeholder for symmetry
                    modifier = Modifier.height(24.dp).width(24.dp)
                )
            }
        }

        // ── Class tag ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeaderBlue.copy(alpha = 0.08f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "XII PPLG  •  SMK Negeri 2 Kuta Selatan  •  SMT Ganjil TA 2026/2027",
                style = MaterialTheme.typography.labelMedium,
                color = HeaderBlue,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        // ── Day tabs ─────────────────────────────────────────────────
        TabRow(
            selectedTabIndex = selectedDayIndex,
            containerColor = surfaceColor,
            contentColor = HeaderBlue,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedDayIndex])
                        .height(3.dp)
                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                        .background(HeaderBlue)
                )
            },
            divider = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(outlineColor)
                )
            }
        ) {
            allDays.forEachIndexed { index, day ->
                Tab(
                    selected = selectedDayIndex == index,
                    onClick = { selectedDayIndex = index },
                    text = {
                        Text(
                            text = day.name,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (selectedDayIndex == index) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selectedDayIndex == index) HeaderBlue else textSecondary
                        )
                    }
                )
            }
        }

        // ── Day Schedule ─────────────────────────────────────────────
        val currentDay = allDays[selectedDayIndex]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Build a map of jp -> JadwalItem for quick lookup
            val jpMap = currentDay.schedule.associateBy { it.jp }

            currentDay.timeTable.forEach { timeSlot ->
                if (timeSlot.isBreak || timeSlot.period == "0") {
                    // Break / JP 0 row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (timeSlot.period == "BREAK")
                                    Color(0xFFFFF176).copy(alpha = 0.5f)
                                else
                                    surfaceColor.copy(alpha = 0.5f)
                            )
                            .border(1.dp, outlineColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (timeSlot.period == "BREAK") "ISTIRAHAT" else "JP 0",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (timeSlot.period == "BREAK") Color(0xFF795548) else textSecondary
                        )
                        Text(
                            text = timeSlot.time,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (timeSlot.period == "BREAK") Color(0xFF795548) else textSecondary
                        )
                    }
                } else {
                    val jpNum = timeSlot.period.toIntOrNull()
                    val item = jpMap[jpNum]

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // JP number + time column
                        Column(
                            modifier = Modifier
                                .width(64.dp)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "JP ${timeSlot.period}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = HeaderBlue
                            )
                            Text(
                                text = timeSlot.time,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = textSecondary,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Subject card
                        if (item != null) {
                            val subjectColor = subjectColors[item.subject] ?: Color(0xFF8B1A1A)
                            val textColor = subjectTextColors[item.subject] ?: Color.White

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(72.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = subjectColor),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = item.subject,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = textColor
                                    )
                                    Text(
                                        text = item.teacher,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = textColor.copy(alpha = 0.85f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (item.note.isNotEmpty()) {
                                        Text(
                                            text = "* ${item.note}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = textColor.copy(alpha = 0.7f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        } else {
                            // Empty slot
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, outlineColor, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "—",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = textSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Legend
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Kode Mata Pelajaran",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val legends = listOf(
                        "RPL" to "Rekayasa Perangkat Lunak",
                        "B.ING" to "Bahasa Inggris",
                        "MAT" to "Matematika",
                        "PABP" to "Agama & Budi Pekerti",
                        "KIK" to "Kecerdasan & Inovasi Kelas",
                        "BB" to "Bahasa Bali",
                        "PP" to "Projek Penguatan",
                        "B.INDO" to "Bahasa Indonesia",
                        "PDL RPL" to "Praktik Dasar Lap. RPL",
                        "PDL KL" to "Praktik Dasar Lap. KL"
                    )
                    legends.forEach { (code, name) ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(subjectColors[code] ?: HeaderBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$code",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = textPrimary,
                                modifier = Modifier.width(60.dp)
                            )
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelSmall,
                                color = textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
