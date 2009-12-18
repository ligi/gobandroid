package org.ligi.gobandroid;

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
    public String[] menu_items= {"Start 9x9" ,"Start 13x13","Start 19x19","Quit" };
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, menu_items));
           
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent go_intent;
            switch (position) {
                case 0:
                    go_intent=new Intent(this,GoActivity.class);
                    go_intent.putExtra("size",(byte)9 );
                    startActivity(go_intent);
                     break;
                case 1:
                    go_intent=new Intent(this,GoActivity.class);
                    go_intent.putExtra("size",(byte)13 );
                    startActivity(go_intent);
                     break;
                case 2:
                    go_intent=new Intent(this,GoActivity.class);
                    go_intent.putExtra("size",(byte)19 );
                    startActivity(go_intent);
                     break;
                case 3:
                    finish();
                    break;
            }
      
    }
    
}