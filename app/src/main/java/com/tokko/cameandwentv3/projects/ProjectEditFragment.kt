package com.tokko.cameandwentv3.projects

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DialogFragment
import android.app.Fragment
import android.content.*
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tokko.cameandwentv3.MyApplication
import com.tokko.cameandwentv3.R
import com.tokko.cameandwentv3.events.EventEditProjectComplete
import com.tokko.cameandwentv3.model.Project
import kotlinx.android.synthetic.main.project_edit_fragment.*
import kotlinx.android.synthetic.main.ssid_list_dialog_fragment.*
import java.util.stream.Collector
import java.util.stream.Collectors

class ProjectEditFragment : Fragment(){
    var project : Project? = null
    var locationAdapter: ArrayAdapter<Project.ProjectLocation>? = null
    var SSIDAdapter: ArrayAdapter<String>? = null

    companion object Factory {
        fun newInstance(project: Project?): ProjectEditFragment {
            var b = Bundle()
            if (project != null)
                b.putSerializable("project", project)
            var f = ProjectEditFragment()
            f.arguments = b
            return f
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        project = (savedInstanceState ?: arguments)?.getSerializable("project") as? Project ?: Project()
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.project_edit_fragment, null, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
    }
    override fun onStart() {
        super.onStart()
        (activity.application as MyApplication).bus.register(this)
    }

    override fun onStop() {
        super.onStop()
        (activity.application as MyApplication).bus.unregister(this)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable("project", project)
    }

    private fun bindViews() {
        title!!.setText(project!!.title)
        locationAdapter = object: ArrayAdapter<Project.ProjectLocation>(activity, android.R.layout.simple_list_item_1, android.R.id.text1, project!!.locations){
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val v = convertView ?: (activity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(android.R.layout.simple_list_item_1, null)
                val item = getItem(position)
                v!!.findViewById<TextView>(android.R.id.text1).text = if(item.address == null) item.longitude.toString() + "\n" + item.latitude else item.address
                return v
            }
        }
        SSIDAdapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, android.R.id.text1, project!!.SSIDs)
        locations.adapter = locationAdapter
        ssids.adapter = SSIDAdapter

        ok.setOnClickListener {
            FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance()
                    .currentUser!!.uid+"/projects/"+project!!.id)
                    .setValue(project)
            (activity.application as MyApplication).bus.post(EventEditProjectComplete(project)) }
            cancel.setOnClickListener { (activity.application as MyApplication).bus.post(EventEditProjectComplete(project))
        }

