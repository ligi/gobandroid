package org.ligi.gobandroid_hd.ui.sgf_listing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ligi.android.common.files.FileHelper;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.tracedroid.logging.Log;

public class GoLink {

	public static boolean isGoLink(String fname) {
		return fname.endsWith(".golink");
	}
	
	private String fname="";
	private int move_pos=0;
	
	public GoLink(String fname) {
		this(new File(fname.replace("file://", "")));
	}
	
	public GoLink(File file) {

		try {
			String go_lnk=FileHelper.file2String(file);
			go_lnk=go_lnk.replace("\n","").replace("\r","");
			fname=go_lnk; // backup
			String[] arr_content=go_lnk.split(":#");
		   	fname=arr_content[0];
		   	fname=fname.replace("file://", "");
		   	move_pos=Integer.parseInt(arr_content[1]);
		} catch (Exception e) {}
	}
	
	/**
	 * returns the move Depth/pos
	 * should only be used for displaying
	 *  
	 * @return
	 */
	public int getMoveDepth() {
		return move_pos;
	}
	
	
	public boolean linksToDirectory() {
		return new File(fname).isDirectory();
	}
	
	/**
	 * TODO care for remote content
	 * 
	 * @return
	 */
	public String getSGFString() {
		try {
			return FileHelper.file2String(new File(fname));
		} catch (IOException e) {
			return "";
		}
	}
	
	public String getFileName() {
		return fname;
	}
	
	public static void saveGameToGoLink(GoGame game,String golink_path,String golink_fname) {

		int move_pos=game.getActMove().getMovePos();
		
		File f = new File(golink_path);
		
		if (!f.isDirectory())
			f.mkdirs();
		
		try {
			f.createNewFile();
			f=new File(golink_path + "/"+golink_fname);
			
			FileWriter sgf_writer = new FileWriter(f);
			
			BufferedWriter out = new BufferedWriter(sgf_writer);
			
			out.write(game.getMetaData().getFileName()+":#"+move_pos);
			out.close();
			sgf_writer.close();
		}
		catch (IOException e) {
			Log.i(""+e);
		}
	}
}
