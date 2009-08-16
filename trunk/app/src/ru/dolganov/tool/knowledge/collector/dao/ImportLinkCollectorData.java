package ru.dolganov.tool.knowledge.collector.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import ru.chapaj.tool.link.collector.model.DataContainer;
import ru.chapaj.tool.link.collector.model.Link;
import ru.chapaj.tool.link.collector.store.file.DataContainerStore;
import model.knowledge.Dir;
import model.knowledge.LocalLink;
import model.knowledge.NetworkLink;
import model.knowledge.NodeMeta;
import model.knowledge.Note;
import model.knowledge.Root;

public class ImportLinkCollectorData {
	
	static class QS {
		Dir dir;
		ru.chapaj.tool.link.collector.model.Dir lcDir;
		public QS(Dir dir, ru.chapaj.tool.link.collector.model.Dir lcDir) {
			super();
			this.dir = dir;
			this.lcDir = lcDir;
		}
	}
	
	public static void fill(String lcFilePath, Root root){
		try {
			DataContainer dc = new DataContainerStore().loadFile(new File(lcFilePath));
			
			//tree
			LinkedList<QS> q = new LinkedList<QS>();
			q.add(new QS(root.getRoot(),dc.getRoot()));
			while(!q.isEmpty()){
				QS qs = q.removeFirst();
				ArrayList<ru.chapaj.tool.link.collector.model.Dir> dirs = qs.lcDir.getSubDir();
				if(dirs != null){
					for(ru.chapaj.tool.link.collector.model.Dir lcDir : dirs){
						Dir dir = convertDir(lcDir);
						qs.dir.getNodes().add(dir);
						q.addLast(new QS(dir,lcDir));
						
					}
				}
				
				ArrayList<Link> links = qs.lcDir.getLinks();
				if(links != null){
					for(Link link : links){
						qs.dir.getNodes().add(convertLink(link));
					}
				}
			}
			
			
			//snapshots
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static NodeMeta convertLink(Link ob) {
		NodeMeta meta = null;
		String url = ob.getUrl();
		if(url.startsWith("http")||url.startsWith("www")){
			meta = new NetworkLink();
			((model.knowledge.Link)meta).setUrl(ob.getUrl());
		}
		else if(url.indexOf(':')==1){
			meta = new LocalLink();
			((model.knowledge.Link)meta).setUrl(ob.getUrl());
		}
		else meta = new Note();
		
		meta.setCreateDate(System.currentTimeMillis());
		meta.setDescription(ob.getDescription());
		meta.setName(ob.getName());
		meta.setUuid(ob.getUuid());
		
		return meta;
	}

	private static Dir convertDir(ru.chapaj.tool.link.collector.model.Dir ob) {
		Dir dir = new Dir();
		dir.setCreateDate(System.currentTimeMillis());
		dir.setUuid(ob.getUuid());
		dir.setDescription(ob.getMeta().getDescription());
		dir.setName(ob.getMeta().getName());
//		dir.setTags(ob.getMeta())
		return dir;
	}

}
