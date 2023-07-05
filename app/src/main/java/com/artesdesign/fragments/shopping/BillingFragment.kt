package com.artesdesign.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artesdesign.adapters.AddressAdapter
import com.artesdesign.adapters.BillingProductAdapter
import com.artesdesign.augmentedReality.R
import com.artesdesign.augmentedReality.databinding.FragmentBillingBinding
import com.artesdesign.data.Address
import com.artesdesign.data.CartProduct
import com.artesdesign.data.order.Order
import com.artesdesign.data.order.OrderStatus
import com.artesdesign.util.HorizontalItemDecoration
import com.artesdesign.util.Resource
import com.artesdesign.viewmodel.shopping.BillingViewModel
import com.artesdesign.viewmodel.shopping.PlaceOrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BillingFragment :Fragment(){
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter()}
    private val billingProductAdapter by lazy { BillingProductAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()
    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f

    private var selectedAddress : Address? = null
    private val orderViewModel by viewModels<PlaceOrderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        products = args.products.toList()
        totalPrice = args.totalPrice
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAddressRv()
        setupBillingProductsRv()

        if(!args.payment){
            binding.apply {
                buttonPlaceOrder.visibility= View.INVISIBLE
                totalBoxContainer.visibility= View.INVISIBLE
                middleLine.visibility= View.INVISIBLE
                bottomLine.visibility= View.INVISIBLE
            }
        }

        binding.apply {
            imageAddAddress.setOnClickListener {
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
            }

        }

        lifecycleScope.launchWhenStarted {
            billingViewModel.addresses.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(),"Error ${it.message}",Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.buttonPlaceOrder.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(),"Siparişiniz verildi!",Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        Toast.makeText(requireContext(),"Error ${it.message}",Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
        billingProductAdapter.differ.submitList(products)
        binding.apply {
            tvTotalPrice.text = "${totalPrice} TL"

            imageCloseBilling.setOnClickListener {
                findNavController().navigateUp()
            }

            addressAdapter.onClick = {
                selectedAddress = it

                if(!args.payment){
                    val b = Bundle().apply { putParcelable("address",selectedAddress) }
                    findNavController().navigate(R.id.action_billingFragment_to_addressFragment,b)
                }

            }
            buttonPlaceOrder.setOnClickListener {
                if(selectedAddress == null) {
                    Toast.makeText(requireContext(),"Please select an address",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                showOrderConfirmationDialog()

            }
        }

    }

    private fun showOrderConfirmationDialog() {

        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Sipariş ürünleri")
            setMessage("Bu ürünleri sipariş etmek ister misiniz?")
            setNegativeButton("İptal Et"){ dialog,_ ->
                dialog.dismiss()
            }
            setPositiveButton("Sipariş Ver"){ dialog,_ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    products,
                    selectedAddress!!
                )
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun setupAddressRv() {
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            adapter = addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun setupBillingProductsRv() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            adapter = billingProductAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }
}