package ru.kc.main;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import ru.kc.common.controller.Controller;
import ru.kc.common.node.NodeContainer;
import ru.kc.common.node.NodeContainerListener;
import ru.kc.common.node.event.OpenNodeRequest;
import ru.kc.main.tab.TabModule;
import ru.kc.model.Node;
import ru.kc.model.Text;
import ru.kc.platform.annotations.Mapping;
import ru.kc.platform.event.annotation.EventListener;
import ru.kc.platform.module.Module;
import ru.kc.platform.ui.tabbedform.MainForm;
import ru.kc.platform.ui.tabbedform.TabbedPanel;
import ru.kc.platform.ui.tabs.TabbedWrapper;
import ru.kc.platform.ui.tabs.TabbedWrapper.TabsListener;
import ru.kc.util.Check;

@Mapping(MainForm.class)
public class TabsController extends Controller<MainForm> {
	
	JTabbedPane tabs;
	TabbedWrapper tabsWrapper;

	@Override
	public void init() {
		TabbedPanel root = ui.root;
		tabs = root.tabs;
		tabs.remove(root.testTab1);
		tabs.remove(root.testTab2);
		
		tabsWrapper = new TabbedWrapper(tabs);
		tabsWrapper.addTab(createTab("dashboard"), "dashboard", false);
		tabsWrapper.addTab(createTab("dashboard"), "test concurrent modification");
		
		tabsWrapper.addListener(new TabsListener() {
			
			@Override
			public void onClosed(Component comp, int index, String text) {
				Module.removeAllListneres(comp);
			}
			
			@Override
			public boolean canClose(Component comp, int index, String text) {
				//boolean confirm = dialogs.confirmByDialog(rootUI, "Закрыть?");
				return true;
			}
		});
		
	}
	
	@EventListener(OpenNodeRequest.class)
	public void onOpenNodeRequest(OpenNodeRequest event){
		Node node = event.node;
		TabModule existTab = findFirstExistNodeTab(node);
		if(existTab != null){
			focusRequest(existTab);
		} else {
			Component createdTab = createTab(node);
			focusRequest(createdTab);
		}
	}

	
	
	@Override
	protected void onNodeUpdated(Node old, Node updatedNode) {
		List<TabModule> list = findExistNodeTabs(old);
		for (TabModule tab : list) {
			Component component = tab.getComponent();
			setNode(component, updatedNode);
		}
	}
	
	@Override
	protected void onChildDeletedRecursive(Node parent, final Node deletedChild, final List<Node> deletedSubChildren) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				closeNodeTab(deletedChild);
				for (Node node : deletedSubChildren) {
					closeNodeTab(node);
				}
			}
		});

	}
	
	private void closeNodeTab(Node node){
		List<TabModule> list = findExistNodeTabs(node);
		for (TabModule tab : list) {
			closeTab(tab);
		}
	}
	
	
	
	private void closeTab(Component tab) {
		tabsWrapper.close(tab);
	}

	private void focusRequest(Component tab) {
		tabs.setSelectedComponent(tab);
	}
	
	
	private TabModule findFirstExistNodeTab(Node node) {
		List<TabModule> list = findExistNodeTabs(node);
		return list.size() > 0 ? list.get(0) : null;
	}
	
	private List<TabModule> findExistNodeTabs(Node node) {
		ArrayList<TabModule> out = new ArrayList<TabModule>();
		for (int i = 0; i < tabs.getComponentCount(); i++) {
			Component tab = tabs.getComponent(i);
			if(tab instanceof TabModule){
				Component component = ((TabModule)tab).getComponent();
				if(component instanceof NodeContainer<?>){
					Node candidat = ((NodeContainer<?>) component).getNode();
					if(candidat.equals(node)){
						out.add((TabModule)tab);
					}
				}
			}
		}
		return out;
	}

	private TabModule createTab(Node node) {
		if(node instanceof Text){
			TabModule tab = createTab("text-editor");
			tabsWrapper.addTab(tab, convertToShort(node.getName()), true);
			Component component = tab.getComponent();
			setNode(component,node);
			addNodeContainerListener(tab, component);
			return tab;
		}
		else throw new IllegalArgumentException("unknow type for open tab: "+node);
	}

	private String convertToShort(String string) {
		if(Check.isEmpty(string)) return string;
		
		if(string.length() > 20){
			string = string.substring(0, 17);
			string = string + "...";
		}
		return string;
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setNode(Component component, Node node) {
		if(component instanceof NodeContainer<?>){
			((NodeContainer) component).setNode(node);
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	private void addNodeContainerListener(final Component tab, Component component) {
		if(component instanceof NodeContainer<?>){
			((NodeContainer) component).addListener(new NodeContainerListener() {
				
				@Override
				public void onModified(Node node) {
					tabsWrapper.setModified(tab, true);
				}
				
				@Override
				public void onReverted(Node node) {
					tabsWrapper.setModified(tab, false);
				}
				

			});
		}
	}

	private TabModule createTab(String mapping){
		Component component = (Component)instanceByMapping(mapping);
		return createTab(component);
	}
	
	private TabModule createTab(Component component){
		TabModule tab = new TabModule();
		tab.setAppContext(appContext);
		tab.setComponent(component);
		return tab;
	}
	
	

	


}
