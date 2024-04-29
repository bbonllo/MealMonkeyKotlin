package com.bbonllo.mealmonkey.ui.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bbonllo.mealmonkey.R
import com.bbonllo.mealmonkey.databinding.FragmentMapsBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton


@Suppress("DEPRECATION")
class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val permissionLocation = Manifest.permission.ACCESS_FINE_LOCATION
    private val permissionLocationReqCode = 100
    private var firstTimePermission: Boolean = true

    private var mMap: GoogleMap? = null
    private var mapInitialized = false
    private var mapPosition: LatLng? = null

    private lateinit var fabMyLocation: FloatingActionButton

    private val latlngBILBAO = LatLng(43.262985, -2.935013)

    @RequiresApi(Build.VERSION_CODES.R)
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        if (!mapInitialized)
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latlngBILBAO, 4.8F))
        else
            mapPosition?.let {
                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 4.8F))
            }

        mapInitialized = true

        myLocationMarker()
        addMapMarkers()
    }

    private fun addMapMarkers() {
        val melbourne = LatLng(-37.813, 144.962)
        val sydney = LatLng(-33.852, 151.211)

        mMap!!.addMarker(
            MarkerOptions()
                .position(melbourne)
                .title("Melbourne")
                .snippet("Population: 4,137,400")
            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_down_24dp))
        )

        mMap!!.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )

        // Algo para controlar que marcador se pulsa
        val markerPerth: Marker? = mMap!!.addMarker(
            MarkerOptions()
                .position(latlngBILBAO)
                .title("Bilbao")
        )
        markerPerth?.tag = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        fabMyLocation = view.findViewById(R.id.fab_my_location)
        mapFragment?.getMapAsync(callback)
        fabMyLocation.setOnClickListener {
            getMyLocation()
        }
    }

    private fun getMyLocation() {
        if (this.activity?.let { ActivityCompat.checkSelfPermission(it, permissionLocation) }
            == PackageManager.PERMISSION_GRANTED) {
            isGPSEnabled()
            val fusedLocationProviderClient =
                context?.let { LocationServices.getFusedLocationProviderClient(it) }
            fusedLocationProviderClient?.lastLocation?.addOnCompleteListener { task ->
                val location: Location? = task.result
                if (location != null) {
                    mMap?.isMyLocationEnabled = true
                    mMap?.uiSettings?.isMyLocationButtonEnabled = false
                    val latLng = LatLng(location.latitude, location.longitude)
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                    mMap?.animateCamera(cameraUpdate)
                }
            }
            fabMyLocation.setImageResource(R.drawable.ic_location_24dp)
        } else if (this.activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    permissionLocation
                )
            } == true) {
            val builder = MaterialAlertDialogBuilder(this.requireContext())
            builder.setTitle(R.string.title_location_permission)
                .setMessage(R.string.is_required_permission_location)
                .setCancelable(false)
                .setPositiveButton(R.string.title_ok) { dialog, _ ->
                    ActivityCompat.requestPermissions(
                        this.requireActivity(), arrayOf(permissionLocation),
                        permissionLocationReqCode
                    )
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.title_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            if (firstTimePermission) {
                firstTimePermission = false
                this.activity?.let {
                    ActivityCompat.requestPermissions(
                        it, arrayOf(permissionLocation),
                        permissionLocationReqCode
                    )
                }
            } else {
                firstTimePermission = true
                val builder = MaterialAlertDialogBuilder(this.requireContext())
                builder.setTitle(R.string.title_location_permission)
                    .setMessage(R.string.unaviable_location_feature)
                    .setCancelable(false)
                    .setPositiveButton(R.string.title_settings) { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", activity?.packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .setNegativeButton(R.string.title_cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun myLocationMarker() {
        if (this.activity?.let { ActivityCompat.checkSelfPermission(it, permissionLocation) }
            == PackageManager.PERMISSION_GRANTED) {
            mMap?.isMyLocationEnabled = true
            mMap?.uiSettings?.isMyLocationButtonEnabled = false
        } else {
            fabMyLocation.setImageDrawable(resources.getDrawable(R.drawable.ic_location_disabled_24dp,
                context?.theme
            ));
            //fabMyLocation.setImageResource(R.drawable.ic_location_disabled_24dp)
            //fabMyLocation.setBackgroundColor(R.color)
        }
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

}
