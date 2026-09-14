package org.example.evenly.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import evenly.shared.generated.resources.Res
import evenly.shared.generated.resources.currency_name_aed
import evenly.shared.generated.resources.currency_name_aud
import evenly.shared.generated.resources.currency_name_eur
import evenly.shared.generated.resources.currency_name_gbp
import evenly.shared.generated.resources.currency_name_inr
import evenly.shared.generated.resources.currency_name_jpy
import evenly.shared.generated.resources.currency_name_lkr
import evenly.shared.generated.resources.currency_name_usd
import org.example.evenly.model.Currency
import org.example.evenly.ui.theme.AppColors
import org.example.evenly.ui.theme.AppSpacing
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPickerSheet(title: String, onDismiss: () -> Unit, onSelect: (Currency) -> Unit, selected: Currency?, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(modifier = modifier, containerColor = AppColors.surfaceCard, onDismissRequest = onDismiss, sheetState = sheetState) {
        SheetFrame(title = title) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                Currency.entries.forEach { currency ->
                    AppListTile(
                        icon = Icons.Outlined.CurrencyExchange,
                        onClick = { sheetState.hideThen(action = { onSelect(currency) }, scope = scope) },
                        subtitle = currencyName(currency),
                        title = currency.name,
                    ) {
                        if (currency == selected) {
                            Icon(contentDescription = null, imageVector = Icons.Outlined.Check, tint = AppColors.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun currencyName(currency: Currency): String = stringResource(
    when (currency) {
        Currency.AED -> Res.string.currency_name_aed
        Currency.AUD -> Res.string.currency_name_aud
        Currency.EUR -> Res.string.currency_name_eur
        Currency.GBP -> Res.string.currency_name_gbp
        Currency.INR -> Res.string.currency_name_inr
        Currency.JPY -> Res.string.currency_name_jpy
        Currency.LKR -> Res.string.currency_name_lkr
        Currency.USD -> Res.string.currency_name_usd
    },
)
