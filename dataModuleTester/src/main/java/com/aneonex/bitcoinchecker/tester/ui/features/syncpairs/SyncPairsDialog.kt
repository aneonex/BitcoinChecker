package com.aneonex.bitcoinchecker.tester.ui.features.syncpairs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aneonex.bitcoinchecker.datamodule.util.FormatUtilsBase
import com.aneonex.bitcoinchecker.tester.R
import com.aneonex.bitcoinchecker.tester.domain.model.MyMarketPairsInfo
import com.aneonex.bitcoinchecker.tester.ui.features.markettest.dto.MarketPairsUpdateState

@Composable
fun SyncPairsDialog(
    currencyPairsInfo: MyMarketPairsInfo,
    resultState: State<MarketPairsUpdateState>,
    onDismiss: () -> Unit,
    onSyncClick: () -> Unit
) {
    AlertDialog(
//        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = {
//                openDialog.value = false
            onDismiss()
        },
        title = {
            Column {
                Text(text = stringResource(R.string.checker_add_dynamic_currency_pairs_dialog_title))
                Text(
                    text = stringResource(R.string.checker_add_check_currency_empty_warning_summary),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !resultState.value.isInProgress,
                    onClick = {
                        onSyncClick()
                    }) {
                    Text(stringResource(R.string.checker_add_dynamic_currency_pairs_dialog_synchronize))
                }

                val lastSyncDateText =
                    if(currencyPairsInfo.lastSyncDate > 0)
                        FormatUtilsBase.formatSameDayTimeOrDate(LocalContext.current, currencyPairsInfo.lastSyncDate)
                    else stringResource(id = R.string.checker_add_dynamic_currency_pairs_dialog_last_sync_never)

                Text(
                    text = stringResource(id = R.string.checker_add_dynamic_currency_pairs_dialog_last_sync, lastSyncDateText)
                )
                Text("Currency pairs: ${currencyPairsInfo.size}")
                resultState.value.error?.also {
                    Text(stringResource(R.string.check_error_generic_prefix, it))
                }
            }
        },
        confirmButton = {
            Button(
//                    modifier = Modifier.fillMaxWidth(),
                onClick = {
//                            openDialog.value = false
                    onDismiss()
                }
            ) {
                Text(stringResource(android.R.string.ok))
            }
        }
    )
}
