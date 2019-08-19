package com.example.api_dispatcher.base;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.api_dispatcher.base.ApiDispatcher;
import com.example.api_dispatcher.base.service.impl.TestServiceImpl;
import com.example.api_dispatcher.base.ApiDispatcherTestUnits;

/**
 * API Dispatcher Tests
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestServiceImpl.class })
public class ApiDispatcherTest extends ApiDispatcherTestUnits {

	@Before
	public void setUp() throws Exception {
		setDispatcher(new ApiDispatcher(getConfig()));
	}
}
