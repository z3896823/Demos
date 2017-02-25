package org.zyb.servicetest.utils;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/20.
 *
 * 这个下载的异步任务必须开在服务里才能实现后台下载的功能
 * 不然在活动中一旦活动不在前台，异步任务也暂停了
 *
 * 异步任务的三个泛型参数：Params，Progress, Result
 *
 * doInBackground()
 * 主要功能:进行下载，并将结果返回给onPostExecute()
 * 主要逻辑：先看看本地是否已存在要下载的文件，如果有，看一下长度，并跟要下载的文件的长度进行对比
 *         以此决定是否需要下载以及从和出开始断点续传
 * 一些逻辑细节：对下载结果的处理
 *             以1024个字节为单位进行文件的写入，每次写入新的1024字节时检查用户是否调用了暂停或删除方法
 *             如果有，将方法return掉，并且return之前执行finally里的代码：将输入流关闭，将savedFile关闭，
 *             如果是cancel，还要将文件删除
 *
 * onPostExecute()
 * 主要功能：对下载结果进行判断并进行相应的回调，以通知服务
 *
 * 如何将正在进行的下载暂停或取消？
 * 文件的下载以1024字节为单位进行写入本地，每次写入新的1024字节时检查isPause和isCancel变量的值
 * 一旦发现这两个变量为真，则不再写入新的1024字节，而是return一个状态给onPostExecute
 *
 *
 * 补充一下Java IO操作的知识：
 * 读取数据和写入数据可以分别用FileInputStream和FileOutputStream
 * 对应的方法分别为read()和write()，两个方法参数均为三个：字节流数组，偏移量（一般为0），字节流的长度
 * 对read()方法来说，偏移量为0的话字节流的长度一般为数组长度；对write()方法来说，字节流的长度要为read()方法的返回值，即读多少写多少，否则最后一次读取的时候极有可能写入空字符
 * 使用循环来转移字节流，用read()方法的返回值来中断循环,当输入流已经读取完毕时，该方法会返回-1.
 * 输入输出流使用完毕后要关闭（一般在finally中进行该操作）
 *
 * 注意：输入输出流中数据的读取是采取的类似剪切的操作，而不是复制，这样就可以不用去管上次读取了哪些字节，直接读剩下的字节即可
 *
 */

public class DownLoadAsyncTask extends AsyncTask<String, Integer, Integer> {

    private static final String TAG = "ybz";

    private DownloadListener listener;
    
    private int lastProgress;
    
    //定义几个状态参数
    private static final int TYPE_SUCCESS = 0;
    private static final int TYPE_FAILED = 1;
    private static final int TYPE_PAUSED = 2;
    private static final int TYPE_CANCELLED = 3;

    //这几个参数用来控制下载的暂停与取消
    private static boolean isPaused = false;
    private static boolean isCancelled = false;

    public DownLoadAsyncTask(DownloadListener listener){
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream inputStream;
        RandomAccessFile savedFile = null;
        String downloadUrl = params[0];
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        String fileName = "dowmloadTestFile";
        long downloadedLength = 0;
        File file = new File(directory+fileName);
        if (file.exists()){
            downloadedLength = file.length();
        }
        long contentLength = getContentLength(downloadUrl);
        if (contentLength == 0){
            Log.d(TAG, "从网络获取文件失败");
            return TYPE_FAILED;
        } else if (contentLength ==downloadedLength){
            return TYPE_SUCCESS;
        }
        //如果以上if语句判断均为false，说明需要下载，下面自动根据downloadedLength是否为0来判断从哪开始下载
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .addHeader("RANGE","bytes="+downloadedLength+"-").build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            Log.d(TAG, "doInBackground: get response IOException caught");
        }

        //对下载结果进行处理
        if (response !=null){
            inputStream = response.body().byteStream();//将返回的结果转换成输入流
            try {
                savedFile = new RandomAccessFile(file,"rw");
                savedFile.seek(downloadedLength);
                byte[] b = new byte[1024];//每次读取1024个字节
                int total = 0;//已经从网络中获取到的字节流长度
                int len;
                while ((len = inputStream.read(b)) != -1){
                    Log.d(TAG, "check pause and cancel");
                    if (isPaused){
                        return TYPE_PAUSED;
                    } else if (isCancelled){
                        return TYPE_CANCELLED;
                    } else {
                        total = total + len;
                        savedFile.write(b,0,len);
                        int progress = (int) ((total+downloadedLength)*100/contentLength);
                        publishProgress(progress);//通过该方法将进度发送到onProgressUpdate()方法中
                    }
                }
                //region while语句的另一种写法
//                while (true){
//                    len = inputStream.read(b);
//                    if (len == -1){
//                        break;
//                    }
//
//                    if (isPaused){
//                        return TYPE_PAUSED;
//                    } else if (isCancelled){
//                        return TYPE_CANCELLED;
//                    } else {
//                        total = total + len;
//                        savedFile.write(b,0,len);
//                        int progress = (int) ((total+downloadedLength)*100/contentLength);
//                        publishProgress(progress);//通过该方法将进度发送到onProgressUpdate()方法中
//                    }
//
//                }
                //endregion
                response.body().close();
                
                return TYPE_SUCCESS;
            } catch (FileNotFoundException e) {
                Log.d(TAG, "RandomAccessFile read exception");
            } catch (IOException e){
                Log.d(TAG, "断点续传时找不到文件的节点");
            } finally {
                try{
                    if (inputStream != null){
                        inputStream.close();
                    }
                    if (savedFile !=null){
                        savedFile.close();
                    }
                    if (isCancelled && file !=null){
                        file.delete();
                    }
                } catch (Exception e){
                    Log.d(TAG, "释放资源时出错");
                }
            }
        }

        return TYPE_FAILED;
    }

    @Override
    protected void onPostExecute(Integer status) {
        switch (status){
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELLED:
                listener.onCanceled();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress >lastProgress){
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }


    //获得待下载文件的长度
    private long getContentLength(String downloadUrl){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            Log.d(TAG, "getContentLength: IOException caught");
            return 0;
        }
        if (response !=null &&response.isSuccessful()){
            long contentLength = response.body().contentLength();
            response.body().close(); //close?
            return contentLength;
        }
        return 0;
    }

    public void pauseDownload(){
        isPaused = true;
    }

    public void cancelDownload(){
        isCancelled = true;
    }
}
