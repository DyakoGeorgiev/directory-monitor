package com.folder.monitor.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class FileLogger  implements Logger{

  private File file;
  private StringBuilder builder;

  public FileLogger(File file, StringBuilder builder) {
    this.file = file;
    this.builder = builder;
  }

  @Override
  public synchronized void log(String logMessage) {
    this.builder.append(logMessage);
  }

  @Override
  public synchronized boolean flush() {
    try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file,true))))) {
      writer.println(this.builder.toString());
      this.builder.setLength(0);
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
