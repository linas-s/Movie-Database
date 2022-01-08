package com.example.moviedb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.moviedb.databinding.ActivityMainBinding
import com.example.moviedb.features.home.HomeFragment
import com.example.moviedb.features.search.SearchFragment
import com.example.moviedb.features.watchlist.WatchlistFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var homeFragment: HomeFragment
    private lateinit var searchFragment: SearchFragment
    private lateinit var watchlistFragment: WatchlistFragment

    private val fragments: Array<Fragment>
        get() = arrayOf(
            homeFragment,
            searchFragment,
            watchlistFragment
        )

    private var selectedIndex = 0

    private val selectedFragment get() = fragments[selectedIndex]

    private fun selectFragment(selectedFragment: Fragment) {
        var transaction = supportFragmentManager.beginTransaction()
        fragments.forEachIndexed { index, fragment ->
            if (selectedFragment == fragment) {
                transaction = transaction.attach(fragment)
                selectedIndex = index
            } else {
                transaction = transaction.detach(fragment)
            }
        }
        transaction.commit()

        title = when (selectedFragment) {
            is HomeFragment -> getString(R.string.title_home)
            is SearchFragment -> getString(R.string.title_search)
            is WatchlistFragment -> getString(R.string.title_watchlist)
            else -> ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            homeFragment = HomeFragment()
            searchFragment = SearchFragment()
            watchlistFragment = WatchlistFragment()

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, homeFragment, TAG_HOME_FRAGMENT)
                .add(R.id.fragment_container, searchFragment, TAG_SEARCH_FRAGMENT)
                .add(R.id.fragment_container, watchlistFragment, TAG_WATCHLIST_FRAGMENT)
                .commit()
        } else {
            homeFragment =
                supportFragmentManager.findFragmentByTag(TAG_HOME_FRAGMENT) as HomeFragment
            searchFragment =
                supportFragmentManager.findFragmentByTag(TAG_SEARCH_FRAGMENT) as SearchFragment
            watchlistFragment =
                supportFragmentManager.findFragmentByTag(TAG_WATCHLIST_FRAGMENT) as WatchlistFragment

            selectedIndex = savedInstanceState.getInt(KEY_SELECTED_INDEX, 0)
        }

        selectFragment(selectedFragment)

        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> homeFragment
                R.id.nav_search -> searchFragment
                R.id.nav_watchlist -> watchlistFragment
                else -> throw IllegalArgumentException("Unexpected itemId")
            }

            selectFragment(fragment)
            true
        }
    }

    override fun onBackPressed() {
        if (selectedIndex != 0) {
            binding.bottomNav.selectedItemId = R.id.nav_home
        } else {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_INDEX, selectedIndex)
    }
}

private const val TAG_HOME_FRAGMENT = "TAG_HOME_FRAGMENT"
private const val TAG_SEARCH_FRAGMENT = "TAG_SEARCH_FRAGMENT"
private const val TAG_WATCHLIST_FRAGMENT = "TAG_WATCHLIST_FRAGMENT"
private const val KEY_SELECTED_INDEX = "KEY_SELECTED_INDEX"