package org.ligi.gobandroid.logic;

public class GoGameMetadata {

	private String name="";
	private String result="";
	
	private String black_name="";
	private String black_rank="";
	private String white_name="";
	private String white_rank="";
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setWhiteRank(String white_rank) {
		this.white_rank = white_rank;
	}
	public String getWhiteRank() {
		return white_rank;
	}
	public void setWhiteName(String white_name) {
		this.white_name = white_name;
	}
	public String getWhiteName() {
		return white_name;
	}
	public void setBlackName(String black_name) {
		this.black_name = black_name;
	}
	public String getBlackName() {
		return black_name;
	}
	public void setBlackRank(String black_rank) {
		this.black_rank = black_rank;
	}
	public String getBlackRank() {
		return black_rank;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getResult() {
		return result;
	}
}
