package io.github.sweehaw.websupports.tools;

import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import lombok.Data;

/**
 * @author sweehaw
 */
@Data
public class UserAgentTools {

    private final UserAgent ua;
    private final String userAgent;

    public UserAgentTools(String userAgent) {
        this.userAgent = userAgent;
        this.ua = UserAgent.parseUserAgentString(userAgent);
    }

    public String getBrowserName() {
        String browser = this.ua.getBrowser().toString();
        return browser.substring(0, 1).toUpperCase() + browser.substring(1).toLowerCase();
    }

    public String getBrowserVersion() {
        Version browserVersion = this.ua.getBrowserVersion();
        return browserVersion != null ? browserVersion.toString() : "";
    }

    public String getBrowser() {
        String browser = this.getBrowserName() + "/" + this.getBrowserVersion();
        return "Unknown/".equals(browser) ? this.userAgent : browser;
    }
}
