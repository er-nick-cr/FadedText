package com.github.ernickcr.fadedtextapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.ernickcr.fadedtext.FadedText
import com.github.ernickcr.fadedtextapp.ui.theme.MyApplicationTheme
import com.github.ernickcr.fadedtextapp.ui.theme.WidgetPreview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            fontSize = 24.sp
                        )

                        InternalCard {
                            FadedText(
                                text = stringResource(R.string.app_demo),
                                maxLines = 1,
                            )
                        }

                        InternalCard {
                            FadedText(
                                text = stringResource(R.string.app_demo),
                                maxLines = 2,
                            )
                        }

                        InternalCard {
                            FadedText(
                                text = stringResource(R.string.app_demo),
                                maxLines = 4,
                            )
                        }

                        InternalCard {
                            FadedText(
                                text = stringResource(R.string.app_demo),
                                maxLines = 5,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InternalCard(
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .shadow(
                ambientColor = Color.Black.copy(alpha = 0.15F),
                spotColor = Color.Black.copy(alpha = 0.15F),
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp),
            )
            .clip(RoundedCornerShape(20.dp))
            .padding(PaddingValues(horizontal = 20.dp, vertical = 16.dp))
    ) {
        content()
    }
}


@WidgetPreview
@Composable
fun FadedTextPreview() {
    Column {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp)
        ) {
            InternalCard {
                FadedText(
                    text = stringResource(R.string.app_demo),
                    maxLines = 1,
                )
            }
        }
    }
}