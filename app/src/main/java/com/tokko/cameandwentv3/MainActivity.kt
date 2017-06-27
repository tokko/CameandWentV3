package com.tokko.cameandwentv3

import android.app.ListFragment
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

/**
 * Created by andre on 10/06/2017.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, DummyListFragment()).commit()
    }
}

class DummyListFragment : ListFragment() {

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, android.R.id.text1)
        listAdapter = adapter
        //FetchData({c -> adapter.addAll(c); adapter.notifyDataSetChanged();}).execute()
        var database = FirebaseDatabase.getInstance()
        var auth = FirebaseAuth.getInstance()
        var myRef = database.getReference("list")

        myRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot?) {
                adapter.add(p0?.getValue(String::class.java))
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        })

    }
}

class FetchData(param: (ArrayList<String>) -> Unit) : AsyncTask<Void, Void, ArrayList<String>>() {
    val param = param

    override fun doInBackground(vararg params: Void?): ArrayList<String>? {
        return ArrayList(Arrays.asList("1", "2", "3"))
    }

    override fun onPostExecute(result: ArrayList<String>) {
        super.onPostExecute(result)
        param(result)
    }
}
