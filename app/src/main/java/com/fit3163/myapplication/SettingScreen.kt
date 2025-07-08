package com.fit3163.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


class SettingScreen {
    @Composable
    fun MainSettingScreenFunction(navController: NavHostController) {

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
//            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.height(40.dp))
            Text("Settings", fontSize = 32.sp, fontWeight = FontWeight.Bold )
            Spacer(modifier = Modifier.height(40.dp))



            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable{navController.navigate("Account Settings")}
            ){

                Image(
                    painter = painterResource(id = R.drawable.personwithoutbackground), // Replace 'your_png_name'
                    contentDescription = "Account Icon",
                    modifier = Modifier.size(25.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))
                Text("Account Settings", fontSize = 24.sp, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(108.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )

            }

//            Row {
//                Image(
//                    painter = painterResource(id = R.drawable.baseline_display_settings_24), // Replace 'your_png_name'
//                    contentDescription = "Account Icon",
//                    modifier = Modifier.size(25.dp)
//                )
//                Spacer(modifier = Modifier.height(8.dp).width(8.dp))
//                Text("Appearance", fontSize = 24.sp)
//            }






        }
    }
}