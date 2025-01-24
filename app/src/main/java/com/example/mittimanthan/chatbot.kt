package com.example.mittimanthan

import android.R.layout.simple_spinner_dropdown_item
import android.R.layout.simple_spinner_item
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.ChipGroup
import com.google.android.material.button.MaterialButton
import okhttp3.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import android.util.Log
import okhttp3.FormBody
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class ChatbotFragment : BottomSheetDialogFragment() {
    private lateinit var queryEditText: EditText
    private lateinit var languageSpinner: Spinner
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var faqChipGroup: ChipGroup
    private lateinit var sendButton: MaterialButton

    private val client = OkHttpClient()
    private val mapper = jacksonObjectMapper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chatbot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupSpinner()
        setupRecyclerView()
        setupClickListeners()
        showWelcomeMessage()
    }

    private fun initializeViews(view: View) {
        queryEditText = view.findViewById(R.id.queryEditText)
        languageSpinner = view.findViewById(R.id.languageSpinner)
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        faqChipGroup = view.findViewById(R.id.faqChipGroup)
        sendButton = view.findViewById(R.id.sendButton)
    }

    private fun setupSpinner() {
        val languages = arrayOf(
            "en",
            "hi",
            "mr",
            "tl"
        )
        val adapter = ArrayAdapter(requireContext(), simple_spinner_item, languages)
        adapter.setDropDownViewResource(simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        languageSpinner.setSelection(languages.indexOf("en"))
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }
    }

    private fun setupClickListeners() {
        sendButton.setOnClickListener {
            val query = queryEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                sendMessage(query)
            }
        }

        val chips = listOf(
            "Soil Organic" to "How do I measure soil organic carbon content??",
            "product" to "buy product",
            "ph" to "soil pH"
        )

        chips.forEach { (title, query) ->
            val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                text = title
                setOnClickListener {
                    queryEditText.setText(query)
                    sendMessage(query)
                }
            }
            faqChipGroup.addView(chip)
        }
    }

    private fun sendMessage(message: String) {
        chatAdapter.addMessage(ChatMessage(message, false))
        queryEditText.text.clear()
        chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)

        val query = message
        val language = languageSpinner.selectedItem.toString()

        val url = "https://mlnew-15.onrender.com/chat"  // Updated URL

        val client = OkHttpClient.Builder()
            .connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS)
            .writeTimeout(6, TimeUnit.SECONDS)
            .build()

        Log.d("ChatbotAPI", "Sending request - Query: $query, Language: $language")
        Log.d("ChatbotAPI", "URL: $url")  // Log the URL

        val formBody = FormBody.Builder()
            .add("query", query)
            .add("language", language)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        activity?.runOnUiThread {
            chatAdapter.addMessage(ChatMessage("Finding an answer...", true))
        }

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ChatbotAPI", "API call failed", e)
                activity?.runOnUiThread {
                    chatAdapter.removeLastMessage()
                    chatAdapter.addMessage(ChatMessage("Network error. Please try again.", true))
                    chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d("ChatbotAPI", "Response Code: ${response.code}")
                Log.d("ChatbotAPI", "Response Body: $responseData")

                activity?.runOnUiThread {
                    chatAdapter.removeLastMessage() // Remove loading message

                    if (response.isSuccessful && !responseData.isNullOrEmpty()) {
                        try {
                            val jsonResponse = JSONObject(responseData)

                            if (jsonResponse.has("error")) {
                                val errorMessage = jsonResponse.getString("error")
                                chatAdapter.addMessage(ChatMessage(errorMessage, true))
                            } else {
                                val answer = jsonResponse.getString("answer")
                                chatAdapter.addMessage(ChatMessage(answer, true))
                            }
                        } catch (e: Exception) {
                            Log.e("ChatbotAPI", "JSON parsing error", e)
                            chatAdapter.addMessage(ChatMessage("Error processing response", true))
                        }
                    } else {
                        val errorMessage = when (response.code) {
                            404 -> {
                                Log.e("ChatbotAPI", "Server not found at $url")
                                "Server not found. Please verify the API endpoint."
                            }
                            400 -> "Invalid request. Please try again."
                            500 -> "Server error. The translation service might be unavailable."
                            else -> "Error ${response.code}. Please try again."
                        }
                        chatAdapter.addMessage(ChatMessage(errorMessage, true))
                    }
                    chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
                }
            }
        })
    }

    private fun ChatAdapter.removeLastMessage() {
        if (itemCount > 0) {
            messages.removeAt(itemCount - 1)
            notifyItemRemoved(itemCount)
        }
    }

    private fun showWelcomeMessage() {
        chatAdapter.addMessage(ChatMessage("Hello! How can I help you today?", true))
    }
}
