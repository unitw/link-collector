package ru.dolganov.tool.knowledge.collector.dao;

import java.util.List;
import java.util.Map;

import model.knowledge.NodeMeta;
import model.knowledge.Root;
import model.knowledge.role.Parent;

public interface DAO {
	
	Root getRoot();

	void flushMeta();

	List<NodeMeta> getChildren(Parent parent);
	
	boolean addChild(Parent parent, NodeMeta child);
	
	boolean addChild(Parent parent, NodeMeta child,Map<String, String> params);
	
	NodeMetaObjectsCache getCache();
	
	void addListener(DAOEventListener listener);

	void delete(NodeMeta node);

	void update(NodeMeta node, Map<String, String> params);

	Map<String,Object> getExternalData(NodeMeta ob);

	void persist(Object object, Map<String, Object> params);
	
	void merge(Object object, Map<String, Object> params);

}
