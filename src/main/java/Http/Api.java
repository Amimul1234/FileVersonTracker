package Http;

import chunkTransport.ChunkTransporter;
import metaData.FileMetaData;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {

    @POST("/v1/fileVersion/fileChunkReceiver")
    Call<ResponseBody> saveFileChunk( @Body ChunkTransporter chunkTransporter);


    @POST("/v1/fileVersion/fileMetaDataReceiver")
    Call<ResponseBody> saveFileMetaData(@Body FileMetaData fileMetaData);
}
