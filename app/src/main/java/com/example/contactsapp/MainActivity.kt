package com.example.contactsapp

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast

class MainActivity : Activity() {

    private val contacts = mutableListOf<Contact>()
    private lateinit var adapter: ArrayAdapter<Contact>
    private val originalContacts = mutableListOf<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val contactListView: ListView = findViewById(R.id.contactListView)
        val addContactButton: Button = findViewById(R.id.addContactButton)
        val searchView: SearchView = findViewById(R.id.searchView)

        adapter = object : ArrayAdapter<Contact>(this, R.layout.contact_item, contacts) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false)
                val contact = getItem(position)!!

                val nameTextView: android.widget.TextView = view.findViewById(R.id.contactName)
                val detailsTextView: android.widget.TextView = view.findViewById(R.id.contactDetails)

                nameTextView.text = contact.name
                detailsTextView.text = "${contact.number} | ${contact.email}"

                return view
            }
        }

        contactListView.adapter = adapter

        contactListView.setOnItemClickListener { _, _, position, _ ->
            val contact = contacts[position]
            showEditContactDialog(contact, position)
        }

        addContactButton.setOnClickListener {
            showAddContactDialog()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterContacts(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterContacts(newText)
                return true
            }
        })
    }

    private fun filterContacts(query: String?) {
        val filteredContacts = originalContacts.filter { contact ->
            contact.name.contains(query ?: "", ignoreCase = true) ||
                    contact.number.contains(query ?: "", ignoreCase = true) ||
                    contact.email.contains(query ?: "", ignoreCase = true)
        }
        contacts.clear()
        contacts.addAll(filteredContacts)
        adapter.notifyDataSetChanged()
    }

    private fun showAddContactDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_contact, null)
        val nameEditText: EditText = dialogView.findViewById(R.id.nameEditText)
        val numberEditText: EditText = dialogView.findViewById(R.id.numberEditText)
        val emailEditText: EditText = dialogView.findViewById(R.id.emailEditText)

        AlertDialog.Builder(this)
            .setTitle("Adicionar Contato")
            .setView(dialogView)
            .setPositiveButton("Adicionar") { _, _ ->
                val name = nameEditText.text.toString()
                val number = numberEditText.text.toString()
                val email = emailEditText.text.toString()
                val contact = Contact(contacts.size + 1, name, number, email)
                contacts.add(contact)
                originalContacts.add(contact)
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditContactDialog(contact: Contact, position: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_contact, null)
        val nameEditText: EditText = dialogView.findViewById(R.id.nameEditText)
        val numberEditText: EditText = dialogView.findViewById(R.id.numberEditText)
        val emailEditText: EditText = dialogView.findViewById(R.id.emailEditText)

        nameEditText.setText(contact.name)
        numberEditText.setText(contact.number)
        emailEditText.setText(contact.email)

        AlertDialog.Builder(this)
            .setTitle("Editar Contato")
            .setView(dialogView)
            .setPositiveButton("Salvar") { _, _ ->
                contact.name = nameEditText.text.toString()
                contact.number = numberEditText.text.toString()
                contact.email = emailEditText.text.toString()
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Deletar") { _, _ ->
                contacts.removeAt(position)
                originalContacts.removeAt(position)
                adapter.notifyDataSetChanged()
            }
            .setNeutralButton("Cancelar", null)
            .show()
    }
}

data class Contact(var id: Int, var name: String, var number: String, var email: String)
