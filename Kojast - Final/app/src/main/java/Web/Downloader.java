package Web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ir.ncis.kojast.App;

class Downloader {
    private int downloaded, percent, total;
    private String serverFileName, localFileName;
    private DownloadProgressListener downloadProgressListener;

    void download() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(App.URL_PHOTOS + Downloader.this.serverFileName);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoOutput(true);
                    connection.connect();
                    total = connection.getContentLength();
                    String path = App.DIR_DATA + "/photos/";
                    new File(path).mkdirs();
                    File file = new File(path + localFileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    FileOutputStream outputStream = new FileOutputStream(path + localFileName);
                    InputStream inputStream = connection.getInputStream();
                    byte[] buffer = new byte[App.BUFFER_SIZE];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, len);
                        downloaded += len;
                        percent = (int) (100.0f * (float) downloaded / total);
                        if (downloadProgressListener != null) {
                            App.HANDLER.post(new Runnable() {
                                @Override
                                public void run() {
                                    downloadProgressListener.progressDownload(downloaded, percent, total);
                                }
                            });
                        }
                    }
                    outputStream.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Downloader setDownloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
        return this;
    }

    Downloader setServerFileName(String serverFileName) {
        this.serverFileName = serverFileName;
        return this;
    }

    Downloader setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
        return this;
    }

    public interface DownloadProgressListener {
        void progressDownload(int downloaded, int percent, int total);
    }
}
