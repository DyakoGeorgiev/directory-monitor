package com.folder.monitor.logger;

public interface Logger {

  /**
   * Logs message on a new line.
   *
   * @param logMessage - user-message
   */
  void log (String logMessage);

  boolean flush ();

}
