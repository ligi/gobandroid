import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.ligi.gobandroid_hd.ui.GobanDroidTVActivity;


public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, GobanDroidTVActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
