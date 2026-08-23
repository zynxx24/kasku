package com.kasku.app.model

import java.util.UUID

enum class TransactionType {
    INCOME,
    EXPENSE
}

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
    val monthlyPayments: Map<String, Boolean> = emptyMap()
)

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val classGroup: String = "XII PPLG"
)

data class AppSettings(
    val notificationsEnabled: Boolean = true,
    val isDarkMode: Boolean = false,
    val weeklyFee: Double = 10000.0,
    val appVersion: String = "1.0.1"
)
