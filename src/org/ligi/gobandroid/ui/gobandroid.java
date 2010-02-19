package org.ligi.gobandroid.ui;


import org.ligi.gobandroid.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * This is the main Activity of gobandroid
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
**/

public class gobandroid extends ListActivity {
    public String[] menu_items= {"Start Game","Settings" ,"Quit", /*"Load Game" */ };
    
    private final static int MENU_GAMESTART=0;
    private final static int MENU_SETTINGS=1;
    private final static int MENU_QUIT=2;
    private final static int MENU_LOADGAME=3;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        GoPrefs.init(this);
        
        setContentView(R.layout.main);
        this.setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, menu_items));
           
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent go_intent;
            switch (position) {
                case MENU_GAMESTART:
                    go_intent=new Intent(this,GoSetupActivity.class);
                    startActivity(go_intent);
                     break;
    
                case MENU_LOADGAME:
                    go_intent=new Intent(this,SGFListActivity.class);
                    startActivity(go_intent);
                    break;
    
                case MENU_SETTINGS:
                    go_intent=new Intent(this,GoPrefsActivity.class);
                    startActivity(go_intent);
                    break;
                     
                case MENU_QUIT:
                    finish();
                    break;
            }
      
    }
    
}