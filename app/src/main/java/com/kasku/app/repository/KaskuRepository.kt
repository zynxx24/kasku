package com.kasku.app.repository

import android.content.Context
import android.content.SharedPreferences
import com.kasku.app.model.AppSettings
import com.kasku.app.model.CashTransaction
import com.kasku.app.model.Member
import com.kasku.app.model.TransactionType
import com.kasku.app.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.util.Locale

class KaskuRepository(context: Context? = null) {

    private val prefs: SharedPreferences? = context?.getSharedPreferences("kasku_prefs", Context.MODE_PRIVATE)

    private val _transactions = MutableStateFlow<List<CashTransaction>>(emptyList())
    val transactions: StateFlow<List<CashTransaction>> = _transactions.asStateFlow()

    private val _members = MutableStateFlow<List<Member>>(emptyList())
    val members: StateFlow<List<Member>> = _members.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile(name = "Akun", email = "AkunKu1234@gmail.com", role = "Administrator"))
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    // email -> Triple(name, password, role)
    private val registeredUsers = mutableMapOf<String, Triple<String, String, String>>()

    init {
        loadData()
    }

    private fun loadData() {
        // Default admin account
        registeredUsers["akunku1234@gmail.com"] = Triple("Akun", "123456", "Administrator")

        if (prefs != null && prefs.contains("saved_data")) {
            loadFromPrefs()
        } else {
            loadInitialData()
            saveToPrefs()
        }
    }

    private fun loadInitialData() {
        val memberNames = listOf(
            "BOYKE VILANO HAMONANGAN SIHITE" to "Ketua Kelas",
            "BINTANG LEONITA CHRISTYA RENATA" to "Bendahara",
            "CAROLINA TIMUTHY JANGGUR" to "Sekretaris",
            "DEWA GEDE DALEM OKA ADNYANA SANDI" to "Wakil Ketua",
            "GALISTAN RAMADHAN KURNIA TAUNAES" to "Anggota",
            "GEDE AGUS WIRA DARMA PUTRA" to "Anggota",
            "I GEDE ABI WIRYA DINATA" to "Anggota",
            "I GEDE DARMA SUPTIAWAN" to "Anggota",
            "I KOMANG RADITYA PUTRA" to "Anggota",
            "I KOMANG RISKI SETIAWAN" to "Anggota",
            "I NYOMAN GEDE ARTA WIGUNA" to "Anggota",
            "I PUTU DIKA LAKSMANA PUTRA" to "Anggota",
            "I PUTU DITYA ARTHA WIJAYA" to "Anggota",
            "I PUTU PANDE ANDIKA" to "Anggota",
            "I PUTU SUYOGA MAHENDRA" to "Anggota",
            "I WAYAN BAGUS PUTRAWAN" to "Anggota",
            "I WAYAN PASEK KEVIN ARIADI" to "Anggota",
            "KADEK YUDA PRASETYA" to "Anggota",
            "KADEK YUNI CALLISTA PUTRI DEWI" to "Anggota",
            "KOMANG DIAH PUTRI PRATIWI" to "Anggota",
            "LUH RIA MIRASIH" to "Anggota",
            "NI KADEK ADELIA CAHYA KENCANA PUTRI" to "Anggota",
            "NI KADEK LINA ANTIKA DEWI" to "Anggota",
            "NI KOMANG KIRANA PARAMITA ARDANARI" to "Anggota",
            "NI KOMANG SEPTIARINI" to "Anggota",
            "NI LUH PUTU KESYA ASTRI MELANI" to "Anggota",
            "NI PUTU CAHAYA LESTARI DEWI" to "Anggota",
            "NI PUTU INTAN LESTARI DARMAYANTI" to "Anggota",
            "OKTA PRADIPTA ATTALA DZAKI" to "Anggota",
            "PUTU BAYU SATRIA WANGSA BUKIAN" to "Anggota",
            "PUTU NANDA LINDIA MAHARANI" to "Anggota",
            "PUTU PUTRI CAHYANI" to "Anggota",
            "RADITYA RONDI" to "Anggota"
        )

        // Generate per-member monthly payment data
        val initialMembers = memberNames.mapIndexed { index, (name, role) ->
            val isPaidAugust = index < 22
            val isPaidJuli = index < 28
            val payments = mapOf(
                "Juli 2026" to isPaidJuli,
                "Agustus 2026" to isPaidAugust
            )
            val monthsPaid = payments.values.count { it }
            Member(
                name = name,
                role = role,
                totalPaid = monthsPaid * 10000.0,
                isPaidThisWeek = isPaidAugust,
                monthlyPayments = payments
            )
        }
        _members.value = initialMembers

        // Generate transaction history with varied dates across 4 weeks
        val initialTransactions = mutableListOf<CashTransaction>()

        val weekDates = listOf(
            "03 Agu 2026", "04 Agu 2026",   // Minggu 1
            "10 Agu 2026", "11 Agu 2026",   // Minggu 2
            "17 Agu 2026", "18 Agu 2026",   // Minggu 3
            "24 Agu 2026", "25 Agu 2026", "26 Agu 2026", "27 Agu 2026"  // Minggu 4
        )

        // Add income transactions for paid members with varied dates
        initialMembers.filter { it.isPaidThisWeek }.forEachIndexed { i, member ->
            val dateIdx = i % weekDates.size
            initialTransactions.add(
                CashTransaction(
                    title = "Iuran Kas - ${member.name}",
                    amount = 10000.0,
                    type = TransactionType.INCOME,
                    memberName = member.name,
                    date = weekDates[dateIdx],
                    category = "Iuran Mingguan"
                )
            )
        }

        // Add class expenses
        initialTransactions.add(
            CashTransaction(
                title = "Beli Spidol & Penghapus Board",
                amount = 25000.0,
                type = TransactionType.EXPENSE,
                memberName = null,
                date = "21 Agu 2026",
                category = "Perlengkapan Kelas"
            )
        )
        initialTransactions.add(
            CashTransaction(
                title = "Cetak Dokumen & Kasus Kelas",
                amount = 15000.0,
                type = TransactionType.EXPENSE,
                memberName = null,
                date = "19 Agu 2026",
                category = "Operasional"
            )
        )

        _transactions.value = initialTransactions
        _userProfile.value = UserProfile(name = "Akun", email = "AkunKu1234@gmail.com", role = "Administrator")
        _settings.value = AppSettings()
    }

    private fun saveToPrefs() {
        prefs?.edit()?.apply {
            putBoolean("saved_data", true)
            putString("user_name", _userProfile.value.name)
            putString("user_email", _userProfile.value.email)
            putString("user_role", _userProfile.value.role)
            putBoolean("notif_enabled", _settings.value.notificationsEnabled)
            apply()
        }
    }

    private fun loadFromPrefs() {
        if (prefs == null) {
            loadInitialData()
            return
        }
        val name = prefs.getString("user_name", "Akun") ?: "Akun"
        val email = prefs.getString("user_email", "AkunKu1234@gmail.com") ?: "AkunKu1234@gmail.com"
        val role = prefs.getString("user_role", "Anggota") ?: "Anggota"
        val notifEnabled = prefs.getBoolean("notif_enabled", true)

        _userProfile.value = UserProfile(name = name, email = email, role = role)
        _settings.value = AppSettings(notificationsEnabled = notifEnabled)
        loadInitialData()
        _userProfile.value = UserProfile(name = name, email = email, role = role)
        _settings.value = AppSettings(notificationsEnabled = notifEnabled)
    }

    // ── Auth ──────────────────────────────────────────────────────────────────

    fun login(email: String, password: String): Boolean {
        val key = email.trim().lowercase()
        val stored = registeredUsers[key]
        if (stored != null && stored.second == password) {
            _userProfile.value = UserProfile(name = stored.first, email = key, role = stored.third)
            saveToPrefs()
            return true
        }
        return false
    }

    fun register(name: String, email: String, password: String): Boolean {
        val key = email.trim().lowercase()
        if (registeredUsers.containsKey(key)) return false
        registeredUsers[key] = Triple(name.trim(), password, "Anggota")
        _userProfile.value = UserProfile(name = name.trim(), email = key, role = "Anggota")
        saveToPrefs()
        return true
    }

    fun logout() {
        _userProfile.value = UserProfile()
        saveToPrefs()
    }

    // ── Profile & Settings ────────────────────────────────────────────────────

    fun updateProfile(name: String, email: String) {
        _userProfile.value = _userProfile.value.copy(name = name, email = email)
        saveToPrefs()
    }

    fun updateSettings(settings: AppSettings) {
        _settings.value = settings
        saveToPrefs()
    }

    fun resetData() {
        prefs?.edit()?.clear()?.apply()
        loadInitialData()
    }

    // ── Penalty / Denda ───────────────────────────────────────────────────────

    /**
     * Returns list of (month, delay multiplier) for unpaid months.
     * Current month is September 2026. Months tracked: Juli, Agustus.
     * Juli unpaid -> 2× delay (Jul->Aug, Aug->Sep)
     * Agustus unpaid -> 1× delay (Aug->Sep)
     */
    fun getUnpaidMonthsWithDelay(member: Member): List<Pair<String, Int>> {
        val monthOrder = listOf("Juli 2026", "Agustus 2026")
        val currentMonthIndex = 2 // September 2026 = index 2
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

    /**
     * Calculate total denda (penalty) for a member.
     * Denda = 5% × iuran (Rp 10.000) × jumlah bulan keterlambatan per bulan yang belum dibayar.
     */
    fun calculatePenalty(member: Member): Double {
        val fee = _settings.value.weeklyFee  // Rp 10.000
        val penaltyRate = 0.05
        var totalPenalty = 0.0
        for ((_, delay) in getUnpaidMonthsWithDelay(member)) {
            totalPenalty += fee * penaltyRate * delay
        }
        return totalPenalty
    }

    /**
     * Weekly income data aggregation for chart.
     * Groups income transactions by week and returns (weekLabel, totalIncome).
     */
    fun getWeeklyIncomeData(): List<Pair<String, Double>> {
        val weekMap = linkedMapOf(
            "Mg 1" to 0.0,
            "Mg 2" to 0.0,
            "Mg 3" to 0.0,
            "Mg 4" to 0.0
        )
        for (t in _transactions.value) {
            if (t.type != TransactionType.INCOME) continue
            val day = t.date.split(" ").firstOrNull()?.toIntOrNull() ?: continue
            val weekKey = when {
                day <= 7 -> "Mg 1"
                day <= 14 -> "Mg 2"
                day <= 21 -> "Mg 3"
                else -> "Mg 4"
            }
            weekMap[weekKey] = (weekMap[weekKey] ?: 0.0) + t.amount
        }
        return weekMap.toList()
    }

    // ── Transactions ──────────────────────────────────────────────────────────

    fun addTransaction(transaction: CashTransaction) {
        _transactions.value = listOf(transaction) + _transactions.value

        if (transaction.type == TransactionType.INCOME && transaction.memberName != null) {
            _members.value = _members.value.map { member ->
                if (member.name.equals(transaction.memberName, ignoreCase = true)) {
                    member.copy(
                        totalPaid = member.totalPaid + transaction.amount,
                        isPaidThisWeek = true
                    )
                } else member
            }
        }
    }

    fun toggleMemberPayment(memberId: String) {
        val weeklyFee = _settings.value.weeklyFee
        _members.value = _members.value.map { member ->
            if (member.id == memberId) {
                val newPaidState = !member.isPaidThisWeek
                val penalty = if (newPaidState) calculatePenalty(member) else 0.0
                val totalAmount = weeklyFee + penalty
                if (newPaidState) {
                    addTransaction(
                        CashTransaction(
                            title = "Iuran Kas - ${member.name}" + if (penalty > 0) " (+Denda)" else "",
                            amount = totalAmount,
                            type = TransactionType.INCOME,
                            memberName = member.name,
                            date = "Hari Ini",
                            category = "Iuran Mingguan"
                        )
                    )
                }
                member.copy(
                    isPaidThisWeek = newPaidState,
                    totalPaid = if (newPaidState) member.totalPaid + totalAmount else member.totalPaid
                )
            } else member
        }
    }

    fun formatRupiah(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount)
    }

    private fun String.capitalized(): String {
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}
