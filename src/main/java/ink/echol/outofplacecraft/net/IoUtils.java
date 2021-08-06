package ink.echol.outofplacecraft.net;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.Logging;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IoUtils {
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public static class FileRequest {
        private String ident;
        private String fileDest;
        private String fileName;
        private String url;

        public FileRequest(String id, String out, String name, String fromUrl) {
            this.ident = id;
            this.fileDest = out;
            this.fileName = name;
            this.url = fromUrl;
        }

        public String getUrl() {
            return url;
        }

        public String getFileDest() {
            return fileDest;
        }

        public String getFileName() {
            return fileName;
        }
        public String getIdent() {
            return ident;
        }
    }
    public static class PendingRequest {
        private String ident;
        private Future<FileRequest> request;

        public PendingRequest(String id,Future<FileRequest> req) {
            this.request = req;
            this.ident = id;
        }

        public String getIdent() {
            return ident;
        }

        public void setIdent(String ident) {
            this.ident = ident;
        }

        public Future<FileRequest> getRequest() {
            return request;
        }

        public void setRequest(Future<FileRequest> request) {
            this.request = request;
        }
    }

    public static List<PendingRequest> download(List<FileRequest> requests) {
        List<PendingRequest> result = new ArrayList<PendingRequest>();
        for (final FileRequest req : requests) {
            String id = req.getIdent();

            result.add(
                new PendingRequest(id, executorService.submit(new Callable<FileRequest>() {
                    @Override
                    public FileRequest call() throws Exception {
                        return doDownload(req);
                    }
                }))
            );
        }

        return result;
    }

    private static FileRequest doDownload(FileRequest pend) throws Exception {
        String attUrl = pend.getUrl();
        URL url = new URL(attUrl);
        File fileLocation = new File(pend.getFileDest(), pend.getFileName());
        FileUtils.copyURLToFile(url, fileLocation);
        return pend;
    }
}