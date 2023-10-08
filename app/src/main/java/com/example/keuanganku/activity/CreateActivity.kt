package com.example.keuanganku.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.keuanganku.R
import com.example.keuanganku.activity.BaseActivity
import com.example.keuanganku.adapter.CategoryAdapter
import com.example.keuanganku.adapter.TransactionAdapter
import com.example.keuanganku.databinding.ActivityCreateBinding
import com.example.keuanganku.model.Category
import com.example.keuanganku.model.Transaction
import com.example.keuanganku.preferences.PreferencesManager
import com.example.keuanganku.utils.PrefUtil
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateActivity : BaseActivity() {

    final val TAG = "CreateActivity"

    private val binding by lazy { ActivityCreateBinding.inflate(layoutInflater) }
    private val db by lazy { Firebase.firestore }
    private val pref by lazy { PreferencesManager(this) }
    private lateinit var categoryAdapter: CategoryAdapter
    private var type: String = ""
    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupList()
        setupListener()
    }

    override fun onStart() {
        super.onStart()
        getCategory()
    }

    private fun setupList() {
        categoryAdapter = CategoryAdapter(this, arrayListOf(), object: CategoryAdapter.AdapterListener {
            override fun onClick(category: Category) {
                this@CreateActivity.category = category.name!!
                Log.e(TAG, this@CreateActivity.category)
            }
        })
        binding.listCategory.adapter = categoryAdapter
    }

    private fun setupListener() {
        binding.buttonIn.setOnClickListener {
            type = "IN"
            setButton(it as MaterialButton)
        }
        binding.buttonOut.setOnClickListener {
            type = "OUT"
            setButton(it as MaterialButton)
        }
        binding.buttonSave.setOnClickListener {
            progress(true)
            val transaction = Transaction(
                id = null,
                username = pref.getString(PrefUtil.pref_username)!!,
                category = category,
                type = type,
                amount = binding.editAmount.text.toString().toInt(),
                note = binding.editNote.text.toString(),
                created = Timestamp.now()
            )
            db.collection("transaction")
                .add(transaction)
                .addOnSuccessListener {
                    progress(false)
                    Toast.makeText(applicationContext, "Transaction Added", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    private fun setButton(buttonSelected: MaterialButton) {
        Log.e(TAG, type)
        listOf<MaterialButton>(binding.buttonIn, binding.buttonOut).forEach {
            it.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        }
        buttonSelected.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_700))
    }

    private fun getCategory() {
        val categories: ArrayList<Category> = arrayListOf()
        db.collection("category")
            .get()
            .addOnSuccessListener { result ->
                result.forEach { document ->
                    categories.add(Category(document.data["name"].toString()))
                }
                Log.e("HomeActivity", "categories $categories")
                categoryAdapter.setData(categories)
            }
    }

    private fun progress(progress: Boolean) {
        when (progress) {
            true -> {
                binding.buttonSave.text = "Loading..."
                binding.buttonSave.isEnabled = false
            }
            false -> {
                binding.buttonSave.text = "Simpan"
                binding.buttonSave.isEnabled = true
            }
        }
    }
}