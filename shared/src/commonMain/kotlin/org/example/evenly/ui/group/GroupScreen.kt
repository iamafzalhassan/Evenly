package org.example.evenly.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import evenly.shared.generated.resources.Res
import evenly.shared.generated.resources.action_add_expense
import evenly.shared.generated.resources.action_add_member
import evenly.shared.generated.resources.action_delete_payment
import evenly.shared.generated.resources.action_record_payment
import evenly.shared.generated.resources.balance_gets_back
import evenly.shared.generated.resources.balance_owes
import evenly.shared.generated.resources.balance_settled
import evenly.shared.generated.resources.delete_payment_message
import evenly.shared.generated.resources.expense_tile_subtitle
import evenly.shared.generated.resources.group_action_failed
import evenly.shared.generated.resources.group_balances_heading
import evenly.shared.generated.resources.group_expenses_empty_message
import evenly.shared.generated.resources.group_expenses_empty_title
import evenly.shared.generated.resources.group_expenses_heading
import evenly.shared.generated.resources.group_members_heading
import evenly.shared.generated.resources.group_payments_heading
import evenly.shared.generated.resources.group_settle_up_heading
import evenly.shared.generated.resources.member_unknown
import evenly.shared.generated.resources.record_payment_message
import evenly.shared.generated.resources.settlement_title
import evenly.shared.generated.resources.transfer_subtitle
import evenly.shared.generated.resources.transfer_title
import org.example.evenly.model.Balance
import org.example.evenly.model.Expense
import org.example.evenly.model.ExpenseId
import org.example.evenly.model.MemberId
import org.example.evenly.model.Money
import org.example.evenly.model.Settlement
import org.example.evenly.model.Transfer
import org.example.evenly.ui.components.AppListTile
import org.example.evenly.ui.components.AppSnackbarHost
import org.example.evenly.ui.components.AppTopBar
import org.example.evenly.ui.components.ConfirmSheet
import org.example.evenly.ui.components.EmptyState
import org.example.evenly.ui.components.PrimaryButton
import org.example.evenly.ui.components.SecondaryButton
import org.example.evenly.ui.components.SectionHeader
import org.example.evenly.ui.components.rememberAppSnackbarState
import org.example.evenly.ui.group.components.AddMemberSheet
import org.example.evenly.ui.theme.AppColors
import org.example.evenly.ui.theme.AppSpacing
import org.example.evenly.ui.theme.AppTheme
import org.example.evenly.util.DateFormat
import org.example.evenly.util.MoneyFormat
import org.jetbrains.compose.resources.stringResource

@Composable
fun GroupScreen(onAddExpense: () -> Unit, onBack: () -> Unit, onEditExpense: (ExpenseId) -> Unit, viewModel: GroupViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarState = rememberAppSnackbarState()
    val actionFailedMessage = stringResource(Res.string.group_action_failed)
    val unknownMember = stringResource(Res.string.member_unknown)
    var isAddingMember by rememberSaveable { mutableStateOf(false) }
    var pendingSettlement by remember { mutableStateOf<Settlement?>(null) }
    var pendingTransfer by remember { mutableStateOf<Transfer?>(null) }
    val group = state.group
    val memberNames = state.memberNames
    val memberName: (MemberId) -> String = { memberId -> memberNames[memberId] ?: unknownMember }

    LaunchedEffect(state.isActionFailed) {
        if (state.isActionFailed) {
            snackbarState.showError(actionFailedMessage)
            viewModel.onEvent(GroupEvent.DismissActionFailure)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(AppColors.surfaceBase)) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(onBack = onBack, title = group?.name.orEmpty())
            if (group != null) {
                GroupContent(
                    modifier = Modifier.weight(1f),
                    memberName = memberName,
                    onAddMember = { isAddingMember = true },
                    onEditExpense = onEditExpense,
                    onSelectSettlement = { pendingSettlement = it },
                    onSelectTransfer = { pendingTransfer = it },
                    state = state,
                )
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(bottom = AppSpacing.lg, end = AppSpacing.screenPadding, start = AppSpacing.screenPadding, top = AppSpacing.sm),
                    label = stringResource(Res.string.action_add_expense),
                    onClick = onAddExpense,
                )
            }
        }
        AppSnackbarHost(modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding(), state = snackbarState)
    }

    if (isAddingMember && group != null) {
        AddMemberSheet(
            isNameAvailable = state::isMemberNameAvailable,
            onAdd = { name ->
                isAddingMember = false
                viewModel.onEvent(GroupEvent.AddMember(name))
            },
            onDismiss = { isAddingMember = false },
        )
    }

    pendingTransfer?.let { transfer ->
        ConfirmSheet(
            confirmLabel = stringResource(Res.string.action_record_payment),
            message = stringResource(Res.string.record_payment_message, memberName(transfer.from), memberName(transfer.to), MoneyFormat.format(transfer.amount)),
            onConfirm = {
                pendingTransfer = null
                viewModel.onEvent(GroupEvent.RecordTransfer(transfer))
            },
            onDismiss = { pendingTransfer = null },
            title = stringResource(Res.string.action_record_payment),
        )
    }

    pendingSettlement?.let { settlement ->
        ConfirmSheet(
            confirmLabel = stringResource(Res.string.action_delete_payment),
            isDestructive = true,
            message = stringResource(Res.string.delete_payment_message, memberName(settlement.from), memberName(settlement.to), MoneyFormat.format(settlement.amount)),
            onConfirm = {
                pendingSettlement = null
                viewModel.onEvent(GroupEvent.DeleteSettlement(settlement.id))
            },
            onDismiss = { pendingSettlement = null },
            title = stringResource(Res.string.action_delete_payment),
        )
    }
}

