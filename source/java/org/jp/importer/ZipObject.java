package org.jp.importer;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.*;
import org.apache.log4j.Logger;
import org.rsna.util.FileUtil;

/**
 * A generic Zip File class.
 */
public class ZipObject {

	static Charset latin1 = Charset.forName("iso-8859-1");
	static Charset utf8 = Charset.forName("utf-8");

	static final Logger logger = Logger.getLogger(ZipObject.class);
	
	File file;

	/**
	 * Class constructor; verify that the zip file parses.
	 * @param file the zip file.
	 * @throws Exception if the zip file does not parse.
	 */
	public ZipObject(File file) throws Exception {
		this.file = file;
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file);
			zipFile.close();
		}
		catch (Exception ex) {
			if (zipFile != null) zipFile.close();
			throw ex;
		}
	}

	/**
	 * Get an array of ZipEntry objects corresponding to the files
	 * in the ZipObject. This method does not return ZipEntry objects
	 * which correspond to directories.
	 * @return the array of ZipEntry objects corresponding to the files
	 * in the ZipObject, or null if the ZipObject cannot be read.
	 */
	public ZipEntry[] getEntries() {
		try {
			ZipEntry[] entries = null;
			ZipFile zipFile = new ZipFile(file);
			ZipEntry ze;
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			//Get the ZipEntries corresponding to files (not directories).
			ArrayList<ZipEntry> list = new ArrayList<ZipEntry>();
			while ( e.hasMoreElements() ) {
				ze = e.nextElement();
				if (!ze.isDirectory()) list.add(ze);
			}
			entries = new ZipEntry[list.size()];
			entries = list.toArray(entries);
			return entries;
		}
		catch (Exception ex) { return null; }
	}

	/**
	 * Unpack the ZipObject using the directory structure of the zip file.
	 * @param dir the directory into which to unpack the zip file. This becomes
	 * root directory of the file tree of the object. Files with no path information
	 * are placed in this directory; files with path information are placed in child
	 * directories with this directory as the root. Directories are created when necessary.
	 * @return true if the operation succeeded; false otherwise.
	 */
	public boolean extractAll(File dir) {
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		ZipFile zipFile = null;
		if (!file.exists()) return false;
		if (!dir.isDirectory()) return false;
		String path = dir.getAbsolutePath();
		if (!path.endsWith(File.separator)) path += File.separator;
		try {
			zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
			while (zipEntries.hasMoreElements()) {
				ZipEntry entry = zipEntries.nextElement();
				String name = entry.getName().replace('/',File.separatorChar);
				File outFile = new File(path + name);
				if (!entry.isDirectory()) {
					outFile.getParentFile().mkdirs();
					out = new BufferedOutputStream(new FileOutputStream(outFile));
					in = new BufferedInputStream(zipFile.getInputStream(entry));
					FileUtil.copy(in, out, -1);
				}
				else outFile.mkdirs();
			}
			zipFile.close();
			return true;
		}
		catch (Exception e) {
			try {
				if (zipFile != null) zipFile.close();
				if (in != null) in.close();
				if (out != null) out.close();
			}
			catch (Exception ignore) { logger.warn("Unable to close zipFile."); }
			return false;
		}
	}

	/**
	 * Unpack the file corresponding to a ZipEntry and place it in a specified directory.
	 * If the directory does not exist, it is created.
	 * @param entry the entry pointing to the desired file in the zip file. This can be
	 * one of the entries in the array returned by getEntries().
	 * @param dir the directory into which to unpack the zip file. This becomes
	 * root directory of the file tree of the object. Files with no path information
	 * are placed in this directory; files with path information are placed in child
	 * directories with this directory as the root. Directories are created when necessary.
	 * @return true if the operation succeeded; false otherwise.
	 */
	public boolean extractFile(ZipEntry entry, File dir) {
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		ZipFile zipFile = null;
		if (!file.exists()) return false;
		if (!dir.exists()) dir.mkdirs();
		try {
			zipFile = new ZipFile(file);
			File outFile = new File(entry.getName().replace('/',File.separatorChar));
			outFile = new File(dir,outFile.getName());
			if (!entry.isDirectory()) {
				outFile.getParentFile().mkdirs();
				out = new BufferedOutputStream(new FileOutputStream(outFile));
				in = new BufferedInputStream(zipFile.getInputStream(entry));
				FileUtil.copy(in, out, -1);
				zipFile.close();
				return true;
			}
		}
		catch (Exception e) { }
		FileUtil.close(in);
		FileUtil.close(out);
		FileUtil.close(zipFile);
		return false;
	}

	/**
	 * Unpack the file corresponding to a ZipEntry and place it in a specified directory
	 * with a specified file name. No path information from the Zip Entry is used.
	 * If the directory does not exist, it is created. If the ZipEntry is a directory,
	 * nothing is done and false is returned.
	 * @param entry the entry pointing to the desired file in the zip file. This can be
	 * one of the entries in the array returned by getEntries().
	 * @param dir the directory into which to unpack the zip file.
	 * @param name the file name for the unpacked file.
	 * @return true if the operation succeeded; false otherwise.
	 */
	public boolean extractFile(ZipEntry entry, File dir, String name) {
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		ZipFile zipFile = null;
		if (!file.exists()) return false;
		if (!dir.exists()) dir.mkdirs();
		try {
			zipFile = new ZipFile(file);
			File outFile = new File(dir, name);
			if (!entry.isDirectory()) {
				outFile.getParentFile().mkdirs();
				out = new BufferedOutputStream(new FileOutputStream(outFile));
				in = new BufferedInputStream(zipFile.getInputStream(entry));
				FileUtil.copy(in, out, -1);
				zipFile.close();
				return true;
			}
		}
		catch (Exception e) { }
			FileUtil.close(in);
			FileUtil.close(out);
			FileUtil.close(zipFile);
		return false;
	}

	/**
	 * Unpack the file corresponding to a ZipEntry and return its contents as a String,
	 * using the iso-8859-1 character set (Latin-1).
	 * Care should be taken to use this method only on known text files.
	 * @param entry the entry pointing to the desired file in the zip file. This can be
	 * one of the entries in the array returned by getEntries().
	 * @return the text of the file if the operation succeeded, or the empty string if it failed.
	 */
	public String extractFileText(ZipEntry entry) {
		return extractFileText(entry, latin1);
	}

	/**
	 * Unpack the file corresponding to a ZipEntry and return its contents as a String,
	 * using the specified character set.
	 * Care should be taken to use this method only on known text files.
	 * @param entry the entry pointing to the desired file in the zip file. This can be
	 * one of the entries in the array returned by getEntries().
	 * @param charset the character encoding to be used for the bytes in the file.
	 * @return the text of the file if the operation succeeded, or the empty string if it failed.
	 */
	public String extractFileText(ZipEntry entry, Charset charset) {
		StringWriter sw = new StringWriter();
		BufferedReader in = null;
		ZipFile zipFile = null;
		if (!file.exists()) return "";
		try {
			zipFile = new ZipFile(file);
			if (!entry.isDirectory()) {
				in = new BufferedReader(
							new InputStreamReader(zipFile.getInputStream(entry),charset));
				int size = 1024;
				int n = 0;
				char[] c = new char[size];
				while ((n = in.read(c,0,size)) != -1) sw.write(c,0,n);
				in.close();
			}
			zipFile.close();
			return sw.toString();
		}
		catch (Exception e) {
			try {
				if (zipFile != null) zipFile.close();
				if (in != null) in.close();
			}
			catch (Exception ignore) { logger.warn("Unable to close zipFile."); }
			return "";
		}
	}
}
