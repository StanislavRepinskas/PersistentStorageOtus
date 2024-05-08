package com.otus.persistentstorage

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val galleryAdapter = GalleryAdapter()

    private val viewModel: MainViewModel by viewModels(
        factoryProducer = { MainViewModel.Factory(applicationContext) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val progressView: View = findViewById(R.id.progress)
        val galleryView: RecyclerView = findViewById(R.id.gallery)
        galleryView.adapter = galleryAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.galleryItemsFlow.collect {
                    galleryAdapter.submitList(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showProgressFlow.collect {
                    progressView.isVisible = it
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.update -> {
                viewModel.update(isForce = true)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
