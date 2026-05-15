package com.example.raktaa_vahini.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.raktaa_vahini.data.BloodGroup
import com.example.raktaa_vahini.data.Donor
import com.example.raktaa_vahini.ui.viewmodel.DonorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencySearchScreen(viewModel: DonorViewModel) {
    val donors by viewModel.eligibleDonors.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Emergency Search", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Find eligible donors nearby immediately.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Search Bar Trigger
        OutlinedTextField(
            value = "",
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Search for donors (Group, Area, Pincode...)") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true },
            enabled = false, // To make the whole field clickable without focusing
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (donors.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No matching eligible donors found.")
            }
        } else {
            LazyColumn {
                items(donors) { donor ->
                    DonorCard(donor = donor) {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${donor.phoneNumber}")
                        }
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    if (showDialog) {
        SearchCriteriaDialog(
            onDismiss = { showDialog = false },
            onSearch = { bloodGroup, area, district, pincode ->
                viewModel.searchDonors(bloodGroup, area, district, pincode)
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCriteriaDialog(
    onDismiss: () -> Unit,
    onSearch: (BloodGroup, String, String, String) -> Unit
) {
    var selectedGroup by remember { mutableStateOf(BloodGroup.O_POSITIVE) }
    var area by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Search Criteria") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedGroup.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Blood Group") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        BloodGroup.entries.forEach { group ->
                            DropdownMenuItem(
                                text = { Text(group.displayName) },
                                onClick = {
                                    selectedGroup = group
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("Area / Village") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = district,
                    onValueChange = { district = it },
                    label = { Text("District") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = pincode,
                    onValueChange = { if (it.all { char -> char.isDigit() }) pincode = it },
                    label = { Text("Pincode") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSearch(selectedGroup, area, district, pincode)
            }) {
                Text("Search")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DonorCard(donor: Donor, onCallClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = donor.name, style = MaterialTheme.typography.titleLarge)
                Text(text = donor.phoneNumber, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Group: ${donor.bloodGroup.displayName} | ${donor.area}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onCallClick) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call Donor",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
