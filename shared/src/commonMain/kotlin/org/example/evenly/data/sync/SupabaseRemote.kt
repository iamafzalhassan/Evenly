package org.example.evenly.data.sync

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.result.PostgrestResult
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal const val PULL_PAGE_SIZE: Int = 500

private const val COLUMN_ID: String = "id"
private const val COLUMN_SYNC_SEQ: String = "sync_seq"
private const val TABLE_EXPENSES: String = "expenses"
private const val TABLE_GROUPS: String = "groups"
private const val TABLE_MEMBERS: String = "members"
private const val TABLE_SETTLEMENTS: String = "settlements"

class SupabaseRemote internal constructor(private val client: SupabaseClient) {
    suspend fun ensureSignedIn(): Boolean {
        client.auth.awaitInitialization()
        if (client.auth.currentSessionOrNull() != null) return false
        client.auth.signInAnonymously()
        return true
    }

    suspend fun fetchGroups(ids: List<String>): List<RemoteGroupRow> = client.postgrest.from(TABLE_GROUPS).select { filter { isIn(COLUMN_ID, ids) } }.decodeList<RemoteGroupRow>()

    suspend fun joinGroup(code: String): String? = client.postgrest.rpc(function = "join_group", parameters = buildJsonObject { put("code", code) }).decodeAs<String?>()

    suspend fun pullExpenses(afterSeq: Long): List<RemoteExpenseRow> = pull(afterSeq = afterSeq, table = TABLE_EXPENSES).decodeList<RemoteExpenseRow>()

    suspend fun pullGroups(afterSeq: Long): List<RemoteGroupRow> = pull(afterSeq = afterSeq, table = TABLE_GROUPS).decodeList<RemoteGroupRow>()

    suspend fun pullMembers(afterSeq: Long): List<RemoteMemberRow> = pull(afterSeq = afterSeq, table = TABLE_MEMBERS).decodeList<RemoteMemberRow>()

    suspend fun pullSettlements(afterSeq: Long): List<RemoteSettlementRow> = pull(afterSeq = afterSeq, table = TABLE_SETTLEMENTS).decodeList<RemoteSettlementRow>()

    suspend fun pushExpenses(rows: List<RemoteExpenseWrite>) {
        client.postgrest.from(TABLE_EXPENSES).upsert(rows) { onConflict = COLUMN_ID }
    }

    suspend fun pushGroups(rows: List<RemoteGroupWrite>) {
        client.postgrest.from(TABLE_GROUPS).upsert(rows) { onConflict = COLUMN_ID }
    }

    suspend fun pushMembers(rows: List<RemoteMemberWrite>) {
        client.postgrest.from(TABLE_MEMBERS).upsert(rows) { onConflict = COLUMN_ID }
    }

    suspend fun pushSettlements(rows: List<RemoteSettlementWrite>) {
        client.postgrest.from(TABLE_SETTLEMENTS).upsert(rows) { onConflict = COLUMN_ID }
    }

    private suspend fun pull(afterSeq: Long, table: String): PostgrestResult = client.postgrest.from(table).select {
        filter { gt(COLUMN_SYNC_SEQ, afterSeq) }
        order(column = COLUMN_SYNC_SEQ, order = Order.ASCENDING)
        limit(PULL_PAGE_SIZE.toLong())
    }
}
