package metaData;

import java.io.Serializable;

public class Chunk implements Serializable {
    private String chunkUUID;
    private int chunkNumber;
    private int versionNumber;
    private String chunkHashValue;

    public Chunk() {
    }

    public Chunk( String chunkUUID, int chunkNumber, int versionNumber, String chunkHashValue ) {
        this.chunkUUID = chunkUUID;
        this.chunkNumber = chunkNumber;
        this.versionNumber = versionNumber;
        this.chunkHashValue = chunkHashValue;
    }

    public String getChunkUUID() {
        return chunkUUID;
    }

    public void setChunkUUID( String chunkUUID ) {
        this.chunkUUID = chunkUUID;
    }

    public int getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber( int chunkNumber ) {
        this.chunkNumber = chunkNumber;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber( int versionNumber ) {
        this.versionNumber = versionNumber;
    }

    public String getChunkHashValue() {
        return chunkHashValue;
    }

    public void setChunkHashValue( String chunkHashValue ) {
        this.chunkHashValue = chunkHashValue;
    }
}
