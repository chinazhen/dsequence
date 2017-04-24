package com.github.dsequence;

import com.github.dsequence.server.Application;
import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * 测试父类
 *
 * @author 牧之 Date: 2015/08/06 Project: account Version: 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public abstract class BaseSpringTest extends TestCase {


}
