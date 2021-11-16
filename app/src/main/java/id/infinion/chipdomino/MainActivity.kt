package id.infinion.chipdomino

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import id.infinion.Auth
import id.infinion.chipdomino.databinding.ActivityMainBinding
import id.infinion.models.Order
import id.infinion.remote.AppApiService
import id.infinion.remote.ServiceBuilderApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var apiService: AppApiService

    private lateinit var rvOrders : RecyclerView
    private lateinit var orderAdapter: OrderAdapter

    private var searchNotClicked = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        apiService = ServiceBuilderApp.buildService(AppApiService::class.java)
        supportActionBar?.hide()
        setupValidators()
        setupListeners()
        setupOrders()
        setContentView(binding.root)
    }

    private fun setupOrders(){
        rvOrders = binding.rvOrders
        orderAdapter = OrderAdapter()
        rvOrders.adapter = orderAdapter
        rvOrders.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        val orders = arrayListOf<Order>()
        lifecycleScope.launch(Dispatchers.Main){
            val wait = lifecycleScope.async {
                if(Auth.authUser(this@MainActivity)){
                    apiService.getOrderHistory(Auth.getToken(this@MainActivity)!!).body()?.map {
                        orders.add(it)
                    }
                }
            }
            wait.await()
            orderAdapter.orders.addAll(orders)
            orderAdapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupListeners(){
        var name : String? = null
        binding.btnSearchPlayerById.setOnClickListener{
            searchNotClicked = false
            lifecycleScope.launch(Dispatchers.Main){
                val progress = ProgressDialog(this@MainActivity)
                progress.setMessage("Mencari pemain ...")
                progress.show()
                val wait = lifecycleScope.async {
                    try {
                        name = apiService.getPlayerById(binding.etPlayerId.text.toString()).body()
                    }catch (e : Exception){
                        showToast(e.toString())
                    }
                }
                wait.await()
                progress.dismiss()
                    if (name.isNullOrBlank()){
                        showToast("Pemain tidak ditemukan.")
                        binding.tvPlayerName.text = "Pemain tidak ditemukan."
                    }else{
                        binding.tvPlayerName.text = name
                    }

            }
        }

        var dialogClickListener =
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        sendVoucher()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }

        binding.btnSendVoucher.setOnClickListener{
            if (searchNotClicked){
                showToast("Silakan cari pemain")
            }else{
                val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
                builder
                    .setMessage("Kirim 1B ke ${binding.tvPlayerName.text} dengan ID ${binding.etPlayerId.text}?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show()
            }
        }
    }

    private fun sendVoucher(){
        var code = 400
        lifecycleScope.launch(Dispatchers.Main){

            if(Auth.authUser(this@MainActivity)){
                val wait = lifecycleScope.async {
                    Auth.getToken(this@MainActivity)?.let {
                        code = apiService.sendVoucher(
                            it,
                            binding.etPlayerId.text.toString()
                        ).code()
                    }
                }
                wait.await()
                if(code == 200){
                    showToast("Berhasil mengirim koin")
                    setupOrders()
                }else{
                    showToast("Tidak cukup stok, atau kendala lain")
                }
            }else{
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
    }



    private fun showToast(message : String){
        Snackbar.make(binding.cardView, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setupValidators(){
        binding.etPlayerId.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                if(!searchNotClicked){
                    searchNotClicked = true
                    binding.tvPlayerName.text = "Detail pemain tampil di sini"
                }
            }

        })
    }
}