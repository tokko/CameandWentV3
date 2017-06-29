package com.tokko.cameandwentv3.projects

import android.Manifest
import android.app.AlertDialog
import android.app.Fragment
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tokko.cameandwentv3.MyApplication
import com.tokko.cameandwentv3.R
import com.tokko.cameandwentv3.events.EventEditProjectComplete
import com.tokko.cameandwentv3.model.Project
import kotlinx.android.synthetic.main.project_edit_fragment.*

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
                var v = convertView ?: (activity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(android.R.layout.simple_list_item_1, null)
                var item = getItem(position)
                (v!!.findViewById(android.R.id.text1) as TextView).text = item.longitude.toString() + "\n" + item.latitude
                return v
            }
        }
        SSIDAdapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, android.R.id.text1, project!!.SSIDs)
        locations.adapter = locationAdapter
        ssids.adapter = SSIDAdapter

        ok.setOnClickListener { FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().currentUser!!.uid+"/projects").push().setValue(project); (activity.application as MyApplication).bus.post(EventEditProjectComplete(project)) }
        cancel.setOnClickListener { (activity.application as MyApplication).bus.post(EventEditProjectComplete(project)) }

        title!!.addTextChangedListener(object: TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                project!!.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
        })

        add_location!!.setOnClickListener {
            getCurrentLocation() }
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
        }

        /*
        mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, object: LocationListener{
            override fun onProviderEnabled(provider: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onProviderDisabled(provider: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onLocationChanged(location: Location?) {
                if(location != null)
                    project!!.locations.add(location)
            }

        }, Looper.getMainLooper())
        */
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
}