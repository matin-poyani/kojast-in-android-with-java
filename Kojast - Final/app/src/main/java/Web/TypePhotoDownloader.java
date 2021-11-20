package Web;

import java.io.File;

import ir.ncis.kojast.App;

public class TypePhotoDownloader {
    private OnCompleteListener onCompleteListener;
    private String markerFileName, offFileName, onFileName;
    private boolean markerDownloaded, offDownloaded, onDownloaded;

    public TypePhotoDownloader(int typeId) {
        markerFileName = String.valueOf(typeId) + "_marker.png";
        offFileName = String.valueOf(typeId) + "_off.png";
        onFileName = String.valueOf(typeId) + "_on.png";
    }

    public void download() {
        if (new File(App.DIR_DATA + "/photos/" + markerFileName).exists()) {
            markerDownloadCompleted();
        } else {
            new Downloader()
                    .setDownloadProgressListener(new Downloader.DownloadProgressListener() {
                        @Override
                        public void progressDownload(int downloaded, int percent, int total) {
                            if (percent == 100) {
                                markerDownloadCompleted();
                            }
                        }
                    })
                    .setLocalFileName(markerFileName)
                    .setServerFileName(markerFileName)
                    .download();
        }
        if (new File(App.DIR_DATA + "/photos/" + offFileName).exists()) {
            offDownloadCompleted();
        } else {
            new Downloader()
                    .setDownloadProgressListener(new Downloader.DownloadProgressListener() {
                        @Override
                        public void progressDownload(int downloaded, int percent, int total) {
                            if (percent == 100) {
                                offDownloadCompleted();
                            }
                        }
                    })
                    .setLocalFileName(offFileName)
                    .setServerFileName(offFileName)
                    .download();
        }
        if (new File(App.DIR_DATA + "/photos/" + onFileName).exists()) {
            onDownloadCompleted();
        } else {
            new Downloader()
                    .setDownloadProgressListener(new Downloader.DownloadProgressListener() {
                        @Override
                        public void progressDownload(int downloaded, int percent, int total) {
                            if (percent == 100) {
                                onDownloadCompleted();
                            }
                        }
                    })
                    .setLocalFileName(onFileName)
                    .setServerFileName(onFileName)
                    .download();
        }
    }

    private void markerDownloadCompleted() {
        markerDownloaded = true;
        if (onCompleteListener != null && offDownloaded && onDownloaded) {
            onCompleteListener.OnComplete();
        }
    }

    private void offDownloadCompleted() {
        offDownloaded = true;
        if (onCompleteListener != null && markerDownloaded && onDownloaded) {
            onCompleteListener.OnComplete();
        }
    }

    private void onDownloadCompleted() {
        onDownloaded = true;
        if (onCompleteListener != null && markerDownloaded && offDownloaded) {
            onCompleteListener.OnComplete();
        }
    }

    public TypePhotoDownloader setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
        return this;
    }

    public interface OnCompleteListener {
        void OnComplete();
    }
}
