package cn.slimsmart.sso.server.web;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/monitor/*")
public class MonitorController {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@RequestMapping("total")
	@ResponseBody
	public Map<String,Object> total(){
		logger.debug("total ....");
		return null;
	}

}
