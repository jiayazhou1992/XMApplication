package com.xiaomawang.commonlib.utils.dev.common.assist.search;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * detail: 文件广度优先搜索算法 (多线程 + 队列搜索)
 * Created by Ttt
 */
public class FileBreadthFirstSearchUtils {

    // = 构造函数 =

    public FileBreadthFirstSearchUtils(){
    }

    public FileBreadthFirstSearchUtils(ISearchHandle iSearchHandle) {
        this.iSearchHandle = iSearchHandle;
    }

    /**
     * 文件信息 Item
     */
    public class FileItem {

        public FileItem(File file) {
            this.file = file;
        }

        // 文件
        public File file = null;

        // HashMap 保存目录信息
        public HashMap<String, FileItem> mapChilds = null;

        /**
         * 保存子文件信息
         * @param file
         */
        private synchronized FileItem put(File file){
            if (mapChilds == null){
                mapChilds = new HashMap<>();
            }
            if (file != null){
                FileItem fileItem = new FileItem(file);
                mapChilds.put(file.getAbsolutePath(), fileItem);
                return fileItem;
            }
            return null;
        }
    }

    /**
     * 文件队列
     */
    private class FileQueue {

        public FileQueue(File file, FileItem fileItem) {
            this.file = file;
            this.fileItem = fileItem;
        }

        // 当前准备处理文件夹
        private File file;

        // 上一级目录对象
        private FileItem fileItem;
    }

    /**
     * 搜索处理接口
     */
    public interface ISearchHandle {

        /**
         * 判断是否处理该文件
         * @param file
         * @return
         */
        boolean isHandleFile(File file);

        /**
         * 是否添加到集合
         * @param file
         * @return
         */
        boolean isAddToList(File file);

        /**
         * 搜索结束监听
         * @param rootFileItem
         * @param startTime
         * @param endTime
         */
        void OnEndListener(FileItem rootFileItem, long startTime, long endTime);
    }

    // 搜索处理接口
    private ISearchHandle iSearchHandle;

    // 内部实现接口
    private ISearchHandle inside = new ISearchHandle() {
        @Override
        public boolean isHandleFile(File file) {
            if (iSearchHandle != null){
                return iSearchHandle.isHandleFile(file);
            }
            return true;
        }

        @Override
        public boolean isAddToList(File file) {
            if (iSearchHandle != null){
                return iSearchHandle.isAddToList(file);
            }
            return true;
        }

        @Override
        public void OnEndListener(FileItem rootFileItem, long startTime, long endTime) {
            // 表示非搜索中
            isRuning = false;
            // 触发回调
            if (iSearchHandle != null){
                iSearchHandle.OnEndListener(rootFileItem, startTime, endTime);
            }
        }
    };

    /**
     * 设置搜索处理接口
     * @param iSearchHandle
     * @return
     */
    public FileBreadthFirstSearchUtils setSearchHandle(ISearchHandle iSearchHandle) {
        this.iSearchHandle = iSearchHandle;
        return this;
    }

    /**
     * 获取任务队列同时进行数量
     * @return
     */
    public int getQueueSameTimeNumber() {
        return queueSameTimeNumber;
    }

    /**
     * 任务队列同时进行数量
     * @param queueSameTimeNumber
     * @return
     */
    public synchronized FileBreadthFirstSearchUtils setQueueSameTimeNumber(int queueSameTimeNumber) {
        if (isRuning){
            return this;
        }
        this.queueSameTimeNumber = queueSameTimeNumber;
        return this;
    }

    /**
     * 是否搜索中
     * @return
     */
    public boolean isRuning() {
        return isRuning;
    }

    /**
     * 停止搜索
     */
    public void stop(){
        isStop = true;
    }

    /**
     * 是否停止搜索
     * @return
     */
    public boolean isStop() {
        return isStop;
    }

    /**
     * 获取开始搜索时间
     * @return
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * 获取结束搜索时间
     * @return
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * 获取延迟校验时间
     * @return
     */
    public long getDelayTime() {
        return delayTime;
    }

    /**
     * 设置延迟校验时间
     * @param delayTime
     */
    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    // =

