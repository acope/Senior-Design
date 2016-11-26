/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.io.IOException;
import java.util.logging.*;

/**
 *
 * @author Austin Copeman
 */
public class LogToFile {
    private Logger logger;
    private final String logFile;
    private final String loggerName;

    public LogToFile(String loggerName, String logFile) {
        logger = Logger.getLogger(loggerName); //Find or create a logger for a named subsystem.
        this.logFile = logFile + ".log"; //File name to save log
        this.loggerName = loggerName;
    }
    
    /**
     * log Method 
     * enable to log all exceptions to a file and display user message on demand
     * @param ex
     * @param level "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST"
     * @param msg 
     */
    public void log(Exception ex, String level, String msg){
        LogManager lm = LogManager.getLogManager();
        logger = Logger.getLogger(loggerName); //Find or create a logger for a named subsystem.
        Handler fh = null;
        try {
            lm.addLogger(logger);
            fh = new FileHandler(logFile,true);
            logger.addHandler(fh);
            fh.setLevel(Level.ALL);

            switch (level) {
                case "severe":
                    logger.log(Level.SEVERE, msg, ex);
                    break;
                case "warning":
                    logger.log(Level.WARNING, msg, ex);
                    break;
                case "info":
                    logger.log(Level.INFO, msg, ex);
                    break;
                case "config":
                    logger.log(Level.CONFIG, msg, ex);
                    break;
                case "fine":
                    logger.log(Level.FINE, msg, ex);
                    break;
                case "finer":
                    logger.log(Level.FINER, msg, ex);
                    break;
                case "finest":
                    logger.log(Level.FINEST, msg, ex);
                    break;
                default:
                    logger.log(Level.CONFIG, msg, ex);
                    break;
            }
        } catch (IOException | SecurityException ex1) {
            logger.log(Level.SEVERE, null, ex1);
        } finally{
            if(fh!=null)fh.close();
        }
    }
    
    
}
