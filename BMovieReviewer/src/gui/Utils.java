package gui;

import java.awt.Component;
import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Utils {
	
	public static final char FILE_EXTENSION_SEPARATOR = '.';
	public static File getFile(Component parent, String path,
			FileNameExtensionFilter filter) {
		return getFile(parent, path, filter, JFileChooser.OPEN_DIALOG);
	}

	public static File getFile(Component parent, String path,
			FileNameExtensionFilter filter, int kind) {
		Vector<FileNameExtensionFilter> filterList = new Vector<FileNameExtensionFilter>();
		filterList.add(filter);
		return getFile(parent, path, filterList, kind);
	}

	public static File getFile(Component parent, String path,
			Vector<FileNameExtensionFilter> filters, int kind) {
		if (path == null || parent == null) {
			throw new IllegalArgumentException();
		}
		JFileChooser chooser = new JFileChooser(path);
		chooser.resetChoosableFileFilters();
		if (filters != null) {
			for (FileNameExtensionFilter filter : filters) {
				chooser.addChoosableFileFilter(filter);
				chooser.setFileFilter(filter);
			}
		}
		int returnVal;

		if (kind == JFileChooser.OPEN_DIALOG)
			returnVal = chooser.showOpenDialog(parent);
		else
			returnVal = chooser.showSaveDialog(parent);

		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		
		return chooser.getSelectedFile();
	}
	
	public static File ensureFileExtension(File file, FileNameExtensionFilter filter) {
		if(filter.accept(file)) {
			return file;
		}
		String fileName = file.getName();
		String extension = getExtensionString(filter);
		if(extension == null) return file;
		fileName = getWithoutFileNameExtension(fileName);
		fileName = getWithFileNameExtension(fileName, extension);
		String path = file.getParent();
		if(path != null) {
			fileName = path + File.separator + fileName;
		}
		file = new File(fileName);	
		return file;
	}
	
	public static String getWithoutFileNameExtension(String fileName) {
		int indexOfExtensionSeparator = fileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
		if(indexOfExtensionSeparator > -1) {
			fileName = fileName.substring(0, indexOfExtensionSeparator);
		}
		return fileName;
	}
	
	public static String getWithFileNameExtension(String fileName, String extension) {
		return fileName + FILE_EXTENSION_SEPARATOR + extension;
	}
	
	protected static String getExtensionString(FileNameExtensionFilter filter) {
		String extensions [] = filter.getExtensions();
		if(extensions.length < 1) {
			return null;
		}
		return extensions[0];
	}
	
	

}
