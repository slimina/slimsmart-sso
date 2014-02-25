package cn.slimsmart.sso.server.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.slimsmart.sso.server.model.LoginUser;

@Controller
public class LoginController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@RequestMapping("/login")
	public ModelAndView login(LoginUser loginUser){
		logger.debug("login LoginUser:"+loginUser);
		return null;
	}
	
	@RequestMapping("/ajaxLogin")
	public void ajaxLogin(LoginUser loginUser,HttpServletResponse response){
		logger.debug("login LoginUser:"+loginUser);
	}
	
	@RequestMapping(value="/restLogin/{userName}/{password}/{authcode}",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> restLogin(@PathVariable String userName,@PathVariable String password,@PathVariable String authcode){
		LoginUser loginUser = new LoginUser();
		loginUser.setUserName(userName);
		loginUser.setAuthcode(authcode);
		loginUser.setPassword(password);
		logger.debug("login LoginUser:"+loginUser);
		Map<String,Object>  map = new HashMap<String, Object>();
		map.put("code", 400);
		map.put("loginUser", loginUser);
		return map;
	}
	
}
