package com.example.mynotes.ui

import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.navigation.Navigation

import com.example.mynotes.R
import com.example.mynotes.db.Note
import com.example.mynotes.db.NoteDatabase
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.coroutines.launch


class AddNoteFragment : BaseFragment() {

    private var note: Note?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            note = AddNoteFragmentArgs.fromBundle(it).note
            title_edit_text.setText(note?.title)
            note_edit_text.setText(note?.note)
        }
        add_note_btn.setOnClickListener{view ->
            val noteTitle = title_edit_text.text.toString().trim()
            val noteBody = note_edit_text.text.toString().trim()

            if (noteTitle.isEmpty()) {
                title_edit_text.error = "title required"
                title_edit_text.requestFocus()
                return@setOnClickListener
            }

            if (noteBody.isEmpty()) {
                note_edit_text.error = "note required"
                note_edit_text.requestFocus()
                return@setOnClickListener
            }


            launch {

                context.let {
                    val mNote = Note(noteTitle, noteBody)

                    if (note == null) {
                        NoteDatabase(it!!).getNoteDao().addNote(mNote)
                        Toast.makeText(it, "Note saved", Toast.LENGTH_LONG).show()
                    } else {
                        Log.e("sss",""+note!!.id)
                        mNote.id = note!!.id
                        NoteDatabase(it!!).getNoteDao().updateNote(mNote)
                        Toast.makeText(it, "Note updated", Toast.LENGTH_LONG).show()
                    }
                    val action = AddNoteFragmentDirections.actionSaveNote()
                    Navigation.findNavController(view).navigate(action)
                }
            }
        }
    }

    private fun deleteNote() {
        AlertDialog.Builder(activity).apply {
            setTitle("Are you sure?")
            setMessage("You cannot undo this operation")
            setPositiveButton("Yes") { _,_ ->
                launch {
                    NoteDatabase(context).getNoteDao().deleteNote(note!!)
                    val action = AddNoteFragmentDirections.actionSaveNote()
                    Navigation.findNavController(requireView()).navigate(action)
                }
            }
            setNegativeButton("No") { _,_ ->
            }
        }.create().show()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete -> if (note != null) deleteNote() else Toast.makeText(activity, "Cannot delete the note", Toast.LENGTH_LONG).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }
}
