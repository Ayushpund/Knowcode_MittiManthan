package com.example.mittimanthan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mittimanthan.databinding.FragmentDashboardBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Dashboard : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        setupClickListeners()
        setupInitialData()
        return binding.root
    }

    private fun setupClickListeners() { // Quick Report Card click listener
        binding.btnGenerateReport.setOnClickListener {
            try {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Quick Report")
                    .setMessage(generateQuickReport())
                    .setPositiveButton("Download") { _, _ ->
                        Toast.makeText(context, "Report downloaded", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Close", null)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error generating report", Toast.LENGTH_SHORT).show()
            }
        }

        binding.chatbotFab.setOnClickListener {
            try {
                showLanguageSelectionDialog()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error opening language selection: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.downloadReportButton.setOnClickListener {
            try {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Download Report")
                    .setMessage(generateDetailedReport())
                    .setPositiveButton("Download") { _, _ ->
                        Toast.makeText(context, "Report downloaded", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Close", null)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error downloading report", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDetailedReport.setOnClickListener {
            try {
                showDetailedReportDialog()
            } catch (e: Exception) {
                Toast.makeText(context, "Error showing detailed report", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupInitialData() {
        binding.locationText.text = "Farm Location: Nashik"

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        binding.dateText.text = "Date: ${dateFormat.format(Date())}"

        updateSoilHealth(78)
        updateSoilParameters(
            ph = 6.5f,
            nutrients = "14-10-8",
            moisture = 65,
            temperature = 26.0f
        )
    }

    private fun generateQuickReport(): String {
        return """
            Quick Soil Health Report
            ${binding.dateText.text}
            ${binding.locationText.text}
            
            Overall Health: ${binding.soilHealthStatus.text}
            ${binding.soilHealthDescription.text}
            
            Key Parameters:
            - pH: ${binding.phValue.text}
            - Temperature: ${binding.temperatureValue.text}
            - Moisture: ${binding.moistureValue.text}
        """.trimIndent()
    }

    private fun generateDetailedReport(): String {
        return """
            Detailed Soil Health Report
            ${binding.dateText.text}
            ${binding.locationText.text}
            
            Health Status: ${binding.soilHealthStatus.text}
            ${binding.soilHealthDescription.text}
            
            Parameters:
            - pH Level: ${binding.phValue.text}
            - Temperature: ${binding.temperatureValue.text}
            - Moisture: ${binding.moistureValue.text}
            
            Nutrient Levels:
            - Nitrogen: ${binding.nutrientsValue.text} mg/kg
            - Phosphorus: ${binding.phosphorusValue.text} mg/kg
            - Potassium: ${binding.potassiumValue.text} mg/kg
        """.trimIndent()
    }

    private fun updateSoilHealth(healthScore: Int) {
        binding.soilHealthStatus.text = "Good $healthScore"
        binding.healthIndicator.progress = healthScore
        binding.moistureProgress.progress = healthScore

        val healthDescription = when {
            healthScore >= 80 -> "Excellent soil conditions for crop growth"
            healthScore >= 60 -> "Good soil conditions for crop growth"
            healthScore >= 40 -> "Moderate soil conditions, some improvements needed"
            else -> "Poor soil conditions, immediate attention required"
        }
        binding.soilHealthDescription.text = healthDescription
    }

    private fun updateSoilParameters(
        ph: Float,
        nutrients: String,
        moisture: Int,
        temperature: Float
    ) {
        binding.phValue.text = String.format("%.1f", ph)

        val npkValues = nutrients.split("-")
        if (npkValues.size == 3) {
            binding.nutrientsValue.text = npkValues[0]
            binding.phosphorusValue.text = npkValues[1]
            binding.potassiumValue.text = npkValues[2]
        }

        binding.moistureValue.text = "$moisture%"
        binding.temperatureValue.text = String.format("%.1f°C", temperature)

        val healthScore = calculateHealthScore(ph, nutrients, moisture, temperature)
        updateSoilHealth(healthScore)
    }

    private fun calculateHealthScore(
        ph: Float,
        nutrients: String,
        moisture: Int,
        temperature: Float
    ): Int {
        var score = 0

        score += when {
            ph in 6.0..7.5 -> 25
            ph in 5.5..8.0 -> 15
            else -> 5
        }

        score += when {
            moisture in 60..80 -> 25
            moisture in 40..90 -> 15
            else -> 5
        }

        score += when {
            temperature in 20f..30f -> 25
            temperature in 15f..35f -> 15
            else -> 5
        }

        val npkValues = nutrients.split("-").map { it.toIntOrNull() ?: 0 }
        score += when {
            npkValues.all { it >= 10 } -> 25
            npkValues.all { it >= 5 } -> 15
            else -> 5
        }

        return score
    }

    private fun showDetailedReportDialog() {
        val report = """
            Detailed Analysis:
            
            Soil Health: ${binding.soilHealthStatus.text}
            Description: ${binding.soilHealthDescription.text}
            
            Parameters:
            - pH Level: ${binding.phValue.text}
            - Temperature: ${binding.temperatureValue.text}
            - Moisture: ${binding.moistureValue.text}
            
            Nutrient Levels:
            - Nitrogen: ${binding.nutrientsValue.text} mg/kg
            - Phosphorus: ${binding.phosphorusValue.text} mg/kg
            - Potassium: ${binding.potassiumValue.text} mg/kg
        """.trimIndent()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Detailed Analysis")
            .setMessage(report)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("English")
        var selectedLanguage = 0

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Choose Language / भाषा निवडा")
            .setSingleChoiceItems(languages, selectedLanguage) { _, which ->
                selectedLanguage = which
            }
            .setPositiveButton("Continue") { _, _ ->
                // Launch ChatbotFragment with selected language
                val chatbotFragment = ChatbotFragment().apply {
                    arguments = Bundle().apply {
                        putString("selected_language", languages[selectedLanguage])
                    }
                }
                chatbotFragment.show(parentFragmentManager, "chatbot_dialog")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}