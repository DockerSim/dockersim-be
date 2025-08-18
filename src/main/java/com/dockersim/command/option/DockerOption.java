package com.dockersim.command.option;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DockerOption {
    ALL_TAGS(Constants.ALL_TAGS_SHORT, Constants.ALL_TAGS_LONG, Constants.ALL_TAGS_DESC),
    DISABLE_CONTENT_TRUST(null, Constants.DISABLE_CONTENT_TRUST_LONG,
        Constants.DISABLE_CONTENT_TRUST_DESC),
    PLATFORM(null, Constants.PLATFORM, Constants.PLATFORM_DESC),
    QUIET(Constants.QUIET_SHORT, Constants.QUIET_LONG, Constants.QUIET_DESC);

    private final String shortName;
    private final String longName;
    private final String description;

    public static final class Constants {

        // ALL_TAGS
        public static final String ALL_TAGS_SHORT = "-a";
        public static final String ALL_TAGS_LONG = "--all-tags";
        public static final String ALL_TAGS_DESC = "Download all tagged images in the repository";


        // DISABLE_CONTENT_TRUST
        public static final String DISABLE_CONTENT_TRUST_LONG = "--disable-content-trust";
        public static final String DISABLE_CONTENT_TRUST_DESC = "Skip image verification";

        // PLATFORM
        public static final String PLATFORM = "--platform";
        public static final String PLATFORM_DESC = "Set platform if server is multi-platform capable";

        // QUIET
        public static final String QUIET_SHORT = "-q";
        public static final String QUIET_LONG = "--quiet";
        public static final String QUIET_DESC = "Suppress verbose output";
    }
}
