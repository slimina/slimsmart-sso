package cn.slimsmart.sso.server.web;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/validation/*")
public class ValidationController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@RequestMapping(value = "/validationTicketCode/{ticketCode}",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> validationTicketCode(@PathVariable String ticketCode){
		logger.debug("validationTicketCode ticketCode:"+ticketCode);
		return null;
	}
}
