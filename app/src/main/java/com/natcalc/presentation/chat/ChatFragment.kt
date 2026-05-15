package com.natcalc.presentation.chat

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.natcalc.R
import com.natcalc.databinding.FragmentChatBinding
import com.natcalc.presentation.chat.adapter.MessageAdapter
import com.natcalc.presentation.chat.adapter.toChatRows
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private val adapter = MessageAdapter()

    private val speechLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val text = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull() ?: return@registerForActivityResult
            binding.etInput.setText(text)
            binding.etInput.setSelection(text.length)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupInputArea()
        observeViewModel()
        setupToolbar()
    }

    private fun setupRecyclerView() {
        binding.rvMessages.apply {
            this.adapter = this@ChatFragment.adapter
            layoutManager = LinearLayoutManager(context).apply { stackFromEnd = true }
        }
    }

    private fun setupInputArea() {
        binding.btnSend.setOnClickListener { sendMessage() }
        binding.btnMic.setOnClickListener { startVoiceInput() }
        binding.etInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) { sendMessage(); true } else false
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_history -> {
                    findNavController().navigate(R.id.action_chat_to_history)
                    true
                }
                R.id.action_clear -> {
                    viewModel.clearChat()
                    true
                }
                else -> false
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.messages.collect { messages ->
                        val rows = messages.toChatRows()
                        adapter.submitList(rows) {
                            if (rows.isNotEmpty()) {
                                binding.rvMessages.scrollToPosition(rows.size - 1)
                            }
                        }
                    }
                }
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is ChatUiState.Loading -> {
                                binding.btnSend.isEnabled = false
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is ChatUiState.Idle -> {
                                binding.btnSend.isEnabled = true
                                binding.progressBar.visibility = View.GONE
                            }
                            is ChatUiState.Error -> {
                                binding.btnSend.isEnabled = true
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sendMessage() {
        val text = binding.etInput.text?.toString()?.trim() ?: return
        if (text.isEmpty()) return
        viewModel.send(text)
        binding.etInput.text?.clear()
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_prompt))
        }
        try {
            speechLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.voice_not_available, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
