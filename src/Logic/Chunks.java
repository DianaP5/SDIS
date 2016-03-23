package Logic;

public class Chunks {
	private static int id;
	private static int number;
	private final int maxLenght=64;
	private static int size;
	
	private int ReplicationDeg;
	
	
    private byte[] chunkNo;

    public Chunks(int id, byte[] chunkNo) {
        this.id = id;
        this.chunkNo = chunkNo;
    }

    public int getId(){
        return id;
    }

    public int getChunkSize() {
        return chunkNo.length;
    }

    public byte[] getChunkNo() {
        return chunkNo;
    }
    
    
    public int getReplicationDeg(){
    	return ReplicationDeg;
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> origin/master
