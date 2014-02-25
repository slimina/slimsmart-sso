package cn.slimsmart.sso.server.ticket;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import cn.slimsmart.sso.server.AbstractSpringTest;

public class SlimTicketManagerTest extends AbstractSpringTest{

	@Before //在每个测试用例方法之前都会执行  
    public void init(){  
    }  
     
	
	@Resource  
    private SlimTicketManager slimTicketManager; 
	
	@Test
	@Transactional
	public void testAdd(){
		slimTicketManager.getTicketByCode("aaaaaa");
	}
	
}
