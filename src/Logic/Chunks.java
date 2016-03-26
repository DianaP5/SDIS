package logic;

public class Chunks {
	private String fileId;
	private int number;
	private String content;

    public Chunks(String fileId,int number,String s1) {
        this.fileId = fileId;
        this.setNumber(number);
        this.setContent(s1);
    }

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String s1) {
		this.content = s1;
	}
}