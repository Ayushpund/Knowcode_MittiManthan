package com.example.mittimanthan
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class CropPrediction : Fragment() {
    private lateinit var etN: EditText
    private lateinit var etP: EditText
    private lateinit var etK: EditText
    private lateinit var etPH: EditText
    private lateinit var etSoilType: EditText
    private lateinit var btnPredict: Button
    private lateinit var tvResult: TextView
    private lateinit var resultCard: CardView
    private lateinit var cropImage: ImageView
    private lateinit var tvPlantingSeason: TextView
    private lateinit var tvWaterRequirement: TextView
    private lateinit var tvSoilType: TextView
    private lateinit var tvNutrients: TextView

    companion object {
        private const val API_URL = "https://mlnew-10.onrender.com/predict"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_crop_prediction, container, false)
        initializeViews(rootView)
        setupClickListeners()
        return rootView
    }

    private fun initializeViews(view: View) {
        etN = view.findViewById(R.id.etN)
        etP = view.findViewById(R.id.etP)
        etK = view.findViewById(R.id.etK)
        etPH = view.findViewById(R.id.etPH)
        etSoilType = view.findViewById(R.id.etSoilType)
        btnPredict = view.findViewById(R.id.btnPredict)
        tvResult = view.findViewById(R.id.tvResult)
        resultCard = view.findViewById(R.id.resultCard)
        cropImage = view.findViewById(R.id.cropImage)
        tvPlantingSeason = view.findViewById(R.id.tvPlantingSeason)
        tvWaterRequirement = view.findViewById(R.id.tvWaterRequirement)
        tvSoilType = view.findViewById(R.id.tvSoilType)
        tvNutrients = view.findViewById(R.id.tvNutrients)

        resultCard.visibility = View.GONE
    }

    private fun setupClickListeners() {
        btnPredict.setOnClickListener {
            if (validateInputs()) {
                predictCrop(
                    etN.text.toString(),
                    etP.text.toString(),
                    etK.text.toString(),
                    etPH.text.toString(),
                    etSoilType.text.toString()
                )
            }
        }
    }

    private fun validateInputs(): Boolean {
        if (etN.text.isBlank() || etP.text.isBlank() ||
            etK.text.isBlank() || etPH.text.isBlank() ||
            etSoilType.text.isBlank()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            val n = etN.text.toString().toFloat()
            val p = etP.text.toString().toFloat()
            val k = etK.text.toString().toFloat()
            val ph = etPH.text.toString().toFloat()

            if (n < 0 || p < 0 || k < 0) {
                Toast.makeText(requireContext(), "N, P, K values must be positive", Toast.LENGTH_SHORT).show()
                return false
            }

            if (ph < 0 || ph > 14) {
                Toast.makeText(requireContext(), "pH must be between 0 and 14", Toast.LENGTH_SHORT).show()
                return false
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun predictCrop(N: String, P: String, K: String, pH: String, soilType: String) {
        tvResult.text = "Predicting..."
        btnPredict.isEnabled = false
        resultCard.visibility = View.GONE

        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("N", N)
            .add("P", P)
            .add("K", K)
            .add("pH", pH)
            .add("Soil_type", soilType)
            .build()

        val request = Request.Builder()
            .url(API_URL)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    tvResult.text = "Error occurred"
                    btnPredict.isEnabled = true
                    Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                activity?.runOnUiThread {
                    btnPredict.isEnabled = true
                }

                if (response.isSuccessful) {
                    try {
                        val responseData = response.body?.string()
                        val jsonResponse = JSONObject(responseData!!)
                        val predictedCrop = jsonResponse.getString("PredictedCrop")

                        activity?.runOnUiThread {
                            displayCropResult(predictedCrop, N, P, K, pH, soilType)
                        }
                    } catch (e: Exception) {
                        activity?.runOnUiThread {
                            tvResult.text = "Error parsing response"
                            Toast.makeText(context, "Error parsing response: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    val errorMessage = try {
                        val errorBody = response.body?.string()
                        val jsonError = JSONObject(errorBody!!)
                        jsonError.getString("error")
                    } catch (e: Exception) {
                        "Failed to predict crop (${response.code})"
                    }

                    activity?.runOnUiThread {
                        tvResult.text = "Prediction failed"
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun displayCropResult(crop: String, N: String, P: String, K: String, pH: String, soilType: String) {
        tvResult.text = "Predicted Crop: $crop"
        resultCard.visibility = View.VISIBLE

        cropImage.setImageResource(getCropImageResource(crop))

        tvPlantingSeason.text = "Planting Season: ${getCropSeason(crop)}"
        tvWaterRequirement.text = "Water Requirement: ${getCropWaterRequirement(crop)}"
        tvSoilType.text = "Soil Type: $soilType"
        tvNutrients.text = "Nutrients: N=$N, P=$P, K=$K, pH=$pH"
    }

    private fun getCropImageResource(cropName: String): Int {
        return when (cropName.lowercase()) {
            "rice" -> R.drawable.rice
            "wheat" -> R.drawable.wheat
            "maize" -> R.drawable.maize
            "cotton" -> R.drawable.cotton
            "watermelon" -> R.drawable.watermelon
            "banana" -> R.drawable.banana
            "mango" -> R.drawable.mango
            "grapes" -> R.drawable.grapes
            "orange" -> R.drawable.orange
            "apple" -> R.drawable.apple
            "papaya" -> R.drawable.papaya
            "coconut" -> R.drawable.coconut
            "jute" -> R.drawable.jute
            "coffee" -> R.drawable.cofee
            else -> R.drawable.crop // default crop icon
        }
    }

    private fun getCropSeason(cropName: String): String {
        return when (cropName.lowercase()) {
            "rice" -> "June-July (Kharif)"
            "wheat" -> "October-November (Rabi)"
            "maize" -> "June-July (Kharif)"
            "cotton" -> "March-May (Summer)"
            "sugarcane" -> "January-March"
            "banana" -> "June-July"
            "mango" -> "December-January"
            "grapes" -> "January-February"
            "orange" -> "June-July"
            "apple" -> "November-December"
            "papaya" -> "June-July"
            "coconut" -> "May-June"
            "jute" -> "March-April"
            "coffee" -> "November-December"
            else -> "Season varies by region"
        }
    }

    private fun getCropWaterRequirement(cropName: String): String {
        return when (cropName.lowercase()) {
            "rice" -> "High (150-300 cm)"
            "wheat" -> "Medium (45-100 cm)"
            "maize" -> "Medium (50-80 cm)"
            "cotton" -> "Medium (60-100 cm)"
            "sugarcane" -> "High (150-250 cm)"
            "banana" -> "High (120-180 cm)"
            "mango" -> "Medium (100-150 cm)"
            "grapes" -> "Medium (60-80 cm)"
            "orange" -> "Medium (80-120 cm)"
            "apple" -> "Medium (100-120 cm)"
            "papaya" -> "Medium (80-120 cm)"
            "coconut" -> "High (150-250 cm)"
            "jute" -> "High (120-180 cm)"
            "coffee" -> "Medium (150-200 cm)"
            else -> "Requirement varies"
        }
    }
}
