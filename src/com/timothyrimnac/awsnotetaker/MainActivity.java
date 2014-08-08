package com.timothyrimnac.awsnotetaker;

import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.timothyrimnac.awsnotetaker.data.NoteItem;
import com.timothyrimnac.awsnotetaker.data.NotesDataSource;

public class MainActivity extends ListActivity {
	
	private static final int EDITOR_ACTIVITY_REQUEST = 1101;
	private static final int MENU_DELETE_ID = 1047;
	private int currentNoteId;
	private NotesDataSource datasource;
	List<NoteItem> notesList; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        registerForContextMenu(getListView());
        
        datasource = new NotesDataSource(this);
        
        refreshDislpay();
        
    }


    private void refreshDislpay() {
		notesList = datasource.findAll();
		
		ArrayAdapter<NoteItem> adapter = 
				new ArrayAdapter<NoteItem>(this, R.layout.list_item_layout, notesList);
		
		setListAdapter(adapter);
	}


	@Override
    public boolean  onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == R.id.action_create) {
			createNote();
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void createNote() {
		NoteItem note = NoteItem.getNew();
		Intent intent = new Intent(this, NoteEditorActivity.class);
		
		intent.putExtra("key", note.getKey());
		intent.putExtra("text", note.getText());
		
		startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		NoteItem note = notesList.get(position);
		Intent intent = new Intent(this, NoteEditorActivity.class);
		
		intent.putExtra("key", note.getKey());
		intent.putExtra("text", note.getText());
		
		startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EDITOR_ACTIVITY_REQUEST && resultCode == RESULT_OK) {
			NoteItem note = new NoteItem();
			note.setKey(data.getStringExtra("key"));
			note.setText(data.getStringExtra("text"));
			datasource.update(note);
			refreshDislpay();
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		 
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		currentNoteId = (int)info.id;
		menu.add(0, MENU_DELETE_ID, 0, "Delete");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		if (item.getItemId() == MENU_DELETE_ID) {
			NoteItem note = notesList.get(currentNoteId);
			datasource.remove(note);
			refreshDislpay();
		}
		
		return super.onContextItemSelected(item);
	}
}
