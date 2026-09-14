package org.example.evenly.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.evenly.data.sources.GroupDao
import org.example.evenly.data.sources.GroupEntity
import org.example.evenly.data.sources.MemberEntity
import org.example.evenly.model.Currency
import org.example.evenly.model.Group
import org.example.evenly.model.GroupId
import kotlin.time.Clock
import kotlin.uuid.Uuid

class GroupRepository internal constructor(private val groupDao: GroupDao) {
    suspend fun addMember(groupId: GroupId, name: String) = groupDao.appendMember(groupId = groupId.raw, id = Uuid.random().toString(), name = name.trim())

    suspend fun createGroup(name: String, memberNames: List<String>, currency: Currency): GroupId {
        val groupId = GroupId(Uuid.random().toString())
        val group = GroupEntity(createdAtEpochMillis = Clock.System.now().toEpochMilliseconds(), currencyCode = currency.name, id = groupId.raw, name = name.trim())
        val members = memberNames.mapIndexed { index, memberName -> MemberEntity(position = index, groupId = groupId.raw, id = Uuid.random().toString(), name = memberName.trim()) }
        groupDao.insertGroupWithMembers(group = group, members = members)
        return groupId
    }

    fun observeGroup(id: GroupId): Flow<Group?> = groupDao.observeGroup(id.raw).map { it?.toGroup() }

    fun observeGroups(): Flow<List<Group>> = groupDao.observeGroups().map { groups -> groups.map { it.toGroup() } }
}
