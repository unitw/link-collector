package ru.kc.tools.filepersist.persist;

import java.io.File;
import java.io.IOException;

import ru.kc.tools.filepersist.impl.InitContextExt;
import ru.kc.tools.filepersist.model.impl.Container;
import ru.kc.tools.filepersist.model.impl.NodeBean;
import ru.kc.util.file.FileUtil;

public class Blobs {
	
	private File blobsDir;
	private File nodesDir;

	public void init(FSContext c) {
		InitContextExt init = c.c.init;
		blobsDir = init.blobsDir;
		nodesDir = init.nodesDir;
		
	}

	public String getText(NodeBean node) throws Exception {
		File path = getTextPath(node);
		if(!path.exists()) return null;
		return getText(path);
	}
	
	public void setText(NodeBean node, String text) throws Exception {
		File path = getTextPath(node);
		setText(path, text);
	}
	
	
	

	public File getTextPath(NodeBean node) {
		File file = getTextFile(node);
		createDirs(file);
		return file;
	}

	private File getTextFile(NodeBean node){
		String id = node.getId();
		String containerSimplePath = getFileSimplePath(node.getContainer());
		String path = blobsDir.getPath()+"/"+containerSimplePath+"/"+id+".txt";
		path = normalizePath(path);
		File file = new File(path);
		return file;
	}
	
	private String getFileSimplePath(Container container) {
		String path = container.getFile().getParentFile().getPath();
		String rootPath = nodesDir.getPath();
		if(rootPath.equals(path)) return "";
		
		String out = path.substring(rootPath.length()+1);
		return out;
	}

	private String normalizePath(String path) {
		path = path.replace("\\", "/");
		path = path.replace("//", "/");
		return path;
	}
	
	private void createDirs(File file) {
		file.getParentFile().mkdirs();
	}
	
	private String getText(File path) throws IOException {
		return FileUtil.readFileUTF8(path);
	}

	private void setText(File path, String text) throws IOException {
		FileUtil.writeFileUTF8(path, text);
	}

	
	

}
