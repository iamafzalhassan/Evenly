package org.example.evenly.data.sources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GroupDao {
    @Insert
    abstract suspend fun insertGroup(group: GroupEntity)

    @Insert
    abstract suspend fun insertMember(member: MemberEntity)

    @Insert
    abstract suspend fun insertMembers(members: List<MemberEntity>)

    @Query("SELECT COALESCE(MAX(position) + 1, 0) FROM members WHERE groupId = :groupId")
    abstract suspend fun nextMemberPosition(groupId: String): Int

    @Transaction
    @Query("SELECT * FROM expense_groups WHERE id = :id")
    abstract fun observeGroup(id: String): Flow<GroupWithMembers?>

    @Transaction
    @Query("SELECT * FROM expense_groups ORDER BY createdAtEpochMillis DESC")
    abstract fun observeGroups(): Flow<List<GroupWithMembers>>

    @Transaction
    open suspend fun appendMember(groupId: String, id: String, name: String) {
        insertMember(MemberEntity(position = nextMemberPosition(groupId), groupId = groupId, id = id, name = name))
    }

    @Transaction
    open suspend fun insertGroupWithMembers(group: GroupEntity, members: List<MemberEntity>) {
        insertGroup(group)
        insertMembers(members)
    }
}
