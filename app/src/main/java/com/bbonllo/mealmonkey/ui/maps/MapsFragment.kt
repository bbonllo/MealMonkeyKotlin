package com.bbonllo.mealmonkey.ui.maps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bbonllo.mealmonkey.R
import com.bbonllo.mealmonkey.databinding.FragmentMapsBinding
import com.bbonllo.mealmonkey.ui.addmarker.AddMarkerActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.graphics.Path
import android.graphics.RectF
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt

@Suppress("DEPRECATION")
class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val permissionLocation = Manifest.permission.ACCESS_FINE_LOCATION
    private val permissionLocationReqCode = 100

    private var mMap: GoogleMap? = null
    private var mapInitialized = false
    private var mapPosition: LatLng? = null
    private var firstTimePermission: Boolean = true

    private lateinit var fabMyLocation: FloatingActionButton
    private lateinit var btnAddLocation: Button

    private val latlngBILBAO = LatLng(43.262985, -2.935013)

    // Callback para cuando el mapa está listo
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap?.uiSettings?.isRotateGesturesEnabled = false // Desactiva rotación

        if (!mapInitialized) {
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latlngBILBAO, 4.8F))
        } else {
            mapPosition?.let {
                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 4.8F))
            }
        }

        mapInitialized = true
        myLocationMarker()
        addMapMarkers()
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
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        fabMyLocation = view.findViewById(R.id.fab_my_location)
        btnAddLocation = view.findViewById(R.id.fab_add_location)

        mapFragment?.getMapAsync(callback)

        fabMyLocation.setOnClickListener {
            getMyLocation()
        }

        btnAddLocation.setOnClickListener {
            val intent = Intent(requireContext(), AddMarkerActivity::class.java)
            startActivity(intent)
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

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getMyLocation() {
        if (this.activity?.let { ActivityCompat.checkSelfPermission(it, permissionLocation) }
            == PackageManager.PERMISSION_GRANTED) {
            isGPSEnabled()
            val fusedLocationProviderClient = context?.let { LocationServices.getFusedLocationProviderClient(it) }
            fusedLocationProviderClient?.lastLocation?.addOnCompleteListener { task ->
                val location: Location? = task.result
                if (location != null) {
                    mMap?.isMyLocationEnabled = true
                    mMap?.uiSettings?.isMyLocationButtonEnabled = false
                    val latLng = LatLng(location.latitude, location.longitude)
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                    mMap?.animateCamera(cameraUpdate, 1200, null)
                }
            }
            fabMyLocation.setImageResource(R.drawable.ic_location_24dp)
        } else if (this.activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    permissionLocation
                )
            } == true) {
            showPermissionDialog()
        } else {
            handlePermissionRequest()
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showPermissionDialog() {
        MaterialAlertDialogBuilder(this.requireContext())
            .setTitle(R.string.title_location_permission)
            .setMessage(R.string.is_required_permission_location)
            .setCancelable(false)
            .setPositiveButton(R.string.title_ok) { dialog, _ ->
                requestLocationPermission()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.title_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun handlePermissionRequest() {
        if (firstTimePermission) {
            firstTimePermission = false
            requestLocationPermission()
        } else {
            showSettingsDialog()
        }
    }

    private fun requestLocationPermission() {
        this.activity?.let {
            ActivityCompat.requestPermissions(
                it, arrayOf(permissionLocation),
                permissionLocationReqCode
            )
        }
    }

    private fun showSettingsDialog() {
        MaterialAlertDialogBuilder(this.requireContext())
            .setTitle(R.string.title_location_permission)
            .setMessage(R.string.unaviable_location_feature)
            .setCancelable(false)
            .setPositiveButton(R.string.title_settings) { _, _ ->
                openAppSettings()
            }
            .setNegativeButton(R.string.title_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity?.packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun myLocationMarker() {
        if (this.activity?.let { ActivityCompat.checkSelfPermission(it, permissionLocation) }
            == PackageManager.PERMISSION_GRANTED) {
            mMap?.isMyLocationEnabled = true
            mMap?.uiSettings?.isMyLocationButtonEnabled = false
            fabMyLocation.setImageResource(R.drawable.ic_location_24dp)
        } else {
            fabMyLocation.setImageResource(R.drawable.ic_location_disabled_24dp)
        }
    }

    private fun addMapMarkers() {
        val madrid = LatLng(40.416775, -3.703790)
        val barcelona = LatLng(41.390205, 2.154007)

        mMap!!.addMarker(
            MarkerOptions()
                .position(madrid)
                .title("Madrid")
                .snippet("Population: 4,137,400")
                .icon(context?.let { bitmapDescriptor(it, "fastfood", listOf("#FF7F50", "#7d8282", "#3ec37d")) })
        )

        mMap!!.addMarker(
            MarkerOptions()
                .position(barcelona)
                .title("Barcelona")
                .icon(context?.let { bitmapDescriptor(it, "ramen", listOf("#00FF00", "#7d8282", "#FF7F50", "#7CFC00")) })
        )

        val markerPerth: Marker? = mMap!!.addMarker(
            MarkerOptions()
                .position(latlngBILBAO)
                .title("Bilbao")
                .snippet("Population: 4,137,400")
                .icon(context?.let { bitmapDescriptor(it, "ramen", listOf("#fa626c", "#7d8282", "#3ec37d")) })
        )
        markerPerth?.tag = 0
    }

    private fun bitmapDescriptor(context: Context, iconName: String, colors: List<String>): BitmapDescriptor {
        val mapIconName = "ic_${iconName}_maps"
        val resourceId = context.resources.getIdentifier(mapIconName, "drawable", context.packageName)

        return if (resourceId != 0) {
            val vectorDrawable = ContextCompat.getDrawable(context, resourceId)

            val size = 65
            val pointerHeight = 15
            val pointerWidth = 10f
            val borderWidth = 7f

            val totalHeight = size + pointerHeight + borderWidth.toInt() * 2
            val bitmap = createBitmap(size + borderWidth.toInt() * 2, totalHeight)
            val canvas = Canvas(bitmap)
            canvas.drawFilter =
                PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

            val centerX = (bitmap.width / 2).toFloat()
            val centerY = (size / 2 + borderWidth).toFloat()
            val radius = (size / 2).toFloat()

            val borderPaint = Paint().apply {
                color = "#FFFEFE".toColorInt()
                isAntiAlias = true
                setShadowLayer(7f, 0f, 0f, "#33000000".toColorInt())
            }

            canvas.drawCircle(centerX, centerY, radius + borderWidth, borderPaint)

            val pointerPath = Path().apply {
                moveTo(centerX - pointerWidth, centerY + radius - 5)
                lineTo(centerX + pointerWidth, centerY + radius - 5)
                close()
            }

            val pointerBorderPath = Path().apply {
                moveTo(centerX - pointerWidth - borderWidth + 2.5f - 1.8f, centerY + radius - 5)
                lineTo(centerX + pointerWidth + borderWidth + 2.5f - 1f, centerY + radius - 5)
                lineTo(centerX, centerY + radius + pointerHeight + borderWidth)
                close()
            }

            canvas.drawPath(pointerBorderPath, borderPaint)
            canvas.drawPath(pointerPath, borderPaint)

            val oval = RectF(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
            )

            val anglePerSlice = 360f / colors.size
            var startAngle = 0f

            colors.forEach { hexColor ->
                val paint = Paint().apply {
                    color = hexColor.toColorInt()
                    isAntiAlias = true
                    style = Paint.Style.FILL
                }
                canvas.drawArc(oval, startAngle, anglePerSlice, true, paint)
                startAngle += anglePerSlice
            }

            vectorDrawable!!.setTint("#FFFEFE".toColorInt())
            vectorDrawable.setBounds(
                (centerX - radius / 1.4f).toInt(),
                (centerY - radius / 1.4f).toInt(),
                (centerX + radius / 1.4f).toInt(),
                (centerY + radius / 1.4f).toInt()
            )
            vectorDrawable.draw(canvas)

            BitmapDescriptorFactory.fromBitmap(bitmap)
        } else {
            BitmapDescriptorFactory.defaultMarker()
        }
    }
}