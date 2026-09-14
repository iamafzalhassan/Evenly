package org.example.evenly.data.sync

import org.example.evenly.data.sources.AppSettingEntity
import org.example.evenly.data.sources.RowSyncState
import org.example.evenly.data.sources.SettingsDao
import org.example.evenly.data.sources.SyncDao

private const val CURSOR_EXPENSES: String = "sync_cursor_expenses"
private const val CURSOR_GROUPS: String = "sync_cursor_groups"
private const val CURSOR_MEMBERS: String = "sync_cursor_members"
private const val CURSOR_SETTLEMENTS: String = "sync_cursor_settlements"

class SyncEngine internal constructor(private val settingsDao: SettingsDao, private val remote: SupabaseRemote, private val syncDao: SyncDao) {
    suspend fun joinGroup(code: String): String {
        remote.ensureSignedIn()
        val groupId = remote.joinGroup(code)
        listOf(CURSOR_EXPENSES, CURSOR_GROUPS, CURSOR_MEMBERS, CURSOR_SETTLEMENTS).forEach { writeCursor(key = it, value = 0L) }
        sync()
        return groupId
    }

    suspend fun sync() {
        remote.ensureSignedIn()
        pushGroups()
        pushMembers()
        pushExpenses()
        pushSettlements()
        pullTable(apply = ::applyGroup, cursorKey = CURSOR_GROUPS, fetch = remote::pullGroups, seqOf = { it.syncSeq })
        pullTable(apply = ::applyMember, cursorKey = CURSOR_MEMBERS, fetch = remote::pullMembers, seqOf = { it.syncSeq })
        pullTable(apply = ::applyExpense, cursorKey = CURSOR_EXPENSES, fetch = remote::pullExpenses, seqOf = { it.syncSeq })
        pullTable(apply = ::applySettlement, cursorKey = CURSOR_SETTLEMENTS, fetch = remote::pullSettlements, seqOf = { it.syncSeq })
    }

    private suspend fun pushGroups() {
        val groups = syncDao.dirtyGroups()
        if (groups.isEmpty()) return
        remote.pushGroups(groups.map { it.toRemote() })
        groups.forEach { syncDao.markGroupClean(id = it.id, modifiedAtEpochMillis = it.modifiedAtEpochMillis) }
    }

    private suspend fun pushMembers() {
        val members = syncDao.dirtyMembers()
        if (members.isEmpty()) return
        remote.pushMembers(members.map { it.toRemote() })
        members.forEach { syncDao.markMemberClean(id = it.id, modifiedAtEpochMillis = it.modifiedAtEpochMillis) }
    }

    private suspend fun pushExpenses() {
        val expenses = syncDao.dirtyExpenses()
        if (expenses.isEmpty()) return
        remote.pushExpenses(expenses.map { it.toRemote() })
        expenses.forEach { syncDao.markExpenseClean(id = it.expense.id, modifiedAtEpochMillis = it.expense.modifiedAtEpochMillis) }
    }

    private suspend fun pushSettlements() {
        val settlements = syncDao.dirtySettlements()
        if (settlements.isEmpty()) return
        remote.pushSettlements(settlements.map { it.toRemote() })
        settlements.forEach { syncDao.markSettlementClean(id = it.id, modifiedAtEpochMillis = it.modifiedAtEpochMillis) }
    }

    private suspend fun <T> pullTable(apply: suspend (T) -> Unit, cursorKey: String, fetch: suspend (Long) -> List<T>, seqOf: (T) -> Long) {
        var cursor = readCursor(cursorKey)
        while (true) {
            val rows = fetch(cursor)
            rows.forEach { apply(it) }
            if (rows.isEmpty()) return
            cursor = rows.maxOf(seqOf)
            writeCursor(key = cursorKey, value = cursor)
            if (rows.size < PULL_PAGE_SIZE) return
        }
    }

    private suspend fun readCursor(key: String): Long = settingsDao.findValue(key)?.toLongOrNull() ?: 0L

    private suspend fun writeCursor(key: String, value: Long) = settingsDao.upsertSetting(AppSettingEntity(key = key, value = value.toString()))

    private suspend fun applyGroup(row: RemoteGroupRow) {
        if (!keepsLocal(state = syncDao.groupState(row.id), remoteModifiedAtMs = row.modifiedAtMs)) {
            syncDao.upsertGroup(row.toEntity())
        }
        syncDao.saveInviteCode(id = row.id, inviteCode = row.inviteCode)
    }

    private suspend fun applyMember(row: RemoteMemberRow) {
        if (syncDao.groupState(row.groupId) == null || keepsLocal(state = syncDao.memberState(row.id), remoteModifiedAtMs = row.modifiedAtMs)) return
        syncDao.upsertMember(row.toEntity())
    }

    private suspend fun applyExpense(row: RemoteExpenseRow) {
        if (syncDao.groupState(row.groupId) == null || keepsLocal(state = syncDao.expenseState(row.id), remoteModifiedAtMs = row.modifiedAtMs)) return
        syncDao.saveRemoteExpense(expense = row.toEntity(), shares = row.toShareEntities())
    }

    private suspend fun applySettlement(row: RemoteSettlementRow) {
        if (syncDao.groupState(row.groupId) == null || keepsLocal(state = syncDao.settlementState(row.id), remoteModifiedAtMs = row.modifiedAtMs)) return
        syncDao.upsertSettlement(row.toEntity())
    }

    private fun keepsLocal(state: RowSyncState?, remoteModifiedAtMs: Long): Boolean = state != null && state.isDirty && state.modifiedAtEpochMillis > remoteModifiedAtMs
}
