package id.android.kmabsensi.presentation.komship.selectdestination

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import id.android.kmabsensi.data.remote.response.komship.DestinationItem
import id.android.kmabsensi.databinding.ActivitySelectDestinationBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.utils.UiState
import org.koin.android.ext.android.inject

class SelectDestinationActivity : BaseActivityRf<ActivitySelectDestinationBinding>(
    ActivitySelectDestinationBinding::inflate
) {
    private val vm : DestinationViewModel by inject()
    private var destinationAdapter : DestinationAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar("Pilih Kode Pos/Kecamatan", isBackable = true)
        vm.getDestination(search = "a")
        binding.etSearchDestination.doAfterTextChanged {
            vm.getDestination(search = it.toString())
        }
        setupList()
        setupObserver()
    }

    private fun setupObserver() {
        vm.destinationState.observe(this, {
            when(it){
                is UiState.Loading -> Log.d("_destinationState", "on loading")
                is UiState.Success -> {
                    Log.d("_destinationState", "on Success")
                    destinationAdapter?.setData(it.data.data?.data!!)
                }
                is UiState.Error -> Log.d("_destinationState", "on Error ${it.throwable}")
            }
        })
    }

    private fun setupList() {
        destinationAdapter = DestinationAdapter(object : DestinationAdapter.onAdapterListener{
            override fun onCLick(data: DestinationItem) {
                val intent = Intent()
                intent.putExtra("_destination", data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })

        binding.rvDestination.apply{
            adapter = destinationAdapter
            layoutManager = LinearLayoutManager(this@SelectDestinationActivity)
            setHasFixedSize(true)
        }
    }
}