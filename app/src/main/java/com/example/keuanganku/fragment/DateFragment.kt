package com.example.keuanganku.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.keuanganku.R
import com.example.keuanganku.databinding.FragmentDateBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class DateFragment(var listener: DateListener) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDateBinding
    private var clickDateStart: Boolean = false
    private var dateTemp: String = ""
    private var dateStart: String = ""
    private var dateEnd: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView("Tanggal mulai", "Pilih")
        setupListener()
    }

    private fun setupListener() {
        binding.calendarView.setOnDateChangeListener { _, year, month, day ->
            dateTemp = "$day/${month + 1}/$year"
        }
        binding.textApply.setOnClickListener {
            when (clickDateStart) {
                false -> {
                    clickDateStart = true
                    dateStart = dateTemp
                    binding.calendarView.date = Date().time
                    setView("Tanggal Akhir", "Terapkan")
                }
                true -> {
                    dateEnd = dateTemp
                    listener.onSuccess(dateStart, dateEnd)
                    this.dismiss()
                }
            }
        }
    }

    private fun setView(title: String, apply: String) {
        binding.textTitle.text = title
        binding.textApply.text = apply
    }

    interface DateListener {
        fun onSuccess(dateStart: String, dateEnd: String)
    }
}