@Composable
private fun GroupContent(
    onAddMember: () -> Unit,
    onEditExpense: (ExpenseId) -> Unit,
    memberName: (MemberId) -> String,
    onSelectSettlement: (Settlement) -> Unit,
    onSelectTransfer: (Transfer) -> Unit,
    state: GroupUiState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxWidth(), contentPadding = PaddingValues(bottom = AppSpacing.lg, end = AppSpacing.screenPadding, start = AppSpacing.screenPadding, top = AppSpacing.lg)) {
        if (state.hasActivity) {
            balanceSection(balances = state.balances, memberName = memberName)
            settleUpSection(memberName = memberName, onSelectTransfer = onSelectTransfer, transfers = state.transfers)
        }
        expenseSection(expenses = state.expenses, memberName = memberName, onEditExpense = onEditExpense)
        paymentSection(memberName = memberName, onSelectSettlement = onSelectSettlement, settlements = state.settlements)
        item {
            SectionHeader(label = stringResource(Res.string.group_members_heading))
        }
        items(items = state.group?.members.orEmpty(), key = { member -> "member-${member.id.raw}" }) { member ->
            AppListTile(modifier = Modifier.padding(bottom = AppSpacing.sm), icon = Icons.Outlined.Person, title = member.name)
        }
        item {
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            SecondaryButton(modifier = Modifier.fillMaxWidth(), label = stringResource(Res.string.action_add_member), onClick = onAddMember)
        }
    }
}

@Composable
private fun AmountText(money: Money, modifier: Modifier = Modifier, color: Color = AppColors.textPrimary) {
    Text(modifier = modifier, maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTheme.textStyles.amount.copy(color = color), text = MoneyFormat.format(money))
}

private fun LazyListScope.balanceSection(balances: List<Balance>, memberName: (MemberId) -> String) {
    item {
        SectionHeader(label = stringResource(Res.string.group_balances_heading))
    }
    items(items = balances, key = { balance -> "balance-${balance.memberId.raw}" }) { balance ->
        val status = when {
            balance.net.isPositive -> stringResource(Res.string.balance_gets_back)
            balance.net.isNegative -> stringResource(Res.string.balance_owes)
            else -> stringResource(Res.string.balance_settled)
        }
        val color = when {
            balance.net.isPositive -> AppColors.success
            balance.net.isNegative -> AppColors.danger
            else -> AppColors.textSecondary
        }

        AppListTile(modifier = Modifier.padding(bottom = AppSpacing.sm), icon = Icons.Outlined.Person, subtitle = status, title = memberName(balance.memberId)) {
            AmountText(color = color, money = balance.net.absolute)
        }
    }
    item {
        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

private fun LazyListScope.expenseSection(expenses: List<Expense>, onEditExpense: (ExpenseId) -> Unit, memberName: (MemberId) -> String) {
    item {
        SectionHeader(label = stringResource(Res.string.group_expenses_heading))
    }
    if (expenses.isEmpty()) {
        item {
            EmptyState(
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                message = stringResource(Res.string.group_expenses_empty_message),
                title = stringResource(Res.string.group_expenses_empty_title),
            )
        }
    }
    items(items = expenses, key = { expense -> "expense-${expense.id.raw}" }) { expense ->
        AppListTile(
            modifier = Modifier.padding(bottom = AppSpacing.sm),
            icon = Icons.AutoMirrored.Outlined.ReceiptLong,
            onClick = { onEditExpense(expense.id) },
            subtitle = stringResource(Res.string.expense_tile_subtitle, memberName(expense.paidBy), DateFormat.formatShort(expense.spentAt)),
            title = expense.title,
        ) {
            AmountText(money = expense.amount)
        }
    }
    item {
        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

private fun LazyListScope.paymentSection(settlements: List<Settlement>, memberName: (MemberId) -> String, onSelectSettlement: (Settlement) -> Unit) {
    if (settlements.isEmpty()) return
    item {
        SectionHeader(label = stringResource(Res.string.group_payments_heading))
    }
    items(items = settlements, key = { settlement -> "settlement-${settlement.id.raw}" }) { settlement ->
        AppListTile(
            modifier = Modifier.padding(bottom = AppSpacing.sm),
            icon = Icons.Outlined.Payments,
            onClick = { onSelectSettlement(settlement) },
            subtitle = DateFormat.formatShort(settlement.settledAt),
            title = stringResource(Res.string.settlement_title, memberName(settlement.from), memberName(settlement.to)),
        ) {
            AmountText(money = settlement.amount)
        }
    }
    item {
        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

private fun LazyListScope.settleUpSection(transfers: List<Transfer>, memberName: (MemberId) -> String, onSelectTransfer: (Transfer) -> Unit) {
    if (transfers.isEmpty()) return
    item {
        SectionHeader(label = stringResource(Res.string.group_settle_up_heading))
    }
    items(items = transfers, key = { transfer -> "transfer-${transfer.from.raw}-${transfer.to.raw}" }) { transfer ->
        AppListTile(
            modifier = Modifier.padding(bottom = AppSpacing.sm),
            icon = Icons.Outlined.SwapHoriz,
            onClick = { onSelectTransfer(transfer) },
            subtitle = stringResource(Res.string.transfer_subtitle),
            title = stringResource(Res.string.transfer_title, memberName(transfer.from), memberName(transfer.to)),
        ) {
            AmountText(money = transfer.amount)
        }
    }
    item {
        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}
