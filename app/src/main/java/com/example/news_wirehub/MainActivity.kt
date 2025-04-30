package com.example.news_wirehub

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

class MainActivity : AppCompatActivity(), NewsItemClicked {

    private lateinit var mAdapter: NewsAdapter
    private lateinit var countryNames: Array<String>
    private lateinit var countryCodes: Array<String>
    private var selectedCountry: String = "in" // Default country code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.list)
        rv.setHasFixedSize(true)

        rv.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter
        mAdapter = NewsAdapter(this)
        rv.adapter = mAdapter

        // Fetch general news for India by default
        fetchDataByDefault()

        // Fetch data for spinner
        fetchData()
        fetchData2()
    }

    private fun fetchDataByDefault() {
        // Set default category and country
        val defaultCategory = "general"
        val defaultCountry = "in"

        // Fetch data for the default category and country
        fetchDataByCategory(defaultCategory)
        selectedCountry = defaultCountry
        fetchDataByCountry()
    }

    private fun fetchData() {
        val categories = resources.getStringArray(R.array.categories)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, categories)
        val value = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        value.setAdapter(arrayAdapter)

        value.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            Log.d("TAG", selectedItem)

            // Pass the selected country to the fetchDataByCategory function
            fetchDataByCategory(selectedItem)
        }
    }

    private fun fetchData2() {
        // Initialize the countryNames and countryCodes arrays
        countryNames = resources.getStringArray(R.array.country_names)
        countryCodes = resources.getStringArray(R.array.country_codes)

        // Set up AutoCompleteTextView for country names
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, countryNames)
        val countryValue = findViewById<AutoCompleteTextView>(R.id.countryAutoCompleteTextView)
        countryValue.setAdapter(arrayAdapter)

        countryValue.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // Convert country name to country code
            selectedCountry = countryCodes[position]

            // Fetch data with the selected country
            fetchDataByCountry()
        }
    }

    private fun fetchDataByCategory(category: String) {
        val url = "https://newsapi.org/v2/top-headlines?country=$selectedCountry&category=$category&apiKey=51c548a3ebc44b9f809b9129121b7712"
        performNewsFetchRequest(url)
    }

    private fun fetchDataByCountry() {
        val url = "https://newsapi.org/v2/top-headlines?country=$selectedCountry&apiKey=51c548a3ebc44b9f809b9129121b7712"
        performNewsFetchRequest(url)
    }

    private fun performNewsFetchRequest(url: String) {
        val getRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            Response.Listener {
                Log.e("TAG", "$it")
                val newsJsonArray = it.getJSONArray("articles")
                val newsArray = ArrayList<News>()
                for (i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)

                    // Extract source information
                    val sourceObject = newsJsonObject.getJSONObject("source")
                    val sourceName = sourceObject.getString("name")

                    // Create News object
                    val news = News(
                        newsJsonObject.getString("author"),
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("urlToImage"),
                        newsJsonObject.getString("publishedAt"),
                        sourceName
                    )
                    news.publishedAt = news.formattedDate()
                    newsArray.add(news)
                }
                mAdapter.updateNews(newsArray)
            },
            Response.ErrorListener { error ->

            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }
        MySingleton.getInstance(this).addToRequestQueue(getRequest)
    }

    override fun onItemClicked(item: News) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(item.url))
    }
    override fun onItemLongClicked(item: News): Boolean {
        // Implement the logic to share the news item
        shareNews(item)
        return true // Return true to indicate that the long press is consumed
    }
    private fun shareNews(news: News) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"

        val message = "Check out this news:\n\n" +
                "Headlines: ${news.title}\n" +
                "Author: ${news.author}\n" +
                "Published At: ${news.publishedAt}\n" +
                "URL: ${news.url}"

        shareIntent.putExtra(Intent.EXTRA_TEXT, message)

        // Start the activity with the share intent
        startActivity(Intent.createChooser(shareIntent, "Share news via"))
    }

}
