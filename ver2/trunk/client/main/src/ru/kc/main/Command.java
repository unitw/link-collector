package ru.kc.main;

import ru.kc.platform.annotations.Inject;
import ru.kc.platform.command.RollbackableAbstractCommand;
import ru.kc.tools.filepersist.Factory;
import ru.kc.tools.filepersist.PersistService;
import ru.kc.tools.filepersist.Tree;

public abstract class Command<T> extends RollbackableAbstractCommand<T> {
	
	@Inject
	protected Context context;
	protected PersistService persist;
	protected Tree persistTree;
	protected Factory persistFactory;
	
	@Override
	protected void beforeInvoke() {
		persist = context.persistService;
		persistTree = persist.tree();
		persistFactory = persist.factory();
	}

}