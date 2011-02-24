package ru.kc.module.properties;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Arrays;
import java.util.List;

import ru.kc.common.controller.Controller;
import ru.kc.common.tree.event.NodeSelected;
import ru.kc.model.Dir;
import ru.kc.model.FileLink;
import ru.kc.model.Link;
import ru.kc.model.Node;
import ru.kc.model.Text;
import ru.kc.module.properties.link.LinkPropsModule;
import ru.kc.module.properties.node.NodePropsModule;
import ru.kc.module.properties.text.TextPropsModule;
import ru.kc.module.properties.ui.PropsPanel;
import ru.kc.platform.annotations.Mapping;
import ru.kc.platform.event.annotation.EventListener;

@Mapping(PropsPanel.class)
public class PropsController extends Controller<PropsPanel> {

	NodePropsModule nodePropsModule;
	LinkPropsModule linkPropsModule;
	TextPropsModule textPropsModule;
	Node currentNode;
	
	@Override
	protected void init() {
		ui.setLayout(new BorderLayout());
		
		nodePropsModule = new NodePropsModule();
		nodePropsModule.setAppContext(appContext);
		
		linkPropsModule = new LinkPropsModule();
		linkPropsModule.setAppContext(appContext);
		
		textPropsModule = new TextPropsModule();
		textPropsModule.setAppContext(appContext);
	}
	
	
	@EventListener(NodeSelected.class)
	public void onNodeSelected(NodeSelected event){
		currentNode = event.node;
		showProps();
	}

	private void showProps() {
		if(currentNode == null){
			showEmpty();
		} else if(currentNode instanceof Dir){
			showProps((Dir)currentNode);
		} else if(currentNode instanceof Link){
			showProps((Link)currentNode);
		} else if(currentNode instanceof FileLink){
			showProps((FileLink)currentNode);
		} else if(currentNode instanceof Text){
			showProps((Text)currentNode);
		} else {
			showPropsForUnknowType(currentNode);
		}
	}


	private void showEmpty() {
		clearAll();
	}


	private void showProps(Dir node) {
		showPropsForAbstractNode(node);
	}
	
	private void showProps(Link node) {
		linkPropsModule.setNode(node);
		replace(linkPropsModule);
	}
	
	private void showProps(Text node) {
		textPropsModule.setNode(node);
		replace(textPropsModule);
	}
	
	private void showProps(FileLink node) {
		showPropsForAbstractNode(node);
	}
	

	
	private void showPropsForUnknowType(Node node) {
		showPropsForAbstractNode(node);
	}
	
	private void showPropsForAbstractNode(Node node){
		nodePropsModule.setNode(node);
		replace(nodePropsModule);
	}
	
	
	@Override
	protected void onNodeUpdated(Node old, Node updatedNode) {
		if(old.equals(currentNode)){
			onNodeSelected(new NodeSelected(updatedNode));
		}
	}
	
	@Override
	protected void onChildDeletedRecursive(Node parent, Node deletedChild) {
		currentNode = null;
		clearAll();
	}
	

	private void clearAll(){
		replace(null);
	}
	
	private void replace(Component newComponent){
		List<Component> components = Arrays.asList(ui.getComponents());
		boolean exist = false;
		for(Component c : components){
			if(c == newComponent){
				exist = true;
				enableUpdateMode(c);
			} else {
				ui.remove(c);
				disableUpdateMode(c);
			}
		}
		if(!exist && newComponent != null){
			enableUpdateMode(newComponent);
			ui.add(newComponent);
		}
			
		
		ui.revalidate();
		ui.repaint();

	}


	private void disableUpdateMode(Component c) {
		if(c instanceof PropsUpdater){
			((PropsUpdater) c).disableUpdateMode();
		}
	}


	private void enableUpdateMode(Component c) {
		if(c instanceof PropsUpdater){
			((PropsUpdater) c).enableUpdateMode();
		}
	}




}
