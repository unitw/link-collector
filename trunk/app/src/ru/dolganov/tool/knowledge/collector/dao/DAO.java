package ru.dolganov.tool.knowledge.collector.dao;

import java.util.List;
import java.util.Map;

import model.knowledge.NodeMeta;
import model.knowledge.Root;
import model.knowledge.role.Parent;
import model.tree.TreeSnapshot;
import model.tree.TreeSnapshotDir;

public interface DAO {
	
	Root getRoot();

	//NodeMeta
	List<NodeMeta> getChildren(Parent parent);
	
	boolean addChild(Parent parent, NodeMeta child);
	
	boolean addChild(Parent parent, NodeMeta child,Map<String, String> params);
	
	NodeMetaObjectsCache getCache();
	
	void addListener(DAOEventListener listener);

	void delete(NodeMeta node);

	void update(NodeMeta node, Map<String, String> params);

	Map<String,Object> getExternalData(NodeMeta ob);

	void merge(Root object);
	
	
	
	//Snaps
	void add(TreeSnapshot object, Map<String, Object> params);
	
	void add(TreeSnapshotDir object);

	void delete(TreeSnapshotDir parent, TreeSnapshot node);

	void delete(TreeSnapshotDir ob);

	void update(TreeSnapshot snap);

}
