package dk.nesos.util;

import java.io.*;
import java.util.*;

public class Debug {
    
    public static PrintWriter logStream;
    public static final boolean logToSysErr = false;
    private static long loggingStartMs;
    
    public static void mf(String debugString){
        if (Configuration.hasDebugMf()) {
            System.err.println(debugString);
        }
    }
    
    public static void ndhb(String debugString){
        if (Configuration.hasDebugNdhb()) {
            System.err.println(debugString);
        }
    }
    
    public static void mf(Object o){
        mf (o.toString());
    }
    
    public static void ndhb(Object o){
        ndhb (o.toString());
    }
    
    public static void println(String debugString){
        if (Configuration.hasDebug()) {
            System.err.println(debugString);
        }
    }
    
    public static void println(Object o){
        println(o.toString());
    }
    
    public static void print(String debugString){
        if (Configuration.hasDebug()) {
            System.err.print(debugString);
        }
    }
    
    public static void print(Object o){
        print(o.toString());
    }

    /**
     * Inits the log stream
     */
    private static void initLogStream(){
        String logFileName = Configuration.getLogFilename();
        if (logToSysErr) {
            logStream = new PrintWriter (System.err);
        } else {
            try {
                FileOutputStream fos = new FileOutputStream(new File(logFileName), true);
                logStream = new PrintWriter(fos);
            }
            catch (IOException e){
                System.err.println("Could not create log file '" + logFileName + "'");
                System.err.println(e.getStackTrace());
                System.exit(1);
            }
        }
        
        //Create a thread for closing the outputstream
        Thread t = new Thread() {
            public void run() {
                Debug.mf("Closing logstream");
                if (logStream != null) {
                    logStream.println("LOG END\n");
                    logStream.close();
                }
            }
        };
        
        //Add thread to runtime to close file on exit
        Runtime.getRuntime().addShutdownHook(t);
        
        //Write a log start message to the file.
        loggingStartMs = System.currentTimeMillis();
        Date logStartDate = new Date(loggingStartMs); 
        logToFile("LOG START AT: " + logStartDate.toString());
    }

    /**
     * Logs to file
     * @param str
     */
    public static void logToFile(String str){
        if (logStream == null) {
            initLogStream();
        }
        logStream.print(System.currentTimeMillis() - loggingStartMs);
        logStream.print(":\t");
        logStream.println(str);
        logStream.flush();
    }
    

    public static void logToFile(Object o) {
        logToFile(o.toString());
    }
    
    /**
     * Logs to file
     * @param o
     */
    public static void logToFileNdhb(Object o){
        if (Configuration.hasDebugNdhb()) {
            logToFile(o.toString());
        }
    }
    
    /**
     * Logs to file
     * @param s
     */
    public static void logToFileNdhb(String s){
        if (Configuration.hasDebugNdhb()) {
            logToFile(s);
        }
    }
    
    /**
     * Logs to file
     * @param o
     */
    public static void logToFileMf(Object o){
        if (Configuration.hasDebugMf()) {
            logToFile(o.toString());
        }
    }
    
    /**
     * Logs to file
     * @param s
     */
    public static void logToFileMf(String s){
        if (Configuration.hasDebugMf()) {
            logToFile(s);
        }
    }

}
