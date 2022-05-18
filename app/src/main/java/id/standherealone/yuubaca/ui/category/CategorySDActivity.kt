package id.standherealone.yuubaca.ui.category

import android.app.SearchManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import id.standherealone.yuubaca.R
import id.standherealone.yuubaca.api.*
import id.standherealone.yuubaca.core.adapter.BukuAdapter
import id.standherealone.yuubaca.databinding.ActivityRecyclerviewBinding
import id.standherealone.yuubaca.model.Buku
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategorySDActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecyclerviewBinding
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: BukuAdapter
    private var shimmer: ShimmerFrameLayout? = null
    lateinit var searchView: SearchView
    lateinit var bukuList: List<Buku>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecyclerviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.recyclerview)
        shimmer = binding.shimmerHome
        recyclerAdapter = BukuAdapter(this)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = recyclerAdapter

        bukuList = ArrayList<Buku>()
        val apiInterface = ApiSD.create().getBuku()

        //apiInterface.enqueue( Callback<List<Buku>>())
        apiInterface.enqueue(object : Callback<List<Buku>> {
            override fun onResponse(call: Call<List<Buku>>?, response: Response<List<Buku>>?) {

//                if (response?.body() != null)
//                    recyclerAdapter.setBukuListItems(response.body()!!)

                bukuList = response!!.body()!!
                Log.d("TAG", "Response = $bukuList")
                recyclerAdapter.setBukuList(applicationContext, bukuList)

                //shimmer
                shimmer!!.stopShimmer()
                shimmer!!.visibility = View.GONE
            }

            override fun onFailure(call: Call<List<Buku>>?, t: Throwable?) {
                val snackbar =
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        R.string.failed_load_data_api,
                        Snackbar.LENGTH_SHORT
                    )
                snackbar.show()

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.action_search)
            .actionView as SearchView
        searchView.setSearchableInfo(
            searchManager
                .getSearchableInfo(componentName)
        )
        searchView.maxWidth = Int.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                recyclerAdapter.getFilter()?.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                recyclerAdapter.getFilter()?.filter(query)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_search) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        shimmer!!.startShimmer()
    }

    override fun onPause() {
        shimmer!!.stopShimmer()
        super.onPause()
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.isIconified = true
            return
        }
        super.onBackPressed()
    }

}