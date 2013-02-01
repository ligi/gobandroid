package org.ligi.gobandroid_hd.ui.online;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.api.services.cloudgoban.model.Game;
import com.google.api.services.cloudgoban.model.GameCollection;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.etc.GobandroidConfiguration;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameMetadata;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.SGFLoadActivity;

class ViewOnlineGameListFragment extends ListFragment {

    private GameCollection list;
    private LayoutInflater mLayoutInflater;
    private ViewListAdapter mAdapter;

    public ViewOnlineGameListFragment(GameCollection list) {
        this.list = list;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutInflater = getLayoutInflater(savedInstanceState);
        mAdapter = new ViewListAdapter();
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent=new Intent(getActivity(), SGFLoadActivity.class);
        intent.setData(Uri.parse(GobandroidConfiguration.CLOUD_GOBAN_URL_BASE +  ((Game)mAdapter.getItem(position)).getEncodedKey()));
        getActivity().startActivity(intent);

        super.onListItemClick(l, v, position, id);    //To change body of overridden methods use File | Settings | File Templates.
    }

    class ViewListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.getItems().size() - 1;

        }

        @Override
        public Object getItem(int position) {
            return list.getItems().get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = mLayoutInflater.inflate(R.layout.online_game_details_list_item, null);
            Game online_game = (Game) getItem(position);

            TextView white_tv = (TextView) v.findViewById(R.id.player_white);
            TextView black_tv = (TextView) v.findViewById(R.id.player_black);
            TextView time_tv = (TextView) v.findViewById(R.id.time_tv);
            TextView size_tv = (TextView) v.findViewById(R.id.size_tv);

            View white_block=v.findViewById(R.id.white_block);
            View black_block=v.findViewById(R.id.black_block);


            GoGame game = SGFHelper.sgf2game(online_game.getSgf().getValue(), null);

            GoGameMetadata game_meta = game.getMetaData();

            if (game_meta.getWhiteName().equals(""))
                white_block.setVisibility(View.GONE);
            else {
                String white_label= game_meta.getBlackName();
                if (!game_meta.getWhiteRank().equals(""))
                    white_label+=" ("+game_meta.getWhiteRank()+") ";

                white_tv.setText(white_label);
            }


            if (game_meta.getBlackName().equals(""))
                black_block.setVisibility(View.GONE);
            else  {

                String black_label= game_meta.getBlackName();
                if (!game_meta.getBlackRank().equals(""))
                    black_label+=" ("+game_meta.getBlackRank()+") ";
                black_tv.setText(black_label);
            }

            size_tv.setText("" + game.getSize() + "x" + game.getSize());
            time_tv.setText(DateUtils.getRelativeTimeSpanString(online_game.getUpdated().getValue(), System.currentTimeMillis(), 0));
            return v;
        }
    }


}
