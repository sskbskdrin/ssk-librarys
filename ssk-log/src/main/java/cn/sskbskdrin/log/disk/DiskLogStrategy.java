package cn.sskbskdrin.log.disk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cn.sskbskdrin.log.LogStrategy;

/**
 * Abstract class that takes care of background threading the file log operation on Android.
 * implementing classes are free to directly perform I/O operations there.
 *
 * @author ex-keayuan001
 */
public class DiskLogStrategy implements LogStrategy {

    private static final int WHAT_CLOSE_FILE = 1001;

    private int mMaxFileSize = 1024 * 1024;// 1M
    private String fileName = "log";

    private final String mPath;
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private WriteThread thread;

    public DiskLogStrategy(String path) {
        this(path, 0);
    }

    public DiskLogStrategy(String path, int maxFile) {
        mPath = path;
        if (maxFile > mMaxFileSize) {
            mMaxFileSize = maxFile;
        }
    }

    public DiskLogStrategy fileName(String name) {
        fileName = name == null ? "name" : name;
        return this;
    }

    @Override
    public void print(int level, String tag, String message) {
        queue.offer(tag + ":" + message);
        if (thread == null || !thread.isAlive()) {
            synchronized (DiskLogStrategy.class) {
                if (thread == null || !thread.isAlive()) {
                    thread = new WriteThread(mPath, mMaxFileSize, fileName);
                    thread.start();
                }
            }
        }
    }

    private class WriteThread extends Thread {

        private final int maxFileSize;
        private final String folder;
        private final String fileName;
        private RandomAccessFile out;

        WriteThread(String folder, int maxFileSize, String fileName) {
            this.folder = folder;
            this.maxFileSize = maxFileSize;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String item = queue.take();
                    if (out == null) {
                        out = getFileOutputStream(folder, this.fileName);
                    }
                    out.write(item.getBytes());
                    if (out.length() > maxFileSize) {
                        out.close();
                        out = null;
                    }
                } catch (InterruptedException | FileNotFoundException e) {
                    e.printStackTrace();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private RandomAccessFile getFileOutputStream(String folderName, String fileName) throws FileNotFoundException {
            File folder = new File(folderName);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File newFile = new File(folder, fileName + ".log");
            if (newFile.exists()) {
                long length = newFile.length();
                if (length > maxFileSize) {
                    File temp;
                    int newFileCount = 0;
                    do {
                        temp = new File(folder, String.format("%s_%s.log", fileName, newFileCount++));
                    } while (temp.exists());
                    newFile.renameTo(temp);
                    newFile = new File(folder, fileName + ".log");
                }
            }

            return new RandomAccessFile(newFile, "rw");
        }
    }
}
