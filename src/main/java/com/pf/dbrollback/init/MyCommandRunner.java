package com.pf.dbrollback.init;
import com.pf.dbrollback.recovery.RecoverStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Author:王春伟
 * Date:2019/11/2
 * 版权归王春伟本人所有，如有盗版，必按刑法处置
 **/
@Component
public class MyCommandRunner implements CommandLineRunner{

    private static final Logger log = LoggerFactory.getLogger(MyCommandRunner.class);

    @Override
    public void run(String... args) {
        log.info("...............启动 recovery 数据库程序............");
        try {
            RecoverStarter.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

