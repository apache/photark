package org.apache.photark.jcr.util;

/**
 * Created by IntelliJ IDEA.
 * User: suho
 * Date: Jul 14, 2010
 * Time: 1:12:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class JCREncoder {
     public    static String toJCRFormat(String string) {
        if (string != null) {
            string = string.replaceAll("/", "#1");
            string = string.replaceAll(":", "#2");
        }
        return string;

    }

  public   static String toNormalFormat(String string) {
        if (string != null) {
            string = string.replaceAll("\\#1", "/");
            string = string.replaceAll("\\#2", ":");
        }
        return string;

    }
}
