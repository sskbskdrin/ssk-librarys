package cn.sskbskdrin.lib.demo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import cn.sskbskdrin.util.Task;
import cn.sskbskdrin.util.ThreadUtils;

/**
 * Created by keayuan on 2021/3/12.
 *
 * @author keayuan
 */
class Work {
    private static final String TAG = "Work";

    public static class HolderWorker extends Worker {

        public HolderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            Log.d(TAG, "doWork: finish");
            return Result.success();
        }
    }

    public static class UploadFileWorker extends Worker {

        public UploadFileWorker(Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            Data inputData = getInputData();
            String filePath = inputData.getString("file");
            Log.d(TAG, "doWork: " + filePath);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Data.Builder builder = new Data.Builder();
            builder.putLong(filePath + "i", System.currentTimeMillis());
            Data data = builder.build();
            return Result.success(data);
        }

        @Override
        public void onStopped() {
            Log.d(TAG, "onStopped: ");
        }
    }

    public void en() {
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            ThreadUtils.getSinglePool().execute(new Task.Builder<>(() -> {
                Thread.sleep(1000);
                Log.d(TAG, "run: " + finalI);
                return "jlskdf===" + finalI;
            }).success((stringTask, s) -> {})
                .error((task, throwable) -> Log.d(TAG, "error: "))
                .cancel(task -> Log.d(TAG, "cancel: "))
                //                .complete(task -> Log.d(TAG, "complete: "))
                .build());
        }
        OneTimeWorkRequest.Builder builder = new OneTimeWorkRequest.Builder(UploadFileWorker.class);
        for (int i = 0; i < 2; i++) {
            OneTimeWorkRequest request = builder.setInputData(new Data.Builder().putString("file", i + "-test-")
                .build()).build();
            WorkContinuation continuation = WorkManager.getInstance()
                .beginUniqueWork("name" + i, ExistingWorkPolicy.KEEP, request);
            for (int j = 0; j < 10; j++) {
                request = builder.setInputData(new Data.Builder().putString("file", i + "-test" + "-" + j).build())
                    .addTag("tag-" + i)
                    .build();
                continuation = continuation.then(request);
            }
            //            continuation.then(new OneTimeWorkRequest.Builder(HolderWorker.class).build()).enqueue();
        }
    }


    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    private final Executor mMainThreadExecutor = mMainThreadHandler::post;

}
