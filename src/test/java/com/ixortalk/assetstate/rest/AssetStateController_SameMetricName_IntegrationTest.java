/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-present IxorTalk CVBA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.ixortalk.assetstate.rest;

import java.util.Map;

import javax.inject.Inject;

import com.ixortalk.assetstate.AbstractSpringIntegrationTest;
import com.ixortalk.assetstate.domain.aspect.AssetState;
import com.ixortalk.assetstate.domain.asset.AspectBuilderforTest;
import org.junit.Test;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import org.springframework.test.context.ActiveProfiles;

import static com.ixortalk.assetstate.domain.aspect.Status.OK;
import static com.ixortalk.assetstate.rest.PrometheusStubHelper.setupPrometheusStubForMetric;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"test", "sameMetricName"})
@OAuth2ContextConfiguration(AbstractSpringIntegrationTest.AdminClientCredentialsResourceDetails.class)
public class AssetStateController_SameMetricName_IntegrationTest extends AbstractSpringIntegrationTest {

    @Inject
    private AssetStateController assetStateController;

    @Test
    public void sameMetricName() throws Exception {
        setupAssetMgmtStubWithMetrics(ASSETS);

        setupPrometheusStubForMetric(wireMockRule, EXPECTED_PROMETHEUS_SINGLE_METRIC_RESPONSE);

        Map<String, AssetState> assetStates = assetStateController.getAssetStates();

        assertThat(assetStates).hasSize(4);
        assertThat(assetStates.get("asset1").getAspects())
                .usingElementComparatorIgnoringFields("localDateTime")
                .containsOnly(
                        new AspectBuilderforTest()
                                .withName("first-aspect")
                                .withStatus(OK)
                                .withValue("1")
                                .build(),
                        new AspectBuilderforTest()
                                .withName("second-aspect")
                                .withStatus(OK)
                                .withValue("1")
                                .build());
    }
}
