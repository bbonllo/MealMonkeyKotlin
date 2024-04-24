package com.bbonllo.mealmonkey.ui.maps

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bbonllo.mealmonkey.R
import com.bbonllo.mealmonkey.databinding.FragmentMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private var mMap: GoogleMap? = null
    private var mapInitialized = false
    private var mapPosition: LatLng? = null

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        val bilbao = LatLng(43.262985, 	-2.935013)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Bilbao"))
        mapInitialized = true
        mapPosition?.let {
            moveCameraToPosition(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMap?.let { googleMap ->
            outState.putParcelable("map_position", googleMap.cameraPosition.target)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            mapPosition = it.getParcelable("map_position")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun moveCameraToPosition(position: LatLng) {
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(position))
    }
}
