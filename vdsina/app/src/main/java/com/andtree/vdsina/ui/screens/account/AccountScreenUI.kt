package com.andtree.vdsina.ui.screens.account

import android.content.ClipData.Item
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.common.extensions.isNotNull
import com.andtree.vdsina.R
import com.andtree.vdsina.data.model.account.AccountLimitsData
import com.andtree.vdsina.data.model.account.AccountOperationData
import com.andtree.vdsina.data.model.account.AccountSelectedOperationData
import com.andtree.vdsina.daterangepicker.MainDatePickerColors
import com.andtree.vdsina.daterangepicker.MainDateRangePicker
import com.andtree.vdsina.ui.components.MainModalButtonSheet
import com.andtree.vdsina.utils.converRangeDateToHumanReadable
import com.andtree.vdsina.utils.getCurrentDateInDateFormat
import com.andtree.vdsina.utils.getLastDateInDateFormat
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreenUI(
    viewModel: AccountScreenViewModel,
    accountLimits: AccountLimitsData?,
    accountOperations: List<AccountOperationData>,
    accountSingleOperation: AccountSelectedOperationData?
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    LazyRow(
        Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp)
    ){
        item {
            Button(
                onClick = { viewModel.onEvent(AccountScreenEvent.onLimitsProfileClick) },
                modifier = Modifier
                    .padding(top = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 15.dp)
            ) {
                Text(text = "Лимиты профиля")
            }
            if (accountOperations.isNotEmpty()) {
                Button(
                    onClick = { viewModel.onEvent(AccountScreenEvent.onDateRangeClick) },
                    modifier = Modifier
                        .padding(start = 10.dp, top = 15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 10.dp, horizontal = 15.dp)
                ) {
                    if (viewModel.toDate.value.isNotEmpty()) {
                        Text(text = converRangeDateToHumanReadable(viewModel.fromDate.value, viewModel.toDate.value))
                    } else {
                        Text(text = "Период")
                    }
                }
            }
            if (viewModel.toDate.value.isNotEmpty()) {
                Button(
                    onClick = { viewModel.onEvent(AccountScreenEvent.onClearClick) },
                    modifier = Modifier
                        .padding(start = 10.dp, top = 15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 10.dp, horizontal = 15.dp)
                ) {
                    Text(text = "Сбросить")
                }
            }

        }
    }
    if (accountOperations.isNotEmpty()) {
        TextHead(text = "Операции по аккаунту")
        AccountOperation(
            viewModel = viewModel,
            accountOperations = accountOperations
        )
    } else {
        TextHead(text = "Не найдено операций")
    }
    if (viewModel.showBottomSheet.value) {
        LaunchedEffect(key1 = viewModel.showBottomSheet.value) {
            scope.launch {
                sheetState.show()
            }
        }
        MainModalButtonSheet(
            scope = scope,
            sheetState = sheetState,
            showButtonSheet = viewModel.showBottomSheet
        ) {
            if (viewModel.buttomSheetType.value == "limits"){
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 5.dp, end = 5.dp, top = 15.dp),
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            CardInformation(title = "Сервера", text = "Доступно:", cardInformation = accountLimits?.server?.max.toString() + " шт.")
                            CardInformation(text = "Текущее количество:", cardInformation = accountLimits?.server?.now.toString() + " шт.")
                            CardInformation(title = "IPv4", text = "Доступно:", cardInformation = accountLimits?.serverIp4?.max.toString() + " шт.")
                            CardInformation(text = "Доступно для одного сервера:", cardInformation = accountLimits?.serverIp4?.childMax.toString() + " шт.")
                            CardInformation(text = "Текущее количество:", cardInformation = accountLimits?.serverIp4?.now.toString() + " шт.")
                            CardInformation(title = "IPv6", text = "Доступно:", cardInformation = accountLimits?.serverIp6?.max.toString() + " шт.")
                            CardInformation(text = "Доступно для одного сервера:", cardInformation = accountLimits?.serverIp6?.childMax.toString() + " шт.")
                            CardInformation(text = "Текущее количество:", cardInformation = accountLimits?.serverIp6?.now.toString() + " шт.")
                            CardInformation(title = "ISO", text = "Доступно:", cardInformation = accountLimits?.iso?.max.toString() + " шт.")
                            CardInformation(text = "Текущее количество:", cardInformation = accountLimits?.iso?.now.toString() + " шт.")
                            CardInformation(title = "Резервная копия", text = "Доступно:", cardInformation = accountLimits?.backup?.max.toString() + " шт.")
                            CardInformation(text = "Текущее количество:", cardInformation = accountLimits?.backup?.now.toString() + " шт.")
                            CardInformation(title = "Домены", text = "Доступно:", cardInformation = accountLimits?.domain?.max.toString() + " шт.")
                            CardInformation(text = "Текущее количество:", cardInformation = accountLimits?.domain?.now.toString() + " шт.")
                            CardInformation(title = "DNS", text = "Доступно:", cardInformation = accountLimits?.dns?.max.toString() + " шт.")
                            CardInformation(text = "Текущее количество:", cardInformation = accountLimits?.dns?.now.toString() + " шт.")
                            CardInformation(title = "HDD диски", text = "Доступно:", cardInformation = accountLimits?.extDiskHdd?.max.toString() + " шт.")
                            CardInformation(text = "Текущее количество:", cardInformation = accountLimits?.extDiskHdd?.now.toString() + " шт.")
                            CardInformation(title = "NVMe диски", text = "Доступно:", cardInformation = accountLimits?.extDiskNvme?.max.toString() + " шт.")
                            CardInformation(text = "Текущее количество:", cardInformation = accountLimits?.extDiskNvme?.now.toString() + " шт.")
                            CardInformation(title = "Резервные IP", text = "Доступно:", cardInformation = accountLimits?.reserveIp?.max.toString() + " шт.")
                            CardInformation(text = "Текущее количество:", cardInformation = accountLimits?.reserveIp?.now.toString() + " шт.")
                        }
                    }
                }
            } else if (viewModel.buttomSheetType.value == "operation") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp, end = 5.dp, top = 15.dp, bottom = 10.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    CardInformation(text = "ID операции:", cardInformation = accountSingleOperation?.id.toString())
                    CardInformation(text = "Сумма:", cardInformation = accountSingleOperation?.summ.toString())
                    CardInformation(text = "Создано:", cardInformation = accountSingleOperation?.created.toString())
                    CardInformation(text = "Обновлено:", cardInformation = accountSingleOperation?.updated.toString())
                    CardInformation(text = "Детали:", cardInformation = accountSingleOperation?.comment.toString())
                    if (accountSingleOperation?.payment != null){
                        CardInformation(text = "Тип платежа:", cardInformation = accountSingleOperation?.payment?.type.toString())
                        CardInformation(text = "Способ оплаты:", cardInformation = accountSingleOperation?.payment?.name.toString())
                    }
                    if (accountSingleOperation?.paylink != null){
                        CardInformation(text = "Ссылка на оплату:", cardInformation = accountSingleOperation?.paylink.toString())
                    }
                }
            } else if (viewModel.buttomSheetType.value == "date") {
                val minPickerDate = Calendar.getInstance()
                minPickerDate.add(Calendar.YEAR, 5)
                val maxPickerDate = getCurrentDateInDateFormat()
                MainDateRangePicker(
                    maxDate = maxPickerDate,
                    startDate = viewModel.startDateForPicker,
                    endDate = viewModel.endDateForPicker,
                    colors = MainDatePickerColors.defaults(
                        selectedDayBackgroundColor = MaterialTheme.colorScheme.inversePrimary
                    )
                )
            }
        }
    }
}

