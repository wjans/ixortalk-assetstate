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

import com.ixortalk.assetstate.AbstractSpringIntegrationTest;
import com.ixortalk.assetstate.domain.aspect.AssetState;
import com.ixortalk.assetstate.domain.asset.AspectBuilderforTest;
import org.junit.Test;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import org.springframework.test.context.ActiveProfiles;

import javax.inject.Inject;
import java.util.Map;

import static com.ixortalk.assetstate.ConfigurationTestConstants.WITH_FLATTENING_LABELS_ASPECT;
import static com.ixortalk.assetstate.rest.PrometheusStubHelper.FLATTENING_LABELS_PROMETHEUS_RESPONSE;
import static com.ixortalk.assetstate.rest.PrometheusStubHelper.setupPrometheusStubForMetric;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"test", "flatteningLabels"})
@OAuth2ContextConfiguration(AbstractSpringIntegrationTest.AdminClientCredentialsResourceDetails.class)
public class AssetStateController_FlatteningLabels_IntegrationTest extends AbstractSpringIntegrationTest {

    @Inject
    private AssetStateController assetStateController;

    @Test
    public void flatteningLabels_flattensOnSpecifiedLabelValuesInAspect() throws Exception {
        setupAssetMgmtStubWithMetrics(SINGLE_ASSET);

        setupPrometheusStubForMetric(wireMockRule, FLATTENING_LABELS_PROMETHEUS_RESPONSE);

        Map<String, AssetState> assetStates = assetStateController.getAssetStates();

        assertThat(assetStates).hasSize(1);
        assertThat(assetStates.get("asset1").getAspects())
                .usingElementComparatorIgnoringFields("value", "status", "localDateTime")
                .containsOnly(
                        new AspectBuilderforTest()
                                .withName(WITH_FLATTENING_LABELS_ASPECT)
                                .withLabels("labelA1", "labelB1")
                                .build(),
                        new AspectBuilderforTest()
                                .withName(WITH_FLATTENING_LABELS_ASPECT)
                                .withLabels("labelA1", "labelB2")
                                .build(),
                        new AspectBuilderforTest()
                                .withName(WITH_FLATTENING_LABELS_ASPECT)
                                .withLabels("labelA2", "labelB2")
                                .build());
    }
}
