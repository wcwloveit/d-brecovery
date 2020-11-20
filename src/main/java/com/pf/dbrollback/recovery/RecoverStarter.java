package com.pf.dbrollback.recovery;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
/**
 * Author:王春伟
 * Date:2020/11/18
 * 佛系码农,热爱生活！
 **/
public class RecoverStarter {

    private static final Logger log = LoggerFactory.getLogger(RecoverStarter.class);

    private static final String BACKUP_NAME = "sql.txt";

    private static final String CHECKPOINT_NAME = "checkpoint.txt";

    private static final String ERROR_NAME = "error.txt";

    private static RandomAccessFile randomAccessFile;

    private static RandomAccessFile pointAccessFile;

    private static RandomAccessFile errorAccessFile;

    private static final String DIR_PATH = "D:\\dbtest\\";

    //偏移量
    private static long offset;

    static {
        try {
            pointAccessFile = new RandomAccessFile(DIR_PATH + CHECKPOINT_NAME,"rw");
            errorAccessFile = new RandomAccessFile(DIR_PATH + ERROR_NAME,"rw");
            final String position = pointAccessFile.readLine();
            offset = (position == null || position.equals("")) ? 0L : Long.parseLong(position);
            randomAccessFile = new RandomAccessFile(DIR_PATH + BACKUP_NAME,"rw");
            randomAccessFile.seek(offset);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void run() throws Exception{

        String line ;
        while (true){
            line = randomAccessFile.readLine();
            //获取文件已经被读的偏移量
            offset = randomAccessFile.getFilePointer();
            // 重置偏移量
            pointAccessFile.seek(0);
            // 将偏移量写入 point.txt 文件
            pointAccessFile.write(String.valueOf(offset).getBytes());
            if (line != null) {
                line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                final JSONObject jsonObject = JSONObject.parseObject(line);
                // 执行恢复数据库数据
                executeRecover(jsonObject);
            }else {
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }

    private static void executeRecover(JSONObject jsonObject) {
        final String sql = jsonObject.getString("sql");
        System.out.println(sql);
        PreparedStatement ps = null;
        try {
            ps = RecoverDatabase.connection.prepareStatement(sql);
            ps.execute();
        } catch (SQLException e) {
            //记录执行出错的sql
            try {
                errorAccessFile.write(jsonObject.toJSONString().getBytes());
            } catch (IOException ex) {
                log.info("记录错误的sql失败: " + ex.getMessage());
            }
            log.error("恢复数据库发生失败,失败的sql为: " + sql);
        }
    }

}
