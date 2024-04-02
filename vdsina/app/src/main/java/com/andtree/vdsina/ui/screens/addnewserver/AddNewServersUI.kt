package com.andtree.vdsina.ui.screens.addnewserver


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.andtree.vdsina.data.model.BackupResponseData
import com.andtree.vdsina.data.model.CreateServerResponse
import com.andtree.vdsina.data.model.CreateServerResponseData
import com.andtree.vdsina.data.model.DatacenterResponseData
import com.andtree.vdsina.data.model.IsoImageResponseData
import com.andtree.vdsina.data.model.ServerGroupData
import com.andtree.vdsina.data.model.ServerPlanResponseData
import com.andtree.vdsina.data.model.SshKeyData
import com.andtree.vdsina.data.model.TemplatesData
import com.andtree.vdsina.ui.theme.CardBackgroundColor
import com.andtree.vdsina.ui.theme.White
import java.util.Locale

typealias SelectionHandler = (Int) -> Unit

@Composable
fun AddNewServersUI(
    datacenterData: List<DatacenterResponseData>,
    serverPlanData: List<ServerPlanResponseData>,
    templateData: List<TemplatesData>,
    sshKeyData: List<SshKeyData>,
    serverGroupData: List<ServerGroupData>,
    isoImageData: List<IsoImageResponseData>,
    backupData: List<BackupResponseData>,
    viewModel: AddNewServerViewModel
) {
    TextHead(text = "Операционная система", requariedField = true)
    ServerOrderCard() {
        LazyRow(
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            items(templateData) { item ->
                val textFields = listOf(
                    "ОС" to item.name,
                    "Свой SSH" to item.sshKey.toString(),
                    "Минимум CPU" to item.limits.cpu.min.toString(),
                    "Минимум RAM" to item.limits.ram.min.toString(),
                    "Минимум Disk" to item.limits.disk.min.toString()
                )
                DynamicLazyRowItems(
                    image = item.image,
                    textFields = textFields,
                    itemId = item.id,
                    selectedItemId = viewModel.selectedOsTemplate
                ) { itemId -> viewModel.onEvent(AddNewServerEvent.onOsTemplateSelected(itemId, item.limits.cpu.min, item.limits.ram.min)) }
            }
        }
    }
    if (viewModel.selectedOsTemplate.value !in viewModel.windowsOsTemplatesId) {
        if (sshKeyData.isNotEmpty()) {
            TextHead(text = "SSH ключ")
            ServerOrderCard() {
                LazyRow(
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    items(sshKeyData) {item ->
                        val textFields = listOf(
                            "Ключ" to item.name
                        )
                        DynamicLazyRowItems(
                            image = null,
                            textFields = textFields,
                            itemId = item.id,
                            selectedItemId = viewModel.selectedSshKeyId ,
                            onItemSelected = { itemId -> viewModel.onEvent(AddNewServerEvent.onSshKeyIdSelected(itemId)) }
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            viewModel.onEvent(AddNewServerEvent.onSshCleanClick)
                        },
                        modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                    ) {
                        Text(
                            text = "Снять выбор",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        } else {
            ServerOrderCard() {
                LazyRow(
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    item {
                        Text(
                            text = "Нет ssh ключей",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
    TextHead(text = "Резервная копия")
    ServerOrderCard() {
        LazyRow(
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
           if (backupData.isNotEmpty()) {
               items(backupData) { item ->
                   val textFields = listOf(
                       "Имя" to item.name,
                       "Статус" to item.status,
                       "!" to "взаимозаменяем параметром template"
                   )
                   DynamicLazyRowItems(
                       image = null,
                       textFields = textFields,
                       itemId = item.id,
                       selectedItemId = viewModel.selectedBackup,
                       onItemSelected = { itemId -> viewModel.onEvent(AddNewServerEvent.onBackupSelected(itemId)) }
                   )
               }
           } else {
               item {
                   Text(
                       text = "Список резервных копий пустой",
                       color = MaterialTheme.colorScheme.onSecondaryContainer
                   )
               }
           }
        }
    }
    TextHead(text = "ISO-образ")
    ServerOrderCard() {
        LazyRow(
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            if (isoImageData.isNotEmpty()) {
                items(isoImageData) { item ->
                    val textFields = listOf(
                        "Имя" to item.name,
                        "Статус" to item.status,
                        "Размер" to item.file.size,
                        "!" to "взаимозаменяем параметром template"
                    )
                    DynamicLazyRowItems(
                        image = null,
                        textFields = textFields,
                        itemId = item.id,
                        selectedItemId = viewModel.selectedIsoImage,
                        onItemSelected = { itemId -> viewModel.onEvent(AddNewServerEvent.onIsoImageSelected(itemId)) }
                    )
                }
            } else {
                item {
                    Text(
                        text = "Список ISO образов пустой",
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
    TextHead(text = "Тип сервера", requariedField = true)
    ServerOrderCard() {
        LazyRow(
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            items(serverGroupData) {item ->
                val textFields = listOf(
                    "Тип" to item.name
                )
                DynamicLazyRowItems(
                    image = item.image,
                    textFields = textFields,
                    itemId = item.id,
                    selectedItemId = viewModel.selectedServerGroupTemplate
                ) { itemId -> viewModel.onEvent(AddNewServerEvent.onServerGroupSelected(itemId))}
            }
        }
    }
    TextHead(text = "Конфигурация сервера", requariedField = true)
    ServerOrderCard() {
        LazyRow(
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            items(serverPlanData) { item ->
                if (item.serverGroup != 5 && item.serverGroup != 8){
                    val textFields = listOf(
                        "Память" to "${item.data.ram.value} ${item.data.ram.volumes}",
                        "Хранилище" to "${item.data.disk.value} ${item.data.disk.volumes}",
                        "Процессор" to "${item.data.cpu.value} ${item.data.cpu.volumes}",
                        "Трафик" to "${item.data.traff.value} ${item.data.traff.volumes}",
                        "Тариф" to "${item.cost.toString()} ₽ / ${item.period}"
                    )
                    if (viewModel.minCpuFromSelectedOsTemplate.value.toInt() <= item.data.cpu.value.toInt() && viewModel.minRamFromSelectedOsTemplate.value.toInt() <= item.data.ram.value.toInt()) {
                        DynamicLazyRowItems(
                            image = null,
                            textFields = textFields,
                            itemId = item.id,
                            selectedItemId = viewModel.selectedServerPlanTemplate
                        ) { itemId -> viewModel.onEvent(AddNewServerEvent.onServerPlanSelected(itemId)) }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .width(335.dp)
                            .padding(start = 8.dp, end = 8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ConfiguredServer(
                            title = "CPU",
                            sliderValue = viewModel.cpu.toFloat(),
                            onSliderValueChange = { coreNumber -> viewModel.onEvent(AddNewServerEvent.onCpuChange(coreNumber)) },
                            sliderRange = item.params.cpu.min.toFloat()..item.params.cpu.max.toFloat(),
                            rowModifier = Modifier.width(335.dp),
                            volumeType = item.params.cpu.volumes,
                            volumeMax = item.params.cpu.max.toString()
                        )
                        ConfiguredServer(
                            title = "RAM",
                            sliderValue = viewModel.ram.toFloat(),
                            onSliderValueChange = { ram -> viewModel.onEvent(AddNewServerEvent.onRamChange(ram)) },
                            sliderRange = item.params.ram.min.toFloat()..item.params.ram.max.toFloat(),
                            rowModifier = Modifier.width(335.dp),
                            volumeType = item.params.ram.volumes,
                            volumeMax = item.params.ram.max.toString()
                        )
                        ConfiguredServer(
                            title = "DISK",
                            sliderValue = viewModel.disk.toFloat(),
                            onSliderValueChange = { disk -> viewModel.onEvent(AddNewServerEvent.onDiskChange(disk)) },
                            sliderRange = 5f..item.params.disk.max.toFloat(),
                            rowModifier = Modifier.width(335.dp),
                            volumeType = item.params.disk.volumes,
                            volumeMax = item.params.disk.max.toString()
                        )
                        Row(
                            modifier = Modifier
                                .width(335.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Подключить IPv4")
                            Spacer(modifier = Modifier.width(5.dp))
                            Switch(
                                checked = viewModel.isIpChecked,
                                onCheckedChange = { viewModel.isIpChecked = it },
                                thumbContent = if (viewModel.isIpChecked) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Check icon",
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                } else {
                                    null
                                }
                            )
                        }
                        var isDrawRam: Boolean = false
                        var isDrawCpu: Boolean = false

                        if (viewModel.ram > item.params.ram.min) {
                            isDrawRam = true
                        }
                        if (viewModel.cpu > item.params.cpu.min) {
                            isDrawCpu = true
                        }
                        ConfiguredServerFooter(
                            dividerModifier = Modifier
                                .padding(top = 10.dp, bottom = 10.dp)
                                .width(335.dp),
                            rowModifier = Modifier
                                .width(335.dp),
                            basicTarifCost = item.cost.toInt().toString(),
                            basicTarifCostText =  "₽ / ${item.period}",
                            basicTarifDescribe = "cpu " +
                                    "${item.data.cpu.value} " +
                                    "${item.data.cpu.volumes}, " +
                                    "ram " +
                                    "${item.data.ram.value} " +
                                    "${item.data.ram.volumes}, " +
                                    "disk " +
                                    "${item.data.disk.value} " +
                                    "${item.data.disk.volumes}, " +
                                    "traffic " +
                                    "${item.data.traff.value} " +
                                    "${item.data.traff.volumes}",
                            diskText = "disk ${viewModel.disk - item.params.disk.min}" +
                                        "${item.params.disk.volumes} NVMe",
                            diskCost = "${String.format(Locale.US, "%.1f", item.params.disk.cost * (viewModel.disk - item.params.disk.min))}",
                            diskCostText = " ₽ / ${item.params.disk.period}",
                            isDrawCpu = isDrawCpu,
                            isDrawRam = isDrawRam,
                            ramText = "ram ${viewModel.ram - item.params.ram.min} ${item.data.ram.volumes} RAM",
                            ramCost = "${item.params.ram.cost * (viewModel.ram - item.params.ram.min)}",
                            ramCostText = " ₽ / ${item.params.ram.period}",
                            cpuText = "cpu ${viewModel.cpu - item.params.cpu.min} ${item.data.cpu.volumes}",
                            cpuCost = "${item.params.cpu.cost * (viewModel.cpu - item.params.cpu.min)}",
                            cpuCostText = " ₽ / ${item.params.cpu.period}",
                            isIpV4Enabled = viewModel.isIpChecked,
                            ipV4Text = "IPv4 1 шт",
                            ipV4Cost = "${item.params.ip4.cost}",
                            ipV4CostText = " ₽ / ${item.params.ip4.period}"
                        )
                    }
                }
            }
        }
    }
    TextHead(text = "Локация сервера", requariedField = true)
    ServerOrderCard() {
        LazyRow(
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            items(datacenterData) {item ->
                val textFields = listOf(
                    item.country.uppercase() to item.name
                )
                DynamicLazyRowItems(
                    image = null,
                    textFields = textFields,
                    itemId = item.id,
                    selectedItemId = viewModel.selectedDatacenter,
                    onItemSelected ={ itemId -> viewModel.onEvent(AddNewServerEvent.onDatacenterSelected(itemId)) }
                )
            }
        }
    }
    TextHead(text = "Дополнительные настройки")
    ServerOrderCard() {
        val focusManager = LocalFocusManager.current
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 15.dp, end = 18.dp),
            value = viewModel.host,
            onValueChange = { hostname -> viewModel.onEvent(AddNewServerEvent.onHostNameChange(hostname)) },
            placeholder = {
                Text(
                    text = "Hostname",
                    fontSize = 15.sp
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                unfocusedContainerColor = MaterialTheme.colorScheme.inverseOnSurface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 15.dp, end = 18.dp),
            value = viewModel.name,
            onValueChange = { serverName -> viewModel.onEvent(AddNewServerEvent.onNameChange(serverName)) },
            placeholder = {
                Text(
                    text = "Название",
                    fontSize = 15.sp
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                unfocusedContainerColor = MaterialTheme.colorScheme.inverseOnSurface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier
                    .padding(start = 16.dp, top = 10.dp, bottom = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = {
                    focusManager.clearFocus()
                }
            ) {
                Text(text = "Сохранить")
            }
            Button(
                modifier = Modifier
                    .padding(end = 16.dp, top = 10.dp, bottom = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = {
                    viewModel.onEvent(AddNewServerEvent.onClearButtomClick)
                }
            ) {
                Text(text = "Очистить")
            }
        }
    }
    Button(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(5.dp),
        onClick = {
            viewModel.onEvent(AddNewServerEvent.onCreationConfirm)
        }
    ) {
        Text(text = "Создать")
    }
}

@Composable
fun ConfiguredServerFooter(
    dividerModifier: Modifier,
    rowModifier: Modifier,
    basicTarifDescribe: String,
    basicTarifCost: String,
    basicTarifCostText: String,
    diskText: String,
    diskCost: String,
    diskCostText: String,
    isDrawRam: Boolean,
    isDrawCpu: Boolean,
    ramText: String,
    ramCost: String,
    ramCostText: String,
    cpuText: String,
    cpuCost: String,
    cpuCostText: String,
    isIpV4Enabled: Boolean,
    ipV4Text: String,
    ipV4Cost: String,
    ipV4CostText: String
) {
    Divider(dividerModifier)
    Row(
        rowModifier,
        horizontalArrangement = Arrangement.SpaceBetween
        ) {
        Text(
            text = "Базовый тариф",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "$basicTarifCost $basicTarifCostText",
            style = MaterialTheme.typography.labelLarge
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
    Row(rowModifier) {
        Text(
            text = basicTarifDescribe,
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Light
            )
        )
    }
    Divider(dividerModifier)
    Row(rowModifier) {
        Text(
            text = "Дополнительно:",
            style = MaterialTheme.typography.titleLarge
        )
    }
    Divider(dividerModifier)
    ConfiguredServerOptionalRow(
        rowModifier = rowModifier,
        textDescribe = diskText,
        textCost = "$diskCost $diskCostText"
    )
    Divider(dividerModifier)
    if (isDrawRam) {
        ConfiguredServerOptionalRow(
            rowModifier = rowModifier,
            textDescribe = ramText,
            textCost = "$ramCost $ramCostText"
        )
        Divider(dividerModifier)
    }
    if (isDrawCpu) {
        ConfiguredServerOptionalRow(
            rowModifier = rowModifier,
            textDescribe = cpuText,
            textCost = "$cpuCost $cpuCostText"
        )
        Divider(dividerModifier)
    }
    if (isIpV4Enabled) {
        ConfiguredServerOptionalRow(
            rowModifier = rowModifier,
            textDescribe = ipV4Text,
            textCost = "$ipV4Cost $ipV4CostText"
        )
        Divider(dividerModifier)
    }
    Row(
        rowModifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Итого:",
            style = MaterialTheme.typography.titleLarge
        )
        val costPerDay = diskCost.toFloat() + ramCost.toInt() + cpuCost.toInt() + basicTarifCost.toInt() + if (isIpV4Enabled) ipV4Cost.toInt() else 0
        Column(
        ){
            Text(
                text = String.format(Locale.US, "%.1f", costPerDay * 30) + "₽ / месяц",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
            Text(
                text = String.format(Locale.US, "%.1f", costPerDay) + "₽ / день",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
        }
    }
}

@Composable
fun ConfiguredServerOptionalRow(
    rowModifier: Modifier,
    textDescribe: String,
    textCost: String
) {
    Row(
        rowModifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = textDescribe,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = textCost,
            style = TextStyle(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun ConfiguredServer(
    title: String,
    sliderValue: Float,
    onSliderValueChange: (Int) -> Unit,
    sliderRange: ClosedFloatingPointRange<Float>,
    rowModifier: Modifier,
    volumeType: String,
    volumeMax: String
) {
    Row(
        rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Slider(
            value = sliderValue,
            onValueChange = { newValue -> onSliderValueChange(newValue.toInt()) },
            valueRange = sliderRange,
            colors = SliderDefaults.colors(
                thumbColor = Color.Green,
                activeTickColor = Color.White,
                activeTrackColor = Color.Green,
                inactiveTickColor = MaterialTheme.colorScheme.onSecondary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSecondary
            )
        )
    }
    Row(
        rowModifier
    ) {
        Text(
            text = sliderValue.toInt().toString(),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "${volumeType} / ${volumeMax}",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun ServerOrderCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 15.dp),
        shape = RoundedCornerShape(5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}

@Composable
fun TextHead(
    text: String,
    requariedField: Boolean? = false
){
    Column(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 15.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall
        )
        if (requariedField == true) {
            Text(
                text = "Обязательное поле",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

@Composable
fun formatValue(label: String, value: String): String {
    return when {
        label.contains("RAM", ignoreCase = true) || label.contains("Disk", ignoreCase = true) -> "$value GB"
        label.contains("SSH", ignoreCase = true) -> if (value.toBoolean()) "Да" else "Нет"
        else -> value
    }
}

@Composable
fun DynamicLazyRowItems(
    image: String?,
    textFields: List<Pair<String, String>>,
    itemId: Int,
    selectedItemId: State<Int>,
    onItemSelected: SelectionHandler
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .padding(start = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!image.isNullOrEmpty()) {
            AsyncImage(
                model = image,
                contentDescription = "Item icon",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
        }
        RadioButton(
            selected = selectedItemId.value == itemId,
            onClick = { onItemSelected(itemId) }
        )
        textFields.forEach { (label, value) ->
            Row {
                Text(
                    text = "$label: ",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = formatValue(label, value),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
