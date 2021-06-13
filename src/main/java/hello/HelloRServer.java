package hello;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;

import java.util.Date;

public class HelloRServer {
    public static void main(String[] args) {

        System.out.println("The current local time is: " + new Date());

        //Using port 6312, which is the one mapped to the Pod's port of 6311
        //kubectl port-forward -n dev-dqf deployment/r-server 6312:6311
        RConnection c = connectAndTest(null, 6312);
        if (c == null) {
            System.err.println("Exiting");
            return;
        }
        try {
            REXP resp = null;
            c.assign("executionId", "execid-0001");

            //resp = c.parseAndEval("System$getHostname()");
            //System.out.println(resp.asString());


            String filenameToWrite = "/sample/testing-file-creation.txt";
            System.out.println("Attempting to write a Test File: " + filenameToWrite);
            c.createFile(filenameToWrite);

            //c.assign("serverPath", "/repo/DQF-R/return0.R");
            c.assign("serverPath", "/sample/return0.R");
            resp = c.eval("source(serverPath)");
//            resp = c.eval("execute(executionId)");


            String R_COMMAND_OR_SOURCE_FILE_PATH = "source(serverPath)";
            R_COMMAND_OR_SOURCE_FILE_PATH = "System.getHostname()";
            resp = c.parseAndEval(
                    "try(eval(" + R_COMMAND_OR_SOURCE_FILE_PATH + "),silent=TRUE)");
            if (resp.inherits("try-error")) {
                System.err.println("R Serve Eval Exception : " + resp.asString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static RConnection connectAndTest(String host, int port) {

        if (host == null || host.isEmpty()) {
            host = "127.0.0.1";
        }
        if (port == 0) {
            port = 6312;
        }

        System.out.println("Attempting to create RConnection, with host="+host+", port=" + port);
        //kubectl port-forward -n dev-dqf deployment/r-server 6311:6311
        RConnection c = null;
        try {
            c = new RConnection(host, port);
            REXP resp = c.eval("R.version.string");
            System.out.println(resp.asString());
            System.out.println("==============================================================");
            double[] d = c.eval("rnorm(10)").asDoubles();
            System.out.println("Generated array size=" + d.length + ", First entry=" + d[0]);
        } catch (Exception e) {
            System.err.println("Error:" + e.getMessage() + ", Type:" + e.getClass().getName());
        }
        return c;
    }
}
