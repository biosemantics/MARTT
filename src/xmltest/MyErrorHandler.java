package xmltest;

import org.xml.sax.*;
import java.io.*;
/**
 * <p>Title: XMLTest</p>
 * <p>Description: Learn to use SAX and DOM interface to processing XML documents</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public class MyErrorHandler implements ErrorHandler{

          /** Error handler output goes here */
          private PrintStream out;

          public MyErrorHandler(PrintStream out) {
              this.out = out;
          }

          /**
           * Returns a string describing parse exception details
           */
          private String getParseExceptionInfo(SAXParseException spe) {
              String systemId = spe.getSystemId();
              if (systemId == null) {
                  systemId = "null";
              }
              String info = "URI=" + systemId +
                  " Line=" + spe.getLineNumber() +
                  ": " + spe.getMessage();
              return info;
          }

          // The following methods are standard SAX ErrorHandler methods.
          // See SAX documentation for more info.

          public void warning(SAXParseException spe) throws SAXException {
              out.println("Warning: " + getParseExceptionInfo(spe));
          }

          public void error(SAXParseException spe) throws SAXException {
              String message = "Error: " + getParseExceptionInfo(spe);
              throw new SAXException(message);
          }

          public void fatalError(SAXParseException spe) throws SAXException {
              String message = "Fatal Error: " + getParseExceptionInfo(spe);
              throw new SAXException(message);
          }
      }

