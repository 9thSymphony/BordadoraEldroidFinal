package com.bordadoraeldroidfinal

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bordadoraeldroidfinal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val bookList = mutableListOf<Book>()
    private var selectedBookIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener { addBook() }
        binding.btnEdit.setOnClickListener { editBook() }
        binding.btnDelete.setOnClickListener { deleteBook() }
        binding.btnSearch.setOnClickListener { searchBooks() }

        val bookTitles = bookList.map { it.title }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bookTitles)
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                selectedBookIndex = position
                loadBookDetails(bookList[position])
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Handle when nothing is selected if needed
            }
        }

        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                sortBooks(position)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Handle when nothing is selected if needed
            }
        }
    }

    private fun addBook() {
        val title = binding.etTitle.text.toString().trim()
        val author = binding.etAuthor.text.toString().trim()
        val genre = binding.etGenre.text.toString().trim()

        if (title.isNotEmpty() && author.isNotEmpty() && genre.isNotEmpty()) {
            val book = Book(title, author, genre)
            bookList.add(book)

            clearFields()
            updateSpinner()
        } else {
            Toast.makeText(this, "Please fill in all of the fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editBook() {
        if (selectedBookIndex != -1) {
            val editedTitle = binding.etTitle.text.toString().trim()
            val editedAuthor = binding.etAuthor.text.toString().trim()
            val editedGenre = binding.etGenre.text.toString().trim()

            if (editedTitle.isNotEmpty() && editedAuthor.isNotEmpty() && editedGenre.isNotEmpty()) {
                val editedBook = Book(editedTitle, editedAuthor, editedGenre)
                bookList[selectedBookIndex] = editedBook

                clearFields()
                updateSpinner()
                loadBookDetails(editedBook)
            } else {
                Toast.makeText(this, "Please fill in all of the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteBook() {
        if (selectedBookIndex != -1) {
            bookList.removeAt(selectedBookIndex)
            clearFields()
            updateSpinner()
        }
    }

    private fun loadBookDetails(book: Book) {
        binding.etTitle.setText(book.title)
        binding.etAuthor.setText(book.author)
        binding.etGenre.setText(book.genre)
    }

    private fun clearFields() {
        binding.etTitle.text.clear()
        binding.etAuthor.text.clear()
        binding.etGenre.text.clear()
    }

    private fun updateSpinner() {
        val bookTitles = bookList.map { it.title }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bookTitles)
        binding.spinner.adapter = adapter
    }

    private fun searchBooks() {
        val query = binding.etSearch.text.toString().toLowerCase()
        val searchResults = bookList.filter {
            it.title.toLowerCase().contains(query) ||
                    it.author.toLowerCase().contains(query) ||
                    it.genre.toLowerCase().contains(query)
        }

        val searchResultsTitles = searchResults.map {
            "Title: ${it.title}, Author: ${it.author}, Genre: ${it.genre}"
        }
        val adapter = binding.spinner.adapter as ArrayAdapter<String>
        adapter.clear()
        adapter.addAll(searchResultsTitles)
        adapter.notifyDataSetChanged()

        val tvSearchResults = findViewById<TextView>(R.id.tvSearchResults)
        tvSearchResults.text = searchResultsTitles.joinToString("\n")

        if (searchResults.isNotEmpty()) {
            selectedBookIndex = bookList.indexOf(searchResults.first())
            loadBookDetails(searchResults.first())
        } else {
            clearFields()
        }
    }

    private fun sortBooks(sortOption: Int) {
        when (sortOption) {
            0 -> bookList.sortBy { it.title }
            1 -> bookList.sortBy { it.author }
            2 -> bookList.sortBy { it.genre }
        }
        updateSpinner()
    }
}
