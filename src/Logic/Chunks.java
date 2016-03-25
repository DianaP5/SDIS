package logic;

public class Chunks {
	private String fileId;
	private int number;
	private byte[] content;

    public Chunks(String fileId,int number,byte[] content) {
        this.fileId = fileId;
        this.setNumber(number);
        this.setContent(content);
    }

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
}