package com.kasku.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasku.app.model.AppSettings
import com.kasku.app.model.CashTransaction
import com.kasku.app.model.Member
import com.kasku.app.model.TransactionType
import com.kasku.app.model.UserProfile
import com.kasku.app.repository.KaskuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class KaskuUiState(
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val transactions: List<CashTransaction> = emptyList(),
    val members: List<Member> = emptyList(),
    val paidCountThisWeek: Int = 0,
    val totalMembersCount: Int = 0,
    val weeklyIncomeData: List<Pair<String, Double>> = emptyList()
)

class KaskuViewModel(
    private val repository: KaskuRepository = KaskuRepository()
) : ViewModel() {

    // -- Auth state --
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val settings: StateFlow<AppSettings> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    // -- Main UI state --
    val uiState: StateFlow<KaskuUiState> = combine(
        repository.transactions,
        repository.members
    ) { transactions, members ->
        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val balance = income - expense
        val paidCount = members.count { it.isPaidThisWeek }

        KaskuUiState(
            totalBalance = balance,
            totalIncome = income,
            totalExpense = expense,
            transactions = transactions,
            members = members,
            paidCountThisWeek = paidCount,
            totalMembersCount = members.size,
            weeklyIncomeData = repository.getWeeklyIncomeData()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = KaskuUiState()
    )

    // ── Auth ──────────────────────────────────────────────────────────────────

    /** Returns true if login succeeds */
    fun login(email: String, password: String): Boolean {
        val ok = repository.login(email, password)
        if (ok) _isLoggedIn.value = true
        return ok
    }

    /** Returns true if register succeeds (email not already taken) */
    fun register(name: String, email: String, password: String): Boolean {
        val ok = repository.register(name, email, password)
        if (ok) _isLoggedIn.value = true
        return ok
    }

    fun logout() {
        repository.logout()
        _isLoggedIn.value = false
    }

    // ── Profile & Settings ────────────────────────────────────────────────────

    fun updateProfile(name: String, email: String) {
        repository.updateProfile(name, email)
    }

    fun updateSettings(settings: AppSettings) {
        repository.updateSettings(settings)
    }

    fun resetData() {
        repository.resetData()
    }

    // ── Transactions ──────────────────────────────────────────────────────────

    fun addTransaction(title: String, amount: Double, type: TransactionType, category: String, memberName: String?) {
        repository.addTransaction(
            CashTransaction(
                title = title,
                amount = amount,
                type = type,
                category = category,
                memberName = memberName,
                date = "Hari Ini"
            )
        )
    }

    fun toggleMemberPayment(memberId: String) {
        repository.toggleMemberPayment(memberId)
    }

    fun formatRupiah(amount: Double): String = repository.formatRupiah(amount)

    fun calculatePenalty(member: Member): Double = repository.calculatePenalty(member)

    fun getUnpaidMonthsWithDelay(member: Member) = repository.getUnpaidMonthsWithDelay(member)
}
