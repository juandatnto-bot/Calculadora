package com.example.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.R
import com.example.calculator.model.HistoryEntry
import com.example.calculator.ui.components.asDisplayText

@Composable
fun HistoryScreen(
    history: List<HistoryEntry>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.calc_background))
            .safeDrawingPadding(),
    ) {
        HistoryHeader(onBack = onBack)
        if (history.isEmpty()) {
            EmptyHistory(modifier = Modifier.weight(1f))
        } else {
            HistoryList(history = history, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun HistoryHeader(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        BasicText(
            text = stringResource(R.string.back_to_calculator),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable(role = Role.Button, onClick = onBack)
                .padding(vertical = 12.dp),
            style = TextStyle(color = colorResource(R.color.calc_operator_background), fontSize = 18.sp),
        )
        BasicText(
            text = stringResource(R.string.history_title),
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                color = colorResource(R.color.calc_text_primary),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}

@Composable
private fun EmptyHistory(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        BasicText(
            text = stringResource(R.string.history_empty),
            style = TextStyle(color = colorResource(R.color.calc_text_secondary), fontSize = 18.sp),
        )
    }
}

@Composable
private fun HistoryList(history: List<HistoryEntry>, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    LaunchedEffect(history.size) {
        listState.scrollToItem(history.lastIndex)
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(history) { entry ->
            HistoryItem(entry)
        }
    }
}

@Composable
private fun HistoryItem(entry: HistoryEntry) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            BasicText(
                text = entry.expression.asDisplayText(),
                style = TextStyle(
                    color = colorResource(R.color.calc_text_secondary),
                    fontSize = 18.sp,
                    textAlign = TextAlign.End,
                ),
            )
        }
        BasicText(
            text = entry.result,
            style = TextStyle(
                color = colorResource(R.color.calc_text_primary),
                fontSize = 34.sp,
                textAlign = TextAlign.End,
            ),
        )
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(colorResource(R.color.calc_divider)),
        )
    }
}
