package com.tokko.cameandwentv3

import android.app.ListFragment
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
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
        FetchData({c -> adapter.addAll(c); adapter.notifyDataSetChanged();}).execute()
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
