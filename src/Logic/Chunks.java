package logic;

public class Chunks {
	@SuppressWarnings("unused")
	private String fileId;
	private int number;
	private byte[] content;

    public Chunks(String fileId,int number,byte[] s1) {
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

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] s1) {
		this.content = s1;
	}
}