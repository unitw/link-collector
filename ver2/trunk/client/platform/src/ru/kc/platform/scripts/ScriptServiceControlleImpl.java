package ru.kc.platform.scripts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.kc.platform.scripts.annotations.Mapping;
import ru.kc.platform.scripts.controller.ScriptController;
import ru.kc.tools.scriptengine.ScriptServiceController;

public class ScriptServiceControlleImpl implements ScriptServiceController {
	
	private static Log log = LogFactory.getLog(ScriptServiceControlleImpl.class);
	
	@Override
	public Object getMapping(Class<?> clazz) {
		Class<?> type = clazz;
		Mapping annotation = type.getAnnotation(Mapping.class);
		if(annotation == null){
			log.info("no annotation "+Mapping.class);
			return null;
		}

		
		if(!ScriptController.class.isAssignableFrom(type)) {
			log.info("no superclass "+ScriptController.class);
			return null;
		}
		
		return annotation.value();
	}

}