@Composable
fun AccountOperation(
    viewModel: AccountScreenViewModel,
    accountOperations: List<AccountOperationData>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 70.dp)
    ) {
        items(accountOperations) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 15.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .clickable {
                            viewModel.onEvent(
                                AccountScreenEvent.onSingleOperationClick(
                                    item.id
                                )
                            )
                        }
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.date_range_icon),
                            contentDescription = "date",
                            modifier = Modifier
                                .padding(end = 5.dp)
                        )
                        Text(
                            text = item.created,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(0f, 1f)
                                )
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Text(
                        text = item.comment,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Счет: ",
                            style = TextStyle(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = item.purse,
                            style = TextStyle(
                                fontSize = 17.sp
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Сумма: ",
                            style = TextStyle(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = item.summ.toString(),
                            style = TextStyle(
                                fontSize = 17.sp
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TextHead(
    text: String
){
    Column(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 15.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun CardInformation(
    title: String? = null,
    text: String,
    cardInformation: String
) {
    //val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    if (title != null) {
        Text(
            text = title,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .padding(start = 5.dp, top = 5.dp)
        )
    }
    Row(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        if (cardInformation.startsWith("http://") || cardInformation.startsWith("https://")) {
            Text(
                text = cardInformation,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    textDecoration = TextDecoration.Underline
                ),
                color = Color.Blue,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable { uriHandler.openUri(cardInformation) }
            )
        } else {
            Text(
                text = cardInformation,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp),
        color = MaterialTheme.colorScheme.onSecondary
    )
}