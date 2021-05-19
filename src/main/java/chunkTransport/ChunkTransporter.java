package chunkTransport;

import java.io.Serializable;

public class ChunkTransporter implements Serializable {
    private String hashedName;
    private byte[] chunk;

    public ChunkTransporter() {
    }

    public ChunkTransporter( String hashedName, byte[] chunk ) {
        this.hashedName = hashedName;
        this.chunk = chunk;
    }

    public String getHashedName() {
        return hashedName;
    }

    public void setHashedName( String hashedName ) {
        this.hashedName = hashedName;
    }

    public byte[] getChunk() {
        return chunk;
    }

    public void setChunk( byte[] chunk ) {
        this.chunk = chunk;
    }
}
