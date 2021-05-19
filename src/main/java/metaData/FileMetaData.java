package metaData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileMetaData implements Serializable
{
    private String fileId;
    private double fileSize;
    private String fileName;
    private String fileExtension;
    private String filePath;
    private String fileHash;
    private String createdAt;
    private String lastModified;
    private List<Chunk> chunkList = new ArrayList<>();

    public FileMetaData() {
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId( String fileId ) {
        this.fileId = fileId;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize( double fileSize ) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName( String fileName ) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension( String fileExtension ) {
        this.fileExtension = fileExtension;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath( String filePath ) {
        this.filePath = filePath;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash( String fileHash ) {
        this.fileHash = fileHash;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt( String createdAt ) {
        this.createdAt = createdAt;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified( String lastModified ) {
        this.lastModified = lastModified;
    }

    public List<Chunk> getChunkList() {
        return chunkList;
    }

    public synchronized void addChunk( Chunk chunk)
    {
        chunkList.add(chunk);
    }
}
