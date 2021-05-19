import Http.RetrofitClient;
import chunkTransport.ChunkTransporter;
import metaData.Chunk;
import metaData.FileMetaData;
import okhttp3.ResponseBody;
import org.apache.commons.io.FilenameUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public record FileChunkUtil(String source)
{
    public FileChunkUtil( String source ) {
        this.source = source;
        try {
            copyUsingChunks();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void copyUsingChunks() throws IOException{
        //counting the processor
        int coreCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(coreCount);


        try (BufferedInputStream bufferedInputStream =
                     new BufferedInputStream(new FileInputStream(source)))
        {
            //Creating metadata of the file
            FileMetaData fileMetaData = new FileMetaData();

            Runnable fileMetaDataCreatorRunnable = () ->
            {
                fileMetaData.setFileId(UUID.randomUUID().toString());

                //file length in mb
                fileMetaData.setFileSize(new File(source).length() / (1048576.0));

                fileMetaData.setFileName(FilenameUtils.getBaseName(source));
                fileMetaData.setFileExtension(FilenameUtils.getExtension(source));
                fileMetaData.setFilePath(FilenameUtils.getFullPath(source));

                try {
                    fileMetaData.setFileHash(getFileChecksum(new File(source)));
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();

                fileMetaData.setCreatedAt(formatter.format(date));
                fileMetaData.setLastModified(formatter.format(date));
            };

            executorService.execute(fileMetaDataCreatorRunnable);

            byte[] buffer = new byte[1048576]; //1mb is performing good.

            int i=0;

            while (bufferedInputStream.read(buffer, 0, buffer.length) != -1) {

                int finalI = i;

                Runnable runnable = () ->
                {
                    Chunk chunk = new Chunk();

                    chunk.setChunkUUID(UUID.randomUUID().toString());
                    chunk.setChunkNumber(finalI);
                    chunk.setVersionNumber(1);

                    try {
                        String hashedName = getFileChunkHash(buffer);
                        chunk.setChunkHashValue(hashedName);

                        ChunkTransporter chunkTransporter = new ChunkTransporter();

                        chunkTransporter.setChunk(buffer);
                        chunkTransporter.setHashedName(hashedName);

                        fileMetaData.addChunk(chunk);

                        RetrofitClient.getInstance().getApi()
                                .saveFileChunk(chunkTransporter)
                                .enqueue(new Callback<>()
                                {
                                    @Override
                                    public void onResponse( Call<ResponseBody> call, Response<ResponseBody> response )
                                    {
                                        if (!response.isSuccessful()) {
                                            System.out.println("Failed to push chunk : "+ finalI);
                                        }
                                    }

                                    @Override
                                    public void onFailure( Call<ResponseBody> call, Throwable throwable ) {
                                        throwable.printStackTrace();
                                    }
                                });

                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                };

                executorService.submit(runnable);
                i++;
            }

            executorService.shutdown();

            //Waiting for all threads to close
            try {
                if (!executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
                else
                {
                    RetrofitClient.getInstance().getApi()
                            .saveFileMetaData(fileMetaData)
                            .enqueue(new Callback<>() {
                                @Override
                                public void onResponse( Call<ResponseBody> call, Response<ResponseBody> response ) {
                                    if(response.isSuccessful())
                                    {
                                        System.out.println("Successfully uploaded to server");
                                    }
                                    else {
                                        System.out.println("Failed to upload to server");
                                    }
                                }

                                @Override
                                public void onFailure( Call<ResponseBody> call, Throwable throwable ) {
                                    throwable.printStackTrace();
                                }
                            });
                }
            } catch (InterruptedException ex) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }


    //get sha-256 of chunks
    private String getFileChunkHash( byte[] chunk ) throws NoSuchAlgorithmException
    {
        MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");

        byte[] hashedArray = shaDigest.digest(chunk);

        Formatter formatter = new Formatter();

        for (byte b : hashedArray) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }


    //get sha-256 of the whole file
    private String getFileChecksum( File file ) throws IOException, NoSuchAlgorithmException
    {
        MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");

        BufferedInputStream bufferedInputStream =
                new BufferedInputStream(new FileInputStream(file));

        byte[] byteArray = new byte[1024];
        int bytesCount;

        while ((bytesCount = bufferedInputStream.read(byteArray)) != -1) {
            shaDigest.update(byteArray, 0, bytesCount);
        }

        bufferedInputStream.close();

        byte[] bytes = shaDigest.digest();

        StringBuilder sb = new StringBuilder();

        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
