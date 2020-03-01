package test_locally.api.methods;

import com.slack.api.Slack;
import com.slack.api.SlackConfig;
import com.slack.api.methods.MethodsConfig;
import com.slack.api.methods.impl.AsyncRateLimitExecutor;
import com.slack.api.methods.metrics.MetricsDatastore;
import com.slack.api.methods.metrics.impl.MemoryMetricsDatastore;
import com.slack.api.methods.metrics.impl.RedisMetricsDatastore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.MockSlackApiServer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static util.MockSlackApi.ValidToken;

public class AuthTest {

    MockSlackApiServer server = new MockSlackApiServer();
    SlackConfig config = new SlackConfig();
    Slack slack = Slack.getInstance(config);

    @Before
    public void setup() throws Exception {
        server.start();
        config.setMethodsEndpointUrlPrefix(server.getMethodsEndpointPrefix());
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void auth() throws Exception {
        assertThat(slack.methods(ValidToken).authRevoke(r -> r.test(true)).isOk(), is(true));
        assertThat(slack.methods(ValidToken).authTest(r -> r).getTeamId(), is("T1234567"));
    }

    @Test
    public void auth_async() throws Exception {
        assertThat(slack.methodsAsync(ValidToken).authRevoke(r -> r.test(true)).get().isOk(), is(true));
        assertThat(slack.methodsAsync(ValidToken).authTest(r -> r).get().getTeamId(), is("T1234567"));
    }

}
