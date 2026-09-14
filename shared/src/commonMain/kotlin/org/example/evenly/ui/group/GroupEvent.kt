package org.example.evenly.ui.group

import org.example.evenly.model.SettlementId
import org.example.evenly.model.Transfer

sealed interface GroupEvent {
    data class AddMember(val name: String) : GroupEvent

    data class DeleteSettlement(val settlementId: SettlementId) : GroupEvent

    data object DismissActionFailure : GroupEvent

    data class RecordTransfer(val transfer: Transfer) : GroupEvent
}
