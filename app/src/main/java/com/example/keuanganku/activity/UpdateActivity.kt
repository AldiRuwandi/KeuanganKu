package com.example.keuanganku.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.keuanganku.R
import com.example.keuanganku.activity.BaseActivity
import com.example.keuanganku.adapter.CategoryAdapter
import com.example.keuanganku.databinding.ActivityCreateBinding
import com.example.keuanganku.model.Category
import com.example.keuanganku.model.Transaction
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UpdateActivity : BaseActivity() {

    private final val TAG: String = "UpdateActivity"

    private val binding by lazy { ActivityCreateBinding.inflate(layoutInflater) }
    private lateinit var categoryAdapter: CategoryAdapter
    private val transactionId by lazy { intent.getStringExtra("id") }
    private var type: String = ""
    private var category: String = ""
    private val db by lazy { Firebase.firestore }

    private lateinit var transaction: Transaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListener()
        setupList()
        Log.e(TAG, "transactionId: $transactionId")
    }

    override fun onStart() {
        super.onStart()
        detailTransaction()
    }

    private fun setupList() {
        categoryAdapter = CategoryAdapter(this, arrayListOf(), object : CategoryAdapter.AdapterListener {
            override fun onClick(category: Category) {
                transaction.category = category.name!!
            }
        })
        binding.listCategory.adapter = categoryAdapter
    }

    private fun setupListener() {
        binding.buttonSave.setText("Simpan Perubahan")
        binding.buttonSave.setOnClickListener {
            progress(true)
            transaction.amount = binding.editAmount.text.toString().toInt()
            transaction.note = binding.editNote.text.toString()
            db.collection("transaction")
                .document(transactionId!!)
                .set(transaction)
                .addOnSuccessListener {
                    progress(false)
                    Toast.makeText(applicationContext, "Transaction Updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
        binding.buttonIn.setOnClickListener {
            transaction.type = "IN"
            setButton(it as MaterialButton)
        }
        binding.buttonOut.setOnClickListener {
            transaction.type = "OUT"
            setButton(it as MaterialButton)
        }
    }

    private fun progress(progress: Boolean) {
        when (progress) {
            true -> {
                binding.buttonSave.text = "Loading..."
                binding.buttonSave.isEnabled = false
            }
            false -> {
                binding.buttonSave.text = "Simpan Perubahan"
                binding.buttonSave.isEnabled = true
            }
        }
    }

    private fun setButton(buttonSelected: MaterialButton) {
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
                Handler(Looper.getMainLooper()!!).postDelayed({
                    categoryAdapter.setButton(transaction.category)
                }, 200)
            }
    }

    private fun detailTransaction() {
        db.collection("transaction")
            .document(transactionId!!)
            .get()
            .addOnSuccessListener { result ->
                transaction = Transaction(
                    id = result.id,
                    amount = result["amount"].toString().toInt(),
                    category = result["category"].toString(),
                    type = result["type"].toString(),
                    note = result["note"].toString(),
                    username = result["username"].toString(),
                    created = result["created"] as Timestamp
                )
                binding.editAmount.setText(transaction.amount.toString())
                binding.editNote.setText(transaction.note.toString())

                when (transaction.type) {
                    "IN" -> setButton(binding.buttonIn)
                    "OUT" -> setButton(binding.buttonOut)
                }
                getCategory()
            }
    }
}