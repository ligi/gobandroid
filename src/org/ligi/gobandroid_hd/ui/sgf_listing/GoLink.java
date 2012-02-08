package org.ligi.gobandroid_hd.ui.sgf_listing;

import java.io.File;
import java.io.IOException;

import org.ligi.android.common.files.FileHelper;

public class GoLink {

	public static boolean isGoLink(String fname) {
		return fname.endsWith(".golink");
	}
	
	private String fname="";
	private int move_pos=0;
	
	public GoLink(String fname) {
		this(new File(fname));
	}
	
	public GoLink(File file) {
		
		try {
			String go_lnk=FileHelper.file2String(file);
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
}
