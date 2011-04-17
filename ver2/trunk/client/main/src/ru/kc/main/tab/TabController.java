package ru.kc.main.tab;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToolBar;

import ru.kc.common.controller.Controller;
import ru.kc.main.tab.event.NextPrevButtonsEnableRequest;
import ru.kc.main.tab.tools.MainMenu;
import ru.kc.main.tab.ui.TabPanel;
import ru.kc.platform.action.AbstractAction;
import ru.kc.platform.action.ButtonAction;
import ru.kc.platform.action.ComboBoxAction;
import ru.kc.platform.action.facade.AbstractActionFacade;
import ru.kc.platform.action.facade.ButtonFacadeMediator;
import ru.kc.platform.action.facade.ComboBoxFacadeMediator;
import ru.kc.platform.annotations.Mapping;
import ru.kc.util.collection.Pair;
import ru.kc.util.swing.button.DropDownButton;
import ru.kc.util.swing.icon.IconUtil;

@Mapping(TabPanel.class)
public class TabController extends Controller<TabPanel>{

	JToolBar toolbar;
	Component component;
	
	
	@Override
	protected void init() {
		ui.root.setLayout(new BorderLayout());
		toolbar = ui.toolbar;
		toolbar.removeAll();
	}

	public void setComponent(Component component) {
		this.component = component;
		ui.root.add(component);
		refreshToolbar();
	}
	

	public Component getComponent() {
		return component;
	}
	
	private void refreshToolbar() {
		toolbar.removeAll();
		
		addMainButton();
		addSeparator();
		addExternalActionComponents();
		addSeparator();
		addPrevNextButtons();
		

	}

	private void addMainButton() {
		MainMenu mainMenu = new MainMenu(appContext, context, this);
		
		DropDownButton dropDownButton = new DropDownButton();
		dropDownButton.setIcon(IconUtil.get("/ru/kc/common/img/mainMenu.png"));
		dropDownButton.setMenu(mainMenu);
		dropDownButton.setToolTipText("Main menu");
		toolbar.add(dropDownButton);
	}
	
	private void addPrevNextButtons() {
		final JButton prev = new JButton(IconUtil.get("/ru/kc/main/tab/img/prev.png"));
		final JButton next = new JButton(IconUtil.get("/ru/kc/main/tab/img/next.png"));
		
		
		
		prev.setToolTipText("Open previous tab  (Alt+LEFT)");
		prev.addHierarchyListener(new HierarchyListener() {
			
			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				Pair<Boolean, Boolean> result = invokeSafe(new NextPrevButtonsEnableRequest()).result;
				if(result != null){
					prev.setEnabled(result.getFirst());
					next.setEnabled(result.getSecond());
				} else {
					prev.setEnabled(false);
					next.setEnabled(false);
				}
			}
		});
		toolbar.add(prev);
		

		next.setToolTipText("Open next tab  (Alt+RIGHT)");
		toolbar.add(next);
		
	}

	private void addSeparator() {
		toolbar.add(new JToolBar.Separator());
	}

	private void addExternalActionComponents() {
		List<AbstractActionFacade> facades = getSubActionFacades();
		AbstractActionFacade before = null;
		for (AbstractActionFacade facade : facades) {
			addSeparatorInNewGroup(before, facade);
			AbstractAction action = null;
			if(facade instanceof ButtonFacadeMediator){
				ButtonAction buttonAction = new ButtonAction();
				((ButtonFacadeMediator) facade).addRealFacade(buttonAction);
				action = buttonAction;
			}
			else if(facade instanceof ComboBoxFacadeMediator){
				ComboBoxAction comboAction = new ComboBoxAction();
				((ComboBoxFacadeMediator) facade).addRealFacade(comboAction);
				action = comboAction;
			}
			
			if(action != null){
				toolbar.add(action.getComponent());
			}
			before = facade;
		}
	}

	private void addSeparatorInNewGroup(AbstractActionFacade before,
			AbstractActionFacade next) {
		if(before != null){
			String beforeGroup = before.getGroup();
			String nextGroup = next.getGroup();
			if(beforeGroup == null) beforeGroup = "";
			if(nextGroup == null) nextGroup = "";
			if( ! beforeGroup.equals(nextGroup)){
				addSeparator();
			}
		}
	}






	
	

}
