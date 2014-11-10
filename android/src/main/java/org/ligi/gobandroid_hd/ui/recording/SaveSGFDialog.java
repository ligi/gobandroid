package org.ligi.gobandroid_hd.ui.recording;

import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGameMetadata;
import org.ligi.gobandroid_hd.logic.sgf.SGFWriter;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Dialog to save a game to SGF file and ask the user about how in here
 * <p/>
 * TODO check if file exists
 *
 * @author ligi
 */
public class SaveSGFDialog extends GobandroidDialog {

    private EditText fname_et;
    private GobandroidFragmentActivity context;
    private CheckBox override_checkbox;

    public SaveSGFDialog(GobandroidFragmentActivity _context) {
        super(_context);

        context = _context;

        setContentView(R.layout.dialog_save_sgf);

        setIconResource(R.drawable.save);
        TextView intro_text = (TextView) findViewById(R.id.intro_txt);

        override_checkbox = (CheckBox) findViewById(R.id.override_checkbox);

        intro_text.setText(String.format(context.getResources().getString(R.string.save_sgf_question), context.getSettings().getSGFSavePath()));

        fname_et = (EditText) findViewById(R.id.sgf_name_edittext);

        class SaveSGFOnClickListener implements DialogInterface.OnClickListener {
            public void onClick(DialogInterface dialog, int whichButton) {
                String fname = getCompleteFileName();
                boolean res = SGFWriter.saveSGF(App.getGame(), fname);

                if (res)
                    Toast.makeText(context, String.format(context.getString(R.string.file_saved), fname), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, String.format(context.getString(R.string.file_not_saved), fname), Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }

        }

        setPositiveButton(android.R.string.ok, new SaveSGFOnClickListener());

        fname_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                setPositiveButtonAndOverrideCheckboxEnabledByExistenceOfFile();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

        });

        override_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setPositiveButtonAndOverrideCheckboxEnabledByExistenceOfFile();

            }

        });

        // get the old filename from the metadata
        String old_fname = App.getGame().getMetaData().getFileName();

        if ((old_fname != null) && (!old_fname.equals(""))) {
            String suggested_name = old_fname.replace(".sgf", "");
            if (suggested_name.startsWith(context.getSettings().getSGFSavePath()))
                suggested_name = suggested_name.substring(context.getSettings().getSGFSavePath().length());
            fname_et.setText(suggested_name);
        }

        final GoGameMetadata game_meta = App.getGame().getMetaData();

        /**
         * this is a OnClickListener to add Stuff to the FileName like
         * date/gamename/...
         */
        class FileNameAdder implements View.OnClickListener {

            private String getTextByButtonId(int btn_resId) {
                switch (btn_resId) {
                    case R.id.button_add_date:
                        SimpleDateFormat date_formatter = new SimpleDateFormat("yyyy.MM.dd");
                        return date_formatter.format(new Date());
                    case R.id.button_add_time:
                        SimpleDateFormat time_formatter = new SimpleDateFormat("H'h'm'm'");
                        return time_formatter.format(new Date());
                    case R.id.button_add_gamename:
                        return game_meta.getName();
                    case R.id.button_add_players:
                        return game_meta.getBlackName() + "_vs_" + game_meta.getWhiteName();

                    default:
                        return null;
                }
            }

            @Override
            public void onClick(View v) {
                String toAdd = getTextByButtonId(v.getId());
                if (toAdd != null) {
                    String text = fname_et.getText().toString();
                    int cursorPos = fname_et.getSelectionStart();
                    StringBuilder sb = new StringBuilder();
                    sb.append(text.substring(0, cursorPos)).append(toAdd).append(text.substring(cursorPos, fname_et.length()));
                    fname_et.setText(sb.toString());
                    fname_et.setSelection(cursorPos + toAdd.length());
                }
            }

        }
        FileNameAdder adder = new FileNameAdder();

        ((Button) (findViewById(R.id.button_add_date))).setOnClickListener(adder);
        ((Button) (findViewById(R.id.button_add_time))).setOnClickListener(adder);
        Button add_name_btn = ((Button) (findViewById(R.id.button_add_gamename)));
        Button players_name_btn = ((Button) (findViewById(R.id.button_add_players)));

        if (game_meta.getName().equals(""))
            add_name_btn.setVisibility(View.GONE);
        else
            add_name_btn.setOnClickListener(adder);

        if (game_meta.getBlackName().equals("") && game_meta.getWhiteName().equals(""))
            players_name_btn.setVisibility(View.GONE);
        else
            players_name_btn.setOnClickListener(adder);

        setTitle(R.string.save_sgf);

        setPositiveButtonAndOverrideCheckboxEnabledByExistenceOfFile();

    }

    private void setPositiveButtonAndOverrideCheckboxEnabledByExistenceOfFile() {

        String fname = getCompleteFileName();

        if (fname == null) { // we got no filename from user

            override_checkbox.setVisibility(View.GONE); // no overwrite without
            // filename
            getPositiveButton().setEnabled(false); // should not save without a
            // filename
            return;
        }

        File wanted_file = new File(getCompleteFileName());
        boolean target_file_exist = wanted_file.exists();
        override_checkbox.setVisibility((target_file_exist && !wanted_file.isDirectory()) ? View.VISIBLE : View.GONE);
        getPositiveButton().setEnabled(!target_file_exist || override_checkbox.isChecked());
    }

    /**
     * @return the filename with path and file extension - returns null when
     * there is no filename given
     */
    private String getCompleteFileName() {
        String fname = fname_et.getText().toString();

        if (fname.length() == 0)
            return null;

        fname += ".sgf"; // append filename extension

        if (fname.startsWith("/"))
            return fname;
        else
            return context.getSettings().getSGFSavePath() + fname;
    }

}
