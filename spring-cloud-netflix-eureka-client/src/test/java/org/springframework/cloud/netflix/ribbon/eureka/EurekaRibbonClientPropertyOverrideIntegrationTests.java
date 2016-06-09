/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.netflix.ribbon.eureka;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.netflix.archaius.ArchaiusAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.netflix.ribbon.eureka.RibbonClientPreprocessorIntegrationTests.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.netflix.loadbalancer.ConfigurationBasedServerList;
import com.netflix.loadbalancer.DummyPing;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import com.netflix.niws.loadbalancer.NIWSDiscoveryPing;

/**
 * @author Spencer Gibb
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
@IntegrationTest({"foo.ribbon.NFLoadBalancerPingClassName=com.netflix.loadbalancer.DummyPing",
		"foo.ribbon.NIWSServerListClassName=com.netflix.loadbalancer.ConfigurationBasedServerList",
		"foo.ribbon.NIWSServerListFilterClassName=com.netflix.loadbalancer.ServerListSubsetFilter",
})
@DirtiesContext
public class EurekaRibbonClientPropertyOverrideIntegrationTests {

	@Autowired
	private SpringClientFactory factory;

	@Test
	public void pingOverridesToDummy() throws Exception {
		DummyPing.class.cast(getLoadBalancer("foo").getPing());
		NIWSDiscoveryPing.class.cast(getLoadBalancer("bar").getPing());
	}

	@Test
	public void serverListOverridesToTest() throws Exception {
		ConfigurationBasedServerList.class.cast(getLoadBalancer("foo").getServerListImpl());
		DomainExtractingServerList.class.cast(getLoadBalancer("bar").getServerListImpl());
	}

	@SuppressWarnings("unchecked")
	private ZoneAwareLoadBalancer<Server> getLoadBalancer(String name) {
		return (ZoneAwareLoadBalancer<Server>) this.factory.getLoadBalancer(name);
	}

	@Configuration
	@Import({ PropertyPlaceholderAutoConfiguration.class,
			ArchaiusAutoConfiguration.class, RibbonAutoConfiguration.class,
			RibbonEurekaAutoConfiguration.class })
	protected static class TestConfiguration {
	}
}
