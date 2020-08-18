package com.froggengo.zookeeper.election;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.*;

/**
 * https://www.allprogrammingtutorials.com/tutorials/leader-election-using-apache-zookeeper.php
 * https://github.com/SainTechnologySolutions/allprogrammingtutorials/blob/master/apache-zookeeper/
 */
public class LeaderElectionLauncher {

    private static final Logger LOG = Logger.getLogger(LeaderElectionLauncher.class);

    public static void main(String[] args) throws IOException, InterruptedException {

/*        if(args.length < 2) {
            System.err.println("Usage: java -jar <jar_file_name> <process id integer> <zkhost:port pairs>");
            System.exit(2);
        }*/

/*        final int id = Integer.valueOf(args[0]);
        final String zkURL = args[1];*/
        final int id = new Random(100).nextInt();
        final String zkURL = "127.0.0.1:2181";
        final ExecutorService service = Executors.newSingleThreadExecutor();

        final Future<?> status = service.submit(new ProcessNode(id, zkURL));

/*        TimeUnit.SECONDS.sleep(60);
        service.shutdownNow();*/
        System.out.println(service.isShutdown() +"="+service.isTerminated());
        try {
            status.get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.fatal(e.getMessage(), e);
            service.shutdown();
        }
    }
}
