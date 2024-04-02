package com.andtree.vdsina.ui.screens.singleserver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.andtree.vdsina.data.model.SingleServerResponseData
import com.andtree.vdsina.ui.theme.CardBackgroundColor

@Composable
fun SingleServerCard(
    singleServerData: SingleServerResponseData?
) {
    var autoprologOverride: String = ""
    if (singleServerData?.autoprolong != false) {
        autoprologOverride = "включено"
    } else {
        autoprologOverride = "выключено"
    }
    HeadTitle(text = "Общая информация", image = singleServerData?.template?.image)
    SingleCard() {
        //HeadTitle(text = "Общая информация", image = singleServerData?.template?.image)
        SingleServerInformation(text = "Имя сервера: ", serverInformation = singleServerData?.name ?: "")
        SingleServerInformation(text = "ID сервера: ", serverInformation = singleServerData?.id.toString() ?: "")
        SingleServerInformation(text = "Статус: ", serverInformation = singleServerData?.status ?: "")
        SingleServerInformation(text = "Дата создания: ", serverInformation = singleServerData?.created ?: "")
        SingleServerInformation(text = "Последнее обновление: ", serverInformation = singleServerData?.updated ?: "")
        SingleServerInformation(text = "Автопродление: ", serverInformation = autoprologOverride)
    }
    HeadTitle(text = "Параметры сети")
    SingleCard() {
        singleServerData?.ip?.forEach{ ipData ->
            SingleServerInformation(text = "IP адрес: ", serverInformation = ipData.ip)
            SingleServerInformation(text = "Имя хоста: ", serverInformation = ipData.host)
            SingleServerInformation(text = "Шлюз: ", serverInformation = ipData.gateway)
            SingleServerInformation(text = "Маска сети: ", serverInformation = ipData.netmask)
            SingleServerInformation(text = "MAC адрес: ", serverInformation = ipData.mac)
        }
    }
    HeadTitle(text = "Параметры сервера")
    SingleCard() {
        SingleServerInformation(text = "CPU: ", serverInformation = "${singleServerData?.data?.cpu?.value} ${singleServerData?.data?.cpu?.volumeType}")
        SingleServerInformation(text = "RAM: ", serverInformation = "${singleServerData?.data?.ram?.value} ${singleServerData?.data?.ram?.volumeType}")
        SingleServerInformation(text = "Disk: ", serverInformation = "${singleServerData?.data?.disk?.value} ${singleServerData?.data?.disk?.volumeType}")
        SingleServerInformation(text = "Трафик: ", serverInformation = "${singleServerData?.data?.traff?.value} ${singleServerData?.data?.traff?.volumeType}")
    }
    HeadTitle(text = "Тарифные планы")
    SingleCard() {
        SingleServerInformation(text = "Группа серверов: ", serverInformation = singleServerData?.serverGroup?.name ?: "")
        SingleServerInformation(text = "Шаблон ОС: ", serverInformation = singleServerData?.template?.name ?: "")
        SingleServerInformation(text = "Датацентр: ", serverInformation = singleServerData?.datacenter?.name ?: "")
    }
}

@Composable
fun SingleCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 15.dp),
        shape = RoundedCornerShape(5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        content()
    }
}

@Composable
fun HeadTitle(
    text: String,
    image: String? = null
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 15.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall
        )
        image?.let {
            AsyncImage(
                model = it,
                contentDescription = "OS image",
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .align(Alignment.CenterEnd)
            )
        }
    }
}


@Composable
fun SingleServerInformation(
    text: String,
    serverInformation: String
) {
    Row(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        SelectionContainer(modifier = Modifier.weight(1f)) {
            Text(
                text = text,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        SelectionContainer(modifier = Modifier.weight(1f)) {
            Text(
                text = serverInformation,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer
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