    // 根目录对象
    private FileItem rootFileItem;
    // 判断是否运行中
    private boolean isRuning = false;
    // 是否停止搜索
    private boolean isStop = false;
    // 开始搜索时间
    private long startTime = 0l;
    // 结束搜索时间
    private long endTime = 0l;
    // 延迟时间
    private long delayTime = 50l;
    // 任务队列同时进行数量
    private int queueSameTimeNumber = 5;
    // 线程池
    private ExecutorService executor = Executors.newCachedThreadPool();
    // 任务队列
    private LinkedBlockingQueue<FileQueue> taskQueue = new LinkedBlockingQueue<>();

    /**
     * 查询
     * @param path 根目录地址
     */
    public synchronized void query(String path) {
        if (isRuning){
           return;
        }
        // 表示运行中
        isRuning = true;
        isStop = false;
        // 设置开始搜索时间
        startTime = System.currentTimeMillis();
        try {
            // 获取根目录 File
            File file = new File(path);
            if (file != null) {
                // 初始化根目录
                rootFileItem = new FileItem(file);
                // 判断是否文件
                if (file.isFile()){
                    // 触发结束回调
                    endTime = System.currentTimeMillis();
                    inside.OnEndListener(rootFileItem, startTime, endTime);
                    return;
                }
                // 获取文件夹全部子文件
                String[] fileArys = file.list();
                // 获取文件总数
                if (fileArys != null && fileArys.length != 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 查询文件
                            queryFile(rootFileItem.file, rootFileItem);
                            // 循环队列
                            whileQueue();
                        }
                    }).start();
                } else {
                    // 触发结束回调
                    endTime = System.currentTimeMillis();
                    inside.OnEndListener(rootFileItem, startTime, endTime);
                }
            }
        } catch (Exception e) {
            // 触发结束回调
            endTime = System.currentTimeMillis();
            inside.OnEndListener(rootFileItem, startTime, endTime);
        }
    }

    /**
     * 查询文件
     * @param file
     * @param fileItem 所在文件夹对象(上一级目录)
     */
    private void queryFile(File file, FileItem fileItem) {
        try {
            if (isStop){
                return;
            }
            if (file != null && file.exists()) {
                // 判断是否处理
                if (inside.isHandleFile(file)){
                    // 如果属于文件夹
                    if (file.isDirectory()){
                        // 获取文件夹全部子文件
                        File[] files = file.listFiles();
                        if (files == null){
                            return;
                        }
                        // 循环处理
                        for (File f : files){
                            // 属于文件夹
                            if (f.isDirectory()){
                                if (isStop){
                                    return;
                                }
                                FileItem subFileItem = fileItem.put(f);
                                // 添加任务
                                taskQueue.offer(new FileQueue(f, subFileItem));
                            } else { // 属于文件
                                if (!isStop && inside.isAddToList(f)){
                                    // 属于文件则直接保存
                                    fileItem.put(f);
                                }
                            }
                        }
                    } else { // 属于文件
                        if (!isStop && inside.isAddToList(file)){
                            // 属于文件则直接保存
                            fileItem.put(file);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // == 线程池处理 ==

    /**
     * 循环队列
     */
    private void whileQueue(){
        // 判断是否为null
        boolean isEmpty = taskQueue.isEmpty();
        // 循环则不处理
        while (!isEmpty){
            if (isStop){
                break;
            }
            // 获取线程活动数量
            int threadCount = ((ThreadPoolExecutor) executor).getActiveCount();
            // 判断是否超过
            if (threadCount > queueSameTimeNumber){
                continue;
            }
            // 获取文件对象
            final FileQueue fileQueue = taskQueue.poll();
            // 判断是否为null
            if (fileQueue != null){
                // 后台运行
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        queryFile(fileQueue.file, fileQueue.fileItem);
                    }
                });
            }

            // 判断是否存在队列数据
            isEmpty = (taskQueue.isEmpty() && threadCount == 0);
            if (isEmpty){ // 如果不存在, 防止搜索过快, 延迟再次判断
                if (isStop){
                    break;
                }
                try {
                    Thread.sleep(delayTime);
                } catch (Exception e) {
                }
                isEmpty = (taskQueue.isEmpty() && threadCount == 0);
            }
        }
        // 进行通知
        endTime = System.currentTimeMillis();
        inside.OnEndListener(rootFileItem, startTime, endTime);
    }
}
