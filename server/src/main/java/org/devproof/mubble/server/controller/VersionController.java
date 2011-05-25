package org.devproof.mubble.server.controller;

import org.apache.commons.lang.StringUtils;
import org.devproof.mubble.dto.version.MubbleVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

/**
 * Returns the version to check compatibility
 *
 * Created At: 08.04.11 14:41
 *
 * @author Carsten Hufe
 */
@Controller
public class VersionController {
    @Autowired
    @Qualifier("compatibleVersions")
    private String compatibleVersions;
    @Autowired
    @Qualifier("version")
    private String version;

    @RequestMapping(value="/version", method = RequestMethod.GET)
	public @ResponseBody MubbleVersion retrieveVersion() {
        String[] split = StringUtils.split(compatibleVersions, ' ');
        return new MubbleVersion(version, Arrays.asList(split));
	}
}
