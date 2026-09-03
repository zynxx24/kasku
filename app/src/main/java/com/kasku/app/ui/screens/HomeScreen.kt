package com.kasku.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kasku.app.model.CashTransaction
import com.kasku.app.model.TransactionType
import com.kasku.app.theme.*
import com.kasku.app.ui.viewmodel.KaskuUiState
import com.kasku.app.ui.viewmodel.KaskuViewModel

@Composable
fun HomeScreen(
    uiState: KaskuUiState,
    viewModel: KaskuViewModel,
    onAddTransactionClick: () -> Unit
) {
    val bgColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val dividerColor = MaterialTheme.colorScheme.outlineVariant
    val outlineColor = MaterialTheme.colorScheme.outline

    var showLaporanSheet by remember { mutableStateOf(false) }
    var showRiwayatSheet by remember { mutableStateOf(false) }
    var showMemberSheet by remember { mutableStateOf(false) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showNotifSheet by remember { mutableStateOf(false) }
    var selectedMonth by remember { mutableStateOf("Agustus 2026") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Blue header bar
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
                    Icons.Default.SwapVert,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "KAS KELAS",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black
                    ),
                    color = Color.White
                )
                IconButton(onClick = { showNotifSheet = true }) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifikasi",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Saldo kas total card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBlue)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Saldo kas total",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = viewModel.formatRupiah(uiState.totalBalance),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Total Siswa + Bulan Aktif row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showMemberSheet = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = HeaderBlue,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Total Siswa",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = textSecondary
                                )
                                Text(
                                    text = "${uiState.totalMembersCount}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = textPrimary
                                )
                                Text(
                                    text = "Orang",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = textSecondary
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showMonthPicker = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                tint = HeaderBlue,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Bulan Aktif",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = textSecondary
                                )
                                Text(
                                    text = selectedMonth,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = textPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Pembayaran Bulan Ini
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val progress = if (uiState.totalMembersCount > 0)
                            uiState.paidCountThisWeek.toFloat() / uiState.totalMembersCount.toFloat()
                        else 0f
                        val percent = (progress * 100).toInt()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = HeaderBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pembayaran Bulan Ini",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = textPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "$percent%",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = IncomeGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = IncomeGreen,
                            trackColor = dividerColor,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${uiState.paidCountThisWeek} / ${uiState.totalMembersCount} siswa yang sudah bayar",
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecondary
                        )
                    }
                }
            }

            // Line Chart Pemasukan Per Minggu
            item {
                WeeklyIncomeChartCard(
                    weeklyData = uiState.weeklyIncomeData,
                    viewModel = viewModel,
                    cardColor = cardColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Aksi cepat
            item {
                Text(
                    text = "Aksi cepat",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = textPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickActionItem(
                            icon = Icons.Default.ArrowUpward,
                            label = "Kas\nMasuk",
                            color = IncomeGreen,
                            textColor = textPrimary,
                            onClick = onAddTransactionClick
                        )
                        QuickActionItem(
                            icon = Icons.Default.ArrowDownward,
                            label = "Kas\nKeluar",
                            color = ExpenseRed,
                            textColor = textPrimary,
                            onClick = onAddTransactionClick
                        )
                        QuickActionItem(
                            icon = Icons.Default.Description,
                            label = "Laporan\nTransaksi",
                            color = HeaderBlue,
                            textColor = textPrimary,
                            onClick = { showLaporanSheet = true }
                        )
                        QuickActionItem(
                            icon = Icons.Default.History,
                            label = "Riwayat\nTransaksi",
                            color = HeaderBlue,
                            textColor = textPrimary,
                            onClick = { showRiwayatSheet = true }
                        )
                    }
                }
            }

            // Kas Masuk / Kas Keluar summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(IncomeGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "KAS MASUK",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = textPrimary
                                )
                                Text(
                                    text = viewModel.formatRupiah(uiState.totalIncome),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = textPrimary
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ExpenseRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "KAS KELUAR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = textPrimary
                                )
                                Text(
                                    text = viewModel.formatRupiah(uiState.totalExpense),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = textPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Recent transactions section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat Transaksi",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = textPrimary
                    )
                    Text(
                        text = "${uiState.transactions.size} transaksi",
                        style = MaterialTheme.typography.labelMedium,
                        color = textSecondary
                    )
                }
            }

            if (uiState.transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada transaksi",
                                style = MaterialTheme.typography.bodyMedium,
                                color = textSecondary
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(uiState.transactions) { _, transaction ->
                    TransactionItem(
                        transaction = transaction,
                        viewModel = viewModel,
                        cardColor = cardColor,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // Laporan Transaksi Bottom Sheet
    if (showLaporanSheet) {
        LaporanTransaksiSheet(
            uiState = uiState,
            viewModel = viewModel,
            onDismiss = { showLaporanSheet = false }
        )
    }

    // Riwayat Transaksi Bottom Sheet
    if (showRiwayatSheet) {
        RiwayatTransaksiSheet(
            uiState = uiState,
            viewModel = viewModel,
            onDismiss = { showRiwayatSheet = false }
        )
    }

    // Daftar Siswa & Riwayat Bayar Bottom Sheet
    if (showMemberSheet) {
        DaftarSiswaSheet(
            uiState = uiState,
            viewModel = viewModel,
            selectedMonth = selectedMonth,
            onDismiss = { showMemberSheet = false }
        )
    }

    // Month Picker Bottom Sheet
    if (showMonthPicker) {
        MonthPickerSheet(
            selectedMonth = selectedMonth,
            onSelectMonth = { selectedMonth = it },
            onDismiss = { showMonthPicker = false }
        )
    }

    // Notification Log Bottom Sheet
    if (showNotifSheet) {
        NotificationLogSheet(
            onDismiss = { showNotifSheet = false }
        )
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    color: Color,
    textColor: Color = TextDark,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            maxLines = 2,
            lineHeight = 14.sp,
            modifier = Modifier.width(64.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// ── Weekly Income Line Chart ─────────────────────────────────────────────
@Composable
fun WeeklyIncomeChartCard(
    weeklyData: List<Pair<String, Double>>,
    viewModel: KaskuViewModel,
    cardColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = HeaderBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pemasukan Per Minggu",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = textPrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Perbandingan iuran kas masuk per minggu",
                style = MaterialTheme.typography.labelSmall,
                color = textSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (weeklyData.isEmpty() || weeklyData.all { it.second == 0.0 }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada data pemasukan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textSecondary
                    )
                }
            } else {
                WeeklyIncomeChart(
                    weeklyData = weeklyData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Weekly totals row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weeklyData.forEach { (label, amount) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecondary
                        )
                        Text(
                            text = viewModel.formatRupiah(amount),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (amount > 0) IncomeGreen else textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyIncomeChart(
    weeklyData: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    val values = weeklyData.map { it.second.toFloat() }
    val maxVal = values.maxOrNull()?.coerceAtLeast(1f) ?: 1f
    val lineColor = HeaderBlue
    val dotColor = HeaderBlue
    val fillColorTop = HeaderBlue.copy(alpha = 0.25f)
    val fillColorBottom = HeaderBlue.copy(alpha = 0.02f)
    val gridColor = DividerGray
    val labelColor = TextGray.toArgb()

    Canvas(modifier = modifier) {
        val paddingLeft = 16.dp.toPx()
        val paddingRight = 16.dp.toPx()
        val paddingTop = 12.dp.toPx()
        val paddingBottom = 28.dp.toPx()

        val chartWidth = size.width - paddingLeft - paddingRight
        val chartHeight = size.height - paddingTop - paddingBottom

        val stepX = if (values.size > 1) chartWidth / (values.size - 1) else chartWidth

        // Draw horizontal grid lines (3 lines)
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
        for (i in 0..2) {
            val y = paddingTop + chartHeight * (1f - i / 2f)
            drawLine(
                color = gridColor,
                start = Offset(paddingLeft, y),
                end = Offset(size.width - paddingRight, y),
                pathEffect = dashEffect,
                strokeWidth = 1.dp.toPx()
            )
        }

        // Calculate points
        val points = values.mapIndexed { index, value ->
            val x = paddingLeft + index * stepX
            val y = paddingTop + chartHeight * (1f - value / maxVal)
            Offset(x, y)
        }

        // Draw gradient fill area
        if (points.size >= 2) {
            val fillPath = Path().apply {
                moveTo(points.first().x, paddingTop + chartHeight)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, paddingTop + chartHeight)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(fillColorTop, fillColorBottom),
                    startY = paddingTop,
                    endY = paddingTop + chartHeight
                )
            )

            // Draw line
            val linePath = Path().apply {
                moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }
            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        // Draw dots and labels
        points.forEachIndexed { index, point ->
            // Outer glow
            drawCircle(
                color = dotColor.copy(alpha = 0.2f),
                radius = 8.dp.toPx(),
                center = point
            )
            // Inner dot
            drawCircle(
                color = dotColor,
                radius = 4.5.dp.toPx(),
                center = point
            )
            // Center white
            drawCircle(
                color = Color.White,
                radius = 2.5.dp.toPx(),
                center = point
            )

            // Draw week labels at bottom
            val label = weeklyData[index].first
            drawContext.canvas.nativeCanvas.drawText(
                label,
                point.x,
                size.height - 4.dp.toPx(),
                android.graphics.Paint().apply {
                    color = labelColor
                    textSize = 10.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
            )
        }
    }
}

@Composable
fun TransactionItem(
    transaction: CashTransaction,
    viewModel: KaskuViewModel,
    cardColor: Color = CardWhite,
    textPrimary: Color = TextDark,
    textSecondary: Color = TextGray
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val accentColor = if (isIncome) IncomeGreen else ExpenseRed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = textPrimary,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${transaction.category} - ${transaction.date}",
                        style = MaterialTheme.typography.labelSmall,
                        color = textSecondary
                    )
                }
            }

            Text(
                text = viewModel.formatRupiah(transaction.amount),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = accentColor
            )
        }
    }
}

// ── Bottom Sheet: Laporan Transaksi ──────────────────────────────────────
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun LaporanTransaksiSheet(
    uiState: KaskuUiState,
    viewModel: KaskuViewModel,
    onDismiss: () -> Unit
) {
    val cardColor = MaterialTheme.colorScheme.surface
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Laporan Transaksi",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = textPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Kas Masuk summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = IncomeGreen.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(IncomeGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowUpward, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Total Kas Masuk", style = MaterialTheme.typography.titleSmall, color = textSecondary)
                        Text(viewModel.formatRupiah(uiState.totalIncome), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = IncomeGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Kas Keluar summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ExpenseRed.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(ExpenseRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowDownward, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Total Kas Keluar", style = MaterialTheme.typography.titleSmall, color = textSecondary)
                        Text(viewModel.formatRupiah(uiState.totalExpense), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = ExpenseRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Saldo Akhir
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = HeaderBlue.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(HeaderBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Saldo Akhir", style = MaterialTheme.typography.titleSmall, color = textSecondary)
                        Text(viewModel.formatRupiah(uiState.totalBalance), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = HeaderBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pembayaran status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Sudah Bayar", style = MaterialTheme.typography.bodyMedium, color = textSecondary)
                Text("${uiState.paidCountThisWeek} / ${uiState.totalMembersCount}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = IncomeGreen)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Belum Bayar", style = MaterialTheme.typography.bodyMedium, color = textSecondary)
                Text("${uiState.totalMembersCount - uiState.paidCountThisWeek} / ${uiState.totalMembersCount}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = ExpenseRed)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Bottom Sheet: Riwayat Transaksi ──────────────────────────────────────
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun RiwayatTransaksiSheet(
    uiState: KaskuUiState,
    viewModel: KaskuViewModel,
    onDismiss: () -> Unit
) {
    val cardColor = MaterialTheme.colorScheme.surface
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Riwayat Transaksi",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = textPrimary
                )
                Text(
                    text = "${uiState.transactions.size} total",
                    style = MaterialTheme.typography.labelMedium,
                    color = textSecondary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada transaksi", color = textSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.height(400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(uiState.transactions) { _, tx ->
                        TransactionItem(
                            transaction = tx,
                            viewModel = viewModel,
                            cardColor = cardColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ── Bottom Sheet: Daftar Siswa & Riwayat Bayar ───────────────────────────
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DaftarSiswaSheet(
    uiState: KaskuUiState,
    viewModel: KaskuViewModel,
    selectedMonth: String,
    onDismiss: () -> Unit
) {
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val cardColor = MaterialTheme.colorScheme.surface
    val months = listOf("Juli 2026", "Agustus 2026")

    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Siswa",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = textPrimary
                )
                Text(
                    text = "${uiState.totalMembersCount} siswa",
                    style = MaterialTheme.typography.labelMedium,
                    color = textSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Month headers + Denda column
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                months.forEach { month ->
                    val shortMonth = month.substringBefore(" ")
                    Text(
                        text = shortMonth,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (month == selectedMonth) HeaderBlue else textSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
                Text(
                    text = "Denda",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = ExpenseRed,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Members list
            LazyColumn(
                modifier = Modifier.height(420.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(uiState.members) { index, member ->
                    val penalty = viewModel.calculatePenalty(member)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(HeaderBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = member.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = textPrimary,
                                        maxLines = 1
                                    )
                                    if (member.role != "Anggota") {
                                        Text(
                                            text = member.role,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = HeaderBlue
                                        )
                                    }
                                }

                                months.forEach { month ->
                                    val paid = member.monthlyPayments[month] ?: false
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (paid) IncomeGreen.copy(alpha = 0.15f) else ExpenseRed.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (paid) "V" else "X",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                            color = if (paid) IncomeGreen else ExpenseRed
                                        )
                                    }
                                }

                                // Penalty badge
                                if (penalty > 0) {
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 4.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ExpenseRed.copy(alpha = 0.1f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = viewModel.formatRupiah(penalty),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 8.sp
                                            ),
                                            color = ExpenseRed
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 4.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(IncomeGreen.copy(alpha = 0.1f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "-",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 8.sp
                                            ),
                                            color = IncomeGreen
                                        )
                                    }
                                }
                            }

                            // Show penalty breakdown for members with denda
                            if (penalty > 0) {
                                val unpaidMonths = viewModel.getUnpaidMonthsWithDelay(member)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(ExpenseRed.copy(alpha = 0.05f))
                                        .padding(horizontal = 52.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = ExpenseRed.copy(alpha = 0.7f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    val detail = unpaidMonths.joinToString(", ") { (m, d) ->
                                        "${m.substringBefore(" ")}: ${d}x denda"
                                    }
                                    Text(
                                        text = detail,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = ExpenseRed.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val paidCount = uiState.members.count { it.monthlyPayments[selectedMonth] == true }
            val unpaidCount = uiState.totalMembersCount - paidCount
            val totalPenalty = uiState.members.sumOf { viewModel.calculatePenalty(it) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(IncomeGreen))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Lunas: $paidCount", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = IncomeGreen)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(ExpenseRed))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Belum: $unpaidCount", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = ExpenseRed)
                }
            }

            if (totalPenalty > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = ExpenseRed.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = ExpenseRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Total Denda Keseluruhan",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = ExpenseRed
                            )
                        }
                        Text(
                            text = viewModel.formatRupiah(totalPenalty),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                            color = ExpenseRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ── Bottom Sheet: Pilih Bulan ────────────────────────────────────────────
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MonthPickerSheet(
    selectedMonth: String,
    onSelectMonth: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val cardColor = MaterialTheme.colorScheme.surface

    val availableMonths = listOf("Juli 2026", "Agustus 2026")

    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Pilih Bulan Aktif",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            availableMonths.forEach { month ->
                val isSelected = month == selectedMonth
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            onSelectMonth(month)
                            onDismiss()
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) HeaderBlue else cardColor
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else HeaderBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = month,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) Color.White else textPrimary
                            )
                        }
                        if (isSelected) {
                            Text(
                                text = "Aktif",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Bottom Sheet: Notification Log ───────────────────────────────────────
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun NotificationLogSheet(
    onDismiss: () -> Unit
) {
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val cardColor = MaterialTheme.colorScheme.surface

    data class NotifItem(
        val icon: ImageVector,
        val iconColor: Color,
        val title: String,
        val desc: String,
        val time: String,
        val isNew: Boolean = false
    )

    val notifications = listOf(
        NotifItem(
            icon = Icons.Default.Notifications,
            iconColor = HeaderBlue,
            title = "Update v1.0.1 Tersedia",
            desc = "Pembaruan fitur: Daftar Siswa interaktif, Pemilih Bulan Aktif, dan perbaikan Mode Gelap.",
            time = "25 Agu 2026, 08:00",
            isNew = true
        ),
        NotifItem(
            icon = Icons.Default.ArrowUpward,
            iconColor = IncomeGreen,
            title = "Pembayaran Kas Diterima",
            desc = "NI KADEK LINA ANTIKA DEWI telah membayar iuran kas Agustus 2026 sebesar Rp 10.000.",
            time = "24 Agu 2026, 14:32",
            isNew = true
        ),
        NotifItem(
            icon = Icons.Default.Email,
            iconColor = ExpenseRed,
            title = "Pengingat: 11 Siswa Belum Bayar",
            desc = "Masih ada 11 siswa yang belum melunasi iuran kas bulan Agustus 2026. Segera ingatkan.",
            time = "23 Agu 2026, 09:00",
            isNew = false
        ),
        NotifItem(
            icon = Icons.Default.Edit,
            iconColor = HeaderBlue,
            title = "Patch v1.0.0 Diterapkan",
            desc = "Perbaikan bug: Kartu QRIS tidak tampil pada dialog Kas Masuk. Tema putih minimalis diterapkan.",
            time = "22 Agu 2026, 20:15",
            isNew = false
        ),
        NotifItem(
            icon = Icons.Default.Add,
            iconColor = CardBlue,
            title = "Anggota Baru Ditambahkan",
            desc = "RADITYA RONDI telah ditambahkan ke daftar anggota kelas XII PPLG sebagai Anggota.",
            time = "22 Agu 2026, 10:30",
            isNew = false
        ),
        NotifItem(
            icon = Icons.Default.AccountBalanceWallet,
            iconColor = IncomeGreen,
            title = "Pembayaran Kas Massal",
            desc = "22 siswa telah membayar iuran kas Agustus 2026 secara bersamaan. Total: Rp 220.000.",
            time = "21 Agu 2026, 08:45",
            isNew = false
        ),
        NotifItem(
            icon = Icons.Default.Done,
            iconColor = IncomeGreen,
            title = "Backup Berhasil",
            desc = "Data kas kelas berhasil dicadangkan ke penyimpanan lokal perangkat.",
            time = "20 Agu 2026, 18:00",
            isNew = false
        ),
        NotifItem(
            icon = Icons.Default.Favorite,
            iconColor = HeaderBlue,
            title = "Rilis Awal KasKu v1.0.0",
            desc = "Selamat datang di KasKu! Aplikasi kas kelas digital untuk XII PPLG telah siap digunakan.",
            time = "19 Agu 2026, 07:00",
            isNew = false
        )
    )

    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notifikasi",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = textPrimary
                )
                Text(
                    text = "${notifications.count { it.isNew }} baru",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = HeaderBlue
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.height(450.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(notifications) { _, notif ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (notif.isNew) notif.iconColor.copy(alpha = 0.06f) else cardColor
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (notif.isNew) 2.dp else 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(notif.iconColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    notif.icon,
                                    contentDescription = null,
                                    tint = notif.iconColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (notif.isNew) FontWeight.Bold else FontWeight.SemiBold
                                        ),
                                        color = textPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (notif.isNew) {
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(HeaderBlue)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = notif.desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = notif.time,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = textSecondary.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
