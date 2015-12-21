package org.shirdrn.dw.es.utils;

import java.io.Closeable;

public class IOUtils {
	
	public static void closeQuietly(Closeable... closeables) {
		for(Closeable closeable : closeables) {
			try {
				closeable.close();
			} catch (Exception e) { }
		}
	}
}