        title!!.addTextChangedListener(object: TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                project!!.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
        })

        add_location!!.setOnClickListener { getCurrentLocation() }
        add_ssid!!.setOnClickListener { pickSSID() }

        locations.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val builder = AlertDialog.Builder(activity)
                    .setTitle("Delete item?")
                    .setMessage("Are you sure?")
                    .setNegativeButton("No, I'm scared :(", { _, _ -> ; })
                    .setPositiveButton("Yes!", { _, _ ->
                        project!!.locations.remove(project!!.locations[position])
                        locationAdapter!!.notifyDataSetChanged()
                    })
            builder.show()
        }
        ssids.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->
            val builder = AlertDialog.Builder(activity)
                    .setTitle("Delete item?")
                    .setMessage("Are you sure?")
                    .setNegativeButton("No, I'm scared :(", {_, _ -> ;})
                    .setPositiveButton("Yes!", { _, _ ->
                        project!!.SSIDs.remove(project!!.SSIDs[position])
                        SSIDAdapter!!.notifyDataSetChanged()
                    })
            builder.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        SSIDAdapter!!.add(data?.getStringExtra("ssid"))
        SSIDAdapter!!.notifyDataSetChanged()
    }

    fun pickSSID(){
        val newInstance = SSIDFragmentDialog.newInstance()
        newInstance.setTargetFragment(this, 0)
        newInstance.show(fragmentManager, "ssid dialog")
    }

    private fun getCurrentLocation() {
        val mLocationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
               AlertDialog.Builder(activity.applicationContext)
                       .setTitle("GPS permissions")
                       .setMessage("This permission is required to detect when you enter or leave your working area. Without it this app will basically be a digital punch clock.")
                       .setPositiveButton("Ok") { dialog, _ -> dialog?.dismiss() }

            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 0)
            }
            return
        }
        val location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if(location != null) {
            project!!.addLocation(location)
            locationAdapter!!.notifyDataSetChanged()
            val resultReceiver: ResultReceiver = object : ResultReceiver(Handler()) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                    project!!.locations.last().address = resultData!!.getString("address")
                    locationAdapter!!.notifyDataSetChanged()
                }
            }

            GeocodingService.startService(activity, project!!.id, location.latitude, location.longitude, resultReceiver)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        when(requestCode){
            0 -> {
                if(grantResults!!.isNotEmpty() && grantResults.toList().stream().allMatch({x -> x == PackageManager.PERMISSION_GRANTED}))
                    getCurrentLocation()
                else
                    AlertDialog.Builder(activity.applicationContext)
                            .setTitle("GPS permissions")
                            .setMessage("Denying these permissions will kill the core usefulness of this app.")
                            .setPositiveButton("Ok") { dialog, _ -> dialog?.dismiss() }
            }

        }
    }

    class SSIDFragmentDialog: DialogFragment(){
        companion object{
            fun newInstance(): SSIDFragmentDialog{
                val b = Bundle()
                val f = SSIDFragmentDialog()
                f.arguments = b
                return f
            }
        }

        var ssids: ArrayList<String> = ArrayList()
        var adapter: ArrayAdapter<String>? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            ssids = (savedInstanceState ?: arguments)?.getStringArrayList("ssids") ?: ArrayList()
        }

        override fun onSaveInstanceState(outState: Bundle?) {
            super.onSaveInstanceState(outState)
            outState!!.putStringArrayList("ssids", ssids)
        }

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
            return inflater!!.inflate(R.layout.ssid_list_dialog_fragment, null, false)
        }

        override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            adapter = ArrayAdapter(activity.applicationContext, android.R.layout.simple_list_item_1, android.R.id.text1, ssids)
            dialog_list.adapter = adapter
            dialog_list.setOnItemClickListener { _, _, position, _ -> targetFragment.onActivityResult(0, Activity.RESULT_OK, Intent().putExtra("ssid", ssids[position])); dismiss()}
            dialog_list.emptyView = empty_view
        }

        override fun onResume() {
            super.onResume()
            pickSSID()
        }

        private fun pickSSID() {
            val wm = activity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CHANGE_WIFI_STATE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_WIFI_STATE)) {
                    AlertDialog.Builder(activity.applicationContext)
                            .setTitle("GPS permissions")
                            .setMessage("This permission is required to detect when you enter or leave your working area. Without it this app will basically be a digital punch clock.")
                            .setPositiveButton("Ok") { dialog, _ -> dialog?.dismiss() }

                } else {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_WIFI_STATE), 1)
                }
                return
            }
            if (!wm.isWifiEnabled) {
                wm.isWifiEnabled = true
                Toast.makeText(activity.applicationContext, "Wifi is disabled. Enabling...", Toast.LENGTH_SHORT).show()
            }
            activity.registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val scanResults = wm.scanResults
                    val elements = scanResults.stream().map { x -> x.SSID }.collect(Collectors.toList())
                    ssids.clear()
                    ssids.addAll(elements)
                    adapter!!.notifyDataSetChanged()
                }
            }, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
            wm.startScan()
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
            when(requestCode){
                1 -> {
                    if(grantResults!!.isNotEmpty() && grantResults.toList().stream().allMatch({x -> x == PackageManager.PERMISSION_GRANTED}))
                        pickSSID()
                    else
                        AlertDialog.Builder(activity.applicationContext)
                                .setTitle("GPS permissions")
                                .setMessage("Denying these permissions will kill the core usefulness of this app.")
                                .setPositiveButton("Ok") { dialog, _ -> dialog?.dismiss() }
                }
            }
        }
    }
}